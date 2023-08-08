package be.hvwebsites.itembord.entities;

import android.widget.EditText;

import java.util.Calendar;

import be.hvwebsites.itembord.constants.FrequentieDateUnit;
import be.hvwebsites.itembord.constants.SpecificData;
import be.hvwebsites.libraryandroid4.helpers.DateString;
import be.hvwebsites.libraryandroid4.helpers.IDNumber;

public class Opvolgingsitem extends SuperItem{
    private String entityNamePast;
    private int frequentieNbr;
    private FrequentieDateUnit frequentieUnit;
    private DateString latestDate;
    private DateString skipDate;
    private long nextDate;
    private long eventId;
    private IDNumber rubriekId;
    public static final String ENTITY_LATEST_ID = "opvolgingsitemlatestid";
    public static final long ONE_WEEK_IN_MILLISEC = 7 * 24 * 60 * 60 * 1000;

    public Opvolgingsitem() {
        super();
    }

    public Opvolgingsitem(String basedir, boolean b) {
        super(new IDNumber(basedir, ENTITY_LATEST_ID));
        setEntityName("");
        entityNamePast = "";
        latestDate = new DateString(DateString.EMPTY_DATESTRING);
        skipDate = new DateString(DateString.EMPTY_DATESTRING);
        nextDate = 0;
    }

    public Opvolgingsitem(String fileLine){
        this.skipDate = new DateString(DateString.EMPTY_DATESTRING);
        convertFromFileLine(fileLine);
        this.nextDate = calcNextDate();
    }

    private DateString calculateNextDate(DateString inDateString){
        DateString newDate = new DateString(inDateString.getDateString());

        // Bereken nieuwe datum als datestring
        switch (frequentieUnit){
            case YEARS:
                // Gemakkelijke manier
                // newDate = latestDate.addNbrOfDays(frequentieNbr * 365);
                // Complexe manier
                newDate.addNbrOfYears(frequentieNbr);
                break;
            case MONTHS:
                // Gemakkelijke manier
                // newDate = latestDate.addNbrOfDays(frequentieNbr * 30);
                // Complexe manier
                newDate.addNbrOfMonths(frequentieNbr);
                break;
            case WEEKS:
                newDate.addNbrOfWeeks(frequentieNbr);
                break;
            case DAYS:
                newDate.addNbrOfDays(frequentieNbr);
                break;
            default:
                break;
        }
        return newDate;
    }

    public void updateItemFromScreen(EditText inItemData){

    }

    public void setOpvolgingsitem(Opvolgingsitem inItem){
        setEntityId(inItem.getEntityId());
        setEntityName(inItem.getEntityName());
        setEntityNamePast(inItem.getEntityNamePast());
        setFrequentieUnit(inItem.getFrequentieUnit());
        setFrequentieNbr(inItem.getFrequentieNbr());
        setLatestDate(inItem.getLatestDate());
        setSkipDate(inItem.getSkipDate());
        setEventId(inItem.getEventId());
        setRubriekId(inItem.rubriekId);
    }

    public String getDisplayOpvolgingsitem(){
        return getEntityName() +
                " (" +
                frequentieNbr +
                frequentieUnit.getLetter() +
                "): " +
                latestDate.getFormatDate();
    }

    public String getDisplayOitemStatusBord(){
        return getEntityName()
                + " laatste uitvoering: "
                + latestDate.getFormatDate();
    }

    public String getDisplayStyle(){
        if (latestDateIsEmpty()){
            return SpecificData.STYLE_NORMAL;
        }else if(isOverDue(getNextDateDS())){
            return SpecificData.STYLE_RED;
        }else if (isDue(getNextDateDS())){
            return SpecificData.STYLE_ORANGE;
        }else {
            return SpecificData.STYLE_GREEN;
        }
    }

    private boolean latestDateIsEmpty() {
        // Bepaalt of de latestdate empty is
        return latestDate.getDateString().equals(DateString.EMPTY_DATESTRING);
    }

    private boolean isOverDue(DateString inDateString) {
        // Today
        Calendar calendarDateToday = Calendar.getInstance();
        long todayInMilliSec = calendarDateToday.getTimeInMillis();

        // next Date
        long nextDateInMilliSec = inDateString.getDateInMillis();

        // Overdue
        return todayInMilliSec > nextDateInMilliSec;
    }

    private boolean isDue(DateString inDateString) {
        // Today
        Calendar calendarDateToday = Calendar.getInstance();
        long todayInMilliSec = calendarDateToday.getTimeInMillis();

        // next Date
        long nextDateInMilliSec = inDateString.getDateInMillis();

        // Due
        return todayInMilliSec >= nextDateInMilliSec - ONE_WEEK_IN_MILLISEC;
    }

    public long getEventId() {
        return eventId;
    }

    public void setEventId(long eventId) {
        this.eventId = eventId;
    }

    public IDNumber getRubriekId() {
        return rubriekId;
    }

    public void setRubriekId(IDNumber rubriekId) {
        this.rubriekId = rubriekId;
    }

    public int getFrequentieNbr() {
        return frequentieNbr;
    }


    public void setFrequentieNbr(int frequentieNbr) {
        this.frequentieNbr = frequentieNbr;
        if (frequentieNbr == 0){
            // Item zonder frequentie !!
            setFrequentieUnit(FrequentieDateUnit.NONE);
        }
    }

    public FrequentieDateUnit getFrequentieUnit() {
        return frequentieUnit;
    }

    public void setFrequentieUnit(FrequentieDateUnit frequentieUnit) {
        this.frequentieUnit = frequentieUnit;
    }

    public void setFreqUnitFromLetter(String inLetter){
        switch (inLetter){
            case "d":
                this.frequentieUnit = FrequentieDateUnit.DAYS;
                break;
            case "w":
                this.frequentieUnit = FrequentieDateUnit.WEEKS;
                break;
            case "m":
                this.frequentieUnit = FrequentieDateUnit.MONTHS;
                break;
            case "y":
                this.frequentieUnit = FrequentieDateUnit.YEARS;
                break;
            case "n":
                this.frequentieUnit = FrequentieDateUnit.NONE;
                break;
        }
    }

    public String getEntityNamePast() {
        return entityNamePast;
    }

    public void setEntityNamePast(String entityNamePast) {
        this.entityNamePast = SpecificData.setFirstLetterCapital(entityNamePast);
    }

    public DateString getLatestDate() {
        return latestDate;
    }

    public void setLatestDate(DateString latestDate) {
        this.latestDate = latestDate;
        this.nextDate = calcNextDate();
    }

    public DateString getSkipDate() {
        return skipDate;
    }

    public void setSkipDate(DateString skipDate) {
        this.skipDate = skipDate;
        this.nextDate = calcNextDate();
    }

    private long calcNextDate(){
        if (this.skipDate != null){
            if ((!this.skipDate.getDateString().equals(DateString.EMPTY_DATESTRING))
                    && (this.skipDate.getDateInMillis() > this.latestDate.getDateInMillis())){
                // Skipdate is groter dan wordt die genomen voor de berekening vn nextdate
                return calculateNextDate(this.skipDate).getDateInMillis();
            }else{
                return calculateNextDate(this.latestDate).getDateInMillis();
            }
        }else {
            return calculateNextDate(this.latestDate).getDateInMillis();
        }
    }

    public long getNextDate() {
        return nextDate;
    }

    public DateString getNextDateDS(){
        return new DateString(getNextDate());
    }

    public void convertFromFileLine(String fileLine) {
        // Maakt een opvolgingsitem obv een fileline - format: <key><102><item><auto><freqa><6><freqe><m><latest><01012021><rubriek><2>
        // fileLine splitsen in argumenten
        String[] fileLineContent = fileLine.split("<");
        for (int i = 0; i < fileLineContent.length; i++) {
            if (fileLineContent[i].matches("key.*")){
                setEntityId(new IDNumber(fileLineContent[i+1].replace(">", "")));
            }
            if (fileLineContent[i].matches("item.*")){
                setEntityName(fileLineContent[i+1].replace(">",""));
            }
            if (fileLineContent[i].matches("past.*")){
                setEntityNamePast(fileLineContent[i+1].replace(">",""));
            }
            if (fileLineContent[i].matches("freqa.*")){
                setFrequentieNbr(Integer.parseInt(fileLineContent[i+1].replace(">","")));
            }
            if (fileLineContent[i].matches("freqe.*")){
                setFreqUnitFromLetter(fileLineContent[i+1].replace(">",""));
            }
            if (fileLineContent[i].matches("latest.*")){
                setLatestDate(new DateString(fileLineContent[i+1].replace(">","")));
            }
            if (fileLineContent[i].matches("skip.*")){
                setSkipDate(new DateString(fileLineContent[i+1].replace(">","")));
            }
            if (fileLineContent[i].matches("event.*")){
                setEventId(Integer.parseInt(fileLineContent[i+1].replace(">","")));
            }
            if (fileLineContent[i].matches("rubriek.*")){
                setRubriekId(new IDNumber(fileLineContent[i+1].replace(">", "")));
            }
        }
    }

    public String convertToFileLine() {
        return "<key><" + getEntityId().getIdString()
                + "><item><" + getEntityName()
                + "><past><" + getEntityNamePast()
                + "><freqa><" + getFrequentieNbr()
                + "><freqe><" + getFrequentieUnit().getLetter()
                + "><latest><" + getLatestDate().getDateString()
                + "><skip><" + getSkipDate().getDateString()
                + "><event><" + getEventId()
                + "><rubriek><" + getRubriekId().getIdString() + ">";
    }

}
