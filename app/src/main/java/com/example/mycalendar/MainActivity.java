package com.example.mycalendar;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.mycalendar.ViewModel.DateTimeTracker;
import com.example.mycalendar.ViewModel.MainViewModel;

import java.util.Calendar;
import java.util.Objects;

public class MainActivity extends AppCompatActivity{

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
        FragmentManager fragmentManager = getSupportFragmentManager();
        String tag;
        switch (itemId){
            case R.layout.fragment_calendar:
                tag = String.valueOf(R.layout.fragment_calendar);
                CalendarFragment calendarFragment = (CalendarFragment) fragmentManager.findFragmentByTag(tag);
                if (calendarFragment == null) {
                    calendarFragment = new CalendarFragment();
                }
                fragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, calendarFragment, tag)
                        .addToBackStack(null)
                        .commit();
                break;

            case R.layout.fragment_add:
                dateTimeTracker.setTime(Calendar.getInstance().get(Calendar.HOUR_OF_DAY), Calendar.getInstance().get(Calendar.MINUTE));
                tag = String.valueOf(R.layout.fragment_add);
                AddFragment addFragment = (AddFragment) fragmentManager.findFragmentByTag(tag);
                if (addFragment == null) {
                    addFragment = new AddFragment();
                }
                fragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, addFragment, tag)
                        .addToBackStack(null)
                        .commit();
        }
    }
}


