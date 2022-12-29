package be.hvwebsites.itembord;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import be.hvwebsites.itembord.adapters.StatusbordItemListAdapter;
import be.hvwebsites.itembord.constants.SpecificData;
import be.hvwebsites.itembord.entities.Opvolgingsitem;
import be.hvwebsites.itembord.helpers.ListItemStatusbordHelper;
import be.hvwebsites.itembord.services.FileBaseService;
import be.hvwebsites.itembord.viewmodels.EntitiesViewModel;
import be.hvwebsites.libraryandroid4.returninfo.ReturnInfo;
import be.hvwebsites.libraryandroid4.statics.StaticData;

public class MainActivity extends AppCompatActivity {
    private EntitiesViewModel viewModel;
    private List<ListItemStatusbordHelper> itemList = new ArrayList<>();
    // Device
    private final String deviceModel = Build.MODEL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
            Toast.makeText(MainActivity.this,
                    viewModelRetInfo.get(i).getReturnMessage(),
                    Toast.LENGTH_SHORT).show();
        }

        // Recyclerview definieren
        RecyclerView recyclerView = findViewById(R.id.recycler_statusbord);
        final StatusbordItemListAdapter adapter = new StatusbordItemListAdapter(this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Recyclerview invullen met statusbord items
        itemList.clear();
        itemList.addAll(buildStatusbordList());
        adapter.setItemList(itemList);
        if (itemList.size() == 0){
            Toast.makeText(this,
                    SpecificData.NO_STATUSBORDITEMS_YET,
                    Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    private List<ListItemStatusbordHelper> buildStatusbordList(){
        List<ListItemStatusbordHelper> statusbordList = new ArrayList<>();
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
                        opvolgingsitem.calculateNextDate().getFormatDate() +
                        " Vorige: " +
                        opvolgingsitem.getLatestDate().getFormatDate();
                // Creer statusborditem en steek in statusbordlist
                statusbordList.add(new ListItemStatusbordHelper(item1,
                        item2,
                        "",
                        opvolgingsitem.getEntityId()));
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
        // TODO: Menu items definieren
        Intent mainIntent;
        switch (item.getItemId()) {
            case R.id.menu_beheer_rubrieken:
                // ga naar activity ManageRubriek
                mainIntent = new Intent(MainActivity.this, ManageRubriek.class);
                startActivity(mainIntent);
                return true;
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
}