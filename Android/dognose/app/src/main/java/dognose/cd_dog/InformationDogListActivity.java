package dognose.cd_dog;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ListView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;

/**
 * Created by paeng on 2018. 3. 26..
 */

public class InformationDogListActivity extends AppCompatActivity {

    private String ownerId;
    private TextView tvOwnerId;

    // Dog list 들을 위한 ListView

    private ArrayList<String> dogList;
    private ArrayList<String> items;
    ListViewAdapter adapter;
    private String[] dataDog;

    private ListView listViewDog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.information_dog_list);
        tvOwnerId = (TextView)findViewById(R.id.tv_owner_id);
        listViewDog = (ListView)findViewById(R.id.lv_dog);


        Intent intent = getIntent();
        ownerId = intent.getStringExtra("id");

        tvOwnerId.setText("Hello " + ownerId);

        UpdatingList();



    }

    public void UpdatingList(){
        final DBHelper dbHelper = new DBHelper(getApplicationContext(), "RumyPet.db", null, 1);

        dogList = new ArrayList();
        items = new ArrayList();

        dataDog = dbHelper.getResultOwnerDogList(ownerId);

        adapter = new ListViewAdapter();

        for (String data : dataDog){
            if(data!=null){
                Log.d("paengData", data);
                String data_element[] = data.split("/");


                adapter.addItemDog(null, data_element[2], data_element[3], data_element[4], data_element[5]);

            }
        }

        listViewDog.setAdapter(adapter);


    }

}
