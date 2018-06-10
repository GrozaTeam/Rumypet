package dognose.cd_dog.InnerDB;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

import dognose.cd_dog.R;

/**
 * Created by paeng on 2018. 4. 3..
 */

public class DBChecker extends AppCompatActivity {

    private ListView listOwner, listDog;


    private String dbstringOwner[], dbstringDog[];
    private ArrayList<String> itemsOwner, itemsDog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.checkout_database);


        listOwner = (ListView) findViewById(R.id.list_owner) ;
        listDog = (ListView) findViewById(R.id.list_dog) ;


        final DBHelper dbHelper = new DBHelper(getApplicationContext(), "RumyPet.db", null, 1);

        dbstringOwner = dbHelper.getAllDataOwnerForChecker();


        itemsOwner = new ArrayList();
        int i=0;
        while(dbstringOwner[i]!=null){
            itemsOwner.add(dbstringOwner[i]);
            i++;
        }

        // 폴더 데이터
        dbstringDog = dbHelper.getAllDataDogForChecker();
        itemsDog = new ArrayList();
        int j=0;
        while(dbstringOwner[j]!=null){
            itemsDog.add(dbstringDog[j]);
            j++;
        }



        ArrayAdapter adapter = new ArrayAdapter(DBChecker.this, android.R.layout.simple_list_item_1, itemsOwner);
        listOwner.setAdapter(adapter) ;


        ArrayAdapter adapterFolder = new ArrayAdapter(DBChecker.this, android.R.layout.simple_list_item_1, itemsDog);
        listDog.setAdapter(adapterFolder);


    }
}
