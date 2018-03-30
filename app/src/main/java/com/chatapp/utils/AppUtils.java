package com.chatapp.utils;

import android.content.Context;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.support.v4.app.ActivityCompat;
import android.util.Patterns;

/**
 * Created by Usman on 3/28/18.
 */

public class AppUtils {

    public static boolean isEmailAndPasswordValid(String email,String password){

        if(email.isEmpty()||password.isEmpty()||!Patterns.EMAIL_ADDRESS.matcher(email).matches())
            return false;

        return true;
    }

    public static boolean isNetworkAvailable(Context context) {
        final ConnectivityManager connectivityManager = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE));
        return connectivityManager.getActiveNetworkInfo() != null && connectivityManager.getActiveNetworkInfo().isConnected();
    }

    public static boolean hasPermissions(Context context, String... permissions) {
        if (context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }
}
