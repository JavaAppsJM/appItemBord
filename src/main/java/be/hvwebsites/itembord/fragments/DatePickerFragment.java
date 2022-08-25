package be.hvwebsites.itembord.fragments;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.widget.DatePicker;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import java.util.Calendar;

import be.hvwebsites.itembord.interfaces.DatePickerInterface;

public class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {
    public static DatePickerFragment newInstance(String param1, String param2) {
        DatePickerFragment fragment = new DatePickerFragment();
        Bundle args = new Bundle();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState){
        // bepalen meegegeven bundle arguments
        Bundle parmBundle = getArguments();
        String caller = (String) parmBundle.get("Caller");
        // Use the current date as the default date in the picker.
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        // Create a new instance of DatePickerDialog and return it.
        FragmentActivity test = getActivity();
        return new DatePickerDialog(getActivity(), this, year, month, day);
    }
    @Override
    public void onDateSet(DatePicker view, int year, int month, int day) {
        // Geef resultaat aan de calling activity fragment
        FragmentActivity test = getActivity();
        Fragment test4 = getParentFragment();
        Fragment test5 = getParentFragmentManager().getPrimaryNavigationFragment();
        Fragment test6 = getChildFragmentManager().getPrimaryNavigationFragment();
        Fragment test7 = getActivity().getSupportFragmentManager().getPrimaryNavigationFragment();
//        String test8 = getActivity().getCallingActivity().flattenToString();


        DatePickerInterface callingActivityFragment = (DatePickerInterface) getActivity();
        callingActivityFragment.processDatePickerResult(year, month, day);

    }
}
