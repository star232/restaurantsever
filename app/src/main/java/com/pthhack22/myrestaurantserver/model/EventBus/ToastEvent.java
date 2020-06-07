package com.pthhack22.myrestaurantserver.model.EventBus;

public class ToastEvent {
    private boolean isUpdate;
    private boolean isFromLoadList;

    public ToastEvent(boolean isUpdate, boolean isFromLoadList) {
        this.isUpdate = isUpdate;
        this.isFromLoadList = isFromLoadList;
    }

    public boolean isFromLoadList() {
        return isFromLoadList;
    }

    public void setFromLoadList(boolean fromLoadList) {
        isFromLoadList = fromLoadList;
    }

    public boolean isUpdate() {
        return isUpdate;
    }

    public void setUpdate(boolean update) {
        isUpdate = update;
    }
}
