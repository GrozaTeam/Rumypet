package dognose.cd_dog;


import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;

import dognose.cd_dog.R;
import dognose.cd_dog.model.Response;
import dognose.cd_dog.model.User;
import dognose.cd_dog.network.NetworkUtil;

import retrofit2.adapter.rxjava.HttpException;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

import static dognose.cd_dog.utils.Validation.validateEmail;
import static dognose.cd_dog.utils.Validation.validateFields;
import static java.lang.System.err;

/**
 * Created by paeng on 2018. 3. 26..
 */

public class RegisterActivity extends AppCompatActivity {

    private EditText etId, etPw, etPw2, etOwnerName, etOwnerPhone;
    private Button btnRegister, btnCheckId, btnCheckPhone;
    // For database
    private String id="", pw="", pw2="", ownerName, ownerPhone;
    private boolean idDulplicated = false;
    private boolean duplicateCheck = false;


    private ProgressBar mProgressbar;

    private CompositeSubscription mSubscriptions;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_register);
        mSubscriptions = new CompositeSubscription();
        bindingView();

    }


    private boolean checkjoin() {

        if (id.equals("")) {
            Toast.makeText(RegisterActivity.this, "ID를 입력해주세요.", Toast.LENGTH_SHORT).show();

            return false;
        }else if(!validateEmail(id)){
            Toast.makeText(RegisterActivity.this, "ID를 E-mail형태로 입력해주세요.", Toast.LENGTH_SHORT).show();

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
                        //내부 DB 이용하여 Register 할 때
                        DBHelper dbHelper = new DBHelper(getApplicationContext(), "RumyPet.db", null, 1);
                        dbHelper.insertOwner(id, pw, ownerName, ownerPhone);

                        User user = new User();
                        user.setName(ownerName);
                        user.setEmail(id);
                        user.setPassword(pw);

                        mProgressbar.setVisibility(View.VISIBLE);
                        registerProcess(user);




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


    private void registerProcess(User user) {

        mSubscriptions.add(NetworkUtil.getRetrofit().register(user)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponse,this::handleError));
    }

    private void handleResponse(Response response) {

        mProgressbar.setVisibility(View.GONE);
        showSnackBarMessage(response.getMessage());
    }

    private void handleError(Throwable error) {

        mProgressbar.setVisibility(View.GONE);

        if (error instanceof HttpException) {

            Gson gson = new GsonBuilder().create();

            try {

                String errorBody = ((HttpException) error).response().errorBody().string();
                Response response = gson.fromJson(errorBody,Response.class);
                showSnackBarMessage(response.getMessage());

            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {

            showSnackBarMessage("Network Error !");
        }
    }

    private void showSnackBarMessage(String message) {

        Toast.makeText(RegisterActivity.this, message, Toast.LENGTH_SHORT).show();


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
        mProgressbar = (ProgressBar) findViewById(R.id.progress);

        btnRegister.setOnClickListener(listener);
        btnCheckId.setOnClickListener(listener);
        btnCheckPhone.setOnClickListener(listener);

        textChangedListener(etId);
        textChangedListener(etPw);
        textChangedListener(etPw2);
        textChangedListener(etOwnerName);
        textChangedListener(etOwnerPhone);

    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        mSubscriptions.unsubscribe();
    }

}
