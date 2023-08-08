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

import be.hvwebsites.itembord.adapters.TextItemListAdapter;
import be.hvwebsites.itembord.constants.SpecificData;
import be.hvwebsites.itembord.entities.Opvolgingsitem;
import be.hvwebsites.itembord.services.FileBaseService;
import be.hvwebsites.itembord.viewmodels.EntitiesViewModel;
import be.hvwebsites.libraryandroid4.adapters.NothingSelectedSpinnerAdapter;
import be.hvwebsites.libraryandroid4.helpers.IDNumber;
import be.hvwebsites.libraryandroid4.helpers.ListItemHelper;
import be.hvwebsites.libraryandroid4.repositories.CookieRepository;
import be.hvwebsites.libraryandroid4.returninfo.ReturnInfo;
import be.hvwebsites.libraryandroid4.statics.StaticData;

public class OItemStatusBord extends AppCompatActivity {
    private EntitiesViewModel viewModel;
    private List<ListItemHelper> oItemList = new ArrayList<>();
    // Filters
    private IDNumber filterRubriekID = new IDNumber(StaticData.IDNUMBER_NOT_FOUND.getId());
    // Device
    private final String deviceModel = Build.MODEL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_status_bord);
        setTitle("Opvolgingsitems Status Bord");

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

        // Cookierepository definieren
        CookieRepository cookieRepository = new CookieRepository(baseDir);

        // Zet calling activity
        cookieRepository.registerCookie(SpecificData.CALLING_ACTIVITY, SpecificData.ACTIVITY_STATUSBORD);

        // Recyclerview definieren
        RecyclerView recyclerView = findViewById(R.id.recycStatusBord);
        final TextItemListAdapter recyclerAdapter = new TextItemListAdapter(this);
        recyclerView.setAdapter(recyclerAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerAdapter.setCallingActivity(SpecificData.ACTIVITY_STATUSBORD);

        // Rubriekfilter Spinner en adapter definieren
        Spinner rubriekFilterSpinner = (Spinner) findViewById(R.id.spinrStatusBordRubriek);
        // rubriekfilterAdapter obv ListItemHelper
        ArrayAdapter<ListItemHelper> rubriekFilterAdapter = new ArrayAdapter(this,
                android.R.layout.simple_spinner_item);
        rubriekFilterAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Cookie rubriekfilter ophalen
        if (cookieRepository.bestaatCookie(SpecificData.COOKIE_RUBRIEK_FILTER)
                != StaticData.ITEM_NOT_FOUND){
            // Er is een rubriekfilter
            filterRubriekID.setId(Integer.parseInt(
                            cookieRepository.getCookieValueFromLabel(SpecificData.COOKIE_RUBRIEK_FILTER)
                    ));
            // Recyclerlist invullen obv rubriekfilter
            oItemList = buildStatusbord(viewModel.getOpvolgingsItemListByRubriekID(filterRubriekID));

        }else {
            // Er is nog geen rubriekfilter
            rubriekFilterSpinner.setAdapter(new NothingSelectedSpinnerAdapter(
                    rubriekFilterAdapter, R.layout.contact_spinner_row_nothing_selected, this
            ));
            // Recyclerlist invullen obv alle opvolgingsitems
            oItemList = buildStatusbord(viewModel.getOpvolgingsitemList());
        }

/*
        // Rubriekspinner vullen met rubrieken
        rubriekFilterAdapter.add(new ListItemHelper(SpecificData.NO_RUBRIEK_FILTER,
                "",
                StaticData.IDNUMBER_NOT_FOUND));
*/
        rubriekFilterAdapter.addAll(viewModel.getRubriekItemList());
        rubriekFilterSpinner.setAdapter(rubriekFilterAdapter);

        // Recyclerview invullen met opvolgingsitems
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
                if (filterRubriekID.getId() != StaticData.IDNUMBER_NOT_FOUND.getId()){

                    // Spinner selectie zetten
                    //rubriekFilterSpinner.setSelection(i, false);

                }
                // Recyclerlist invullen obv rubriekfilter
                oItemList.clear();
                oItemList = buildStatusbord(viewModel.getOpvolgingsItemListByRubriekID(filterRubriekID));
                recyclerAdapter.setItemList(oItemList);
                recyclerView.setAdapter(recyclerAdapter);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private List<ListItemHelper> buildStatusbord(List<Opvolgingsitem> inList){
        List<ListItemHelper> listStatusBord = new ArrayList<>();
        for (int i = 0; i < inList.size() ; i++) {
            listStatusBord.add(new ListItemHelper(
                    inList.get(i).getDisplayOitemStatusBord()
                    , ""
                    , inList.get(i).getRubriekId()
            ));
        }
        return listStatusBord;
    }
}