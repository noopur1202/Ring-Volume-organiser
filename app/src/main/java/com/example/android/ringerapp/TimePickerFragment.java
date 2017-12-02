package com.example.android.ringerapp;

import android.icu.util.Calendar;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TimePicker;

/**
 * Created by amith on 7/17/17.
 */

public class TimePickerFragment extends Fragment{

    public TimePicker timePicker1;
    public Calendar calendar;
    public static StringBuilder finalTimeToBeSet;
    public String format = "";
    public Button timeOkButton;
    public RelativeLayout timePickerView;

    @Override
    public void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.time_picker, container,false);
        timeOkButton = (Button) v.findViewById(R.id.time_ok_button);
        timePickerView = (RelativeLayout) v.findViewById(R.id.time_picker_view);
        timePicker1 = (TimePicker)v.findViewById(R.id.timePicker1);

        calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int min = calendar.get(Calendar.MINUTE);
        showTime(hour, min);

        timeOkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int hour = timePicker1.getHour();
                int min = timePicker1.getMinute();
                showTime(hour, min);
                timePickerView.setVisibility(View.GONE);
            }
        });
        return v;
    }

    public void showTime(int hour, int min) {
        if (hour == 0) {
            hour += 12;
            format = "AM";
        } else if (hour == 12) {
            format = "PM";
        } else if (hour > 12) {
            hour -= 12;
            format = "PM";
        } else {
            format = "AM";
        }

        finalTimeToBeSet = new StringBuilder().append(hour).append(" : ").append(min)
                .append(" ").append(format);

        switch (MainActivity.setTimeToTextview){
            case 0:  MainActivity.startTime1.setText(finalTimeToBeSet);break;
            case 1:  MainActivity.endTime1.setText(finalTimeToBeSet);break;
            case 2:  MainActivity.startTime2.setText(finalTimeToBeSet);break;
            case 3:  MainActivity.endTime2.setText(finalTimeToBeSet);break;
        }
    }
}
