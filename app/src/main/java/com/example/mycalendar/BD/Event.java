package com.example.mycalendar.BD;

import com.example.mycalendar.Remind;

public class Event {

    private int id;
    private String event;
    private int day;
    private int month;
    private int year;
    private int hour;
    private int minute;
    private int allDay;
    private Remind remind;
    private int idAlarm;

    public Event(int id, String event, int day, int month, int year, int hour, int minute, int allDay, Remind remind, int idAlarm) {
        this.id = id;
        this.event = event;
        this.day = day;
        this.month = month;
        this.year = year;
        this.hour = hour;
        this.minute = minute;
        this.allDay = allDay;
        this.remind = remind;
        this.idAlarm = idAlarm;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getEvent() {
        return event;
    }

    public void setEvent(String event) {
        this.event = event;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getHour() {
        return hour;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public int getMinute() {
        return minute;
    }

    public void setMinute(int minute) {
        this.minute = minute;
    }

    public int getAllDay() {
        return allDay;
    }

    public void setAllDay(int allDay) {
        this.allDay = allDay;
    }

    public Remind getRemind() {
        return remind;
    }

    public void setRemind(Remind remind) {
        this.remind = remind;
    }

    public int getIdAlarm() {
        return idAlarm;
    }

    public void setIdAlarm(int idAlarm) {
        this.idAlarm = idAlarm;
    }
}
