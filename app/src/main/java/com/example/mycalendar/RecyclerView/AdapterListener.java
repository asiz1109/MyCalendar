package com.example.mycalendar.RecyclerView;

import com.example.mycalendar.BD.Event;

public interface AdapterListener {

    void onItemClick(Event event, int position);

    void onItemLongClick(Event event, int position);
}
