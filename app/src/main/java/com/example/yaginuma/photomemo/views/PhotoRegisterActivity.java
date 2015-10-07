package com.example.yaginuma.photomemo.views;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.support.v7.app.AppCompatActivity;
import android.net.Uri;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
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
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;


/**
 * A login screen that offers login via email/password.
 */
public class PhotoRegisterActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private Realm mRealm;
    private String mImagePath;
    private Photo mPhoto;
    private GoogleApiClient mGoogleApiClient;

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

        Button mRegisterButton = (Button) findViewById(R.id.register_button);
        mRegisterButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptRegister();
            }
        });
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
        completeRegister();
    }

    private void displayConfirmDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.register_confirm_msg);
        builder.setPositiveButton(R.string.set_current_pos_msg, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                setCurrentPosAndRegister();
            }
        });

        builder.setNeutralButton(R.string.no_set_pos_msg, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                completeRegister();
            }
        });
        builder.show();
    }

    private void completeRegister() {
        mRealm.beginTransaction();
        mRealm.copyToRealm(mPhoto);
        mRealm.commitTransaction();
        mRealm.close();
        Toast.makeText(this, "データの登録が完了しました", Toast.LENGTH_LONG).show();
        finish();
    }

    private void setCurrentPosAndRegister() {
        buildGoogleApiClient();
        connectGoogleApiClient();
    }

    private void handleSendImage(Intent intent) {
        Uri imageUri = intent.getParcelableExtra(Intent.EXTRA_STREAM);
        ImageView imageView = (ImageView) findViewById(R.id.imageView);
        imageView.setImageURI(imageUri);
        mImagePath = PathUtil.getPath(this,imageUri);
    }

    protected synchronized void buildGoogleApiClient() {
        if (mGoogleApiClient != null) return;

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    private void connectGoogleApiClient() {
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

        if (location != null) {
            mPhoto.setLatitude((float)location.getLatitude());
            mPhoto.setLongitude((float)location.getLongitude());
            completeRegister();
        } else {
            Toast.makeText(this, R.string.cannot_get_current_post, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void  onConnectionSuspended (int cause) {
        Toast.makeText(this, R.string.unexpected_error_msg, Toast.LENGTH_LONG).show();
        Log.e(TAG, "onConnectionSuspended cause: " + cause);
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Toast.makeText(this, R.string.unexpected_error_msg, Toast.LENGTH_LONG).show();
        Log.e(TAG, "onConnectionFailed cause: " + connectionResult.toString());
    }
}
