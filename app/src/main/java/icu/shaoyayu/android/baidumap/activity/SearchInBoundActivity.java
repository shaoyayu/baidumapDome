package icu.shaoyayu.android.baidumap.activity;

import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.model.LatLngBounds;
import com.baidu.mapapi.search.poi.PoiBoundSearchOption;

import java.util.ArrayList;
import java.util.List;

/**
 * @author shaoyayu
 * 范围内检索 V4.3.2版本后（包含V4.3.2版本），该方法要在Listener设置方法
 * setOnGetPoiSearchResultListener(OnGetPoiSearchResultListener) 之后执行，
 * 否则会在某些场景出现拿不到回调结果的情况
 */
public class SearchInBoundActivity extends PoiSearchBaseActivity  {

    private static final String TAG = "SearchInBoundActivity";


    @Override
    protected void poiSearchInit() {
        poiSearch.searchInBound(getSearchOption());
    }

    private PoiBoundSearchOption getSearchOption() {
        //创建一个查找参数
        PoiBoundSearchOption poiBoundSearchOption = new PoiBoundSearchOption();
        //112.126482,30.334096 西南
        //112.250304,30.384205 东北
        LatLng latLng1 = new LatLng(30.334096,112.126482);
        LatLng latLng2 = new LatLng(30.384205,112.250304);
        List<LatLng> latLngs = new ArrayList<>();
        //指定搜索范围
        LatLngBounds bounds = new LatLngBounds.Builder()
                //设置内容
                .include(latLng1)
                .include(latLng2)
                .build();
        //设置搜索范围
        poiBoundSearchOption.bound(bounds);
        //设置搜索内容
        poiBoundSearchOption.keyword("学校");
        return poiBoundSearchOption;
    }

    /**
     * <p>文档简介</p>
     * Modifier and Type	Method and Description
     * void	destroy()
     * 释放检索对象
     * static PoiSearch	newInstance()
     * 创建PoiSearch实例
     * boolean	searchInBound(PoiBoundSearchOption option)
     * 范围内检索 V4.3.2版本后（包含V4.3.2版本），该方法要在Listener设置方法 setOnGetPoiSearchResultListener(OnGetPoiSearchResultListener)
     * 之后执行，否则会在某些场景出现拿不到回调结果的情况
     * boolean	searchInCity(PoiCitySearchOption option)
     * 城市内检索 V4.3.2版本后（包含V4.3.2版本），该方法要在Listener设置方法 setOnGetPoiSearchResultListener(OnGetPoiSearchResultListener)
     * 之后执行，否则会在某些场景出现拿不到回调结果的情况
     * boolean	searchNearby(PoiNearbySearchOption option)
     * 周边检索 V4.3.2版本后（包含V4.3.2版本），该方法要在Listener设置方法 setOnGetPoiSearchResultListener(OnGetPoiSearchResultListener)
     * 之后执行，否则会在某些场景出现拿不到回调结果的情况
     * boolean	searchPoiDetail(PoiDetailSearchOption option)
     * POI 详情检索 V4.3.2版本后（包含V4.3.2版本），该方法要在Listener设置方法 setOnGetPoiSearchResultListener(OnGetPoiSearchResultListener)
     * 之后执行，否则会在某些场景出现拿不到回调结果的情况
     * boolean	searchPoiIndoor(PoiIndoorOption option)
     * POI 室内检索 V4.3.2版本后（包含V4.3.2版本），该方法要在Listener设置方法 setOnGetPoiSearchResultListener(OnGetPoiSearchResultListener)
     * 之后执行，否则会在某些场景出现拿不到回调结果的情况
     * void	setOnGetPoiSearchResultListener(OnGetPoiSearchResultListener listener)
     * 设置poi检索监听者 V4.3.2版本之后（包含V4.3.2版本），该方法要先于检索方法searchInBound(PoiBoundSearchOption) searchNearby(PoiNearbySearchOption)
     * searchInCity(PoiCitySearchOption) searchPoiDetail(PoiDetailSearchOption) searchPoiIndoor(PoiIndoorOption) 调用，否则会在某些场景出现拿不到回调结果的情况
     */
}
