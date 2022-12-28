package be.hvwebsites.itembord;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import be.hvwebsites.itembord.adapters.TextItemListAdapter;
import be.hvwebsites.itembord.constants.SpecificData;
import be.hvwebsites.itembord.entities.Rubriek;
import be.hvwebsites.itembord.services.FileBaseService;
import be.hvwebsites.itembord.viewmodels.EntitiesViewModel;
import be.hvwebsites.libraryandroid4.helpers.IDNumber;
import be.hvwebsites.libraryandroid4.helpers.ListItemHelper;
import be.hvwebsites.libraryandroid4.returninfo.ReturnInfo;
import be.hvwebsites.libraryandroid4.statics.StaticData;

public class EditRubriek extends AppCompatActivity {
    private EntitiesViewModel viewModel;
    private List<ListItemHelper> itemList = new ArrayList<>();
    private int iDToUpdate = StaticData.ITEM_NOT_FOUND;
    private String action = StaticData.ACTION_NEW;
    private TextView parentRubriekView;
    private TextView labelSubrubriekView;
    private TextView labelOpvolgingsitemView;
    private TextView labelLogboekView;
    private String listEntityType;
    private IDNumber parentRubriekId = StaticData.IDNUMBER_NOT_FOUND;
    // Device
    private final String deviceModel = Build.MODEL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_rubriek);
        parentRubriekView = findViewById(R.id.nameHoofrubriek);

        // Creer een filebase service (bevat file base en file base directory) obv device en package name
        FileBaseService fileBaseService = new FileBaseService(deviceModel, getPackageName());

        // Get a viewmodel from the viewmodelproviders
        viewModel = new ViewModelProvider(this).get(EntitiesViewModel.class);
        // Basis directory definitie
        String baseDir = fileBaseService.getFileBaseDir();
        // Initialize viewmodel mt basis directory (data wordt opgehaald in viewmodel)
        ReturnInfo viewModelStatus = viewModel.initializeViewModel(baseDir);
        if (viewModelStatus.getReturnCode() != 0) {
            Toast.makeText(EditRubriek.this,
                    "Ophalen data is mislukt",
                    Toast.LENGTH_LONG).show();
        }

        // Intent bekijken vr action
        Intent editIntent = getIntent();
        if (editIntent.hasExtra(StaticData.EXTRA_INTENT_KEY_ACTION)){
            action = editIntent.getStringExtra(StaticData.EXTRA_INTENT_KEY_ACTION);
        }
        // Als parentId in intent zit ...
        if (editIntent.hasExtra(SpecificData.ID_RUBRIEK)) {
            parentRubriekId = new IDNumber(editIntent.getIntExtra(SpecificData.ID_RUBRIEK, 0));
            //TODO: Hoofdrubriek moet aanpasbaar gemaakt worden met een spinner ?
            parentRubriekView.setText(viewModel.getRubriekById(parentRubriekId).getEntityName());
        }

        // Vul Scherm in vlgns new/update
        switch (action) {
            case StaticData.ACTION_NEW:
                setTitle(SpecificData.TITLE_NEW_RUBRIEK);
                break;
            case StaticData.ACTION_UPDATE:
                setTitle(SpecificData.TITLE_UPDATE_RUBRIEK);
                // ID uit intent halen om te weten welk element moet aangepast worden
                iDToUpdate = editIntent.getIntExtra(StaticData.EXTRA_INTENT_KEY_ID,
                        StaticData.ITEM_NOT_FOUND);
                Rubriek rubriekToUpdate = new Rubriek();
                rubriekToUpdate.setRubriek(viewModel.getRubriekById(new IDNumber(iDToUpdate)));
                EditText nameView = findViewById(R.id.editNameRubriek);
                nameView.setText(rubriekToUpdate.getEntityName());
                // Enkel indien rubriek gekend is
                // ParentId bepalen als die er is
                if (rubriekToUpdate.getParentId().getId() != StaticData.IDNUMBER_NOT_FOUND.getId()){
                    parentRubriekId = rubriekToUpdate.getParentId();
                    parentRubriekView.setText(viewModel.getRubriekById(parentRubriekId).getEntityName());
                }
                // Recyclerview voor opvolgingsitems/logboek definieren
                RecyclerView recyclerView = findViewById(R.id.recycler_edit_rubriek);
                final TextItemListAdapter adapter = new TextItemListAdapter(this);
                recyclerView.setAdapter(adapter);
                recyclerView.setLayoutManager(new LinearLayoutManager(this));
                itemList.clear();
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
                                Toast.makeText(EditRubriek.this,
                                        "Deleting item ... ",
                                        Toast.LENGTH_LONG).show();
                                // Bepalen entity IDNumber to be deleted
                                int position = viewHolder.getAdapterPosition();
                                IDNumber idNumberToBeDeleted = itemList.get(position).getItemID();
                                // Leegmaken itemlist
                                itemList.clear();
                                if (listEntityType.equals(SpecificData.ENTITY_TYPE_OPVOLGINGSITEM)) {
                                    // Delete opvolgingsitem
                                    viewModel.deleteOpvolgingsitemByID(idNumberToBeDeleted);
                                    itemList.addAll(viewModel.getOpvolgingsItemItemListByRubriekID(rubriekToUpdate.getEntityId()));
                                } else if (listEntityType.equals(SpecificData.ENTITY_TYPE_LOG)) {
                                    // Delete log
                                    viewModel.deleteLogByID(idNumberToBeDeleted);
                                }
                                // Refresh recyclerview
                                adapter.setEntityType(listEntityType);
                                adapter.setCallingActivity(SpecificData.ENTITY_TYPE_RUBRIEK);
                                adapter.setItemList(itemList);
                            }
                        }
                );
                helper.attachToRecyclerView(recyclerView);

                // Sturing Headers en list vn Deel 2
                labelSubrubriekView = findViewById(R.id.labelSubrubriek);
                labelOpvolgingsitemView = findViewById(R.id.labelNameItemList);
                // Subrubrieken voorzien mr enkel activeren indien er bestaan
                try {
                    itemList.addAll(viewModel.getRubriekListByHoofdrubriekID(rubriekToUpdate.getEntityId()));
                    // Er zijn subrubrieken, subrubrieken activeren en laten zien
                    labelSubrubriekView.setTypeface(null, Typeface.BOLD);
                    labelSubrubriekView.setTextColor(ContextCompat.getColor(this,
                            R.color.black));
                    labelSubrubriekView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // format vn logboek
                            labelLogboekView.setTypeface(null, Typeface.NORMAL);
                            labelLogboekView.setTextColor(ContextCompat.getColor(v.getContext(),
                                    R.color.grey));
                            // format vn opvolgingsitem
                            labelOpvolgingsitemView.setTypeface(null, Typeface.NORMAL);
                            labelOpvolgingsitemView.setTextColor(ContextCompat.getColor(v.getContext(),
                                    R.color.grey));
                            // format vn subrubriek
                            labelSubrubriekView.setTypeface(null, Typeface.BOLD);
                            labelSubrubriekView.setTextColor(ContextCompat.getColor(v.getContext(),
                                    R.color.black));
                            // Opvullen recycler list met subrubrieken vr rubriek in kwestie
                            itemList.clear();
                            itemList.addAll(viewModel.getRubriekListByHoofdrubriekID(rubriekToUpdate.getEntityId()));
                            listEntityType = SpecificData.ENTITY_TYPE_RUBRIEK;
                            adapter.setEntityType(SpecificData.ENTITY_TYPE_RUBRIEK);
                            adapter.setCallingActivity(SpecificData.ENTITY_TYPE_RUBRIEK);
                            adapter.setItemList(itemList);
                        }
                    });
                    // Vullen recyclerlist mt rubrieken
                    listEntityType = SpecificData.ENTITY_TYPE_RUBRIEK;
                    adapter.setEntityType(SpecificData.ENTITY_TYPE_RUBRIEK);
                    adapter.setItemList(itemList);
                }catch (NullPointerException ex){
                    // Er zijn geen subrubrieken
                    labelSubrubriekView.setTypeface(null, Typeface.NORMAL);
                    labelSubrubriekView.setTextColor(ContextCompat.getColor(this,
                            R.color.grey));
                    labelOpvolgingsitemView.setTypeface(null, Typeface.BOLD);
                    labelOpvolgingsitemView.setTextColor(ContextCompat.getColor(this,
                            R.color.black));
                    // Opvullen recycler list met opvolgingsitems vr rubriek in kwestie
                    itemList.addAll(viewModel.getOpvolgingsItemItemListByRubriekID(rubriekToUpdate.getEntityId()));
                    listEntityType = SpecificData.ENTITY_TYPE_OPVOLGINGSITEM;
                    adapter.setEntityType(SpecificData.ENTITY_TYPE_OPVOLGINGSITEM);
                    adapter.setCallingActivity(SpecificData.ENTITY_TYPE_RUBRIEK);
                    adapter.setItemList(itemList);
                }

                labelOpvolgingsitemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // format vn logboek
                        labelLogboekView.setTypeface(null, Typeface.NORMAL);
                        labelLogboekView.setTextColor(ContextCompat.getColor(v.getContext(),
                                R.color.grey));
                        // format vn subrubriek
                        labelSubrubriekView.setTypeface(null, Typeface.NORMAL);
                        labelSubrubriekView.setTextColor(ContextCompat.getColor(v.getContext(),
                                R.color.grey));
                        // format vn opvolgingsitem
                        labelOpvolgingsitemView.setTypeface(null, Typeface.BOLD);
                        labelOpvolgingsitemView.setTextColor(ContextCompat.getColor(v.getContext(),
                                R.color.black));
                        // Opvullen recycler list met opvolgingsitems vr rubriek in kwestie
                        itemList.clear();
                        itemList.addAll(viewModel.getOpvolgingsItemItemListByRubriekID(rubriekToUpdate.getEntityId()));
                        listEntityType = SpecificData.ENTITY_TYPE_OPVOLGINGSITEM;
                        adapter.setEntityType(SpecificData.ENTITY_TYPE_OPVOLGINGSITEM);
                        adapter.setCallingActivity(SpecificData.ENTITY_TYPE_RUBRIEK);
                        adapter.setItemList(itemList);
                    }
                });

                labelLogboekView = findViewById(R.id.labelNameLogList);
                labelLogboekView.setTypeface(null, Typeface.NORMAL);
                labelLogboekView.setTextColor(ContextCompat.getColor(this,
                        R.color.grey));
                labelLogboekView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // format vn opvolgingsitem
                        labelOpvolgingsitemView.setTypeface(null, Typeface.NORMAL);
                        labelOpvolgingsitemView.setTextColor(ContextCompat.getColor(v.getContext(),
                                R.color.grey));
                        // format vn subrubriek
                        labelSubrubriekView.setTypeface(null, Typeface.NORMAL);
                        labelSubrubriekView.setTextColor(ContextCompat.getColor(v.getContext(),
                                R.color.grey));
                        // format vn logboek
                        labelLogboekView.setTypeface(null, Typeface.BOLD);
                        labelLogboekView.setTextColor(ContextCompat.getColor(v.getContext(),
                                R.color.black));
                        // Opvullen recycler list met logs vr rubriek in kwestie
                        itemList.clear();
                        itemList.addAll(viewModel.getLogItemListByRubriekID(rubriekToUpdate.getEntityId()));
                        listEntityType = SpecificData.ENTITY_TYPE_LOG;
                        adapter.setEntityType(SpecificData.ENTITY_TYPE_LOG);
                        adapter.setCallingActivity(SpecificData.ENTITY_TYPE_RUBRIEK);
                        adapter.setItemList(itemList);
                    }
                });
                // FloatingActionButton
                FloatingActionButton fab = findViewById(R.id.fab_edit_rubriek);
                fab.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent();
                        // ofwel wil je een subrubriek, een opvolgingsitem toevoegen ofwel een log
                        if (listEntityType.equals(SpecificData.ENTITY_TYPE_OPVOLGINGSITEM)) {
                            intent = new Intent(EditRubriek.this, EditOpvolgingsitem.class);
                        } else if (listEntityType.equals(SpecificData.ENTITY_TYPE_LOG)) {
                            // intent voor editlogitem
                            intent = new Intent(EditRubriek.this, EditLog.class);
                        } else if (listEntityType.equals(SpecificData.ENTITY_TYPE_RUBRIEK)) {
                            // Intent voor editsubrubriek
                            intent = new Intent(EditRubriek.this, EditRubriek.class);
                        }
                        intent.putExtra(StaticData.EXTRA_INTENT_KEY_ACTION, StaticData.ACTION_NEW);
                        intent.putExtra(SpecificData.ID_RUBRIEK, rubriekToUpdate.getEntityId().getId());
                        intent.putExtra(StaticData.EXTRA_INTENT_KEY_RETURN, SpecificData.ENTITY_TYPE_RUBRIEK);
                        startActivity(intent);
                    }
                });
                break;
        }
        Button saveButton = findViewById(R.id.buttonSaveRubriek);
        // Als button ingedrukt wordt...
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Definitie inputvelden
                EditText nameView = findViewById(R.id.editNameRubriek);
                // Gegevens overnemen vh scherm
                if (action.equals(StaticData.ACTION_UPDATE)) { // Update
                    viewModel.getRubriekById(new IDNumber(iDToUpdate)).setEntityName(String.valueOf(nameView.getText()));
                } else { // New
                    Rubriek newRubriek = new Rubriek(viewModel.getBasedir(), false);
                    newRubriek.setEntityName(String.valueOf(nameView.getText()));
                    if (parentRubriekId.getId() != StaticData.IDNUMBER_NOT_FOUND.getId()){
                        newRubriek.setParentId(parentRubriekId);
                    }
                    viewModel.getRubriekList().add(newRubriek);
                }
                viewModel.storeRubrieken();
                Intent replyIntent = new Intent(EditRubriek.this, ManageRubriek.class);
                startActivity(replyIntent);
            }
        });
    }
}