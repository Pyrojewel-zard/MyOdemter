package com.pyrojewel.myodemter;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;


public class OdometerService extends Service {
    private final IBinder binder = new OdometerBinder();
    private LocationListener listener;
    private LocationManager locManager;
    private static double distanceInMeters;
    private static Location lastLocation = null;
    public static final String PERMISSION_STRING
            = android.Manifest.permission.ACCESS_FINE_LOCATION;

    public class OdometerBinder extends Binder {
        OdometerService getOdometer() {
            return OdometerService.this;
        }
    }
    @Override
    public void onCreate(){
        super.onCreate();
        listener=new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {
                if(lastLocation==null)//说明初始化
                {
                    lastLocation=location;
                }
                distanceInMeters+=location.distanceTo(lastLocation);
                lastLocation=location;
            }//即完全可以叠加并把所有变量都保存
            @Override
            public void onProviderDisabled(String arg0) {
            }

            @Override
            public void onProviderEnabled(String arg0) {
            }

            @Override
            public void onStatusChanged(String arg0, int arg1, Bundle bundle) {
            }
        };
        locManager=(LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if(ContextCompat.checkSelfPermission(this,PERMISSION_STRING)==PackageManager.PERMISSION_GRANTED){
            String provider=locManager.getBestProvider(new Criteria(),true);
            if(provider!=null){
                locManager.requestLocationUpdates(provider,1000,1,listener);
            }
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }


    public double getDistance() {
        return this.distanceInMeters / 1609.344+1;
    }

}
