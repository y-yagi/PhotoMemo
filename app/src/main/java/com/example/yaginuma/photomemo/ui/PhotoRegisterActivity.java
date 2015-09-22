package com.example.yaginuma.photomemo.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.media.ExifInterface;
import android.support.v7.app.AppCompatActivity;
import android.app.LoaderManager.LoaderCallbacks;

import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;

import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.yaginuma.photomemo.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import models.Photo;
import utils.LogUtil;
import utils.PathUtil;
import utils.RealmBuilder;


/**
 * A login screen that offers login via email/password.
 */
public class PhotoRegisterActivity extends AppCompatActivity {
    private TextView mMemoView;
    private View mProgressView;
    private View mRegisterFormView;
    private Realm mRealm;
    private String mImagePath;

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
                handleSendImage(intent); // Handle single image being sent
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
        showProgress(true);
        String memo = ((TextView)findViewById(R.id.memo)).getText().toString();
        Photo photo;

        try {
            photo = new Photo.Builder().build(mImagePath, memo);
        } catch(IOException exception) {
            showProgress(false);
            Log.d(TAG, exception.getMessage());
            Toast.makeText(this, "画像ファイルが見つかりません", Toast.LENGTH_LONG).show();
            return;
        }

        mRealm.beginTransaction();
        mRealm.copyToRealm(photo);
        mRealm.commitTransaction();
        showProgress(false);
        Toast.makeText(this, "データの登録が完了しました", Toast.LENGTH_LONG).show();
        finish();
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

        mRegisterFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        mRegisterFormView.animate().setDuration(shortAnimTime).alpha(show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mRegisterFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            }
        });

        mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
        mProgressView.animate().setDuration(shortAnimTime).alpha(show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            }
        });
    }

    private void handleSendImage(Intent intent) {
        Uri imageUri = intent.getParcelableExtra(Intent.EXTRA_STREAM);
        ImageView imageView = (ImageView) findViewById(R.id.imageView);
        imageView.setImageURI(imageUri);
        mImagePath = PathUtil.getPath(this,imageUri);
    }
}
