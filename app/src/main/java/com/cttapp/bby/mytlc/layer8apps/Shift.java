package com.cttapp.bby.mytlc.layer8apps;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class Shift {

    private String title;
    private String department;
    private String address;
    private Calendar startDate;
    private Calendar endDate;

    public Shift() {}

    public Shift(String title, String department, String address, Calendar startDate, Calendar endDate) {
        this.title = title;
        this.department = department;
        this.address = address;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public Shift(JSONObject jsonObj)
    {
        Log.v("SHIFT", "JSON String: " + jsonObj.toString());

        SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy hh:mm:ss a");

        try
        {
            this.title = jsonObj.getString("title");
            this.department = jsonObj.getString("department");
            this.address = jsonObj.getString("address");


            Log.v("SHIFT", "Start Date: " + sdf.parse(jsonObj.getString("startDate")));
            Log.v("SHIFT", "End Date: " + sdf.parse(jsonObj.getString("endDate")));

            Calendar cal1 = Calendar.getInstance();
            cal1.setTime(sdf.parse(jsonObj.getString("startDate")));
            this.startDate = cal1;

            Calendar cal2 = Calendar.getInstance();
            cal2.setTime(sdf.parse(jsonObj.getString("endDate")));
            this.endDate = cal2;
        }
        catch (JSONException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public String getAddress() {
        return address;
    }

    private void setAddress(String address) {
        this.address = address;
    }

    public String getDepartment() {
        return department;
    }

    private void setDepartment(String department) {
        this.department = department;
    }

    public Calendar getEndDate() {
        return endDate;
    }

    private void setEndDate(Calendar endDate) {
        this.endDate = endDate;
    }

    public Calendar getStartDate() {
        return startDate;
    }

    private void setStartDate(Calendar startDate) {
        this.startDate = startDate;
    }

    public String getTitle() {
        return title;
    }

    private void setTitle(String title) {
        this.title = title;
    }

}
