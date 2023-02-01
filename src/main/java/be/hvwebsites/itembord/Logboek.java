package be.hvwebsites.itembord;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Build;
import android.os.Bundle;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import be.hvwebsites.itembord.adapters.LoboekItemListAdapter;
import be.hvwebsites.itembord.constants.SpecificData;
import be.hvwebsites.itembord.entities.Log;
import be.hvwebsites.itembord.entities.Opvolgingsitem;
import be.hvwebsites.itembord.helpers.ListItemLogboekHelper;
import be.hvwebsites.itembord.helpers.ListItemStatusbordHelper;
import be.hvwebsites.itembord.services.FileBaseService;
import be.hvwebsites.itembord.viewmodels.EntitiesViewModel;
import be.hvwebsites.libraryandroid4.returninfo.ReturnInfo;

public class Logboek extends AppCompatActivity {
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

        // Recyclerview definieren
        RecyclerView recyclerView = findViewById(R.id.recyc_logboek);
        final LoboekItemListAdapter logboekAdapter = new LoboekItemListAdapter(this);
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