package com.example.yaginuma.photomemo.models;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

/**
 * Created by yaginuma on 15/10/08.
 */
public class ClusterPhoto implements ClusterItem{
    public final String memo;
    public final String imagePath;
    private final LatLng mPosition;

    public ClusterPhoto(LatLng position, String memo, String imagePath) {
        this.memo = memo;
        this.imagePath = imagePath;
        mPosition = position;
    }

    @Override
    public LatLng getPosition() {
        return mPosition;
    }
}
