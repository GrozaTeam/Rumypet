package dognose.cd_dog;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;

import dognose.cd_dog.Camera.CameraActivity_for_body;
import dognose.cd_dog.model.Dog;
import dognose.cd_dog.network.ImageResponse;
import dognose.cd_dog.network.RetrofitInterface;
import dognose.cd_dog.utils.Constants;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static dognose.cd_dog.utils.ImageTransformation.Bitmap2InputStream;
import static dognose.cd_dog.utils.ImageTransformation.getBytes;
import static dognose.cd_dog.utils.ImageTransformation.rotateBitmap;

/*
 * Created by paeng on 2018. 3. 26..
 */

public class InformationDogListDetail extends AppCompatActivity {

    private static final int GALLERY_CODE=1112;
    private static final int PHOTO_CODE=1111;
    private int position, dogNum;
    private ArrayList<Dog> dogArrayList;
    private ImageButton btnBefore, btnAfter;
    private ImageView imageInf;
    private Button btnMoreInfo;
    private TextView tvName, tvSpecies, tvGender, tvBirth, tvAge;
    private ProgressBar mProgressBar;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.information_dog_detail);
        bindingView();

        dogArrayList = new ArrayList<Dog>();

        Intent intent = getIntent();
        position = Integer.parseInt(intent.getStringExtra("position"));
        dogNum = Integer.parseInt(intent.getStringExtra("dogNum"));
        userId = intent.getStringExtra("userId");
        dogArrayList = (ArrayList<Dog>) intent.getSerializableExtra("dogSet");
        setInformation();

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {

            switch (requestCode) {

                case GALLERY_CODE:
                    mProgressBar.setVisibility(View.VISIBLE);
                    verification_process(data.getData());

                    break;
                default:
                    break;
            }
        }
        else if(resultCode == RESULT_CANCELED){
            switch (requestCode) {

                case PHOTO_CODE:
                    mProgressBar.setVisibility(View.VISIBLE);

                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        public void run() {
                            mProgressBar.setVisibility(View.GONE);
                            Toast.makeText(InformationDogListDetail.this, "Invalid Image\nWe recommend you to upload dog's nose image from gallery", Toast.LENGTH_LONG).show();

                        }
                    }, 5000);

                    break;

                default:
                    break;
            }
        }
    }

    private void verification_process(Uri imgUri){
        try{
            String dogId = dogArrayList.get(position).getDogId();

            String imagePath = getRealPathFromURI(imgUri);
            Bitmap orgImage = BitmapFactory.decodeFile(imagePath);
            Bitmap resize = Bitmap.createScaledBitmap(orgImage, 300, 300, true);
            ExifInterface exif = null;
            try {
                exif = new ExifInterface(imagePath);
            } catch (IOException e) {
                e.printStackTrace();
            }
            int exifOrientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);

            Bitmap resultBitmap = rotateBitmap(resize, exifOrientation);


            InputStream is_result = Bitmap2InputStream(resultBitmap);


            byte[] imageBytes = getBytes(is_result);
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(Constants.BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            RetrofitInterface retrofitInterface = retrofit.create(RetrofitInterface.class);
            RequestBody requestFile = RequestBody.create(MediaType.parse("image/jpeg"), imageBytes);

            MultipartBody.Part body = MultipartBody.Part.createFormData("image", dogId + ".jpg", requestFile);
            Call<ImageResponse> call = retrofitInterface.dogVerification(body);

            call.enqueue(new Callback<ImageResponse>() {
                @Override
                public void onResponse(Call<ImageResponse> call, retrofit2.Response<ImageResponse> response) {

                    if (response.isSuccessful()) {
                        ImageResponse responseBody = response.body();
                        String result = responseBody.getResult();
                        String message = responseBody.getMessage();
                        String path = responseBody.getPath();
                        mProgressBar.setVisibility(View.GONE);
                        Log.d("TESTPAENG", result);
                        if(result.equals("true")){
                            Toast.makeText(InformationDogListDetail.this, "Verification Success!", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(InformationDogListDetail.this, InformationDogListDetailDetail.class);
                            intent.putExtra("dogId", dogArrayList.get(position).getDogId());
                            intent.putExtra("dogName", dogArrayList.get(position).getName());
                            intent.putExtra("dogGender", dogArrayList.get(position).getGender());
                            intent.putExtra("dogSpecies", dogArrayList.get(position).getSpecies());
                            intent.putExtra("dogBirth", dogArrayList.get(position).getBirth());
                            intent.putExtra("dogAge", getAge(dogArrayList.get(position).getBirth()));

                            intent.putExtra("userId", userId);

                            startActivity(intent);
                        }else{
                            Toast.makeText(InformationDogListDetail.this, "Verification Failed!", Toast.LENGTH_SHORT).show();

                        }

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
    private String getRealPathFromURI(Uri contentUri) {
        int column_index=0;
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(contentUri, proj, null, null, null);
        if(cursor.moveToFirst()){
            column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        }

        return cursor.getString(column_index);
    }

    Button.OnClickListener listener = new Button.OnClickListener(){

        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.btn_after:
                    if (position == dogNum -1){
                        Toast.makeText(InformationDogListDetail.this, "This is the last DOG", Toast.LENGTH_SHORT).show();
                    }else{
                        position +=1;
                        setInformation();
                    }
                    break;

                case R.id.btn_before:
                    if (position == 0){
                        Toast.makeText(InformationDogListDetail.this, "This is the first DOG", Toast.LENGTH_SHORT).show();
                    }else{
                        position -=1;
                        setInformation();
                    }
                    break;

                case R.id.btn_moreinfo:

                    AlertDialog.Builder dialog = new AlertDialog.Builder(InformationDogListDetail.this);
                    dialog.setCancelable(false);
                    dialog.setTitle("You need Certification");
                    dialog.setMessage("If you want to see the detail Information of this dog, You need certification of your dog by image of dog's nose.\n\nDo you want to see it?");
                    dialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            new android.support.v7.app.AlertDialog.Builder(InformationDogListDetail.this)
                                    .setTitle("Select Upload Image")
                                    .setPositiveButton("Select Album", albumListener)
                                    .setNegativeButton("Take Picture", cameraListener)
                                    .setNeutralButton("Cancel", cancelListener)
                                    .show();

                        }
                    }).setNegativeButton("Cancel ", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //Action for "Cancel".
                        }
                    });

                    final AlertDialog alert = dialog.create();
                    alert.show();
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
            String ownerId2 = userId + "|body|";
            intentNosePhoto.putExtra("ownerId", ownerId2);
            startActivityForResult(intentNosePhoto, PHOTO_CODE);

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

    private void setInformation(){
        tvName.setText(dogArrayList.get(position).getName());
        tvSpecies.setText(dogArrayList.get(position).getSpecies());
        tvGender.setText(dogArrayList.get(position).getGender());
        tvBirth.setText(dogArrayList.get(position).getBirth());
        tvAge.setText(getAge(dogArrayList.get(position).getBirth()));

        String url = Constants.BASE_URL + "images/" + dogArrayList.get(position).getDogId();
        Glide.with(this).load(url).into(imageInf);

    }

    private void bindingView(){
        tvName = (TextView)findViewById(R.id.info_name);
        tvGender = (TextView)findViewById(R.id.info_gender);
        tvBirth = (TextView)findViewById(R.id.info_birth);
        tvSpecies = (TextView)findViewById(R.id.info_species);
        tvAge = (TextView)findViewById(R.id.info_age);
        btnMoreInfo = (Button)findViewById(R.id.btn_moreinfo);
        imageInf = (ImageView)findViewById(R.id.image_info);
        mProgressBar = (ProgressBar)findViewById(R.id.progress_bar);

        btnBefore = (ImageButton)findViewById(R.id.btn_before);
        btnAfter = (ImageButton)findViewById(R.id.btn_after);
        btnBefore.setOnClickListener(listener);
        btnAfter.setOnClickListener(listener);
        btnMoreInfo.setOnClickListener(listener);

    }

    private String getAge(String birth){

        String birthYear = birth.substring(0,4);

        Calendar calendarStart = Calendar.getInstance();
        int todayYear = calendarStart.get(Calendar.YEAR);

        String age = String.valueOf(todayYear - Integer.valueOf(birthYear) +1);

        return age;
    }

}
