package be.hvwebsites.itembord;

import android.content.Intent;
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

import be.hvwebsites.itembord.adapters.StatusBordItemListAdapter;
import be.hvwebsites.itembord.adapters.TextItemListAdapter;
import be.hvwebsites.itembord.constants.SpecificData;
import be.hvwebsites.itembord.entities.Opvolgingsitem;
import be.hvwebsites.itembord.services.FileBaseService;
import be.hvwebsites.itembord.viewmodels.EntitiesViewModel;
import be.hvwebsites.libraryandroid4.helpers.IDNumber;
import be.hvwebsites.libraryandroid4.helpers.ListItemHelper;
import be.hvwebsites.libraryandroid4.repositories.CookieRepository;
import be.hvwebsites.libraryandroid4.returninfo.ReturnInfo;
import be.hvwebsites.libraryandroid4.statics.StaticData;

public class OItemStatusBord extends AppCompatActivity {
    private EntitiesViewModel viewModel;
    // Opvolgingsitems
    private List<ListItemHelper> oItemList = new ArrayList<>();
    // Filters
    private IDNumber filterRubriekID = new IDNumber(StaticData.IDNUMBER_NOT_FOUND.getId());
    // Device
    private final String deviceModel = Build.MODEL;
    // Default rubriek index in spinner list
    private static final int DEFAULT_RUBRIEK_INDEX = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_status_bord);
        setTitle("Opvolgingsitems Status Bord");

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

        // Cookierepository definieren
        CookieRepository cookieRepository = new CookieRepository(baseDir);

        // Zet calling activity
        cookieRepository.registerCookie(SpecificData.CALLING_ACTIVITY, SpecificData.ACTIVITY_STATUSBORD);

        // Recyclerview definieren
        RecyclerView recyclerView = findViewById(R.id.recycStatusBord);
        final StatusBordItemListAdapter recyclerAdapter = new StatusBordItemListAdapter(this);
        recyclerView.setAdapter(recyclerAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerAdapter.setCallingActivity(SpecificData.ACTIVITY_STATUSBORD);
        recyclerAdapter.setEntityType(SpecificData.ENTITY_TYPE_OPVOLGINGSITEM);

        // Rubriekfilter Spinner en adapter definieren
        Spinner rubriekFilterSpinner = (Spinner) findViewById(R.id.spinrStatusBordRubriek);
        // rubriekfilterAdapter obv ListItemHelper
        ArrayAdapter<ListItemHelper> rubriekFilterAdapter = new ArrayAdapter(this,
                android.R.layout.simple_spinner_item);
        rubriekFilterAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Rubriekfilterspinner invullen met rubrieken
        List<ListItemHelper> spinnerList = new ArrayList<>();
        spinnerList.addAll(viewModel.getRubriekItemList());
        rubriekFilterAdapter.addAll(spinnerList);

        // Rubriekfilter bepalen
        Intent statBordIntent = getIntent();
        if (statBordIntent.hasExtra(SpecificData.COOKIE_RUBRIEK_FILTER)){
            filterRubriekID.setId(statBordIntent.getIntExtra(SpecificData.COOKIE_RUBRIEK_FILTER,0));
        } else if ((cookieRepository.bestaatCookie(SpecificData.COOKIE_RUBRIEK_FILTER)
                != StaticData.ITEM_NOT_FOUND)) {
            // Er is een rubriekfilter
            filterRubriekID.setId(Integer.parseInt(
                    cookieRepository.getCookieValueFromLabel(SpecificData.COOKIE_RUBRIEK_FILTER)
            ));
        } else {
            // Er is nog geen rubriekfilter, neem eerste rubriek uit spinnerlist
            filterRubriekID.setId(spinnerList.get(DEFAULT_RUBRIEK_INDEX).getItemID().getId());
        }

        // Gekozen rubriek selecteren in rubriekfilter
        int rubIndex = DEFAULT_RUBRIEK_INDEX;
        if (filterRubriekID.getId() != StaticData.IDNUMBER_NOT_FOUND.getId()){
            rubIndex = determineIndexInList(spinnerList, filterRubriekID);
        }
        rubriekFilterSpinner.setAdapter(rubriekFilterAdapter);
        rubriekFilterSpinner.setSelection(rubIndex, false);

        // Recyclerlist invullen obv rubriekfilter
        oItemList = buildStatusbord(viewModel.getOpvolgingsItemListByRubriekID(filterRubriekID));
        recyclerAdapter.setItemList(oItemList);
        if (oItemList.size() == 0){
            Toast.makeText(this,
                    SpecificData.NO_OPVOLGINGSITEMS_YET,
                    Toast.LENGTH_LONG).show();
        }

        // selection listener activeren, moet gebueren nadat de adapter gekoppeld is aan de spinner !!
        rubriekFilterSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                ListItemHelper selHelper = (ListItemHelper) adapterView.getItemAtPosition(i);
                filterRubriekID = selHelper.getItemID();
                if (viewModel.getRubriekIndexById(filterRubriekID) == StaticData.ITEM_NOT_FOUND){
                    // De gekozen rubriek bestaat niet !
                    Toast.makeText(getBaseContext(),
                            SpecificData.RUBRIEK_UNKNOWN,
                            Toast.LENGTH_LONG).show();
                }else {
                    // Recyclerlist invullen obv rubriekfilter
                    oItemList.clear();
                    oItemList = buildStatusbord(viewModel.getOpvolgingsItemListByRubriekID(filterRubriekID));
                    recyclerAdapter.setItemList(oItemList);
                    recyclerAdapter.setEntityType(SpecificData.ENTITY_TYPE_OPVOLGINGSITEM);
                    recyclerView.setAdapter(recyclerAdapter);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private Integer determineIndexInList(List<ListItemHelper> inList, IDNumber inID){
        for (int i = 0; i < inList.size() ; i++){
            if (inList.get(i).getItemID().getId() == inID.getId()){
                return i;
            }
        }
        return StaticData.ITEM_NOT_FOUND;
    }

    private List<ListItemHelper> buildStatusbord(List<Opvolgingsitem> inList){
        List<ListItemHelper> listStatusBord = new ArrayList<>();
        for (int i = 0; i < inList.size() ; i++) {
            listStatusBord.add(new ListItemHelper(
                    inList.get(i).getDisplayOitemStatusBord()
                    , SpecificData.STYLE_STATUSBORD
                    , inList.get(i).getEntityId()
            ));
        }
        return listStatusBord;
    }
}