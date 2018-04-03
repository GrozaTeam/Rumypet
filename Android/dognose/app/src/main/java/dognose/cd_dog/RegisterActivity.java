package dognose.cd_dog;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

/**
 * Created by paeng on 2018. 3. 26..
 */

public class RegisterActivity extends AppCompatActivity {

    private EditText etId, etPw, etPw2, etDogName, etSpecies, etGender, etBirth, etOwnerName, etOwnerPhone;
    private Button btnRegister, btnCheckId, btnCheckPhone, btnPhoto;
    // For database
    private String id, pw, pw2, dogName, species, gender, birth, ownerName, ownerPhone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_register);
        bindingView();

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

        btnRegister.setOnClickListener(listener);
        btnCheckId.setOnClickListener(listener);
        btnCheckPhone.setOnClickListener(listener);

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

    Button.OnClickListener listener = new Button.OnClickListener() {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btn_register:

                    Log.d("paeng", id + pw + pw2 + dogName + species + gender);
                    if (checkjoin()) {
                        DBHelper dbHelper = new DBHelper(getApplicationContext(), "RumyPet.db", null, 1);
                        dbHelper.insertOwner(id, pw, ownerName, ownerPhone);
                        dbHelper.insertDog(id, dogName, species, gender, birth);

                        Toast.makeText(RegisterActivity.this, "Register Complete.", Toast.LENGTH_SHORT).show();
                        finish();


                    }

                    break;
                case R.id.btn_check_id:
                    Log.d("paeng", "id check pushed");
                    break;
                case R.id.btn_photo:
                    Intent intentPhoto = new Intent(getApplicationContext(), CameraActivity.class);
                    startActivity(intentPhoto);
                    break;

                case R.id.btn_check_phone:
                    Log.d("paeng", "phone check pushed");
                    break;
                default:
                    break;
            }
        }
    };

    private boolean checkjoin() {

        if (id.isEmpty()) {
            Toast.makeText(RegisterActivity.this, "ID를 입력해주세요.", Toast.LENGTH_SHORT).show();

            return false;
        } else if (pw.isEmpty()) {
            Toast.makeText(RegisterActivity.this, "패스워드를 입력해주세요.", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            return true;
        }
    }



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

}
