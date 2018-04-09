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

    private EditText etId, etPw, etPw2, etOwnerName, etOwnerPhone;
    private Button btnRegister, btnCheckId, btnCheckPhone;
    // For database
    private String id="", pw="", pw2="", ownerName, ownerPhone;
    private boolean idDulplicated = false;
    private boolean duplicateCheck = false;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_register);
        bindingView();

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
        etOwnerName = (EditText) findViewById(R.id.et_owner_name);
        etOwnerPhone = (EditText) findViewById(R.id.et_owner_phone);
        btnRegister = (Button) findViewById(R.id.btn_register);
        btnCheckId = (Button) findViewById(R.id.btn_check_id);
        btnCheckPhone = (Button) findViewById(R.id.btn_check_phone);

        btnRegister.setOnClickListener(listener);
        btnCheckId.setOnClickListener(listener);
        btnCheckPhone.setOnClickListener(listener);

        textChangedListener(etId);
        textChangedListener(etPw);
        textChangedListener(etPw2);
        textChangedListener(etOwnerName);
        textChangedListener(etOwnerPhone);

    }

}
