package com.yey.kindergaten.activity;

import android.content.Context;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.widget.Toast;


import com.yey.kindergaten.BaseActivity;
import com.yey.kindergaten.R;

import java.io.IOException;
import java.util.Date;

/**
 * Created by Administrator on 2015/2/28.
 */
public class GPRSShakeActivity extends BaseActivity implements SensorEventListener {

    SensorManager sensorManager;
    LocationManager locationManager;
    Criteria criteria=new Criteria();
    float acc_x=0;
    float acc_y=0;
    float acc_z=0;
    Date olddata;
    Date nowdata;
    MediaPlayer mediaPlayer=new MediaPlayer();
    private static final int SPEED_SHRESHOLD = 4500;//这个值越大需要越大的力气来摇晃手机
    private static final int UPTATE_INTERVAL_TIME = 50;
    private float lastX;
    private float lastY;
    private float lastZ;
    private long lastUpdateTime;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.shake_gprs);
         locationManager= (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (locationManager.isProviderEnabled(android.location.LocationManager.GPS_PROVIDER)) {
        }else{
            Intent callGPSSettingIntent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(callGPSSettingIntent);
        }

        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        criteria.setAltitudeRequired(false);
        criteria.setBearingRequired(false);
        criteria.setCostAllowed(false);
        criteria.setPowerRequirement(Criteria.POWER_MEDIUM);

        sensorManager= (SensorManager) getSystemService(SENSOR_SERVICE);
        sensorManager.registerListener(this,sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),SensorManager.SENSOR_DELAY_FASTEST);
    }

    public void playMediaMusic(String thepath)
    {
        try {
            AssetManager am = getAssets();
            AssetFileDescriptor afd = am.openFd(thepath);
            mediaPlayer.reset();
            mediaPlayer.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
            mediaPlayer.prepare();
            mediaPlayer.start();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        switch (sensorEvent.sensor.getType()){
            case Sensor.TYPE_ACCELEROMETER:
                String provide=locationManager.getBestProvider(criteria,true);
                Location location=locationManager.getLastKnownLocation(provide);
                Double latitude=0.0;
                Double longitude=0.0;
                if(location!=null){
                    //经度
                     latitude=location.getLatitude()*1E6;
                    //维度
                     longitude=location.getLongitude()*1E6;
                }

                long currentUpdateTime = System.currentTimeMillis();
                long timeInterval = currentUpdateTime - lastUpdateTime;
                if (timeInterval < UPTATE_INTERVAL_TIME)
                    return;
                lastUpdateTime = currentUpdateTime;
                float x = sensorEvent.values[0];
                float y = sensorEvent.values[1];
                float z = sensorEvent.values[2];
                float deltaX = x - lastX;
                float deltaY = y - lastY;
                float deltaZ = z - lastZ;
                lastX = x;
                lastY = y;
                lastZ = z;
                double speed = Math.sqrt(deltaX * deltaX + deltaY * deltaY + deltaZ
                        * deltaZ)
                        / timeInterval * 10000;
                if (speed >= SPEED_SHRESHOLD) {
                    playMediaMusic("shake_sound_male.mp3");
                    Toast.makeText(this,""+latitude+"  "+longitude,Toast.LENGTH_SHORT).show();
                }

                /*
               float x=sensorEvent.values[0];
               float y=sensorEvent.values[1];
               float z=sensorEvent.values[2];
                if(x>acc_x||y>acc_y||z>acc_z) {
                    acc_x =Math.abs(x);
                    acc_y =Math.abs(y);;
                    acc_z =Math.abs(z);;
                }
                if(acc_x>19.0||acc_y>19.0||acc_z>19.0){
                    acc_x=0;
                    acc_y=0;
                    acc_z=0;
                    if(olddata==null){
                        olddata=new Date();
                        playMediaMusic("shake_sound_male.mp3");
                        Toast.makeText(GPRSShakeActivity.this,"x:"+x+"y:"+y +"z:"+z,Toast.LENGTH_SHORT).show();
                    }else{
                        nowdata=new Date();
                        if((nowdata.getTime()-olddata.getTime())>2000){
                            olddata=nowdata;
                            playMediaMusic("shake_sound_male.mp3");
                            Toast.makeText(GPRSShakeActivity.this,"x:"+x+"y:"+y +"z:"+z,Toast.LENGTH_SHORT).show();
                        }
                    }
                }
                */
                break;
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
         sensorManager.unregisterListener(this);
    }
}
