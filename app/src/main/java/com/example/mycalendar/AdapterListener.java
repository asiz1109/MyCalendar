package com.example.mycalendar;

public interface AdapterListener {

    void onItemClick(Event event, int position);

    void onItemLongClick(Event event, int position);
}
