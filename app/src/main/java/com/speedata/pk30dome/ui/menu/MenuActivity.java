package com.speedata.pk30dome.ui.menu;


import android.Manifest;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.liang.scancode.MsgEvent;
import com.speedata.pk30dome.MyApp;
import com.speedata.pk30dome.R;
import com.speedata.pk30dome.mvp.MVPBaseActivity;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.PermissionListener;
import com.yanzhenjie.permission.Rationale;
import com.yanzhenjie.permission.RationaleListener;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import speedata.com.blelib.utils.PK30DataUtils;


public class MenuActivity extends MVPBaseActivity<MenuContract.View, MenuPresenter>
        implements MenuContract.View, View.OnClickListener {

    private TextView device_name;
    private TextView device_address;
    private ToggleButton btn_serviceStatus;
    private LinearLayout ll;
    private TextView mTvL;
    private TextView mTvW;
    private TextView mTvH;
    private TextView mTvG;
    private TextView tvSoftware;
    private Button btnSoftware;
    private TextView tvHardware;
    private Button btnHardware;
    private Spinner spModel;
    private Button btnModel;
    private TextView tvMac;
    private Button btnMac;

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
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
        } else if ("L".equals(type)) {
            String string = (String) msg;
            mTvL.setText("L:" + string);
        } else if ("W".equals(type)) {
            String string = (String) msg;
            mTvW.setText("W:" + string);
        } else if ("H".equals(type)) {
            String string = (String) msg;
            mTvH.setText("H:" + string);
        } else if ("G".equals(type)) {
            String string = (String) msg;
            mTvG.setText("G:" + string);
        } else if ("MAC".equals(type)) {
            tvMac.setText(msg + "");
        } else if ("SOFT".equals(type)) {
            tvSoftware.setText(msg + "");
        } else if ("HARD".equals(type)) {
            tvHardware.setText(msg + "");
        } else if ("MODEL".equals(type)) {
            String string = (String) msg;
            switch (string) {
                case "00":
                    string = "模式更改为长度测量";
                    break;
                case "02":
                    string = "模式更改为宽度测量";
                    break;
                case "03":
                    string = "模式更改为高度测量";
                    break;
                case "01":
                    string = "模式更改为重量测量";
                    break;
            }
            Toast.makeText(this, string, Toast.LENGTH_SHORT).show();
        }

    }


    private void initView() {
        device_name = findViewById(R.id.device_name);
        device_address = findViewById(R.id.device_address);
        btn_serviceStatus = findViewById(R.id.btn_serviceStatus);

        btn_serviceStatus.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
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
        mTvG = findViewById(R.id.tv_g);
        tvSoftware = (TextView) findViewById(R.id.tv_software);
        btnSoftware = (Button) findViewById(R.id.btn_software);
        btnSoftware.setOnClickListener(this);
        tvHardware = (TextView) findViewById(R.id.tv_hardware);
        btnHardware = (Button) findViewById(R.id.btn_hardware);
        btnHardware.setOnClickListener(this);
        spModel = (Spinner) findViewById(R.id.sp_model);
        btnModel = (Button) findViewById(R.id.btn_model);
        btnModel.setOnClickListener(this);
        tvMac = findViewById(R.id.tv_mac);
        btnMac = findViewById(R.id.btn_mac);
        btnMac.setOnClickListener(this);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_mac:
                PK30DataUtils.getMac();
                break;
            case R.id.btn_software:
                PK30DataUtils.getSoftware();
                break;
            case R.id.btn_hardware:
                PK30DataUtils.getHardware();
                break;
            case R.id.btn_model:
                PK30DataUtils.setModel(spModel.getSelectedItemPosition());
                break;
        }
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
