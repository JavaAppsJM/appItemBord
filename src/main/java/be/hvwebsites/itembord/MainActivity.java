package be.hvwebsites.itembord;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import be.hvwebsites.itembord.adapters.StatusbordItemListAdapter;
import be.hvwebsites.itembord.constants.SpecificData;
import be.hvwebsites.itembord.entities.Opvolgingsitem;
import be.hvwebsites.itembord.fragments.FlexDialogFragment;
import be.hvwebsites.itembord.helpers.ListItemStatusbordHelper;
import be.hvwebsites.itembord.interfaces.FlexDialogInterface;
import be.hvwebsites.itembord.services.FileBaseService;
import be.hvwebsites.itembord.viewmodels.EntitiesViewModel;
import be.hvwebsites.libraryandroid4.helpers.DateString;
import be.hvwebsites.libraryandroid4.helpers.IDNumber;
import be.hvwebsites.libraryandroid4.returninfo.ReturnInfo;
import be.hvwebsites.libraryandroid4.statics.StaticData;

public class MainActivity extends AppCompatActivity implements FlexDialogInterface {
    private EntitiesViewModel viewModel;
    private List<ListItemStatusbordHelper> itemList = new ArrayList<>();
    private IDNumber opvolgingsitemID;
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

        // Als er lang geklikt is op een oitem, kan dat hier gecapteerd worden
        adapter.setOnItemClickListener(new StatusbordItemListAdapter.ClickListener() {
            @Override
            public void onItemClicked(IDNumber itemID, View v) {
                // Opvolgingsitem ID global maken
                opvolgingsitemID.setId(itemID.getId());
                // Via een dialog vragen om te bevestigen om oitem af te vinken
                // Create an instance of the dialog fragment and show it
                FlexDialogFragment oitemDialog = new FlexDialogFragment();
                oitemDialog.setSubjectDialog("Oitem");
                oitemDialog.show(getSupportFragmentManager(), "oItemDialogFragment");
            }
        });


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
                        opvolgingsitem.getNextDateDS().getFormatDate() +
                        " Vorige: " +
                        opvolgingsitem.getLatestDate().getFormatDate();
                // Creer statusborditem en steek in statusbordlist
                statusbordList.add(new ListItemStatusbordHelper(item1,
                        item2,
                        opvolgingsitem.getDisplayStyle(),
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

        /** Menu items definieren */
        Intent mainIntent;
        switch (item.getItemId()) {
            case R.id.menu_beheer_rubrieken:
                // ga naar activity ManageRubriek
                mainIntent = new Intent(MainActivity.this, ManageRubriek.class);
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
        Opvolgingsitem oItem = viewModel.getOpvolgingsitemById(opvolgingsitemID);
        DateString tempDs = new DateString();
        tempDs.setDateToday();
        oItem.setLatestDate(tempDs);

        // TODO: Opvolgingsitem itemID moet afgevinkt worden
        // TODO: oitem bepalen
        // TODO: latestdate en vervaldatum herbepalen
        // TODO: updaten oitem

        // TODO: via een dialog vragen of er een log moet aangemaakt worden
        FlexDialogFragment logDialog = new FlexDialogFragment();
        logDialog.setSubjectDialog("Log");
        logDialog.show(getSupportFragmentManager(), "LogDialogFragment");

        // TODO: agenda wijzigen indien nodig
    }

    @Override
    public void onDateDialogNegativeClick(DialogFragment dialogFragment) {
        // TODO: Opvolgingsitem moet latest date = vervaldatum nemen

    }

    @Override
    public void onLogDialogPositiveClick(DialogFragment dialogFragment, String subject) {
        // TODO: via een dialog vragen of er een log moet aangemaakt worden
        FlexDialogFragment logDialog = new FlexDialogFragment();
        logDialog.setSubjectDialog("Log");
        logDialog.show(getSupportFragmentManager(), "LogDialogFragment");

        // TODO: Opvolgingsitem itemID moet afgevinkt worden
        // TODO: oitem bepalen
        Opvolgingsitem oItem = viewModel.getOpvolgingsitemById(opvolgingsitemID);
        oItem.setLatestDate(oItem.calculateNextDate());
        // TODO: latestdate en vervaldatum herbepalen
        // TODO: updaten oitem

        // TODO: via een dialog vragen of er een log moet aangemaakt worden
        FlexDialogFragment logDialog = new FlexDialogFragment();
        logDialog.setSubjectDialog("Log");
        logDialog.show(getSupportFragmentManager(), "LogDialogFragment");

        // TODO: agenda wijzigen indien nodig

    }

    @Override
    public void onLogDialogNegativeClick(DialogFragment dialogFragment) {

    }

    @Override
    public void onEventDialogPositiveClick(DialogFragment dialogFragment) {

    }

    @Override
    public void onEventDialogNegativeClick(DialogFragment dialogFragment) {

    }

}