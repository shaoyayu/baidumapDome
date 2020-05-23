package icu.shaoyayu.android.baidumap.activity;

import android.util.Log;

import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.route.BikingRouteResult;
import com.baidu.mapapi.search.route.DrivingRouteResult;
import com.baidu.mapapi.search.route.IndoorRouteResult;
import com.baidu.mapapi.search.route.MassTransitRouteResult;
import com.baidu.mapapi.search.route.PlanNode;
import com.baidu.mapapi.search.route.TransitRouteResult;
import com.baidu.mapapi.search.route.WalkingRouteLine;
import com.baidu.mapapi.search.route.WalkingRoutePlanOption;
import com.baidu.mapapi.search.route.WalkingRouteResult;

import java.util.List;

import icu.shaoyayu.android.baidumap.utils.WalkingRouteOverlay;

/**
 * @author shaoyayu
 * 步行路线规划
 */
public class WalkingSearchActivity extends RoutePlanSearchBase {

    private static final String TAG = "TransitSearchActivity";

    /**
     * 初始化搜索路线
     */
    @Override
    protected void routePlanSearchInit() {

        routePlanSearch.walkingSearch(getWalkingRoutePlanOption());

    }

    private WalkingRoutePlanOption getWalkingRoutePlanOption() {
        WalkingRoutePlanOption walkingRoutePlanOption = new WalkingRoutePlanOption();
        /*
        黔东草海风景区
        地址：贵州省铜仁市松桃苗族自治县
        坐标：109.345191,28.248327
         */
        walkingRoutePlanOption.from(PlanNode.withLocation(new LatLng(28.248327,109.345191)));
        /*
        松桃彩虹桥头加油站
        109.219478,28.167921
         */
        walkingRoutePlanOption.to(PlanNode.withLocation(new LatLng(28.167921,109.219478)));
        return walkingRoutePlanOption;
    }


    /**
     * 步行路线结果回调
     * @param walkingRouteResult
     */
    @Override
    public void onGetWalkingRouteResult(WalkingRouteResult walkingRouteResult) {
        WalkingRouteOverlay overlay = new WalkingRouteOverlay(baiduMap);
        baiduMap.setOnMarkerClickListener(overlay);
        //取出查询出来的所有路线，选择第一条
        List<WalkingRouteLine> routeLines = walkingRouteResult.getRouteLines();
        Log.i(TAG,"===>>>Total number of routes:"+routeLines.size());
        Log.i(TAG,"===>>>Route details[时间]:"+routeLines.get(0).getDuration()/60+"分钟");
        overlay.setData(routeLines.get(0));
        //把搜索结果添加到百度地图里面
        overlay.addToMap();
        //把搜索结果显示在同一个屏幕
        overlay.zoomToSpan();
    }

    /**
     * 换乘路线结果回调
     * @param transitRouteResult
     */
    @Override
    public void onGetTransitRouteResult(TransitRouteResult transitRouteResult) {

    }



    /**
     * 驾车路线结果回调
     * @param drivingRouteResult
     */
    @Override
    public void onGetDrivingRouteResult(DrivingRouteResult drivingRouteResult) {

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
