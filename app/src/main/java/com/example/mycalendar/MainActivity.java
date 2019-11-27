package com.example.mycalendar;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.mycalendar.ViewModel.DateTimeTracker;
import com.example.mycalendar.ViewModel.MainViewModel;

import java.util.Calendar;
import java.util.Objects;

public class MainActivity extends AppCompatActivity{

    CalendarFragment calendarFragment = new CalendarFragment();
    AddFragment addFragment = new AddFragment();
    private DateTimeTracker dateTimeTracker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MainViewModel mainViewModel = ViewModelProviders.of(this).get(MainViewModel.class);
        mainViewModel.getTitle().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
               Objects.requireNonNull(getSupportActionBar()).setTitle(s);
            }
        });
        mainViewModel.getFragmentId().observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                replaceFragment(integer);
            }
        });
        dateTimeTracker = ViewModelProviders.of(this).get(DateTimeTracker.class);
    }

    private void replaceFragment(int itemId) {
        switch (itemId){
            case R.layout.fragment_calendar:
                getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, calendarFragment)
                    .commit();
                break;
            case R.layout.fragment_add:
                dateTimeTracker.setTime(Calendar.getInstance().get(Calendar.HOUR_OF_DAY), Calendar.getInstance().get(Calendar.MINUTE));
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container, addFragment)
                        .commit();
        }
    }
}


