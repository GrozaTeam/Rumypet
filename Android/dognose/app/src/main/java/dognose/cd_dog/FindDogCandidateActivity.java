package dognose.cd_dog;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;

import dognose.cd_dog.ListViewContent.ListViewAdapter;
import dognose.cd_dog.ListViewContent.ListViewAdapterCandidate;
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
    private ListViewAdapterCandidate adapter;
    private int rank_dog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_find_dog_candidate);
        bindingView();
        Intent intent = getIntent();
        String result = intent.getStringExtra("result");
        String[] dogIds = result.split("/");
        rank_dog = 1;

        dogArrayList = new ArrayList<Dog>();
        adapter = new ListViewAdapterCandidate();

        Log.d("doglist", dogIds[0]+"__"+dogIds[1]+"__"+dogIds[2]);

        String[] dogId_split_1 = dogIds[0].split(":");
        String[] dogId_split_2 = dogIds[1].split(":");
        String[] dogId_split_3 = dogIds[2].split(":");


        loadDogProfile(dogId_split_1[0]);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                loadDogProfile(dogId_split_2[0]);
            }
        }, 1000);
        handler.postDelayed(new Runnable() {
            public void run() {
                loadDogProfile(dogId_split_3[0]);
            }
        }, 1000);

        rank_dog = 1;


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
        adapter.addItemDogCandidate(String.valueOf(rank_dog), url, dog.getName(), dog.getSpecies(), dog.getGender(), getAge(dog.getBirth()));
        Log.d("TestPaeng", String.valueOf(rank_dog)+":"+dog.getName());
        rank_dog++;
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
        listViewDog = (ListView)findViewById(R.id.list_view_dog);
        mSubscriptions = new CompositeSubscription();

        listViewDog.setOnItemClickListener(new ListView.OnItemClickListener(){

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(FindDogCandidateActivity.this, FindDogDetailActivity.class);
                intent.putExtra("ownerId", dogArrayList.get(position).getOwnerId());
                intent.putExtra("dogId", dogArrayList.get(position).getDogId());
                intent.putExtra("dogName", dogArrayList.get(position).getName());
                intent.putExtra("dogGender", dogArrayList.get(position).getGender());
                intent.putExtra("dogSpecies", dogArrayList.get(position).getSpecies());

                startActivity(intent);
            }
        });



    }
    @Override
    protected void onDestroy(){
        super.onDestroy();
        mSubscriptions.unsubscribe();
    }
}
