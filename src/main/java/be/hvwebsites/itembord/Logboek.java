package be.hvwebsites.itembord;

import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import be.hvwebsites.itembord.adapters.LogboekItemListAdapter;
import be.hvwebsites.itembord.constants.SpecificData;
import be.hvwebsites.itembord.entities.Log;
import be.hvwebsites.itembord.entities.Opvolgingsitem;
import be.hvwebsites.itembord.entities.Rubriek;
import be.hvwebsites.itembord.helpers.ListItemTwoLinesHelper;
import be.hvwebsites.itembord.services.FileBaseService;
import be.hvwebsites.itembord.viewmodels.EntitiesViewModel;
import be.hvwebsites.libraryandroid4.helpers.IDNumber;
import be.hvwebsites.libraryandroid4.helpers.ListItemHelper;
import be.hvwebsites.libraryandroid4.returninfo.ReturnInfo;
import be.hvwebsites.libraryandroid4.statics.StaticData;

public class Logboek extends AppCompatActivity implements AdapterView.OnItemSelectedListener{
    private EntitiesViewModel viewModel;
    private List<ListItemTwoLinesHelper> logboekList = new ArrayList<>();
    // Filters
    private IDNumber filterRubriekID = new IDNumber(StaticData.IDNUMBER_NOT_FOUND.getId());
    private IDNumber filterOItemID = new IDNumber(StaticData.IDNUMBER_NOT_FOUND.getId());
    // Device
    private final String deviceModel = Build.MODEL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logboek);

        // Creer een filebase service, bepaalt file base directory obv device en Context
        FileBaseService fileBaseService = new FileBaseService(deviceModel, this);

        // Data ophalen
        // Get a viewmodel from the viewmodelproviders
        viewModel = new ViewModelProvider(this).get(EntitiesViewModel.class);
        // Basis directory definitie
        String baseDir = fileBaseService.getFileBaseDir();
        // Initialize viewmodel mt basis directory (data wordt opgehaald in viewmodel)
        List<ReturnInfo> viewModelRetInfo = viewModel.initializeViewModel(baseDir);
        // Display return msg(s)
        for (int i = 0; i < viewModelRetInfo.size(); i++) {
            Toast.makeText(getApplicationContext(),
                    viewModelRetInfo.get(i).getReturnMessage(),
                    Toast.LENGTH_SHORT).show();
        }

        // Rubriekfilter Spinner en adapter definieren
        Spinner rubriekFilterSpinner = (Spinner) findViewById(R.id.spinr_rubriek);

        // rubriekfilterAdapter obv ListItemHelper
        ArrayAdapter<ListItemHelper> rubriekFilterAdapter = new ArrayAdapter(this,
                android.R.layout.simple_spinner_item);
        rubriekFilterAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Rubriekfilter vullen met alle rubrieken
        rubriekFilterAdapter.add(new ListItemHelper(SpecificData.NO_RUBRIEK_FILTER,
                "",
                StaticData.IDNUMBER_NOT_FOUND));
        rubriekFilterAdapter.addAll(viewModel.getRubriekItemList());
        rubriekFilterSpinner.setAdapter(rubriekFilterAdapter);

        // Opvolgingsitemfilter Spinner en adapter definieren
        Spinner oItemFilterSpinner = (Spinner) findViewById(R.id.spinr_oitem);

        // OpvolgingsitemfilterAdapter obv ListItemHelper
        ArrayAdapter<ListItemHelper> oItemFilterAdapter = new ArrayAdapter(this,
                android.R.layout.simple_spinner_item);
        oItemFilterAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Opvolgingsitemfilter invullen
        oItemFilterAdapter.add(new ListItemHelper(SpecificData.NO_OPVOLGINGSITEM_FILTER,
                "",
                StaticData.IDNUMBER_NOT_FOUND));
        oItemFilterSpinner.setAdapter(oItemFilterAdapter);

        // Recyclerview definieren
        RecyclerView recyclerView = findViewById(R.id.recyc_logboek);
        final LogboekItemListAdapter logboekAdapter = new LogboekItemListAdapter(this);
        recyclerView.setAdapter(logboekAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Recyclerview invullen met logboek items
        logboekList.clear();
        logboekList.addAll(buildLogboek(StaticData.IDNUMBER_NOT_FOUND, StaticData.IDNUMBER_NOT_FOUND));
        logboekAdapter.setItemList(logboekList);
        if (logboekList.size() == 0){
            Toast.makeText(this,
                    SpecificData.NO_LOGBOEKITEMS_YET,
                    Toast.LENGTH_LONG).show();
        }

        // selection listener activeren, moet gebueren nadat de adapter gekoppeld is aan de spinner !!
        rubriekFilterSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                ListItemHelper selHelper = (ListItemHelper) adapterView.getItemAtPosition(i);
                filterRubriekID = selHelper.getItemID();

                // Als er een rubriekID geselecteerd
                if (filterRubriekID.getId() != StaticData.IDNUMBER_NOT_FOUND.getId()){
                    // Clearen van filter opvolgingsitem
                    filterOItemID = StaticData.IDNUMBER_NOT_FOUND;

                    // Spinner selectie zetten
                    //rubriekFilterSpinner.setSelection(i, false);

                    // Inhoud vd opvolgingsitem filter bepalen adhv gekozen rubriek
                    oItemFilterAdapter.clear();
                    oItemFilterAdapter.add(new ListItemHelper(SpecificData.NO_OPVOLGINGSITEM_FILTER,
                            "",
                            StaticData.IDNUMBER_NOT_FOUND));
                    oItemFilterAdapter.addAll(viewModel.getOpvolgingsItemItemListByRubriekID(filterRubriekID));
                    oItemFilterSpinner.setAdapter(oItemFilterAdapter);
                }
                // Inhoud vd logboek bepalen adhv gekozen filters
                logboekList.clear();
                logboekList.addAll(buildLogboek(filterRubriekID, filterOItemID));
                logboekAdapter.setItemList(logboekList);
                recyclerView.setAdapter(logboekAdapter);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        oItemFilterSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                ListItemHelper selHelper = (ListItemHelper) adapterView.getItemAtPosition(i);
                filterOItemID = selHelper.getItemID();

                // Spinner selectie zetten
                //oItemFilterSpinner.setSelection(i, false);

                // Inhoud vd logboek bepalen adhv gekozen filters
                logboekList.clear();
                logboekList.addAll(buildLogboek(filterRubriekID, filterOItemID));
                logboekAdapter.setItemList(logboekList);
                recyclerView.setAdapter(logboekAdapter);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
    }

    private List<ListItemTwoLinesHelper> buildLogboek(IDNumber rubriekID, IDNumber oItemID){
        List<ListItemTwoLinesHelper> logboekList = new ArrayList<>();
        String item1;
        String item2;
        Log log;
        Rubriek rubriekLog;
        Opvolgingsitem opvolgingsitemLog;
        String rubriekLogName = "";
        String oItemLogName = "";
        int indexhelp;

        // Bepaal voor elke logitem dat voldoet, lijn1 & 2
        for (int i = 0; i < viewModel.getLogList().size(); i++) {
            log = viewModel.getLogList().get(i);
            if ((rubriekID.getId() == StaticData.IDNUMBER_NOT_FOUND.getId()
                    || (log.getRubriekId().getId() == rubriekID.getId()))
            && (oItemID.getId() == StaticData.IDNUMBER_NOT_FOUND.getId()
                    || (log.getItemId().getId() == oItemID.getId()))){
                // Ophalen rubriek en opvolgingsitem
                rubriekLog = viewModel.getRubriekById(log.getRubriekId());
                opvolgingsitemLog = viewModel.getOpvolgingsitemById(log.getItemId());

                // Bepaal lijn1
                item1 = log.getLogDate().getFormatDate() + ": ";

                // Als rubriek filter niet ingevuld is, moet rubriek name in lijn 1 gezet worden
                if ((rubriekLog != null)
                        && (rubriekID.getId() == StaticData.IDNUMBER_NOT_FOUND.getId())){
                    rubriekLogName = rubriekLog.getEntityName();
                    item1 = item1.concat(rubriekLogName).concat(": ");
                }

                // Opvolgingsitem kan null zijn !
                if ((opvolgingsitemLog != null)
                        && (oItemID.getId() == StaticData.IDNUMBER_NOT_FOUND.getId())){
                    oItemLogName = opvolgingsitemLog.getEntityName();
                    item1 = item1.concat(oItemLogName);
                }

                // Vul tweede lijn in
                item2 = log.getLogDescTrunc(70);
                // Creer logboekitem en steek in list
                logboekList.add(new ListItemTwoLinesHelper(item1,
                        item2,
                        "",
                        log.getEntityId(),
                        rubriekLogName,
                        oItemLogName,
                        log.getLogDate().getIntDate()));
            }
        }

        // Logboeklist sorteren op rubriek, oitem en datum
        // Er wordt gesorteerd op de rubriekId en de opvolgingsitemId ipv op naam !!
        ListItemTwoLinesHelper temp = new ListItemTwoLinesHelper();
        String sortf11, sortf12 ,sortf21, sortf22;
        int sortf31, sortf32;
        int testint1, testint2;
        for (int i = logboekList.size() ; i > 0 ; i--) {
            for (int j = 1 ; j < i ; j++) {
                sortf11 = logboekList.get(j-1).getSortField1();
                sortf12 = logboekList.get(j).getSortField1();
                sortf21 = logboekList.get(j-1).getSortField2();
                sortf22 = logboekList.get(j).getSortField2();
                sortf31 = logboekList.get(j-1).getSortField3();
                sortf32 = logboekList.get(j).getSortField3();
                testint1 = sortf11.compareToIgnoreCase(sortf12);
                testint2 = sortf21.compareToIgnoreCase(sortf22);
                if ((sortf11.compareToIgnoreCase(sortf12) > 0)
                        || ((sortf11.compareToIgnoreCase(sortf12) == 0) && (sortf21.compareToIgnoreCase(sortf22) > 0))
                        || ((sortf11.compareToIgnoreCase(sortf12) == 0) && (sortf21.compareToIgnoreCase(sortf22) == 0) && (sortf31 < sortf32))) {
                    // wisselen
                    temp.setLogItem(logboekList.get(j));
                    logboekList.get(j).setLogItem(logboekList.get(j-1));
                    logboekList.get(j-1).setLogItem(temp);
                }
            }
        }
        return logboekList;
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}