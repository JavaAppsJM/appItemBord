package be.hvwebsites.itembord;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import be.hvwebsites.itembord.adapters.StatusbordItemListAdapter;
import be.hvwebsites.itembord.constants.SpecificData;
import be.hvwebsites.itembord.entities.Opvolgingsitem;
import be.hvwebsites.itembord.helpers.ListItemStatusbordHelper;
import be.hvwebsites.itembord.viewmodels.EntitiesViewModel;
import be.hvwebsites.libraryandroid4.returninfo.ReturnInfo;

public class MainActivity extends AppCompatActivity {
    private EntitiesViewModel viewModel;
    private List<ListItemStatusbordHelper> itemList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Data ophalen
        // Get a viewmodel from the viewmodelproviders
        viewModel = new ViewModelProvider(this).get(EntitiesViewModel.class);
        // Basis directory definitie
        String baseDir = getBaseContext().getExternalFilesDir(null).getAbsolutePath();
        // Initialize viewmodel mt basis directory (data wordt opgehaald in viewmodel)
        ReturnInfo viewModelStatus = viewModel.initializeViewModel(baseDir);
        if (viewModelStatus.getReturnCode() == 0) {
            // Files gelezen
        } else if (viewModelStatus.getReturnCode() == 100) {
            Toast.makeText(MainActivity.this,
                    viewModelStatus.getReturnMessage(),
                    Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(MainActivity.this,
                    "Ophalen data is mislukt",
                    Toast.LENGTH_LONG).show();
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
        if (itemList == null){
            Toast.makeText(this,
                    SpecificData.NO_STATUSBORDITEMS_YET,
                    Toast.LENGTH_LONG).show();
        }
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
}