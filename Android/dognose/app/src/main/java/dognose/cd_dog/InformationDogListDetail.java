package dognose.cd_dog;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by paeng on 2018. 3. 26..
 */

public class InformationDogListDetail extends AppCompatActivity {

    private String dogData;
    private String[] dogDatas;

    private TextView tvName, tvSpecies, tvGender, tvBirth, tvMemo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.information_dog_detail);

        Intent intent = getIntent();
        dogData = intent.getStringExtra("data");
        dogDatas = dogData.split("/");

        Log.d("Paeng", dogData);
        Log.d("Paeng dogname", dogDatas[1]);

        tvName = (TextView)findViewById(R.id.info_name);
        tvGender = (TextView)findViewById(R.id.info_gender);
        tvBirth = (TextView)findViewById(R.id.info_birth);
        tvSpecies = (TextView)findViewById(R.id.info_species);

        tvName.setText(dogDatas[2]);
        tvSpecies.setText(dogDatas[3]);
        tvGender.setText(dogDatas[4]);
        tvBirth.setText(dogDatas[5]);




    }

}
