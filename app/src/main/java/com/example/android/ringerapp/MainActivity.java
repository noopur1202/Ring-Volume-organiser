package com.example.android.ringerapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

@RequiresApi(api = Build.VERSION_CODES.N)
public class MainActivity extends AppCompatActivity {

    public static TextView startTime1, endTime1 , startTime2, endTime2;
    public static Button timeOkButton, muteButton, saveButton;
    TimePickerFragment timePickerFragment;
    public static TextView level1, level2;
    FragmentTransaction fragmentTransaction;
    FragmentManager fragmentManager;
    public static int setTimeToTextview;
    public AudioManager mAudioManager;
    BroadcastReceiver tickReceiver;
    CalenderHelper calenderHelper;
    public static ArrayList<HashMap<String,String>> calenderEventMap = new ArrayList<HashMap<String,String>>();
    public List<String> dStartTime, dEndTime;
    public CharSequence start1Text, start2Text, end1Text, end2Text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        startTime1 = (TextView) findViewById(R.id.start_time1);
        endTime1 = (TextView) findViewById(R.id.end_time1);
        startTime2 = (TextView) findViewById(R.id.start_time2);
        timeOkButton = (Button) findViewById(R.id.time_ok_button);
        muteButton = (Button)findViewById(R.id.muteButton);
        saveButton = (Button)findViewById(R.id.saveButton);
        endTime2 = (TextView) findViewById(R.id.end_time2);
        level1 = (TextView)findViewById(R.id.level1);
        level2 = (TextView)findViewById(R.id.level2);
        calenderHelper = new CalenderHelper();

        start1Text = startTime1.getText();
        start2Text = startTime2.getText();
        end1Text = endTime1.getText();
        end2Text = endTime2.getText();

        dStartTime = new ArrayList<String>();
        dEndTime = new ArrayList<String>();

        compareAndChangeRingVolume();

        mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

        startTime1.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View view) {
                setTimeToTextview = 0;
                saveButton.setVisibility(View.VISIBLE);
               openTimePickerFragment();
            }
        });


        endTime1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setTimeToTextview = 1;
                saveButton.setVisibility(View.VISIBLE);
               openTimePickerFragment();
            }
        });

        startTime2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setTimeToTextview = 2;
                saveButton.setVisibility(View.VISIBLE);
                openTimePickerFragment();
            }
        });

        endTime2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setTimeToTextview = 3;
                saveButton.setVisibility(View.VISIBLE);
                openTimePickerFragment();
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                DateFormat df1 = new SimpleDateFormat("dd-MM-yyyy");
                String date = df1.format(Calendar.getInstance().getTime());

                String startTimestamp1 = date+" "+changeStringDateFormat((String) startTime1.getText());
                String startTimestamp2 = date+" "+changeStringDateFormat((String) startTime2.getText());
                String endTimestamp1 = date+" "+changeStringDateFormat((String) endTime1.getText());
                String endTimestamp2 = date+" "+changeStringDateFormat((String) endTime2.getText());

                long start1Milli = getMilliseconds(startTimestamp1);
                long start2Milli = getMilliseconds(startTimestamp2);
                long end1Milli = getMilliseconds(endTimestamp1);
                long end2Milli = getMilliseconds(endTimestamp2);

                if (start1Milli>end1Milli){
                    startTime1.setText(start1Text);
                    endTime1.setText(end1Text);
                    Toast.makeText(getApplicationContext(), "Set start time before end time.", Toast.LENGTH_SHORT).show();
                }
                else if (end1Milli>start2Milli){
                    endTime1.setText(end1Text);
                    startTime2.setText(start2Text);
                    Toast.makeText(getApplicationContext(), "Timings overlapping, adjust timings.", Toast.LENGTH_SHORT).show();
                }
                else if (start2Milli>end2Milli){
                    startTime2.setText(start2Text);
                    endTime2.setText(end2Text);
                    Toast.makeText(getApplicationContext(), "Set start time before end time.", Toast.LENGTH_SHORT).show();
                }
                else {
                    saveButton.setVisibility(View.GONE);
                }
            }
        });

        level1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                levelPick((TextView) view);
            }
        });

        level2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                levelPick((TextView) view);
            }
        });

        muteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                muteButton.setVisibility(View.GONE);
                int num1 = Integer.valueOf(level1.getText().toString());
                mAudioManager.setStreamVolume(AudioManager.STREAM_RING,num1,
                        AudioManager.FLAG_ALLOW_RINGER_MODES|AudioManager.FLAG_PLAY_SOUND);
                level1.setText(String.valueOf(num1));
            }
        });

        tickReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if(intent.getAction().compareTo(Intent.ACTION_TIME_TICK)==0){
                    compareAndChangeRingVolume();
                    syncWithCalender();
                }
            }
        };
        registerReceiver(tickReceiver, new IntentFilter(Intent.ACTION_TIME_TICK));
    }

    public void openTimePickerFragment(){
        timePickerFragment = new TimePickerFragment();
        fragmentManager = getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.main_page,timePickerFragment);
        fragmentTransaction.commit();
    }

    public void levelPick(final TextView textView){
        PopupMenu popup = new PopupMenu(MainActivity.this, textView);
        popup.getMenuInflater().inflate(R.menu.level_pick, popup.getMenu());
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                textView.setText(item.getTitle());
                return true;
            }
        });
        popup.show();
    }

    public void compareAndChangeRingVolume() {

        mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

        DateFormat df = new SimpleDateFormat("dd-MM-yyyy HH:mm");
        String currentTimestamp = df.format(Calendar.getInstance().getTime());

        DateFormat df1 = new SimpleDateFormat("dd-MM-yyyy");
        String date = df1.format(Calendar.getInstance().getTime());

        long currentMilliseconds = getMilliseconds(currentTimestamp);

        String startTimestamp1 = date+" "+changeStringDateFormat((String) startTime1.getText());
        String endTimestamp1 = date+" "+changeStringDateFormat((String) endTime1.getText());

        long startTime1Milliseconds = getMilliseconds(startTimestamp1);
        long endTime1Milliseconds = getMilliseconds(endTimestamp1);

        String startTimestamp2 = date+" "+changeStringDateFormat((String) startTime2.getText());

        long startTime2Milliseconds = getMilliseconds(startTimestamp2);

        if (currentMilliseconds>=startTime1Milliseconds && currentMilliseconds<=endTime1Milliseconds){
            int num1 = Integer.valueOf(level1.getText().toString());
            mAudioManager.setStreamVolume(AudioManager.STREAM_RING,num1,
                    AudioManager.FLAG_ALLOW_RINGER_MODES|AudioManager.FLAG_PLAY_SOUND);
        }
        else if (currentMilliseconds>=startTime2Milliseconds){
            int num1 = Integer.valueOf(level2.getText().toString());
            mAudioManager.setStreamVolume(AudioManager.STREAM_RING,num1,
                    AudioManager.FLAG_ALLOW_RINGER_MODES|AudioManager.FLAG_PLAY_SOUND);
        }
    }

    public void syncWithCalender() {

        mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

        DateFormat df = new SimpleDateFormat("dd-MM-yyyy HH:mm");
        String currentTimestamp = df.format(Calendar.getInstance().getTime());
        long currentMilliseconds = getMilliseconds(currentTimestamp);

        calenderEventMap = calenderHelper.readCalendarEvent(getApplicationContext());
        if (calenderEventMap.size() > 0) {

            findMeetingTime();

            for (int i = 0; i < dStartTime.size(); i++) {

                long calenderStartMilliseconds = getMilliseconds(dStartTime.get(i));
                long calenderEndMilliseconds = getMilliseconds(dEndTime.get(i));

                if (currentMilliseconds>=calenderStartMilliseconds&&currentMilliseconds<=calenderEndMilliseconds) {
                    mAudioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
                    muteButton.setVisibility(View.VISIBLE);
                }
                else if (currentMilliseconds>=calenderEndMilliseconds) {
                    int num1 = Integer.valueOf(level1.getText().toString());
                    mAudioManager.setStreamVolume(AudioManager.STREAM_RING, num1,
                            AudioManager.FLAG_ALLOW_RINGER_MODES | AudioManager.FLAG_PLAY_SOUND);
                    muteButton.setVisibility(View.GONE);
                }
            }
        } else {
            Toast.makeText(getApplicationContext(), "No calender events found.", Toast.LENGTH_SHORT).show();
        }
    }

    public void findMeetingTime(){
        for (int i=0;i<calenderEventMap.size();i++)
        {
            HashMap<String, String> hashmap= calenderEventMap.get(i);
            String titleString= hashmap.get("title");
            String dtstartString= hashmap.get("dtstart");
            String dtendString= hashmap.get("dtend");
            if (titleString.matches(".*meeting.*")) {
                dStartTime.add(dtstartString);
                dEndTime.add(dtendString);
            } else {
                Log.v("LOG_TAG","Value of titleStringWithoutMeeting "+titleString);
            }
        }
        Log.v("LOG_TAG","Value of titleStringWithMeeting "+ dStartTime.get(0) + dStartTime.get(1));
    }

    public long getMilliseconds(String myDateTime) {

        String toParse = myDateTime; // Results in "2-5-2012 20:43"
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm");
        Date date = null;
        try {
            date = formatter.parse(toParse);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        long millis = date.getTime();
        return millis;
    }

    public String changeStringDateFormat(String incomingDate){
        String [] tempStr = new String[5];
        tempStr = incomingDate.split(" ");

        if (tempStr[3].equals("PM")){
            int newValue = Integer.parseInt(tempStr[0])+12;
            tempStr[0]= String.valueOf(newValue);
        }

        String outgoingDate = tempStr[0]+tempStr[1]+tempStr[2];
        return outgoingDate;
    }
}
