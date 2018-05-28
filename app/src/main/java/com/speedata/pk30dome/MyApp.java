package com.speedata.pk30dome;

import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;
import android.widget.Toast;

import com.speedata.pk30dome.bean.DaoMaster;
import com.speedata.pk30dome.bean.DaoSession;
import com.speedata.pk30dome.bean.Data;
import com.speedata.pk30dome.bean.MsgEvent;

import org.greenrobot.eventbus.EventBus;


import speedata.com.blelib.base.BaseBleApplication;
import speedata.com.blelib.bean.LWHData;
import speedata.com.blelib.bean.PK20Data;
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
    // （1、EXTRA_DATA 设置回复信息 2、NOTIFICATION_DATA_LWH 长宽高测量信息
    // 3、NOTIFICATION_DATA 长宽高体积条码测量信息 4、NOTIFICATION_DATA_ERR 错误信息）
    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (ACTION_GATT_CONNECTED.equals(action)) {
                Toast.makeText(MyApp.this, "连接成功", Toast.LENGTH_SHORT).show();
                EventBus.getDefault().post(new MsgEvent("ServiceConnectedStatus", true));
            } else if (ACTION_GATT_DISCONNECTED.equals(action)) {
                EventBus.getDefault().post(new MsgEvent("ServiceConnectedStatus", false));
                unregisterReceiver(mGattUpdateReceiver);
                Toast.makeText(MyApp.this, "断开连接", Toast.LENGTH_SHORT).show();
            } else if (ACTION_DATA_AVAILABLE.equals(action)) {
                String data = intent.getStringExtra(BluetoothLeService.EXTRA_DATA);
                if (TextUtils.isEmpty(data)) {
                    LWHData lwh = intent.getParcelableExtra(BluetoothLeService.NOTIFICATION_DATA_LWH);
                    if (lwh != null) {
                        EventBus.getDefault().post(new MsgEvent("LWHData", lwh));
                    } else {
                        String dataERR = intent.getStringExtra(BluetoothLeService.NOTIFICATION_DATA_ERR);
                        if (TextUtils.isEmpty(dataERR)) {
                            PK20Data mPK20Data = intent.getParcelableExtra(BluetoothLeService.NOTIFICATION_DATA);
                            Data mData = new Data();
                            mData.setWangDian(mPK20Data.wangDian);
                            mData.setCenter(mPK20Data.center);
                            mData.setMuDi(mPK20Data.muDi);
                            mData.setLiuCheng(mPK20Data.liuCheng);
                            mData.setL(mPK20Data.L);
                            mData.setW(mPK20Data.W);
                            mData.setH(mPK20Data.H);
                            mData.setG(mPK20Data.G);
                            mData.setV(mPK20Data.V);
                            mData.setTime(mPK20Data.time);
                            mData.setBarCode(mPK20Data.barCode);
                            mData.setZhu(mPK20Data.zhu);
                            mData.setZi(mPK20Data.zi);
                            mData.setBiaoJi(mPK20Data.biaoJi);
                            mData.setBiaoshi(mPK20Data.biaoshi);
                            mData.setMac(mPK20Data.mac);
                            mData.setName(mPK20Data.name);
                            MyApp.getDaoInstant().getDataDao().insertOrReplace(mData);
                            EventBus.getDefault().post(new MsgEvent("Save6DataSuccess", "数据存储成功"));
                        } else {
                            EventBus.getDefault().post(new MsgEvent("Save6DataErr", dataERR));
                        }
                    }

                }
            }
        }
    };


}
