package dognose.cd_dog;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import pub.devrel.easypermissions.EasyPermissions;

public class Main extends AppCompatActivity {


    private LinearLayout btnAdd, btnInf, btnFind, btnList, btnSet;
    private String[] galleryPermissions = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        if (EasyPermissions.hasPermissions(this, galleryPermissions)) {
            Log.d("Rumypet", "Have Permission");
        } else {
            EasyPermissions.requestPermissions(this, "Access for storage",
                    101, galleryPermissions);
        }
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
                    Intent intentInf = new Intent(getApplicationContext(), LoginActivity.class);
                    startActivity(intentInf);
                    break;

                case R.id.btn_find:
                    Intent intent = new Intent(getApplicationContext(), FindDogActivity.class);
                    startActivity(intent);
                    break;

                case R.id.btn_list:
                    //Intent intentList = new Intent(getApplicationContext(), DBChecker.class);
                    //startActivity(intentList);
                    Toast.makeText(Main.this, "Coming Soon", Toast.LENGTH_SHORT);
                    break;

                case R.id.btn_set:
                    Toast.makeText(Main.this, "Coming Soon", Toast.LENGTH_SHORT);
                    break;
            }
        }
    };
}
