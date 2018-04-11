package dognose.cd_dog;

import android.content.Intent;
import android.content.SharedPreferences;
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
import java.util.ArrayList;

import dognose.cd_dog.model.Dog;
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

public class InformationDogListActivity extends AppCompatActivity {

    private String ownerId, ownerName;
    private TextView tvOwnerId;

    // Dog list 들을 위한 ListView

    private ArrayList<String> dogArrayList;
    ListViewAdapter adapter;
    private String[] dataDog;
    private ListView listViewDog;
    private LinearLayout btnAdd, btnProfile;


    private SharedPreferences mSharedPreferences;
    private CompositeSubscription mSubscriptions;

    private String mToken;
    private String mEmail;


    private ArrayList<ListViewItem> data;

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

        mSubscriptions.add(NetworkUtil.getRetrofit(mToken).getDogProfile(mEmail)
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

        dogArrayList = new ArrayList();
        adapter = new ListViewAdapter();

        for (Dog dogitem : dog){
            if(dogitem!=null){
                adapter.addItemDog(null, dogitem.getName(), dogitem.getSpecies(), dogitem.getGender(), dogitem.getBirth());
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
                Log.d("PaengPosition", String.valueOf(position));


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
                    Intent intent = new Intent(getApplicationContext(), ProfileActivity.class);
                    startActivity(intent);
                    break;

                default:

                    break;
            }
        }
    };

    public void UpdatingListInnerDB(){
        final DBHelper dbHelper = new DBHelper(getApplicationContext(), "RumyPet.db", null, 1);


        dogArrayList = new ArrayList();

        dataDog = dbHelper.getResultOwnerDogList(ownerId);

        adapter = new ListViewAdapter();

        for (String data : dataDog){
            if(data!=null){
                Log.d("paengData", data);
                dogArrayList.add(data);
                String data_element[] = data.split("/");



                adapter.addItemDog(null, data_element[2], data_element[3], data_element[4], data_element[5]);

            }
        }

        listViewDog.setAdapter(adapter);


    }

}
