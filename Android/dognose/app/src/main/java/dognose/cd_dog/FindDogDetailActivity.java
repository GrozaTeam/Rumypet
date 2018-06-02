package dognose.cd_dog;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;

import dognose.cd_dog.model.Res;
import dognose.cd_dog.model.User;
import dognose.cd_dog.network.NetworkUtil;
import dognose.cd_dog.utils.Constants;
import retrofit2.adapter.rxjava.HttpException;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by paeng on 2018. 6. 2..
 */

public class FindDogDetailActivity extends AppCompatActivity {

    private String dogId, dogName, dogSpecies, dogGender, ownerId;
    private TextView tvInfoName, tvInfoGender, tvInfoSpecies, tvInfoOwnerName, tvInfoOwnerPhone, tvInfoOwnerEmail;
    private SharedPreferences mSharedPreferences;
    private CompositeSubscription mSubscriptions;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_find_dog_detail);
        getIntentData();
        bindingView();

        mSubscriptions = new CompositeSubscription();

        loadProfile();

    }
    private void loadProfile() {

        mSubscriptions.add(NetworkUtil.getRetrofit().getUserProfile(ownerId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponse,this::handleError));
    }
    private void handleResponse(User user) {
        
        tvInfoOwnerName.setText(user.getName());
        tvInfoOwnerPhone.setText(user.getPhone());
        tvInfoOwnerEmail.setText(user.getEmail());

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

    private void showSnackBarMessage(String message){
        Toast.makeText(FindDogDetailActivity.this, "Network Error!", Toast.LENGTH_SHORT).show();

    }
    private void getIntentData(){
        Intent intent = getIntent();
        ownerId = intent.getStringExtra("ownerId");
        dogId = intent.getStringExtra("dogId");
        dogName = intent.getStringExtra("dogId");
        dogGender = intent.getStringExtra("dogGender");
        dogSpecies = intent.getStringExtra("dogSpecies");
    }

    private void bindingView(){
        tvInfoName = (TextView)findViewById(R.id.info_name);
        tvInfoGender = (TextView)findViewById(R.id.info_gender);
        tvInfoSpecies = (TextView)findViewById(R.id.info_species);
        tvInfoOwnerName = (TextView)findViewById(R.id.info_owner_name);
        tvInfoOwnerPhone = (TextView)findViewById(R.id.info_owner_phone);
        tvInfoOwnerEmail = (TextView)findViewById(R.id.info_owner_email);
        tvInfoName.setText(dogName);
        tvInfoSpecies.setText(dogSpecies);
        tvInfoGender.setText(dogGender);

    }


}
