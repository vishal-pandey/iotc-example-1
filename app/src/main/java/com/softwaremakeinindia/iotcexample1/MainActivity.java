package com.softwaremakeinindia.iotcexample1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {

    SharedPreferences sharedPreferences;
    public static final String MyPREFERENCES = "KeyStore" ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button connectButton = findViewById(R.id.connectButton);
        Button scanButton = findViewById(R.id.scanButton);
        final EditText appKey = findViewById(R.id.appKey);

        sharedPreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        String key = sharedPreferences.getString("key", "false");
        if (key != "false"){
            Intent i = new Intent(getApplicationContext(), DeviceControl.class);
            i.putExtra("key", key);
            startActivity(i);
            finish();
        }

        connectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), DeviceControl.class);
                String key = appKey.getText().toString();
                i.putExtra("key", key);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("key", key);
                editor.commit();
                startActivity(i);
                finish();
            }
        });

        scanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), Scanqr.class);
                startActivity(i);
                finish();
            }
        });
    }
}
