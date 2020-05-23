package icu.shaoyayu.android.baidumap.activity;

import com.baidu.mapapi.search.route.OnGetRoutePlanResultListener;
import com.baidu.mapapi.search.route.RoutePlanSearch;

/**
 * @author shaoyayu
 *
 * 路线规划的一个父类
 */
public abstract class RoutePlanSearchBase extends BaiDuMapBaseActivity implements OnGetRoutePlanResultListener {
    protected RoutePlanSearch routePlanSearch;
    @Override
    public final void init() {
        routePlanSearch = RoutePlanSearch.newInstance();
        routePlanSearch.setOnGetRoutePlanResultListener(this);
        routePlanSearchInit();
    }

    protected abstract void routePlanSearchInit();
}
