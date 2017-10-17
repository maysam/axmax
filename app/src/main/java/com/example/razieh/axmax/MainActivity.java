package com.example.razieh.axmax;

import android.Manifest;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import com.squareup.picasso.Picasso;

import java.io.File;

import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.RuntimePermissions;

@RuntimePermissions
public class MainActivity extends AppCompatActivity {

    private static final int READ_EXTERNAL_STORAGE_PERMISSIONS_REQUEST = 1;

    //    String[] projection = { MediaStore.Images.Media.DATA };
    private static final String[] projection = new String[]{
            MediaStore.Images.ImageColumns._ID,
            MediaStore.Images.ImageColumns.DATA,
            MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME,
            MediaStore.Images.ImageColumns.DATE_TAKEN,
            MediaStore.Images.ImageColumns.MIME_TYPE
    };

    private Cursor cursor = null;
    private String getOriginalImagePath(int i) {
        if (cursor == null) {
            cursor = getContentResolver().query(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    projection, null, null, MediaStore.Images.ImageColumns.DATE_TAKEN + " ASC");
        }
        if (cursor != null) {
            int column_index_data = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToLast();
            while (i-- > 0)
                cursor.moveToPrevious();

            return cursor.getString(column_index_data);
        }
        return null;
    }

    int image_offset = 0;
    ImageView image1 = null;
    ImageView image2 = null;
    TextView text1 = null;
    TextView text2 = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getPermissions();
//        MainActivityPermissionsDispatcher.showCameraWithPermissionCheck(this);

        image1 = (ImageView) findViewById(R.id.image1);
        image2 = (ImageView) findViewById(R.id.image2);

        loadImages(image_offset);
        image1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (image_offset>0) {
                    image_offset--;
                    loadImages(image_offset);
                }
            }
        });
        image2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                image_offset++;
                loadImages(image_offset);
            }
        });
    }

    @NeedsPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
    void loadImages(int image_offset) {
        try {
            String path1 = getOriginalImagePath(image_offset);
            String path2 = getOriginalImagePath(image_offset+1);
            Log.i(getLocalClassName(), path1);
            Log.i(getLocalClassName(), path2);
            File file1 = new File(path1);
            File file2 = new File(path2);
            // wrap File object into a content provider. NOTE: authority here should match authority in manifest declaration
            Uri bmpUri1 = FileProvider.getUriForFile(MainActivity.this, "com.codepath.fileprovider", file1);
            Uri bmpUri2 = FileProvider.getUriForFile(MainActivity.this, "com.codepath.fileprovider", file2);
//            Log.i(getLocalClassName(), path1 + " vs " + file1.toString());
//            TextView tv = new TextView(this);
//            tv.setBackground((Drawable) Drawable.createFromPath(path1));
//            image1.setImageDrawable((Drawable) Drawable.createFromPath(path1));
//            image2.setImageDrawable((Drawable) Drawable.createFromPath(path2));
//            System.gc();

//            image1.setImageURI(bmpUri1);
//            image2.setImageURI(bmpUri2);

            Picasso.with(this).load(bmpUri1).fit().centerCrop().into(image1);
            Picasso.with(this).load(bmpUri2).fit().centerCrop().into(image2);
        } catch (Exception e) {
            Log.e(getLocalClassName(), e.getMessage());
        }catch (Error e) {
            System.gc();
            Log.e(getLocalClassName(), e.getMessage());
        }
    }

    private void getPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            // The permission is NOT already granted.
            // Check if the user has been asked about this permission already and denied
            // it. If so, we want to give more explanation about why the permission is needed.
            if (shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE)) {
                // Show our own UI to explain to the user why we need to read the contacts
                // before actually requesting the permission and showing the default UI
            }
            if (shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                // Show our own UI to explain to the user why we need to read the contacts
                // before actually requesting the permission and showing the default UI
            }

            // Fire off an async request to actually get the permission
            // This will show the standard permission request dialog UI
            String[] permissions = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
            requestPermissions(permissions , READ_EXTERNAL_STORAGE_PERMISSIONS_REQUEST);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        // Make sure it's our original READ_CONTACTS request
        if (requestCode == READ_EXTERNAL_STORAGE_PERMISSIONS_REQUEST) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Read Contacts permission granted", Toast.LENGTH_SHORT).show();
            } else {
                // showRationale = false if user clicks Never Ask Again, otherwise true
                boolean showRationale = shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE);

                if (showRationale) {
                    // do something here to handle degraded mode
                } else {
                    Toast.makeText(this, "Read External Storage permission denied", Toast.LENGTH_SHORT).show();
                }

                showRationale = shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE);

                if (showRationale) {
                    // do something here to handle degraded mode
                } else {
                    Toast.makeText(this, "WRITE External Storage permission denied", Toast.LENGTH_SHORT).show();
                }
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

}
