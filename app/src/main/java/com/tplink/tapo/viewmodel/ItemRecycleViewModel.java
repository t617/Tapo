package com.tplink.tapo.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import com.tplink.tapo.model.RecycleViewItemData;

import java.util.List;

public class ItemRecycleViewModel extends AndroidViewModel {
    private MutableLiveData<List<RecycleViewItemData>> items;

    public ItemRecycleViewModel(@NonNull Application application) {
        super(application);
    }

    public MutableLiveData<List<RecycleViewItemData>> getItems() {
        if (items == null) {
            items = new MutableLiveData<List<RecycleViewItemData>>();
        }
        return items;
    }
}
