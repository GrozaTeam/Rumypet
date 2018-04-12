package dognose.cd_dog;


import android.os.Bundle;
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

        } else if (pw.equals("")) {
            Toast.makeText(RegisterActivity.this, "Please input password", Toast.LENGTH_SHORT).show();
            return false;

        } else if (pw2.equals("")) {
            Toast.makeText(RegisterActivity.this, "Please input password2", Toast.LENGTH_SHORT).show();
            return false;

        } else if (ownerName.equals("")) {
            Toast.makeText(RegisterActivity.this, "Please input name", Toast.LENGTH_SHORT).show();
            return false;

        } else if (ownerPhone.equals("")) {
            Toast.makeText(RegisterActivity.this, "Please input phone", Toast.LENGTH_SHORT).show();
            return false;

        } else if(!pw.equals(pw2)){
            Toast.makeText(RegisterActivity.this, "Password is not same", Toast.LENGTH_SHORT).show();
            return false;

        } else {
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
                        //DBHelper dbHelper = new DBHelper(getApplicationContext(), "RumyPet.db", null, 1);
                        //dbHelper.insertOwner(id, pw, ownerName, ownerPhone);
                        //dbHelper.insertDog("id","dogname","species","gender", "birth");

                        User user = new User();
                        user.setName(ownerName);
                        user.setEmail(id);
                        user.setPassword(pw);
                        user.setPhone(ownerPhone);

                        mProgressbar.setVisibility(View.VISIBLE);
                        registerProcess(user);


                        Toast.makeText(RegisterActivity.this, "Register Complete.", Toast.LENGTH_SHORT).show();
                        finish();
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

        btnRegister.setOnClickListener(listener);
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
