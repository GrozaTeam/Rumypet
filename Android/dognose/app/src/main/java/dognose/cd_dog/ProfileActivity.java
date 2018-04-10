package dognose.cd_dog;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;

import dognose.cd_dog.model.Response;
import dognose.cd_dog.model.User;
import dognose.cd_dog.network.NetworkUtil;
import dognose.cd_dog.utils.Constants;
import retrofit2.adapter.rxjava.HttpException;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by paeng on 2018. 4. 10..
 */

public class ProfileActivity extends AppCompatActivity{

    private TextView tvID, tvPw, tvName, tvPhone;

    private SharedPreferences mSharedPreferences;
    private CompositeSubscription mSubscriptions;
    private String mToken;
    private String mEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_profile);
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

        tvID.setText(user.getEmail());
        tvName.setText(user.getName());
        tvPhone.setText(user.getPhone());
    }

    private void handleError(Throwable error) {

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

    private void showSnackBarMessage(String message){
        Toast.makeText(ProfileActivity.this, "Network Error!", Toast.LENGTH_SHORT).show();

    }


    private void bindingView(){
        tvID = (TextView)findViewById(R.id.tv_id);
        tvPw = (TextView)findViewById(R.id.tv_pw);
        tvName = (TextView)findViewById(R.id.tv_name);
        tvPhone = (TextView)findViewById(R.id.tv_phone);
    }

}
