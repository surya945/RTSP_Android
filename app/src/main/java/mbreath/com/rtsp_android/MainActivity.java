package mbreath.com.rtsp_android;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
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

    private static final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 1;
    private SensorManager sensorManager;
    private Sensor accelerometer;
    TextView  textViewTimeStamp, textViewX, textViewY, textViewZ;
    EditText editTextFileName;

    List<AcceleroData> acceleroDataList = new ArrayList<>();
    boolean capturingData = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        textView = findViewById(R.id.textView);
        textViewTimeStamp = findViewById(R.id.textViewTimeStamp);
        textViewX = findViewById(R.id.textViewX);
        textViewY = findViewById(R.id.textViewY);
        textViewZ = findViewById(R.id.textViewZ);
        editTextFileName = findViewById(R.id.editTextViewFile);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        if (sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null) {
            accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        }

    }


    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if (capturingData && sensorEvent.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            final float x, y, z;
            x = sensorEvent.values[0];
            y = sensorEvent.values[1];
            z = sensorEvent.values[2];

            final long timeStamp = System.currentTimeMillis();

            acceleroDataList.add(new AcceleroData(timeStamp, x, y, z));
            final String textData = String.format("t=%d x=%f y=%f z=%f", timeStamp, x, y, z);

            Log.v("Accelerometer", textData);

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
//                    textView.setText(textData);
                    textViewTimeStamp.setText(Long.toString(timeStamp));
                    textViewX.setText(Float.toString(x));
                    textViewY.setText(Float.toString(y));
                    textViewZ.setText(Float.toString(z));
                }

            });
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    public void onStartClick(View view) {
        acceleroDataList = new ArrayList<>();
        capturingData = true;

    }

    public void onStopClick(View view) {
        capturingData = false;
    }

    String fileName;

    public void onSaveClick(View view) {
        fileName = editTextFileName.getText().toString();
        if (fileName == null) {
            Toast.makeText(this, "Please enter the file name", Toast.LENGTH_LONG).show();
            return;
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
        } else {
            generateNoteOnSD(this, fileName, GetStringValueFromList(acceleroDataList));
            acceleroDataList = new ArrayList<>();
        }

    }

    public void generateNoteOnSD(Context context, String sFileName, String sBody) {
        try {
            File root = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "AccelerometerData");
            if (!root.exists()) {
                root.mkdirs();
            }
            File gpxfile = new File(root, sFileName + ".csv");
            FileWriter writer = new FileWriter(gpxfile);
            writer.append(sBody);
            writer.flush();
            writer.close();
            Toast.makeText(context, "Saved to " + gpxfile.getAbsolutePath(), Toast.LENGTH_LONG).show();
            Log.e("save to", gpxfile.getAbsolutePath());
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

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        generateNoteOnSD(this, fileName, GetStringValueFromList(acceleroDataList));

        acceleroDataList = new ArrayList<>();
    }


}
