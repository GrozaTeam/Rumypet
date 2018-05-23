package dognose.cd_dog;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
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
 * Created by paeng on 2018. 3. 26..
 */

public class InformationDogListDetailDetail extends AppCompatActivity {


    private TextView tvAbove, tvId, tvName, tvSpecies, tvGender, tvBirth, tvAge, tvOwnerName, tvOwnerPhone, tvOwnerEmail;
    private ImageView imgDog;
    private Button btnEdit;
    private SharedPreferences mSharedPreferences;
    private CompositeSubscription mSubscriptions;
    private String mToken;
    private String mEmail;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.information_dog_detail_more);
        bindingView();

        Intent intent = getIntent();
        String dogId = intent.getStringExtra("dogId");
        String dogName = intent.getStringExtra("dogName");
        tvId.setText(dogId);
        tvName.setText(dogName);
        tvSpecies.setText(intent.getStringExtra("dogSpecies"));
        tvBirth.setText(intent.getStringExtra("dogBirth"));
        tvAge.setText(intent.getStringExtra("dogAge"));
        tvGender.setText(intent.getStringExtra("dogGender"));

        tvAbove.setText("About "+dogName);

        mSubscriptions = new CompositeSubscription();
        initSharedPreferences();
        String url = Constants.BASE_URL + "images/" + dogId;
        Glide.with(this).load(url).into(imgDog);
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

        tvOwnerName.setText(user.getName());
        tvOwnerEmail.setText(user.getEmail());
        tvOwnerPhone.setText(user.getPhone());
    }

    private void handleError(Throwable error) {

        if (error instanceof HttpException) {

            Gson gson = new GsonBuilder().create();
            try {
                String errorBody = ((HttpException) error).response().errorBody().string();
                Res response = gson.fromJson(errorBody, dognose.cd_dog.model.Res.class);
                showSnackBarMessage(response.getMessage());

            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            showSnackBarMessage("Network Error !");
        }
    }
    private void showSnackBarMessage(String message){
        Toast.makeText(InformationDogListDetailDetail.this, message, Toast.LENGTH_SHORT).show();

    }

    private void bindingView(){
        tvAbove = (TextView)findViewById(R.id.tv_above);
        tvId = (TextView)findViewById(R.id.tv_id);
        tvName = (TextView)findViewById(R.id.tv_name);
        tvSpecies = (TextView)findViewById(R.id.tv_species);
        tvGender = (TextView)findViewById(R.id.tv_gender);
        tvBirth = (TextView)findViewById(R.id.tv_birth);
        tvAge = (TextView)findViewById(R.id.tv_age);
        tvOwnerName = (TextView)findViewById(R.id.tv_owner_name);
        tvOwnerPhone = (TextView)findViewById(R.id.tv_owner_phone);
        tvOwnerEmail = (TextView)findViewById(R.id.tv_owner_email);
        imgDog = (ImageView)findViewById(R.id.img_dog);
        btnEdit = (Button)findViewById(R.id.btn_edit_dog_profile);
        btnEdit.setOnClickListener(buttonListener);
    }

    Button.OnClickListener buttonListener = new Button.OnClickListener(){
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.btn_edit_dog_profile:
                    Toast.makeText(InformationDogListDetailDetail.this, "Coming Soon", Toast.LENGTH_SHORT).show();
                    break;

                default:
                    break;

            }
        }
    };


}
