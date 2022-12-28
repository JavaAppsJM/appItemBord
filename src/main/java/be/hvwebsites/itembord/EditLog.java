package be.hvwebsites.itembord;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import be.hvwebsites.itembord.constants.SpecificData;
import be.hvwebsites.itembord.entities.Log;
import be.hvwebsites.itembord.entities.Opvolgingsitem;
import be.hvwebsites.itembord.entities.Rubriek;
import be.hvwebsites.itembord.fragments.DatePickerFragment;
import be.hvwebsites.itembord.interfaces.DatePickerInterface;
import be.hvwebsites.itembord.services.FileBaseService;
import be.hvwebsites.itembord.viewmodels.EntitiesViewModel;
import be.hvwebsites.libraryandroid4.helpers.DateString;
import be.hvwebsites.libraryandroid4.helpers.IDNumber;
import be.hvwebsites.libraryandroid4.helpers.ListItemHelper;
import be.hvwebsites.libraryandroid4.returninfo.ReturnInfo;
import be.hvwebsites.libraryandroid4.statics.StaticData;

public class EditLog extends AppCompatActivity implements DatePickerInterface {
    private EntitiesViewModel viewModel;
    private List<ListItemHelper> itemList = new ArrayList<>();
    private int iDToUpdate = StaticData.ITEM_NOT_FOUND;
    private String action = StaticData.ACTION_NEW;
    private String callingActivity = SpecificData.ENTITY_TYPE_RUBRIEK;
    private EditText logitemDateV;
    private Log newLog;
    // Device
    private final String deviceModel = Build.MODEL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_log);

        // Declareer rubriek van logitem
        Rubriek rubriekLogitem = new Rubriek();

        // Declareer opvolgingsitem van logitem
        Opvolgingsitem opvolgingsitemLogitem = new Opvolgingsitem();
        opvolgingsitemLogitem.setEntityId(StaticData.IDNUMBER_NOT_FOUND);

        // Declareer logitem to update
        Log logitemToUpdate = new Log();

        // Initialiseer invulvelden
        TextView rubriekNameV = findViewById(R.id.nameLogRubriek);
        TextView opvolgingsitemNameV = findViewById(R.id.nameLogOpvolgingsitem);
        EditText logDescriptionV = findViewById(R.id.editLogDescription);

        logitemDateV = findViewById(R.id.editItemLogDate);

        // Creer een filebase service (bevat file base en file base directory) obv device en package name
        FileBaseService fileBaseService = new FileBaseService(deviceModel, getPackageName());

        // Get a viewmodel from the viewmodelproviders
        viewModel = new ViewModelProvider(this).get(EntitiesViewModel.class);
        // Basis directory definitie
        String baseDir = fileBaseService.getFileBaseDir();
        // Initialize viewmodel mt basis directory (data wordt opgehaald in viewmodel)
        ReturnInfo viewModelStatus = viewModel.initializeViewModel(baseDir);
        if (viewModelStatus.getReturnCode() != 0) {
            Toast.makeText(EditLog.this,
                    "Ophalen data is mislukt",
                    Toast.LENGTH_LONG).show();
        }

        // Intent bekijken vr action en evt rubriek en/of opvolgingsitem (indien new logitem)
        Intent editIntent = getIntent();
        action = editIntent.getStringExtra(StaticData.EXTRA_INTENT_KEY_ACTION);
        // Bepaal nr welke activiteit teruggeaan moet worden
        callingActivity = editIntent.getStringExtra(StaticData.EXTRA_INTENT_KEY_RETURN);

        // Als op datum veld geclickt wordt...
        logitemDateV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePicker(v);
            }
        });

        // Vul Scherm in vlgns new/update
        switch (action) {
            case StaticData.ACTION_NEW:
                setTitle(SpecificData.TITLE_NEW_LOG);
                // Bepaal rubriek uit intent
                IDNumber rubriekId = new IDNumber(editIntent.getIntExtra(SpecificData.ID_RUBRIEK, 0));
                rubriekLogitem.setRubriek(viewModel.getRubriekById(rubriekId));
                // Bepaal opvolgingsitem uit intent indien meegegeven
                if (String.valueOf(editIntent.getStringExtra(StaticData.EXTRA_INTENT_KEY_RETURN))
                        .equals(String.valueOf(SpecificData.ENTITY_TYPE_OPVOLGINGSITEM))){
                    IDNumber opvolgingsitemId = new IDNumber(editIntent.getIntExtra(SpecificData.ID_OPVOLGINGSITEM, 0));
                    opvolgingsitemLogitem.setOpvolgingsitem(viewModel.getOpvolgingsitemById(opvolgingsitemId));
                }
                break;
            case StaticData.ACTION_UPDATE:
                setTitle(SpecificData.TITLE_UPDATE_LOG);
                // ID uit intent halen om te weten welk element moet aangepast worden
                iDToUpdate = editIntent.getIntExtra(StaticData.EXTRA_INTENT_KEY_ID,
                        StaticData.ITEM_NOT_FOUND);
                logitemToUpdate.setLog(viewModel.getLogById(new IDNumber(iDToUpdate)));
                // rubriek uit logitem halen
                rubriekLogitem.setRubriek(viewModel.getRubriekById(logitemToUpdate.getRubriekId()));

                // opvolgingsitem uit logitem halen
                if (logitemToUpdate.getItemId().getId() != StaticData.IDNUMBER_NOT_FOUND.getId()){
                    opvolgingsitemLogitem.setOpvolgingsitem(viewModel.getOpvolgingsitemById(logitemToUpdate.getItemId()));
                }else {
                    opvolgingsitemLogitem.setEntityId(StaticData.IDNUMBER_NOT_FOUND);
                }
                // Invullen vn logdate en log description op scherme
                logitemDateV.setText(logitemToUpdate.getLogDate().getFormatDate());
                logDescriptionV.setText(logitemToUpdate.getLogDescription());
                break;
        }
        // Invullen vn rubriek en evt opvolgingsitem op scherm
        rubriekNameV.setText(rubriekLogitem.getEntityName());
        if (opvolgingsitemLogitem.getEntityId() != StaticData.IDNUMBER_NOT_FOUND){
            opvolgingsitemNameV.setText(opvolgingsitemLogitem.getEntityName());
        }

        Button saveButton = findViewById(R.id.buttonSaveLog);
        // Als button ingedrukt wordt...
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Gegevens overnemen vh scherm
                if (action.equals(StaticData.ACTION_UPDATE)) { // Update
                    // Update logitem to update met gegevens vn scherm
                    DateString newLogDatestring = new DateString(String.valueOf(logitemDateV.getText()));
                    if (!logitemToUpdate.getLogDate().getDateString().equals(newLogDatestring.getDateString())){
                        // Indien logdate gewijzigd is ...
                        logitemToUpdate.setLogDate(newLogDatestring);
                    }
                    logitemToUpdate.setLogDescription(String.valueOf(logDescriptionV.getText()));

                    // logitem in de loglist aanpassen
                    int logitemIndex = viewModel.getLogIndexById(new IDNumber(iDToUpdate));
                    viewModel.getLogList().get(logitemIndex).setLog(logitemToUpdate);
                } else { // New
                    // Creer een nieuw logitem en vul met gegevens vn scherm
                    Log newLogItem = new Log(viewModel.getBasedir(), false);
                    newLogItem.setRubriekId(rubriekLogitem.getEntityId());
                    newLogItem.setItemId(opvolgingsitemLogitem.getEntityId());
                    newLogItem.setLogDescription(String.valueOf(logDescriptionV.getText()));
                    newLogItem.setLogDate(new DateString(String.valueOf(logitemDateV.getText())));

                    // Toevoegen logitem aan loglist
                    viewModel.getLogList().add(newLogItem);
                }
                // Bewaren logitem
                viewModel.storeLogs();

                // Teruggaan nr EditRubriek of EditOpvolgingsitem
                Intent replyIntent = null;
                if (callingActivity.equals(SpecificData.ENTITY_TYPE_RUBRIEK)){
                    replyIntent = new Intent(EditLog.this, EditRubriek.class);
                    replyIntent.putExtra(StaticData.EXTRA_INTENT_KEY_ID, rubriekLogitem.getEntityId().getId());
                }else if (callingActivity.equals(SpecificData.ENTITY_TYPE_OPVOLGINGSITEM)){
                    replyIntent = new Intent(EditLog.this, EditOpvolgingsitem.class);
                    replyIntent.putExtra(StaticData.EXTRA_INTENT_KEY_ID, opvolgingsitemLogitem.getEntityId().getId());
                }else {// TODO:Als je van logboek komt

                }
                replyIntent.putExtra(StaticData.EXTRA_INTENT_KEY_ACTION, StaticData.ACTION_UPDATE);
                startActivity(replyIntent);
            }
        });
    }

    @Override
    public void showDatePicker(View view) {
        // Toont de datum picker, de gebruiker kan nu de datum picken
        DialogFragment newFragment = new DatePickerFragment();
        Bundle bundle = new Bundle();
        bundle.putString("Caller", "Calling activity place holder");
        newFragment.setArguments(bundle);
        FragmentManager dateFragmentMgr = getSupportFragmentManager();
        newFragment.show(dateFragmentMgr, "datePicker");
    }

    @Override
    public void processDatePickerResult(int year, int month, int day) {
        // Verwerkt de gekozen datum uit de picker
        String month_string = Integer.toString(month+1);
        String day_string = Integer.toString(day);
        String year_string = Integer.toString(year);
        String dateMessage = (day_string +
                "/" + month_string + "/" + year_string);

        logitemDateV.setText(dateMessage);

        Toast.makeText(this, "Date: " + dateMessage, Toast.LENGTH_SHORT).show();
    }
}