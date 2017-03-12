package com.wigh.ex10;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;
import android.widget.ToggleButton;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.CoordinateConverter;

public class MainActivity extends AppCompatActivity {
    MapView mapView = null;
    SensorManager sensorManager;
    Sensor magneticSensor;
    Sensor accelerometerSensor;
    LocationManager locationManager;
    ToggleButton toggleButton;
    float curRotationDegree = 0;
    LatLng curLatLng = null;
    Vibrator vibrator = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) throws SecurityException {
        super.onCreate(savedInstanceState);
        SDKInitializer.initialize(getApplicationContext());
        setContentView(R.layout.activity_main);

        mapView = (MapView) findViewById(R.id.bMapView);
        toggleButton = (ToggleButton) findViewById(R.id.tb_center);
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        magneticSensor = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);

        // 箭头
        Bitmap bitmap = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(),
                R.mipmap.pointer), 100, 100, true);
        BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory.fromBitmap(bitmap);

        mapView.getMap().setMyLocationEnabled(true);
        MyLocationConfiguration configuration = new MyLocationConfiguration(
                MyLocationConfiguration.LocationMode.NORMAL, true, bitmapDescriptor);
        mapView.getMap().setMyLocationConfigeration(configuration);

        // 缓存
//        Criteria criteria = new Criteria();
//        criteria.setAccuracy(Criteria.ACCURACY_FINE);
//        criteria.setAltitudeRequired(false);
//        criteria.setBearingRequired(false);
//        criteria.setCostAllowed(true);
//        provider = locationManager.getBestProvider(criteria, true);
//        locationManager.getLastKnownLocation(provider);

        mapView.getMap().setOnMapTouchListener(new BaiduMap.OnMapTouchListener() {
            @Override
            public void onTouch(MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_MOVE:
                        toggleButton.setChecked(false);
                        break;
                    default:
                        break;
                }
            }
        });

        toggleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (toggleButton.isChecked()) {
                    center();
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    protected void onResume() throws SecurityException {
        super.onResume();
        mapView.onResume();
        sensorManager.registerListener(sensorEventListener, magneticSensor,
                SensorManager.SENSOR_DELAY_GAME);
        sensorManager.registerListener(sensorEventListener, accelerometerSensor,
                SensorManager.SENSOR_DELAY_GAME);
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                0, 0, locationListener);
    }

    @Override
    protected void onPause() throws SecurityException {
        super.onPause();
        mapView.onPause();
        sensorManager.unregisterListener(sensorEventListener);
        locationManager.removeUpdates(locationListener);
    }

    //箭头移到中心
    private void center() {
        if (curLatLng == null) return;
        MapStatus mapStatus = new MapStatus.Builder().target(curLatLng).build();
        MapStatusUpdate mapStatusUpdate = MapStatusUpdateFactory.newMapStatus(mapStatus);
        mapView.getMap().setMapStatus(mapStatusUpdate);
    }

    //调整箭头方向
    private void adjust() {
        if (curLatLng == null || mapView == null) return;
        MyLocationData.Builder data = new MyLocationData.Builder();
        data.latitude(curLatLng.latitude);
        data.longitude(curLatLng.longitude);
        data.direction(curRotationDegree);
        mapView.getMap().setMyLocationData(data.build());
    }

    //转为百度坐标
    private LatLng convert(Location srcLoc) {
        if (srcLoc == null) {
            return null;
        }
        CoordinateConverter converter = new CoordinateConverter();
        converter.from(CoordinateConverter.CoordType.GPS);
        converter.coord(new LatLng(srcLoc.getLatitude(), srcLoc.getLongitude()));
        return converter.convert();
    }

    //设置箭头方向
    private void setDegree(float[] accValues, float[] magValues) {
        if (accValues != null && magValues != null) {

            float[] R = new float[9];
            float[] values = new float[3];
            SensorManager.getRotationMatrix(R, null, accValues, magValues);
            SensorManager.getOrientation(R, values);
            float lastDegree = curRotationDegree;
            curRotationDegree = (float) Math.toDegrees(values[0]);
            if (Math.abs(curRotationDegree - lastDegree) > 1)
                adjust();
        }
    }
    private SensorEventListener sensorEventListener = new SensorEventListener() {
        float[] accValues = null;
        float[] magValues = null;
        long lastShakeTime = 0;
        final float threshold = 19;
        final float thresholdEnd = 5;
        final long interval = 3000;
        boolean shaken = false;
        @Override
        public void onSensorChanged(SensorEvent event) {
            switch (event.sensor.getType()) {
                case Sensor.TYPE_ACCELEROMETER:
                    accValues = event.values;
                    setDegree(accValues, magValues);
                    long curTime = System.currentTimeMillis();
                    if (Math.abs(accValues[0]) > threshold
                            || Math.abs(accValues[1]) > threshold
                            || Math.abs(accValues[2]) > threshold) {
                        shaken = true;
                    } else if ((Math.abs(accValues[0]) < thresholdEnd
                            && Math.abs(accValues[1]) < thresholdEnd
                            && Math.abs(accValues[2]) < thresholdEnd)
                            && curTime - lastShakeTime > interval) {
                        if (shaken) {
                            lastShakeTime = curTime;
                            vibrator.vibrate(100);
                            Toast.makeText(MainActivity.this,
                                    "Shake it!!!", Toast.LENGTH_SHORT).show();
                            shaken = false;
                        }

                    }
                    break;
                case Sensor.TYPE_MAGNETIC_FIELD:
                    magValues = event.values;
                    setDegree(accValues, magValues);
                    break;
                default:
                    break;
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    };
    private LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            curLatLng = convert(location);
            if (toggleButton.isChecked()) {
                center();
            }
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    };
}
