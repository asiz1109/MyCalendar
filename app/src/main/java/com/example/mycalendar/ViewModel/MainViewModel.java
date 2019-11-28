package com.example.mycalendar.ViewModel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.mycalendar.R;

public class MainViewModel extends ViewModel {

    private MutableLiveData<String> mTitle = new MutableLiveData<>();
    private MutableLiveData<Integer> mFragmentId = new MutableLiveData<>();

    public MainViewModel(){
        mFragmentId.postValue(R.layout.fragment_calendar);
    }

    public void setTitle(String title){
        mTitle.postValue(title);
    }

    public LiveData<String> getTitle(){
        return mTitle;
    }

    public LiveData<Integer> getFragmentId(){
        return mFragmentId;
    }

    public void setFragmentId(int id) {
            mFragmentId.postValue(id);
    }

}
