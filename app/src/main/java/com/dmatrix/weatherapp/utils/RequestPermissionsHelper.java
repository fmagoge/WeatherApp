package com.dmatrix.weatherapp.utils;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class RequestPermissionsHelper {

    private static final int CODE_REQUEST_LOCATION = 0;

    public static boolean verifyPermissions(Context context) {
        int permissionFineLocationCheck = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION);
        int permissionCourseLocationCheck = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION);
        int permissionWifiCheck = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_WIFI_STATE);

        if (permissionFineLocationCheck == PackageManager.PERMISSION_DENIED &&
                permissionCourseLocationCheck == PackageManager.PERMISSION_DENIED &&
                permissionWifiCheck == PackageManager.PERMISSION_DENIED ) {
            return false;
        } else return true;
    }

    public static void requestPermissions(AppCompatActivity context) {
        ActivityCompat.requestPermissions(context, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_WIFI_STATE }, CODE_REQUEST_LOCATION);
    }
}
