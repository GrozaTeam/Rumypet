package dognose.cd_dog;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by paeng on 2018. 3. 26..
 */

public class SplashActivity extends AppCompatActivity {                                         // 맨 처음 띄우는 Splash 화면.


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);


        Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg){
                super.handleMessage(msg);

                Intent intent = new Intent(SplashActivity.this, Main.class);
                startActivity(intent);
                finish();
            }
        };
        handler.sendEmptyMessageDelayed(0,2000);
    }




}

