package com.example.yaginuma.photomemo.ui;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.example.yaginuma.photomemo.R;
import java.io.IOException;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import com.example.yaginuma.photomemo.models.Photo;
import com.example.yaginuma.photomemo.utils.LogUtil;
import com.example.yaginuma.photomemo.utils.PathUtil;
import com.example.yaginuma.photomemo.utils.RealmBuilder;


/**
 * A login screen that offers login via email/password.
 */
public class PhotoRegisterActivity extends AppCompatActivity {
    private TextView mMemoView;
    private View mProgressView;
    private View mRegisterFormView;
    private Realm mRealm;
    private String mImagePath;
    private Photo mPhoto;

    private static final String TAG = LogUtil.makeLogTag(PhotoRegisterActivity.class);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        RealmConfiguration realmConfiguration = RealmBuilder.getRealmConfiguration(this);
        mRealm = RealmBuilder.getRealmInstance(realmConfiguration);

        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();

        if (Intent.ACTION_SEND.equals(action) && type != null) {
            if (type.startsWith("image/")) {
                handleSendImage(intent);
            }
        }

        mMemoView = (TextView)findViewById(R.id.memo);
        mRegisterFormView = (View)findViewById(R.id.register_form);
        Button mRegisterButton = (Button) findViewById(R.id.register_button);
        mRegisterButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptRegister();
            }
        });

        mProgressView = findViewById(R.id.login_progress);
    }

    private void attemptRegister() {
        String memo = ((TextView)findViewById(R.id.memo)).getText().toString();

        try {
            mPhoto = new Photo.Builder().build(mImagePath, memo);
        } catch(IOException exception) {
            Log.d(TAG, exception.getMessage());
            Toast.makeText(this, "画像ファイルが見つかりません", Toast.LENGTH_LONG).show();
            return;
        }

        if (mPhoto.getLatitude() == 0 && mPhoto.getLongitude() == 0) {
            displayConfirmDialog();
            return;
        }

        // DEBUG
        Toast.makeText(this, "latitude:" + mPhoto.getLatitude() + " longitude: " + mPhoto.getLongitude(), Toast.LENGTH_LONG).show();
        completeRegister();
    }

    private void displayConfirmDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.register_confirm_msg);
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                completeRegister();
            }
        });

        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // DO nothing
            }
        });
    }

    private void completeRegister() {
        mRealm.beginTransaction();
        mRealm.copyToRealm(mPhoto);
        mRealm.commitTransaction();
        Toast.makeText(this, "データの登録が完了しました", Toast.LENGTH_LONG).show();
        finish();
    }

    private void handleSendImage(Intent intent) {
        Uri imageUri = intent.getParcelableExtra(Intent.EXTRA_STREAM);
        ImageView imageView = (ImageView) findViewById(R.id.imageView);
        imageView.setImageURI(imageUri);
        mImagePath = PathUtil.getPath(this,imageUri);
    }
}
