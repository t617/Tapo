package com.tplink.tapo.model;

public class RecycleViewItemData<T> {
    //用来装载不同类型的item数据bean
    T t;
    //item数据bean的类型
    int dataType;

    public RecycleViewItemData (T t, int dataType) {
        this.t = t;
        this.dataType = dataType;
    }

    public T getT () {
        return t;
    }

    public int getDataType () {
        return dataType;
    }
}