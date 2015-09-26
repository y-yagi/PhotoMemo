package com.example.yaginuma.photomemo.views;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import com.example.yaginuma.photomemo.R;
import com.example.yaginuma.photomemo.adapters.PhotoAdapter;
import com.example.yaginuma.photomemo.models.Photo;
import com.example.yaginuma.photomemo.utils.RealmBuilder;

import io.realm.Realm;
import io.realm.RealmConfiguration;

public class PhotoListActivity extends AppCompatActivity {
    private Realm mRealm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_list);

        GridView gridView = (GridView)findViewById(R.id.photos);
        gridView.setAdapter(new PhotoAdapter(this));

        RealmConfiguration realmConfiguration = RealmBuilder.getRealmConfiguration(this);
        mRealm = RealmBuilder.getRealmInstance(realmConfiguration);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                displayConfirmDialog(parent, v, position, id);
            }
        });
    }
    private void displayConfirmDialog(final AdapterView<?> parent, View v, final int position, long id) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.destroy_confirm_msg);
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                PhotoAdapter adaper = (PhotoAdapter)parent.getAdapter();
                Photo photo = (Photo) adaper.getItem(position);

                mRealm.beginTransaction();
                photo.removeFromRealm();
                mRealm.commitTransaction();

                adaper.notifyDataSetChanged();

            }
        });

        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // DO nothing
            }
        });
        builder.show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mRealm.close();
    }
}
