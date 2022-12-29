package be.hvwebsites.itembord;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import be.hvwebsites.itembord.adapters.TextItemListAdapter;
import be.hvwebsites.itembord.constants.SpecificData;
import be.hvwebsites.itembord.services.FileBaseService;
import be.hvwebsites.itembord.viewmodels.EntitiesViewModel;
import be.hvwebsites.libraryandroid4.helpers.IDNumber;
import be.hvwebsites.libraryandroid4.helpers.ListItemHelper;
import be.hvwebsites.libraryandroid4.returninfo.ReturnInfo;
import be.hvwebsites.libraryandroid4.statics.StaticData;

public class ManageRubriek extends AppCompatActivity {
    // Activiteit om rubrieken te beheren (toevoegen, aanpassen, deleten)
    private EntitiesViewModel viewModel;
    private List<ListItemHelper> itemList = new ArrayList<>();
    // Device
    private final String deviceModel = Build.MODEL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_rubriek);

        // FloatingActionButton
        FloatingActionButton fab = findViewById(R.id.fab_manage_rubriek);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent manIntent = new Intent(ManageRubriek.this, EditRubriek.class);
                manIntent.putExtra(StaticData.EXTRA_INTENT_KEY_ACTION, StaticData.ACTION_NEW);
                startActivity(manIntent);

            }
        });

        // Creer een filebase service (bevat file base en file base directory) obv device en package name
        FileBaseService fileBaseService = new FileBaseService(deviceModel, getPackageName());

        // Data ophalen
        // Get a viewmodel from the viewmodelproviders
        viewModel = new ViewModelProvider(this).get(EntitiesViewModel.class);
        // Basis directory definitie
        String baseDir = fileBaseService.getFileBaseDir();
        // String baseDir = getBaseContext().getFilesDir().getAbsolutePath(); // internal files
        // Initialize viewmodel mt basis directory (data wordt opgehaald in viewmodel)
        List<ReturnInfo> viewModelRetInfo = viewModel.initializeViewModel(baseDir);
        for (int i = 0; i < viewModelRetInfo.size(); i++) {
            Toast.makeText(ManageRubriek.this,
                    viewModelRetInfo.get(i).getReturnMessage(),
                    Toast.LENGTH_SHORT).show();
        }

        // Recyclerview definieren
        RecyclerView recyclerView = findViewById(R.id.recycler_manage_rubriek);
        final TextItemListAdapter adapter = new TextItemListAdapter(this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        itemList.clear();

        // Recyclerview vullen
        // setTitle("Beheer Rubrieken");
        itemList.addAll(viewModel.getRubriekItemList());

        // om te kunnen swipen in de recyclerview ; swippen == deleten
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
                        Toast.makeText(ManageRubriek.this,
                                "Deleting item ... ",
                                Toast.LENGTH_LONG).show();
                        // Bepalen entity IDNumber to be deleted
                        int position = viewHolder.getAdapterPosition();
                        IDNumber idNumberToBeDeleted = itemList.get(position).getItemID();
                        // Leegmaken itemlist
                        itemList.clear();
                        // Delete
                        viewModel.deleteRubriekByID(idNumberToBeDeleted);
                        itemList.addAll(viewModel.getRubriekItemList());
                        // Refresh recyclerview
                        adapter.setEntityType(SpecificData.ENTITY_TYPE_RUBRIEK);
                        adapter.setItemList(itemList);
                    }
                });
        helper.attachToRecyclerView(recyclerView);
        adapter.setEntityType(SpecificData.ENTITY_TYPE_RUBRIEK);
        adapter.setItemList(itemList);
    }
}