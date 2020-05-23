package icu.shaoyayu.android.baidumap.activity;

import android.widget.Toast;

import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiDetailSearchResult;
import com.baidu.mapapi.search.poi.PoiIndoorResult;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiSearch;

import icu.shaoyayu.android.baidumap.utils.PoiOverlay;

/**
 * @author shaoyayu
 */
public abstract class PoiSearchBaseActivity extends BaiDuMapBaseActivity implements OnGetPoiSearchResultListener {

    protected PoiSearch poiSearch;

    @Override
    public final void init() {
        poiSearch = PoiSearch.newInstance();
        //设置一个搜索完成回调的监听器
        poiSearch.setOnGetPoiSearchResultListener(this);
        poiSearchInit();
    }

    /**
     * 初始换搜索
     */
    protected abstract void poiSearchInit();

    /**
     * 搜索的结果
     * @param poiResult
     */
    @Override
    public void onGetPoiResult(final PoiResult poiResult) {
        if (poiResult == null
                || poiResult.error == SearchResult.ERRORNO.RESULT_NOT_FOUND) {// 没有找到检索结果
            Toast.makeText(PoiSearchBaseActivity.this, "未找到结果",
                    Toast.LENGTH_LONG).show();
            return;
        }

        if (poiResult.error == SearchResult.ERRORNO.NO_ERROR) {// 检索结果正常返回
            baiduMap.clear();
            final PoiOverlay poiOverlay = new PoiOverlay(baiduMap){
                @Override
                public boolean onPoiClick(int index) {
                    return PoiSearchBaseActivity.this.onPoiClick(poiResult,index);
                }
            };
            poiOverlay.setData(poiResult);// 设置POI数据
            baiduMap.setOnMarkerClickListener(poiOverlay);
            poiOverlay.addToMap();// 将所有的overlay添加到地图上
            poiOverlay.zoomToSpan();
        }
    }

    /**
     * 抽离出来让子类可以复写
     * @param poiResult
     * @param index
     * @return
     */
    public boolean onPoiClick(PoiResult poiResult,int index) {
        PoiInfo poiInfo = poiResult.getAllPoi().get(index);
        showToast(poiInfo.name+","+poiInfo.city+","+poiInfo.area);
        return true;
    }

    @Override
    public void onGetPoiDetailResult(PoiDetailResult poiDetailResult) {

    }

    @Override
    public void onGetPoiDetailResult(PoiDetailSearchResult poiDetailSearchResult) {

    }

    @Override
    public void onGetPoiIndoorResult(PoiIndoorResult poiIndoorResult) {

    }
}
