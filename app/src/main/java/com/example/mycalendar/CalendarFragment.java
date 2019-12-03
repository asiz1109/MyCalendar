package com.example.mycalendar;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.preference.PreferenceManager;
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

import com.example.mycalendar.Alarm.MyReceiver;
import com.example.mycalendar.BD.DBHelper;
import com.example.mycalendar.BD.Event;
import com.example.mycalendar.RecyclerView.AdapterListener;
import com.example.mycalendar.RecyclerView.AdapterRV;
import com.example.mycalendar.ViewModel.DateTimeTracker;
import com.example.mycalendar.ViewModel.MainViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class CalendarFragment extends Fragment implements AdapterListener {

    private CalendarView cv_calendar;
    private FloatingActionButton btn_add;
    private MainViewModel mainViewModel;
    private DateTimeTracker dateTimeTracker;
    private RecyclerView recyclerView;
    private AdapterRV adapterRV;
    private List<Event> eventList = new ArrayList<>();
    private DBHelper dbHelper;
    private int position;

    public static final String TAG = String.valueOf(R.layout.fragment_calendar);

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
        dbHelper = DBHelper.getInstance(requireContext());
        SQLiteDatabase database = dbHelper.getReadableDatabase();
        eventList.clear();
        String day = String.valueOf(dateTimeTracker.getDay().getValue());
        String month = String.valueOf(dateTimeTracker.getMonth().getValue());
        String year = String.valueOf(dateTimeTracker.getYear().getValue());
        Cursor cursor = database.query(DBHelper.TABLE_EVENTS, null,
                DBHelper.KEY_DAY + " = ? AND " + DBHelper.KEY_MONTH + " = ? AND " + DBHelper.KEY_YEAR + " = ?", new String[]{day, month, year},
                null, null, null);
        if (cursor.moveToFirst()) {
            do {
                int db_id = cursor.getInt(cursor.getColumnIndex(DBHelper.KEY_ID));
                String db_event = cursor.getString(cursor.getColumnIndex(DBHelper.KEY_EVENT));
                int db_day = cursor.getInt(cursor.getColumnIndex(DBHelper.KEY_DAY));
                int db_month = cursor.getInt(cursor.getColumnIndex(DBHelper.KEY_MONTH));
                int db_year = cursor.getInt(cursor.getColumnIndex(DBHelper.KEY_YEAR));
                int db_hour = cursor.getInt(cursor.getColumnIndex(DBHelper.KEY_HOUR));
                int db_minute = cursor.getInt(cursor.getColumnIndex(DBHelper.KEY_MINUTE));
                int db_all_day = cursor.getInt(cursor.getColumnIndex(DBHelper.KEY_ALL_DAY));
                int db_remind = cursor.getInt(cursor.getColumnIndex(DBHelper.KEY_REMIND));
                eventList.add(new Event(db_id, db_event, db_day, db_month, db_year, db_hour, db_minute, db_all_day, db_remind));
            } while (cursor.moveToNext());
        }
        cursor.close();
        dbHelper.close();
        adapterRV.setList(eventList);
    }

    @Override
    public void onItemClick(final Event event, final int position) {
        this.position = position;
        new AlertDialog.Builder(requireContext())
                .setMessage(R.string.share)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        share(eventList.get(position));
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                }).show();
    }

    @Override
    public void onItemLongClick(Event event, int position) {
        this.position = position;
        new AlertDialog.Builder(requireContext())
                .setMessage(R.string.delete)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteEvent();
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                }).show();
    }

    private void deleteEvent(){
        Event event = eventList.get(position);
        if(event.getRemind()>0) {
            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(requireContext());
            String idAlarm = sp.getString(event.getDay() + "." + event.getMonth() + "." + event.getYear() + "." + event.getHour() + "." + event.getMinute(), "");
            AlarmManager alarmManager = (AlarmManager) Objects.requireNonNull(getActivity()).getSystemService(Context.ALARM_SERVICE);
            Intent intent = new Intent(requireContext(), MyReceiver.class);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(requireContext(), Integer.parseInt(idAlarm), intent, 0);
            Objects.requireNonNull(alarmManager).cancel(pendingIntent);
        }

        dbHelper = DBHelper.getInstance(requireContext());
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        database.delete(DBHelper.TABLE_EVENTS, DBHelper.KEY_ID + " = " + event.getId(), null);
        dbHelper.close();
        getListOfTheDay();
    }

    private void share(Event event){
        String month = String.valueOf(event.getMonth()+1);
        String editMonth = month.length()<2 ? "0"+month : month;
        String hour = String.valueOf(event.getHour()).length()<2 ? "0"+event.getHour() : String.valueOf(event.getHour());
        String minute = String.valueOf(event.getMinute()).length()<2 ? "0"+event.getMinute() : String.valueOf(event.getMinute());
        String toSend = String.format(Locale.US,"Event: %s - %d.%s.%d at %s:%s", event.getEvent(), event.getDay(), editMonth, event.getYear(), hour, minute);
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, toSend);
        sendIntent.setType("text/plain");
        startActivity(sendIntent);
    }
}
