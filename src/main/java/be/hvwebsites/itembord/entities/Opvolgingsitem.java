package be.hvwebsites.itembord.entities;

import be.hvwebsites.itembord.constants.FrequentieDateUnit;
import be.hvwebsites.itembord.constants.SpecificData;
import be.hvwebsites.libraryandroid4.helpers.DateString;
import be.hvwebsites.libraryandroid4.helpers.IDNumber;

public class Opvolgingsitem extends SuperItem{
    private String entityNamePast;
    private int frequentieNbr;
    private FrequentieDateUnit frequentieUnit;
    private DateString latestDate;
    private long eventId;
    public static final String ENTITY_LATEST_ID = "opvolgingsitemlatestid";
    private IDNumber rubriekId;

    public Opvolgingsitem() {
        super();
    }

    public Opvolgingsitem(String basedir, boolean b) {
        super(new IDNumber(basedir, ENTITY_LATEST_ID));
        setEntityName("");
        entityNamePast = "";
        latestDate = new DateString(DateString.EMPTY_DATESTRING);
    }

    public Opvolgingsitem(String fileLine){
        convertFromFileLine(fileLine);
    }

    public DateString calculateNextDate(){
        DateString newDate = new DateString(latestDate.getDateString());

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

    public void setOpvolgingsitem(Opvolgingsitem inItem){
        setEntityId(inItem.getEntityId());
        setEntityName(inItem.getEntityName());
        setEntityNamePast(inItem.getEntityNamePast());
        setFrequentieNbr(inItem.getFrequentieNbr());
        setFrequentieUnit(inItem.getFrequentieUnit());
        setLatestDate(inItem.getLatestDate());
        setEventId(inItem.getEventId());
        setRubriekId(inItem.rubriekId);

        boolean tobedeleted = true;
    }

    public String getDisplayOpvolgingsitem(){
        return getEntityName() +
                " (" +
                frequentieNbr +
                frequentieUnit.getLetter() +
                "): " +
                latestDate.getFormatDate();
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
    }

    public void setEntityName(String entName) {
        if (!entName.equals("")){
            super.setEntityName(SpecificData.setFirstLetterCapital(entName));
        }else {
            super.setEntityName("");
        }
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
            if (fileLineContent[i].matches("event.*")){
                setEventId(Integer.parseInt(fileLineContent[i+1].replace(">","")));
            }
            if (fileLineContent[i].matches("rubriek.*")){
                setRubriekId(new IDNumber(fileLineContent[i+1].replace(">", "")));
            }
        }
    }

    public String convertToFileLine() {
        String fileLine = "<key><" + getEntityId().getIdString()
                + "><item><" + getEntityName()
                + "><past><" + getEntityNamePast()
                + "><freqa><" + getFrequentieNbr()
                + "><freqe><" + getFrequentieUnit().getLetter()
                + "><latest><" + getLatestDate().getDateString()
                + "><event><" + getEventId()
                + "><rubriek><" + getRubriekId().getIdString() + ">";
        return fileLine;
    }

}
