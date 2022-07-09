package com.example.mooptheorymario;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

public class MainActivity extends AppCompatActivity implements LocationListener {

    double latitude, longitude;
    LocationManager locationManager;
    Button buttonShowLocation, buttonGetLocation, buttonGetFromFile, buttonWriteToFile;
    EditText editTextLatitude, editTextLongitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        buttonShowLocation = findViewById(R.id.buttonShowLocation);
        buttonGetLocation = findViewById(R.id.buttonGetLocation);
        buttonGetFromFile = findViewById(R.id.buttonGetFromFile);
        buttonWriteToFile = findViewById(R.id.buttonWriteToFile);

        editTextLatitude = findViewById(R.id.editTextLatitude);
        editTextLongitude = findViewById(R.id.editTextLongitude);



        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION
            }, 100);
        }

        buttonShowLocation.setOnClickListener(v -> {
            try {
                latitude = Double.parseDouble(editTextLatitude.getText().toString());
                longitude = Double.parseDouble(editTextLongitude.getText().toString());
                Intent intent = new Intent(this, MapsActivity.class);
                intent.putExtra("latitude", latitude);
                intent.putExtra("longitude", longitude);
                startActivity(intent);
            } catch (Exception exception) {
                Toast.makeText(this, "Please Insert Valid Data", Toast.LENGTH_SHORT).show();
            }
        });

        buttonGetLocation.setOnClickListener(v -> getLocation());

        buttonWriteToFile.setOnClickListener(view -> {
            String text = editTextLatitude.getText().toString() + "," + editTextLongitude.getText().toString();
            writeToFile(text, this);
        });

        buttonGetFromFile.setOnClickListener(view -> {
            String text = readFromFile(this);
            String[] data = text.split(",");
            editTextLatitude.setText(data[0]);
            editTextLongitude.setText(data[1]);

        });


    }

    @SuppressLint("MissingPermission")
    public void getLocation() {
        try {
            locationManager = (LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE);
            locationManager.requestLocationUpdates
                    (LocationManager.GPS_PROVIDER, 5000, 5, MainActivity.this);
            Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            editTextLongitude.setText(String.valueOf(location.getLongitude()));
            editTextLatitude.setText(String.valueOf(location.getLatitude()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(@NonNull String provider) {

    }

    @Override
    public void onProviderDisabled(@NonNull String provider) {

    }

    private void writeToFile(String data, Context context) {
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput("config.txt", Context.MODE_PRIVATE));
            outputStreamWriter.write(data);
            outputStreamWriter.close();
        } catch (IOException e) {
            Log.e("Exception", "File write failed: " + e);
        }
    }

    private String readFromFile(Context context) {

        String ret = "";

        try {
            InputStream inputStream = context.openFileInput("config.txt");

            if ( inputStream != null ) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();

                while ( (receiveString = bufferedReader.readLine()) != null ) {
                    stringBuilder.append("\n").append(receiveString);
                }

                inputStream.close();
                ret = stringBuilder.toString();
            }
        }
        catch (FileNotFoundException e) {
            Log.e("login activity", "File not found: " + e);
        } catch (IOException e) {
            Log.e("login activity", "Can not read file: " + e);
        }

        return ret;
    }
}