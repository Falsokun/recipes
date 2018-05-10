package com.hotger.recipes.utils;

import android.databinding.BaseObservable;
import android.databinding.Bindable;

/**
 * Model to use in dummy fragments
 */
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
}
