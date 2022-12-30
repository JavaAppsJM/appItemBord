package be.hvwebsites.itembord.entities;

import be.hvwebsites.libraryandroid4.helpers.IDNumber;

public class SuperItem {
    private IDNumber entityId;
    private String entityName;

    public SuperItem() {
    }

    public SuperItem(IDNumber entityId) {
        this.entityId = entityId;
    }

    public void setEntityName(String entityName) {
        this.entityName = entityName;
    }

    public IDNumber getEntityId() {
        return entityId;
    }

    public void setEntityId(IDNumber entityId) {
        this.entityId = entityId;
    }

    public void convertFromFileLine(String fileLine){
    }

    public String convertToFileLine(){
        return "";
    }

    public String getEntityName(){
        return "";
    }

}
