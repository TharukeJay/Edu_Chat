package com.nishan.hello_nini_chat;

import android.content.Context;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.dexafree.materialList.card.Card;
import com.dexafree.materialList.card.provider.BigImageCardProvider;
import com.dexafree.materialList.view.MaterialListView;
import com.nishan.hello_nini_chat.ChatModule.MessageActivity;


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.lang.Math;
import timber.log.Timber;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;

@EActivity(R.layout.activity_main)
public class MainActivity extends AppCompatActivity {

//    public com.nishan.hello_nini_chat.EmotionPredictionWeka em = new com.nishan.hello_nini_chat.EmotionPredictionWeka();
    public com.nishan.hello_nini_chat.EmotionsList cs = new com.nishan.hello_nini_chat.EmotionsList();
    public String modelName = null;
    public Bundle bundle = new Bundle();
    private static final int RESULT_LOAD_IMG = 1;
    private static final int REQUEST_CODE_PERMISSION = 2;
    private static final String WEKA_TEST = "WekaTest";
    private static final String TAG = "MainActivity";
    private static final String tfModel = "fer2013_mini_XCEPTION.102-0.66.pb";
    private com.nishan.hello_nini_chat.EmotionPredictionTFMobile mClassifier;
    String class_name_new = new String();

    // Permission for storage read write
    private static String[] PERMISSIONS_REQ = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA
    };

    protected String mTestImgPath;

    @ViewById(R.id.material_listview)
    protected MaterialListView mListView;
    @ViewById(R.id.fab)
    protected FloatingActionButton mFabActionBt;
    @ViewById(R.id.fab_cam)
    protected FloatingActionButton mFabCamActionBt;
    @ViewById(R.id.toolbar)
    protected Toolbar mToolbar;
    @ViewById(R.id.Model1)
    protected Button mButton1;
    @ViewById(R.id.Model2)
    protected Button mButton2;
    @ViewById(R.id.Model3)
    protected Button mButton3;
    @ViewById(R.id.TFModel)
    protected Button mButtonTF;

    private TextView mPred;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mListView = findViewById(R.id.material_listview);
        setSupportActionBar(mToolbar);
        modelName = tfModel;
        saveModel();

        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);

        isExternalStorageWritable();
        isExternalStorageReadable();

        int currentapiVersion = android.os.Build.VERSION.SDK_INT;

        if (currentapiVersion >= Build.VERSION_CODES.M) {
            verifyPermissions(this);
        }

        Log.d(WEKA_TEST, "onCreate() finished.");

//        getSelectedMethod();

        launchCameraPreview();

//
    }


    @AfterViews
    protected void setupUI() {
        mToolbar.setTitle(getString(R.string.app_name));
        mToolbar.setTitleTextColor(Color.parseColor("#30DAC5"));
        Toast.makeText(MainActivity.this, getString(R.string.description_info), Toast.LENGTH_LONG).show();
    }

    @Click({R.id.fab})
    protected void launchGallery() {
        Toast.makeText(MainActivity.this, "Pick one image", Toast.LENGTH_SHORT).show();
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, RESULT_LOAD_IMG);
    }
    @Click({R.id.fab_cam})
    protected void launchCameraPreview() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//        File dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
//        File output = new File(dir, "hellonini.jpeg");
//        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(output));
//        intent.putExtras(bundle);
        startActivityForResult(intent, RESULT_LOAD_IMG);
    }

    @Click({R.id.Model1})
    protected  void loadModel1(){
        modelName = "logistic.model";
        saveModel();
        Toast.makeText(MainActivity.this,  "Model loaded: Logistic.", Toast.LENGTH_SHORT).show();
    }
    @Click({R.id.Model2})
    protected void loadModel2(){
        modelName = "sgd.model";
        saveModel();
        Toast.makeText(MainActivity.this,  "Model loaded: Sgd.", Toast.LENGTH_SHORT).show();
    }
    @Click({R.id.Model3})
    protected void loadModel3(){
        modelName = "naivebayes.model";
        saveModel();
        Toast.makeText(MainActivity.this,  "Model loaded: Naive Bayes.", Toast.LENGTH_SHORT).show();
    }
    @Click({R.id.TFModel})
    protected void loadTFModel(){
        modelName = tfModel;
        saveModel();
        Toast.makeText(MainActivity.this, "Model loaded: Cnn.", Toast.LENGTH_SHORT).show();
    }

    public void saveModel(){
        bundle.putString("modelString", modelName);
    }


    private static boolean verifyPermissions(Activity activity) {
        int write_permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int read_persmission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int camera_permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.CAMERA);

        if (write_permission != PackageManager.PERMISSION_GRANTED ||
                read_persmission != PackageManager.PERMISSION_GRANTED ||
                camera_permission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_REQ,
                    REQUEST_CODE_PERMISSION
            );
            return false;
        } else {
            return true;
        }
    }

    private boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    private boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            return true;
        }
        return false;
    }

    protected void demoStaticImage() {
        Timber.tag(WEKA_TEST).d("demoStaticImage() mTestImgPath is null, go to gallery");
        Toast.makeText(MainActivity.this, "Pick an image to run algorithms", Toast.LENGTH_SHORT).show();

//        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//        startActivityForResult(galleryIntent, RESULT_LOAD_IMG);
        Intent galleryIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(galleryIntent, RESULT_LOAD_IMG);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == REQUEST_CODE_PERMISSION) {
            Toast.makeText(MainActivity.this, "Demo using static images", Toast.LENGTH_SHORT).show();
            demoStaticImage();
        }
    }

    //Load image from gallery
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        setContentView(R.layout.static_image_section);
        mPred = findViewById(R.id.predicted_emotion);

        try {
                //set image path
            if (requestCode == RESULT_LOAD_IMG && resultCode == RESULT_OK) {
                Bitmap photo = (Bitmap) data.getExtras().get("data");
//                imageView.setImageBitmap(photo);
//                knop.setVisibility(Button.VISIBLE);


                // CALL THIS METHOD TO GET THE URI FROM THE BITMAP
                Uri tempUri = getImageUri(getApplicationContext(), photo);

                // CALL THIS METHOD TO GET THE ACTUAL PATH
                File finalFile = new File(getRealPathFromURI(tempUri));



            } else {
                Toast.makeText(this, "image not selected", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "error", Toast.LENGTH_LONG).show();
        }

    }
    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    public String getRealPathFromURI(Uri uri) {
        String path = "";
        if (getContentResolver() != null) {
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            if (cursor != null) {
                cursor.moveToFirst();
                int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                path = cursor.getString(idx);
                cursor.close();
            }
        }
        return path;
    }


    private ProgressDialog mDialog;




    private void setText(final TextView text,final String value){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                text.setText(value);
            }
        });
    }

    @UiThread
    protected void addCardView(Card card) {
        mListView.add(card);
    }

    @UiThread
    protected void showDiaglog() {
        mDialog = ProgressDialog.show(MainActivity.this, "Wait", "Emotion detection", true);
    }

    @UiThread
    protected void dismissDialog() {
        if (mDialog != null) {
            mDialog.dismiss();
        }
    }


    private String GetClassName(String cls){
        return  cls;
    }

    protected Bitmap getResizedBitmap(Bitmap bm, int newWidth, int newHeight) {
        Bitmap resizedBitmap = Bitmap.createScaledBitmap(bm, newWidth, newHeight, true);
        return resizedBitmap;
    }

    private void init() {
        try {
            mClassifier = new com.nishan.hello_nini_chat.EmotionPredictionTFMobile(getApplicationContext(), tfModel);
        } catch (RuntimeException e) {
            Log.e(TAG, "Failed to create classifier.", e);
        }
    }

    private void getSelectedMethod() {
        Bundle b = getIntent().getExtras();
        String camera = b.getString("btnCamera");
        String gallery = b.getString("btnGallery");
        if (camera.equals("camera")){
            launchCameraPreview();
        } else if (gallery.equals(null)){
            launchGallery();
        }
    }
}
