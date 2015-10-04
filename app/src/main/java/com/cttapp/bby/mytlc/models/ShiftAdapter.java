package com.cttapp.bby.mytlc.models;


import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.cttapp.bby.mytlc.R;
import com.cttapp.bby.mytlc.objects.ShiftViewObject;

import java.util.List;

/**
 * Created by chris on 9/29/15.
 */
public class ShiftAdapter extends RecyclerView.Adapter<ShiftAdapter.ShiftViewHolder> {

    List<ShiftViewObject> shiftList;

    public ShiftAdapter(List<ShiftViewObject> shiftList){
        this.shiftList = shiftList;
    }

    public static class ShiftViewHolder extends RecyclerView.ViewHolder {
        CardView cv;
        TextView workDay;
        TextView workDayLabel;
        TextView startTime;
        TextView endTime;
        TextView hoursWorked;

        ShiftViewHolder(View itemView) {
            super(itemView);
            cv = (CardView)itemView.findViewById(R.id.cv);
            workDay = (TextView)itemView.findViewById(R.id.dayWorking);
            workDayLabel = (TextView)itemView.findViewById(R.id.dayWorkingLabel);
            startTime = (TextView)itemView.findViewById(R.id.startShift);
            endTime = (TextView)itemView.findViewById(R.id.endShift);
            hoursWorked = (TextView)itemView.findViewById(R.id.hoursWorked);
        }
    }

    @Override
    public int getItemCount() {
        return shiftList.size();
    }

    @Override
    public ShiftViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.shift_view_layout, viewGroup, false);
        ShiftViewHolder pvh = new ShiftViewHolder(v);
        return pvh;
    }

    @Override
    public void onBindViewHolder(ShiftViewHolder shiftViewHolder, int i) {
        shiftViewHolder.hoursWorked.setText(shiftList.get(i).hoursWorked);
        shiftViewHolder.startTime.setText(shiftList.get(i).startTime);
        shiftViewHolder.endTime.setText(shiftList.get(i).endTime);
        shiftViewHolder.workDayLabel.setText(shiftList.get(i).workDate);
        shiftViewHolder.workDay.setText(shiftList.get(i).workWeekDay);

    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

}
