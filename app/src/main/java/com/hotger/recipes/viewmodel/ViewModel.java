package com.hotger.recipes.viewmodel;

import android.databinding.BaseObservable;

public abstract class ViewModel extends BaseObservable {

    public abstract void OnResume();

    public abstract void OnPause();
}
