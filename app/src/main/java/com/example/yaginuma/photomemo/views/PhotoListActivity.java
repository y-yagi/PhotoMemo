package com.example.yaginuma.photomemo.views;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.GridView;

import com.example.yaginuma.photomemo.R;
import com.example.yaginuma.photomemo.adapters.PhotoAdapter;

public class PhotoListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_list);
        GridView gridView = (GridView)findViewById(R.id.photos);
        gridView.setAdapter(new PhotoAdapter(this));
    }
}
