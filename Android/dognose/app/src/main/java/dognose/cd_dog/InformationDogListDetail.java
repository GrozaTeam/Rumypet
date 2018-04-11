package dognose.cd_dog;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import dognose.cd_dog.model.Dog;

/**
 * Created by paeng on 2018. 3. 26..
 */

public class InformationDogListDetail extends AppCompatActivity {

    private int position, dogNum;

    private ArrayList<Dog> dogArrayList;

    private ImageButton btnBefore, btnAfter;


    private TextView tvName, tvSpecies, tvGender, tvBirth, tvMemo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.information_dog_detail);
        bindingView();

        dogArrayList = new ArrayList<Dog>();

        Intent intent = getIntent();
        position = Integer.parseInt(intent.getStringExtra("position"));
        dogNum = Integer.parseInt(intent.getStringExtra("dogNum"));
        dogArrayList = (ArrayList<Dog>) intent.getSerializableExtra("dogSet");
        setInformation();


    }

    ImageButton.OnClickListener imgBtnListener = new ImageButton.OnClickListener(){

        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.btn_after:
                    if (position == dogNum -1){
                        Toast.makeText(InformationDogListDetail.this, "This is the last DOG", Toast.LENGTH_SHORT).show();
                    }else{
                        position +=1;
                        setInformation();
                        Log.d("PaengPosition", String.valueOf(position));
                        Log.d("PaengDogNum", String.valueOf(dogNum));
                    }
                    break;

                case R.id.btn_before:
                    if (position == 0){
                        Toast.makeText(InformationDogListDetail.this, "This is the first DOG", Toast.LENGTH_SHORT).show();
                    }else{
                        position -=1;
                        setInformation();
                        Log.d("PaengPosition", String.valueOf(position));
                    }
                    break;
            }
        }
    };

    private void setInformation(){
        tvName.setText(dogArrayList.get(position).getName());
        tvSpecies.setText(dogArrayList.get(position).getSpecies());
        tvGender.setText(dogArrayList.get(position).getGender());
        tvBirth.setText(dogArrayList.get(position).getBirth());
    }

    private void bindingView(){
        tvName = (TextView)findViewById(R.id.info_name);
        tvGender = (TextView)findViewById(R.id.info_gender);
        tvBirth = (TextView)findViewById(R.id.info_birth);
        tvSpecies = (TextView)findViewById(R.id.info_species);

        btnBefore = (ImageButton)findViewById(R.id.btn_before);
        btnAfter = (ImageButton)findViewById(R.id.btn_after);
        btnBefore.setOnClickListener(imgBtnListener);
        btnAfter.setOnClickListener(imgBtnListener);

    }

}
