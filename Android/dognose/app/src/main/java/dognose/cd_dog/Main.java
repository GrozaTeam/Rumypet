package dognose.cd_dog;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

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
                    Intent intentInf = new Intent(getApplicationContext(), LoginActivity.class);
                    startActivity(intentInf);
                    break;

                case R.id.btn_find:
                    AlertDialog.Builder dialog = new AlertDialog.Builder(Main.this);
                    dialog.setCancelable(false);
                    dialog.setTitle("Find the dog");
                    dialog.setMessage("We will find the dog's information by dog's nose. So you need to take three pictures of the dog.\n\nWill you try it?");
                    dialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {

                            Intent intentNosePhoto = new Intent(getApplicationContext(), CameraActivity.class);
                            String ownerId = "temp|find|";
                            intentNosePhoto.putExtra("ownerId", ownerId);
                            startActivityForResult(intentNosePhoto,1);

                        }
                    }).setNegativeButton("Cancel ", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //Action for "Cancel".
                        }
                    });

                    final AlertDialog alert = dialog.create();
                    alert.show();
                    break;
                case R.id.btn_list:
                    Intent intentList = new Intent(getApplicationContext(), DBChecker.class);
                    startActivity(intentList);
                    break;
            }
        }
    };
}
