package be.hvwebsites.itembord.entities;

import be.hvwebsites.itembord.constants.SpecificData;
import be.hvwebsites.libraryandroid4.helpers.IDNumber;
import be.hvwebsites.libraryandroid4.statics.StaticData;

public class Rubriek extends SuperItem{
    private IDNumber parentId;
    public static final String ENTITY_LATEST_ID = "rubrieklatestid";

    public Rubriek() {
        super();
    }

    public Rubriek(String basedir, boolean b) {
        super(new IDNumber(basedir, ENTITY_LATEST_ID));
        setEntityName("");
        parentId = StaticData.IDNUMBER_NOT_FOUND;
    }

    public Rubriek(IDNumber idNumber){
        super(idNumber);
        setEntityName("");
        parentId = StaticData.IDNUMBER_NOT_FOUND;
    }

    public Rubriek(String fileLine){
        convertFromFileLine(fileLine);
    }

    public IDNumber getParentId() {
        return parentId;
    }

    public void setParentId(IDNumber parentId) {
        this.parentId = parentId;
    }

    public void setRubriek(Rubriek inRub){
        setEntityId(inRub.getEntityId());
        setEntityName(inRub.getEntityName());
        setParentId(inRub.getParentId());
    }

    public void convertFromFileLine(String fileLine) {
        // Maakt een rubriek obv een fileline - format: <key><102><rubriek><auto>
        // fileLine splitsen in argumenten
        String[] fileLineContent = fileLine.split("<");
        for (int i = 0; i < fileLineContent.length; i++) {
            if (fileLineContent[i].matches("key.*")){
                setEntityId(new IDNumber(fileLineContent[i+1].replace(">", "")));
            }
            if (fileLineContent[i].matches("rubriek.*")){
                setEntityName(fileLineContent[i+1].replace(">",""));
            }
            if (fileLineContent[i].matches("parent.*")){
                setParentId(new IDNumber(fileLineContent[i+1].replace(">", "")));
            }
        }
    }

    public String convertToFileLine() {
        String fileLine = "<key><" + getEntityId().getIdString()
                + "><rubriek><" + getEntityName()
                + "><parent><" + getParentId().getIdString() + ">";
        return fileLine;
    }

}
