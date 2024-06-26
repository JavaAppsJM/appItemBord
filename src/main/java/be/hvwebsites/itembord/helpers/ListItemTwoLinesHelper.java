package be.hvwebsites.itembord.helpers;

import be.hvwebsites.libraryandroid4.helpers.IDNumber;

public class ListItemTwoLinesHelper {
    private String itemTextLine1;
    private String itemTextLine2;
    private String itemStyle;
    private IDNumber itemID;
    private String sortField1;
    private String sortField2;
    private int sortField3;

    public ListItemTwoLinesHelper(
            String itemtext1,
            String itemtext2,
            String inStyle,
            IDNumber inID,
            String sortf1,
            String sortf2,
            int sortf3) {
        this.itemTextLine1 = itemtext1;
        this.itemTextLine2 = itemtext2;
        this.itemStyle = inStyle;
        this.itemID = inID;
        this.sortField1 = sortf1;
        this.sortField2 = sortf2;
        this.sortField3 = sortf3;
    }

    public ListItemTwoLinesHelper() {
    }

    public void setLogItem(ListItemTwoLinesHelper inItem){
        this.itemTextLine1 = inItem.getItemTextLine1();
        this.itemTextLine2 = inItem.getItemTextLine2();
        this.itemStyle = inItem.getItemStyle();
        this.itemID = inItem.getItemID();
        this.sortField1 = inItem.getSortField1();
        this.sortField2 = inItem.getSortField2();
        this.sortField3 = inItem.getSortField3();
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

    public String getSortField1() {
        return sortField1;
    }

    public String getSortField2() {
        return sortField2;
    }

    public int getSortField3() {
        return sortField3;
    }
}
