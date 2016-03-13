package com.cttapp.bby.mytlc;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;

import com.cttapp.bby.mytlc.layer8apps.MyTlc;
import com.cttapp.bby.mytlc.layer8apps.Preferences;
import com.cttapp.bby.mytlc.layer8apps.Settings;
import com.cttapp.bby.mytlc.layer8apps.Shift;
import com.cttapp.bby.mytlc.models.ShiftAdapter;
import com.cttapp.bby.mytlc.objects.ShiftViewObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

public class ShiftViewList extends Activity implements View.OnClickListener {

    private Preferences pf;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("onCreate", "onCreate");

        setContentView(R.layout.activity_shift_view_list);


        Log.d("onCreate", "set content view");

        Bundle bundle = getIntent().getExtras();

        Log.d("onCreate", "getting bundle");

        String jsonString;

        if(bundle == null)
        {
            pf = new Preferences(getBaseContext());
            jsonString = pf.getJSONString();
        }
        else
        {
            jsonString = bundle.getString("preloadedData");
        }

        Log.d("onCreate", "got json");

        Log.v("onCreate", "JSON: " + jsonString);


            RecyclerView rv = (RecyclerView) findViewById(R.id.rv);
            rv.setHasFixedSize(true);

            LinearLayoutManager llm = new LinearLayoutManager(this);
            rv.setLayoutManager(llm);

        Log.d("onCreate", "created recycler view");

        if(jsonString != null) {

            List<ShiftViewObject> persons = getShiftsFromJSON(jsonString);
            if(persons == null)
                return;
            Collections.reverse(persons);
            ShiftAdapter adapter = new ShiftAdapter(persons);
            rv.setAdapter(adapter);
        }

        Log.d("onCreate", "finished on create");

        NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(0);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_shift_view_list, menu);
        getMenuInflater().inflate(R.menu.menu_shift_view_list, menu);

        Log.d("onCreateOptionsMenu", "start onCreateOptionsMenu");

        WifiManager wifiManager = (WifiManager) getSystemService(WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();

        Log.d("onCreateOptionsMenu", "got wifi info");

        if(wifiInfo.getSSID() == null)
            Log.d("onCreateOptionsMenu","wifi is null");

        //Log.d("onCreateOptionsMenu", wifiInfo.getSSID());

        if (wifiInfo.getSSID() != null && !wifiInfo.getSSID().equalsIgnoreCase("\"BBYDemo\"") && !wifiInfo.getSSID().equalsIgnoreCase("\"BBYDemoFast\"") && !wifiInfo.getSSID().equalsIgnoreCase("\"iD Tech - Staff\""))
        {
            Log.d("onCreateOptionsMenu", "The ssid is not valid.");
            MenuItem item = menu.findItem(R.id.rss);
            item.setVisible(false);
        }
        else {
            MenuItem item = menu.findItem(R.id.rss);
            item.setVisible(true);

            Log.d("onCreateOptionsMenu", "ssid is valid, showing button");
        }



        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if(id == R.id.showLoginScreen)
        {
            Intent newView = new Intent(getBaseContext(), MyTlc.class);
            //newView.putExtra("preloadedData", pf.getJSONString());
            startActivity(newView);
        }
        else if (id == R.id.settings)
        {
            Intent settingsInt = new Intent(getBaseContext(), Settings.class);
            startActivity(settingsInt);
        }
        else if (id == R.id.rss)
        {
            Intent rssInt = new Intent(getBaseContext(), RSSView.class);
            startActivity(rssInt);
        }

        return super.onOptionsItemSelected(item);
    }

    private ArrayList<ShiftViewObject> getShiftsFromJSON(String json)
    {
        ArrayList<ShiftViewObject> tmpShiftViewObjects = new ArrayList<>();

        try
        {
            JSONArray array = new JSONArray(json);
            for(int x = 0; x < array.length(); x++)
            {
                JSONObject jsonObject = array.getJSONObject(x);
                Shift newShift = new Shift(jsonObject);
                Calendar now = Calendar.getInstance();

                Log.d("DATE", "Date:" + now.compareTo(newShift.getEndDate()));


                if(now.compareTo(newShift.getEndDate()) <= 0)
                    tmpShiftViewObjects.add(new ShiftViewObject(newShift.getStartDate(), newShift.getEndDate()));
            }
            return tmpShiftViewObjects;
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }


    @Override
    public void onResume(){
        super.onResume();
        // put your code here...

        invalidateOptionsMenu();

        String jsonString;

        pf = new Preferences(getBaseContext());
        jsonString = pf.getJSONString();

        if(jsonString == null)
        {
            Intent newView = new Intent(getBaseContext(), MyTlc.class);
            startActivity(newView);

            //Intent newView = new Intent(getBaseContext(), AlertDetailActivity.class);
            //newView.putExtra("URL", "https://s3.amazonaws.com/mytlc/files/android-init.html");
            //startActivity(newView);
        }

        RecyclerView rv = (RecyclerView) findViewById(R.id.rv);
        rv.setHasFixedSize(true);

        LinearLayoutManager llm = new LinearLayoutManager(this);
        rv.setLayoutManager(llm);

        if(jsonString != null) {

            List<ShiftViewObject> persons = getShiftsFromJSON(jsonString);
            if(persons == null)
                return;
            Collections.reverse(persons);
            ShiftAdapter adapter = new ShiftAdapter(persons);
            rv.setAdapter(adapter);
        }

        NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(0);

    }

    public void onClick(View v) {

    }
}
