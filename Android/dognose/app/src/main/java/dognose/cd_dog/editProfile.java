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
import dognose.cd_dog.model.*;
import dognose.cd_dog.network.*;

import retrofit2.adapter.rxjava.HttpException;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

import static dognose.cd_dog.utils.*;

/**
 * Created by paeng on 2018. 3. 26..
 */

public class editProfile extends AppCompatActivity {

    private EditText etPw, etnewPw, etnewPw2, etOwnerName, etOwnerPhone;
    private Button btnEdit, btnCheckPhone;
    // For database
    private String id="",pw ="", newpw="", newpw2="", ownerName="", ownerPhone="";

    private ProgressBar mProgressbar;
    private CompositeSubscription mSubscriptions;

    private TextView tvId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_profile);
        mSubscriptions = new CompositeSubscription();
        bindingView();
    }

    private boolean checkjoin() {

        if (id.equals("")) {
            Toast.makeText(editProfile.this, "Please input id", Toast.LENGTH_SHORT).show();
            return false;

        }/*else if(!validateEmail(id)){
            Toast.makeText(editProfile.this, "Input ID into e-mail form", Toast.LENGTH_SHORT).show();
            return false;

        }*/else if (pw.equals("")) {
            Toast.makeText(editProfile.this, "Please input password", Toast.LENGTH_SHORT).show();
            return false;

        }else if (newpw.equals("")) {
            Toast.makeText(editProfile.this, "Please input password", Toast.LENGTH_SHORT).show();
            return false;

        }else if (newpw2.equals("")) {
            Toast.makeText(editProfile.this, "Please input password2", Toast.LENGTH_SHORT).show();
            return false;

        }else if (ownerName.equals("")) {
            Toast.makeText(editProfile.this, "Please input name", Toast.LENGTH_SHORT).show();
            return false;

        }else if (ownerPhone.equals("")) {
            Toast.makeText(editProfile.this, "Please input phone", Toast.LENGTH_SHORT).show();
            return false;

        }else if(!newpw.equals(newpw2)){
            Toast.makeText(editProfile.this, "Password is not same", Toast.LENGTH_SHORT).show();
            return false;

        }else if(newpw.length()<4 || newpw.length()>15){
            Toast.makeText(editProfile.this, "Choose Password Length between 4 and 15", Toast.LENGTH_SHORT).show();
            return false;

        }else if(ownerPhone.length() != 10 && ownerPhone.length() != 11){
            Toast.makeText(editProfile.this, "Phone number Length must be 10 or 11", Toast.LENGTH_SHORT).show();
            return false;

        }else {
            return true;
        }
    }

    Button.OnClickListener listener = new Button.OnClickListener() {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btn_edit_profile:

                    if (checkjoin()) {

                        User user = new User();
                        user.setName(ownerName);
                        user.setEmail(id);
                        user.setPassword(newpw);
                        user.setPhone(ownerPhone);

                        mProgressbar.setVisibility(View.VISIBLE);
                        editProcess(user);
                        finish();
                    }

                    break;


                case R.id.btn_check_phone:
                    Toast.makeText(editProfile.this, "Coming Soon...", Toast.LENGTH_SHORT).show();
                    break;

                default:
                    break;
            }
        }
    };

    private void editProcess(User user) {

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

        Toast.makeText(editProfile.this, message, Toast.LENGTH_SHORT).show();


    }

    private void textChangedListener(final EditText etInput){
        etInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                switch (etInput.getId()){
                        case R.id.et_pw:
                        pw = s.toString();
                        break;
                    case R.id.et_new_pw:
                        newpw = s.toString();
                        break;
                    case R.id.et_pw2:
                        newpw2 = s.toString();
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
        tvId = (TextView) findViewById(R.id.tv_id);
        etPw = (EditText) findViewById(R.id.et_pw);
        etnewPw = (EditText) findViewById(R.id.et_new_pw);
        etnewPw2 = (EditText) findViewById(R.id.et_new_pw2);
        etOwnerName = (EditText) findViewById(R.id.et_owner_name);
        etOwnerPhone = (EditText) findViewById(R.id.et_owner_phone);
        btnEdit = (Button) findViewById(R.id.btn_edit_profile);
        btnCheckPhone = (Button) findViewById(R.id.btn_check_phone);
        mProgressbar = (ProgressBar) findViewById(R.id.progress);


        btnEdit.setOnClickListener(listener);
        btnCheckPhone.setOnClickListener(listener);

        textChangedListener(etPw);
        textChangedListener(etnewPw);
        textChangedListener(etnewPw2);
        textChangedListener(etOwnerName);
        textChangedListener(etOwnerPhone);

    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        mSubscriptions.unsubscribe();
    }

}
