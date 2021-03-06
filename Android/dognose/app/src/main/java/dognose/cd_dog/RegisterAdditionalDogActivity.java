package dognose.cd_dog;

import android.app.DatePickerDialog;
import android.content.ClipData;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.media.Image;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.Random;

import dognose.cd_dog.model.Dog;
import dognose.cd_dog.model.Res;
import dognose.cd_dog.network.ImageResponse;
import dognose.cd_dog.network.NetworkUtil;
import dognose.cd_dog.network.RetrofitInterface;
import dognose.cd_dog.utils.Constants;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.HttpException;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

import static android.app.Activity.RESULT_OK;
import static dognose.cd_dog.utils.ImageTransformation.Bitmap2InputStream;
import static dognose.cd_dog.utils.ImageTransformation.getBytes;
import static dognose.cd_dog.utils.ImageTransformation.rotateBitmap;

/**
 * Created by paeng on 2018. 3. 26..
 */

public class RegisterAdditionalDogActivity extends AppCompatActivity {

    private static final int GALLERY_CODE=1112;
    private static final int RECORD_CODE=1114;
    private static final int GALLERY_MULTIPLE_CODE=1113;
    private static final int colorMaleLight = 0XFFCCCCFF;
    private static final int colorMaleDark = 0XFF5555FF;
    private static final int colorFemaleLight =  0XFFFFCCCC;
    private static final int colorFemaleDark = 0XFFFF5555;

    private EditText etDogName;
    private Spinner etSpecies;
    private TextView tvBirth;
    private Button btnRegister, btnPhoto, btnPhotoNose, btnGenderMale, btnGenderFemale;
    // For database
    private String dogName="", species="", gender="", birth="";
    private ImageView imgDog, imgDogNose1, imgDogNose2, imgDogNose3;
    private String ownerId;
    protected String ownerName;

    private CompositeSubscription mSubscriptions;

    private Uri imageUri, imageNoseUri1, imageNoseUri2, imageNoseUri3;
    private String mImageUrl = "";

    private int sequenceNose = 0;
    private String dogId = "";
    private boolean dogNoseUpload = false;

    private ProgressBar pb1, pb2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_register_additional);
        Intent intent = getIntent();
        ownerId = intent.getStringExtra("id");
        ownerName = intent.getStringExtra("name");
        mSubscriptions = new CompositeSubscription();
        bindingView();
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        mSubscriptions.unsubscribe();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {

            switch (requestCode) {

                case RECORD_CODE:
                    /*
                    String[] imageNoseRecorded = new String[3];
                    imageNoseRecorded[0] = "content://media/external/images/media/41666";
                    imageNoseRecorded[1] = "content://media/external/images/media/41665";
                    imageNoseRecorded[2] = "content://media/external/images/media/41664";
                    imageNoseUri1 = Uri.parse(imageNoseRecorded[0]);
                    imageNoseUri2 = Uri.parse(imageNoseRecorded[1]);
                    imageNoseUri3 = Uri.parse(imageNoseRecorded[2]);
                    */
                    pb2.setVisibility(View.VISIBLE);
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        public void run() {
                            dogNoseUpload = false;
                            Toast.makeText(RegisterAdditionalDogActivity.this, "Invalid Video\n\nWe recommend you to upload dog's nose from gallery", Toast.LENGTH_LONG).show();
                            pb2.setVisibility(View.GONE);
                        }
                    }, 2000);

                    break;

                case GALLERY_CODE:
                    showImage(data.getData(), imgDog);
                    imageUri = data.getData();
                    break;

                case GALLERY_MULTIPLE_CODE:
                    pb2.setVisibility(View.VISIBLE);
                    ClipData clipData = data.getClipData();
                    for (int i=0;i<3;i++){
                        if(i<clipData.getItemCount()){
                            Uri urione = clipData.getItemAt(i).getUri();

                            switch (i){
                                case 0:
                                    imageNoseUri1 = urione;
                                    showImage(imageNoseUri1, imgDogNose1);
                                    break;
                                case 1:
                                    imageNoseUri2 = urione;
                                    showImage(imageNoseUri2, imgDogNose2);
                                    break;
                                case 2:
                                    imageNoseUri3 = urione;
                                    showImage(imageNoseUri3, imgDogNose3);
                                    break;
                            }
                        }
                    }
                    dogNoseUpload = true;
                    pb2.setVisibility(View.GONE);
                    break;

                default:
                    break;
            }
        }
    }

    private void uploadImage(Uri imgUri, int mode) {

        try {
            Bitmap resultBitmap = null;
            if(mode == 1 || mode == 3){
                String imagePath = getRealPathFromURI(imgUri);
                InputStream is = getContentResolver().openInputStream(imgUri);
                Bitmap orgImage = BitmapFactory.decodeStream(is);
                Bitmap resize = Bitmap.createScaledBitmap(orgImage, 300, 300, true);
                ExifInterface exif = null;
                try {
                    exif = new ExifInterface(imagePath);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                int exifOrientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);

                resultBitmap = rotateBitmap(resize, exifOrientation);
            }
            // 이미지가 없는 경우
            else if(mode==2){
                resultBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.img_dog_sample);
            }


            InputStream is_result = Bitmap2InputStream(resultBitmap);

            byte[] imageBytes = getBytes(is_result);
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(Constants.BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            RetrofitInterface retrofitInterface = retrofit.create(RetrofitInterface.class);
            RequestBody requestFile = RequestBody.create(MediaType.parse("image/jpeg"), imageBytes);

            MultipartBody.Part body;
            Call<ImageResponse> call = null;
            if (mode == 1 || mode == 2){
                body = MultipartBody.Part.createFormData("image", dogId +".jpg", requestFile);
                call = retrofitInterface.uploadImage(body);
            }else if (mode ==3){
                body = MultipartBody.Part.createFormData("image", "nose_"+Integer.toString(sequenceNose) +".jpg", requestFile);
                sequenceNose++;
                call = retrofitInterface.uploadImageNose(body);
            }

            call.enqueue(new Callback<ImageResponse>() {
                @Override
                public void onResponse(Call<ImageResponse> call, retrofit2.Response<ImageResponse> response) {

                    if (response.isSuccessful()) {
                        ImageResponse responseBody = response.body();
                        mImageUrl = Constants.BASE_URL + responseBody.getPath();

                    } else {
                        ResponseBody errorBody = response.errorBody();
                        Gson gson = new Gson();

                        try {
                            ImageResponse errorResponse = gson.fromJson(errorBody.string(), ImageResponse.class);
                            Snackbar.make(findViewById(R.id.content), errorResponse.getMessage(),Snackbar.LENGTH_SHORT).show();

                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }

                @Override
                public void onFailure(Call<ImageResponse> call, Throwable t) {
                    Log.d("testPaeng: ", t.getLocalizedMessage());

                }
            });

        }catch (IOException e){
            e.printStackTrace();
        }
    }

    private void showImage(Uri imgUri, ImageView imgView){
        String imagePath = getRealPathFromURI(imgUri);
        Bitmap orgImage = BitmapFactory.decodeFile(imagePath);
        Bitmap resize = Bitmap.createScaledBitmap(orgImage, 300, 300, true);
        // image rotation
        ExifInterface exif = null;
        try {
            exif = new ExifInterface(imagePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
        int exifOrientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);

        Bitmap bitmapRotated = rotateBitmap(resize, exifOrientation);

        imgView.setImageBitmap(bitmapRotated);
        imgView.setBackground(null);

    }

    private String getRealPathFromURI(Uri contentUri) {
        int column_index=0;
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(contentUri, proj, null, null, null);
        if(cursor.moveToFirst()){
            column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        }

        return cursor.getString(column_index);
    }

    Button.OnClickListener listener = new Button.OnClickListener() {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btn_register:

                    if (checkjoin()) {
                        dogId = getRandomString(8);
                        Dog dogdb = new Dog();
                        dogdb.setDogId(dogId);
                        dogdb.setOwnerId(ownerId);
                        dogdb.setName(dogName);
                        dogdb.setGender(gender);
                        dogdb.setBirth(birth);
                        dogdb.setSpecies(etSpecies.getSelectedItem().toString());

                        registerProgress(dogdb);

                        if (imageUri == null){
                            uploadImage(null, 2);

                        }else{
                            uploadImage(imageUri, 1);
                        }
                        uploadImage(imageNoseUri1, 3);
                        uploadImage(imageNoseUri2, 3);
                        uploadImage(imageNoseUri3, 3);

                        sequenceNose = 0;
                        finish();
                    }
                    break;

                case R.id.btn_photo:

                    new AlertDialog.Builder(RegisterAdditionalDogActivity.this)
                            .setTitle("Select way to Upload Image")
                            .setPositiveButton("Select Album", albumListener)
                            .setNegativeButton("Cancel", cancelListener)
                            .show();
                    break;

                case R.id.btn_photo_nose:
                    android.app.AlertDialog.Builder dialog = new android.app.AlertDialog.Builder(RegisterAdditionalDogActivity.this);
                    dialog.setCancelable(false);
                    dialog.setTitle("You need Dog's nose Image");
                    dialog.setMessage("For others to find your dog's owner, and for certification, we need your dog's nose image.\n" +
                            "You have to take 5 seconds video of your dog's nose. Or select 3 noses from your gallery.\n\n" +
                            "Do you agree to do this?");
                    dialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            new AlertDialog.Builder(RegisterAdditionalDogActivity.this)
                                    .setTitle("Select Way to Get Dog Nose Image")
                                    .setNeutralButton("Select Album", albumMultipleListener)
                                    .setPositiveButton("Take Video", cameraListener)
                                    .setNegativeButton("Cancel", cancelListener)
                                    .show();
                        }
                    }).setNegativeButton("Cancel ", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //Action for "Cancel".
                        }
                    });

                    final android.app.AlertDialog alert = dialog.create();
                    alert.show();
                    break;

                case R.id.btn_gender_male:
                    gender = "Male";
                    btnGenderMale.setBackgroundColor(colorMaleDark);
                    btnGenderFemale.setBackgroundColor(colorFemaleLight);

                    break;

                case R.id.btn_gender_female:
                    gender = "Female";
                    btnGenderMale.setBackgroundColor(colorMaleLight);
                    btnGenderFemale.setBackgroundColor(colorFemaleDark);
                    break;

                case R.id.tv_birth:
                    Calendar calendarStart = Calendar.getInstance();
                    int todayYear = calendarStart.get(Calendar.YEAR);
                    int todayMonth = calendarStart.get(Calendar.MONTH);
                    int todayDay = calendarStart.get(Calendar.DAY_OF_MONTH);

                    DatePickerDialog dialogDate = new DatePickerDialog(RegisterAdditionalDogActivity.this, datePickListener, todayYear, todayMonth, todayDay);
                    dialogDate.show();

                    break;

                default:
                    break;
            }
        }
    };

    DialogInterface.OnClickListener cameraListener = new DialogInterface.OnClickListener(){

        @Override
        public void onClick(DialogInterface dialog, int which) {
            Intent intentRecord = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
            intentRecord.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 5);
            startActivityForResult(intentRecord, RECORD_CODE);
        }
    };

    DialogInterface.OnClickListener albumListener = new DialogInterface.OnClickListener(){

        @Override
        public void onClick(DialogInterface dialog, int which) {

            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setData(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            intent.setType("image/*");
            startActivityForResult(intent, GALLERY_CODE);
        }
    };

    DialogInterface.OnClickListener albumMultipleListener = new DialogInterface.OnClickListener(){

        @Override
        public void onClick(DialogInterface dialog, int which) {

            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Audio.Media.EXTERNAL_CONTENT_URI);
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
            intent.setType("image/*");
            startActivityForResult(intent, GALLERY_MULTIPLE_CODE);
        }
    };

    DialogInterface.OnClickListener cancelListener = new DialogInterface.OnClickListener(){

        @Override
        public void onClick(DialogInterface dialog, int which) {
            dialog.dismiss();
        }
    };

    private DatePickerDialog.OnDateSetListener datePickListener = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            monthOfYear++;
            String stringYear, stringMonth, stringDay;
            stringYear = Integer.toString(year);
            if (monthOfYear>0 && monthOfYear<10){
                stringMonth = "0" + Integer.toString(monthOfYear);
            }else{
                stringMonth = Integer.toString(monthOfYear);
            }
            if (dayOfMonth>0 && dayOfMonth<10){
                stringDay = "0" + Integer.toString(dayOfMonth);
            }else{
                stringDay = Integer.toString(dayOfMonth);
            }
            birth = stringYear+stringMonth+stringDay;
            tvBirth.setText(stringYear+"."+stringMonth+"."+stringDay);
        }
    };

    private void registerProgress(Dog dogdb) {

        mSubscriptions.add(NetworkUtil.getRetrofit().registerDog(dogdb)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponse,this::handleError));
    }

    private void handleResponse(Res response) {

        showMessage(response.getMessage());
    }

    private void handleError(Throwable error) {

        if (error instanceof HttpException) {
            Gson gson = new GsonBuilder().create();
            try {
                String errorBody = ((HttpException) error).response().errorBody().string();
                Res response = gson.fromJson(errorBody,Res.class);
                showMessage(response.getMessage());
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            showMessage("Network Error !");
        }
    }

    private void showMessage(String message) {
        Toast.makeText(RegisterAdditionalDogActivity.this, message, Toast.LENGTH_SHORT).show();
    }

    private void textChangedListener(final EditText etInput){
        etInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                switch (etInput.getId()){
                    case R.id.et_dogname:
                        dogName = s.toString();
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private static String getRandomString(int length) {
        StringBuffer buffer = new StringBuffer();
        Random random = new Random();

        String chars[] = "A,B,C,D,E,F,G,H,I,J,K,L,M,N,O,P,Q,R,S,T,U,V,W,X,Y,Z,1,2,3,4,5,6,7,8,9,0".split(",");

        for (int i=0 ; i<length ; i++)
        {
            buffer.append(chars[random.nextInt(chars.length)]);
        }
        return buffer.toString();
    }

    private boolean checkjoin() {

        if (dogName.equals("")) {
            Toast.makeText(RegisterAdditionalDogActivity.this, "Enter Dog Name", Toast.LENGTH_SHORT).show();
            return false;
        }
        else if (etSpecies.getSelectedItem().toString().equals("Select")){
            Toast.makeText(RegisterAdditionalDogActivity.this, "Enter Species", Toast.LENGTH_SHORT).show();
            return false;
        }
        else if (gender.equals("")){
            Toast.makeText(RegisterAdditionalDogActivity.this, "Choose Gender", Toast.LENGTH_SHORT).show();
            return false;
        }
        else if (birth.equals("")){
            Toast.makeText(RegisterAdditionalDogActivity.this, "Enter Birth", Toast.LENGTH_SHORT).show();
            return false;
        }
        else if (!dogNoseUpload){
            Toast.makeText(RegisterAdditionalDogActivity.this, "Register your dog's nose", Toast.LENGTH_SHORT).show();
            return false;
        }

        else {
            return true;
        }
    }

    public void bindingView() {
        etDogName = (EditText) findViewById(R.id.et_dogname);
        etSpecies = (Spinner) findViewById(R.id.et_species);
        tvBirth = (TextView) findViewById(R.id.tv_birth);
        btnRegister = (Button) findViewById(R.id.btn_register);
        btnPhoto = (Button) findViewById(R.id.btn_photo);
        btnPhotoNose = (Button) findViewById(R.id.btn_photo_nose);
        btnGenderMale = (Button) findViewById(R.id.btn_gender_male);
        btnGenderFemale = (Button) findViewById(R.id.btn_gender_female);

        btnRegister.setOnClickListener(listener);
        btnPhoto.setOnClickListener(listener);
        btnPhotoNose.setOnClickListener(listener);

        btnGenderMale.setOnClickListener(listener);
        btnGenderFemale.setOnClickListener(listener);

        imgDog = (ImageView) findViewById(R.id.img_photo);
        imgDogNose1 = (ImageView) findViewById(R.id.img_photo_nose1);
        imgDogNose2 = (ImageView) findViewById(R.id.img_photo_nose2);
        imgDogNose3 = (ImageView) findViewById(R.id.img_photo_nose3);

        textChangedListener(etDogName);
        tvBirth.setOnClickListener(listener);

        pb1 = (ProgressBar) findViewById(R.id.pb_1);
        pb2 = (ProgressBar) findViewById(R.id.pb_2);

        dogNoseUpload = false;
    }
}
