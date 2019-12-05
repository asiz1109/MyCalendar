package com.example.mycalendar;

public enum Remind {

    notRemind(0), remind_10minuts(1), remind_30minuts(3), remind_1hour(4);

    private int number;

    Remind(int number) {
        this.number = number;
    }

    public int getNumber() {
        return number;
    }
}
