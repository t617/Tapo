package com.tplink.tapo.view;

import android.os.Bundle;
import android.support.v7.util.DiffUtil;
import android.util.Log;

import com.tplink.tapo.model.RecycleViewItemData;
import com.tplink.tapo.model.Subject;
import com.tplink.tapo.model.ViewAndText;

import java.util.List;

public class DiffUtilCallBack extends DiffUtil.Callback {
    private List<RecycleViewItemData> oldDatas;
    private List<RecycleViewItemData> newDatas;
    public DiffUtilCallBack(List<RecycleViewItemData> oldDatas, List<RecycleViewItemData> newDatas) {
        this.oldDatas = oldDatas;
        this.newDatas = newDatas;
    }
    @Override
    public int getOldListSize() {
        return oldDatas.size();
    }

    @Override
    public int getNewListSize() {
        return newDatas.size();
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        return oldDatas.get(oldItemPosition).getDataType() == newDatas.get(newItemPosition).getDataType();
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        if (newDatas.get(newItemPosition).getDataType() == 0 && oldDatas.get(oldItemPosition).getDataType() == 0) {
            Subject oldSubject = (Subject)oldDatas.get(oldItemPosition).getT();
            Subject newSubject = (Subject)newDatas.get(newItemPosition).getT();
            return oldSubject.getTitle().equals(newSubject.getTitle())
                    && oldSubject.getLocation().equals(newSubject.getLocation())
                    && oldSubject.getImg() == newSubject.getImg()
                    && oldSubject.getImgMore() == newSubject.getImgMore();
        }
        if (newDatas.get(newItemPosition).getDataType() == 1 && oldDatas.get(oldItemPosition).getDataType() == 1) {
            ViewAndText oldViewAndText = (ViewAndText)oldDatas.get(oldItemPosition).getT();
            ViewAndText newViewAndText = (ViewAndText)newDatas.get(newItemPosition).getT();
            return oldViewAndText.getTitle().equals(newViewAndText.getTitle());
        }
        if (newDatas.get(newItemPosition).getDataType() == 2 && oldDatas.get(oldItemPosition).getDataType() == 2)
            return true;
        return false;
    }
    @Override
    public Object getChangePayload(int oldItemPosition, int newItemPosition) {
        Log.e("tag", "title");
        Bundle payload = new Bundle();
        if (newDatas.get(newItemPosition).getDataType() == 0) {
            Subject newSubject = (Subject) newDatas.get(newItemPosition).getT();
            Subject oldSubject = (Subject) oldDatas.get(oldItemPosition).getT();
            if (!oldSubject.getTitle().equals(newSubject.getTitle())) {
                payload.putString("titleS", newSubject.getTitle());
                Log.e("tag", "titleS");
            }
            if (!oldSubject.getLocation().equals(newSubject.getLocation())) {
                payload.putString("location", newSubject.getLocation());
            }
            if (oldSubject.getImg() != newSubject.getImg()) {
                payload.putInt("img", newSubject.getImg());
            }
            if (oldSubject.getImgMore() != newSubject.getImgMore()) {
                payload.putInt("imgMore", newSubject.getImgMore());
            }
        }
        if (newDatas.get(newItemPosition).getDataType() == 1) {
            ViewAndText oldViewAndText = (ViewAndText) oldDatas.get(oldItemPosition).getT();
            ViewAndText newViewAndText = (ViewAndText) newDatas.get(newItemPosition).getT();
            if (!oldViewAndText.getTitle().equals(newViewAndText.getTitle())) {
                payload.putString("titleV", newViewAndText.getTitle());
            }
        }
        return payload;
    }
}
