package icu.shaoyayu.android.baidumap.activity;

import android.view.KeyEvent;

import com.baidu.mapapi.map.BaiduMap;

/**
 * @author shaoyayu
 * 切换不同的涂层地图
 */
public class MapLayerActivity extends BaiDuMapBaseActivity {
    @Override
    public void init() {

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode){
            case KeyEvent.KEYCODE_1:
                //显示普通的地图
                baiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
                //关闭交通地图
                baiduMap.setTrafficEnabled(false);
                break;
            case KeyEvent.KEYCODE_2:
                //显示卫星地图
                baiduMap.setMapType(BaiduMap.MAP_TYPE_SATELLITE);
                //关闭交通地图
                baiduMap.setTrafficEnabled(false);
                break;
            case KeyEvent.KEYCODE_3:
                //显示交通地图
                baiduMap.setTrafficEnabled(true);
                break;
            case KeyEvent.KEYCODE_4:
                baiduMap.setBaiduHeatMapEnabled(true);
                break;
            default:
                showToast("此选项无指令");
                break;
        }
        return super.onKeyDown(keyCode,event);
    }
}
