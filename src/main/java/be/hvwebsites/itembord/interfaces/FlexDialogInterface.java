package be.hvwebsites.itembord.interfaces;

import androidx.fragment.app.DialogFragment;

public interface FlexDialogInterface {
    public void onLogDialogPositiveClick(DialogFragment dialogFragment);
    public void onLogDialogNegativeClick(DialogFragment dialogFragment);
    public void onEventDialogPositiveClick(DialogFragment dialogFragment);
    public void onEventDialogNegativeClick(DialogFragment dialogFragment);
}
