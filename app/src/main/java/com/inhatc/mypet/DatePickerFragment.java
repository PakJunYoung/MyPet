package com.inhatc.mypet;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.Gravity;
import android.widget.DatePicker;

import java.util.Calendar;

public class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current date as the default date in the picker

        final Calendar c = Calendar.getInstance();      // 오늘 날짜로 디폴트값을 설정하기 위해 캘린더 객체 선언

        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);              // MONTH : 0~11
        int day = c.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog datepicker = new DatePickerDialog(getActivity(), this, year, month, day);
        datepicker.getDatePicker().setMaxDate(c.getTimeInMillis());
        // Create a new instance of DatePickerDialog and return it
        return datepicker; // this는 리스너를 가르키는데 이 프래그먼트 클래스 자신을 가리킨다.
    }
    @Override
    public void onDateSet(android.widget.DatePicker view, int year, int month, int dayOfMonth) {
        pet_add add_activity = (pet_add) getActivity();
        add_activity.pet_birth.setText(year+"년 "+(month+1)+"월 "+ dayOfMonth + "일");
    }
}