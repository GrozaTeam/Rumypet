package dognose.cd_dog;

import android.content.ClipData;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

/**
 * Created by paeng on 2018. 5. 24..
 */

public class FindDogActivity extends AppCompatActivity {

    private Button btnFind;
    private static final int CAMERA_CODE=1111;
    private static final int GALLERY_CODE=1112;
    private static final int RECORD_CODE=1113;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_find_dog);
        bindingView();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {

            switch (requestCode) {
                case CAMERA_CODE:
                    break;
                case RECORD_CODE:
                    String[] imageNoseRecorded = new String[3];
                    imageNoseRecorded[0] = "content://video0";
                    imageNoseRecorded[1] = "content://video1";
                    imageNoseRecorded[2] = "content://video2";
                    Intent intentRecord = new Intent(FindDogActivity.this, FindDogProgressActivity.class);
                    intentRecord.putExtra("input_dog", imageNoseRecorded);
                    startActivity(intentRecord);
                    finish();

                    break;

                case GALLERY_CODE:
                    String[] imageNose = new String[3];
                    ClipData clipData = data.getClipData();
                    for (int i=0;i<3;i++){
                        if(i<clipData.getItemCount()){
                            Uri urione = clipData.getItemAt(i).getUri();

                            switch (i){
                                case 0:
                                    imageNose[0] = urione.toString();
                                    break;
                                case 1:
                                    imageNose[1] = urione.toString();
                                    break;
                                case 2:
                                    imageNose[2] = urione.toString();
                                    break;
                            }
                        }
                    }
                    Log.d("TestPaengRecord", imageNose[0]+":"+imageNose[1]+":"+imageNose[2]);

                    Intent intent = new Intent(FindDogActivity.this, FindDogProgressActivity.class);
                    intent.putExtra("input_dog", imageNose);
                    startActivity(intent);
                    finish();
                    break;

                default:
                    break;
            }
        }
    }
    DialogInterface.OnClickListener cameraListener = new DialogInterface.OnClickListener(){

        @Override
        public void onClick(DialogInterface dialog, int which) {
            Intent intentRecord = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
            intentRecord.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 5);
            startActivityForResult(intentRecord, RECORD_CODE);
        }
    };

    DialogInterface.OnClickListener albumListener = new DialogInterface.OnClickListener(){

        @Override
        public void onClick(DialogInterface dialog, int which) {

            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Audio.Media.EXTERNAL_CONTENT_URI);
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
            intent.setType("image/*");
            startActivityForResult(intent, GALLERY_CODE);
        }
    };

    DialogInterface.OnClickListener cancelListener = new DialogInterface.OnClickListener(){

        @Override
        public void onClick(DialogInterface dialog, int which) {
            dialog.dismiss();
        }
    };

    private void bindingView(){

        btnFind = (Button)findViewById(R.id.btn_find);
        btnFind.setOnClickListener(new Button.OnClickListener(){

            @Override
            public void onClick(View v) {
                new android.support.v7.app.AlertDialog.Builder(FindDogActivity.this)
                        .setTitle("Select Upload Image")
                        .setPositiveButton("Take Video", cameraListener)
                        .setNegativeButton("Cancel", cancelListener)
                        .setNeutralButton("Select Album", albumListener)
                        .show();
            }
        });

    }
}
