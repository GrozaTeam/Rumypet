package dognose.cd_dog;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;

import dognose.cd_dog.ListViewContent.ListViewAdapter;
import dognose.cd_dog.model.Dog;
import dognose.cd_dog.model.Res;
import dognose.cd_dog.network.NetworkUtil;
import dognose.cd_dog.utils.Constants;
import retrofit2.adapter.rxjava.HttpException;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by paeng on 2018. 5. 25..
 */

public class FindDogCandidateActivity extends AppCompatActivity {

    private SharedPreferences mSharedPreferences;
    private CompositeSubscription mSubscriptions;
    private ArrayList<Dog> dogArrayList;

    private ListView listViewDog;
    private ListViewAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_find_dog_progress);
        bindingView();
        Intent intent = getIntent();
        String result = intent.getStringExtra("result");
        String[] dogIds = result.split(":");

        dogArrayList = new ArrayList<Dog>();
        adapter = new ListViewAdapter();

        loadDogProfile(dogIds[0]);
        loadDogProfile(dogIds[1]);

        listViewDog.setAdapter(adapter);

    }


    private void loadDogProfile(String dogId){

        mSubscriptions.add(NetworkUtil.getRetrofit().getDogProfile(dogId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponseDog,this::handleError));
    }

    private void handleResponseDog(Dog dog) {

        dogArrayList.add(dog);
        String url = Constants.BASE_URL + "images/" + dog.getDogId();
        adapter.addItemDog(url, dog.getName(), dog.getSpecies(), dog.getGender(), getAge(dog.getBirth()));



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
        Toast.makeText(FindDogCandidateActivity.this, message, Toast.LENGTH_SHORT).show();

    }

    private String getAge(String birth){

        String birthYear = birth.substring(0,4);

        Calendar calendarStart = Calendar.getInstance();
        int todayYear = calendarStart.get(Calendar.YEAR);

        String age = String.valueOf(todayYear - Integer.valueOf(birthYear) +1);

        return age;
    }



    private void bindingView(){
        listViewDog = (ListView)findViewById(R.id.listview_dog);
        mSubscriptions = new CompositeSubscription();



    }
    @Override
    protected void onDestroy(){
        super.onDestroy();
        mSubscriptions.unsubscribe();
    }
}
