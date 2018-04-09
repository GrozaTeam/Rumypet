package dognose.cd_dog;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by paeng on 2018. 3. 26..
 */

public class RegisterActivity extends AppCompatActivity {

    private static final int PICK_FROM_CAMERA = 0;
    private static final int PICK_FROM_ALBUM = 1;
    private static final int CROP_FROM_IMAGE = 2;
    private Uri mImageCaptureUri;
    private String absolutePath;

    private EditText etId, etPw, etPw2, etDogName, etSpecies, etGender, etBirth, etOwnerName, etOwnerPhone;
    private Button btnRegister, btnCheckId, btnCheckPhone, btnPhoto, btnPhotoNose;
    // For database
    private String id="", pw="", pw2="", dogName, species, gender, birth, ownerName, ownerPhone , registerMode;
    private ImageView imgDog, imgDogNose;
    private boolean idDulplicated = false;
    private boolean duplicateCheck = false;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_register);
        bindingView();

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode!= RESULT_OK)

        switch (requestCode)
        {
            case PICK_FROM_ALBUM:
            {
                try{
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), data.getData());
                    imgDog.setImageBitmap(bitmap);
                }catch (Exception e){
                }



            }
            case PICK_FROM_CAMERA:
            {


            }
            case CROP_FROM_IMAGE:
            {

            }
        }
    }





    public void doTakePhotoAction(){


    }

    public void doTakeAlbumAction(){


        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(android.provider.MediaStore.Images.Media.CONTENT_TYPE);
        intent.setData(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_FROM_ALBUM);


    }

    private boolean checkjoin() {

        if (id.equals("")) {
            Toast.makeText(RegisterActivity.this, "ID를 입력해주세요.", Toast.LENGTH_SHORT).show();

            return false;
        } else if (pw.equals("")) {
            Toast.makeText(RegisterActivity.this, "패스워드를 입력해주세요.", Toast.LENGTH_SHORT).show();
            return false;
        } else if(!duplicateCheck){
            Toast.makeText(RegisterActivity.this, "Please Double Check ID.", Toast.LENGTH_SHORT).show();
            return false;
        }else {
            return true;
        }
    }




    Button.OnClickListener listener = new Button.OnClickListener() {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btn_register:

                    if (checkjoin()) {
                        DBHelper dbHelper = new DBHelper(getApplicationContext(), "RumyPet.db", null, 1);
                        dbHelper.insertOwner(id, pw, ownerName, ownerPhone);
                        dbHelper.insertDog(id, dogName, species, gender, birth);

                        Toast.makeText(RegisterActivity.this, "Register Complete.", Toast.LENGTH_SHORT).show();
                        finish();

                    }
                    break;

                case R.id.btn_check_id:
                    DBHelper dbHelper = new DBHelper(getApplicationContext(), "RumyPet.db", null, 1);
                    String[] allId = dbHelper.getAllIdForDoubleCheck();
                    for (String data : allId) {
                        if (data != null) {
                            if(id.equals(data)){
                                idDulplicated = true;
                            }
                        }
                    }
                    if(idDulplicated){
                        Toast.makeText(RegisterActivity.this, "ID is duplicated please try again.", Toast.LENGTH_SHORT).show();
                        duplicateCheck = false;
                    }else if(id.equals("")){
                        Toast.makeText(RegisterActivity.this, "Please input ID", Toast.LENGTH_SHORT).show();
                        duplicateCheck = false;
                    }
                    else{
                        Toast.makeText(RegisterActivity.this, "ID double check Complete", Toast.LENGTH_SHORT).show();
                        duplicateCheck = true;
                        etId.setFocusable(false);

                    }

                    break;

                case R.id.btn_photo:

                    DialogInterface.OnClickListener cameraListener = new DialogInterface.OnClickListener(){

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            doTakePhotoAction();
                        }
                    };

                    DialogInterface.OnClickListener albumListener = new DialogInterface.OnClickListener(){

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            doTakeAlbumAction();
                        }
                    };

                    DialogInterface.OnClickListener cancelListener = new DialogInterface.OnClickListener(){

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    };

                    new AlertDialog.Builder(RegisterActivity.this)
                            .setTitle("Select Upload Image")
                            .setPositiveButton("Take Photo", cameraListener)
                            .setNeutralButton("Select Album", albumListener)
                            .setNegativeButton("Cancel", cancelListener)
                            .show();

                    break;


                case R.id.btn_photo_nose:
                    Intent intentNosePhoto = new Intent(getApplicationContext(), CameraActivity.class);
                    startActivity(intentNosePhoto);
                    break;


                case R.id.btn_check_phone:
                    Log.d("paeng", "phone check pushed");
                    break;


                default:
                    break;
            }
        }
    };



    private void textChangedListener(final EditText etInput){
        etInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                switch (etInput.getId()){
                    case R.id.et_id:
                        id = s.toString();
                        break;
                    case R.id.et_pw:
                        pw = s.toString();
                        break;
                    case R.id.et_pw2:
                        pw2 = s.toString();
                        break;
                    case R.id.et_dogname:
                        dogName = s.toString();
                        break;
                    case R.id.et_species:
                        species = s.toString();
                        break;
                    case R.id.et_gender:
                        gender = s.toString();
                        break;
                    case R.id.et_birth:
                        birth = s.toString();
                        break;
                    case R.id.et_owner_name:
                        ownerName = s.toString();
                        break;
                    case R.id.et_owner_phone:
                        ownerPhone = s.toString();
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

    public void bindingView() {
        etId = (EditText) findViewById(R.id.et_id);
        etPw = (EditText) findViewById(R.id.et_pw);
        etPw2 = (EditText) findViewById(R.id.et_pw2);
        etDogName = (EditText) findViewById(R.id.et_dogname);
        etSpecies = (EditText) findViewById(R.id.et_species);
        etGender = (EditText) findViewById(R.id.et_gender);
        etBirth = (EditText) findViewById(R.id.et_birth);
        etOwnerName = (EditText) findViewById(R.id.et_owner_name);
        etOwnerPhone = (EditText) findViewById(R.id.et_owner_phone);
        btnRegister = (Button) findViewById(R.id.btn_register);
        btnCheckId = (Button) findViewById(R.id.btn_check_id);
        btnCheckPhone = (Button) findViewById(R.id.btn_check_phone);
        btnPhoto = (Button) findViewById(R.id.btn_photo);
        btnPhotoNose = (Button) findViewById(R.id.btn_photo_nose);

        btnRegister.setOnClickListener(listener);
        btnCheckId.setOnClickListener(listener);
        btnCheckPhone.setOnClickListener(listener);
        btnPhoto.setOnClickListener(listener);
        btnPhotoNose.setOnClickListener(listener);

        imgDog = (ImageView) findViewById(R.id.img_photo);
        imgDogNose = (ImageView) findViewById(R.id.img_photo_nose);

        textChangedListener(etId);
        textChangedListener(etPw);
        textChangedListener(etPw2);
        textChangedListener(etDogName);
        textChangedListener(etSpecies);
        textChangedListener(etGender);
        textChangedListener(etBirth);
        textChangedListener(etOwnerName);
        textChangedListener(etOwnerPhone);

    }

}
