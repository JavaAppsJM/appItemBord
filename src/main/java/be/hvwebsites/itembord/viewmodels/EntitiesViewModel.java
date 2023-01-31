package be.hvwebsites.itembord.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import be.hvwebsites.itembord.constants.SpecificData;
import be.hvwebsites.itembord.entities.Log;
import be.hvwebsites.itembord.entities.Opvolgingsitem;
import be.hvwebsites.itembord.entities.Rubriek;
import be.hvwebsites.itembord.entities.SuperItem;
import be.hvwebsites.libraryandroid4.helpers.IDNumber;
import be.hvwebsites.libraryandroid4.helpers.ListItemHelper;
import be.hvwebsites.libraryandroid4.repositories.Cookie;
import be.hvwebsites.libraryandroid4.repositories.CookieRepository;
import be.hvwebsites.libraryandroid4.repositories.FlexiRepository;
import be.hvwebsites.libraryandroid4.returninfo.ReturnInfo;
import be.hvwebsites.libraryandroid4.statics.StaticData;

public class EntitiesViewModel extends AndroidViewModel {
    private FlexiRepository repository;
    private String basedir;
    // Lijsten om data in te zetten
    private List<Rubriek> rubriekList = new ArrayList<>();
    private List<Opvolgingsitem> itemList = new ArrayList<>();
    private List<Log> logList = new ArrayList<>();
    // File declaraties
    File rubriekFile;
    File itemFile;
    File logFile;
    // File names constants
    public static final String RUBRIEK_FILE = "rubriek.txt";
    public static final String ITEM_FILE = "item.txt";
    public static final String LOG_FILE = "log.txt";


    public EntitiesViewModel(@NonNull Application application) {
        super(application);
    }

    public List<ReturnInfo> initializeViewModel(String basedir){
        List<ReturnInfo> returninfo = new ArrayList<>();
        this.basedir = basedir;
        // Filedefinities
        rubriekFile = new File(basedir, RUBRIEK_FILE);
        itemFile = new File(basedir, ITEM_FILE);
        logFile = new File(basedir, LOG_FILE);
        // Ophalen rubrieken
        repository = new FlexiRepository(rubriekFile);
        rubriekList.addAll(getRubriekenFromDataList(repository.getDataList()));
        if (rubriekList.size() == 0){
            returninfo.add(new ReturnInfo(100, SpecificData.NO_RUBRIEKEN_YET));
        } else {
            // Ophalen opvolgingsitems
            repository = new FlexiRepository(itemFile);
            itemList.addAll(getItemsFromDataList(repository.getDataList()));
            if (itemList.size() == 0){
                returninfo.add(new ReturnInfo(100, SpecificData.NO_OPVOLGINGSITEMS_YET));
            }

            // Ophalen logs
            repository = new FlexiRepository(logFile);
            logList.addAll(getLogsFromDataList(repository.getDataList()));
            if (logList.size() == 0){
                returninfo.add(new ReturnInfo(100, SpecificData.NO_LOGS_YET));
            }
        }
        return returninfo;
    }

    /** Flexi methodes */

    public int getIndexItemHelperById(List<ListItemHelper> inList, IDNumber inID){
        // Bepaalt de index vh element met een opgegeven IDNumber
        for (int i = 0; i < inList.size(); i++) {
            if (inList.get(i).getItemID().getId() == inID.getId()){
                return i;
            }
        }
        return StaticData.ITEM_NOT_FOUND;
    }

    private List<String> convertEntityListinDataList(List<? extends SuperItem> inList){
        // Converteert een SuperItemlist in een datalist voor bewaard te worden in een bestand
        List<String> lineList = new ArrayList<>();
        for (int i = 0; i < inList.size(); i++) {
            lineList.add(inList.get(i).convertToFileLine());
        }
        return lineList;
    }

    private int getIndexById(List<? extends SuperItem> inList, IDNumber inID){
        // Bepaalt de index vh element met een opgegeven IDNumber
        for (int i = 0; i < inList.size(); i++) {
            if (inList.get(i).getEntityId().getId() == inID.getId()){
                return i;
            }
        }
        return StaticData.ITEM_NOT_FOUND;
    }

    private List<ListItemHelper> getItemsFromList(List<? extends SuperItem> inList, SuperItem neglectItem){
        // bepaalt een lijst met ListItemHelpers obv inlist mr sluit een neglectitem uit
        List<ListItemHelper> nameList = new ArrayList<>();
        for (int i = 0; i < inList.size(); i++) {
            if ((neglectItem == null) || ((neglectItem != null) &&
                    (inList.get(i).getEntityId().getId() != neglectItem.getEntityId().getId()))){
                nameList.add(new ListItemHelper(inList.get(i).getEntityName(),
                        "",
                        inList.get(i).getEntityId()));
            }
        }
        return nameList;
    }

    public String getNameByIdFromList(List<? extends SuperItem> inList, int inId){
        // bepaalt de entitynaam obv inlist en inId
        String name = "";
        for (int i = 0; i < inList.size(); i++) {
            if (inList.get(i).getEntityId().getId() == inId){
                return inList.get(i).getEntityName();
            }
        }
        return null;
    }

    /** Rubriek Methodes */

    private List<Rubriek> getRubriekenFromDataList(List<String> dataList){
        // Converteert een datalist met rubrieken in een rubrieklist
        List<Rubriek> rubriekList = new ArrayList<>();
        for (int j = 0; j < dataList.size(); j++) {
            rubriekList.add(new Rubriek(dataList.get(j)));
        }
        return rubriekList;
    }

    public List<ListItemHelper> getRubriekItemList(){
        // bepaalt een lijst met rubriek namen en ID's obv rubrieklist
        return getItemsFromList(rubriekList, null);
    }

    public List<ListItemHelper> getRubriekItemsInStyle(){
        // bepaalt een lijst met ListItemHelpers obv rubrieklist geeft de hoofdrubrieken style BOLD
        List<ListItemHelper> nameList = new ArrayList<>();
        for (int i = 0; i < rubriekList.size(); i++) {
            if (rubriekList.get(i).getParentId().getId() == StaticData.ITEM_NOT_FOUND){
                nameList.add(new ListItemHelper(rubriekList.get(i).getEntityName(),
                        SpecificData.STYLE_BOLD,
                        rubriekList.get(i).getEntityId()));
            }else {
                nameList.add(new ListItemHelper(rubriekList.get(i).getEntityName(),
                        "",
                        rubriekList.get(i).getEntityId()));
            }
        }
        return nameList;
    }

    public List<ListItemHelper> getRubriekListAndNeglectItem(Rubriek inRubriek){
        return getItemsFromList(rubriekList, inRubriek);
    }

    public List<ListItemHelper> getRubriekListByHoofdrubriekID(IDNumber hoofdrubriekID){
        // bepaalt een lijst met rubriek namen en ID's obv rubrieklist en parentID
        List<ListItemHelper> displayList = new ArrayList<>();
        for (int i = 0; i < rubriekList.size(); i++) {
            if (rubriekList.get(i).getParentId().getId() == hoofdrubriekID.getId()){
                displayList.add(new ListItemHelper(
                        rubriekList.get(i).getEntityName(), "",
                        rubriekList.get(i).getEntityId()
                ));
            }
        }
        return displayList;
    }

    public int getRubriekIndexById(IDNumber rubriekId){
        // Bepaalt de index vd rubriek in rubrieklist obv zijn ID
        return getIndexById(rubriekList, rubriekId);
    }

    public Rubriek getRubriekById(IDNumber rubriekId){
        // Bepaalt de rubriek obv zijn ID
        return rubriekList.get(getRubriekIndexById(rubriekId));
    }

    /** Opvolgingsitems Metodes */

    private List<Opvolgingsitem> getItemsFromDataList(List<String> dataList){
        // Converteert een datalist met opvolgingsitems in een opvolgingsitemlist
        List<Opvolgingsitem> opvolgingsitems = new ArrayList<>();
        for (int j = 0; j < dataList.size(); j++) {
            opvolgingsitems.add(new Opvolgingsitem(dataList.get(j)));
        }
        return opvolgingsitems;
    }

    public List<ListItemHelper> getOpvolgingsItemItemListByRubriekID(IDNumber rubriekID){
        // bepaalt een lijst met opvolgingsitems namen en ID's obv itemlist en rubriekID
        List<ListItemHelper> displayList = new ArrayList<>();
        for (int i = 0; i < itemList.size(); i++) {
            if (itemList.get(i).getRubriekId().getId() == rubriekID.getId()){
                displayList.add(new ListItemHelper(
                        itemList.get(i).getDisplayOpvolgingsitem(), "",
                        itemList.get(i).getEntityId()
                ));
            }
        }
        return displayList;
    }

    public int getItemIndexById(IDNumber itemID){
        // Bepaalt de index vh opvolgingsitem in de list obv zijn ID
        return getIndexById(itemList, itemID);
    }

    public Opvolgingsitem getOpvolgingsitemById(IDNumber itemId){
        // Bepaalt het opvolgingsitem obv zijn ID
        int index = getItemIndexById(itemId);
        if (index == StaticData.ITEM_NOT_FOUND){
            return null;
        }else {
            return itemList.get(index);
        }
    }

    public int getFirstItemIndexByRubriek(IDNumber rubriekID){
        for (int j = 0; j < itemList.size(); j++) {
            if (itemList.get(j).getRubriekId().getId() == rubriekID.getId()){
                return j;
            }
        }
        return StaticData.ITEM_NOT_FOUND;
    }

    /** Logitems Methodes */

    private List<Log> getLogsFromDataList(List<String> dataList){
        // Converteert een datalist met logs in een loglist
        List<Log> logs = new ArrayList<>();
        for (int j = 0; j < dataList.size(); j++) {
            logs.add(new Log(dataList.get(j)));
        }
        return logs;
    }

    public List<ListItemHelper> getLogItemListByRubriekID(IDNumber rubriekID){
        // bepaalt een lijst met logs namen en ID's obv logList en rubriekID
        List<ListItemHelper> displayList = new ArrayList<>();
        for (int i = 0; i < logList.size(); i++) {
            if (logList.get(i).getRubriekId().getId() == rubriekID.getId()){
                displayList.add(new ListItemHelper(
                        logList.get(i).getDisplayLog(), "",
                        logList.get(i).getEntityId()
                ));
            }
        }
        return displayList;
    }

    public List<ListItemHelper> getLogItemListByOItemID(IDNumber oItemID){
        // bepaalt een lijst met logs namen en ID's obv logList en opvolgingsitemID
        List<ListItemHelper> displayList = new ArrayList<>();
        for (int i = 0; i < logList.size(); i++) {
            if (logList.get(i).getItemId().getId() == oItemID.getId()){
                displayList.add(new ListItemHelper(
                        logList.get(i).getDisplayLog(), "",
                        logList.get(i).getEntityId()
                ));
            }
        }
        return displayList;
    }

    public int getLogIndexById(IDNumber logID){
        // Bepaalt de index vd log in de loglist obv zijn ID
        return getIndexById(logList, logID);
    }

    public Log getLogById(IDNumber logId){
        // Bepaalt de log obv zijn ID
        return logList.get(getLogIndexById(logId));
    }

    public int getFirstLogIndexByRubriek(IDNumber rubriekID){
        for (int j = 0; j < logList.size(); j++) {
            if (logList.get(j).getRubriekId().getId() == rubriekID.getId()){
                return j;
            }
        }
        return StaticData.ITEM_NOT_FOUND;
    }

    public int getFirstLogIndexByItem(IDNumber itemID){
        for (int j = 0; j < logList.size(); j++) {
            if (logList.get(j).getItemId().getId() == itemID.getId()){
                return j;
            }
        }
        return StaticData.ITEM_NOT_FOUND;
    }

    /** Store Methods */

    public void storeRubrieken(){
        // Bewaart de rubrieklist
        // Eerst de rubrieklist alfabetisch sorteren
        sortRubriekList(rubriekList);
        repository.storeData(rubriekFile, convertEntityListinDataList(rubriekList));
    }

    public void storeItems(){
        // Bewaart de opvolgingsitems
        // Eerst alfabetisch sorteren
        if (itemList.size() > 1){
            sortItemList(itemList);
        }
        repository.storeData(itemFile, convertEntityListinDataList(itemList));
    }

    public void storeLogs(){
        // Bewaart de loglist
        // logs omgekeerd chronologisch sorteren
        if (logList.size() > 1){
            sortLogs();
        }
        repository.storeData(logFile, convertEntityListinDataList(logList));
    }

    /** Sorteer Methods */

    private void sortRubriekList(List<Rubriek> rubList){
        // Sorteert een rubrieklist op entityname alfabetisch
        Rubriek tempRubriek = new Rubriek();
        for (int i = rubList.size() ; i > 0; i--) {
            for (int j = 1; j < i ; j++) {
                if (rubList.get(j).getEntityName().compareToIgnoreCase(rubList.get(j-1).getEntityName()) < 0){
                    tempRubriek.setRubriek(rubList.get(j));
                    rubList.get(j).setRubriek(rubList.get(j-1));
                    rubList.get(j-1).setRubriek(tempRubriek);
                }
            }
        }
    }

    private void sortItemList(List<Opvolgingsitem> inItemList){
        // Sorteert een opvolgingsitemlist op nextDate chronologisch
        Opvolgingsitem tempItem = new Opvolgingsitem();
        for (int i = inItemList.size() ; i > 0; i--) {
            for (int j = 1; j < i ; j++) {
                if (inItemList.get(j).getNextDate() < inItemList.get(j-1).getNextDate()){
                    tempItem.setOpvolgingsitem(inItemList.get(j));
                    inItemList.get(j).setOpvolgingsitem(inItemList.get(j-1));
                    inItemList.get(j-1).setOpvolgingsitem(tempItem);
                }
            }
        }
    }

    private void sortLogs(){
        // Logs worden omgekeerd chronologisch gesorteerd
        Log tempLog = new Log();
        int currentDate;
        int previousDate;

        for (int i = logList.size() ; i > 0; i--) {
            for (int j = 1; j < i ; j++) {
                currentDate = logList.get(j).getLogDate().getIntDate();
                previousDate = logList.get(j-1).getLogDate().getIntDate();
                if (currentDate > previousDate){
                    tempLog.setLog(logList.get(j));
                    logList.get(j).setLog(logList.get(j-1));
                    logList.get(j-1).setLog(tempLog);
                }
            }
        }
    }

    /** Delete Methods */

    public void deleteRubriekByID(IDNumber inIDNumber){
        int indexToBeDeleted = getRubriekIndexById(inIDNumber);
        if (indexToBeDeleted != StaticData.ITEM_NOT_FOUND){
            rubriekList.remove(indexToBeDeleted);
            // Alle rubriek relevante items moeten voor die rubriek ook verwijderd worden
            // opvolgingsitems
            int itemIndexToBeDeleted = getFirstItemIndexByRubriek(inIDNumber);
            if (itemIndexToBeDeleted != StaticData.ITEM_NOT_FOUND){
                do {
                    itemList.remove(itemIndexToBeDeleted);
                    itemIndexToBeDeleted = getFirstItemIndexByRubriek(inIDNumber);
                }while (itemIndexToBeDeleted != StaticData.ITEM_NOT_FOUND);
            }
            // logs
            int logIndexToBeDeleted = getFirstLogIndexByRubriek(inIDNumber);
            if (logIndexToBeDeleted != StaticData.ITEM_NOT_FOUND){
                do {
                    logList.remove(logIndexToBeDeleted);
                    logIndexToBeDeleted = getFirstLogIndexByRubriek(inIDNumber);
                }while (logIndexToBeDeleted != StaticData.ITEM_NOT_FOUND);
            }

            // Bewaar nieuwe toestand rubrieken
            storeRubrieken();
            storeItems();
            storeLogs();
        }
    }

    public void deleteOpvolgingsitemByID(IDNumber inIDNumber){
        int indexToBeDeleted = getItemIndexById(inIDNumber);
        if (indexToBeDeleted != StaticData.ITEM_NOT_FOUND){
            itemList.remove(indexToBeDeleted);
            // Alle opvolgingsitem relevante items moeten voor dat opvolgingsitem ook verwijderd worden
            // logs
            int logIndexToBeDeleted = getFirstLogIndexByItem(inIDNumber);
            if (logIndexToBeDeleted != StaticData.ITEM_NOT_FOUND){
                do {
                    logList.remove(logIndexToBeDeleted);
                    logIndexToBeDeleted = getFirstLogIndexByItem(inIDNumber);
                }while (logIndexToBeDeleted != StaticData.ITEM_NOT_FOUND);
            }

            // Bewaar nieuwe toestand
            storeItems();
            storeLogs();
        }
    }

    public void deleteLogByID(IDNumber inIDNumber){
        int indexToBeDeleted = getLogIndexById(inIDNumber);
        if (indexToBeDeleted != StaticData.ITEM_NOT_FOUND){
            logList.remove(indexToBeDeleted);

            // Bewaar nieuwe toestand
            storeLogs();
        }
    }

    /** Getters & Setters */
    public String getBasedir() {
        return basedir;
    }

    public void setSpinnerSelection(String spinnerSelection) {
        // Spinner selecties om te onthouden
    }

    public List<Rubriek> getRubriekList() {
        return rubriekList;
    }

    public List<Opvolgingsitem> getOpvolgingsitemList() {
        return itemList;
    }

    public List<Log> getLogList() {
        return logList;
    }

    /** To be deleted */

    /*
    private List<String> convertItemListinDataList(List<Opvolgingsitem> inList){
        // Converteert een itemlist in een datalist voor bewaard te worden in een bestand
        List<String> lineList = new ArrayList<>();
        for (int i = 0; i < inList.size(); i++) {
            lineList.add(inList.get(i).convertToFileLine());
        }
        return lineList;
    }
*/

/*
    private List<String> convertLogListinDataList(List<Log> inList){
        // Converteert een loglist in een datalist voor bewaard te worden in een bestand
        List<String> lineList = new ArrayList<>();
        for (int i = 0; i < inList.size(); i++) {
            lineList.add(inList.get(i).convertToFileLine());
        }
        return lineList;
    }
*/

}
