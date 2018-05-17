package dognose.cd_dog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;

import dognose.cd_dog.model.Dog;
import dognose.cd_dog.utils.Constants;

/**
 * Created by paeng on 2018. 3. 26..
 */

public class InformationDogListDetail extends AppCompatActivity {

    private static final int GALLERY_CODE=1112;
    private int position, dogNum;
    private ArrayList<Dog> dogArrayList;
    private ImageButton btnBefore, btnAfter;
    private ImageView imageInf;
    private Button btnMoreInfo;
    private TextView tvName, tvSpecies, tvGender, tvBirth, tvAge;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.information_dog_detail);
        bindingView();

        dogArrayList = new ArrayList<Dog>();

        Intent intent = getIntent();
        position = Integer.parseInt(intent.getStringExtra("position"));
        dogNum = Integer.parseInt(intent.getStringExtra("dogNum"));
        dogArrayList = (ArrayList<Dog>) intent.getSerializableExtra("dogSet");
        setInformation();

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {

            switch (requestCode) {

                case GALLERY_CODE:

                    //showImage(data.getData());
                    //imageUri = data.getData();

                    Log.d("PaengTest", "gallary");
                    break;

                default:
                    break;
            }
        }
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
                    dialog.setMessage("If you want to see the detail Information of this dog, You need certification of your dog by picuture of dog's nose.\n\nDo you want to see it?");
                    dialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            new android.support.v7.app.AlertDialog.Builder(InformationDogListDetail.this)
                                    .setTitle("Select Upload Image")
                                    .setPositiveButton("Select Album", albumListener)
                                    .setNegativeButton("Take Photo", cameraListener)
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
            /*
            Intent intentNosePhoto = new Intent(getApplicationContext(), CameraActivity_for_body.class);
            String ownerId2 = ownerId + "|body|";
            intentNosePhoto.putExtra("ownerId", ownerId2);
            startActivityForResult(intentNosePhoto,1);
            */
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
