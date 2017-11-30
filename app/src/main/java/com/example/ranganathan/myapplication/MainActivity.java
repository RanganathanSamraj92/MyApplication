package com.example.ranganathan.myapplication;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.hardware.camera2.CameraManager;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import static com.example.ranganathan.myapplication.PermissionUtil.PermissionUtil.hasPermissions;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    String[] appPermissions = {Manifest.permission.CAMERA};
    int PERM_CODE = 111;
    String[] PERMISSIONS = {Manifest.permission.READ_CONTACTS,
            Manifest.permission.WRITE_CONTACTS,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
            , Manifest.permission.READ_SMS,
            Manifest.permission.CAMERA};
    Context mCon = this;
    ProgressDialog progressBar;
    private File videoFile;
    private MediaMetadataRetriever retriever;
    private String selectedVideoPath;
    private int SELECT_VIDEO = 444;
    private String path;
    private boolean enable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initCode();
    }

    private void initCode() {
        initTools();
        if(!hasPermissions(this, PERMISSIONS)){
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERM_CODE);
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if ( requestCode == PERM_CODE ){
            if(grantResults.length >0){
                for (int i = 0; i < permissions.length; i++) {


                    if (permissions[i].equals(Manifest.permission.CAMERA)) {
                        if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                            Utils.makeLog("msg", "accounts granted");

                        }
                    } else if (permissions[i].equals(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                        if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                            Utils.makeLog("msg", "storage granted");

                        }
                    }
                   else if (permissions[i].equals(Manifest.permission.READ_EXTERNAL_STORAGE)) {
                        if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                            Utils.makeLog("READ_EXTERNAL_STORAGE", "READ_EXTERNAL_STORAGE granted");

                        }
                    }             }
                for (int res : grantResults){
                    if (res == PackageManager.PERMISSION_GRANTED){
                        //Displaying a toast
                        Utils.makeLog("Permission :","Permission granted now you can access camera");
                    }
                }

            }else{
                //Displaying another toast if permission is not granted
                Utils.makeLog("Permission :","Oops you just denied the permission");
            }
        }
    }

    private void initTools(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(i, SELECT_VIDEO);
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }
    public class DoTask extends AsyncTask<Void,Void,Void>{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            testVideo(videoFile);

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (progressBar!=null && progressBar.isShowing())progressBar.dismiss();
            Toast.makeText(mCon,"Video Frames saved on "+path,Toast.LENGTH_LONG).show();
        }
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void testVideo(File videoFile){

        Uri videoFileUri= Uri.parse(videoFile.toString());

        retriever = new MediaMetadataRetriever();
        retriever.setDataSource(videoFile.getAbsolutePath());
        ArrayList<Bitmap> bitmapFrames=new ArrayList<Bitmap>();


        final long totalDuration = checkVideoDurationValidation(mCon,videoFileUri);


        Log.w("totalDuration "," "+totalDuration);




        for(int f=0;f<totalDuration;f++){
            long timeUS = 1000000 * f ;
            Bitmap bitmap=retriever.getFrameAtTime(timeUS,MediaMetadataRetriever.OPTION_CLOSEST);
            bitmapFrames.add(bitmap);

            try {
                saveFrame(bitmap,f);
            } catch (IOException e) {

                Log.w("IOException ",e.getMessage());
                break;
            }
            catch (NullPointerException e) {
                Log.w("NullPointerException ",e.getMessage());
                break;


            }
        }
        retriever.release();



    }
    public long checkVideoDurationValidation(Context context, Uri uri){


        long duration = 0;

        String time = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
        duration = Long.parseLong( time );
        duration =duration/1000;
        Log.d("CommonHandler", ""+time+" " + duration);

        return duration;
    }

    public void saveFrame(Bitmap b,final int i) throws IOException ,NullPointerException{

        String folder = Environment.getExternalStorageDirectory().toString();
        Log.w("Folder ",folder);
        path = folder + "/Movies/new /";
        File saveFolder = new File(path);
        if (!saveFolder.exists()) {
            saveFolder.mkdirs();
        }


        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        b.compress(Bitmap.CompressFormat.JPEG, 40, bytes);

        File f = new File(saveFolder, ("bitmapframe" + i + ".jpg"));

        f.createNewFile();

        FileOutputStream fo = new FileOutputStream(f);
        fo.write(bytes.toByteArray());

        fo.flush();
        fo.close();

    }

    @ Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == SELECT_VIDEO) {
                selectedVideoPath = getPath(data.getData());
                try {
                    if(selectedVideoPath == null) {
                        Log.e("","selected video path = null!");
                       Toast.makeText(mCon,"selected video path = null!",Toast.LENGTH_SHORT).show();
                    } else {
                        /**
                         * try to do something there
                         * selectedVideoPath is path to the selected video
                         */
                        progressBar = new ProgressDialog(MainActivity.this);
                        progressBar.setMessage("Processing");
                        progressBar.setIndeterminate(true);
                        videoFile=new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/Movies/videorecord.mp4");
                        videoFile=new File(selectedVideoPath);
                        Log.w("Folder ",videoFile.getName());
                        new DoTask().execute();
                    }
                } catch (Exception e) {
                    //#debug
                    e.printStackTrace();
                }
            }
        }
    }
    public  void toggleFlashLight() {
        try {
            enable = !enable;
            CameraManager cameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
            String id = cameraManager.getCameraIdList()[0];
            cameraManager.setTorchMode(id, enable);


        } catch (Exception e2) {
            Toast.makeText(mCon, "Torch Failed: " + e2.getMessage(), Toast.LENGTH_SHORT).show();
        }


    }

    public String getPath(Uri uri) {
        String[] projection = { MediaStore.Images.Media.DATA };
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        if(cursor!=null) {
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        }
        else return null;
    }
}
