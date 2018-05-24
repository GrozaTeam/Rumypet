package dognose.cd_dog;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

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

/**
 * Created by paeng on 2018. 5. 24..
 */

public class FindDogProgressActivity extends AppCompatActivity {

    private String[] imageNose;
    private Uri[] imageNoseUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_find_dog_progress);
        bindingView();


        identification_process(imageNoseUri[0]);



    }
    private void identification_process(Uri imgUri){

        try{
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
            MultipartBody.Part body = MultipartBody.Part.createFormData("image",  "input.jpg", requestFile);
            Call<ImageResponse> call = retrofitInterface.dogIdentification(body);

            call.enqueue(new Callback<ImageResponse>() {
                @Override
                public void onResponse(Call<ImageResponse> call, retrofit2.Response<ImageResponse> response) {

                    if (response.isSuccessful()) {
                        ImageResponse responseBody = response.body();
                        String result = responseBody.getResult();



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

    private void bindingView(){

        imageNose = new String[3];
        imageNoseUri = new Uri[3];
        Intent intent = getIntent();
        imageNose = intent.getStringArrayExtra("input_dog");

        for (int i=0;i<3;i++){
            imageNoseUri[i] = Uri.parse(imageNose[i]);
        }
    }
}
