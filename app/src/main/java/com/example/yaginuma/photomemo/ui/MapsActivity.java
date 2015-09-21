package com.example.yaginuma.photomemo.ui;

import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

import com.example.yaginuma.photomemo.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults; import models.Photo; import utils.LogUtil;
import utils.RealmBuilder;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private Realm mRealm;
    private LatLng mStartPosition;

    public static final int DEFAULT_ZOOM = 10;
    private static final String TAG = LogUtil.makeLogTag(MapsActivity.class);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        RealmConfiguration realmConfiguration = RealmBuilder.getRealmConfiguration(this);
        mRealm = RealmBuilder.getRealmInstance(realmConfiguration);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        mStartPosition = new LatLng(35.680795, 139.76721);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        LatLng latLng;
        mMap = googleMap;

        RealmResults<Photo> photos = mRealm.where(Photo.class).findAll();

        for(Photo photo : photos){
            latLng = new LatLng(photo.getLatitude(), photo.getLongitude());
            mMap.addMarker(new MarkerOptions().position(latLng)
                    .title(photo.getMemo())
            );
        }
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mStartPosition, DEFAULT_ZOOM));
    }
}
