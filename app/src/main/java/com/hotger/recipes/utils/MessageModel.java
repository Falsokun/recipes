package com.hotger.recipes.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.databinding.BaseObservable;
import android.databinding.Bindable;

import com.hotger.recipes.BR;

public class MessageModel extends BaseObservable {

    private String msg;

    private int drawable;

    private boolean additionalBtnVisible;

    public MessageModel(String msg, int drawable, boolean additionalBtnVisible) {
        this.msg = msg;
        this.drawable = drawable;
        this.additionalBtnVisible = additionalBtnVisible;
    }

    @Bindable
    public String getMsg() {
        return msg;
    }


    public void setMsg(String msg) {
        this.msg = msg;
        notifyPropertyChanged(BR.msg);
    }

    @Bindable
    public int getDrawable() {
        return drawable;
    }

    public void setDrawable(int drawable) {
        this.drawable = drawable;
    }

    @Bindable
    public boolean isAdditionalBtnVisible() {
        return additionalBtnVisible;
    }

    public void setAdditionalBtnVisible(boolean additionalBtnVisible) {
        this.additionalBtnVisible = additionalBtnVisible;
    }
}
