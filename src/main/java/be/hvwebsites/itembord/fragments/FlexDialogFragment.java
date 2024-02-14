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
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Rekening houden met subject om positive en negative click op te vangen
        switch (subjectDialog){
            case "Log":
                builder.setMessage(msgDialog)
                        .setTitle(titleDialog)
                        .setPositiveButton("JA", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                flexDialogInterface.onLogDialogPositiveClick(FlexDialogFragment.this, subjectDialog);
                            }
                        })
                        .setNegativeButton("NEE", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                flexDialogInterface.onLogDialogNegativeClick(FlexDialogFragment.this);
                            }
                        });
                break;
            case "Event":
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
                break;
            case "Oitem":
                builder.setMessage(msgDialog)
                        .setTitle(titleDialog)
                        .setPositiveButton("JA", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                flexDialogInterface.onOitemDialogPositiveClick(FlexDialogFragment.this);
                            }
                        })
                        .setNegativeButton("NEE", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                flexDialogInterface.onOitemDialogNegativeClick(FlexDialogFragment.this);
                            }
                        });
                break;
            case "Date":
                builder.setMessage(msgDialog)
                        .setTitle(titleDialog)
                        .setPositiveButton("VANDAAG", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                flexDialogInterface.onDateDialogPositiveClick(FlexDialogFragment.this);
                            }
                        })
                        .setNegativeButton("VERVALDATUM", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                flexDialogInterface.onDateDialogNegativeClick(FlexDialogFragment.this);
                            }
                        });
                break;
            case "Skip":
                builder.setMessage(msgDialog)
                        .setTitle(titleDialog)
                        .setPositiveButton("JA", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                flexDialogInterface.onSkipDialogPositiveClick(FlexDialogFragment.this);
                            }
                        })
                        .setNegativeButton("NEE", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                flexDialogInterface.onSkipDialogNegativeClick(FlexDialogFragment.this);
                            }
                        });
                break;
            default:
        }

        // Create the AlertDialog object and return it
        return builder.create();
    }

    public String getSubjectDialog() {
        return subjectDialog;
    }

    public void setSubjectDialog(String subjectDialog) {
        this.subjectDialog = subjectDialog;
        this.msgDialog = "Click JA of NEE: ";
        switch (subjectDialog){
            case "Log":
                this.titleDialog = "Wenst u een log aan te maken ?";
                break;
            case "Event":
                this.titleDialog = "Wenst u een event in de agenda aan te maken ?";
                break;
            case "Oitem":
                this.titleDialog = "Wenst u het opvolgingsitem af te vinken ?";
                break;
            case "Date":
                this.titleDialog = "Wenst u datum uitgevoerd op vandaag of op vervaldatum te zetten ?";
                this.msgDialog = "Click VANDAAG of VERVALDATUM: ";
                break;
            case "Skip":
                this.titleDialog = "Wenst u de uitvoering van het opvolgingsitem uit te stellen ?";
                this.msgDialog = "Click JA of NEE: ";
                break;
            default:
        }
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
