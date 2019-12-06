package com.example.mycalendar;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.AlarmManagerCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.mycalendar.Alarm.MyReceiver;
import com.example.mycalendar.BD.DBHelper;
import com.example.mycalendar.BD.Event;
import com.example.mycalendar.ViewModel.DateTimeTracker;
import com.example.mycalendar.ViewModel.MainViewModel;

import java.util.Calendar;
import java.util.Locale;
import java.util.Objects;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static com.example.mycalendar.Remind.*;


public class AddFragment extends Fragment implements View.OnClickListener {

    private EditText et_event;
    private TextView tv_date, tv_time;
    private RadioGroup radioGroup;
    private Button btn_ok;
    private CheckBox cb_all_day;
    private MainViewModel mainViewModel;
    private DateTimeTracker dateTimeTracker;
    private DBHelper dbHelper;

    private int mYear, mMonth, mDay, mHour, mMinute;
    private Remind remind = notRemind;

    public static final String TAG = String.valueOf(R.layout.fragment_add);
    private static final long REMIND_10_MINUTES_MILLIS = TimeUnit.MINUTES.toMillis(10);
    private static final long REMIND_30_MINUTES_MILLIS = TimeUnit.MINUTES.toMillis(30);
    private static final long REMIND_1_HOUR_MILLIS = TimeUnit.HOURS.toMillis(1);
    public static final String KEY_EVENT = "event";
    public static final String KEY_TIME = "time";


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainViewModel = ViewModelProviders.of(requireActivity()).get(MainViewModel.class);
        dateTimeTracker = ViewModelProviders.of(requireActivity()).get(DateTimeTracker.class);
        dbHelper = DBHelper.getInstance(requireContext());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_add, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mainViewModel.setTitle(getString(R.string.new_event));
        et_event = view.findViewById(R.id.et_event);
        tv_date = view.findViewById(R.id.tv_date);
        tv_date.setOnClickListener(this);
        tv_time = view.findViewById(R.id.tv_time);
        tv_time.setOnClickListener(this);
        radioGroup = view.findViewById(R.id.radio_group);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId){
                    case R.id.rb_not:
                        remind = notRemind;
                        break;
                    case R.id.rb_10m:
                        remind = remind_10minuts;
                        break;
                    case R.id.rb_30m:
                        remind = remind_30minuts;
                        break;
                    case R.id.rb_1h:
                        remind = remind_1hour;
                        break;
                }
            }
        });
        cb_all_day = view.findViewById(R.id.cb_all_day);
        cb_all_day.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    tv_time.setEnabled(false);
                    tv_time.setTextColor(getResources().getColor(R.color.enabled));
                    radioGroup.check(R.id.rb_not);
                    for (int i = 0; i < radioGroup.getChildCount(); i++) {
                        radioGroup.getChildAt(i).setEnabled(false);
                    }
                }
                else {
                    tv_time.setEnabled(true);
                    tv_time.setTextColor(getResources().getColor(R.color.black));
                    for (int i = 0; i < radioGroup.getChildCount(); i++) {
                        radioGroup.getChildAt(i).setEnabled(true);
                    }
                }
            }
        });

        et_event.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                    setButtonEnabled();
            }
        });

        btn_ok = view.findViewById(R.id.btn_ok);
        btn_ok.setClickable(false);
        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addEvent();
                closeFragment();
            }
        });

        dateTimeTracker.getYear().observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                mYear = integer;
                changeTextDate();
            }
        });
        dateTimeTracker.getMonth().observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                mMonth = integer;
                changeTextDate();
            }
        });
        dateTimeTracker.getDay().observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                mDay = integer;
                changeTextDate();
            }
        });
        dateTimeTracker.getHour().observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                mHour = integer;
                changeTextTime();
            }
        });
        dateTimeTracker.getMinute().observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                mMinute = integer;
                changeTextTime();
            }
        });
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.close_fragment, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.close) {
            closeFragment();
        }
        return super.onOptionsItemSelected(item);
    }

    private void setButtonEnabled(){
        btn_ok.setEnabled(!TextUtils.isEmpty(et_event.getText().toString()));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_date:
                DatePickerDialog datePickerDialog = new DatePickerDialog(requireContext(), myDateListener, mYear, mMonth, mDay);
                datePickerDialog.show();
                break;
            case R.id.tv_time:
                Calendar calendar = Calendar.getInstance();
                TimePickerDialog timePickerDialog = new TimePickerDialog(requireContext(), myTimeListener, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true);
                timePickerDialog.show();
                break;
        }
    }

    private DatePickerDialog.OnDateSetListener myDateListener = new DatePickerDialog.OnDateSetListener() {
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            dateTimeTracker.setDate(year, monthOfYear, dayOfMonth);
        }
    };

    private TimePickerDialog.OnTimeSetListener myTimeListener = new TimePickerDialog.OnTimeSetListener() {
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            dateTimeTracker.setTime(hourOfDay, minute);
        }
    };

    private void changeTextDate(){
        String month = String.valueOf(mMonth+1);
        String editMonth = month.length()<2 ? "0"+month : month;
        tv_date.setText(String.format(Locale.US,"%d.%s.%d", mDay, editMonth, mYear));
    }

    private void changeTextTime(){
        String hour = String.valueOf(mHour).length()<2 ? "0"+mHour : String.valueOf(mHour);
        String minute = String.valueOf(mMinute).length()<2 ? "0"+mMinute : String.valueOf(mMinute);
        tv_time.setText(String.format(Locale.US, "%s:%s", hour, minute));
    }

    private void closeFragment(){
        et_event.setText("");
        cb_all_day.setChecked(false);
        radioGroup.check(R.id.rb_not);
        dateTimeTracker.setDefaultDate();
        mainViewModel.setFragmentId(R.layout.fragment_calendar);
    }

    private void addEvent(){
        String event = et_event.getText().toString();
        int day = Objects.requireNonNull(dateTimeTracker.getDay().getValue());
        int month = Objects.requireNonNull(dateTimeTracker.getMonth().getValue());
        int year = Objects.requireNonNull(dateTimeTracker.getYear().getValue());
        int hour = Objects.requireNonNull(dateTimeTracker.getHour().getValue());
        int minute = Objects.requireNonNull(dateTimeTracker.getMinute().getValue());
        int allDay = cb_all_day.isChecked()? 1 : 0;
        Random random_idAlarm = new Random(System.currentTimeMillis());
        int idAlarm = random_idAlarm.nextInt(214748364);
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(DBHelper.KEY_EVENT, event);
        contentValues.put(DBHelper.KEY_DAY, day);
        contentValues.put(DBHelper.KEY_MONTH, month);
        contentValues.put(DBHelper.KEY_YEAR, year);
        if (cb_all_day.isChecked()){
            contentValues.put(DBHelper.KEY_HOUR, 0);
            contentValues.put(DBHelper.KEY_MINUTE, 0);
        } else {
            contentValues.put(DBHelper.KEY_HOUR, hour);
            contentValues.put(DBHelper.KEY_MINUTE, minute);
        }
        contentValues.put(DBHelper.KEY_ALL_DAY, allDay);
        contentValues.put(DBHelper.KEY_REMIND, remind.ordinal());
        contentValues.put(DBHelper.KEY_ID_ALARM, idAlarm);
        database.insert(DBHelper.TABLE_EVENTS, null, contentValues);
        dbHelper.close();
        if(remind.ordinal()>0) addAlarm(new Event(1, event, day, month, year, hour, minute, allDay, remind, idAlarm));
    }

    private void addAlarm(Event event){
        Intent intent = new Intent(requireContext(), MyReceiver.class);
        intent.setAction(MyApp.ALARM);
        intent.putExtra(KEY_EVENT, event.getEvent()).putExtra(KEY_TIME, tv_time.getText().toString());
        PendingIntent task = PendingIntent.getBroadcast(requireContext(), event.getIdAlarm(), intent, 0);
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH, event.getDay());
        calendar.set(Calendar.MONTH, event.getMonth());
        calendar.set(Calendar.YEAR, event.getYear());
        calendar.set(Calendar.HOUR_OF_DAY, event.getHour());
        calendar.set(Calendar.MINUTE, event.getMinute());
        calendar.set(Calendar.SECOND, 0);
        long millis = 0;
        switch (remind){
            case remind_10minuts:
                millis = REMIND_10_MINUTES_MILLIS;
                break;
            case remind_30minuts:
                millis = REMIND_30_MINUTES_MILLIS;
                break;
            case remind_1hour:
                millis = REMIND_1_HOUR_MILLIS;
                break;
        }
        AlarmManager alarmManager = (AlarmManager) Objects.requireNonNull(getActivity()).getSystemService(Context.ALARM_SERVICE);
        AlarmManagerCompat.setExact(Objects.requireNonNull(alarmManager), AlarmManager.RTC, calendar.getTimeInMillis()-millis, task);
    }
}
