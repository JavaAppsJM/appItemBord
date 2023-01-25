package be.hvwebsites.itembord.entities;

import be.hvwebsites.itembord.constants.SpecificData;
import be.hvwebsites.libraryandroid4.helpers.DateString;
import be.hvwebsites.libraryandroid4.helpers.IDNumber;
import be.hvwebsites.libraryandroid4.statics.StaticData;

public class Log extends SuperItem{
    private DateString logDate;
    private String logDescription;
    public static final String ENTITY_LATEST_ID = "loglatestid";
    private IDNumber rubriekId;
    private IDNumber itemId;

    public Log() {
        super();
    }

    public Log(String basedir, boolean b) {
        super(new IDNumber(basedir, ENTITY_LATEST_ID));
        logDate = new DateString("ahjgk/jg");
        logDate.setDateToday();
        this.rubriekId = StaticData.IDNUMBER_NOT_FOUND;
        this.itemId = StaticData.IDNUMBER_NOT_FOUND;
    }

    public Log(String fileLine){
        convertFromFileLine(fileLine);
    }

    public void setLog(Log inLog){
        setEntityId(inLog.getEntityId());
        setEntityName(inLog.getEntityName());
        setLogDate(inLog.getLogDate());
        setLogDescription(inLog.getLogDescription());
        setRubriekId(inLog.getRubriekId());
        setItemId(inLog.getItemId());
    }

    public String getDisplayLog(){
        return logDate.getFormatDate() + " " + logDescription.substring(0, 50);
    }

    public DateString getLogDate() {
        return logDate;
    }

    public void setLogDate(DateString logDate) {
        this.logDate = logDate;
    }

    public String getLogDescription() {
        return logDescription;
    }

    public String getEntityName() {
        return logDescription;
    }

    public void setLogDescription(String logDescription) {
        this.logDescription = SpecificData.setFirstLetterCapital(logDescription);
    }

    public IDNumber getRubriekId() {
        return rubriekId;
    }

    public void setRubriekId(IDNumber rubriekId) {
        this.rubriekId = rubriekId;
    }

    public IDNumber getItemId() {
        return itemId;
    }

    public void setItemId(IDNumber itemId) {
        this.itemId = itemId;
    }

    public void convertFromFileLine(String fileLine) {
        // Maakt een log obv een fileline - format: <key><102><logdate><01012021><logdesc><tralala><rubriek><2><item><102>
        // fileLine splitsen in argumenten
        String[] fileLineContent = fileLine.split("<");
        for (int i = 0; i < fileLineContent.length; i++) {
            if (fileLineContent[i].matches("key.*")){
                setEntityId(new IDNumber(fileLineContent[i+1].replace(">", "")));
            }
            if (fileLineContent[i].matches("logdate.*")){
                setLogDate(new DateString(fileLineContent[i+1].replace(">","")));
            }
            if (fileLineContent[i].matches("logdesc.*")){
                setLogDescription(fileLineContent[i+1].replace(">",""));
            }
            if (fileLineContent[i].matches("rubriek.*")){
                setRubriekId(new IDNumber(fileLineContent[i+1].replace(">","")));
            }
            if (fileLineContent[i].matches("item.*")){
                setItemId(new IDNumber(fileLineContent[i+1].replace(">","")));
            }
        }
    }

    public String convertToFileLine() {
        String fileLine = "<key><" + getEntityId().getIdString()
                + "><logdate><" + getLogDate().getDateString()
                + "><logdesc><" + getLogDescription()
                + "><rubriek><" + getRubriekId().getIdString()
                + "><item><" + getItemId().getIdString() + ">";
        return fileLine;
    }

}
