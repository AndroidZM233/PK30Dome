package com.speedata.pk30dome.ui.menu;


import com.speedata.pk30dome.mvp.BasePresenter;
import com.speedata.pk30dome.mvp.BaseView;

/**
 * MVPPlugin
 *  邮箱 784787081@qq.com
 */

public class MenuContract {
    interface View extends BaseView {
        
    }

    interface  Presenter extends BasePresenter<View> {
        void toggleOnCheckedChangeListener(boolean isCheck);
    }
}
