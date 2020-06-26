package com.softwaremakeinindia.iotcexample1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.softwaremakeinindia.iotc.Iotc;

import java.util.ArrayList;
import java.util.List;

public class DeviceControl extends AppCompatActivity implements AdapterView.OnItemSelectedListener {


    SharedPreferences sharedPreferences;
    public static final String MyPREFERENCES = "KeyStore" ;

    ArrayList myList = new ArrayList();

    String selectedDevice = "";
    final List <String> avalilableDevices = new ArrayList<String>();

    static Spinner spinner;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.device_control);
        setTitle("Connecting ... ");

        Intent intent = getIntent();
        String key = intent.getStringExtra("key");

        final ArrayAdapter adapter = new ArrayAdapter(this, R.layout.device_listview, myList);
        ListView listView = findViewById(R.id.message_list);
        listView.setAdapter(adapter);

        final EditText eText = findViewById(R.id.edittext);
        Button btn = findViewById(R.id.button);
        Button clearBtn = findViewById(R.id.clearBtn);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String msg = eText.getText().toString();
                Iotc.send(selectedDevice,msg);
            }
        });

        clearBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myList.clear();
                adapter.notifyDataSetChanged();
            }
        });


        spinner = (Spinner) findViewById(R.id.spinner);
        spinner.setPrompt("Device List");

        spinner.setOnItemSelectedListener(this);




        final ArrayAdapter<String> deviceAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, avalilableDevices);
        deviceAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        avalilableDevices.clear();
        deviceAdapter.clear();
        deviceAdapter.notifyDataSetChanged();
        spinner.setAdapter(deviceAdapter);


        Iotc.connect(this, key, new Iotc.Options() {
            @Override
            public void onConnect(String appName, String[] devices) {

                setTitle(appName);

                for (int i = 0; i < devices.length; i++){
                    avalilableDevices.add(devices[i]);
                    Iotc.subscribe(devices[i]);
                }
                deviceAdapter.notifyDataSetChanged();
            }

            @Override
            public void onMessageReceive(String deviceId, String msg) {
                myList.add(msg+" from "+deviceId);
                adapter.notifyDataSetChanged();
            }
            @Override
            public void onAppKeyError(){
                Toast.makeText(getApplicationContext(), "Key Not Right Please Check", Toast.LENGTH_LONG).show();
                Intent i = new Intent(getApplicationContext(), MainActivity.class);
                sharedPreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.clear();
                editor.commit();
                finish();
                startActivity(i);
            }
        });

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        // On selecting a spinner item
        String item = parent.getItemAtPosition(position).toString();
        selectedDevice = item;
        // Showing selected spinner item
        Toast.makeText(parent.getContext(), "Selected: " + item, Toast.LENGTH_LONG).show();
    }
    public void onNothingSelected(AdapterView<?> arg0) {
        // TODO Auto-generated method stub
    }

    @Override
    public void onBackPressed() {
        Iotc.disConnect();
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.logout){
            sharedPreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.clear();
            editor.commit();
            Intent i = new Intent(getApplicationContext(), MainActivity.class);
            Iotc.disConnect();

            spinner.setAdapter(null);

            finish();
            startActivity(i);
            Toast.makeText(getApplicationContext(), "Logged Out Success Fully", Toast.LENGTH_LONG).show();
        }
        return super.onOptionsItemSelected(item);
    }
}

