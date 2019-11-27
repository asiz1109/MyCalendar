package com.example.mycalendar;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
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
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.mycalendar.ViewModel.DateTimeTracker;
import com.example.mycalendar.ViewModel.MainViewModel;

import java.util.Calendar;


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
    private int remind = 0;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainViewModel = ViewModelProviders.of(requireActivity()).get(MainViewModel.class);
        dateTimeTracker = ViewModelProviders.of(requireActivity()).get(DateTimeTracker.class);
        dbHelper = new DBHelper(requireContext());
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
                        remind = 0;
                        break;
                    case R.id.rb_10m:
                        remind = 1;
                        break;
                    case R.id.rb_30m:
                        remind = 2;
                        break;
                    case R.id.rb_1h:
                        remind = 3;
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
                }
                else {
                    tv_time.setEnabled(true);
                    tv_time.setTextColor(getResources().getColor(R.color.black));
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
                int allDay = cb_all_day.isChecked()? 1 : 0;
                SQLiteDatabase database = dbHelper.getWritableDatabase();
                ContentValues contentValues = new ContentValues();
                contentValues.put(DBHelper.KEY_EVENT, et_event.getText().toString());
                contentValues.put(DBHelper.KEY_DAY, dateTimeTracker.getDay().getValue());
                contentValues.put(DBHelper.KEY_MONTH, dateTimeTracker.getMonth().getValue());
                contentValues.put(DBHelper.KEY_YEAR, dateTimeTracker.getYear().getValue());
                if (cb_all_day.isChecked()){
                    contentValues.put(DBHelper.KEY_HOUR, 0);
                    contentValues.put(DBHelper.KEY_MINUTE, 0);
                } else {
                    contentValues.put(DBHelper.KEY_HOUR, dateTimeTracker.getHour().getValue());
                    contentValues.put(DBHelper.KEY_MINUTE, dateTimeTracker.getMinute().getValue());
                }
                contentValues.put(DBHelper.KEY_ALL_DAY, allDay);
                contentValues.put(DBHelper.KEY_REMIND, remind);
                database.insert(DBHelper.TABLE_EVENTS, null, contentValues);
                dbHelper.close();
                closeFragment();
            }
        });

        dateTimeTracker.getYear().observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                mYear = integer;
                changeText();
            }
        });
        dateTimeTracker.getMonth().observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                mMonth = integer;
                changeText();
            }
        });
        dateTimeTracker.getDay().observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                mDay = integer;
                changeText();
            }
        });
        dateTimeTracker.getHour().observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                mHour = integer;
                changeText();
            }
        });
        dateTimeTracker.getMinute().observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                mMinute = integer;
                changeText();
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
                DatePickerDialog datePickerDialog = new DatePickerDialog(requireContext(), myDateListener, 2019, 11, 26);
                datePickerDialog.show();
                break;
            case R.id.tv_time:
                Calendar calendar = Calendar.getInstance();
                TimePickerDialog timePickerDialog = new TimePickerDialog(requireContext(), myTimeListener, calendar.getTime().getHours(), calendar.getTime().getMinutes(), true);
                timePickerDialog.show();
                break;
        }
    }

    DatePickerDialog.OnDateSetListener myDateListener = new DatePickerDialog.OnDateSetListener() {

        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            dateTimeTracker.setDate(year, monthOfYear, dayOfMonth);
        }
    };

    TimePickerDialog.OnTimeSetListener myTimeListener = new TimePickerDialog.OnTimeSetListener() {
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            dateTimeTracker.setTime(hourOfDay, minute);
        }
    };

    private void changeText(){
        String month = String.valueOf(mMonth+1);
        String editMonth = month.length()<2 ? "0"+month : month;
        tv_date.setText(mDay + "." + editMonth + "." + mYear);
        String hour = String.valueOf(mHour).length()<2 ? "0"+mHour : String.valueOf(mHour);
        String minute = String.valueOf(mMinute).length()<2 ? "0"+mMinute : String.valueOf(mMinute);
        tv_time.setText(hour + ":" + minute);
    }

    private void closeFragment(){
        et_event.setText("");
        cb_all_day.setChecked(false);
        radioGroup.check(R.id.rb_not);
        dateTimeTracker.setDefaultDate();
        mainViewModel.setFragmentId(R.layout.fragment_calendar);
    }
}
