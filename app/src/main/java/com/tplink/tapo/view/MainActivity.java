package com.tplink.tapo.view;

import android.app.Service;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.tplink.tapo.R;
import com.tplink.tapo.databinding.ActivityMainBinding;
import com.tplink.tapo.model.FavorBlock;
import com.tplink.tapo.model.RecycleViewItemData;
import com.tplink.tapo.model.Subject;
import com.tplink.tapo.model.ViewAndText;
import com.tplink.tapo.viewmodel.ItemRecycleViewModel;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity 
        implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener{
    private RecyclerView mRecyclerView;
    private String[] titles = {"智慧型插座1", "智慧型插座2", "智慧型插座3", "智慧型插座4"};
    private String[] locations = {"寝室", "寝室", "寝室", "寝室"};
    private List<RecycleViewItemData> datas = new ArrayList<>();
    private List<RecycleViewItemData> datasBackUp = new ArrayList<>();
    private ItemTouchHelper mItemTouchHelper;
    private MyAdapter myAdapter;
    private SwipeRefreshLayout mRefreshLayout;
    private TextView textViewSelect;
    private int myDevice;
    private boolean flagInit;
    private boolean flagInitBackUp;
    private boolean flagSelect;
    private ImageView imageViewCancel;
    private TextView textViewSave;
    private int selectCount;
    private Button buttonRemove;
    private Button buttonFavor;
    private int fromPosition;
    private int toPosition;
    private ItemRecycleViewModel itemRecycleViewModel;
    private ActivityMainBinding activityMainBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityMainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        initView();
        subscribeToModel();
        initData();
    }

    public void increaseSelect() {
        selectCount++;
        textViewSelect.setText("已选择" + selectCount + "个设备");
    }
    public void decreaseSelect() {
        selectCount--;
        textViewSelect.setText("已选择" + selectCount + "个设备");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    private void initData() {
        flagInit = true;
        flagSelect = false;
        selectCount = 0;
        int imageMoreId = getResources().getIdentifier("more", "mipmap", getPackageName());
        for (int i = 0; i < titles.length; i++) {
            int imageId = getResources().getIdentifier("icon"+ i, "mipmap", getPackageName());
            datas.add(new RecycleViewItemData(new Subject(titles[i], locations[i], imageId, imageMoreId), 0));
        }
        itemRecycleViewModel.getItems().setValue(datas);
    }

    public void setLongClickSelect() {
        activityMainBinding.setIsLoading(true);
        datasBackUp.clear();
        datasBackUp.addAll(datas);
        flagInitBackUp = flagInit;
        Bundle payload = new Bundle();
        payload.putString("0", "");
        myAdapter.notifyItemRangeChanged(0, myAdapter.getItemCount(), payload);
    }

    public void setLongClickInit() {
        myAdapter.insertData(0, new RecycleViewItemData(new ViewAndText(getResources().getString(R.string.devices)), 1));
        myAdapter.insertData(0, new RecycleViewItemData(new ViewAndText(getResources().getString(R.string.favorite)), 1));
        myAdapter.insertData(1, new RecycleViewItemData(new FavorBlock(getResources().getString(R.string.drag_devices)), 2));
        myDevice = 2;

    }

    private final SubjectClickCallback subjectClickCallback = new SubjectClickCallback() {
        @Override
        public void onItemClick(View v) {
            MyAdapter.SubjectViewHolder subjectViewHolder = (MyAdapter.SubjectViewHolder) mRecyclerView.getChildViewHolder(v);
            int position = subjectViewHolder.getAdapterPosition();
            Toast.makeText(MainActivity.this,"你点击了item按钮"+(position+1), Toast.LENGTH_SHORT).show();
        }

        @Override
        public boolean onItemLongClick(View v) {
            MyAdapter.SubjectViewHolder viewHolder = (MyAdapter.SubjectViewHolder) mRecyclerView.getChildViewHolder(v);
            if (!flagSelect) {
                setLongClickSelect();
                viewHolder.binding.setIsChecked(true);
                increaseSelect();
            } else {
                mItemTouchHelper.startDrag(viewHolder);
            }
            if (flagInit) {
                if (datas.get(0).getDataType() == 0) {
                    setLongClickInit();
                }
            }
            Vibrator vib = (Vibrator) getSystemService(Service.VIBRATOR_SERVICE);
            vib.vibrate(70);
            flagSelect = true;
            return false;
        }

        @Override
        public void onSwitchClick(CompoundButton compoundButton, boolean b) {
            View v = (View) compoundButton.getParent();
            RelativeLayout rlItem = (RelativeLayout) v.findViewById(R.id.RL_item);
            TextView textViewTitle = (TextView) v.findViewById(R.id.tv_title);
            if (!compoundButton.isPressed()|| flagSelect) {
                return;
            }
            if (b) {
                rlItem.setBackgroundResource(R.drawable.bg_blue);
                textViewTitle.setTextColor(Color.WHITE);
            } else {
                rlItem.setBackgroundResource(R.drawable.bg_white);
                textViewTitle.setTextColor(Color.BLACK);
            }
        }

        @Override
        public void onCheckBoxClick(CompoundButton compoundButton, boolean b) {
            if (!compoundButton.isPressed()|| !flagSelect) {
                return;
            }
            if (b) {
                increaseSelect();
            } else {
                decreaseSelect();
            }
        }
    };

    private void subscribeToModel() {
        final Observer<List<RecycleViewItemData>> statusObserver = new Observer<List<RecycleViewItemData>>() {
            @Override
            public void onChanged(@Nullable List<RecycleViewItemData> recycleViewItemData) {
                if (recycleViewItemData != null) {
                    myAdapter.setItemList(recycleViewItemData);
                }
            }
        };
        itemRecycleViewModel = ViewModelProviders.of(this).get(ItemRecycleViewModel.class);
        itemRecycleViewModel.getItems().observe(this, statusObserver);
    }

    private void findViewById() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
        mRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.sr_refresh);
        textViewSave = (TextView) findViewById(R.id.tv_save);
        imageViewCancel = (ImageView) findViewById(R.id.iv_cancel);
        buttonFavor = (Button) findViewById(R.id.bt_favorite);
        buttonRemove = (Button) findViewById(R.id.bt_remove);
        textViewSelect = (TextView) findViewById(R.id.tv_select);
        mRecyclerView = (RecyclerView) findViewById(R.id.rv_recyclerView);
    }

    private void initView() {
        findViewById();
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
        mRecyclerView.setLayoutManager(gridLayoutManager);
        myAdapter = new MyAdapter(subjectClickCallback);
        mRecyclerView.setAdapter(myAdapter);
        // 下拉刷新颜色控制
        mRefreshLayout.setColorSchemeResources(R.color.colorPrimaryDark, R.color.colorPrimary);
        buttonFavor.setOnClickListener(this);
        buttonRemove.setOnClickListener(this);
        textViewSave.setOnClickListener(this);
        imageViewCancel.setOnClickListener(this);

        mRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (flagSelect) {
                    mRefreshLayout.setRefreshing(false);
                } else {
                    mRefreshLayout.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mRefreshLayout.setRefreshing(false);
                        }
                    },2000);
                }
            }
        });
        setmItemTouchHelper();
    }
    public void setmItemTouchHelper() {
        mItemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.Callback() {
            @Override
            public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
                if (recyclerView.getLayoutManager() instanceof GridLayoutManager) {
                    final int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN |
                            ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT;
                    final int swipeFlags = 0;
                    return makeMovementFlags(dragFlags, swipeFlags);
                } else {
                    final int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
                    final int swipeFlags = 0;
                    return makeMovementFlags(dragFlags, swipeFlags);
                }
            }

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                //得到当拖拽的viewHolder的Position
                fromPosition = viewHolder.getAdapterPosition();
                toPosition = target.getAdapterPosition();
                if (toPosition == 0) { return false; }
                if (fromPosition > myDevice && toPosition <= myDevice) {
                    if (flagInit) {
                        clearView(recyclerView, viewHolder);
                    } else {
                        myDevice++;
                    }
                }
                if (toPosition == myDevice) return true;
                myAdapter.onItemMove(fromPosition, toPosition);
                if (fromPosition< myDevice && toPosition > myDevice){
                    if (datas.get(1).getDataType() == 1&& !flagInit) {
                        myAdapter.insertData(1, new RecycleViewItemData(new FavorBlock("将您最爱的设备拖到这里"), 2));
                        flagInit = true;
                    } else {
                        myDevice--;
                    }
                }
                return true;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) { }

            @Override
            public boolean isLongPressDragEnabled() {
                return false;
            }

            @Override
            public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
                super.onSelectedChanged(viewHolder, actionState);
            }

            @Override
            public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
                super.clearView(recyclerView, viewHolder);
                int isDrag = viewHolder.getAdapterPosition();
                if (flagInit) {
                    if (fromPosition == isDrag && fromPosition > myDevice && toPosition <= myDevice) {
                        myAdapter.onItemMove(fromPosition, toPosition);
                        myAdapter.removeData(1);
                        flagInit = false;
                    }
                }
            }
        });
        mItemTouchHelper.attachToRecyclerView(mRecyclerView);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        return false;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.bt_remove:
                setButtonRemove();
                break;
            case R.id.tv_save:
                setTextViewSave();
                break;
            case R.id.iv_cancel:
                setImageViewCancel();
                break;
            case R.id.bt_favorite:
                setButtonFavor();
                break;
        }
    }

    public void setButtonRemove() {
        // 从尾部批量删除数据
        for (int i = datas.size() - 1; i >= 0; i--) {
            if (datas.get(i).getDataType() == 0) {
                RelativeLayout relativeLayout = (RelativeLayout) mRecyclerView.getChildAt(i);
                CheckBox mCheckBox = (CheckBox) relativeLayout.findViewById(R.id.checkbox_subject);
                if (mCheckBox.isChecked()) {
                    int flagIsInitType = datas.get(2).getDataType();
                    myAdapter.removeData(i);
                    if (flagIsInitType == 1 && i == 1) {
                        myAdapter.insertData(1, new RecycleViewItemData(new FavorBlock("将您最爱的设备拖到这里"), 2));
                        flagInit = true;
                    }
                    decreaseSelect();
                }
            }
        }
    }

    public void setTextViewSave() {
        flagSelect = false;
        selectCount = 0;
        activityMainBinding.setIsLoading(false);
        if (myDevice == 2 && datas.get(1).getDataType() == 2) {
            myAdapter.removeData(0);
            myAdapter.removeData(0);
            myAdapter.removeData(0);
            flagInit = true;
        }
        Bundle payload = new Bundle();
        payload.putString("2", "");
        payload.putString("1", "");
        myAdapter.notifyItemRangeChanged(0, myAdapter.getItemCount(), payload);
    }

    public void setImageViewCancel() {
        flagSelect = false;
        selectCount = 0;
        activityMainBinding.setIsLoading(false);
        myAdapter.setData(datasBackUp);
        flagInit = flagInitBackUp;
    }

    public void setButtonFavor() { }
}