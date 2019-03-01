package mbreath.com.rtsp_android;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity
        implements SensorEventListener {

    private SensorManager sensorManager;
    private Sensor accelerometer;
    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textView = findViewById(R.id.textView);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        if (sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null) {
            accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        }

    }

    boolean capturingData = true;

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if (capturingData && sensorEvent.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            float x, y, z;
            x = sensorEvent.values[0];
            y = sensorEvent.values[1];
            z = sensorEvent.values[2];

            final String textData = String.format("x=%f y=%f z=%f", x, y, z);

            Log.v("Accelerometer", textData);

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    textView.setText(textData);
                }
            });
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
}
