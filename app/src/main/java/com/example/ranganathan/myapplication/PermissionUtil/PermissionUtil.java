package com.example.ranganathan.myapplication.PermissionUtil;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;

import com.example.ranganathan.myapplication.Utils;

import java.security.Permissions;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by RanganathanS on 5/20/2017.
 */

public class PermissionUtil {

    static PermissionUtil mInstance;
    public PermissionUtil() {
    }
    public   boolean isPermissionGranted(AppCompatActivity activity,String[] mPermissions){
        for (String permisssion : mPermissions){
            if (ContextCompat.checkSelfPermission(activity, permisssion) != PackageManager.PERMISSION_GRANTED) {
               return false;
            }
        }
        return true;
    }
    public static PermissionUtil getInstance(){
        if (mInstance==null)
            mInstance = new PermissionUtil();
        return mInstance;
    }
    public void requestPermission(AppCompatActivity activity, String[] mPermissions,int CODE){
        List<String> listPermissionsNeeded = new ArrayList<>();
        for (String permisssion : mPermissions){
            int permission_res = ContextCompat.checkSelfPermission(activity, permisssion);

            if (permission_res != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(permisssion);
            }
        }

        //And finally ask for the permission

        if (!listPermissionsNeeded.isEmpty()){
            ActivityCompat.requestPermissions(activity,listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]),CODE);
        }else {
            Utils.makeLog("Permissions : ","No permissions to req");
        }

    }

    public static boolean hasPermissions(Context context, String... permissions) {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

}


