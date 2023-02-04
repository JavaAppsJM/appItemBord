package be.hvwebsites.itembord;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import be.hvwebsites.itembord.adapters.LogboekItemListAdapter;
import be.hvwebsites.itembord.constants.SpecificData;
import be.hvwebsites.itembord.entities.Log;
import be.hvwebsites.itembord.entities.Opvolgingsitem;
import be.hvwebsites.itembord.entities.Rubriek;
import be.hvwebsites.itembord.helpers.ListItemLogboekHelper;
import be.hvwebsites.itembord.services.FileBaseService;
import be.hvwebsites.itembord.viewmodels.EntitiesViewModel;
import be.hvwebsites.libraryandroid4.helpers.IDNumber;
import be.hvwebsites.libraryandroid4.helpers.ListItemHelper;
import be.hvwebsites.libraryandroid4.returninfo.ReturnInfo;
import be.hvwebsites.libraryandroid4.statics.StaticData;

public class Logboek extends AppCompatActivity implements AdapterView.OnItemSelectedListener{
    private EntitiesViewModel viewModel;
    private List<ListItemLogboekHelper> logboekList = new ArrayList<>();
    // Device
    private final String deviceModel = Build.MODEL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logboek);

        // Creer een filebase service (bevat file base en file base directory) obv device en package name
        FileBaseService fileBaseService = new FileBaseService(deviceModel, getPackageName());

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
        rubriekFilterAdapter.addAll(viewModel.getRubriekItemList());
        rubriekFilterAdapter.add(new ListItemHelper(SpecificData.NO_RUBRIEK_FILTER,
                "",
                StaticData.IDNUMBER_NOT_FOUND));
        rubriekFilterSpinner.setAdapter(rubriekFilterAdapter);

        // Opvolgingsitemfilter Spinner en adapter definieren
        Spinner oItemFilterSpinner = (Spinner) findViewById(R.id.spinr_oitem);
        // OpvolgingsitemfilterAdapter obv ListItemHelper
        ArrayAdapter<ListItemHelper> oItemFilterAdapter = new ArrayAdapter(this,
                android.R.layout.simple_spinner_item);
        oItemFilterAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Opvolgingsitemfilter invullen
        //rubriekFilterAdapter.addAll(viewModel.getRubriekItemList());
        oItemFilterAdapter.add(new ListItemHelper(SpecificData.NO_OPVOLGINGSITEM_FILTER,
                "",
                StaticData.IDNUMBER_NOT_FOUND));
        oItemFilterSpinner.setAdapter(oItemFilterAdapter);

        // Recyclerview definieren
        RecyclerView recyclerView = findViewById(R.id.recyc_logboek);
        final LogboekItemListAdapter logboekAdapter = new LogboekItemListAdapter(this);
        recyclerView.setAdapter(logboekAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Recyclerview invullen met statusbord items
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
                IDNumber selID = selHelper.getItemID();
                if (selID.getId() != StaticData.IDNUMBER_NOT_FOUND.getId()){
                    Rubriek selRubriek = viewModel.getRubriekById(selID);
                    // Spinner selectie zetten
                    rubriekFilterSpinner.setSelection(i, false);
                    // Inhoud vd opvolgingsitem filter bepalen adhv gekozen rubriek
                    oItemFilterAdapter.clear();
                    oItemFilterAdapter.addAll(viewModel.getOpvolgingsItemItemListByRubriekID(selID));
                    oItemFilterAdapter.add(new ListItemHelper(SpecificData.NO_OPVOLGINGSITEM_FILTER,
                            "",
                            StaticData.IDNUMBER_NOT_FOUND));
                    oItemFilterSpinner.setAdapter(oItemFilterAdapter);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        oItemFilterSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                ListItemHelper selHelper = (ListItemHelper) adapterView.getItemAtPosition(i);
                IDNumber selID = selHelper.getItemID();
                if (selID.getId() != StaticData.IDNUMBER_NOT_FOUND.getId()){
                    Opvolgingsitem selOItem = viewModel.getOpvolgingsitemById(selID);
                    // Spinner selectie zetten
                    oItemFilterSpinner.setSelection(i, false);
                    // Inhoud vd logboek bepalen adhv gekozen rubriek
                    logboekList.clear();
                    logboekList.addAll(buildLogboek(selOItem.getRubriekId(), selOItem.getEntityId()));
                    logboekAdapter.setItemList(logboekList);
                }else {
                    oItemFilterSpinner.setSelection(i, false);
                    // Inhoud vd logboek bepalen adhv gekozen rubriek
                    logboekList.clear();

                    // TODO: Wat als rubriek als filter wel gekozen werd !!!!
                    logboekList.addAll(buildLogboek(selID, selID));
                    logboekAdapter.setItemList(logboekList);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

    }

    private List<ListItemLogboekHelper> buildLogboek(IDNumber rubriekID, IDNumber oItemID){
        List<ListItemLogboekHelper> logboekList = new ArrayList<>();
        String item1;
        String item2;
        Log log;
        Opvolgingsitem opvolgingsitem;
        int indexhelp;

        // Bepaal voor elke logitem dat voldoet, lijn1 & 2
        for (int i = 0; i < viewModel.getLogList().size(); i++) {
            log = viewModel.getLogList().get(i);
            if (((rubriekID.getId() != StaticData.IDNUMBER_NOT_FOUND.getId())
                    && (log.getRubriekId().getId() == rubriekID.getId()))
            && ((oItemID.getId() != StaticData.IDNUMBER_NOT_FOUND.getId())
            &&(log.getItemId().getId() == oItemID.getId()))){
                opvolgingsitem = viewModel.getOpvolgingsitemById(log.getItemId());
                // Bepaal lijn1
                item1 = log.getLogDate().getFormatDate()
                        + ": "
                        + viewModel.getRubriekById(log.getRubriekId()).getEntityName();

                // Opvolgingsitem kan null zijn !
                if (opvolgingsitem != null){
                    item1 = item1 + ": " + opvolgingsitem.getEntityName();
                }

                // Vul tweede lijn in
                item2 = log.getLogDescTrunc(70);
                // Creer logboekitem en steek in list
                logboekList.add(new ListItemLogboekHelper(item1,
                        item2,
                        "",
                        log.getEntityId(),
                        log.getRubriekId().getId(),
                        log.getLogDate().getIntDate()));
            }
        }

        // Logboeklist sorteren op rubriek, datum
        ListItemLogboekHelper temp = new ListItemLogboekHelper();
        int sortf11, sortf12 ,sortf21, sortf22;
        for (int i = logboekList.size() ; i > 0 ; i--) {
            for (int j = 1 ; j < i ; j++) {
                sortf11 = logboekList.get(j-1).getSortField1();
                sortf12 = logboekList.get(j).getSortField1();
                sortf21 = logboekList.get(j-1).getSortField2();
                sortf22 = logboekList.get(j).getSortField2();
                if ((sortf11 > sortf12)
                        || ((sortf11 == sortf12) && (sortf21 < sortf22))) {
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