package com.vedrax.util;

public class CellItem {

    private String key;
    private String value;
    private boolean bold;

    public CellItem() {
    }

    public CellItem(String key, String value, boolean bold) {
        this.key = key;
        this.value = value;
        this.bold = bold;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public boolean isBold() {
        return bold;
    }

    public void setBold(boolean bold) {
        this.bold = bold;
    }
}
