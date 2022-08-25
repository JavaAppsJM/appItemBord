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
import be.hvwebsites.libraryandroid4.helpers.IDNumber;
import be.hvwebsites.libraryandroid4.helpers.ListItemHelper;
import be.hvwebsites.libraryandroid4.repositories.FlexiRepository;
import be.hvwebsites.libraryandroid4.returninfo.ReturnInfo;
import be.hvwebsites.libraryandroid4.statics.StaticData;

public class EntitiesViewModel extends AndroidViewModel {
    private FlexiRepository repository;
    private String basedir;
    // File declaraties
    File rubriekFile;
    File itemFile;
    File logFile;
    // File names constants
    public static final String RUBRIEK_FILE = "rubriek.txt";
    public static final String ITEM_FILE = "item.txt";
    public static final String LOG_FILE = "log.txt";
    // Lijsten om data in te zetten
    private List<Rubriek> rubriekList = new ArrayList<>();
    private List<Opvolgingsitem> itemList = new ArrayList<>();
    private List<Log> logList = new ArrayList<>();
    // Spinner selecties om te onthouden
    private String spinnerSelection = "";


    public EntitiesViewModel(@NonNull Application application) {
        super(application);
    }

    public ReturnInfo initializeViewModel(String basedir){
        ReturnInfo returnInfo = new ReturnInfo(0);
        this.basedir = basedir;
        // Filedefinities
        rubriekFile = new File(basedir, RUBRIEK_FILE);
        itemFile = new File(basedir, ITEM_FILE);
        logFile = new File(basedir, LOG_FILE);
        // Ophalen rubrieken
        repository = new FlexiRepository(rubriekFile);
        rubriekList.addAll(getRubriekenFromDataList(repository.getDataList()));
        if (rubriekList.size() == 0){
            returnInfo.setReturnCode(100);
            returnInfo.setReturnMessage(SpecificData.NO_RUBRIEKEN_YET);
        } else {
            // Ophalen opvolgingsitems
            repository = new FlexiRepository(itemFile);
            itemList.addAll(getItemsFromDataList(repository.getDataList()));
            if (itemList.size() == 0){
                returnInfo.setReturnCode(100);
                returnInfo.setReturnMessage(SpecificData.NO_OPVOLGINGSITEMS_YET);
            }
            // Ophalen logs
            repository = new FlexiRepository(logFile);
            logList.addAll(getLogsFromDataList(repository.getDataList()));
            if (logList.size() == 0) {
                returnInfo.setReturnCode(100);
                returnInfo.setReturnMessage(SpecificData.NO_LOGS_YET);
            }
        }
        return returnInfo;
    }

    public List<ListItemHelper> getRubriekItemList(){
        // bepaalt een lijst met rubriek namen en ID's obv rubrieklist
        List<ListItemHelper> displayList = new ArrayList<>();
        for (int i = 0; i < rubriekList.size(); i++) {
            displayList.add(new ListItemHelper(
                    rubriekList.get(i).getEntityName(), "",
                    rubriekList.get(i).getEntityId()
            ));
        }
        return displayList;
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

    /*
        public List<String> getLocationNameList(){
            // bepaalt een lijst met location namen obv locationlist voor spinners !!!
            List<String> displayList = new ArrayList<>();
            for (int i = 0; i < locationList.size(); i++) {
                displayList.add(locationList.get(i).getEntityName());
            }
            return displayList;
        }

        public Location getLocationByName(String inName){
            // bepaalt een locatie obv entityname
            for (int i = 0; i < locationList.size(); i++) {
                if (locationList.get(i).getEntityName().equals(inName)){
                    return locationList.get(i);
                }
            }
            return null;
        }

        public int getLocationIndexByName(String inName){
            // bepaalt een locatie index obv entityname
            for (int i = 0; i < locationList.size(); i++) {
                if (locationList.get(i).getEntityName().equals(inName)){
                    return i;
                }
            }
            return StaticData.ITEM_NOT_FOUND;
        }
    */
    private List<Rubriek> getRubriekenFromDataList(List<String> dataList){
        // Converteert een datalist met rubrieken in een rubrieklist
        List<Rubriek> rubriekList = new ArrayList<>();
        for (int j = 0; j < dataList.size(); j++) {
            rubriekList.add(new Rubriek(dataList.get(j)));
        }
        return rubriekList;
    }

    public int getRubriekIndexById(IDNumber rubriekId){
        for (int j = 0; j < rubriekList.size(); j++) {
            if (rubriekList.get(j).getEntityId().getId() == rubriekId.getId()){
                return j;
            }
        }
        return StaticData.ITEM_NOT_FOUND;
    }

    public Rubriek getRubriekById(IDNumber rubriekId){
        return rubriekList.get(getRubriekIndexById(rubriekId));
    }
/*
    public List<String> getMeterNameList(Location inLoc){
        // bepaalt een lijst met meter namen obv meterlist en een location
        List<String> displayList = new ArrayList<>();
        for (int i = 0; i < meterList.size(); i++) {
            if (meterList.get(i).getMeterLocationId().getId() ==
                    inLoc.getEntityId().getId()){
                displayList.add(meterList.get(i).getEntityName());
            }
        }
        return displayList;
    }

    public List<ListItemHelper> getMeterItemList(Location inloc){
        // bepaalt een lijst met meter namen en ID's obv meterlist
        List<ListItemHelper> displayList = new ArrayList<>();
        for (int i = 0; i < meterList.size(); i++) {
            if (meterList.get(i).getMeterLocationId().getId() ==
                    inloc.getEntityId().getId()){
                displayList.add(new ListItemHelper(
                        meterList.get(i).getEntityName(), "",
                        meterList.get(i).getEntityId()
                ));
            }
        }
        return displayList;
    }
*/
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

    public Opvolgingsitem getOpvolgingsitemById(IDNumber itemId){
        return itemList.get(getItemIndexById(itemId));
    }

    public int getItemIndexById(IDNumber itemID){
        for (int j = 0; j < itemList.size(); j++) {
            if (itemList.get(j).getEntityId().getId() == itemID.getId()){
                return j;
            }
        }
        return StaticData.ITEM_NOT_FOUND;
    }

    public int getFirstItemIndexByRubriek(IDNumber rubriekID){
        for (int j = 0; j < itemList.size(); j++) {
            if (itemList.get(j).getRubriekId().getId() == rubriekID.getId()){
                return j;
            }
        }
        return StaticData.ITEM_NOT_FOUND;
    }
/*
    public Meter getMeterByNameForLocation(String inName, Location inLoc){
        if (inLoc != null){
            for (int j = 0; j < meterList.size(); j++) {
                if ((meterList.get(j).getEntityName().equals(inName)) &&
                        (meterList.get(j).getMeterLocationId().getId() == inLoc.getEntityId().getId())) {
                    return meterList.get(j);
                }
            }
        }
        return null;
    }

    public List<ListItemHelper> getMeasurementsforMeter(Location inLoc, Meter inMeter){
        // Eerst gaan we de metinglijst voor de opgegeven locatie, meter bepalen
        List<Measurement> mList = new ArrayList<>();
        if ((inLoc != null) && (inMeter != null)){
            for (int j = 0; j < measurementList.size(); j++) {
                if ((measurementList.get(j).getMeterLocationId().getId() == inLoc.getEntityId().getId() ) &&
                        (measurementList.get(j).getMeterId().getId() == inMeter.getEntityId().getId())) {
                    mList.add(measurementList.get(j));
                }
            }
        }
        // Display list bepalen voor geselecteerde measurements
        List<ListItemHelper> mDisplayList = new ArrayList<>();
        for (int i = 0; i < mList.size() ; i++) {
            if (i == mList.size()-1){
                // Voor de laatste meting kan je geen volgende meting doorgeven !!
                mDisplayList.add(new ListItemHelper(
                        mList.get(i).composeDisplayLine(mList.get(i)),
                        "", mList.get(i).getEntityId()
                ));
            }else {
                mDisplayList.add(new ListItemHelper(
                        mList.get(i).composeDisplayLine(mList.get(i+1)),
                        "", mList.get(i).getEntityId()
                ));
            }
        }
        return mDisplayList;
    }

    public int getMsrmntIndexById(IDNumber msrmntID){
        for (int j = 0; j < measurementList.size(); j++) {
            if (measurementList.get(j).getEntityId().getId() == msrmntID.getId()){
                return j;
            }
        }
        return StaticData.ITEM_NOT_FOUND;
    }
*/
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

    public Log getLogById(IDNumber logId){
        return logList.get(getLogIndexById(logId));
    }

    public int getLogIndexById(IDNumber logID){
        for (int j = 0; j < logList.size(); j++) {
            if (logList.get(j).getEntityId().getId() == logID.getId()){
                return j;
            }
        }
        return StaticData.ITEM_NOT_FOUND;
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

    public ReturnInfo storeRubrieken(){
        // Bewaart de rubrieklist
        // Eerst de rubrieklist alfabetisch sorteren
        sortRubriekList(rubriekList);
        ReturnInfo returnInfo = new ReturnInfo(0);
        repository.storeData(rubriekFile, convertRubriekListinDataList(rubriekList));
        return returnInfo;
    }

    private void sortRubriekList(List<Rubriek> rubList){
        // Sorteert een locationlist op entityname alfabetisch
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

    private List<String> convertRubriekListinDataList(List<Rubriek> inRubList){
        // Converteert een rubrieklist in een datalist voor bewaard te worden in een bestand
        List<String> lineList = new ArrayList<>();
        for (int i = 0; i < inRubList.size(); i++) {
            lineList.add(inRubList.get(i).convertToFileLine());
        }
        return lineList;
    }

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

    public ReturnInfo storeItems(){
        // Bewaart de opvolgingsitems
        // Eerst alfabetisch sorteren
        sortItemList(itemList);
        ReturnInfo returnInfo = new ReturnInfo(0);
        repository.storeData(itemFile, convertItemListinDataList(itemList));
        return returnInfo;
    }

    private void sortItemList(List<Opvolgingsitem> inItemList){
        // Sorteert een list op entityname alfabetisch
        Opvolgingsitem tempItem = new Opvolgingsitem();
        for (int i = inItemList.size() ; i > 0; i--) {
            for (int j = 1; j < i ; j++) {
                if (inItemList.get(j).getEntityName().compareToIgnoreCase(inItemList.get(j-1).getEntityName()) < 0){
                    tempItem.setOpvolgingsitem(inItemList.get(j));
                    inItemList.get(j).setOpvolgingsitem(inItemList.get(j-1));
                    inItemList.get(j-1).setOpvolgingsitem(tempItem);
                }
            }
        }
    }

    private List<String> convertItemListinDataList(List<Opvolgingsitem> inList){
        // Converteert een itemlist in een datalist voor bewaard te worden in een bestand
        List<String> lineList = new ArrayList<>();
        for (int i = 0; i < inList.size(); i++) {
            lineList.add(inList.get(i).convertToFileLine());
        }
        return lineList;
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

    public ReturnInfo storeLogs(){
        // Bewaart de loglist
        ReturnInfo returnInfo = new ReturnInfo(0);

        // logs omgekeerd chronologisch sorteren
        if (logList.size() > 1){
            sortLogs();
        }
        repository.storeData(logFile, convertLogListinDataList(logList));
        return returnInfo;
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

    private List<String> convertLogListinDataList(List<Log> inList){
        // Converteert een loglist in een datalist voor bewaard te worden in een bestand
        List<String> lineList = new ArrayList<>();
        for (int i = 0; i < inList.size(); i++) {
            lineList.add(inList.get(i).convertToFileLine());
        }
        return lineList;
    }

    public void deleteLogByID(IDNumber inIDNumber){
        int indexToBeDeleted = getLogIndexById(inIDNumber);
        if (indexToBeDeleted != StaticData.ITEM_NOT_FOUND){
            logList.remove(indexToBeDeleted);

            // Bewaar nieuwe toestand
            storeLogs();
        }
    }

    public String getBasedir() {
        return basedir;
    }

    public void setSpinnerSelection(String spinnerSelection) {
        this.spinnerSelection = spinnerSelection;
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
}
