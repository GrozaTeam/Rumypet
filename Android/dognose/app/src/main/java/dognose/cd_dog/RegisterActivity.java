package dognose.cd_dog;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.IOException;
import dognose.cd_dog.model.Res;
import dognose.cd_dog.model.User;
import dognose.cd_dog.network.NetworkUtil;

import retrofit2.adapter.rxjava.HttpException;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

import static dognose.cd_dog.utils.Validation.validateEmail;

/**
 * Created by paeng on 2018. 3. 26..
 */

public class RegisterActivity extends AppCompatActivity {

    private EditText etId, etPw, etPw2, etOwnerName, etOwnerPhone;
    private Button btnRegister, btnCheckPhone;
    // For database
    private String id="", pw="", pw2="", ownerName="", ownerPhone="";

    private ProgressBar mProgressbar;
    private CompositeSubscription mSubscriptions;

    private TextView tvSignIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_register);
        mSubscriptions = new CompositeSubscription();
        bindingView();

    }

    private boolean checkjoin() {

        if (id.equals("")) {
            Toast.makeText(RegisterActivity.this, "Please input id", Toast.LENGTH_SHORT).show();
            return false;

        }else if(!validateEmail(id)){
            Toast.makeText(RegisterActivity.this, "Input ID into e-mail form", Toast.LENGTH_SHORT).show();
            return false;

        }else if (pw.equals("")) {
            Toast.makeText(RegisterActivity.this, "Please input password", Toast.LENGTH_SHORT).show();
            return false;

        }else if (pw2.equals("")) {
            Toast.makeText(RegisterActivity.this, "Please input password2", Toast.LENGTH_SHORT).show();
            return false;

        }else if (ownerName.equals("")) {
            Toast.makeText(RegisterActivity.this, "Please input name", Toast.LENGTH_SHORT).show();
            return false;

        }else if (ownerPhone.equals("")) {
            Toast.makeText(RegisterActivity.this, "Please input phone", Toast.LENGTH_SHORT).show();
            return false;

        }else if(!pw.equals(pw2)){
            Toast.makeText(RegisterActivity.this, "Password is not same", Toast.LENGTH_SHORT).show();
            return false;

        }else if(pw.length()<4 || pw.length()>15){
            Toast.makeText(RegisterActivity.this, "Choose Password Length between 4 and 15", Toast.LENGTH_SHORT).show();
            return false;

        }else if(ownerPhone.length() != 10 && ownerPhone.length() != 11){
            Toast.makeText(RegisterActivity.this, "Phone number Length must be 10 or 11", Toast.LENGTH_SHORT).show();
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

                        User user = new User();
                        user.setName(ownerName);
                        user.setEmail(id);
                        user.setPassword(pw);
                        user.setPhone(ownerPhone);

                        mProgressbar.setVisibility(View.VISIBLE);
                        registerProcess(user);
                        finish();
                    }

                    break;


                case R.id.btn_check_phone:
                    Toast.makeText(RegisterActivity.this, "Coming Soon...", Toast.LENGTH_SHORT).show();
                    break;

                case R.id.tv_sign_in:
                    Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                    startActivity(intent);
                    finish();

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

    private void handleResponse(Res response) {

        mProgressbar.setVisibility(View.GONE);
        showSnackBarMessage(response.getMessage());
    }

    private void handleError(Throwable error) {

        mProgressbar.setVisibility(View.GONE);

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
        btnCheckPhone = (Button) findViewById(R.id.btn_check_phone);
        mProgressbar = (ProgressBar) findViewById(R.id.progress);
        tvSignIn = (TextView) findViewById(R.id.tv_sign_in);


        btnRegister.setOnClickListener(listener);
        btnCheckPhone.setOnClickListener(listener);
        tvSignIn.setOnClickListener(listener);

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
