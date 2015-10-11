package com.example.yaginuma.photomemo.views;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.yaginuma.photomemo.R;
import com.example.yaginuma.photomemo.models.ClusterPhoto;
import com.example.yaginuma.photomemo.utils.BitmapUtil;
import com.example.yaginuma.photomemo.utils.PathUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults; import com.example.yaginuma.photomemo.models.Photo; import com.example.yaginuma.photomemo.utils.LogUtil;
import com.example.yaginuma.photomemo.utils.RealmBuilder;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;
import com.google.maps.android.ui.IconGenerator;

import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,
        ClusterManager.OnClusterClickListener<ClusterPhoto>, ClusterManager.OnClusterInfoWindowClickListener<ClusterPhoto>,
        ClusterManager.OnClusterItemClickListener<ClusterPhoto>, ClusterManager.OnClusterItemInfoWindowClickListener<ClusterPhoto> {

    private GoogleMap mMap;
    private Realm mRealm;
    private ClusterManager<ClusterPhoto> mClusterManager;

    private static final LatLng DEFAULT_POSITION = new LatLng(35.680795, 139.76721); // FIXME: 現在位置を取得し設定する
    private static final int DEFAULT_ZOOM = 13;
    private static final String TAG = LogUtil.makeLogTag(MapsActivity.class);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        RealmConfiguration realmConfiguration = RealmBuilder.getRealmConfiguration(this);
        mRealm = RealmBuilder.getRealmInstance(realmConfiguration);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        RealmResults<Photo> photos = mRealm.where(Photo.class).findAll();

        mClusterManager = new ClusterManager<ClusterPhoto>(this, mMap);
        mClusterManager.setRenderer(new PhotoRenderer());
        mClusterManager.setOnClusterClickListener(this);
        mClusterManager.setOnClusterInfoWindowClickListener(this);
        mClusterManager.setOnClusterItemClickListener(this);
        mClusterManager.setOnClusterItemInfoWindowClickListener(this);
        mMap.setOnCameraChangeListener(mClusterManager);
        mMap.setOnMarkerClickListener(mClusterManager);
        mMap.setOnInfoWindowClickListener(mClusterManager);

        for(Photo photo : photos){
            mClusterManager.addItem(new ClusterPhoto(
                new LatLng(photo.getLatitude(), photo.getLongitude()), photo.getMemo(), photo.getImagePath()
            ));
        }
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(DEFAULT_POSITION, DEFAULT_ZOOM));
        mClusterManager.cluster();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mRealm.close();
    }

    private class PhotoRenderer extends DefaultClusterRenderer<ClusterPhoto> {
        private final IconGenerator mIconGenerator = new IconGenerator(getApplicationContext());
        private final IconGenerator mClusterIconGenerator = new IconGenerator(getApplicationContext());
        private final ImageView mImageView;
        private final ImageView mClusterImageView;
        private final int mDimension;

        public PhotoRenderer() {
            super(getApplicationContext(), mMap, mClusterManager);
            View multiProfile = getLayoutInflater().inflate(R.layout.multi_photo, null);
            mClusterIconGenerator.setContentView(multiProfile);
            mClusterImageView = (ImageView) multiProfile.findViewById(R.id.image);

            mImageView = new ImageView(getApplicationContext());
            mDimension = (int) getResources().getDimension(R.dimen.custom_photo_image);
            mImageView.setLayoutParams(new ViewGroup.LayoutParams(mDimension, mDimension));
            int padding = (int) getResources().getDimension(R.dimen.custom_photo_padding);
            mImageView.setPadding(padding, padding, padding, padding);
            mIconGenerator.setContentView(mImageView);
        }

        @Override
        protected void onBeforeClusterItemRendered(ClusterPhoto photo, MarkerOptions markerOptions) {
            Bitmap bitmap = BitmapUtil.decodeSampledBitmapFromFile(photo.imagePath, mDimension, mDimension);
            mImageView.setImageBitmap(bitmap);

            Bitmap icon = mIconGenerator.makeIcon();
            markerOptions.icon(BitmapDescriptorFactory.fromBitmap(icon)).title(photo.memo);
        }

        @Override
        protected void onBeforeClusterRendered(Cluster<ClusterPhoto> cluster, MarkerOptions markerOptions) {
            List<Drawable> photos = new ArrayList<Drawable>(Math.min(4, cluster.getSize()));
            int width = mDimension;
            int height = mDimension;

            for (ClusterPhoto p : cluster.getItems()) {
                if (photos.size() == 4) break;
                Drawable drawable = Drawable.createFromPath(p.imagePath);
                drawable.setBounds(0, 0, width, height);
                photos.add(drawable);
            }
            MultiDrawable multiDrawable = new MultiDrawable(photos);
            multiDrawable.setBounds(0, 0, width, height);

            mClusterImageView.setImageDrawable(multiDrawable);
            Bitmap icon = mClusterIconGenerator.makeIcon(String.valueOf(cluster.getSize()));
            markerOptions.icon(BitmapDescriptorFactory.fromBitmap(icon));
        }

        @Override
        protected boolean shouldRenderAsCluster(Cluster cluster) {
            return cluster.getSize() > 1;
        }
    }

    @Override
    public boolean onClusterClick(Cluster<ClusterPhoto> cluster) {
        return true;
    }

    @Override
    public void onClusterInfoWindowClick(Cluster<ClusterPhoto> cluster) {
    }

    @Override
    public boolean onClusterItemClick(ClusterPhoto item) {
        return false;
    }

    @Override
    public void onClusterItemInfoWindowClick(ClusterPhoto item) {
    }
}
