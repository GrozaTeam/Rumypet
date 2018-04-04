package dognose.cd_dog;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

/**
 * Created by paeng on 2018. 4. 5..
 */

public class LoginActivity extends AppCompatActivity {

    private Button btnLogin;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_login);


        btnLogin = (Button)findViewById(R.id.btn_login);

        btnLogin.setOnClickListener(new Button.OnClickListener() {


            @Override
            public void onClick(View v) {
                Intent intentLogin = new Intent(getApplicationContext(), InformationDogListActivity.class);
                startActivity(intentLogin);
            }
        });





    }
}
