package dognose.cd_dog;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

/**
 * Created by paeng on 2018. 3. 26..
 */

public class InformationDogListDetailDetail extends AppCompatActivity {

    private String dogData;
    private String[] dogDatas;

    private TextView tvName, tvSpecies, tvGender, tvBirth, tvMemo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.information_dog_detail_more);



    }

}
