package dognose.cd_dog;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
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
import dognose.cd_dog.model.Res;
import dognose.cd_dog.network.NetworkUtil;
import dognose.cd_dog.utils.Constants;

import java.io.IOException;

import retrofit2.adapter.rxjava.HttpException;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by paeng on 2018. 4. 5..
 */

public class LoginActivity extends AppCompatActivity {

    private Button btnLogin;
    private EditText etId, etPw;
    private String id, pw;

    private ProgressBar mProgressBar;

    private CompositeSubscription mSubscriptions;
    private SharedPreferences mSharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_login);

        etId = (EditText)findViewById(R.id.et_id);
        etPw = (EditText)findViewById(R.id.et_pw);
        btnLogin = (Button)findViewById(R.id.btn_login);
        mProgressBar = (ProgressBar)findViewById(R.id.progress);

        textChangedListener(etId);
        textChangedListener(etPw);

        mSubscriptions = new CompositeSubscription();
        initSharedPreferences();

        btnLogin.setOnClickListener(new Button.OnClickListener() {

            @Override
            public void onClick(View v) {
                loginProcess(id,pw);
            }
        });
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        mSubscriptions.unsubscribe();
    }

    private void initSharedPreferences() {

        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(LoginActivity.this);

    }

    private void loginProcess(String email, String password) {

        mSubscriptions.add(NetworkUtil.getRetrofit(email, password).login()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponse,this::handleError));
    }

    private void handleResponse(Res response) {

        mProgressBar.setVisibility(View.GONE);

        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString(Constants.TOKEN,response.getToken());
        editor.putString(Constants.EMAIL,response.getMessage());
        editor.apply();

        Intent intent = new Intent(getApplicationContext(), InformationDogListActivity.class);
        startActivity(intent);

        etId.setText(null);
        etPw.setText(null);
        finish();
    }

    private void handleError(Throwable error) {

        mProgressBar.setVisibility(View.GONE);

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
            Log.d("어떤 에러가 뜨니 찬일", String.valueOf(error));
            showSnackBarMessage("Network Error!");
        }
    }

    private void showSnackBarMessage(String message) {

        Toast.makeText(LoginActivity.this, message, Toast.LENGTH_SHORT).show();
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
