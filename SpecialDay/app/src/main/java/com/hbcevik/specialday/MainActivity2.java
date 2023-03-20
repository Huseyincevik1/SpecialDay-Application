package com.hbcevik.specialday;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.hbcevik.specialday.databinding.ActivityMain2Binding;
import com.hbcevik.specialday.databinding.ActivityMainBinding;

import java.io.ByteArrayOutputStream;

public class MainActivity2 extends AppCompatActivity {

    private ActivityMain2Binding binding;
    ActivityResultLauncher<Intent>activityResultLauncher;
    ActivityResultLauncher<String>permissionLauncher;
    Bitmap selectedImage;

    SQLiteDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMain2Binding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        registerLauncher();

        database = this.openOrCreateDatabase("Events",MODE_PRIVATE,null);

        Intent intent = getIntent();
        String info = intent.getStringExtra("info");

        if(info.equals("new")){
            binding.eventText.setText("");
            binding.nameText.setText("");
            binding.timeText.setText("");
            binding.button.setVisibility(View.VISIBLE);
            binding.imageView.setImageResource(R.drawable.selectimage);
        } else{
            int dayId = intent.getIntExtra("dayId",1);
            binding.button.setVisibility(View.INVISIBLE);

            try {
                Cursor cursor = database.rawQuery("SELECT * FROM events Where id = ?", new String[]{String.valueOf(dayId)});
                int eventIx = cursor.getColumnIndex("eventname");
                int nameIx = cursor.getColumnIndex("names");
                int timeIx = cursor.getColumnIndex("time");
                int imageIx = cursor.getColumnIndex("image");

                while(cursor.moveToNext()){
                  binding.eventText.setText(cursor.getString(eventIx));
                  binding.nameText.setText(cursor.getString(nameIx));
                  binding.timeText.setText(cursor.getString(timeIx));

                  byte[] bytes = cursor.getBlob(imageIx);
                  Bitmap bitmap = BitmapFactory.decodeByteArray(bytes,0,bytes.length);
                  binding.imageView.setImageBitmap(bitmap);
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    public void save(View view) {
        String event = binding.eventText.getText().toString();
        String names = binding.nameText.getText().toString();
        String time = binding.timeText.getText().toString();

        Bitmap smallimage = smallimage(selectedImage,300);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        smallimage.compress(Bitmap.CompressFormat.PNG,50,outputStream);
        byte[]Array =outputStream.toByteArray();

        try {

            database.execSQL("CREATE TABLE IF NOT EXISTS events (id INTEGER PRIMARY KEY, eventname VARCHAR, names VARCHAR, time VARCHAR, image BLOB)");
            String sql = "INSERT INTO events (eventname, names, time, image) VALUES(?, ?, ?, ?)";
            SQLiteStatement sqLiteStatement = database.compileStatement(sql);
            sqLiteStatement.bindString(1,event);
            sqLiteStatement.bindString(2,names);
            sqLiteStatement.bindString(3,time);
            sqLiteStatement.bindBlob(4,Array);
            sqLiteStatement.execute();
        }catch (Exception e){
            e.printStackTrace();
        }
        Intent intent = new Intent(MainActivity2.this,MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);

    }

    public Bitmap smallimage(Bitmap image,int maxsize){
        int width = image.getWidth();
        int height = image.getHeight();
        float ratio =(float) (width/height);
        if(ratio>1){
            //yatay resim
            width = maxsize;
            height =(int) (width / ratio);
        }else{
            //dikey resim
            height = maxsize;
            width = (int) (height * ratio);
        }
        return image.createScaledBitmap(image,width,height,true);
    }


    public void select(View view) {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
            if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES)!= PackageManager.PERMISSION_GRANTED){
                if(ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.READ_MEDIA_IMAGES)){
                    Snackbar.make(view,"Galerideki fotoğraflar için izin gerekli",Snackbar.LENGTH_INDEFINITE).setAction("İzin ver", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            permissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES);
                        }
                    }).show();
                }else {
                    permissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES);
                }
            }else{
                Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                activityResultLauncher.launch(gallery);
            }
        }else{
            if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
                if(ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.READ_EXTERNAL_STORAGE)){
                    Snackbar.make(view,"Galerideki fotoğraflar için izin gerekli",Snackbar.LENGTH_INDEFINITE).setAction("İzin ver", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
                        }
                    }).show();
                }else {
                    permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
                }
            }else{
                Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                activityResultLauncher.launch(gallery);
            }
        }

    }

    private  void registerLauncher(){
        activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                if(result.getResultCode()== RESULT_OK){
                  Intent intentfromresult = result.getData();
                  if(intentfromresult != null){
                     Uri imagedata = intentfromresult.getData();
                    // binding.imageView.setImageURI(imagedata);

                     try {
                         if(Build.VERSION.SDK_INT >=28){
                             ImageDecoder.Source source = ImageDecoder.createSource(getContentResolver(),imagedata);
                             selectedImage = ImageDecoder.decodeBitmap(source);
                             binding.imageView.setImageBitmap(selectedImage);
                         }else{
                             selectedImage = MediaStore.Images.Media.getBitmap(MainActivity2.this.getContentResolver(),imagedata);
                         }


                     }catch (Exception e){
                         e.printStackTrace();
                     }
                  }
                }
            }
        });
        permissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), new ActivityResultCallback<Boolean>() {
            @Override
            public void onActivityResult(Boolean result) {
              if(result){
                  Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                  activityResultLauncher.launch(gallery);
              }else{
                  Toast.makeText(MainActivity2.this,"İzin gerekli",Toast.LENGTH_LONG).show();
              }
            }
        });
    }
}