package mbreath.com.rtsp_android;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements SensorEventListener {

    private SensorManager sensorManager;
    private Sensor accelerometer;
    TextView textView;
    EditText editText;

    List<AcceleroData> acceleroDataList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textView = findViewById(R.id.textView);
        editText = findViewById(R.id.editTextViewFile);

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

            long timeStamp = System.currentTimeMillis();

            acceleroDataList.add(new AcceleroData(timeStamp, x, y, z));
            final String textData = String.format("t=%d x=%f y=%f z=%f", timeStamp, x, y, z);

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

    public void onStartClick(View view) {
        capturingData = true;
    }

    public void onStopClick(View view) {
        capturingData = false;
    }

    public void onSaveClick(View view) {
        String fileName = editText.getText().toString();
        if (fileName == null) {
            Toast.makeText(this, "Please enter the file name", Toast.LENGTH_LONG).show();
            return;
        }

        generateNoteOnSD(this, fileName, GetStringValueFromList(acceleroDataList));

        acceleroDataList = new ArrayList<>();

    }

    public void generateNoteOnSD(Context context, String sFileName, String sBody) {
        try {
            File root = new File(Environment.getExternalStorageDirectory(), "AccelerometerData");
            if (!root.exists()) {
                root.mkdirs();
            }
            File gpxfile = new File(root, sFileName);
            FileWriter writer = new FileWriter(gpxfile);
            writer.append(sBody);
            writer.flush();
            writer.close();
            Toast.makeText(context, "Saved", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String GetStringValueFromList(List<AcceleroData> data) {
        String s = "t X Y Z  %data formate";
        for (AcceleroData accelerationData : data) {
            s += accelerationData.toString() + "\n";
        }
        return s;
    }
}
