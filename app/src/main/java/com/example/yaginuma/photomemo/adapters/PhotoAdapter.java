package com.example.yaginuma.photomemo.adapters;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.yaginuma.photomemo.R;
import com.example.yaginuma.photomemo.models.Photo;
import com.example.yaginuma.photomemo.utils.RealmBuilder;

import java.io.File;
import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;

/**
 * Created by yaginuma on 15/09/25.
 */
public class PhotoAdapter extends BaseAdapter {
    private LayoutInflater mLayoutInflater;
    private static class ViewHolder {
        public ImageView imageView;
        public TextView textView;
    }
    private RealmResults<Photo> mPhotos;
    private RealmConfiguration mRealmConfiguration;
    private Realm mRealm;

    public PhotoAdapter(Context context) {
        mLayoutInflater = LayoutInflater.from(context);
        mRealmConfiguration = RealmBuilder.getRealmConfiguration(context);
        mRealm = RealmBuilder.getRealmInstance(mRealmConfiguration);
        mPhotos = mRealm.where(Photo.class).findAll();
    }

    public int getCount() {
        return mPhotos.size();
    }

    public Object getItem(int position) {
        return mPhotos.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    public void removeItem(int position) {
        mRealm.beginTransaction();
        ((Photo)getItem(position)).removeFromRealm();
        mRealm.commitTransaction();
    }

    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder;
        if (convertView == null) {
            convertView = mLayoutInflater.inflate(R.layout.grid_item_photo, null);
            holder = new ViewHolder();
            holder.imageView = (ImageView)convertView.findViewById(R.id.image);
            holder.textView = (TextView)convertView.findViewById(R.id.memo);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder)convertView.getTag();
        }

        Photo photo = mPhotos.get(position);
        holder.imageView.setImageURI(Uri.fromFile(new File(photo.getImagePath())));
        holder.textView.setText(photo.getMemo());

        return convertView;
    }
}
