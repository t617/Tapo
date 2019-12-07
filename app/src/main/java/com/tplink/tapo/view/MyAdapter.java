package com.tplink.tapo.view;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.tplink.tapo.R;
import com.tplink.tapo.databinding.ItemBlockBinding;
import com.tplink.tapo.databinding.ItemGridBinding;
import com.tplink.tapo.databinding.ItemTitleBinding;
import com.tplink.tapo.model.FavorBlock;
import com.tplink.tapo.model.IOperationData;
import com.tplink.tapo.model.RecycleViewItemData;
import com.tplink.tapo.model.Subject;
import com.tplink.tapo.model.ViewAndText;

import java.util.ArrayList;
import java.util.List;

public class MyAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements IOperationData {

    private List<RecycleViewItemData> datas;
    private static final int TYPE_SUBJECT = 0;
    private static final int TYPE_VIEWANDTEXT = 1;
    private static final int TYPE_FAVOR_BLOCK = 2;
    @Nullable
    private final SubjectClickCallback subjectClickCallback;

    public MyAdapter(SubjectClickCallback subjectClickCallback) {
        this.subjectClickCallback = subjectClickCallback;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_SUBJECT) {
            ItemGridBinding binding = DataBindingUtil.
                    inflate(LayoutInflater.from(parent.getContext()), R.layout.item_grid, parent, false);
            binding.setSubjectClickCallback(subjectClickCallback);
            return new SubjectViewHolder(binding);
        }
        if (viewType == TYPE_VIEWANDTEXT) {
            ItemTitleBinding binding = DataBindingUtil.
                    inflate(LayoutInflater.from(parent.getContext()), R.layout.item_title, parent, false);
            return new ViewAndTextViewHolder(binding);
        }
        if (viewType == TYPE_FAVOR_BLOCK) {
            ItemBlockBinding binding = DataBindingUtil.
                    inflate(LayoutInflater.from(parent.getContext()), R.layout.item_block, parent, false);
            return new FavorBlockViewHolder(binding);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        if (viewHolder instanceof ViewAndTextViewHolder) {
            ViewAndText viewAndText = (ViewAndText) datas.get(i).getT();
            ((ViewAndTextViewHolder)viewHolder).binding.setViewAndText(viewAndText);
        }
        if (viewHolder instanceof FavorBlockViewHolder) {
            FavorBlock favorBlock = (FavorBlock) datas.get(i).getT();
            ((FavorBlockViewHolder)viewHolder).binding.setFavorBlock(favorBlock);
        }
        if (viewHolder instanceof SubjectViewHolder) {
            Subject subject = (Subject) datas.get(i).getT();
            ((SubjectViewHolder) viewHolder).binding.setSubject(subject);
            ((SubjectViewHolder) viewHolder).binding.setIsLoading(false);
            ((SubjectViewHolder)viewHolder).binding.setIsChecked(false);
            ((SubjectViewHolder) viewHolder).binding.executePendingBindings();
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i, @NonNull List<Object> payloads) {
        if (payloads.isEmpty()) {
            onBindViewHolder(viewHolder, i);
        } else {
            if (viewHolder instanceof SubjectViewHolder) {
                Bundle payload = (Bundle) payloads.get(0);
                for (String key : payload.keySet()) {
                    switch (key) {
                        case "0":
                            ((SubjectViewHolder) viewHolder).binding.setIsLoading(true);
                            break;
                        case "1":
                            ((SubjectViewHolder) viewHolder).binding.setIsLoading(false);
                            break;
                        case "2":
                            ((SubjectViewHolder)viewHolder).binding.setIsChecked(false);
                            break;
                    }
                }
            }

        }
    }
    public void setItemList(final List<RecycleViewItemData> itemList) {
        if (datas == null) {
            datas = itemList;
            notifyItemRangeInserted(0, itemList.size());
        } else {
            // invalid
            List<RecycleViewItemData> oldDatas = new ArrayList<>(datas);
            DiffUtil.DiffResult result = DiffUtil.calculateDiff(new DiffUtilCallBack(itemList, oldDatas), true);
            datas = new ArrayList<>(itemList);
            result.dispatchUpdatesTo(this);
        }
    }

    public void setData(List<RecycleViewItemData> datasBackUp) {
        datas.clear();
        datas.addAll(datasBackUp);
        notifyDataSetChanged();
    }

    public void insertData(int position, RecycleViewItemData recycleViewItemData) {
        datas.add(position, recycleViewItemData);
        notifyItemInserted(position);
    }

    public void removeData(int position) {
        datas.remove(position);
        notifyItemRemoved(position);
    }

    @Override
    public void onItemMove(int fromPosition, int toPosition) {
        moveData(datas, fromPosition, toPosition);
        notifyItemMoved(fromPosition, toPosition);
    }
    public void moveData(List<RecycleViewItemData> data, int fromPosition, int toPosition) {
        if (data == null || data.size() < 2 || fromPosition == toPosition
                || fromPosition < 0|| fromPosition > data.size()
                || toPosition < 0 || toPosition >= data.size()) {
            return;
        }
        RecycleViewItemData temp = data.remove(fromPosition);
        data.add(toPosition, temp);
    }

    @Override
    public int getItemViewType(int i) {
        if (0 == datas.get(i).getDataType()) {
            return TYPE_SUBJECT;
        } else if (1 == datas.get(i).getDataType()) {
            return TYPE_VIEWANDTEXT;
        } else if (2 == datas.get(i).getDataType()) {
            return TYPE_FAVOR_BLOCK;
        }
        return super.getItemViewType(i);
    }

    @Override
    public int getItemCount() {
        return datas == null ? 0 : datas.size();
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        RecyclerView.LayoutManager manager=recyclerView.getLayoutManager();
        if(manager instanceof GridLayoutManager){
            final GridLayoutManager gridLayoutManager= (GridLayoutManager) manager;
            gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                @Override
                public int getSpanSize(int position) {
                    int type = getItemViewType(position);
                    switch (type){
                        case TYPE_SUBJECT:
                            return 1;
                        case TYPE_VIEWANDTEXT:
                        case TYPE_FAVOR_BLOCK:
                            return 2;
                    }
                    return 0;
                }
            });
        }
    }

    @Override
    public void onDetachedFromRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);
    }

    static class ViewAndTextViewHolder extends RecyclerView.ViewHolder {
        final ItemTitleBinding binding;
        public ViewAndTextViewHolder(ItemTitleBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    static class FavorBlockViewHolder extends RecyclerView.ViewHolder {
        final ItemBlockBinding binding;
        public FavorBlockViewHolder(ItemBlockBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    static class SubjectViewHolder extends RecyclerView.ViewHolder {
        final ItemGridBinding binding;
        public SubjectViewHolder(ItemGridBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
