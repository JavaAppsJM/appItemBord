package be.hvwebsites.itembord.entities;

import be.hvwebsites.itembord.constants.SpecificData;
import be.hvwebsites.libraryandroid4.helpers.IDNumber;
import be.hvwebsites.libraryandroid4.statics.StaticData;

public class Rubriek {
    private IDNumber entityId;
    private String entityName;
    private IDNumber parentId;
    public static final String ENTITY_LATEST_ID = "rubrieklatestid";

    public Rubriek() {
    }

    public Rubriek(String basedir, boolean b) {
        entityId = new IDNumber(basedir, ENTITY_LATEST_ID);
        entityName = "";
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

    public IDNumber getEntityId() {
        return entityId;
    }

    public void setEntityId(IDNumber entityId) {
        this.entityId = entityId;
    }

    public String getEntityName() {
        return entityName;
    }

    public void setEntityName(String entityName) {
        this.entityName = SpecificData.setFirstLetterCapital(entityName);
        boolean debug = true;
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
