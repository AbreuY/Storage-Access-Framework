package com.example.storage;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.OpenableColumns;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {


    Button allFile;
    Button images;
    TextView fileName;
    ImageView imageView;
    private String TAG = "06470647";
    private static final int RC_OPEN_DOCUMENT = 2;
    private static final String LAST_RETURNED_DOCUMENT_URI = "LAST_RETURNED_DOCUMENT_URI";
    private static final String LAST_RETURNED_DOCUMENT_TREE_URI = "LAST_RETURNED_DOCUMENT_TREE_URI";
    private Uri mLastReturnedDocumentUri;
    private Uri mLastReturnedDocumentTreeUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        isStoragePermissionGranted();

        allFile = findViewById(R.id.allFile);
        images = findViewById(R.id.images);
        fileName = findViewById(R.id.fileName);
        imageView = findViewById(R.id.imageView);

        allFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("*/*");
                startActivityForResult(intent, RC_OPEN_DOCUMENT);
            }
        });

        images.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("image/*");
                startActivityForResult(intent, RC_OPEN_DOCUMENT);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != Activity.RESULT_OK || data == null || data.getData() == null) {
            fileName.setText("");
            imageView.setImageURI(null);
            return;
        }
        if (requestCode == RC_OPEN_DOCUMENT) {
            handleOpenDocument(data.getData());
        }else {
            Log.i(TAG,"NO DATA");
        }

    }

    private void handleOpenDocument(Uri documentUri) {

        Cursor cursor = getContentResolver().query(documentUri, null, null, null, null);
        try {
            // moveToFirst() returns false if the cursor has 0 rows.  Very handy for
            // "if there's anything to look at, look at it" conditionals.
            if (cursor != null && cursor.moveToFirst()) {
                // Note it's called "Display Name".  This is
                // provider-specific, and might not necessarily be the file name.
                String displayName = cursor.getString(
                        cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                fileName.setText(displayName);
                imageView.setImageURI(documentUri);
                imageView.setContentDescription(displayName);
                mLastReturnedDocumentUri = documentUri;
                mLastReturnedDocumentTreeUri = null;
            } else {
                fileName.setText("");
                imageView.setImageURI(null);
                imageView.setContentDescription("");
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
    }


    public boolean isStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.v(TAG, "Permission is granted");
                return true;
            } else {

                Log.v(TAG, "Permission is revoked");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                return false;
            }
        } else { //permission is automatically granted on sdk<23 upon installation
            return true;
        }
    }


}
