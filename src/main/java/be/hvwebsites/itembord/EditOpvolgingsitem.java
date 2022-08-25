package be.hvwebsites.itembord;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ContentResolver;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import be.hvwebsites.itembord.adapters.TextItemListAdapter;
import be.hvwebsites.itembord.constants.FrequentieDateUnit;
import be.hvwebsites.itembord.constants.SpecificData;
import be.hvwebsites.itembord.entities.Log;
import be.hvwebsites.itembord.entities.Opvolgingsitem;
import be.hvwebsites.itembord.entities.Rubriek;
import be.hvwebsites.itembord.fragments.DatePickerFragment;
import be.hvwebsites.itembord.fragments.FlexDialogFragment;
import be.hvwebsites.itembord.interfaces.DatePickerInterface;
import be.hvwebsites.itembord.interfaces.FlexDialogInterface;
import be.hvwebsites.itembord.services.CalenderService;
import be.hvwebsites.itembord.viewmodels.EntitiesViewModel;
import be.hvwebsites.libraryandroid4.helpers.DateString;
import be.hvwebsites.libraryandroid4.helpers.IDNumber;
import be.hvwebsites.libraryandroid4.helpers.ListItemHelper;
import be.hvwebsites.libraryandroid4.returninfo.ReturnInfo;
import be.hvwebsites.libraryandroid4.statics.StaticData;

public class EditOpvolgingsitem extends AppCompatActivity
        implements DatePickerInterface, FlexDialogInterface {
    private EntitiesViewModel viewModel;
    private List<ListItemHelper> itemList = new ArrayList<>();
    private int iDToUpdate = StaticData.ITEM_NOT_FOUND;
    private String action = StaticData.ACTION_NEW;
    private RadioButton frequencyDayV;
    private RadioButton frequencyWeekV;
    private RadioButton frequencyMonthV;
    private RadioButton frequencyYearV;
    private EditText opvolgingsitemLatestDateV;
    private Log newLog;
    private CalenderService calenderService;
    private boolean continueFlag = false;
    // Declareer rubriek van opvolgingsitem
    Rubriek rubriekOpvolgingsitem = new Rubriek();
    // Declareer opvolgingsitem to update
    Opvolgingsitem opvolgingsitemToUpdate = new Opvolgingsitem();
    // Declareer opvolgingsitem waarvoor een log moet aangemaakt worden
    Opvolgingsitem opvolgingsitemPastProcess = new Opvolgingsitem();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_opvolgingsitem);

        // Initialiseer invulvelden
        TextView rubriekNameV = findViewById(R.id.valueItemRubriek);
        EditText opvolgingsitemNameV = findViewById(R.id.editNameItem);
        EditText opvolgingsitemNamePastV = findViewById(R.id.editItemNamePast);
        EditText opvolgingsitemFrequencyV = findViewById(R.id.editItemFreqa);

        frequencyDayV = findViewById(R.id.radioButtonFreqDag);
        frequencyWeekV = findViewById(R.id.radioButtonFreqWeek);
        frequencyMonthV = findViewById(R.id.radioButtonFreqMaand);
        frequencyYearV = findViewById(R.id.radioButtonFreqJaar);
        opvolgingsitemLatestDateV = findViewById(R.id.editItemLatestDate);

        // Get a viewmodel from the viewmodelproviders
        viewModel = new ViewModelProvider(this).get(EntitiesViewModel.class);
        // Basis directory definitie
        String baseDir = getBaseContext().getExternalFilesDir(null).getAbsolutePath();
        // Initialize viewmodel mt basis directory (data wordt opgehaald in viewmodel)
        ReturnInfo viewModelStatus = viewModel.initializeViewModel(baseDir);
        if (viewModelStatus.getReturnCode() == 0) {
            // Files gelezen
        } else if (viewModelStatus.getReturnCode() == 100) {
            Toast.makeText(EditOpvolgingsitem.this,
                    viewModelStatus.getReturnMessage(),
                    Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(EditOpvolgingsitem.this,
                    "Ophalen data is mislukt",
                    Toast.LENGTH_LONG).show();
        }

        // Intent bekijken vr action en evt rubriek (indien new opvolgingsitem)
        Intent editIntent = getIntent();
        action = editIntent.getStringExtra(StaticData.EXTRA_INTENT_KEY_ACTION);

        // Als op datum veld geclickt wordt...
        opvolgingsitemLatestDateV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePicker(v);
            }
        });

        // Radiobuttons for frequentie eenheid
        frequencyDayV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onRadioButtonClicked(v);
            }
        });
        frequencyWeekV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onRadioButtonClicked(v);
            }
        });
        frequencyMonthV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onRadioButtonClicked(v);
            }
        });
        frequencyYearV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onRadioButtonClicked(v);
            }
        });

        // Vul Scherm in vlgns new/update
        switch (action) {
            case StaticData.ACTION_NEW:
                setTitle(SpecificData.TITLE_NEW_OPVOLGINGSITEM);
                // Bepaal rubriek uit intent
                IDNumber rubriekId = new IDNumber(editIntent.getIntExtra(SpecificData.ID_RUBRIEK, 0));
                rubriekOpvolgingsitem.setRubriek(viewModel.getRubriekById(rubriekId));
                break;
            case StaticData.ACTION_UPDATE:
                setTitle(SpecificData.TITLE_UPDATE_OPVOLGINGSITEM);
                // ID uit intent halen om te weten welk element moet aangepast worden
                iDToUpdate = editIntent.getIntExtra(StaticData.EXTRA_INTENT_KEY_ID,
                        StaticData.ITEM_NOT_FOUND);
                opvolgingsitemToUpdate.setOpvolgingsitem(viewModel.getOpvolgingsitemById(new IDNumber(iDToUpdate)));
                // rubriek uit opvolgingsitem halen
                rubriekOpvolgingsitem.setRubriek(viewModel.getRubriekById(opvolgingsitemToUpdate.getRubriekId()));

                // Invullen vn gegevens opvolgingsitem op scherm
                opvolgingsitemNameV.setText(opvolgingsitemToUpdate.getEntityName());
                opvolgingsitemNamePastV.setText(opvolgingsitemToUpdate.getEntityNamePast());
                opvolgingsitemFrequencyV.setText(String.valueOf(opvolgingsitemToUpdate.getFrequentieNbr()));
                switch (opvolgingsitemToUpdate.getFrequentieUnit()){
                    case DAYS:
                        setFrequencyDay();
                        break;
                    case WEEKS:
                        setFrequencyWeek();
                        break;
                    case MONTHS:
                        setFrequencyMonth();
                        break;
                    case YEARS:
                        setFrequencyYear();
                        break;
                    default:
                        setFrequencyDay();
                }
                opvolgingsitemLatestDateV.setText(opvolgingsitemToUpdate.getLatestDate().getFormatDate());

                // Recyclerview voor logboek definieren
                RecyclerView recyclerView = findViewById(R.id.recycler_edit_item);
                final TextItemListAdapter adapter = new TextItemListAdapter(this);
                recyclerView.setAdapter(adapter);
                recyclerView.setLayoutManager(new LinearLayoutManager(this));
                itemList.clear();
                // Opvullen recycler list met logitems vr opvolgingsitem in kwestie
                itemList.addAll(viewModel.getLogItemListByOItemID(opvolgingsitemToUpdate.getEntityId()));
                // om te kunnen swipen in de recyclerview ; swippen == deleten
                ItemTouchHelper helper = new ItemTouchHelper(
                        new ItemTouchHelper.SimpleCallback(0,
                                ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
                            @Override
                            public boolean onMove(@NonNull RecyclerView recyclerView,
                                                  @NonNull RecyclerView.ViewHolder viewHolder,
                                                  @NonNull RecyclerView.ViewHolder target) {
                                return false;
                            }

                            @Override
                            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                                Toast.makeText(EditOpvolgingsitem.this,
                                        "Deleting item ... ",
                                        Toast.LENGTH_LONG).show();
                                // Bepalen entity IDNumber to be deleted
                                int position = viewHolder.getAdapterPosition();
                                IDNumber idNumberToBeDeleted = itemList.get(position).getItemID();
                                // Leegmaken itemlist
                                itemList.clear();
                                // Delete logitem
                                viewModel.deleteLogByID(idNumberToBeDeleted);
                                itemList.addAll(viewModel.getLogItemListByOItemID(opvolgingsitemToUpdate.getEntityId()));
                                // Refresh recyclerview
                                adapter.setEntityType(SpecificData.ENTITY_TYPE_LOG);
                                adapter.setCallingActivity(SpecificData.ENTITY_TYPE_OPVOLGINGSITEM);
                                adapter.setItemList(itemList);
                            }
                        });
                helper.attachToRecyclerView(recyclerView);
                adapter.setEntityType(SpecificData.ENTITY_TYPE_LOG);
                adapter.setCallingActivity(SpecificData.ENTITY_TYPE_OPVOLGINGSITEM);
                adapter.setItemList(itemList);

                // FloatingActionButton om een logitem toe te voegen
                FloatingActionButton fab = findViewById(R.id.fab_edit_item);
                fab.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // vul in met editlogitem activity
                        Intent manIntent = new Intent(EditOpvolgingsitem.this, EditLog.class);
                        manIntent.putExtra(StaticData.EXTRA_INTENT_KEY_ACTION, StaticData.ACTION_NEW);
                        manIntent.putExtra(StaticData.EXTRA_INTENT_KEY_RETURN, SpecificData.ENTITY_TYPE_OPVOLGINGSITEM);
                        manIntent.putExtra(SpecificData.ID_RUBRIEK, rubriekOpvolgingsitem.getEntityId().getId());
                        manIntent.putExtra(SpecificData.ID_OPVOLGINGSITEM, opvolgingsitemToUpdate.getEntityId().getId());
                        startActivity(manIntent);
                    }
                });
                break;
        }
        if (rubriekOpvolgingsitem.getEntityId() != null) {
            // rubriek is gekend, vul in op scherm
            rubriekNameV.setText(rubriekOpvolgingsitem.getEntityName());
        }

        Button saveButton = findViewById(R.id.buttonSaveItem);
        // Als button ingedrukt wordt...
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // CalenderService activeren
                calenderService = new CalenderService();
                // Gegevens overnemen vh scherm
                if (action.equals(StaticData.ACTION_UPDATE)) { // Update
                    // Update opvolgingsitem to update met gegevens vn scherm
                    opvolgingsitemToUpdate.setEntityName(String.valueOf(opvolgingsitemNameV.getText()));
                    opvolgingsitemToUpdate.setEntityNamePast(String.valueOf(opvolgingsitemNamePastV.getText()));
                    opvolgingsitemToUpdate.setFrequentieNbr(Integer.parseInt(String.valueOf(opvolgingsitemFrequencyV.getText())));
                    if (frequencyDayV.isChecked()){
                        opvolgingsitemToUpdate.setFrequentieUnit(FrequentieDateUnit.DAYS);
                    }else if (frequencyWeekV.isChecked()){
                        opvolgingsitemToUpdate.setFrequentieUnit(FrequentieDateUnit.WEEKS);
                    }else if (frequencyMonthV.isChecked()){
                        opvolgingsitemToUpdate.setFrequentieUnit(FrequentieDateUnit.MONTHS);
                    }else if (frequencyYearV.isChecked()){
                        opvolgingsitemToUpdate.setFrequentieUnit(FrequentieDateUnit.YEARS);
                    }
                    boolean latestDateChanged = false;
                    String newLatestDate = String.valueOf(opvolgingsitemLatestDateV.getText());
                    DateString newLatestDatestring = new DateString(String.valueOf(opvolgingsitemLatestDateV.getText()));
                    if (!opvolgingsitemToUpdate.getLatestDate().getDateString().equals(newLatestDatestring.getDateString())){
                        // Indien latestdate gewijzigd is ...
                        latestDateChanged = true;
                        opvolgingsitemToUpdate.setLatestDate(newLatestDatestring);
                    }
                    // opvolgingsitem in de opvolgingsitemlist aanpassen
                    int opvolgingsitemIndex = viewModel.getItemIndexById(new IDNumber(iDToUpdate));
                    viewModel.getOpvolgingsitemList().get(opvolgingsitemIndex).setOpvolgingsitem(opvolgingsitemToUpdate);
                    if (latestDateChanged){
                        // Als er een laatste opvolgingsdatum is ...
                        // Als er een event op de vorige datum was, dan moet dit verwijderd worden
                        if (opvolgingsitemToUpdate.getEventId() != StaticData.ITEM_NOT_FOUND){
                            ContentResolver cr = getContentResolver();
                            // Bepaal MyCalendarID, dit is enkel om de permissions in orde te brengen
                            long calID = calenderService.getMyCalendarId(
                                    EditOpvolgingsitem.this,
                                    EditOpvolgingsitem.this,
                                    cr);
                            calenderService.deleteEventInMyCalendar(cr, opvolgingsitemToUpdate.getEventId());
                            boolean debug = true;
                        }
                        // Als latestDate is ingevuld, voorstellen om log aan te maken
                        if (!opvolgingsitemToUpdate.getLatestDate().isEmpty()){
                            // Vraag stellen of er een log moet aangemaakt worden
                            opvolgingsitemPastProcess.setOpvolgingsitem(opvolgingsitemToUpdate);
                            createLogSaveDialog();
                        }
                    }
                } else { // New
                    // Creer een nieuw opvolgingsitem en vul met gegevens vn scherm
                    Opvolgingsitem newOItem = new Opvolgingsitem(viewModel.getBasedir(), false);
                    newOItem.setRubriekId(rubriekOpvolgingsitem.getEntityId());
                    newOItem.setEntityName(String.valueOf(opvolgingsitemNameV.getText()));
                    newOItem.setEntityNamePast(String.valueOf(opvolgingsitemNamePastV.getText()));
                    newOItem.setFrequentieNbr(Integer.parseInt(String.valueOf(opvolgingsitemFrequencyV.getText())));
                    newOItem.setLatestDate(new DateString(String.valueOf(opvolgingsitemLatestDateV.getText())));
                    if (frequencyDayV.isChecked()){
                        newOItem.setFrequentieUnit(FrequentieDateUnit.DAYS);
                    }else if (frequencyWeekV.isChecked()){
                        newOItem.setFrequentieUnit(FrequentieDateUnit.WEEKS);
                    }else if (frequencyMonthV.isChecked()){
                        newOItem.setFrequentieUnit(FrequentieDateUnit.MONTHS);
                    }else if (frequencyYearV.isChecked()){
                        newOItem.setFrequentieUnit(FrequentieDateUnit.YEARS);
                    }
                    // Toevoegen opvolgingsitem aan opvolgingsitemlist
                    viewModel.getOpvolgingsitemList().add(newOItem);
                    // Als er een laatste opvolgingsdatum is ...
                    if (!newOItem.getLatestDate().isEmpty()){
                        // Voorstellen om een log aan te maken
                        opvolgingsitemPastProcess.setOpvolgingsitem(newOItem);
                        createLogSaveDialog();
                    }
                }
                // Bewaren opvolgingsitems
                viewModel.storeItems();

                // Teruggaan nr EditRubriek weggehaald
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

        opvolgingsitemLatestDateV.setText(dateMessage);

        Toast.makeText(this, "Date: " + dateMessage, Toast.LENGTH_SHORT).show();
    }

    private void createLogSaveDialog(){
        // Create an instance of the dialog fragment and show it
        FlexDialogFragment logDialog = new FlexDialogFragment();
        logDialog.setSubjectDialog("Log");
        logDialog.setTitleDialog("Wenst u een log aan te maken ?");
        logDialog.setMsgDialog("Click JA of NEE: ");
        logDialog.show(getSupportFragmentManager(), "LogDialogFragment");
    }

    // The dialog fragment receives a reference to this Activity through the
    // Fragment.onAttach() callback, which it uses to call the following methods
    // defined by the LogDialogFragment.LogDialogInterface interface
    @Override
    public void onLogDialogPositiveClick(DialogFragment dialogFragment) {
        // Maak alvast de log aan
        newLog = new Log(viewModel.getBasedir(), false);
        newLog.setLogDate(opvolgingsitemPastProcess.getLatestDate());
        // entityNamePAst gebruiken om log aan te maken
        newLog.setLogDescription(opvolgingsitemPastProcess.getEntityNamePast());
        newLog.setRubriekId(opvolgingsitemPastProcess.getRubriekId());
        newLog.setItemId(opvolgingsitemPastProcess.getEntityId());
        // de log moet bewaard worden !
        viewModel.getLogList().add(newLog);
        viewModel.storeLogs();

        Toast.makeText(getApplicationContext(), "Pressed JA",
                Toast.LENGTH_SHORT).show();

        // is frequentie ingevuld
        if ((opvolgingsitemPastProcess.getFrequentieNbr() > 0) &&
                (opvolgingsitemPastProcess.getFrequentieUnit() != null)) {
            // Vraag stellen om in agenda te registreren,
            // let op eventId in opvolgingsitem moet nog aangepast wordeninvullen
            createEventSaveDialog();
        }
    }

    @Override
    public void onLogDialogNegativeClick(DialogFragment dialogFragment) {
        // User clicked NEE button, er moet niets gebeuren
        Toast.makeText(getApplicationContext(), "Pressed NEE",
                Toast.LENGTH_SHORT).show();

        // In dialogfragment zit niets bruikbaar

        // is frequentie ingevuld
        if ((opvolgingsitemPastProcess.getFrequentieNbr() > 0) &&
                (opvolgingsitemPastProcess.getFrequentieUnit() != null)){
            // Vraag stellen om in agenda te registreren,
            // let op eventId in opvolgingsitem moet nog aangepast wordeninvullen
            createEventSaveDialog();
        }

    }

    private void createEventSaveDialog(){
        // Create an instance of the dialog fragment and show it
        FlexDialogFragment eventDialog = new FlexDialogFragment();
        eventDialog.setSubjectDialog("Event");
        eventDialog.setTitleDialog("Wenst u een event in de agenda aan te maken ?");
        eventDialog.setMsgDialog("Click JA of NEE: ");
        eventDialog.show(getSupportFragmentManager(), "EventDialogFragment");
    }

    @Override
    public void onEventDialogPositiveClick(DialogFragment dialogFragment) {
        // User clicked JA button, er wordt een event aangemaakt
        ContentResolver cr = getContentResolver();
        // Bepaal MyCalendarID
        long calID = calenderService.getMyCalendarId(
                EditOpvolgingsitem.this,
                EditOpvolgingsitem.this,
                cr);
        // Bepaal volgende opvolgingsdatum
        long eventDateMs = opvolgingsitemPastProcess.calculateNextDate().getCalendarDate().getTimeInMillis();
        // Event om 10h in de vm zetten 10*3600*1000 bijtellen
        eventDateMs = eventDateMs + (10*3600*1000);
        // Maak een event in de agenda
        final long eventId = calenderService.createEventInMyCalendar(
                cr,
                calID,
                eventDateMs,
                eventDateMs,
                opvolgingsitemPastProcess.getEntityName());
        // eventId bewaren in opvolgingsitem
        opvolgingsitemPastProcess.setEventId(eventId);
        int itemIndex = viewModel.getItemIndexById(opvolgingsitemPastProcess.getEntityId());
        viewModel.getOpvolgingsitemList().get(itemIndex).setEventId(eventId);
        viewModel.storeItems();

        // Reminder creeren voor het event
        calenderService.createReminderForEvent(cr, eventId);

        Toast.makeText(getApplicationContext(), "Pressed JA",
                Toast.LENGTH_SHORT).show();

        // Teruggaan nr EditRubriek
        Intent replyIntent = new Intent(EditOpvolgingsitem.this, EditRubriek.class);
        replyIntent.putExtra(StaticData.EXTRA_INTENT_KEY_ACTION, StaticData.ACTION_UPDATE);
        replyIntent.putExtra(StaticData.EXTRA_INTENT_KEY_ID, rubriekOpvolgingsitem.getEntityId().getId());
        startActivity(replyIntent);
    }

    @Override
    public void onEventDialogNegativeClick(DialogFragment dialogFragment) {
        // User clicked NEE button, er moet niets gebeuren
        Toast.makeText(getApplicationContext(), "Pressed NEE",
                Toast.LENGTH_SHORT).show();

        // Teruggaan nr EditRubriek
        Intent replyIntent = new Intent(EditOpvolgingsitem.this, EditRubriek.class);
        replyIntent.putExtra(StaticData.EXTRA_INTENT_KEY_ACTION, StaticData.ACTION_UPDATE);
        replyIntent.putExtra(StaticData.EXTRA_INTENT_KEY_ID, rubriekOpvolgingsitem.getEntityId().getId());
        startActivity(replyIntent);
    }

    public void onRadioButtonClicked(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch(view.getId()) {
            case R.id.radioButtonFreqDag:
                // Dag is gekozen
                if (checked){
                    // Andere radiobuttons afchecken
                    setFrequencyDay();
                    boolean debug = false;
                }
                break;
            case R.id.radioButtonFreqWeek:
                // Week is gekozen
                if (checked){
                    // Andere radiobuttons afchecken
                    setFrequencyWeek();
                    boolean debug = true;
                }
                break;
            case R.id.radioButtonFreqMaand:
                // Week is gekozen
                if (checked){
                    // Andere radiobuttons afchecken
                    setFrequencyMonth();
                    boolean debug = true;
                }
                break;
            case R.id.radioButtonFreqJaar:
                // Week is gekozen
                if (checked){
                    // Andere radiobuttons afchecken
                    setFrequencyYear();
                    boolean debug = true;
                }
                break;
        }
    }

    private void setFrequencyDay(){
        frequencyDayV.setChecked(true);
        frequencyWeekV.setChecked(false);
        frequencyMonthV.setChecked(false);
        frequencyYearV.setChecked(false);
    }

    private void setFrequencyWeek(){
        frequencyDayV.setChecked(false);
        frequencyWeekV.setChecked(true);
        frequencyMonthV.setChecked(false);
        frequencyYearV.setChecked(false);
    }

    private void setFrequencyMonth(){
        frequencyDayV.setChecked(false);
        frequencyWeekV.setChecked(false);
        frequencyMonthV.setChecked(true);
        frequencyYearV.setChecked(false);
    }

    private void setFrequencyYear(){
        frequencyDayV.setChecked(false);
        frequencyWeekV.setChecked(false);
        frequencyMonthV.setChecked(false);
        frequencyYearV.setChecked(true);
    }

}