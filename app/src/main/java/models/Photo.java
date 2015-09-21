package models;

import android.media.ExifInterface;

import java.io.IOException;
import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by yaginuma on 15/09/21.
 */
public class Photo extends RealmObject {
    private String memo;
    private String imageUri;
    private String date;
    private String longitude;
    private String latitude;

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }

    public String getImageUri() {
        return imageUri;
    }

    public void setImageUri(String imageUri) {
        this.imageUri = imageUri;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public static class Builder {
        private void setExifInfo(Photo photo) throws IOException {
            ExifInterface exifInterface = new ExifInterface(photo.getImageUri());
            photo.setLatitude(exifInterface.getAttribute(ExifInterface.TAG_GPS_LATITUDE));
            photo.setLongitude(exifInterface.getAttribute(ExifInterface.TAG_GPS_LONGITUDE));
            photo.setDate(exifInterface.getAttribute(ExifInterface.TAG_DATETIME));
        }

        public Photo build(String imageUri, String memo) throws IOException {
            Photo photo = new Photo();
            photo.setImageUri(imageUri);
            photo.setMemo(memo);
            setExifInfo(photo);
            return photo;
        }
    }
}
