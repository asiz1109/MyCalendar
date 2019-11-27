package com.example.mycalendar;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.example.mycalendar.ViewModel.DateTimeTracker;
import com.example.mycalendar.ViewModel.MainViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class CalendarFragment extends Fragment {

    private CalendarView cv_calendar;
    private FloatingActionButton btn_add;
    private MainViewModel mainViewModel;
    private DateTimeTracker dateTimeTracker;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainViewModel = ViewModelProviders.of(requireActivity()).get(MainViewModel.class);
        dateTimeTracker = ViewModelProviders.of(requireActivity()).get(DateTimeTracker.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_calendar, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mainViewModel.setTitle(getString(R.string.app_name));
        cv_calendar = view.findViewById(R.id.calendar_view);
        cv_calendar.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                dateTimeTracker.setDate(year, month, dayOfMonth);
            }
        });
        btn_add = view.findViewById(R.id.fab_add);
        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainViewModel.setFragmentId(R.layout.fragment_add);
            }
        });
    }
}
