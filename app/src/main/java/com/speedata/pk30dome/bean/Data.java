package com.speedata.pk30dome.bean;

import org.greenrobot.greendao.annotation.Entity;

import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Created by 张明_ on 2017/7/19.
 */
@Entity
public class Data {
    @Id(autoincrement = false)
    private String barCode;

    private String wangDian;
    private String center;
    private String muDi;
    private String liuCheng;
    private String L;
    private String W;
    private String H;
    private String V;
    private String G;
    private String time;
    private String zhu;
    private String zi;
    private String biaoJi;
    private String biaoshi;
    private String mac;
    private String name;
    @Generated(hash = 1865355700)
    public Data(String barCode, String wangDian, String center, String muDi,
            String liuCheng, String L, String W, String H, String V, String G,
            String time, String zhu, String zi, String biaoJi, String biaoshi,
            String mac, String name) {
        this.barCode = barCode;
        this.wangDian = wangDian;
        this.center = center;
        this.muDi = muDi;
        this.liuCheng = liuCheng;
        this.L = L;
        this.W = W;
        this.H = H;
        this.V = V;
        this.G = G;
        this.time = time;
        this.zhu = zhu;
        this.zi = zi;
        this.biaoJi = biaoJi;
        this.biaoshi = biaoshi;
        this.mac = mac;
        this.name = name;
    }
    @Generated(hash = 2135787902)
    public Data() {
    }
    public String getBarCode() {
        return this.barCode;
    }
    public void setBarCode(String barCode) {
        this.barCode = barCode;
    }
    public String getWangDian() {
        return this.wangDian;
    }
    public void setWangDian(String wangDian) {
        this.wangDian = wangDian;
    }
    public String getCenter() {
        return this.center;
    }
    public void setCenter(String center) {
        this.center = center;
    }
    public String getMuDi() {
        return this.muDi;
    }
    public void setMuDi(String muDi) {
        this.muDi = muDi;
    }
    public String getLiuCheng() {
        return this.liuCheng;
    }
    public void setLiuCheng(String liuCheng) {
        this.liuCheng = liuCheng;
    }
    public String getL() {
        return this.L;
    }
    public void setL(String L) {
        this.L = L;
    }
    public String getW() {
        return this.W;
    }
    public void setW(String W) {
        this.W = W;
    }
    public String getH() {
        return this.H;
    }
    public void setH(String H) {
        this.H = H;
    }
    public String getV() {
        return this.V;
    }
    public void setV(String V) {
        this.V = V;
    }
    public String getG() {
        return this.G;
    }
    public void setG(String G) {
        this.G = G;
    }
    public String getTime() {
        return this.time;
    }
    public void setTime(String time) {
        this.time = time;
    }
    public String getZhu() {
        return this.zhu;
    }
    public void setZhu(String zhu) {
        this.zhu = zhu;
    }
    public String getZi() {
        return this.zi;
    }
    public void setZi(String zi) {
        this.zi = zi;
    }
    public String getBiaoJi() {
        return this.biaoJi;
    }
    public void setBiaoJi(String biaoJi) {
        this.biaoJi = biaoJi;
    }
    public String getBiaoshi() {
        return this.biaoshi;
    }
    public void setBiaoshi(String biaoshi) {
        this.biaoshi = biaoshi;
    }
    public String getMac() {
        return this.mac;
    }
    public void setMac(String mac) {
        this.mac = mac;
    }
    public String getName() {
        return this.name;
    }
    public void setName(String name) {
        this.name = name;
    }
    

}
