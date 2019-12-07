package com.tplink.tapo.view;

import android.view.View;
import android.widget.CompoundButton;

public interface SubjectClickCallback {
    boolean onItemLongClick(View view);
    void onItemClick(View view);
    void onSwitchClick(CompoundButton compoundButton, boolean b);
    void onCheckBoxClick(CompoundButton compoundButton, boolean b);
}
