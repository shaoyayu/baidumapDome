package icu.shaoyayu.android.baidumap.activity;

import android.util.Log;

import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.route.BikingRouteResult;
import com.baidu.mapapi.search.route.DrivingRouteLine;
import com.baidu.mapapi.search.route.DrivingRoutePlanOption;
import com.baidu.mapapi.search.route.DrivingRouteResult;
import com.baidu.mapapi.search.route.IndoorRouteResult;
import com.baidu.mapapi.search.route.MassTransitRouteResult;
import com.baidu.mapapi.search.route.PlanNode;
import com.baidu.mapapi.search.route.TransitRouteResult;
import com.baidu.mapapi.search.route.WalkingRouteResult;

import java.util.List;

import icu.shaoyayu.android.baidumap.utils.DrivingRouteOverlay;

/**
 * @author shaoyayu
 * 驾车路线规划
 */
public class DrivingSearchActivity extends RoutePlanSearchBase {

    private static final String TAG = "DrivingSearchActivity";

    /**
     * 初始化搜索路线
     */
    @Override
    protected void routePlanSearchInit() {

        routePlanSearch.drivingSearch(getDrivingRoutePlanOption());

    }

    /**
     * 初始化驾车的路线
     * @return
     */
    private DrivingRoutePlanOption getDrivingRoutePlanOption() {
        DrivingRoutePlanOption drivingRoutePlanOption = new DrivingRoutePlanOption();
        //使用一个位置,西校区，到东校区
        drivingRoutePlanOption.from(PlanNode.withLocation(new LatLng(30.35645,112.158437)));
        //到达西校区
        drivingRoutePlanOption.to(PlanNode.withLocation(new LatLng(30.355534,112.193069)));
        return drivingRoutePlanOption;
    }


    /**
     * 驾车路线结果回调
     * @param drivingRouteResult
     */
    @Override
    public void onGetDrivingRouteResult(DrivingRouteResult drivingRouteResult) {
        DrivingRouteOverlay overlay = new DrivingRouteOverlay(baiduMap);
        baiduMap.setOnMarkerClickListener(overlay);
        //取出查询出来的所有路线，选择第一条
        List<DrivingRouteLine> drivingRouteLines = drivingRouteResult.getRouteLines();
        Log.i(TAG,"===>>>Total number of routes:"+drivingRouteLines.size());
        overlay.setData(drivingRouteLines.get(0));
        //把搜索结果添加到百度地图里面
        overlay.addToMap();
        //把搜索结果显示在同一个屏幕
        overlay.zoomToSpan();
    }

    /**
     * 步行路线结果回调
     * @param walkingRouteResult
     */
    @Override
    public void onGetWalkingRouteResult(WalkingRouteResult walkingRouteResult) {

    }

    /**
     * 换乘路线结果回调
     * @param transitRouteResult
     */
    @Override
    public void onGetTransitRouteResult(TransitRouteResult transitRouteResult) {

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
