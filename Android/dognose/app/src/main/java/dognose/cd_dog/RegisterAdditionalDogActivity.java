package dognose.cd_dog;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

/**
 * Created by paeng on 2018. 3. 26..
 */

public class RegisterAdditionalDogActivity extends AppCompatActivity {

    private static final int PICK_FROM_CAMERA = 0;
    private static final int PICK_FROM_ALBUM = 1;
    private static final int CROP_FROM_IMAGE = 2;
    private Uri mImageCaptureUri;
    private String absolutePath;

    private EditText etDogName, etSpecies, etGender, etBirth;
    private Button btnRegister, btnPhoto, btnPhotoNose;
    // For database
    private String dogName="", species="", gender="", birth="";
    private ImageView imgDog, imgDogNose;

    private String ownerId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_register_additional);

        Intent intent = getIntent();
        ownerId = intent.getStringExtra("id");

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
        intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
        intent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_FROM_ALBUM);


    }

    private boolean checkjoin() {

        if (dogName.equals("")) {
            Toast.makeText(RegisterAdditionalDogActivity.this, "Input Name", Toast.LENGTH_SHORT).show();
            return false;
        }
        else if (species.equals("")){
            Toast.makeText(RegisterAdditionalDogActivity.this, "Input Species", Toast.LENGTH_SHORT).show();
            return false;
        }
        else if (gender.equals("")){
            Toast.makeText(RegisterAdditionalDogActivity.this, "Input Gender", Toast.LENGTH_SHORT).show();
            return false;
        }
        else if (birth.equals("")){
            Toast.makeText(RegisterAdditionalDogActivity.this, "Input Birth", Toast.LENGTH_SHORT).show();
            return false;
        }

        else {
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
                        dbHelper.insertDog(ownerId, dogName, species, gender, birth);

                        Toast.makeText(RegisterAdditionalDogActivity.this, "Add Dog Complete.", Toast.LENGTH_SHORT).show();
                        finish();
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

                    new AlertDialog.Builder(RegisterAdditionalDogActivity.this)
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
        etDogName = (EditText) findViewById(R.id.et_dogname);
        etSpecies = (EditText) findViewById(R.id.et_species);
        etGender = (EditText) findViewById(R.id.et_gender);
        etBirth = (EditText) findViewById(R.id.et_birth);
        btnRegister = (Button) findViewById(R.id.btn_register);
        btnPhoto = (Button) findViewById(R.id.btn_photo);
        btnPhotoNose = (Button) findViewById(R.id.btn_photo_nose);

        btnRegister.setOnClickListener(listener);
        btnPhoto.setOnClickListener(listener);
        btnPhotoNose.setOnClickListener(listener);

        imgDog = (ImageView) findViewById(R.id.img_photo);
        imgDogNose = (ImageView) findViewById(R.id.img_photo_nose);

        textChangedListener(etDogName);
        textChangedListener(etSpecies);
        textChangedListener(etGender);
        textChangedListener(etBirth);

    }

}
