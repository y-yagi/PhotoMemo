package com.example.yaginuma.photomemo.ui;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import io.realm.RealmResults; import com.example.yaginuma.photomemo.models.Photo; import com.example.yaginuma.photomemo.utils.LogUtil;
import com.example.yaginuma.photomemo.utils.RealmBuilder;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private Realm mRealm;

    private static final LatLng DEFAULT_POSITION = new LatLng(35.680795, 139.76721);
    private static final int DEFAULT_ZOOM = 10;
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
            Bitmap image = BitmapFactory.decodeFile(photo.getImagePath());
            Bitmap resized = Bitmap.createScaledBitmap(image, 128, 128, true);
            mMap.addMarker(new MarkerOptions().position(latLng)
                            .title(photo.getMemo())
                            .icon(BitmapDescriptorFactory.fromBitmap(resized))
            );
        }
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(DEFAULT_POSITION, DEFAULT_ZOOM));
    }
}
