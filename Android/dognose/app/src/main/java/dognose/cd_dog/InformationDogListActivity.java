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

import org.w3c.dom.Text;

import java.util.ArrayList;

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

    private LinearLayout btnAdd, btnSet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.information_dog_list);
        tvOwnerId = (TextView)findViewById(R.id.tv_owner_id);
        listViewDog = (ListView)findViewById(R.id.lv_dog);
        btnAdd = (LinearLayout) findViewById(R.id.btn_add_dog);
        btnSet = (LinearLayout) findViewById(R.id.btn_set_user);
        btnAdd.setOnClickListener(listener);
        btnSet.setOnClickListener(listener);

        listViewDog.setOnItemClickListener(new ListView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView parent, View view, int position, long id) {


                Intent intent = new Intent(getApplicationContext(), InformationDogListDetail.class);
                intent.putExtra("data", dogArrayList.get(position));

                startActivity(intent);

            }
        });


        Intent intent = getIntent();
        ownerId = intent.getStringExtra("id");
        tvOwnerId.setText("Hello " + ownerId);

        UpdatingList();
    }

    @Override
    protected void onResume(){
        super.onResume();
        UpdatingList();

    }

    public void UpdatingList(){
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



    Button.OnClickListener listener = new Button.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btn_add_dog:
                    Intent intentAdd = new Intent(getApplicationContext(), RegisterAdditionalDogActivity.class);
                    intentAdd.putExtra("id", ownerId);
                    startActivity(intentAdd);
                    break;

                case R.id.btn_set_user:
                    break;
            }
        }
    };

}
