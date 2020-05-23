package icu.shaoyayu.android.baidumap.activity;

import android.util.Log;

import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.route.BikingRouteResult;
import com.baidu.mapapi.search.route.DrivingRouteResult;
import com.baidu.mapapi.search.route.IndoorRouteResult;
import com.baidu.mapapi.search.route.MassTransitRouteResult;
import com.baidu.mapapi.search.route.PlanNode;
import com.baidu.mapapi.search.route.TransitRouteLine;
import com.baidu.mapapi.search.route.TransitRoutePlanOption;
import com.baidu.mapapi.search.route.TransitRouteResult;
import com.baidu.mapapi.search.route.WalkingRouteResult;

import java.util.List;

import icu.shaoyayu.android.baidumap.utils.TransitRouteOverlay;

/**
 * @author shaoyayu
 * 换成路线搜索
 */
public class TransitSearchActivity extends RoutePlanSearchBase {

    private static final String TAG = "TransitSearchActivity";

    /**
     * 初始化搜索路线
     */
    @Override
    protected void routePlanSearchInit() {

        routePlanSearch.transitSearch(getTransitRoutePlanOption());

    }

    /**
     * 初始化一个换成搜索
     * @return
     */
    private TransitRoutePlanOption getTransitRoutePlanOption() {
        TransitRoutePlanOption transitRoutePlanOption = new TransitRoutePlanOption();
        transitRoutePlanOption.city("贵阳");
        //金阳客车站 坐标：106.586441,26.602937
        transitRoutePlanOption.from(PlanNode.withLocation(new LatLng(26.602937,106.586441)));
        //贵州民族大学 106.676709,26.470687
        transitRoutePlanOption.to(PlanNode.withLocation(new LatLng(26.470687,106.676709)));
        //路线选型
        /*
        EBUS_NO_SUBWAY
            公交检索策略常量：不含地铁
            EBUS_TIME_FIRST
            公交检索策略常量：时间优先
            EBUS_TRANSFER_FIRST
            公交检索策略常量：最少换乘
            EBUS_WALK_FIRST
            公交检索策略常量：最少步行距离
         */
        transitRoutePlanOption.policy(TransitRoutePlanOption.TransitPolicy.EBUS_WALK_FIRST);
        return transitRoutePlanOption;
    }

    /**
     * 换乘路线结果回调
     * @param transitRouteResult
     */
    @Override
    public void onGetTransitRouteResult(TransitRouteResult transitRouteResult) {
        TransitRouteOverlay overlay = new TransitRouteOverlay(baiduMap);
        baiduMap.setOnMarkerClickListener(overlay);
        //取出查询出来的所有路线，选择第一条
        List<TransitRouteLine> routeLines = transitRouteResult.getRouteLines();
        Log.i(TAG,"===>>>Total number of routes:"+routeLines.size());
        overlay.setData(routeLines.get(0));
        //把搜索结果添加到百度地图里面
        overlay.addToMap();
        //把搜索结果显示在同一个屏幕
        overlay.zoomToSpan();
    }



    /**
     * 驾车路线结果回调
     * @param drivingRouteResult
     */
    @Override
    public void onGetDrivingRouteResult(DrivingRouteResult drivingRouteResult) {

    }

    /**
     * 步行路线结果回调
     * @param walkingRouteResult
     */
    @Override
    public void onGetWalkingRouteResult(WalkingRouteResult walkingRouteResult) {

    }


    /**
     * 跨城公共交通路线结果回调 * @param result 跨城公共交通路线结果
     * @param massTransitRouteResult
     */
    @Override
    public void onGetMassTransitRouteResult(MassTransitRouteResult massTransitRouteResult) {

    }



    /**
     * 室内路线规划回调
     * @param indoorRouteResult
     */
    @Override
    public void onGetIndoorRouteResult(IndoorRouteResult indoorRouteResult) {

    }

    /**
     * 骑行路线结果回调
     * @param bikingRouteResult
     */
    @Override
    public void onGetBikingRouteResult(BikingRouteResult bikingRouteResult) {

    }

}
