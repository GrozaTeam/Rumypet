package dognose.cd_dog;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;

import dognose.cd_dog.ListViewContent.ListViewAdapter;
import dognose.cd_dog.ListViewContent.ListViewItem;
import dognose.cd_dog.model.Dog;
import dognose.cd_dog.model.Res;
import dognose.cd_dog.model.User;
import dognose.cd_dog.network.NetworkUtil;
import dognose.cd_dog.utils.Constants;

import retrofit2.adapter.rxjava.HttpException;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

import static dognose.cd_dog.utils.Constants.BASE_URL;

/**
 * Created by paeng on 2018. 3. 26..
 */

public class InformationDogListActivity extends AppCompatActivity {

    private String ownerId, ownerName;
    private TextView tvOwnerId;

    // Dog list 들을 위한 ListView
    private ArrayList<Dog> dogArrayList;
    ListViewAdapter adapter;
    private ListView listViewDog;
    private LinearLayout btnAdd, btnProfile;

    private SharedPreferences mSharedPreferences;
    private CompositeSubscription mSubscriptions;

    private String mToken;
    private String mEmail;

    private int dogNum = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.information_dog_list);
        mSubscriptions = new CompositeSubscription();
        bindingView();
        initSharedPreferences();
        loadProfile();
        loadDogProfile();
    }

    @Override
    protected void onResume(){
        super.onResume();
        loadDogProfile();
    }
    @Override
    protected void onDestroy(){
        super.onDestroy();
        mSubscriptions.unsubscribe();
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

    private void loadDogProfile(){

        mSubscriptions.add(NetworkUtil.getRetrofit(mToken).getDogProfiles(mEmail)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponseDog,this::handleError));
    }

    private void handleResponse(User user) {

        tvOwnerId.setText("Hi " + user.getName() + "!");
        ownerId = user.getEmail();
        ownerName = user.getName();

    }

    private void handleResponseDog(Dog[] dog) {

        dogArrayList = new ArrayList<Dog>();
        adapter = new ListViewAdapter();
        dogNum = 0;


        for (Dog dogitem : dog){
            if(dogitem != null){

                //http://ec2-13-209-70-175.ap-northeast-2.compute.amazonaws.com:8080/api/v1/images/DLGFDFXE
                String url = Constants.BASE_URL + "images/" + dogitem.getDogId();

                dogArrayList.add(dogitem);
                adapter.addItemDog(url, dogitem.getName(), dogitem.getSpecies(), dogitem.getGender(), getAge(dogitem.getBirth()));
                dogNum += 1;
            }
        }
        listViewDog.setAdapter(adapter);

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
        Toast.makeText(InformationDogListActivity.this, message, Toast.LENGTH_SHORT).show();

    }

    private String getAge(String birth){

        String birthYear = birth.substring(0,4);

        Calendar calendarStart = Calendar.getInstance();
        int todayYear = calendarStart.get(Calendar.YEAR);

        String age = String.valueOf(todayYear - Integer.valueOf(birthYear) +1);

        return age;
    }

    private void bindingView(){
        tvOwnerId = (TextView)findViewById(R.id.tv_owner_id);
        listViewDog = (ListView)findViewById(R.id.lv_dog);
        btnAdd = (LinearLayout) findViewById(R.id.btn_add_dog);
        btnProfile = (LinearLayout) findViewById(R.id.btn_profile);

        btnAdd.setOnClickListener(listener);
        btnProfile.setOnClickListener(listener);

        listViewDog.setOnItemClickListener(new ListView.OnItemClickListener(){

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent intent = new Intent(getApplicationContext(), InformationDogListDetail.class);
                intent.putExtra("position", String.valueOf(position));
                intent.putExtra("dogSet", dogArrayList);
                intent.putExtra("dogNum", String.valueOf(dogNum));
                intent.putExtra("userId", ownerId);
                startActivity(intent);

            }
        });
    }

    Button.OnClickListener listener = new Button.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btn_add_dog:
                    Intent intentAdd = new Intent(getApplicationContext(), RegisterAdditionalDogActivity.class);
                    intentAdd.putExtra("id", ownerId);
                    intentAdd.putExtra("name", ownerName);
                    startActivity(intentAdd);
                    break;

                case R.id.btn_profile:
                    Log.d("dog number", String.valueOf(dogNum));
                    Intent intent = new Intent(getApplicationContext(), ProfileActivity.class);
                    startActivity(intent);
                    break;

                default:

                    break;
            }
        }
    };

}
