package be.hvwebsites.itembord.entities;

import be.hvwebsites.libraryandroid4.helpers.IDNumber;

public class SuperItem {
    private IDNumber entityId;

    public SuperItem() {
    }

    public SuperItem(IDNumber entityId) {
        this.entityId = entityId;
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

}
