package com.example.cajet.text_contact.note;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

import com.example.cajet.text_contact.R;


public class EditPlan extends Activity {
    EditText editTheme, editText;
    Button goBack, finish, setDateB, setTimeB;

    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.CHINA);
    TextView finishDate, finishTime;
    CheckBox checkbox;
    Calendar calendar, alarmCalendar;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.note_edit_layout);
        Bundle bundle = this.getIntent().getExtras();
        findView();
        bindButton();
        calendar = Calendar.getInstance();
        alarmCalendar = Calendar.getInstance();
        alarmCalendar.setTimeInMillis(System.currentTimeMillis());
        if (bundle != null) {
            init(bundle);
        }
    }
    private void findView() {
        editTheme=(EditText)findViewById(R.id.editTheme);
        editText=(EditText)findViewById(R.id.editText);
        goBack=(Button)findViewById(R.id.back);
        finish=(Button)findViewById(R.id.finish);
        setDateB=(Button)findViewById(R.id.setupDate);
        setTimeB=(Button)findViewById(R.id.setupTime);
        finishDate=(TextView)findViewById(R.id.finishDate);
        finishTime=(TextView)findViewById(R.id.finishTime);
        checkbox=(CheckBox)findViewById(R.id.checkbox);
    }
    private void bindButton() {
        goBack.setOnClickListener(new myOnClickListener());
        finish.setOnClickListener(new myOnClickListener());
        setDateB.setOnClickListener(new myOnClickListener());
        setTimeB.setOnClickListener(new myOnClickListener());
    }
    public class myOnClickListener implements OnClickListener {

        @Override
        public void onClick(View v) {
            switch(v.getId()) {
                case R.id.back :
                    backEvent();
                    break;
                case R.id.finish:
                    finishEvent();
                    break;
                case R.id.setupDate:
                    setupDate();
                    break;
                case R.id.setupTime:
                    setupTime();
                    break;
            }
        }
    }
    private void init(Bundle bundle) {
        editTheme.setText(bundle.getString("theme"));
        editText.setText(bundle.getString("text"));
        String temp = bundle.getString("deadline");
        if (temp != null && temp.length() > 11) {
            String divide[] = temp.split(" ");
            finishDate.setText(divide[0]);
            finishTime.setText(divide[1]);
        }
        temp = bundle.getString("state");
        if (temp != null && temp.equals("已完成")) {
            checkbox.setChecked(true);
        } else {
            checkbox.setChecked(false);
        }
    }

    private void backEvent() {
//        Intent intent = null;
        EditPlan.this.setResult(RESULT_OK, null);
        EditPlan.this.finish();
    }
    private void finishEvent() {
        Intent intent = new Intent();
        intent.putExtra("theme", editTheme.getText().toString());
        intent.putExtra("text", editText.getText().toString());
        Date curDate = new Date(System.currentTimeMillis());
        intent.putExtra("time", dateFormat.format(curDate));
        intent.putExtra("deadline", finishDate.getText().toString()+ " " +finishTime.getText().toString());
        if (checkbox.isChecked()) {
            intent.putExtra("state", "已完成");
            cancelAlarm();
        } else {
            intent.putExtra("state", "未完成");
            setAlarm();
        }
        sentBroadcast();
        EditPlan.this.setResult(RESULT_OK, intent);
        EditPlan.this.finish();
    }
    private void setupDate() {
        new DatePickerDialog(EditPlan.this,
                new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        finishDate.setText(year+"/"+(monthOfYear+1)+"/"+dayOfMonth);
                        alarmCalendar.set(year, monthOfYear, dayOfMonth);
                    }
                }, calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
    }
    private void setupTime() {
        new TimePickerDialog(EditPlan.this,
                new TimePickerDialog.OnTimeSetListener() {

                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        alarmCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        alarmCalendar.set(Calendar.MINUTE, minute);
                        alarmCalendar.set(Calendar.SECOND, 0);
                        alarmCalendar.set(Calendar.MILLISECOND, 0);
                        finishTime.setText(hourOfDay+":"+minute);
                    }
                }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true).show();

    }
    public void setAlarm () {
        Intent intent = new Intent("myAlarmAction");
        PendingIntent pendingIntent = PendingIntent.getBroadcast(EditPlan.this, 0, intent, 0);
        AlarmManager am = (AlarmManager)getSystemService(ALARM_SERVICE);
        am.set(AlarmManager.RTC_WAKEUP, alarmCalendar.getTimeInMillis(), pendingIntent);
        am.setRepeating(AlarmManager.RTC_WAKEUP, alarmCalendar.getTimeInMillis()+(10*1000),(24*60*60*1000), pendingIntent);
    }
    public void cancelAlarm() {
        Intent intent = new Intent("myAlarmAction");
        PendingIntent pendingIntent = PendingIntent.getBroadcast(EditPlan.this, 0, intent, 0);
        AlarmManager am = (AlarmManager)getSystemService(ALARM_SERVICE);
        am.cancel(pendingIntent);
    }
    public void sentBroadcast() {
        Intent intent = new Intent("myWidgetAction");
        intent.putExtra("theme", editTheme.getText().toString());
        intent.putExtra("text", editText.getText().toString());
        Date curDate = new Date(System.currentTimeMillis());
        intent.putExtra("time", dateFormat.format(curDate));
        intent.putExtra("deadline", finishDate.getText().toString()+ " " +finishTime.getText().toString());
        sendBroadcast(intent);
    }
}
