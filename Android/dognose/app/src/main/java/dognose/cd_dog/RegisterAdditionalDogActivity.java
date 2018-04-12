package dognose.cd_dog;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.w3c.dom.Text;

import java.io.IOException;
import java.util.Calendar;
import java.util.Random;

import dognose.cd_dog.model.Dog;
import dognose.cd_dog.model.Res;
import dognose.cd_dog.network.NetworkUtil;
import retrofit2.adapter.rxjava.HttpException;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by paeng on 2018. 3. 26..
 */

public class RegisterAdditionalDogActivity extends AppCompatActivity {

    private static final int PICK_FROM_CAMERA = 0;
    private static final int PICK_FROM_ALBUM = 1;
    private static final int CROP_FROM_IMAGE = 2;
    private static final int colorMaleLight = 0XFFCCCCFF;
    private static final int colorMaleDark = 0XFF5555FF;
    private static final int colorFemaleLight =  0XFFFFCCCC;
    private static final int colorFemaleDark = 0XFFFF5555;



    private EditText etDogName, etSpecies;
    private TextView tvBirth;
    private Button btnRegister, btnPhoto, btnPhotoNose, btnGenderMale, btnGenderFemale;
    // For database
    private String dogName="", species="", gender="", birth="";
    private ImageView imgDog, imgDogNose;

    private String ownerId, ownerName;

    private CompositeSubscription mSubscriptions;

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
            Toast.makeText(RegisterAdditionalDogActivity.this, "Enter Dog Name", Toast.LENGTH_SHORT).show();
            return false;
        }
        else if (species.equals("")){
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
                        dogdb.setSpecies(species);

                        registerProgress(dogdb);


                        finish();
                    }
                    break;


                case R.id.btn_photo:

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
                    Log.d("PaengTest", "hi");
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

        showSnackBarMessage(response.getMessage());
    }

    private void handleError(Throwable error) {


        if (error instanceof HttpException) {

            Gson gson = new GsonBuilder().create();

            try {

                String errorBody = ((HttpException) error).response().errorBody().string();
                Res response = gson.fromJson(errorBody,Res.class);
                showSnackBarMessage(response.getMessage());

            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {

            showSnackBarMessage("Network Error !");
        }
    }

    private void showSnackBarMessage(String message) {

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
                    case R.id.et_species:
                        species = s.toString();
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
        textChangedListener(etSpecies);

        tvBirth.setOnClickListener(listener);



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


}
