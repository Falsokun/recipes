package com.hotger.recipes;

import android.databinding.BaseObservable;

public class BindableObject extends BaseObservable {
    private int a;

    public int getA() {
        return a;
    }

    public void setA(int a) {
        this.a = a;
    }
}
