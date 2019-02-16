package io.armcha.ribble.presentation.navigationview;

public class NavigationItem {

    private NavigationId item ;
    private int icon = -1;
    private boolean isSelected = false;
    private int itemIconColor;

    public NavigationItem(NavigationId item, int icon, boolean isSelected, int itemIconColor) {
        this.item = item;
        this.icon = icon;
        this.isSelected = isSelected;
        this.itemIconColor = itemIconColor;
    }

    public NavigationId getNavigationId() {
        return item;
    }

    public void setItem(NavigationId item) {
        this.item = item;
    }

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public int getItemIconColor() {
        return itemIconColor;
    }

    public void setItemIconColor(int itemIconColor) {
        this.itemIconColor = itemIconColor;
    }

    @Override
    public String toString() {
        return "NavigationItem{" +
                "item=" + item +
                ", icon=" + icon +
                ", isSelected=" + isSelected +
                ", itemIconColor=" + itemIconColor +
                '}';
    }
}
