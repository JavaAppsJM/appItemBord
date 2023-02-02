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
import be.hvwebsites.itembord.helpers.ListItemLogboekHelper;
import be.hvwebsites.itembord.services.FileBaseService;
import be.hvwebsites.itembord.viewmodels.EntitiesViewModel;
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
        rubriekFilterAdapter.add(new ListItemHelper(SpecificData.NO_OPVOLGINGSITEM_FILTER,
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
        logboekList.addAll(buildLogboek());
        logboekAdapter.setItemList(logboekList);
        if (logboekList.size() == 0){
            Toast.makeText(this,
                    SpecificData.NO_LOGBOEKITEMS_YET,
                    Toast.LENGTH_LONG).show();
        }

        // selection listener activeren, moet gebueren nadat de adapter gekoppeld is aan de spinner !!
        // TODO: Manier 1 om de selectie per spinner te kunnen capteren
        rubriekFilterSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        //TODO: Manier2 om de selectie per spinner te kunnen captern
        oItemFilterSpinner.setOnItemSelectedListener((AdapterView.OnItemSelectedListener) this);

    }

    // Als er iets geselecteerd is in de spinner
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        // TODO: Hoe weet je in welke spinner geselecteerd werd ? via view ?
        if (parent.getItemAtPosition(position) != null){
            // TODO: Kijken in welke view geselecteerd werd
            if (view.getId() == R.id.spinr_oitem){

            }
/*
            // Bepalen wat geselecteerd is
            ListItemHelper selecRubriek = (ListItemHelper) parent.getItemAtPosition(position);
            parentRubriekId = selecRubriek.getItemID();
            if (parentRubriekId.getId() != StaticData.ITEM_NOT_FOUND){
                Toast.makeText(EditRubriek.this,
                        "Hoofdrubriek gekozen ...",
                        Toast.LENGTH_SHORT).show();
            }else {
                Toast.makeText(EditRubriek.this,
                        "Geen hoofdrubriek gekozen ...",
                        Toast.LENGTH_SHORT).show();
            }
*/
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }

    private List<ListItemLogboekHelper> buildLogboek(){
        List<ListItemLogboekHelper> logboekList = new ArrayList<>();
        String item1;
        String item2;
        Log log;
        Opvolgingsitem opvolgingsitem;
        int indexhelp;

        // Bepaal voor elke logitem de rubriek en het opvolgingsitem
        for (int i = 0; i < viewModel.getLogList().size(); i++) {
            log = viewModel.getLogList().get(i);
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
}