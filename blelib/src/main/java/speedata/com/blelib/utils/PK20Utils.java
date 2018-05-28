package speedata.com.blelib.utils;

import android.content.Context;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by 张明_ on 2017/9/4.
 */

public class PK20Utils {

    /**
     * 获取设置时间的发送数据
     *
     * @param currentTimeMillis 时间
     * @return 设置时间的发送数据
     */
    public static String getSetTimeData(long currentTimeMillis) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss E");
        Date date = new Date(currentTimeMillis);
        String format = simpleDateFormat.format(date);
        String[] split = format.split(" ");
        String[] spHMS = split[1].split(":");
        String[] spYMD = split[0].split("-");
        String week = DataManageUtils.backWeek(split[2]);
        String year = spYMD[0].substring(2, 4);
        String jiaoYan = DataManageUtils.getJiaoYan(spHMS[2], year);
        return "FF0C08" + spHMS[2] + spHMS[1] + spHMS[0] + week + spYMD[2]
                + spYMD[1] + year + "0000000000000000" + jiaoYan + "00";
    }

    /**
     * 检测设置时间返回的数据是否正确
     *
     * @param data 设置时间返回的数据
     * @return 0为正确 -1数据格式不正确 -2重发指令 -3部分重发
     */
    public static int checkSetTimeBackData(String data) {
        return DataManageUtils.jiaoYanData(data, "FF", "0C");
    }

    /**
     * 获取设置比例的发送数据
     *
     * @param ratio int
     * @return 设置比例的发送数据
     */
    public static String getSetRatioData(int ratio) {
        if (ratio > 60000) {
            return null;
        }
        int high = ratio >> 8 & 0xff;
        int low = ratio >> 0 & 0xff;
        String highStr = DataManageUtils.toHexString(high);
        String lowStr = DataManageUtils.toHexString(low);
        String jiaoYan = DataManageUtils.getJiaoYan(highStr, lowStr);
        return "FF0D03" + highStr + lowStr +
                "00000000000000000000000000" + jiaoYan + "00";
    }

    /**
     * 检测设置比例返回的数据是否正确
     *
     * @param data 设置比例返回的数据
     * @return 0为正确 -1数据格式不正确 -2重发指令 -3部分重发
     */
    public static int checkSetRatioBackData(String data) {
        return DataManageUtils.jiaoYanData(data, "FF", "0D");
    }

    /**
     * 检测设置字库返回的数据是否正确
     *
     * @param data 设置字库返回的数据
     * @return 0为正确 -1数据格式不正确 -2重发指令 -4重发B1部分 -5重发B2部分
     */
    public static int checkSetZiKuBackData(String data) {
        int jiaoYanData = DataManageUtils.jiaoYanData(data, "FF", "0B");
        if (jiaoYanData == -1 || jiaoYanData == -2) {
            return jiaoYanData;
        } else if (jiaoYanData == -3) {
            String[] splitData = data.split(" ");
            if (splitData[3].equals("B1")) {
                return -4;
            } else if (splitData[3].equals("B2")) {
                return -5;
            }
            return -1;
        } else {
            return 0;
        }
    }

    /**
     * 获取清除流程发送数据
     *
     * @return String
     */
    public static String getCleanData() {
        return "FF0E010000000000000000000000000000000000";
    }

    /**
     * 检测清除流程返回的数据是否正确
     *
     * @param data 清除流程返回的数据
     * @return 0为正确 -1数据格式不正确 -2重发指令 -3部分重发
     */
    public static int checkCleanBackData(String data) {
        return DataManageUtils.jiaoYanData(data, "FF", "0E");
    }


    /**
     * 检测设置公司名返回的数据是否正确
     *
     * @param data 设置公司名返回的数据
     * @return 0为正确 -1数据格式不正确 -2重发指令 -4重发B1部分 -5重发B2部分
     */
    public static int checkSetLOGOBackData(String data) {
        int jiaoYanData = DataManageUtils.jiaoYanData(data, "FF", "10");
        if (jiaoYanData == -1 || jiaoYanData == -2) {
            return jiaoYanData;
        } else if (jiaoYanData == -3) {
            String[] splitData = data.split(" ");
            if (splitData[3].equals("B1")) {
                return -4;
            } else if (splitData[3].equals("B2")) {
                return -5;
            }
            return -1;
        } else {
            return 0;
        }
    }

    /**
     * 检测设置公司名返回的数据是否正确
     *
     * @param data 设置公司名返回的数据
     * @return 0为正确 -1数据格式不正确 -2重发指令 -4重发B1部分 -5重发B2部分
     */
    public static int checkSetNameBackData(String data) {
        int jiaoYanData = DataManageUtils.jiaoYanData(data, "FF", "13");
        if (jiaoYanData == -1 || jiaoYanData == -2) {
            return jiaoYanData;
        } else if (jiaoYanData == -3) {
            String[] splitData = data.split(" ");
            if (splitData[3].equals("B1")) {
                return -4;
            } else if (splitData[3].equals("B2")) {
                return -5;
            }
            return -1;
        } else {
            return 0;
        }
    }


    /**
     * 获取清除FLASH发送数据
     *
     * @return String
     */
    public static String getCleanFlashData() {
        return "FF11010000000000000000000000000000000000";
    }

    /**
     * 检测清除FLASH返回的数据是否正确
     *
     * @param data 清除FLASH返回的数据
     * @return 0为正确 -1数据格式不正确 -2重发指令 -3部分重发
     */
    public static int checkCleanFlashBackData(String data) {
        return DataManageUtils.jiaoYanData(data, "FF", "11");
    }


    /**
     * 获取设置最小比例的发送数据
     *
     * @param ratio int
     * @return 设置最小比例的发送数据
     */
    public static String getSetLeastRatioData(int ratio) {
        if (ratio > 100) {
            return null;
        }
        int high = ratio >> 8 & 0xff;
        int low = ratio >> 0 & 0xff;
        String highStr = DataManageUtils.toHexString(high);
        String lowStr = DataManageUtils.toHexString(low);
        String jiaoYan = DataManageUtils.getJiaoYan(highStr, lowStr);
        return "FF1203" + highStr + lowStr +
                "00000000000000000000000000" + jiaoYan + "00";
    }

    /**
     * 检测设置最小比例返回的数据是否正确
     *
     * @param data 设置最小比例返回的数据
     * @return 0为正确 -1数据格式不正确 -2重发指令 -3部分重发
     */
    public static int checkSetLeastRatioBackData(String data) {
        return DataManageUtils.jiaoYanData(data, "FF", "12");
    }

    /**
     * 获取发送操作员姓名数据
     *
     * @param name
     * @return
     */
    public static List<String> getNameSetData(String name) {
        List<String> list = new ArrayList<>();
        try {
            byte[] gb18030s = name.getBytes("gb18030");
            String hexString = ByteUtils.toHexString(gb18030s);
            if (hexString != null) {
                int dataCount = hexString.length() / 2 + 1;
                String dataCountStr = DataManageUtils.toHexString(dataCount);
                //1、第一条指令
                StringBuilder data1 = new StringBuilder();
                data1.append("FF1301B1").append(dataCountStr);
                StringBuilder hexBuilder = new StringBuilder();
                hexBuilder.append(hexString);
                for (int i = 0; i < 32 - hexString.length(); i++) {
                    hexBuilder.append("0");
                }
                String oneStr = hexBuilder.substring(0, 28);
                data1.append(oneStr);
                data1.append("00");
                list.add(String.valueOf(data1));
                //2
                StringBuilder data2 = new StringBuilder();
                String twoStr = hexBuilder.substring(28, hexBuilder.length());
                String jiaoYan = DataManageUtils.getJiaoYan(hexString.substring(0, 2)
                        , hexString.substring(hexString.length() - 2, hexString.length()));
                data2.append("B2").append(twoStr).append("000000000000000000000000000000").append(jiaoYan).append("00");
                list.add(String.valueOf(data2));
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return list;
    }

    /**
     * 获取名字点阵数据
     *
     * @param context
     * @param string
     * @return
     */
    public static List<String> getNameDianZhen(Context context, String string) {
        List<String> list = new ArrayList<>();
        for (int i = 0; i < string.length(); i++) {
            String substring = string.substring(i, i + 1);
            try {
                byte[] gb18030s = substring.getBytes("gb18030");
                int toInt = ByteUtils.toInt(gb18030s);
                int offset = DataManageUtils.getOffset(toInt);
                byte[] chineseBytes = DataManageUtils.getChineseBytes(context, offset);
                String chineseHex = ByteUtils.toHexString(chineseBytes);
                //1
                StringBuilder data1 = new StringBuilder();
                data1.append("FF13").append(DataManageUtils.toHexString(i + 2)).append("B1");
                String one = chineseHex.substring(0, 30);
                data1.append(one).append("00");
                list.add(String.valueOf(data1));
                //2
                StringBuilder data2 = new StringBuilder();
                String two = chineseHex.substring(30, chineseHex.length());
                String jiaoYan = DataManageUtils.getJiaoYan(chineseHex.substring(0, 2)
                        , chineseHex.substring(chineseHex.length() - 2, chineseHex.length()));
                data2.append("B2").append(two).append(jiaoYan).append("00");
                list.add(String.valueOf(data2));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        if (string.length() < 4) {
            for (int i = string.length(); i < 4; i++) {
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("FF13").append(DataManageUtils.toHexString(i + 2))
                        .append("B1").append("00000000000000000000000000000000");
                list.add(String.valueOf(stringBuilder));
                list.add("B200000000000000000000000000000000000000");
            }
        }
        return list;
    }


    /**
     * 获取LOGO点阵数据
     *
     * @param context
     * @param string
     * @return
     */
    public static List<String> getLOGOData(Context context, String string) {
        List<String> list = new ArrayList<>();
        for (int i = 0; i < string.length(); i++) {
            String substring = string.substring(i, i + 1);
            try {
                byte[] gb18030s = substring.getBytes("gb18030");
                int toInt = ByteUtils.toInt(gb18030s);
                int offset = DataManageUtils.getOffset(toInt);
                byte[] chineseBytes = DataManageUtils.getChineseBytes(context, offset);
                String chineseHex = ByteUtils.toHexString(chineseBytes);
                //1
                StringBuilder data1 = new StringBuilder();
                data1.append("FF10").append(DataManageUtils.toHexString(i + 1)).append("B1");
                String one = chineseHex.substring(0, 30);
                data1.append(one).append("00");
                list.add(String.valueOf(data1));
                //2
                StringBuilder data2 = new StringBuilder();
                String two = chineseHex.substring(30, chineseHex.length());
                String jiaoYan = DataManageUtils.getJiaoYan(chineseHex.substring(0, 2)
                        , chineseHex.substring(chineseHex.length() - 2, chineseHex.length()));
                data2.append("B2").append(two).append(jiaoYan).append("00");
                list.add(String.valueOf(data2));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        return list;
    }
}
