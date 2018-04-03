package dognose.cd_dog;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

public class Main extends AppCompatActivity {

    private LinearLayout btnAdd, btnInf, btnFind, btnList, btnSet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_main);

        bindingView();


    }

    public void bindingView(){
        btnAdd = (LinearLayout)findViewById(R.id.btn_add);
        btnInf = (LinearLayout)findViewById(R.id.btn_inf);
        btnFind = (LinearLayout)findViewById(R.id.btn_find);
        btnList = (LinearLayout)findViewById(R.id.btn_list);
        btnSet = (LinearLayout)findViewById(R.id.btn_set);
        btnAdd.setOnClickListener(listener);
        btnInf.setOnClickListener(listener);
        btnFind.setOnClickListener(listener);
        btnList.setOnClickListener(listener);
        btnSet.setOnClickListener(listener);


    }

    Button.OnClickListener listener = new Button.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btn_add:
                    Intent intentAdd = new Intent(getApplicationContext(), RegisterActivity.class);
                    startActivity(intentAdd);
                    break;

                case R.id.btn_inf:
                    Intent intentInf = new Intent(getApplicationContext(), InformationDogListActivity.class);
                    startActivity(intentInf);
                    break;

                case R.id.btn_find:
                    Intent intentFind = new Intent(getApplicationContext(), CameraActivity.class);
                    startActivity(intentFind);
                    break;
                case R.id.btn_list:
                    Log.d("paeng", "hi");
                    Intent intentList = new Intent(getApplicationContext(), DBChecker.class);
                    startActivity(intentList);
                    break;
            }
        }
    };
}
