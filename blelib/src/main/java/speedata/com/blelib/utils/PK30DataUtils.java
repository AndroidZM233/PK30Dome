package speedata.com.blelib.utils;

import android.content.Context;
import android.content.Intent;

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
    public static void analysisData(Context context, Intent intent, byte[] bytes) {
        switch (bytes[1]) {
            case 0x0A:
                sendBroadcast(2, context, intent, bytes, BluetoothLeService.NOTIFICATION_DATA_L, "ffff");
                break;
            case 0x0B:
                sendBroadcast(2, context, intent, bytes, BluetoothLeService.NOTIFICATION_DATA_W, "ffff");
                break;
            case 0x0C:
                sendBroadcast(2, context, intent, bytes, BluetoothLeService.NOTIFICATION_DATA_H, "ffff");
                break;
            case 0x0D:
                sendBroadcast(4, context, intent, bytes, BluetoothLeService.NOTIFICATION_DATA_G, "ffffffff");
                break;
            default:
                break;
        }
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
        return hexString2Bytes[0] == proof;
    }
}
