package be.hvwebsites.itembord.helpers;

import be.hvwebsites.libraryandroid4.helpers.IDNumber;

public class ListItemTwoLinesHelper {
    private String itemTextLine1;
    private String itemTextLine2;
    private String itemStyle;
    private IDNumber itemID;
    private int sortField1;
    private int sortField2;

    public ListItemTwoLinesHelper(
            String itemtext1,
            String itemtext2,
            String inStyle,
            IDNumber inID,
            int sortf1,
            int sortf2) {
        this.itemTextLine1 = itemtext1;
        this.itemTextLine2 = itemtext2;
        this.itemStyle = inStyle;
        this.itemID = inID;
        this.sortField1 = sortf1;
        this.sortField2 = sortf2;
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

    public int getSortField1() {
        return sortField1;
    }

    public void setSortField1(int sortField1) {
        this.sortField1 = sortField1;
    }

    public int getSortField2() {
        return sortField2;
    }

    public void setSortField2(int sortField2) {
        this.sortField2 = sortField2;
    }
}
