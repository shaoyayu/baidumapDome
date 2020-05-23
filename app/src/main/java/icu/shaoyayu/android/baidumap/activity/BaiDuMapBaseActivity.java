package icu.shaoyayu.android.baidumap.activity;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.model.LatLng;

import icu.shaoyayu.android.baidumap.R;

/**
 * @author shaoyayu
 * 显示百度地图的模板类
 */
public abstract class BaiDuMapBaseActivity extends Activity {

    //百度地图控制器
    protected BaiduMap baiduMap = null;
    private static final String TAG = "BaseActivity";


    protected MapView mMapView = null;



    @Override
    protected final void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mMapView = findViewById(R.id.bmapView);
        baiduMap = mMapView.getMap();
        //获取地图的最大最小缩放
        Log.i(TAG,"最大缩放："+baiduMap.getMaxZoomLevel()+",最小缩放："+baiduMap.getMinZoomLevel());
        //设置地图的中心,通过工厂进行创建,注意经纬度是反的
        MapStatusUpdate centerPoint = MapStatusUpdateFactory.newLatLng(new LatLng(30.35645,112.158437));
        baiduMap.setMapStatus(centerPoint);
        //设置一个默认的缩放
        MapStatusUpdate defaultZoom = MapStatusUpdateFactory.zoomTo(18);
        baiduMap.setMapStatus(defaultZoom);
        init();
    }

    /**
     * 子类抽象接口
     */
    public abstract void init();



    /**
     * 在屏幕中央显示Toast
     * @param text
     */
    public void showToast(CharSequence text){
        Toast toast = Toast.makeText(this,text,Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER,0,0);
        toast.show();
    }



    @Override
    protected void onResume() {
        super.onResume();
        //在activity执行onResume时执行mMapView. onResume ()，实现地图生命周期管理
        mMapView.onResume();
    }
    @Override
    protected void onPause() {
        super.onPause();
        //在activity执行onPause时执行mMapView. onPause ()，实现地图生命周期管理
        mMapView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理
        mMapView.onDestroy();
    }
}
