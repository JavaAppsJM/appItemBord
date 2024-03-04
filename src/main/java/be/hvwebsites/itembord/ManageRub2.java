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

public class ManageRub2 extends AppCompatActivity {
    // Activiteit om rubrieken te beheren (toevoegen, aanpassen, deleten)
    private EntitiesViewModel viewModel;
    private List<ListItemHelper> itemList = new ArrayList<>();
    // Device
    private final String deviceModel = Build.MODEL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_rub2);

        // Creer een filebase service, bepaalt file base directory obv device en Context
        FileBaseService fileBaseService = new FileBaseService(deviceModel, this);
        // Basis directory definitie
        String baseDir = fileBaseService.getFileBaseDir();

        // Data ophalen
        // Get a viewmodel from the viewmodelproviders
        viewModel = new ViewModelProvider(this).get(EntitiesViewModel.class);
        // Initialize viewmodel mt basis directory (data wordt opgehaald in viewmodel)
        List<ReturnInfo> viewModelRetInfo = viewModel.initializeViewModel(baseDir);
        // Als er errors of opmerkingen zijn, worden ze hier getoond
        for (int i = 0; i < viewModelRetInfo.size(); i++) {
            Toast.makeText(ManageRub2.this,
                    viewModelRetInfo.get(i).getReturnMessage(),
                    Toast.LENGTH_SHORT).show();
        }

        FloatingActionButton fab = findViewById(R.id.fabRubList);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ManageRub2.this,
                        EditRubriek.class);
                intent.putExtra(StaticData.EXTRA_INTENT_KEY_ACTION, StaticData.ACTION_NEW);
                intent.putExtra(StaticData.EXTRA_INTENT_KEY_RETURN, SpecificData.ACTIVITY_MANAGE_RUBRIEK);
                startActivity(intent);
            }
        });

        // Recyclerview definieren
        RecyclerView recyclerView = findViewById(R.id.recycRubList);
        final TextItemListAdapter adapter = new TextItemListAdapter(this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        itemList.clear();

        // Recyclerview vullen
        itemList.addAll(viewModel.getRubriekItemsInStyle());

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
                        Toast.makeText(ManageRub2.this,
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
                        adapter.setCallingActivity(SpecificData.ACTIVITY_MANAGE_RUBRIEK);
                        adapter.setItemList(itemList);
                    }
                });
        helper.attachToRecyclerView(recyclerView);
        adapter.setEntityType(SpecificData.ENTITY_TYPE_RUBRIEK);
        adapter.setCallingActivity(SpecificData.ACTIVITY_MANAGE_RUBRIEK);
        adapter.setItemList(itemList);

        boolean debug = true;

    }
}