package com.speedata.pk30dome.ui.main;


import android.Manifest;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.kaopiz.kprogresshud.KProgressHUD;
import com.liang.scancode.CommonScanActivity;
import com.liang.scancode.MsgEvent;
import com.liang.scancode.utils.Constant;
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

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import speedata.com.blelib.base.BaseBleApplication;
import speedata.com.blelib.utils.DataManageUtils;
import speedata.com.blelib.utils.PK30DataUtils;


/**
 * MVPPlugin
 * 邮箱 784787081@qq.com
 */
@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
public class MainActivity extends MVPBaseActivity<MainContract.View, MainPresenter> implements MainContract.View, View.OnClickListener {

    private Button mBtnScan;
    private TextView mTvCode;
    private Button mBtnLength;
    private TextView mTvLength;
    private Button mBtnWidth;
    private TextView mTvWidth;
    private Button mBtnHeight;
    private TextView mTvHeight;
    private Button mBtnWeight;
    private TextView mTvWeight;
    private Button mBtnTest;
    private CheckBox mCbScan;
    private CheckBox mCbLength;
    private CheckBox mCbWidth;
    private CheckBox mCbHeight;
    private CheckBox mCbWeight;
    private TextView mTvSoftware;
    private TextView mTvHardware;
    private Button mBtnFengming;
    private SeekBar mProgress;
    private TextView mTvSeekbarValue;
    private Button mBtnClose;
    private LinearLayout mLl;
    private TextView mDeviceName;
    private TextView mDeviceAddress;
    private LinearLayout mIvOn;
    private KProgressHUD kProgressHUD;
    private Queue<Integer> queue;
    //开始测试
    private boolean isTest = false;
    private Button mBtnTestClose;
    private Button mBtnSoftware;
    private Button mBtnHardware;
    private TextView mTvVersion;


    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_main);
        permission();
        EventBus.getDefault().register(this);

        // 初始化 Bluetooth adapter, 通过蓝牙管理器得到一个参考蓝牙适配器(API必须在以上android4.3或以上和版本)
        final BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        @SuppressLint({"NewApi", "LocalSuppress"}) BluetoothAdapter mBluetoothAdapter = bluetoothManager.getAdapter();
        if (!mBluetoothAdapter.isEnabled()) {
            mBluetoothAdapter.enable();
        }

        initView();
        mBtnTestClose.setEnabled(false);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    private void initView() {
        mBtnScan = findViewById(R.id.btn_scan);
        mBtnScan.setOnClickListener(this);
        mTvCode = findViewById(R.id.tv_code);
        mBtnLength = findViewById(R.id.btn_length);
        mBtnLength.setOnClickListener(this);

        mTvLength = findViewById(R.id.tv_length);
        mBtnWidth = findViewById(R.id.btn_width);
        mBtnWidth.setOnClickListener(this);

        mTvWidth = findViewById(R.id.tv_width);
        mBtnHeight = findViewById(R.id.btn_height);
        mBtnHeight.setOnClickListener(this);

        mTvHeight = findViewById(R.id.tv_height);
        mBtnWeight = findViewById(R.id.btn_weight);
        mBtnWeight.setOnClickListener(this);

        mTvWeight = findViewById(R.id.tv_weight);
        mBtnTest = findViewById(R.id.btn_test);
        mBtnTest.setOnClickListener(this);

        mCbScan = findViewById(R.id.cb_scan);
        mCbLength = findViewById(R.id.cb_length);
        mCbWidth = findViewById(R.id.cb_width);
        mCbHeight = findViewById(R.id.cb_height);
        mCbWeight = findViewById(R.id.cb_weight);
        mTvSoftware = findViewById(R.id.tv_software);
        mTvHardware = findViewById(R.id.tv_hardware);
        mBtnFengming = findViewById(R.id.btn_fengming);
        mBtnFengming.setOnClickListener(this);

        mProgress = findViewById(R.id.progress);
        mProgress.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mTvSeekbarValue.setText((progress + 50) + "");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        mTvSeekbarValue = findViewById(R.id.tv_seekbar_value);
        mBtnClose = findViewById(R.id.btn_close);
        mBtnClose.setOnClickListener(this);
        mLl = findViewById(R.id.ll);
        mDeviceName = findViewById(R.id.device_name);
        mDeviceAddress = findViewById(R.id.device_address);
        mIvOn = findViewById(R.id.iv_on);
        mIvOn.setOnClickListener(this);

        boolean cn = getApplicationContext().getResources().getConfiguration().locale.getCountry().equals("CN");
        if (cn) {
            kProgressHUD = KProgressHUD.create(getApplicationContext())
                    .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                    .setLabel("重连中...")
                    .setCancellable(false)
                    .setAnimationSpeed(2)
                    .setDimAmount(0.5f);
        } else {
            kProgressHUD = KProgressHUD.create(getApplicationContext())
                    .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                    .setLabel("Reconnection...")
                    .setCancellable(false)
                    .setAnimationSpeed(2)
                    .setDimAmount(0.5f);
        }

        mBtnTestClose = findViewById(R.id.btn_test_close);
        mBtnTestClose.setOnClickListener(this);
        mBtnSoftware = findViewById(R.id.btn_software);
        mBtnSoftware.setOnClickListener(this);
        mBtnHardware = findViewById(R.id.btn_hardware);
        mBtnHardware.setOnClickListener(this);
        mTvVersion = findViewById(R.id.tv_version);
        mTvVersion.setText("V" + getVerName(getApplicationContext()));
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(MsgEvent mEvent) {
        String type = mEvent.getType();
        Object msg = mEvent.getMsg();
        if ("ServiceConnectedStatus".equals(type)) {
            boolean result = (boolean) msg;
            Log.d("ZM_connect", "First:" + result);

            if (result) {
                mLl.setVisibility(View.VISIBLE);
                Log.d("ZM_connect", "显示连接按键");
                mDeviceAddress.setText("Address：" + MyApp.address);
                mDeviceName.setText("Name：" + MyApp.name);
                mIvOn.setVisibility(View.VISIBLE);
            } else {
                testClose();
                mLl.setVisibility(View.GONE);
                Log.d("ZM_connect", "隐藏连接按键");
                mIvOn.setVisibility(View.GONE);
            }
            Log.d("ZM_connect", "" + result);

        } else if ("Save6DataErr".equals(type)) {
            Toast.makeText(MainActivity.this, (String) msg, Toast.LENGTH_SHORT).show();
        } else if ("L".equals(type)) {
            String string = (String) msg;
            mTvLength.setText("L:" + string);
            doLoop();
        } else if ("W".equals(type)) {
            String string = (String) msg;
            mTvWidth.setText("W:" + string);
            doLoop();
        } else if ("H".equals(type)) {
            String string = (String) msg;
            mTvHeight.setText("H:" + string);
            doLoop();
        } else if ("G".equals(type)) {
            String string = (String) msg;
            mTvWeight.setText("G:" + string);
            doLoop();
        } else if ("SOFT".equals(type)) {
            mTvSoftware.setText(msg + "");
        } else if ("HARD".equals(type)) {
            mTvHardware.setText(msg + "");
        } else if (type.equals("codeResult")) {
            mTvCode.setText(msg + "");
            doLoop();

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
        } else if ("SHUTDOWN".equals(type)) {
            String string = (String) msg;
            if ("01".equals(string)) {
                Toast.makeText(MainActivity.this, "关机成功", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(MainActivity.this, "关机失败", Toast.LENGTH_SHORT).show();
            }
        } else if ("FENGMING".equals(type)) {
            String string = (String) msg;
            int toInt = DataManageUtils.HexToInt(string);
            Toast.makeText(MainActivity.this, "蜂鸣器时长设置为" + toInt, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 循环执行测量
     */
    private void doLoop() {
        if (isTest && queue != null) {
            Integer integer = queue.poll();
            if (integer != null) {
                PK30DataUtils.setModel(integer);
            } else {
                test();
            }

        }
    }


    @Override
    public void onClick(View v) {
        if (BaseBleApplication.mNotifyCharacteristic3 != null) {
            switch (v.getId()) {
                case R.id.btn_scan:
                    startScanAct();
                    break;

                case R.id.iv_on:
                    closeBle();
                    break;

                case R.id.btn_software:
                    PK30DataUtils.getSoftware();
                    break;
                case R.id.btn_hardware:
                    PK30DataUtils.getHardware();
                    break;

                case R.id.btn_length:
                    PK30DataUtils.setModel(0);
                    break;
                case R.id.btn_width:
                    PK30DataUtils.setModel(1);
                    break;
                case R.id.btn_height:
                    PK30DataUtils.setModel(2);
                    break;
                case R.id.btn_weight:
                    PK30DataUtils.setModel(3);
                    break;

                case R.id.btn_test:
                    boolean b = test();
                    if (b) {
                        mBtnScan.setEnabled(false);
                        mBtnLength.setEnabled(false);
                        mBtnWeight.setEnabled(false);
                        mBtnHeight.setEnabled(false);
                        mBtnWidth.setEnabled(false);
                        mBtnSoftware.setEnabled(false);
                        mBtnHardware.setEnabled(false);
                        mBtnFengming.setEnabled(false);
                        mBtnClose.setEnabled(false);
                        mBtnTest.setEnabled(false);
                        mBtnTestClose.setEnabled(true);
                    }

                    break;

                case R.id.btn_test_close:
                    testClose();
                    break;

                case R.id.btn_close:
                    PK30DataUtils.shutdown();
                    break;

                case R.id.btn_fengming:
                    int time = Integer.parseInt(mTvSeekbarValue.getText().toString());
                    PK30DataUtils.fengMing(time);
                    break;
            }
        } else {
            Toast.makeText(this, "请先连接PK30设备", Toast.LENGTH_SHORT).show();
        }

    }

    private void testClose() {
        isTest = false;
        mBtnScan.setEnabled(true);
        mBtnLength.setEnabled(true);
        mBtnWeight.setEnabled(true);
        mBtnHeight.setEnabled(true);
        mBtnWidth.setEnabled(true);
        mBtnSoftware.setEnabled(true);
        mBtnHardware.setEnabled(true);
        mBtnFengming.setEnabled(true);
        mBtnClose.setEnabled(true);
        mBtnTest.setEnabled(true);
        mBtnTestClose.setEnabled(false);
    }

    /**
     * 启动测试
     */
    private boolean test() {
        queue = new LinkedList<Integer>();
        isTest = true;

        boolean lengthChecked = mCbLength.isChecked();
        if (lengthChecked) {
            queue.offer(0);
        }
        boolean widthChecked = mCbWidth.isChecked();
        if (widthChecked) {
            queue.offer(1);
        }
        boolean heightChecked = mCbHeight.isChecked();
        if (heightChecked) {
            queue.offer(2);
        }
        boolean weightChecked = mCbWeight.isChecked();
        if (weightChecked) {
            queue.offer(3);
        }

        boolean checked = mCbScan.isChecked();
        if (checked) {
            startScanAct();
        } else {
            if (queue.size() != 0) {
                doLoop();
            } else {
                Toast.makeText(this, "请先勾选需要启动的测量模式", Toast.LENGTH_SHORT).show();
                return false;
            }

        }
        return true;
    }

    private void startScanAct() {
        Intent intent = new Intent(this, CommonScanActivity.class);
        intent.putExtra(Constant.REQUEST_SCAN_MODE, Constant.REQUEST_SCAN_MODE_BARCODE_MODE);
        startActivity(intent);
    }


    private void closeBle() {
        MyApp.getInstance().wantDisconnectBle();
        MyApp.getInstance().disconnect();
        mLl.setVisibility(View.GONE);
        Log.d("ZM_connect", "点击了断开");
        mIvOn.setVisibility(View.GONE);
    }


    /**
     * 权限申请
     */
    private void permission() {
        AndPermission.with(MainActivity.this)
                .permission(Manifest.permission.ACCESS_COARSE_LOCATION
                        , Manifest.permission.WRITE_EXTERNAL_STORAGE
                        , Manifest.permission.BLUETOOTH
                        , Manifest.permission.BLUETOOTH_ADMIN
                        , Manifest.permission.VIBRATE
                        , Manifest.permission.CAMERA)
                .callback(listener)
                .rationale(new RationaleListener() {
                    @Override
                    public void showRequestPermissionRationale(int requestCode, Rationale rationale) {
                        AndPermission.rationaleDialog(MainActivity.this, rationale).show();
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
            if (AndPermission.hasAlwaysDeniedPermission(MainActivity.this, deniedPermissions)) {
                AndPermission.defaultSettingDialog(MainActivity.this, 300).show();
            }
        }
    };


    /**
     * 得到软件显示版本信息
     *
     * @param context 上下文
     * @return 当前版本信息
     */
    public static String getVerName(Context context) {
        String verName = "";
        try {
            String packageName = context.getPackageName();
            verName = context.getPackageManager()
                    .getPackageInfo(packageName, 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return verName;
    }
}
