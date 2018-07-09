package speedata.com.blelib.utils;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;

import speedata.com.blelib.base.BaseBleApplication;
import speedata.com.blelib.service.BluetoothLeService;

public class PK30DataUtils {

    /**
     * 检验数据是否符合规定
     *
     * @param bytes 数据
     * @return boolean
     */
    public static boolean checkData(byte[] bytes) {
        if ((byte) 0xAA != bytes[0]) {
            return false;
        }
        int length = bytes.length;
        if ((byte) 0x00 != bytes[length - 1]) {
            return false;
        }

        int result = 0;
        for (int i = 0; i < length - 2; i++) {
            result = result + bytes[i];
        }
        return getProof(result, bytes[length - 2]);
    }

    /**
     * 解析数据并使用广播发送出去
     *
     * @param bytes 数据
     */
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    public static void analysisData(Context context, Intent intent, byte[] bytes) {
        switch (bytes[1]) {
            case 0x0A:
                sendBroadcast(2, context, intent, bytes, BluetoothLeService.NOTIFICATION_DATA_L, "ffff");
                BaseBleApplication.writeCharacteristic3(new byte[]{(byte) 0xAA, (byte) 0x8A, 0x01, 0x35, 0x00});
                break;
            case 0x0B:
                sendBroadcast(2, context, intent, bytes, BluetoothLeService.NOTIFICATION_DATA_W, "ffff");
                BaseBleApplication.writeCharacteristic3(new byte[]{(byte) 0xAA, (byte) 0x8B, 0x01, 0x36, 0x00});
                break;
            case 0x0C:
                sendBroadcast(2, context, intent, bytes, BluetoothLeService.NOTIFICATION_DATA_H, "ffff");
                BaseBleApplication.writeCharacteristic3(new byte[]{(byte) 0xAA, (byte) 0x8C, 0x01, 0x37, 0x00});
                break;
            case 0x0D:
                sendBroadcast(4, context, intent, bytes, BluetoothLeService.NOTIFICATION_DATA_G, "ffffffff");
                BaseBleApplication.writeCharacteristic3(new byte[]{(byte) 0xAA, (byte) 0x8D, 0x01, 0x38, 0x00});
                break;
            default:
                break;
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    public static void replyError(byte[] bytes) {
        switch (bytes[1]) {
            case 0x0A:
                BaseBleApplication.writeCharacteristic3(new byte[]{(byte) 0xFF, (byte) 0x8A, 0x01, (byte) 0xE5, 0x00});
                break;
            case 0x0B:
                BaseBleApplication.writeCharacteristic3(new byte[]{(byte) 0xFF, (byte) 0x8B, 0x01, (byte) 0xE6, 0x00});
                break;
            case 0x0C:
                BaseBleApplication.writeCharacteristic3(new byte[]{(byte) 0xFF, (byte) 0x8C, 0x01, (byte) 0xE7, 0x00});
                break;
            case 0x0D:
                BaseBleApplication.writeCharacteristic3(new byte[]{(byte) 0xFF, (byte) 0x8D, 0x01, (byte) 0xE8, 0x00});
                break;
            default:
                break;
        }
    }

    /**
     * 获取软件版本
     */
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    public static void getSoftware() {
        BaseBleApplication.writeCharacteristic3(new byte[]{(byte) 0xAA, 0x52, 0x01, (byte) 0xFD, 0x00});
    }

    /**
     * 解析软件版本数据
     *
     * @param context
     * @param intent
     * @param bytes
     * @return
     */
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    public static void analysisSoftware(Context context, Intent intent, byte[] bytes) {
        int length = bytes.length - 5;
        byte[] result = new byte[length];
        try {
            System.arraycopy(bytes, 3, result, 0, length);
        } catch (Exception e) {
            e.printStackTrace();
        }
        String string = DataManageUtils.toAsciiString(result);
        intent.putExtra(BluetoothLeService.NOTIFICATION_DATA_SOFT, string);
        context.sendBroadcast(intent);
    }

    /**
     * 获取硬件版本
     */
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    public static void getHardware() {
        BaseBleApplication.writeCharacteristic3(new byte[]{(byte) 0xAA, 0x53, 0x01, (byte) 0xFE, 0x00});
    }

    /**
     * 解析硬件版本数据
     *
     * @param context
     * @param intent
     * @param bytes
     * @return
     */
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    public static void analysisHardware(Context context, Intent intent, byte[] bytes) {
        int length = bytes.length - 5;
        byte[] result = new byte[length];
        try {
            System.arraycopy(bytes, 3, result, 0, length);
        } catch (Exception e) {
            e.printStackTrace();
        }
        String string = DataManageUtils.toAsciiString(result);
        intent.putExtra(BluetoothLeService.NOTIFICATION_DATA_HARD, string);
        context.sendBroadcast(intent);
    }

    /**
     * 获取MAC
     */
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    public static void getMac() {
        BaseBleApplication.writeCharacteristic3(new byte[]{(byte) 0xAA, 0x51, 0x01, (byte) 0xFC, 0x00});
    }

    /**
     * 解析mac数据
     *
     * @param context
     * @param intent
     * @param bytes
     * @return
     */
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    public static void analysisMac(Context context, Intent intent, byte[] bytes) {
        int length = bytes.length - 5;
        byte[] result = new byte[length];
        try {
            System.arraycopy(bytes, 3, result, 0, length);
        } catch (Exception e) {
            e.printStackTrace();
        }
        String mac = DataManageUtils.toAsciiString(result);
        intent.putExtra(BluetoothLeService.NOTIFICATION_DATA_MAC, mac);
        context.sendBroadcast(intent);
    }


    /**
     * 设置测量模式
     */
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    public static void setModel(int model) {
        switch (model) {
            case 0:
                BaseBleApplication.writeCharacteristic3(new byte[]{(byte) 0xAA, 0x54, 0x01, (byte) 0xFF, 0x00});
                break;
            case 1:
                BaseBleApplication.writeCharacteristic3(new byte[]{(byte) 0xAA, 0x55, 0x01, (byte) 0x00, 0x00});
                break;
            case 2:
                BaseBleApplication.writeCharacteristic3(new byte[]{(byte) 0xAA, 0x56, 0x01, (byte) 0x01, 0x00});
                break;
            case 3:
                BaseBleApplication.writeCharacteristic3(new byte[]{(byte) 0xAA, 0x57, 0x01, (byte) 0x02, 0x00});
                break;
        }
    }

    /**
     * 解析设置模式数据
     *
     * @param context
     * @param intent
     * @param bytes
     * @return
     */
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    public static void analysisMdoel(Context context, Intent intent, byte[] bytes) {
        int length = bytes.length - 5;
        byte[] result = new byte[length];
        try {
            System.arraycopy(bytes, 3, result, 0, length);
        } catch (Exception e) {
            e.printStackTrace();
        }
        String string = DataManageUtils.bytesToHexString(result);
        intent.putExtra(BluetoothLeService.NOTIFICATION_DATA_MODEL, string);
        context.sendBroadcast(intent);
    }

    /**
     * 关机
     */
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    public static void shutdown() {
        BaseBleApplication.writeCharacteristic3(new byte[]{(byte) 0xAA, 0x58, 0x01, (byte) 0x03, 0x00});
    }

    /**
     * 解析关机数据
     *
     * @param context
     * @param intent
     * @param bytes
     * @return
     */
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    public static void analysisShutdown(Context context, Intent intent, byte[] bytes) {
        int length = bytes.length - 5;
        byte[] result = new byte[length];
        try {
            System.arraycopy(bytes, 3, result, 0, length);
        } catch (Exception e) {
            e.printStackTrace();
        }
        String string = DataManageUtils.bytesToHexString(result);
        intent.putExtra(BluetoothLeService.NOTIFICATION_SHUTDOWN, string);
        context.sendBroadcast(intent);
    }

    /**
     * 蜂鸣器
     */
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    public static void fengMing(int time) {
        String timeHex = DataManageUtils.IntToHex(time);
        String jiaoYan = DataManageUtils.getJiaoYan("08", timeHex);
        byte[] jiaoyanBytes = StringUtils.hexStringToByteArray(jiaoYan);
        byte[] concatAll = ByteUtils.concatAll(new byte[]{(byte) 0xAA, 0x59, 0x04, (byte) 0x03, 0x01, 0x00}, jiaoyanBytes, new byte[]{0x00});
        BaseBleApplication.writeCharacteristic3(concatAll);
    }

    /**
     * 解析蜂鸣器数据
     *
     * @param context
     * @param intent
     * @param bytes
     * @return
     */
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    public static void analysisFengMing(Context context, Intent intent, byte[] bytes) {
        int length = bytes.length - 7;
        byte[] result = new byte[length];
        try {
            System.arraycopy(bytes, 3, result, 0, length);
        } catch (Exception e) {
            e.printStackTrace();
        }
        String string = DataManageUtils.bytesToHexString(result);
        intent.putExtra(BluetoothLeService.NOTIFICATION_FENGMING, string);
        context.sendBroadcast(intent);
    }

    /**
     * 数据发出
     *
     * @param length     数据截取长度
     * @param context    context
     * @param intent     intent
     * @param bytes      数据
     * @param intentName intentName
     */
    private static void sendBroadcast(int length, Context context, Intent intent, byte[] bytes, String intentName, String errStr) {
        String data = "";
        byte[] result = new byte[length];
        try {
            System.arraycopy(bytes, 3, result, 0, length);
        } catch (Exception e) {
            e.printStackTrace();
        }
        String byteArrayToString = DataManageUtils.byteArrayToString(result);
        int l = 0;
        if (!errStr.equals(byteArrayToString)) {
            l = Integer.parseInt(byteArrayToString, 16);
            double resultDouble = (double) l / 10;
            data = resultDouble + "";
        }
        intent.putExtra(intentName, data);
        context.sendBroadcast(intent);
    }

    /**
     * 校验位检验
     *
     * @param result 报头一直加到校验位前
     * @param proof  校验位
     * @return boolean
     */
    private static boolean getProof(int result, byte proof) {
        String toHexString = Integer.toHexString(result).toUpperCase();
        if (toHexString.length() == 1) {
            toHexString = "0" + toHexString;
        }
        if (toHexString.length() > 2) {
            toHexString = toHexString.substring(toHexString.length() - 2, toHexString.length());
        }
        byte[] hexString2Bytes = DataManageUtils.HexString2Bytes(toHexString);
        return hexString2Bytes[hexString2Bytes.length - 1] == proof;
    }
}
