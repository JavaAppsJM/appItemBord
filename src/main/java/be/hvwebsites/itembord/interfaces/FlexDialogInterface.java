package be.hvwebsites.itembord.interfaces;

import androidx.fragment.app.DialogFragment;

public interface FlexDialogInterface {
    public void onLogDialogPositiveClick(DialogFragment dialogFragment, String subject);
    public void onLogDialogNegativeClick(DialogFragment dialogFragment);
    public void onEventDialogPositiveClick(DialogFragment dialogFragment);
    public void onEventDialogNegativeClick(DialogFragment dialogFragment);
    public void onOitemDialogPositiveClick(DialogFragment dialogFragment);
    public void onOitemDialogNegativeClick(DialogFragment dialogFragment);
    public void onDateDialogPositiveClick(DialogFragment dialogFragment);
    public void onDateDialogNegativeClick(DialogFragment dialogFragment);
}
