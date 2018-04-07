package dognose.cd_dog;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

/**
 * Created by paeng on 2018. 4. 5..
 */

public class LoginActivity extends AppCompatActivity {

    private Button btnLogin;
    private EditText etId, etPw;
    private String id, pw, comparePw;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_login);

        etId = (EditText)findViewById(R.id.et_id);
        etPw = (EditText)findViewById(R.id.et_pw);

        textChangedListener(etId);
        textChangedListener(etPw);


        btnLogin = (Button)findViewById(R.id.btn_login);

        btnLogin.setOnClickListener(new Button.OnClickListener() {

            @Override
            public void onClick(View v) {

                DBHelper dbHelper = new DBHelper(getApplicationContext(), "RumyPet.db", null, 1);
                try{
                    comparePw = dbHelper.getPwById(id);
                    if (comparePw.equals(pw)){
                        Toast.makeText(LoginActivity.this, "Sign In Complete", Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(getApplicationContext(), InformationDogListActivity.class);
                        intent.putExtra("id",id);
                        startActivity(intent);
                    }
                    else{
                        Toast.makeText(LoginActivity.this, "Checkout your password", Toast.LENGTH_LONG).show();
                    }

                }catch (Exception CursorIndexOutOfBoundsException){
                    Toast.makeText(LoginActivity.this, "Cannot find ID", Toast.LENGTH_LONG).show();
                }





            }
        });





    }

    private void textChangedListener(final EditText etInput){
        etInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                switch (etInput.getId()){
                    case R.id.et_id:
                        id = s.toString();
                        break;
                    case R.id.et_pw:
                        pw = s.toString();
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }
}
