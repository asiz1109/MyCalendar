package com.example.mycalendar.ViewModel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.Calendar;

public class DateTimeTracker extends ViewModel {

    private MutableLiveData<Integer> mYear = new MutableLiveData<>();
    private MutableLiveData<Integer> mMonth = new MutableLiveData<>();
    private MutableLiveData<Integer> mDay = new MutableLiveData<>();
    private MutableLiveData<Integer> mHour = new MutableLiveData<>();
    private MutableLiveData<Integer> mMinute = new MutableLiveData<>();

    public DateTimeTracker(){
        setDefaultDate();
    }

    public LiveData<Integer> getYear() {
        return mYear;
    }

    public void setYear(int year) {
        mYear.postValue(year);
    }

    public LiveData<Integer> getMonth() {
        return mMonth;
    }

    public void setMonth(int month) {
        mMonth.postValue(month);
    }

    public LiveData<Integer> getDay() {
        return mDay;
    }

    public void setDay(int day) {
        mDay.postValue(day);
    }

    public LiveData<Integer> getHour() {
        return mHour;
    }

    public void setHour(int hour) {
        mHour.postValue(hour);
    }

    public LiveData<Integer> getMinute() {
        return mMinute;
    }

    public void setMinute(int minute) {
        mMinute.postValue(minute);
    }

    public void setDefaultDate(){
        mYear.postValue(Calendar.getInstance().get(Calendar.YEAR));
        mMonth.postValue(Calendar.getInstance().get(Calendar.MONTH));
        mDay.postValue(Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
    }

    public void setDate(int year, int month, int day){
        mYear.postValue(year);
        mMonth.postValue(month);
        mDay.postValue(day);
    }

    public void setTime(int hour, int minute){
        mHour.postValue(hour);
        mMinute.postValue(minute);
    }
}
