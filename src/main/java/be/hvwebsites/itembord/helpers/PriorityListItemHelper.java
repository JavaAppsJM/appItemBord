package be.hvwebsites.itembord.helpers;

import be.hvwebsites.libraryandroid4.helpers.IDNumber;

public class PriorityListItemHelper {
    private String itemTextLine1;
    private String itemTextLine2;
    private String itemStyle;
    private IDNumber itemID;

    public PriorityListItemHelper(
            String itemtext1,
            String itemtext2,
            String inStyle,
            IDNumber inID) {
        this.itemTextLine1 = itemtext1;
        this.itemTextLine2 = itemtext2;
        this.itemStyle = inStyle;
        this.itemID = inID;
    }

    public PriorityListItemHelper() {
    }

    public IDNumber getItemID() {
        return itemID;
    }

    public void setItemID(IDNumber itemID) {
        this.itemID = itemID;
    }

    public String getItemTextLine1() {
        return itemTextLine1;
    }

    public void setItemTextLine1(String itemTextLine1) {
        this.itemTextLine1 = itemTextLine1;
    }

    public String getItemTextLine2() {
        return itemTextLine2;
    }

    public void setItemTextLine2(String itemTextLine2) {
        this.itemTextLine2 = itemTextLine2;
    }

    public String getItemStyle() {
        return itemStyle;
    }

    public void setItemStyle(String itemStyle) {
        this.itemStyle = itemStyle;
    }


}
