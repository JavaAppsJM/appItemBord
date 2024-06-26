package be.hvwebsites.itembord;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
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
import be.hvwebsites.itembord.services.FileBaseService;
import be.hvwebsites.itembord.viewmodels.EntitiesViewModel;
import be.hvwebsites.libraryandroid4.helpers.DateString;
import be.hvwebsites.libraryandroid4.helpers.IDNumber;
import be.hvwebsites.libraryandroid4.helpers.ListItemHelper;
import be.hvwebsites.libraryandroid4.repositories.CookieRepository;
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
    private RadioGroup radioGroupFrequency;
    private EditText opvolgingsitemLatestDateV;
    private TextItemListAdapter adapter;
    private CalenderService calenderService;
    // Device
    private final String deviceModel = Build.MODEL;

    // Declareer rubriek van opvolgingsitem
    Rubriek rubriekOpvolgingsitem = new Rubriek();
    // Declareer opvolgingsitem to update
    Opvolgingsitem opvolgingsitemToUpdate = new Opvolgingsitem();
    // Declareer opvolgingsitem waarvoor een log moet aangemaakt worden
    Opvolgingsitem opvolgingsitemPastProcess = new Opvolgingsitem();


    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_opvolgingsitem);

        // Initialiseer invulvelden
        TextView rubriekNameV = findViewById(R.id.valueItemRubriek);
        EditText opvolgingsitemNameV = findViewById(R.id.editNameItem);
        EditText opvolgingsitemNamePastV = findViewById(R.id.editItemNamePast);
        EditText opvolgingsitemFrequencyV = findViewById(R.id.editItemFreqa);
        TextView opvolgingsitemLabelFreqUnit = findViewById(R.id.labelItemFrequ);

        frequencyDayV = findViewById(R.id.radioButtonFreqDag);
        frequencyWeekV = findViewById(R.id.radioButtonFreqWeek);
        frequencyMonthV = findViewById(R.id.radioButtonFreqMaand);
        frequencyYearV = findViewById(R.id.radioButtonFreqJaar);
        radioGroupFrequency = findViewById(R.id.radioButtonGroup);
        opvolgingsitemLatestDateV = findViewById(R.id.editItemLatestDate);

        // Creer een filebase service, bepaalt file base directory obv device en Context
        FileBaseService fileBaseService = new FileBaseService(deviceModel, this);

        // Get a viewmodel from the viewmodelproviders
        viewModel = new ViewModelProvider(this).get(EntitiesViewModel.class);
        // Basis directory definitie
        String baseDir = fileBaseService.getFileBaseDir();
        // Initialize viewmodel mt basis directory (data wordt opgehaald in viewmodel)
        List<ReturnInfo> viewModelRetInfo = viewModel.initializeViewModel(baseDir);
        for (int i = 0; i < viewModelRetInfo.size(); i++) {
            Toast.makeText(EditOpvolgingsitem.this,
                    viewModelRetInfo.get(i).getReturnMessage(),
                    Toast.LENGTH_SHORT).show();
        }

        // Cookierepository definieren
        CookieRepository cookieRepository = new CookieRepository(baseDir);

        // Ophalen cookies : action
        action = cookieRepository.getCookieValueFromLabel(SpecificData.COOKIE_RETURN_ACTION);

        // Zet calling activity
        cookieRepository.registerCookie(SpecificData.CALLING_ACTIVITY, SpecificData.ACTIVITY_EDIT_OPVITEM);

        // Intent bekijken vr action
        Intent editIntent = getIntent();
        action = editIntent.getStringExtra(StaticData.EXTRA_INTENT_KEY_ACTION);
        cookieRepository.registerCookie(SpecificData.COOKIE_RETURN_ACTION, action);

        // Terugkeer nr editrubriek cookies voorzien
        // Rubriek index moet later als terugkeer cookie bewaard worden !!
        cookieRepository.registerCookie(SpecificData.COOKIE_RETURN_ACTION, StaticData.ACTION_UPDATE);
        cookieRepository.registerCookie(SpecificData.COOKIE_TAB_SELECTION, SpecificData.COOKIE_TAB_OITEM);

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

        // Als de naam ingevuld is, wordt de naam uitgevoerd automatisch ingevuld
        opvolgingsitemNameV.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                // Welke view has changed
                if (view.getId() == opvolgingsitemNameV.getId() && !b){
                    // Suggestie voor name past
                    opvolgingsitemNamePastV.setText(opvolgingsitemNameV.getText() + " uitgevoerd");
                }else if (view.getId() == opvolgingsitemFrequencyV.getId()){
                    // Checken of er al iets ingevuld is
                    if (opvolgingsitemFrequencyV.getText() == null){
                        radioGroupFrequency.setVisibility(View.GONE);
                        opvolgingsitemLabelFreqUnit.setVisibility(View.GONE);
                    }else {
                        // Checken of frequentieunit moet getoond worden of niet
                        if (Integer.parseInt(String.valueOf(opvolgingsitemFrequencyV.getText())) == 0) {
                            // Item zndr frequentie, frequentieeenheid invisible maken
                            radioGroupFrequency.setVisibility(View.GONE);
                            opvolgingsitemLabelFreqUnit.setVisibility(View.GONE);
                        } else {
                            opvolgingsitemLabelFreqUnit.setVisibility(View.VISIBLE);
                            radioGroupFrequency.setVisibility(View.VISIBLE);
                        }
                    }
                }
            }
        });

        // Detecteren dat de frequency vh opvolgingsitem al of niet op 0 gezet is
        // Deze oplossing werkt maar nadeel : als je de frequentie van 3 op 0 zet, dan moet je eerst op een
        // ander veld gaan staan voordat de frequentie-eenheid onzichtbaar wordt !!
        opvolgingsitemFrequencyV.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                // Welke view has changed
                if (view.getId() == opvolgingsitemFrequencyV.getId()) {
                    // Checken of frequentie = 0 of null is, dan moet de eenheid niet getoond worden
                    String frequentieNbrStr = String.valueOf(opvolgingsitemFrequencyV.getText());
                    if (frequentieNbrStr.equals("")) {
                        // Er is nog geen waarde als frequentie
                        radioGroupFrequency.setVisibility(View.GONE);
                        opvolgingsitemLabelFreqUnit.setVisibility(View.GONE);
                    } else {
                        // Checken of frequentieunit moet getoond worden of niet
                        if (Integer.parseInt(frequentieNbrStr) == 0) {
                            // Item zndr frequentie, frequentieeenheid invisible maken
                            radioGroupFrequency.setVisibility(View.GONE);
                            opvolgingsitemLabelFreqUnit.setVisibility(View.GONE);
                        } else {
                            opvolgingsitemLabelFreqUnit.setVisibility(View.VISIBLE);
                            radioGroupFrequency.setVisibility(View.VISIBLE);
                        }
                    }
                }
            }
        });

        // TODO: Als focus op latestdate komt, moet de softkeyboard worden afgezet
        opvolgingsitemLatestDateV.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                // Welke view has changed
                if (view.getId() == opvolgingsitemLatestDateV.getId()) {
                    // Toon DatePickerInterface
                    showDatePicker(view);
                    // TODO:  Softkeyboard afzetten ?
                }
            }
        });

        // Vul Scherm in vlgns new/update
        switch (action) {
            case StaticData.ACTION_NEW:
                setTitle(SpecificData.TITLE_NEW_OPVOLGINGSITEM);
                // Bepaal rubriek uit intent
                IDNumber rubriekId = new IDNumber(editIntent.getIntExtra(SpecificData.ID_RUBRIEK, 0));
                rubriekOpvolgingsitem.setRubriek(viewModel.getRubriekById(rubriekId));

                // Terugkeer cookie voor rubriek maken
                cookieRepository.registerCookie(SpecificData.COOKIE_RETURN_UPDATE_ID,
                        String.valueOf(rubriekId.getId()));
                cookieRepository.registerCookie(SpecificData.COOKIE_TAB_SELECTION, SpecificData.COOKIE_TAB_OITEM);

                // Frequentie nbr default op 0 zetten
                opvolgingsitemFrequencyV.setText("0");
                opvolgingsitemLabelFreqUnit.setVisibility(View.GONE);
                break;
            case StaticData.ACTION_UPDATE:
                setTitle(SpecificData.TITLE_UPDATE_OPVOLGINGSITEM);

                // ID uit intent halen om te weten welk element moet aangepast worden
                iDToUpdate = editIntent.getIntExtra(StaticData.EXTRA_INTENT_KEY_ID,
                        StaticData.ITEM_NOT_FOUND);
                opvolgingsitemToUpdate = viewModel.getOpvolgingsitemById(new IDNumber(iDToUpdate));

                // rubriek uit opvolgingsitem halen
                rubriekOpvolgingsitem.setRubriek(viewModel.getRubriekById(opvolgingsitemToUpdate.getRubriekId()));

                // rubriek ID in returncookie bewaren
                cookieRepository.registerCookie(SpecificData.COOKIE_RETURN_UPDATE_ID,
                        String.valueOf(rubriekOpvolgingsitem.getEntityId().getId()));
                cookieRepository.registerCookie(SpecificData.COOKIE_TAB_SELECTION, SpecificData.COOKIE_TAB_OITEM);

                // Invullen vn gegevens opvolgingsitem op scherm
                opvolgingsitemNameV.setText(opvolgingsitemToUpdate.getEntityName());
                opvolgingsitemNamePastV.setText(opvolgingsitemToUpdate.getEntityNamePast());
                opvolgingsitemFrequencyV.setText(String.valueOf(opvolgingsitemToUpdate.getFrequentieNbr()));
                if (opvolgingsitemToUpdate.getFrequentieNbr() > 0){
                    opvolgingsitemLabelFreqUnit.setVisibility(View.VISIBLE);
                    radioGroupFrequency.setVisibility(View.VISIBLE);

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
                }else {
                    opvolgingsitemLabelFreqUnit.setVisibility(View.GONE);
                    radioGroupFrequency.setVisibility(View.GONE);
                }
                opvolgingsitemLatestDateV.setText(opvolgingsitemToUpdate.getLatestDate().getFormatDate());

                // Recyclerview voor logboek definieren
                TextView labelLogboek = findViewById(R.id.labelItemLogboek);
                labelLogboek.setVisibility(View.VISIBLE);
                RecyclerView recyclerView = findViewById(R.id.recycler_edit_item);
                recyclerView.setVisibility(View.VISIBLE);
                adapter = new TextItemListAdapter(this);
                recyclerView.setAdapter(adapter);
                recyclerView.setLayoutManager(new LinearLayoutManager(this));

                // Opvullen recycler list met logitems vr opvolgingsitem in kwestie
                itemList.clear();
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
                                adapter.setCallingActivity(SpecificData.ACTIVITY_EDIT_OPVITEM);
                                adapter.setItemList(itemList);
                            }
                        });
                helper.attachToRecyclerView(recyclerView);
                adapter.setEntityType(SpecificData.ENTITY_TYPE_LOG);
                adapter.setCallingActivity(SpecificData.ACTIVITY_EDIT_OPVITEM);
                adapter.setItemList(itemList);

                // FloatingActionButton om een logitem toe te voegen
                FloatingActionButton fab = findViewById(R.id.fab_edit_item);
                fab.setVisibility(View.VISIBLE);
                fab.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // vul in met editlogitem activity
                        Intent manIntent = new Intent(EditOpvolgingsitem.this, EditLog.class);
                        manIntent.putExtra(StaticData.EXTRA_INTENT_KEY_ACTION, StaticData.ACTION_NEW);
                        manIntent.putExtra(StaticData.EXTRA_INTENT_KEY_RETURN, SpecificData.ACTIVITY_EDIT_OPVITEM);
                        manIntent.putExtra(SpecificData.ID_RUBRIEK, rubriekOpvolgingsitem.getEntityId().getId());
                        manIntent.putExtra(SpecificData.ID_OPVOLGINGSITEM, opvolgingsitemToUpdate.getEntityId().getId());
                        startActivity(manIntent);
                    }
                });
                break;
            default:
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
                    if (frequencyDayV.isChecked()){
                        opvolgingsitemToUpdate.setFrequentieUnit(FrequentieDateUnit.DAYS);
                    }else if (frequencyWeekV.isChecked()){
                        opvolgingsitemToUpdate.setFrequentieUnit(FrequentieDateUnit.WEEKS);
                    }else if (frequencyMonthV.isChecked()){
                        opvolgingsitemToUpdate.setFrequentieUnit(FrequentieDateUnit.MONTHS);
                    }else if (frequencyYearV.isChecked()){
                        opvolgingsitemToUpdate.setFrequentieUnit(FrequentieDateUnit.YEARS);
                    }
                    // FrequentieNbr moet na FrequentieUnit upgedate worden !!
                    opvolgingsitemToUpdate.setFrequentieNbr(Integer.parseInt(String.valueOf(opvolgingsitemFrequencyV.getText())));
                    boolean latestDateChanged = false;
                    // String newLatestDate = String.valueOf(opvolgingsitemLatestDateV.getText());
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
                    // Refreshen logboek
                    itemList.addAll(viewModel.getLogItemListByOItemID(opvolgingsitemToUpdate.getEntityId()));
                    adapter.setItemList(itemList);
                } else { // New
                    // Creer een nieuw opvolgingsitem en vul met gegevens vn scherm
                    Opvolgingsitem newOItem = new Opvolgingsitem(viewModel.getBasedir(), false);
                    newOItem.setRubriekId(rubriekOpvolgingsitem.getEntityId());
                    newOItem.setEntityName(String.valueOf(opvolgingsitemNameV.getText()));
                    newOItem.setEntityNamePast(String.valueOf(opvolgingsitemNamePastV.getText()));
                    if (frequencyDayV.isChecked()){
                        newOItem.setFrequentieUnit(FrequentieDateUnit.DAYS);
                    }else if (frequencyWeekV.isChecked()){
                        newOItem.setFrequentieUnit(FrequentieDateUnit.WEEKS);
                    }else if (frequencyMonthV.isChecked()){
                        newOItem.setFrequentieUnit(FrequentieDateUnit.MONTHS);
                    }else if (frequencyYearV.isChecked()){
                        newOItem.setFrequentieUnit(FrequentieDateUnit.YEARS);
                    }
                    newOItem.setFrequentieNbr(Integer.parseInt(String.valueOf(opvolgingsitemFrequencyV.getText())));

                    // Controleer of latestDate ingevuld is
                    String latestDateString = String.valueOf(opvolgingsitemLatestDateV.getText());
                    if (!latestDateString.equals("")){
                        // LatestDate ingevuld
                        newOItem.setLatestDate(new DateString(String.valueOf(opvolgingsitemLatestDateV.getText())));
                    } else {
                        // latestDate niet ingevuld
                        newOItem.setLatestDate(new DateString(DateString.EMPTY_DATESTRING));
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
                Toast.makeText(getApplicationContext(), "Opvolgingsitem bewaard !",
                        Toast.LENGTH_SHORT).show();

                // Teruggaan nr EditRubriek weggehaald
                //cookieRepository.bestaatCookie(SpecificData.COOKIE_RETURN_UPDATE_INDEX);
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
//        logDialog.setTitleDialog("Wenst u een log aan te maken ?");
//        logDialog.setMsgDialog("Click JA of NEE: ");
        logDialog.show(getSupportFragmentManager(), "LogDialogFragment");
    }

    // The dialog fragment receives a reference to this Activity through the
    // Fragment.onAttach() callback, which it uses to call the following methods
    // defined by the LogDialogFragment.LogDialogInterface interface
    @Override
    public void onLogDialogPositiveClick(DialogFragment dialogFragment, String subject) {
        // Maak alvast de log aan
        Log newLog = new Log(viewModel.getBasedir(), opvolgingsitemPastProcess, opvolgingsitemPastProcess.getEntityNamePast());
/*
        newLog.setLogDate(opvolgingsitemPastProcess.getLatestDate());
        // entityNamePAst gebruiken om log aan te maken
        newLog.setLogDescription(opvolgingsitemPastProcess.getEntityNamePast());
        newLog.setRubriekId(opvolgingsitemPastProcess.getRubriekId());
        newLog.setItemId(opvolgingsitemPastProcess.getEntityId());
*/
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

        // Teruggaan nr EditRubriek
        Intent replyIntent = new Intent(EditOpvolgingsitem.this, EditRubriek.class);
        replyIntent.putExtra(StaticData.EXTRA_INTENT_KEY_ACTION, StaticData.ACTION_UPDATE);
        replyIntent.putExtra(StaticData.EXTRA_INTENT_KEY_ID, rubriekOpvolgingsitem.getEntityId().getId());
        replyIntent.putExtra(SpecificData.COOKIE_TAB_SELECTION, SpecificData.COOKIE_TAB_OITEM);
        startActivity(replyIntent);
    }

    @Override
    public void onLogDialogNegativeClick(DialogFragment dialogFragment) {
        // User clicked NEE button, er moet geen log aangemaakt worden
        Toast.makeText(getApplicationContext(), "Pressed NEE",
                Toast.LENGTH_SHORT).show();

        // In dialogfragment zit niets bruikbaar

        // is frequentie ingevuld
        if ((opvolgingsitemPastProcess.getFrequentieNbr() > 0) &&
                (opvolgingsitemPastProcess.getFrequentieUnit() != null)){
            // Vraag stellen om in agenda te registreren,
            createEventSaveDialog();
        }

        // Teruggaan nr EditRubriek
        Intent replyIntent = new Intent(EditOpvolgingsitem.this, EditRubriek.class);
        replyIntent.putExtra(StaticData.EXTRA_INTENT_KEY_ACTION, StaticData.ACTION_UPDATE);
        replyIntent.putExtra(StaticData.EXTRA_INTENT_KEY_ID, rubriekOpvolgingsitem.getEntityId().getId());
        replyIntent.putExtra(SpecificData.COOKIE_TAB_SELECTION, SpecificData.COOKIE_TAB_OITEM);
        startActivity(replyIntent);
    }

    private void createEventSaveDialog(){
        // Create an instance of the dialog fragment and show it
        FlexDialogFragment eventDialog = new FlexDialogFragment();
        eventDialog.setSubjectDialog("Event");
//        eventDialog.setTitleDialog("Wenst u een event in de agenda aan te maken ?");
//        eventDialog.setMsgDialog("Click JA of NEE: ");
        eventDialog.show(getSupportFragmentManager(), "EventDialogFragment");
    }

    @Override
    public void onEventDialogPositiveClick(DialogFragment dialogFragment) {
        // User clicked JA button, er wordt een event aangemaakt in de agenda
        ContentResolver cr = getContentResolver();
        // Bepaal MyCalendarID
        long calID = calenderService.getMyCalendarId(
                EditOpvolgingsitem.this,
                EditOpvolgingsitem.this,
                cr);
        // Bepaal volgende opvolgingsdatum
        long eventDateMs = opvolgingsitemPastProcess.getNextDate();
        // Event om 10h in de vm zetten 10*3600*1000 bijtellen, lukt niet want volgende
        // opvolgingsdatum eventDateMs is bepaald obv uur vh moment en niet 0h00
        //eventDateMs = eventDateMs + (10*3600*1000);
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
        replyIntent.putExtra(SpecificData.COOKIE_TAB_SELECTION, SpecificData.COOKIE_TAB_OITEM);
        startActivity(replyIntent);
    }

    @Override
    public void onEventDialogNegativeClick(DialogFragment dialogFragment) {
        // User clicked NEE button, er moet geen event in de agenda worden gezet
        Toast.makeText(getApplicationContext(), "Pressed NEE",
                Toast.LENGTH_SHORT).show();

        // Teruggaan nr EditRubriek
        Intent replyIntent = new Intent(EditOpvolgingsitem.this, EditRubriek.class);
        replyIntent.putExtra(StaticData.EXTRA_INTENT_KEY_ACTION, StaticData.ACTION_UPDATE);
        replyIntent.putExtra(StaticData.EXTRA_INTENT_KEY_ID, rubriekOpvolgingsitem.getEntityId().getId());
        replyIntent.putExtra(SpecificData.COOKIE_TAB_SELECTION, SpecificData.COOKIE_TAB_OITEM);
        startActivity(replyIntent);
    }

    @Override
    public void onOitemDialogPositiveClick(DialogFragment dialogFragment) {
        // Not used
    }

    @Override
    public void onOitemDialogNegativeClick(DialogFragment dialogFragment) {
        // Not used
    }

    @Override
    public void onDateDialogPositiveClick(DialogFragment dialogFragment) {
        // Not used
    }

    @Override
    public void onDateDialogNegativeClick(DialogFragment dialogFragment) {
        // Not used
    }

    @Override
    public void onSkipDialogPositiveClick(DialogFragment dialogFragment) {

    }

    @Override
    public void onSkipDialogNegativeClick(DialogFragment dialogFragment) {

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


        /*        opvolgingsitemFrequencyV.dispatchKeyEvent(new KeyEvent())
        opvolgingsitemFrequencyV.setImeOptions(EditorInfo.IME_ACTION_GO);
        opvolgingsitemFrequencyV.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                // Wordt niet afgevuurd bij ingeven cijfers !!
                if (i == EditorInfo.IME_ACTION_DONE){
                    return true;
                }
                if (keyEvent != null) {
                    String content = String.valueOf(opvolgingsitemFrequencyV.getText());
                    try {
                        // Checken of frequentieunit moet getoond worden of niet
                        if (Integer.parseInt(String.valueOf(opvolgingsitemFrequencyV.getText())) == 0) {
                            // Item zndr frequentie, frequentieeenheid invisible maken
                            radioGroupFrequency.setVisibility(View.GONE);
                            opvolgingsitemLabelFreqUnit.setVisibility(View.GONE);
                        } else {
                            opvolgingsitemLabelFreqUnit.setVisibility(View.VISIBLE);
                            radioGroupFrequency.setVisibility(View.VISIBLE);
                        }
                    }catch (NumberFormatException ex){
                        radioGroupFrequency.setVisibility(View.GONE);
                        opvolgingsitemLabelFreqUnit.setVisibility(View.GONE);
                    }

                }

                return false;
            }
        });

        opvolgingsitemFrequencyV.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                // Focus zetten
                //opvolgingsitemFrequencyV.setFocusable(View.FOCUSABLE);
                String content = String.valueOf(opvolgingsitemFrequencyV.getText());
                try {
                    // Checken of frequentieunit moet getoond worden of niet
                    if (Integer.parseInt(String.valueOf(opvolgingsitemFrequencyV.getText())) == 0) {
                        // Item zndr frequentie, frequentieeenheid invisible maken
                        radioGroupFrequency.setVisibility(View.GONE);
                        opvolgingsitemLabelFreqUnit.setVisibility(View.GONE);
                    } else {
                        opvolgingsitemLabelFreqUnit.setVisibility(View.VISIBLE);
                        radioGroupFrequency.setVisibility(View.VISIBLE);
                    }
                }catch (NumberFormatException ex){
                    radioGroupFrequency.setVisibility(View.GONE);
                    opvolgingsitemLabelFreqUnit.setVisibility(View.GONE);
                }
                return true;
            }


        });

        opvolgingsitemFrequencyV.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                // Wordt niet afgevuurd bij ingeven cijfers !!
                String content = String.valueOf(opvolgingsitemFrequencyV.getText());
                try {
                    // Checken of frequentieunit moet getoond worden of niet
                    if (Integer.parseInt(String.valueOf(opvolgingsitemFrequencyV.getText())) == 0) {
                        // Item zndr frequentie, frequentieeenheid invisible maken
                        radioGroupFrequency.setVisibility(View.GONE);
                        opvolgingsitemLabelFreqUnit.setVisibility(View.GONE);
                    } else {
                        opvolgingsitemLabelFreqUnit.setVisibility(View.VISIBLE);
                        radioGroupFrequency.setVisibility(View.VISIBLE);
                    }
                }catch (NumberFormatException ex){
                    radioGroupFrequency.setVisibility(View.GONE);
                    opvolgingsitemLabelFreqUnit.setVisibility(View.GONE);
                }

                if (content == null){
                    radioGroupFrequency.setVisibility(View.GONE);
                    opvolgingsitemLabelFreqUnit.setVisibility(View.GONE);
                }else {
                    // Checken of frequentieunit moet getoond worden of niet
                    if (Integer.parseInt(String.valueOf(opvolgingsitemFrequencyV.getText())) == 0) {
                        // Item zndr frequentie, frequentieeenheid invisible maken
                        radioGroupFrequency.setVisibility(View.GONE);
                        opvolgingsitemLabelFreqUnit.setVisibility(View.GONE);
                    } else {
                        opvolgingsitemLabelFreqUnit.setVisibility(View.VISIBLE);
                        radioGroupFrequency.setVisibility(View.VISIBLE);
                    }
                }
                return false;
            }
        });

*/

}