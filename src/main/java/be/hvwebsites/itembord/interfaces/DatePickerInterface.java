package be.hvwebsites.itembord.interfaces;

import android.view.View;

public interface DatePickerInterface {
    // Interface om DatePickerFragment te kunnen hergebruiken voor alle new metingen
    public void showDatePicker(View view);
    public void processDatePickerResult(int year, int month, int day);
}
