package com.example.mycalendar;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mycalendar.ViewModel.DateTimeTracker;
import com.example.mycalendar.ViewModel.MainViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class CalendarFragment extends Fragment implements AdapterListener{

    private CalendarView cv_calendar;
    private FloatingActionButton btn_add;
    private MainViewModel mainViewModel;
    private DateTimeTracker dateTimeTracker;
    private RecyclerView recyclerView;
    private AdapterRV adapterRV;
    private List<Event> eventList = new ArrayList<>();
    private DBHelper dbHelper;
    private int position;

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
        recyclerView = view.findViewById(R.id.recycler_view);
        adapterRV = new AdapterRV();
        adapterRV.setListener(this);
        recyclerView.setAdapter(adapterRV);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        dateTimeTracker.getYear().observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                getListOfTheDay();
            }
        });
        dateTimeTracker.getMonth().observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                getListOfTheDay();
            }
        });
        dateTimeTracker.getDay().observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                getListOfTheDay();
            }
        });
    }


    private void getListOfTheDay(){
        dbHelper = new DBHelper(requireContext());
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        eventList.clear();
        String day = String.valueOf(dateTimeTracker.getDay().getValue());
        String month = String.valueOf(dateTimeTracker.getMonth().getValue());
        String year = String.valueOf(dateTimeTracker.getYear().getValue());
        Cursor cursor = database.query(DBHelper.TABLE_EVENTS, null,
                DBHelper.KEY_DAY + " = ? AND " + DBHelper.KEY_MONTH + " = ? AND " + DBHelper.KEY_YEAR + " = ?", new String[]{day, month, year},
                null, null, null);
        if (cursor.moveToFirst()) {
            do {
                int db_id = cursor.getInt(0);
                String db_event = cursor.getString(1);
                int db_day = cursor.getInt(2);
                int db_month = cursor.getInt(3);
                int db_year = cursor.getInt(4);
                int db_hour = cursor.getInt(5);
                int db_minute = cursor.getInt(6);
                int db_all_day = cursor.getInt(7);
                int db_remind = cursor.getInt(8);
                eventList.add(new Event(db_id, db_event, db_day, db_month, db_year, db_hour, db_minute, db_all_day, db_remind));
            } while (cursor.moveToNext());
        }
        cursor.close();
        dbHelper.close();
        adapterRV.setList(eventList);
    }

    @Override
    public void onItemLongClick(Event event, int position) {
        this.position = position;
        new AlertDialog.Builder(requireContext())
                .setMessage(R.string.delete)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        removeEvent();
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                }).show();
    }

    private void removeEvent(){
        int id = eventList.get(position).getId();
        dbHelper = new DBHelper(requireContext());
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        database.delete(DBHelper.TABLE_EVENTS, DBHelper.KEY_ID + " = " + id, null);
        dbHelper.close();
        getListOfTheDay();
    }
}
