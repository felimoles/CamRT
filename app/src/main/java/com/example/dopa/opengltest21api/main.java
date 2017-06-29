package com.example.dopa.opengltest21api;

import android.hardware.camera2.CameraAccessException;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.TextureView;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.example.dopa.opengltest21api.MediaScanner;


public class main extends AppCompatActivity  {

    private static final int REQUEST_CAMERA_PERMISSION = 101;
    private CameraRenderer renderer;
    private TextureView textureView;
    private int filterId = R.id.filter0;

    private ImageView mCapture;
    private ImageView menufilter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        setTitle("CamRT");


        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
                Toast.makeText(this, "Camera access is required.", Toast.LENGTH_SHORT).show();

            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA},
                        REQUEST_CAMERA_PERMISSION);
            }

        } else {
            setupCameraPreviewView();
        }



        menufilter = (ImageView) findViewById(R.id.btn_setting);



        menufilter.setOnClickListener(new View.OnClickListener() {




            @Override
            public void onClick(View v) {
                //Creating the instance of PopupMenu
                PopupMenu popup = new PopupMenu(main.this, menufilter);
                //Inflating the Popup using xml file
                popup.getMenuInflater().inflate(R.menu.filter, popup.getMenu());

                //registering popup with OnMenuItemClickListener
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {
                        filterId = item.getItemId();

                        if (renderer != null)
                            renderer.setSelectedFilter(filterId);

                       // Toast.makeText(main.this,"You Clicked : " + item.getTitle(),Toast.LENGTH_SHORT).show();



                        return true;

                    }
                });

                popup.show();//showing popup menu
            }

        });//closing the setOnClickListener method


    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CAMERA_PERMISSION: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    setupCameraPreviewView();
                }
            }
        }
    }

    void setupCameraPreviewView() {
        renderer = new CameraRenderer(this);
        textureView = (TextureView) findViewById(R.id.textureView);
        assert textureView != null;
        textureView.setSurfaceTextureListener(renderer);
        mCapture= (ImageView) findViewById(R.id.mCapturee);
        // Show original frame when touch the view
        mCapture.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:

                        Toast.makeText(main.this,
                                caputre() ? "Foto guardada en /CamRT." :
                                                "Error :-[",
                                Toast.LENGTH_SHORT).show();
                        break;

                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL:
                      //  renderer.setSelectedFilter(filterId);
                        break;
                }
                return true;
            }
        });

        textureView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                renderer.onSurfaceTextureSizeChanged(null, v.getWidth(), v.getHeight());
            }
        });
    }

 /*       @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.filter, menu);
        return true;
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        filterId = item.getItemId();

        // TODO: need tidy up
        if (filterId == R.id.capture) {
            Toast.makeText(this,
                    caputre() ? "The capture has been saved to your sdcard root path." :
                            "Save failed!",
                    Toast.LENGTH_SHORT).show();
            return true;
        }

        setTitle(item.getTitle());

        if (renderer != null)
            renderer.setSelectedFilter(filterId);

        return true;
    }
*/

    private boolean caputre() {



        String mPath = genSaveFileName(getTitle().toString() + "_", ".jpg");
        File filepath = Environment.getExternalStorageDirectory();

        File filedir = new File (filepath.getAbsolutePath()+"/CamRT/");

        if(!filedir.exists()) {

            File dir = new File(filepath.getAbsolutePath() + "/CamRT/");
            dir.mkdirs();
        }


        File imageFile = new File(mPath);
        if (imageFile.exists()) {
            imageFile.delete();
        }
        new MediaScanner(this,imageFile);

        // create bitmap screen capture
        Bitmap bitmap = textureView.getBitmap();
        OutputStream outputStream = null;

        try {
            outputStream = new FileOutputStream(imageFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
            outputStream.flush();
            outputStream.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    private String genSaveFileName(String prefix, String suffix) {
        Date date = new Date();
        SimpleDateFormat dateformat1 = new SimpleDateFormat("yyyyMMdd_hhmmss");
        String timeString = dateformat1.format(date);
        String externalPath = Environment.getExternalStorageDirectory()+"/CamRT/".toString();



        return externalPath + "/" + prefix + timeString + suffix;
    }
}
