package com.example.yaginuma.photomemo.models;

import android.media.ExifInterface;
import java.io.IOException;
import io.realm.RealmObject;

/**
 * Created by yaginuma on 15/09/21.
 */
public class Photo extends RealmObject {
    private String memo;
    private String imagePath;
    private String date;
    private float longitude = 0;
    private float latitude = 0;

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public float getLongitude() {
        return longitude;
    }

    public void setLongitude(float longitude) {
        this.longitude = longitude;
    }

    public float getLatitude() {
        return latitude;
    }

    public void setLatitude(float latitude) {
        this.latitude = latitude;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public static class Builder {
        public Photo build(String imagePath, String memo) throws IOException {
            Photo photo = new Photo();
            photo.setImagePath(imagePath);
            photo.setMemo(memo);
            setExifInfo(photo);
            return photo;
        }

        private void setExifInfo(Photo photo) throws IOException {
            ExifInterface exifInterface = new ExifInterface(photo.getImagePath());

            float[] latlong = new float[2];
            exifInterface.getLatLong(latlong);

            if (latlong != null) {
                photo.setLatitude(latlong[0]);
                photo.setLongitude(latlong[1]);
                photo.setDate(exifInterface.getAttribute(ExifInterface.TAG_DATETIME));
            }
        }
    }
}
