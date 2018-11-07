package dognose.cd_dog;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
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

import dognose.cd_dog.utils.*;

/**
 * Created by paeng on 2018. 3. 26..
 */

public class EditProfile extends AppCompatActivity {

    private EditText etPw, etnewPw, etnewPw2, etOwnerName, etOwnerPhone;
    private Button btnEdit, btnCheckPhone;
    // For database
    private String id="",pw ="", newpw="", newpw2="", ownerName="", ownerPhone="";
    private ProgressBar mProgressbar;
    private CompositeSubscription mSubscriptions;
    private SharedPreferences mSharedPreferences;
    private String mToken;
    private String mEmail;

    private TextView tvId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_profile_edit);
        mSubscriptions = new CompositeSubscription();
        bindingView();
        initSharedPreferences();
        loadProfile();
    }
    private void initSharedPreferences() {

        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        mToken = mSharedPreferences.getString(Constants.TOKEN,"");
        mEmail = mSharedPreferences.getString(Constants.EMAIL,"");
    }
    private void loadProfile() {

        mSubscriptions.add(NetworkUtil.getRetrofit(mToken).getProfile(mEmail)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponse,this::handleError));
    }
    private void handleResponse(User user) {

        tvId.setText(user.getEmail());
        etOwnerName.setText(user.getName());
        etOwnerPhone.setText(user.getPhone());
    }

    private boolean checkjoin() {

        if (id.equals("")) {
            Toast.makeText(EditProfile.this, "Please input id", Toast.LENGTH_SHORT).show();
            return false;

        }/*else if(!validateEmail(id)){
            Toast.makeText(editProfile.this, "Input ID into e-mail form", Toast.LENGTH_SHORT).show();
            return false;

        }*/else if (pw.equals("")) {
            Toast.makeText(EditProfile.this, "Please input password", Toast.LENGTH_SHORT).show();
            return false;

        }else if (newpw.equals("")) {
            Toast.makeText(EditProfile.this, "Please input password", Toast.LENGTH_SHORT).show();
            return false;

        }else if (newpw2.equals("")) {
            Toast.makeText(EditProfile.this, "Please input password2", Toast.LENGTH_SHORT).show();
            return false;

        }else if (ownerName.equals("")) {
            Toast.makeText(EditProfile.this, "Please input name", Toast.LENGTH_SHORT).show();
            return false;

        }else if (ownerPhone.equals("")) {
            Toast.makeText(EditProfile.this, "Please input phone", Toast.LENGTH_SHORT).show();
            return false;

        }else if(!newpw.equals(newpw2)){
            Toast.makeText(EditProfile.this, "Password is not same", Toast.LENGTH_SHORT).show();
            return false;

        }else if(newpw.length()<4 || newpw.length()>15){
            Toast.makeText(EditProfile.this, "Choose Password Length between 4 and 15", Toast.LENGTH_SHORT).show();
            return false;

        }else if(ownerPhone.length() != 10 && ownerPhone.length() != 11){
            Toast.makeText(EditProfile.this, "Phone number Length must be 10 or 11", Toast.LENGTH_SHORT).show();
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
                        Intent intent = new Intent(getApplicationContext(), ProfileActivity.class);
                        startActivity(intent);
                        finish();
                    }

                    break;


                case R.id.btn_check_phone:
                    Toast.makeText(EditProfile.this, "Coming Soon...", Toast.LENGTH_SHORT).show();
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

        Toast.makeText(EditProfile.this, message, Toast.LENGTH_SHORT).show();


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
