package com.cttapp.bby.mytlc.objects;

import android.util.Log;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by chris on 9/29/15.
 */
public class ShiftViewObject {

    public String workDate;
    public String startTime;
    public String endTime;
    public String hoursWorked;
    public String workWeekDay;

    public ShiftViewObject(String startTime, String endTime) {
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public ShiftViewObject(Calendar startDate, Calendar endDate) {

        SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a");
        SimpleDateFormat day = new SimpleDateFormat("EEE");
        SimpleDateFormat weekDay = new SimpleDateFormat("MMM dd");

        workWeekDay = day.format(startDate.getTime());
        workDate = weekDay.format(startDate.getTime());
        startTime = timeFormat.format(startDate.getTime());
        endTime = timeFormat.format(endDate.getTime());

        hoursWorked = hoursDifference(startDate.getTime(), endDate.getTime());

        Log.v("ShiftViewObject", "startDate:" + startDate);
        Log.v("ShiftViewObject", "endDate:" + endDate);

        Log.v("ShiftViewObject", "startTime:" + startTime);
        Log.v("ShiftViewObject", "endTime:" + endTime);

    }

    private static String hoursDifference(Date date1, Date date2) {

        final int MILLI_TO_HOUR = 1000 * 60 * 60;
        double hoursWorked = (double) (date2.getTime() - date1.getTime()) / MILLI_TO_HOUR;
        NumberFormat formatter = new DecimalFormat("#0.#");
        return formatter.format(hoursWorked);
    }
}
