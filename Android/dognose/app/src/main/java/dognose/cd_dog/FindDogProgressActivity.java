package dognose.cd_dog;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import java.net.URI;

/**
 * Created by paeng on 2018. 5. 24..
 */

public class FindDogProgressActivity extends AppCompatActivity {

    private String[] imageNose;
    private Uri[] imageNoseUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_find_dog_progress);
        bindingView();





    }

    private void bindingView(){

       imageNose = new String[3];
       imageNoseUri = new Uri[3];
        Intent intent = getIntent();
        imageNose = intent.getStringArrayExtra("input_dog");

        for (int i=0;i<3;i++){
            imageNoseUri[i] = Uri.parse(imageNose[i]);
        }
    }
}
