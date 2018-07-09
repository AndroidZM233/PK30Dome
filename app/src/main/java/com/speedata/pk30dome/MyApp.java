package com.speedata.pk30dome;

import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.liang.scancode.MsgEvent;
import com.speedata.pk30dome.bean.DaoMaster;
import com.speedata.pk30dome.bean.DaoSession;

import org.greenrobot.eventbus.EventBus;


import speedata.com.blelib.base.BaseBleApplication;
import speedata.com.blelib.service.BluetoothLeService;

import static speedata.com.blelib.service.BluetoothLeService.ACTION_DATA_AVAILABLE;
import static speedata.com.blelib.service.BluetoothLeService.ACTION_GATT_CONNECTED;
import static speedata.com.blelib.service.BluetoothLeService.ACTION_GATT_DISCONNECTED;


/**
 * Created by 张明_ on 2017/7/10.
 */

public class MyApp extends BaseBleApplication {

    private static MyApp m_application; // 单例
    public static String address = "";
    public static String name = "";
    //greendao
    private static DaoSession daoSession;

    private void setupDatabase() {
        //创建数据库
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(this, "pk30_dome.db", null);
        //获得可写数据库
        SQLiteDatabase db = helper.getWritableDatabase();
        //获得数据库对象
        DaoMaster daoMaster = new DaoMaster(db);
        //获得dao对象管理者
        daoSession = daoMaster.newSession();
    }

    public static DaoSession getDaoInstant() {
        return daoSession;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        m_application = this;
        setupDatabase();
    }


    public static MyApp getInstance() {
        return m_application;
    }


    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    public void getDeviceName(BluetoothDevice device) {
        address = device.getAddress();
        name = device.getName();
        bindServiceAndRegisterReceiver(device);
        registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
    }

    private IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ACTION_GATT_CONNECTED);
        intentFilter.addAction(ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(ACTION_DATA_AVAILABLE);
        return intentFilter;
    }

    // Handles various events fired by the Service.处理由服务触发的各种事件。
    // ACTION_GATT_CONNECTED: connected to a GATT server.连接到GATT服务器
    // ACTION_GATT_DISCONNECTED: disconnected from a GATT server.与GATT服务器断开连接
    // ACTION_GATT_SERVICES_DISCOVERED: discovered GATT services.发现了GATT服务
    // ACTION_DATA_AVAILABLE: 数据通知
    // （1、EXTRA_DATA 设置回复信息 2、NOTIFICATION_DATA_LWHG 长宽高重测量信息
    // 3、NOTIFICATION_DATA 长宽高体积条码测量信息 4、NOTIFICATION_DATA_ERR 错误信息）
    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (ACTION_GATT_CONNECTED.equals(action)) {
                EventBus.getDefault().post(new MsgEvent("KP", false));
                boolean cn = getApplicationContext().getResources().getConfiguration().locale.getCountry().equals("CN");
                if (cn) {
                    Toast.makeText(getApplicationContext(), "已连接", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getApplicationContext(), "Connection", Toast.LENGTH_LONG).show();
                }
                EventBus.getDefault().post(new MsgEvent("ServiceConnectedStatus", true));
            } else if (ACTION_GATT_DISCONNECTED.equals(action)) {
                EventBus.getDefault().post(new MsgEvent("ServiceConnectedStatus", false));
                boolean cn = getApplicationContext().getResources().getConfiguration().locale.getCountry().equals("CN");
                if (cn) {
                    Toast.makeText(getApplicationContext(), "已断开", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getApplicationContext(), "Disconnect", Toast.LENGTH_LONG).show();
                }

                Log.d("ZM_connect", "application里面的断开连接");
                if (wantDisconnect) {
                    unregisterReceiver(mGattUpdateReceiver);
                } else {
                    EventBus.getDefault().post(new MsgEvent("KP", true));
                }
            } else if (ACTION_DATA_AVAILABLE.equals(action)) {
                String data = intent.getStringExtra(BluetoothLeService.EXTRA_DATA);
                if (TextUtils.isEmpty(data)) {
                    String dataERR = intent.getStringExtra(BluetoothLeService.NOTIFICATION_DATA_ERR);
                    if (TextUtils.isEmpty(dataERR)) {
                        String l = intent.getStringExtra(BluetoothLeService.NOTIFICATION_DATA_L);
                        if (!TextUtils.isEmpty(l)) {
                            EventBus.getDefault().post(new MsgEvent("L", l));
                        }
                        String w = intent.getStringExtra(BluetoothLeService.NOTIFICATION_DATA_W);
                        if (!TextUtils.isEmpty(w)) {
                            EventBus.getDefault().post(new MsgEvent("W", w));
                        }
                        String h = intent.getStringExtra(BluetoothLeService.NOTIFICATION_DATA_H);
                        if (!TextUtils.isEmpty(h)) {
                            EventBus.getDefault().post(new MsgEvent("H", h));
                        }
                        String g = intent.getStringExtra(BluetoothLeService.NOTIFICATION_DATA_G);
                        if (!TextUtils.isEmpty(g)) {
                            EventBus.getDefault().post(new MsgEvent("G", g));
                        }
                        String mac = intent.getStringExtra(BluetoothLeService.NOTIFICATION_DATA_MAC);
                        if (!TextUtils.isEmpty(mac)) {
                            EventBus.getDefault().post(new MsgEvent("MAC", mac));
                        }
                        String soft = intent.getStringExtra(BluetoothLeService.NOTIFICATION_DATA_SOFT);
                        if (!TextUtils.isEmpty(soft)) {
                            EventBus.getDefault().post(new MsgEvent("SOFT", soft));
                        }
                        String hard = intent.getStringExtra(BluetoothLeService.NOTIFICATION_DATA_HARD);
                        if (!TextUtils.isEmpty(hard)) {
                            EventBus.getDefault().post(new MsgEvent("HARD", hard));
                        }
                        String model = intent.getStringExtra(BluetoothLeService.NOTIFICATION_DATA_MODEL);
                        if (!TextUtils.isEmpty(model)) {
                            EventBus.getDefault().post(new MsgEvent("MODEL", model));
                        }
                        String shutdown = intent.getStringExtra(BluetoothLeService.NOTIFICATION_SHUTDOWN);
                        if (!TextUtils.isEmpty(shutdown)) {
                            EventBus.getDefault().post(new MsgEvent("SHUTDOWN", shutdown));
                        }

                        String fengMing = intent.getStringExtra(BluetoothLeService.NOTIFICATION_FENGMING);
                        if (!TextUtils.isEmpty(fengMing)) {
                            EventBus.getDefault().post(new MsgEvent("FENGMING", fengMing));
                        }
                    } else {
                        EventBus.getDefault().post(new MsgEvent("Save6DataErr", dataERR));
                    }
                }


            }
        }
    };


}
