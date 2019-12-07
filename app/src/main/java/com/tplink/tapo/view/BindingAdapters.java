package com.tplink.tapo.view;

import android.view.View;
import android.widget.ImageView;

public class BindingAdapters {
    @android.databinding.BindingAdapter("visibleGone")
    public static void showHide(View view, boolean show) {
        view.setVisibility(show ? View.VISIBLE : View.INVISIBLE);
    }
    @android.databinding.BindingAdapter("src")
    public static void setSrc(ImageView view, int resId) {
        view.setImageResource(resId);
    }
}
