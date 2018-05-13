package dognose.cd_dog;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
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
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.w3c.dom.Text;

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

/**
 * Created by paeng on 2018. 3. 26..
 */

public class RegisterAdditionalDogActivity extends AppCompatActivity {

    private static final int PICK_FROM_ALBUM = 1;
    private static final int GALLERY_CODE=1112;
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
    private ImageView imgDog, imgDogNose;

    private String ownerId, ownerId_for_body, ownerName;

    private CompositeSubscription mSubscriptions;

    private Uri imageUri;

    private String mImageUrl = "";


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

    private class SaveImageTask extends AsyncTask<byte[], Void, Void> {

        @Override
        protected Void doInBackground(byte[]... data) {
            FileOutputStream outStream = null;

            // Write to SD Card
            try {
                File sdCard = Environment.getExternalStorageDirectory();
                File dir = new File (sdCard.getAbsolutePath() + "/RUMYPET");
                dir.mkdirs();

                String fileName = String.format(ownerId_for_body+"|doginalbum|.jpg" );
                File outFile = new File(dir, fileName);

                outStream = new FileOutputStream(outFile);
                outStream.write(data[0]);
                outStream.flush();
                outStream.close();

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
            }
            return null;
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {

            switch (requestCode) {

                case GALLERY_CODE:
                    showImage(data.getData());
                    imageUri = data.getData();
                    break;

                default:
                    break;
            }
        }
    }

    private void uploadImage(Uri imgUri, Dog dogdb) {


        try {

            String dogId = dogdb.getDogId();


            InputStream is = getContentResolver().openInputStream(imgUri);
            byte[] imageBytes = getBytes(is);

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(Constants.BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            RetrofitInterface retrofitInterface = retrofit.create(RetrofitInterface.class);

            RequestBody requestFile = RequestBody.create(MediaType.parse("image/jpeg"), imageBytes);

            MultipartBody.Part body = MultipartBody.Part.createFormData("image", "image.jpg", requestFile);

            Call<ImageResponse> call = retrofitInterface.uploadImage(body);

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

    private void showImage(Uri imgUri){
        String imagePath = getRealPathFromURI(imgUri);
        Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
        imgDog.setImageBitmap(bitmap);
        imgDog.setBackground(null);

    }

    public byte[] getBytes(InputStream is) throws IOException {
        ByteArrayOutputStream byteBuff = new ByteArrayOutputStream();

        int buffSize = 1024;
        byte[] buff = new byte[buffSize];

        int len = 0;
        while ((len = is.read(buff)) != -1) {
            byteBuff.write(buff, 0, len);
        }

        return byteBuff.toByteArray();
    }


    private void sendPicture(Uri imgUri) {

        String imagePath = getRealPathFromURI(imgUri); // path 경로

        Bitmap bitmap = BitmapFactory.decodeFile(imagePath); //경로를 통해 비트맵으로 전환

        imgDog.setImageBitmap(bitmap);//이미지 뷰에 비트맵 넣기
        imgDog.setBackground(null);

/*

        ExifInterface exif = null;
        try {
            exif = new ExifInterface(imagePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
        int exifOrientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);

        byte[] currentData = stream.toByteArray();

        //파일로 저장
        new RegisterAdditionalDogActivity.SaveImageTask().execute(currentData);
*/

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
                        String dogId = getRandomString(8);
                        Dog dogdb = new Dog();
                        dogdb.setDogId(dogId);
                        dogdb.setOwnerId(ownerId);
                        dogdb.setName(dogName);
                        dogdb.setGender(gender);
                        dogdb.setBirth(birth);
                        dogdb.setSpecies(etSpecies.getSelectedItem().toString());
                        uploadImage(imageUri, dogdb);

                        registerProgress(dogdb);
                        finish();
                    }
                    break;

                case R.id.btn_photo:

                    // Toast.makeText(RegisterAdditionalDogActivity.this, "Coming Soon...", Toast.LENGTH_SHORT);
                    new AlertDialog.Builder(RegisterAdditionalDogActivity.this)
                            .setTitle("Select Upload Image")
                            .setPositiveButton("Select Album", albumListener)
                            .setNegativeButton("Cancel", cancelListener)
                            .show();
                    break;

                case R.id.btn_photo_nose:
                    // Toast.makeText(RegisterAdditionalDogActivity.this, "Coming Soon...", Toast.LENGTH_SHORT);

                    Intent intentNosePhoto2 = new Intent(getApplicationContext(), CameraActivity.class);
                    ownerId = ownerId + "|reg|";
                    intentNosePhoto2.putExtra("ownerId", ownerId);
                    startActivityForResult(intentNosePhoto2,1);

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

                    DatePickerDialog dialog = new DatePickerDialog(RegisterAdditionalDogActivity.this, datePickListener, todayYear, todayMonth, todayDay);
                    dialog.show();

                    break;

                default:
                    break;
            }
        }
    };

    DialogInterface.OnClickListener cameraListener = new DialogInterface.OnClickListener(){

        @Override
        public void onClick(DialogInterface dialog, int which) {
            Intent intentNosePhoto = new Intent(getApplicationContext(), CameraActivity_for_body.class);
            String ownerId2 = ownerId + "|body|";
            intentNosePhoto.putExtra("ownerId", ownerId2);
            startActivityForResult(intentNosePhoto,1);
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
        imgDogNose = (ImageView) findViewById(R.id.img_photo_nose);

        textChangedListener(etDogName);
        tvBirth.setOnClickListener(listener);





    }



}
