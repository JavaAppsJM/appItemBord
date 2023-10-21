package be.hvwebsites.itembord;

import android.content.ContentResolver;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import be.hvwebsites.itembord.adapters.PriorityListItemAdapter;
import be.hvwebsites.itembord.constants.SpecificData;
import be.hvwebsites.itembord.entities.Log;
import be.hvwebsites.itembord.entities.Opvolgingsitem;
import be.hvwebsites.itembord.fragments.FlexDialogFragment;
import be.hvwebsites.itembord.helpers.ListItemTwoLinesHelper;
import be.hvwebsites.itembord.interfaces.FlexDialogInterface;
import be.hvwebsites.itembord.services.CalenderService;
import be.hvwebsites.itembord.services.FileBaseService;
import be.hvwebsites.itembord.services.FileBaseServiceOld;
import be.hvwebsites.itembord.viewmodels.EntitiesViewModel;
import be.hvwebsites.libraryandroid4.helpers.DateString;
import be.hvwebsites.libraryandroid4.helpers.IDNumber;
import be.hvwebsites.libraryandroid4.repositories.CookieRepository;
import be.hvwebsites.libraryandroid4.returninfo.ReturnInfo;
import be.hvwebsites.libraryandroid4.statics.StaticData;

public class MainActivity extends AppCompatActivity implements FlexDialogInterface {
    private EntitiesViewModel viewModel;
    private List<ListItemTwoLinesHelper> itemList = new ArrayList<>();
    private Opvolgingsitem opvolgingsitemToRollOn;
    private int opvolgingsitemToRollOnIndex;
    private RecyclerView recyclerView;

    private PriorityListItemAdapter priorityListItemAdapter;
    // Device
    private final String deviceModel = Build.MODEL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Creer een filebase service (bevat file base en file base directory) obv device en package name
        //FileBaseServiceOld fileBaseServiceOld = new FileBaseServiceOld(deviceModel, getPackageName());

        // Creer een filebase service, bepaalt file base directory obv device en Context
        FileBaseService fileBaseService = new FileBaseService(deviceModel, this);

        // Data ophalen
        // Get a viewmodel from the viewmodelproviders
        viewModel = new ViewModelProvider(this).get(EntitiesViewModel.class);
        // Basis directory definitie
        //String baseDir = fileBaseServiceOld.getFileBaseDir();
        String baseDir = fileBaseService.getFileBaseDir();
        // Initialize viewmodel mt basis directory (data wordt opgehaald in viewmodel)
        List<ReturnInfo> viewModelRetInfo = viewModel.initializeViewModel(baseDir);
        // Display return msg(s)
        for (int i = 0; i < viewModelRetInfo.size(); i++) {
            Toast.makeText(getApplicationContext(),
                    viewModelRetInfo.get(i).getReturnMessage(),
                    Toast.LENGTH_SHORT).show();
        }

        // Cookierepository definieren
        CookieRepository cookieRepository = new CookieRepository(baseDir);

        // Zet calling activity
        cookieRepository.registerCookie(SpecificData.CALLING_ACTIVITY, SpecificData.ACTIVITY_MAIN);

        // Recyclerview definieren
        recyclerView = findViewById(R.id.recycler_statusbord);
        priorityListItemAdapter = new PriorityListItemAdapter(this);
        recyclerView.setAdapter(priorityListItemAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Recyclerview invullen met statusbord items
        itemList.clear();
        itemList.addAll(buildStatusbordList());
        priorityListItemAdapter.setItemList(itemList);
        if (itemList.size() == 0){
            Toast.makeText(this,
                    SpecificData.NO_STATUSBORDITEMS_YET,
                    Toast.LENGTH_LONG).show();
        }

        // Als er lang geklikt is op een oitem, kan dat hier gecapteerd worden
        priorityListItemAdapter.setOnItemClickListener(new PriorityListItemAdapter.ClickListener() {
            @Override
            public void onItemClicked(IDNumber itemID, View v) {
                // Opvolgingsitem to roll on en index bepalen
                opvolgingsitemToRollOn = viewModel.getOpvolgingsitemById(itemID);
                opvolgingsitemToRollOnIndex = viewModel.getItemIndexById(itemID);
                // Via een dialog vragen om te bevestigen om oitem af te vinken
                // Create an instance of the dialog fragment and show it
                FlexDialogFragment oitemDialog = new FlexDialogFragment();
                oitemDialog.setSubjectDialog("Oitem");
                oitemDialog.show(getSupportFragmentManager(), "oItemDialogFragment");
            }
        });

        // Als een item in de recyclerview, nr rechts, geswiped wordt, dan wordt deze duedate
        //  geskipped en wordt de volgende gezet
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
                        Toast.makeText(MainActivity.this,
                                " Due date skippen ... ",
                                Toast.LENGTH_LONG).show();
                        // Bepalen entity IDNumber to be skipped
                        int position = viewHolder.getAdapterPosition();
                        IDNumber idNumberToSkip = itemList.get(position).getItemID();

                        // Due date item skippen
                        // Hergebruiken van Opvolgingsitem to roll on en index
                        opvolgingsitemToRollOn = viewModel.getOpvolgingsitemById(idNumberToSkip);
                        opvolgingsitemToRollOnIndex = viewModel.getItemIndexById(idNumberToSkip);

                        // Via een dialog vragen om te bevestigen om te skippen. Hierbinnen
                        // wordt het skippen uitgevoerd !
                        // Create an instance of the dialog fragment and show it
                        FlexDialogFragment oitemSkipDialog = new FlexDialogFragment();
                        oitemSkipDialog.setSubjectDialog("Skip");
                        oitemSkipDialog.show(getSupportFragmentManager(), "oItemDialogFragment");
                    }
                });
        helper.attachToRecyclerView(recyclerView);
    }

    private List<ListItemTwoLinesHelper> buildStatusbordList(){
        List<ListItemTwoLinesHelper> statusbordList = new ArrayList<>();
        String item1;
        String item2;
        Opvolgingsitem opvolgingsitem = new Opvolgingsitem();
        int indexhelp;

        // Bepaal voor elk opvolgingsitem dat een frequentie en een laatste opvolgingsdatum heeft, de rubriek
        for (int i = 0; i < viewModel.getOpvolgingsitemList().size(); i++) {
            // Bepaal opvolgingsitem
            opvolgingsitem.setOpvolgingsitem(viewModel.getOpvolgingsitemList().get(i));
            if ((opvolgingsitem.getFrequentieNbr() > 0) &&
                    (opvolgingsitem.getLatestDate() != null)){
                // Bepaal rubriek
                indexhelp = viewModel.getRubriekIndexById(opvolgingsitem.getRubriekId());
                // Vul eerste lijn in
                item1 = viewModel.getRubriekList().get(indexhelp).getEntityName() +
                        ": " +
                        opvolgingsitem.getEntityName();
                // Vul tweede lijn in
                item2 = "Vervaldatum: " +
                        opvolgingsitem.getNextDateDS().getFormatDate() +
                        " Vorige: " +
                        opvolgingsitem.getLatestDate().getFormatDate();
                // Creer statusborditem en steek in statusbordlist
                statusbordList.add(new ListItemTwoLinesHelper(item1
                        ,item2
                        ,opvolgingsitem.getDisplayStyle()
                        ,opvolgingsitem.getEntityId()
                        , 0
                        , 0));
            }
        }
        return statusbordList;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        /** Menu items definieren */
        Intent mainIntent;
        switch (item.getItemId()) {
            case R.id.menu_statusbord:
                // ga naar activity ManageRubriek
                mainIntent = new Intent(MainActivity.this, OItemStatusBord.class);
                startActivity(mainIntent);
                return true;
            case R.id.menu_logboek:
                // ga naar activity Logboek
                mainIntent = new Intent(MainActivity.this, Logboek.class);
                startActivity(mainIntent);
                return true;
            case R.id.menu_beheer_rubrieken:
                // ga naar activity ManageRubriek
                mainIntent = new Intent(MainActivity.this, ManageRub2.class);
                startActivity(mainIntent);
                return true;
            case R.id.menu_exit:
                // Exit app
                finish();
            default:
                // Do nothing
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        // Bewaar Instance State (bvb: fileBase, smsStatus, entityType, enz..)
        outState.putString(StaticData.FILE_BASE_DIR, viewModel.getBasedir());

    }

    @Override
    public void onOitemDialogPositiveClick(DialogFragment dialogFragment) {
        // Via een dialog vragen of latest date = vandaag of vervaldatum moet gewijzigd worden
        FlexDialogFragment dateDialog = new FlexDialogFragment();
        dateDialog.setSubjectDialog("Date");
        dateDialog.show(getSupportFragmentManager(), "dateDialogFragment");
    }

    @Override
    public void onOitemDialogNegativeClick(DialogFragment dialogFragment) {
        // Opvolgingsitem moet niet afgevinkt worden, er moet niets gebeuren !
    }

    @Override
    public void onDateDialogPositiveClick(DialogFragment dialogFragment) {
        // Opvolgingsitem moet latest date = vandaag nemen
        DateString tempDs = new DateString();
        tempDs.setDateToday();
        opvolgingsitemToRollOn.setLatestDate(tempDs);
        // Doe de verdere verwerking van afvinken
        processRollOnOItem(opvolgingsitemToRollOn.getEntityNamePast());
    }

    @Override
    public void onDateDialogNegativeClick(DialogFragment dialogFragment) {
        // Opvolgingsitem moet latest date = vervaldatum nemen
        opvolgingsitemToRollOn.setLatestDate(opvolgingsitemToRollOn.getNextDateDS());
        // Doe de verdere verwerking van afvinken
        processRollOnOItem(opvolgingsitemToRollOn.getEntityNamePast());
    }

    @Override
    public void onSkipDialogPositiveClick(DialogFragment dialogFragment) {
        // Doe de verdere verwerking van skippen

        // Skipdate aanpassen en next date herbepalen. Opvolgingsitem in kwestie zit in rollon
        opvolgingsitemToRollOn.setSkipDate(opvolgingsitemToRollOn.getNextDateDS());
        processRollOnOItem("Due date geskipped !");
    }

    @Override
    public void onSkipDialogNegativeClick(DialogFragment dialogFragment) {
        // Uitvoering opvolgingsitem moet niet geskipped worden
    }

    private void processRollOnOItem(String inLogText) {
        // Opvolgingsitem moet afgevinkt worden

        // Opvolgingsitem in list aanpassen
        viewModel.getOpvolgingsitemList().get(opvolgingsitemToRollOnIndex).setOpvolgingsitem(opvolgingsitemToRollOn);

        // Er moet een log aangemaakt worden vermits het oitem automatisch afgevinkt wordt
        Log newLog = new Log(viewModel.getBasedir(), opvolgingsitemToRollOn, inLogText);

        // de log moet bewaard worden !
        viewModel.getLogList().add(newLog);
        viewModel.storeLogs();

        Toast.makeText(getApplicationContext(), "Er wordt een log aangemaakt",
                Toast.LENGTH_SHORT).show();

        // Er moet agenda event gewijzigd worden. We doen dit enkel als er al een agenda event was
        // Als er een event op de vorige datum was, dan moet dit verwijderd worden
        if (opvolgingsitemToRollOn.getEventId() != StaticData.ITEM_NOT_FOUND) {
            // CalenderService activeren
            CalenderService calenderService = new CalenderService();

            // Contentresolver definieren
            ContentResolver cr = getContentResolver();

            // Bepaal MyCalendarID, dit is enkel om de permissions in orde te brengen
            long calID = calenderService.getMyCalendarId(
                    getApplicationContext(),
                    MainActivity.this,
                    cr);

            // Bestaand event verwijderen
            calenderService.deleteEventInMyCalendar(cr, opvolgingsitemToRollOn.getEventId());

            // Nieuw event aanmaken op de nieuwe vervaldatum
            // Bepaal nieuwe vervaldatum
            long eventDateMs = opvolgingsitemToRollOn.getNextDate();

            // Maak een event in de agenda
            final long eventId = calenderService.createEventInMyCalendar(
                    cr,
                    calID,
                    eventDateMs,
                    eventDateMs,
                    opvolgingsitemToRollOn.getEntityName());

            // eventId bewaren in opvolgingsitemlist
            // opvolgingsitemToRollOn.setEventId(eventId);
            viewModel.getOpvolgingsitemList().get(opvolgingsitemToRollOnIndex).setEventId(eventId);

            // Reminder creeren voor het event
            calenderService.createReminderForEvent(cr, eventId);

            Toast.makeText(getApplicationContext(), "Event in de agenda gecreeerd !",
                    Toast.LENGTH_SHORT).show();
        }
        // Opvolgingsitem bewaren
        viewModel.storeItems();

        // Recyclerlist refreshen
        itemList.clear();
        itemList.addAll(buildStatusbordList());
        priorityListItemAdapter.setItemList(itemList);
        recyclerView.setAdapter(priorityListItemAdapter);
        if (itemList.size() == 0){
            Toast.makeText(this,
                    SpecificData.NO_STATUSBORDITEMS_YET,
                    Toast.LENGTH_LONG).show();
        }
    }

        @Override
    public void onLogDialogPositiveClick(DialogFragment dialogFragment, String subject) {
        //Not used
    }

    @Override
    public void onLogDialogNegativeClick(DialogFragment dialogFragment) {
        //Not used
    }

    @Override
    public void onEventDialogPositiveClick(DialogFragment dialogFragment) {
        //Not used
    }

    @Override
    public void onEventDialogNegativeClick(DialogFragment dialogFragment) {
        //Not used
    }
}