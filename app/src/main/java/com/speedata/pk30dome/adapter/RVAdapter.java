package com.speedata.pk30dome.adapter;

import android.content.Context;
import android.text.TextUtils;
import com.speedata.pk30dome.R;
import com.speedata.pk30dome.bean.Data;

import java.util.List;
import xyz.reginer.baseadapter.BaseAdapterHelper;
import xyz.reginer.baseadapter.CommonRvAdapter;

/**
 * Created by 张明_ on 2017/7/14.
 */

public class RVAdapter extends CommonRvAdapter<Data> {

    public RVAdapter(Context context, int layoutResId, List data) {
        super(context, layoutResId, data);
    }


    @Override
    public void convert(BaseAdapterHelper helper, Data item, int position) {
        helper.setText(R.id.tv_barCode, item.getBarCode());
        setUi(helper, item.getWangDian(), R.id.tv_wangdian, R.id.ll_wangdian);
        setUi(helper, item.getZhu(), R.id.tv_zhu, R.id.ll_zhu);
        setUi(helper, item.getZi(), R.id.tv_zi, R.id.ll_zi);
        setUi(helper, item.getCenter(), R.id.tv_center, R.id.ll_center);
        setUi(helper, item.getMuDi(), R.id.tv_mudi, R.id.ll_mudi);
        setUi(helper, item.getTime(), R.id.tv_time, R.id.ll_time);
        setUi(helper, item.getL(), R.id.tv_l, R.id.ll_l);
        setUi(helper, item.getW(), R.id.tv_w, R.id.ll_w);
        setUi(helper, item.getH(), R.id.tv_h, R.id.ll_h);
        setUi(helper, item.getV(), R.id.tv_v, R.id.ll_v);
        setUi(helper, item.getBiaoshi(), R.id.tv_biaoshi, R.id.ll_biaoshi);
        setUi(helper, item.getMac(), R.id.tv_mac, R.id.ll_mac);
        setUi(helper, item.getName(), R.id.tv_name, R.id.ll_name);
    }

    private void setUi(BaseAdapterHelper helper, String wangDian, int viewId, int ll) {
        wangDian = wangDian.replace("\u0000", "");
        if (TextUtils.isEmpty(wangDian) || wangDian.equals("")) {
            helper.setVisible(ll, false);
        } else {
            helper.setVisible(ll, true);
            helper.setText(viewId, wangDian);
        }
    }
}
