package com.hotger.recipes.utils;

import android.app.Activity;
import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.view.View;

import com.hotger.recipes.BR;
import com.hotger.recipes.view.ControllableActivity;

/**
 * Model to use in dummy fragments
 */
public class MessageModel extends BaseObservable {

    private String msg;

    private int drawable;

    private boolean additionalBtnVisible;

    private ControllableActivity activity;

    public MessageModel(ControllableActivity activity, String msg,
                        int drawable, boolean additionalBtnVisible) {
        this.activity = activity;
        this.msg = msg;
        this.drawable = drawable;
        this.additionalBtnVisible = additionalBtnVisible;
    }

    @Bindable
    public String getMsg() {
        return msg;
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

    public void setMsg(String msg) {
        this.msg = msg;
        notifyPropertyChanged(BR.msg);
    }
}
