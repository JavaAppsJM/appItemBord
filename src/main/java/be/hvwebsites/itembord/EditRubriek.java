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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
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
import be.hvwebsites.libraryandroid4.adapters.NothingSelectedSpinnerAdapter;
import be.hvwebsites.libraryandroid4.helpers.IDNumber;
import be.hvwebsites.libraryandroid4.helpers.ListItemHelper;
import be.hvwebsites.libraryandroid4.repositories.Cookie;
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

        // Creer een filebase service (bevat file base en file base directory) obv device en package name
        FileBaseService fileBaseService = new FileBaseService(deviceModel, getPackageName());

        // Get a viewmodel from the viewmodelproviders
        viewModel = new ViewModelProvider(this).get(EntitiesViewModel.class);
        // Basis directory definitie
        String baseDir = fileBaseService.getFileBaseDir();
        // Initialize viewmodel mt basis directory (data wordt opgehaald in viewmodel)
        List<ReturnInfo> viewModelRetInfo = viewModel.initializeViewModel(baseDir);
        for (int i = 0; i < viewModelRetInfo.size(); i++) {
            Toast.makeText(EditRubriek.this,
                    viewModelRetInfo.get(i).getReturnMessage(),
                    Toast.LENGTH_SHORT).show();
        }

        // Intent bekijken vr action
        Intent editIntent = getIntent();
        if (editIntent.hasExtra(StaticData.EXTRA_INTENT_KEY_ACTION)){
            action = editIntent.getStringExtra(StaticData.EXTRA_INTENT_KEY_ACTION);
        }

        // Hoofdrubriek Spinner
        Spinner hoofdRubriekSpinner = (Spinner) findViewById(R.id.spinnerParentRubriek);
        // rubriekItemAdapter obv ListItemHelper
        ArrayAdapter<ListItemHelper> rubriekItemAdapter = new ArrayAdapter(this,
                android.R.layout.simple_spinner_item);
        rubriekItemAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Adapter vullen met rubrieken mr niet rubriek inkwestie
        rubriekItemAdapter.addAll(viewModel.getItemsFromList(viewModel.getShopList()));
        rubriekItemAdapter.add(new ListItemHelper(SpecificData.NO_FILTER,
                "",
                StaticData.IDNUMBER_NOT_FOUND));
        // vr de nieuwe shopfilteradapter
        hoofdRubriekSpinner.setAdapter(new NothingSelectedSpinnerAdapter(
                rubriekItemAdapter, R.layout.contact_spinner_row_nothing_selected, this
        ));

        hoofdRubriekSpinner.setAdapter(rubriekItemAdapter);
        // animate parameter moet false staan om het onnodig afvuren vd spinner tegen te gaan
        if (shopFilterString.equals(SpecificData.NO_FILTER)){
            //int positionAA = shopFilterAdapter.getCount()-1;
            int positionAA = shopItemAdapter.getCount()-1;
            shopFilterSpinner.setSelection(positionAA, false);
        }else {
            shopFilterSpinner.setSelection(viewModel.getIndexById(viewModel.getShopList(),
                    shopFilter.getEntityId()), false);
        }
    }
    // selection listener activeren, moet gebueren nadat de adapter gekoppeld is aan de spinner !!
        shopFilterSpinner.setOnItemSelectedListener(this);


        // Als parentId in intent zit ...
        parentRubriekView = findViewById(R.id.nameHoofrubriek);
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

    // Als er een shop geselecteerd is in de spinner
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (parent.getItemAtPosition(position) != null){
            Toast.makeText(A4ShoppingListActivity.this,
                    "Hersamenstellen artikels ...",
                    Toast.LENGTH_LONG).show();
            // Bepalen wat geselecteerd is
            ListItemHelper selecShop = (ListItemHelper) parent.getItemAtPosition(position);
            if (selecShop.getItemID().getId() == StaticData.IDNUMBER_NOT_FOUND.getId()){
                // Alle artikels
                shopFilterString = SpecificData.NO_FILTER;
                shopFilter = null;
            }else {
                // Bepaal Shop om te filteren ==> nieuwe shopfilter
                shopFilter = viewModel.getShopByID(selecShop.getItemID());
                shopFilterString = shopFilter.getEntityName();
            }
            //shopFilterString = String.valueOf(parent.getItemAtPosition(position));
            // TODO: Shopfilter bewaren als Cookie ; de shopfilterId moet bewaard worden
            cookieRepository.addCookie(new Cookie(SpecificData.SHOP_FILTER, shopFilterString));
/*
            // bepaal shop voor herbepalen checkboxlist
            if (!shopFilterString.equals(SpecificData.NO_FILTER)){
                shopFilter = viewModel.getShopByShopName(shopFilterString);
            }else {
                shopFilter = null;
            }
*/
            // spinner refreshen hoeft niet
//            parent.setAdapter(shopFilterAdapter);
//            parent.setSelection(viewModel.getShopIndexById(shopFilter.getEntityId()));
            // Refresh recyclerview met aangepaste checkboxlist
            composeCheckboxList();
            cbListAdapter.setCheckboxList(checkboxList);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }


}