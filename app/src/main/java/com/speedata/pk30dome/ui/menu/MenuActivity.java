package com.speedata.pk30dome.ui.menu;


import android.Manifest;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.speedata.pk30dome.MyApp;
import com.speedata.pk30dome.R;
import com.speedata.pk30dome.adapter.RVAdapter;
import com.speedata.pk30dome.bean.Data;
import com.speedata.pk30dome.bean.MsgEvent;
import com.speedata.pk30dome.mvp.MVPBaseActivity;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.PermissionListener;
import com.yanzhenjie.permission.Rationale;
import com.yanzhenjie.permission.RationaleListener;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import speedata.com.blelib.bean.LWHData;
import xyz.reginer.baseadapter.CommonRvAdapter;


public class MenuActivity extends MVPBaseActivity<MenuContract.View, MenuPresenter>
        implements MenuContract.View, View.OnClickListener, CommonRvAdapter.OnItemClickListener {

    private TextView device_name;
    private TextView device_address;
    private ToggleButton btn_serviceStatus;
    private LinearLayout ll;
    private TextView mTvL;
    private TextView mTvW;
    private TextView mTvH;
    private RecyclerView rv_content;
    private List<Data> datas;
    private TextView tv_countNum;
    private RVAdapter mAdapter;
    private LinearLayoutManager layoutManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_menu);
        EventBus.getDefault().register(this);
        permission();

        // 初始化 Bluetooth adapter, 通过蓝牙管理器得到一个参考蓝牙适配器(API必须在以上android4.3或以上和版本)
        final BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        @SuppressLint({"NewApi", "LocalSuppress"}) BluetoothAdapter mBluetoothAdapter = bluetoothManager.getAdapter();
        if (!mBluetoothAdapter.isEnabled()) {
            mBluetoothAdapter.enable();
        }

        initView();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMain(MsgEvent msgEvent) {
        String type = msgEvent.getType();
        Object msg = msgEvent.getMsg();
        if ("ServiceConnectedStatus".equals(type)) {
            boolean result = (boolean) msg;
            if (result) {
                ll.setVisibility(View.VISIBLE);
                device_address.setText("Address：" + MyApp.address);
                device_name.setText("Name：" + MyApp.name);
            } else {
                ll.setVisibility(View.GONE);
            }
            btn_serviceStatus.setChecked(result);

        } else if ("Save6DataErr".equals(type)) {
            Toast.makeText(MenuActivity.this, (String) msg, Toast.LENGTH_SHORT).show();
        } else if ("Save6DataSuccess".equals(type)) {
            MyApp.getInstance().writeCharacteristic6("AA0A020100000000000000000000000000000200");
            Toast.makeText(MenuActivity.this, (String) msg, Toast.LENGTH_SHORT).show();
            datas.clear();
            List<Data> dataList = MyApp.getDaoInstant().getDataDao().loadAll();
            datas.addAll(dataList);
            handler.sendMessage(handler.obtainMessage());
        } else if ("LWHData".equals(type)) {
            LWHData lwhData = (LWHData) msg;
            mTvH.setText("H:" + lwhData.H);
            mTvL.setText("L:" + lwhData.L);
            mTvW.setText("W:" + lwhData.W);
        }

    }


    private void initView() {
        device_name = findViewById(R.id.device_name);
        device_address = findViewById(R.id.device_address);
        btn_serviceStatus = findViewById(R.id.btn_serviceStatus);

        btn_serviceStatus.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    MyApp.getInstance().connect();
                    Log.d("ZM_connect", "点击了连接");
                } else {
                    MyApp.getInstance().disconnect();
                    ll.setVisibility(View.GONE);
                    Log.d("ZM_connect", "点击了断开");
                }
            }
        });

        ll = findViewById(R.id.ll);
        mTvL = findViewById(R.id.tv_l);
        mTvW = findViewById(R.id.tv_w);
        mTvH = findViewById(R.id.tv_h);
        rv_content = findViewById(R.id.rv_content);
        tv_countNum = findViewById(R.id.tv_countNum);
        datas = MyApp.getDaoInstant().getDataDao().loadAll();
        tv_countNum.setText("数据总数：" + datas.size());
        initRV();
    }

    private void initRV() {
        mAdapter = new RVAdapter(getApplicationContext(), R.layout.info_show, datas);
        rv_content.addItemDecoration(new DividerItemDecoration(getApplicationContext(),
                DividerItemDecoration.VERTICAL));
        layoutManager = new LinearLayoutManager(getApplicationContext());
        layoutManager.setStackFromEnd(true);//列表再底部开始展示，反转后由上面开始展示
        layoutManager.setReverseLayout(true);//列表翻转
        rv_content.setLayoutManager(layoutManager);
        rv_content.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(this);
    }


    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            mAdapter.notifyDataSetChanged();
        }
    };


    @Override
    public void onClick(View v) {

    }

    @Override
    public void onItemClick(RecyclerView.ViewHolder viewHolder, View view, int position) {

    }

    private long mkeyTime = 0;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
            case KeyEvent.ACTION_DOWN:
                if ((System.currentTimeMillis() - mkeyTime) > 2000) {
                    mkeyTime = System.currentTimeMillis();
                    Toast.makeText(MenuActivity.this, "再按一次退出", Toast.LENGTH_SHORT).show();
                } else {
                    try {
                        finish();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                return false;
        }
        return super.onKeyDown(keyCode, event);
    }


    /**
     * 权限申请
     */
    private void permission() {
        AndPermission.with(MenuActivity.this)
                .permission(Manifest.permission.ACCESS_COARSE_LOCATION
                        , Manifest.permission.WRITE_EXTERNAL_STORAGE
                        , Manifest.permission.BLUETOOTH
                        , Manifest.permission.BLUETOOTH_ADMIN
                        , Manifest.permission.INTERNET)
                .callback(listener)
                .rationale(new RationaleListener() {
                    @Override
                    public void showRequestPermissionRationale(int requestCode, Rationale rationale) {
                        AndPermission.rationaleDialog(MenuActivity.this, rationale).show();
                    }
                }).start();
    }

    PermissionListener listener = new PermissionListener() {
        @Override
        public void onSucceed(int requestCode, @NonNull List<String> grantPermissions) {
        }

        @Override
        public void onFailed(int requestCode, @NonNull List<String> deniedPermissions) {
            // 用户否勾选了不再提示并且拒绝了权限，那么提示用户到设置中授权。
            if (AndPermission.hasAlwaysDeniedPermission(MenuActivity.this, deniedPermissions)) {
                AndPermission.defaultSettingDialog(MenuActivity.this, 300).show();
            }
        }
    };


}
