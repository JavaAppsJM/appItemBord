package be.hvwebsites.itembord.fragments;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import be.hvwebsites.itembord.interfaces.FlexDialogInterface;

public class FlexDialogFragment extends DialogFragment {
    private String subjectDialog;
    private String titleDialog;
    private String msgDialog;
    private FlexDialogInterface flexDialogInterface;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the LogDialogListener so we can send events to the host
            flexDialogInterface = (FlexDialogInterface) context;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException("Calling activity"
                    + " must implement NoticeDialogListener");
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        // TODO: rekening houden met subject om positive en negative click op te vangen
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        if (subjectDialog.equals("Log")){
            builder.setMessage(msgDialog)
                    .setTitle(titleDialog)
                    .setPositiveButton("JA", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            flexDialogInterface.onLogDialogPositiveClick(FlexDialogFragment.this);
                        }
                    })
                    .setNegativeButton("NEE", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            flexDialogInterface.onLogDialogNegativeClick(FlexDialogFragment.this);
                        }
                    });
        }else {
            builder.setMessage(msgDialog)
                    .setTitle(titleDialog)
                    .setPositiveButton("JA", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            flexDialogInterface.onEventDialogPositiveClick(FlexDialogFragment.this);
                        }
                    })
                    .setNegativeButton("NEE", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            flexDialogInterface.onEventDialogNegativeClick(FlexDialogFragment.this);
                        }
                    });

        }
        // Create the AlertDialog object and return it
        return builder.create();
    }

    public String getSubjectDialog() {
        return subjectDialog;
    }

    public void setSubjectDialog(String subjectDialog) {
        this.subjectDialog = subjectDialog;
    }

    public String getTitleDialog() {
        return titleDialog;
    }

    public void setTitleDialog(String titleDialog) {
        this.titleDialog = titleDialog;
    }

    public String getMsgDialog() {
        return msgDialog;
    }

    public void setMsgDialog(String msgDialog) {
        this.msgDialog = msgDialog;
    }
}
