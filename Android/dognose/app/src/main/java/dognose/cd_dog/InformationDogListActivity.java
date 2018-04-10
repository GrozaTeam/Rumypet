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

import org.w3c.dom.Text;

import java.io.IOException;
import java.util.ArrayList;

import dognose.cd_dog.model.Dog;
import dognose.cd_dog.model.Response;
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

    private String ownerId;
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
    private String mTokenDog;
    private String mTokenEmail;

    private TextView tvCheckError;
    private Button btnCheckError;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.information_dog_list);
        mSubscriptions = new CompositeSubscription();
        bindingView();
        initSharedPreferences();
        loadProfile();

        // UpdatingList();

/*
        listViewDog.setOnItemClickListener(new ListView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView parent, View view, int position, long id) {

                Intent intent = new Intent(getApplicationContext(), InformationDogListDetail.class);
                intent.putExtra("data", dogArrayList.get(position));

                startActivity(intent);

            }
        });
*/

    }

    @Override
    protected void onResume(){
        super.onResume();
        // UpdatingList();

    }
    //

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
        ownerId = user.getName();

    }
    private void handleResponseTest(User user){
        tvCheckError.setText("Phone: " + user.getPhone());
    }
    private void handleResponseDog(Dog dog) {

        String resultDogID = dog.getDogId();
        String resultDogName = dog.getName();

        Log.d("paengResult", resultDogID+"/"+resultDogName);

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
            Log.d("paengResultError", String.valueOf(error));
        }
    }
    private void handleErrorDog(Throwable error) {

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
        Toast.makeText(InformationDogListActivity.this, "Network Error!", Toast.LENGTH_SHORT).show();

    }
    //




    public void UpdatingList(){
        //final DBHelper dbHelper = new DBHelper(getApplicationContext(), "RumyPet.db", null, 1);


        loadDogProfile();

/*

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
*/

    }

    private void bindingView(){
        tvOwnerId = (TextView)findViewById(R.id.tv_owner_id);
        listViewDog = (ListView)findViewById(R.id.lv_dog);
        btnAdd = (LinearLayout) findViewById(R.id.btn_add_dog);
        btnProfile = (LinearLayout) findViewById(R.id.btn_profile);
        tvCheckError = (TextView) findViewById(R.id.tv_check_error);
        btnCheckError = (Button) findViewById(R.id.btn_check_error);
        btnAdd.setOnClickListener(listener);
        btnProfile.setOnClickListener(listener);
        btnCheckError.setOnClickListener(listener);
    }



    Button.OnClickListener listener = new Button.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btn_add_dog:
                    Intent intentAdd = new Intent(getApplicationContext(), RegisterAdditionalDogActivity.class);
                    intentAdd.putExtra("id", ownerId);
                    startActivity(intentAdd);
                    break;

                case R.id.btn_profile:
                    Intent intent = new Intent(getApplicationContext(), ProfileActivity.class);
                    startActivity(intent);
                    break;

                case R.id.btn_check_error:

                    UpdatingList();

                    break;

                default:

                    break;
            }
        }
    };

}
