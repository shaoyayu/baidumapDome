package icu.shaoyayu.android.iearnit.activity.service;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.route.DrivingRouteLine;
import com.baidu.mapapi.search.route.TransitRouteLine;
import com.baidu.mapapi.search.route.WalkingRouteLine;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

import icu.shaoyayu.android.common.app.ModuleActivity;
import icu.shaoyayu.android.iearnit.R;
import icu.shaoyayu.android.iearnit.dialog.BottomDialog;
import icu.shaoyayu.android.iearnit.dialog.map.MapRouteDialog;
import icu.shaoyayu.android.iearnit.dialog.map.MapSearchDialog;
import icu.shaoyayu.android.iearnit.dialog.map.MapShareDialog;
import icu.shaoyayu.android.iearnit.utils.PermissionsUtils;
import icu.shaoyayu.android.iearnit.utils.map.DrivingRouteOverlay;
import icu.shaoyayu.android.iearnit.utils.map.TransitRouteOverlay;
import icu.shaoyayu.android.iearnit.utils.map.WalkingRouteOverlay;
import icu.shaoyayu.android.iearnit.view.SimpleMenu;

/**
 * @author shaoyayu
 * 导航和定位部分，设计位置签到等功能
 * Activity和Fragment之间的实时交互
 */
public class LocationServiceActivity extends ModuleActivity {

    protected BroadcastReceiver receiver = null;
    //当前Activity涉及的权限
    private String[] purview = {Manifest.permission.ACCESS_NETWORK_STATE
            , Manifest.permission.READ_EXTERNAL_STORAGE
            , Manifest.permission.WRITE_EXTERNAL_STORAGE
            , Manifest.permission.ACCESS_COARSE_LOCATION
            , Manifest.permission.ACCESS_FINE_LOCATION
            , Manifest.permission.ACCESS_WIFI_STATE
            , Manifest.permission.CHANGE_WIFI_STATE
    };

    private BDLocation mLocation = null;
    private PoiInfo mPoiInfo = null;
    private double latitude = 0;
    private double longitude = 0;
    private LocationClient locationClient;
    private MapView mMapView;
    private BaiduMap mBaiduMap;
    private FloatingActionButton mMapRouteDialog,mMapSearchDialog,mMapShareDialog;
    private Switch mHeatMap;
    private BottomDialog dialog = null;
    private NavigationMap navigationMap =  new NavigationMap();

    private static final String TAG = "LocationServiceActivity";

    private SimpleMenu mSimpleMenu ;

    @Override
    protected void initWindows() {
        //初始化服务
        registerSDKCheckReceiver();
    }

    @Override
    protected int getInterfaceResourceId() {
        return R.layout.activity_location_service;
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    /**
     * 初始化控件
     */
    @Override
    protected void initTheControl() {
        super.initTheControl();
        locationClient = new LocationClient(getApplicationContext());
        //初始化位置权限
        mSimpleMenu = findViewById(R.id.menu_location_service);
        mMapView = findViewById(R.id.mp_location_service);
        mMapRouteDialog = findViewById(R.id.fab_map_route);
        mMapSearchDialog = findViewById(R.id.fab_map_search);
        mMapShareDialog = findViewById(R.id.fab_map_share);
        mHeatMap = findViewById(R.id.sc_open_heat_map);
        mBaiduMap = mMapView.getMap();
        Log.d(TAG,"初始化控件");
        //要放在后面执行
        PermissionsUtils.getInstance().checkPermissions(this,
                purview,
                new PermissionsUtils.IPermissionsResult() {
                    @Override
                    public void passPermissions() {
                        initData();
                    }

                    @Override
                    public void forbidPermissions() {
                        //弹出解释框，确定后前往设置见面
                    }
                });
    }

    /**
     * 初始化数据
     */
    @Override
    protected void initData() {
        super.initData();
        Log.d(TAG,"初始化数据");
        mSimpleMenu.setTvMenuThemeText("位置服务");
        mMapView.showZoomControls(false);
        //获取地图的最大最小缩放
        Log.i(TAG,"最大缩放："+mBaiduMap.getMaxZoomLevel()+",最小缩放："+mBaiduMap.getMinZoomLevel());
        //设置地图的中心,通过工厂进行创建,注意经纬度是反的,
        MapStatusUpdate centerPoint = MapStatusUpdateFactory.newLatLng(new LatLng(26.62587,106.680831));
        mBaiduMap.setMapStatus(centerPoint);
        //设置一个默认的缩放
        MapStatusUpdate defaultZoom = MapStatusUpdateFactory.zoomTo(18);
        mBaiduMap.setMapStatus(defaultZoom);

        initLocationOption();
    }

    /**
     * 绑定控件监听
     */
    @Override
    protected void initControlBindingEvents() {
        super.initControlBindingEvents();

        mHeatMap.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (isChecked){
                //开启热力图
                mBaiduMap.setBaiduHeatMapEnabled(true);
            }else {
                //显示正常地图
                mBaiduMap.setBaiduHeatMapEnabled(false);
            }
            }
        });
        //打开搜索弹出
        mMapSearchDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(dialog!=null &&  dialog.getDialog()!=null
                        && dialog.getDialog().isShowing()) {
                    //dialog is showing so do something
                    dialog.onStart();
                    dialog = new MapSearchDialog(mLocation,new ImplementCallback());
                    dialog.show(getSupportFragmentManager(),"搜索");
                } else {
                    //dialog is not showing
                    dialog = new MapSearchDialog(mLocation,new ImplementCallback());
                    dialog.show(getSupportFragmentManager(),"搜索");
                }

            }
        });
        //弹出导航路线选择
        mMapRouteDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(dialog!=null &&  dialog.getDialog()!=null
                        && dialog.getDialog().isShowing()) {
                    //dialog is showing so do something
                    dialog.onStart();
                    dialog = new MapRouteDialog(mPoiInfo,mLocation,navigationMap);
                    dialog.show(getSupportFragmentManager(),"路线");
                } else {
                    //dialog is not showing
                    dialog = new MapRouteDialog(mPoiInfo,mLocation,navigationMap);
                    dialog.show(getSupportFragmentManager(),"路线");
                }

            }
        });
        //弹出分享的见面
        mMapShareDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(dialog!=null &&  dialog.getDialog()!=null
                        && dialog.getDialog().isShowing()) {
                    //dialog is showing so do something
                    dialog.onStart();
                    dialog = new MapShareDialog(mLocation);
                    dialog.show(getSupportFragmentManager(),"分享");
                } else {
                    //dialog is not showing
                    dialog = new MapShareDialog(mLocation);
                    dialog.show(getSupportFragmentManager(),"分享");
                }

                //从新定位
                initLocationOption();
            }
        });
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
        unregisterReceiver(receiver);
    }

    /**
     * 初始化地图服务
     */
    protected void registerSDKCheckReceiver(){
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if (SDKInitializer.SDK_BROADCAST_ACTION_STRING_NETWORK_ERROR.equals(action)){
                    Toast.makeText(context, "网络错误", Toast.LENGTH_SHORT).show();
                    Log.w(TAG,"网络无法连接");
                }else if (SDKInitializer.SDK_BROADTCAST_ACTION_STRING_PERMISSION_CHECK_ERROR.equals(action)){
                    Toast.makeText(context, "Key验证失败", Toast.LENGTH_SHORT).show();
                    Log.w(TAG,"key验证失败");
                }
            }
        };

        IntentFilter filter = new IntentFilter();
        //监听网络错误
        filter.addAction(SDKInitializer.SDK_BROADCAST_ACTION_STRING_NETWORK_ERROR);
        //监听百度地图的sdk的可以时不时正确
        filter.addAction(SDKInitializer.SDK_BROADTCAST_ACTION_STRING_PERMISSION_CHECK_ERROR);

        registerReceiver(receiver,filter);

    }

    /**
     * 权限申请
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PermissionsUtils.getInstance().onRequestPermissionsResult(this,requestCode,permissions,grantResults);
    }

    /**
     * 初始化定位参数配置
     */
    private void initLocationOption() {
        LocationClientOption locationOption = new LocationClientOption();
        MyLocationListener myLocationListener = new MyLocationListener();
        locationClient.registerLocationListener(myLocationListener);
        locationOption.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
        locationOption.setCoorType("bd09ll");
        locationOption.setScanSpan(0);
        locationOption.setIsNeedAddress(true);
        locationOption.setIsNeedLocationDescribe(true);
        locationOption.setNeedDeviceDirect(false);
        locationOption.setLocationNotify(true);
        locationOption.setIgnoreKillProcess(true);
        locationOption.setIsNeedLocationDescribe(true);
        locationOption.setIsNeedLocationPoiList(true);
        locationOption.SetIgnoreCacheException(false);
        locationOption.setOpenGps(true);
        locationOption.setIsNeedAltitude(false);
        locationOption.setOpenAutoNotifyMode();
        locationOption.setOpenAutoNotifyMode(3000,1, LocationClientOption.LOC_SENSITIVITY_HIGHT);
        locationClient.setLocOption(locationOption);
        locationClient.start();
    }

    /**
     * 实现定位回调
     */
    public class MyLocationListener extends BDAbstractLocationListener {
        @Override
        public void onReceiveLocation(BDLocation location){
            //测试使用 112.193069,30.355534
            location.setLongitude(112.193069);
            location.setLatitude(30.355534);
            if (mLocation==null){
                mLocation = location;
            }
            if (latitude==0){
                latitude = location.getLatitude();
            }
            if (longitude==0){
                longitude = location.getLongitude();
            }
            Log.d(TAG,"latitude:"+latitude+",longitude:"+longitude);
            //初始化位置信息
            MapStatusUpdate centerPoint = MapStatusUpdateFactory.newLatLng(new LatLng(latitude,longitude));
            mBaiduMap.setMapStatus(centerPoint);
            //标记
            //构建Marker图标
            MarkerOptions markerOptions = new MarkerOptions();
            BitmapDescriptor bitmapDescriptor4 = BitmapDescriptorFactory.fromResource(R.mipmap.maps_easyicon);
            markerOptions.position(new LatLng(latitude,longitude))
                    //添加图标
                    .icon(bitmapDescriptor4);
            mBaiduMap.addOverlay(markerOptions);
            //---------------------详细信息获取方法---------------------///
            locationClient.stop();
        }
    }

    public void clearRouteOverlay(){
        mBaiduMap.clear();
    }

    /**
     * poi搜索过后描述位置信息的回调
     */
    public class ImplementCallback implements MapSearchDialog.POICallback {

        /**
         * 回调显示所有poi检索结果
         * 绑定点击事件
         * @param poiInfos
         */
        @Override
        public void showSearchResults(List<PoiInfo> poiInfos) {

        }

        /**
         * 搜索到的内容回调到
         * @param view
         * @param poiInfo
         */
        @Override
        public void callbackForDetails(View view, PoiInfo poiInfo) {
            mPoiInfo = poiInfo;
            Log.d(TAG,"POI详情回调"+poiInfo.name);
            clearRouteOverlay();
            //构建Marker图标
            //初始化位置信息
            MapStatusUpdate centerPoint = MapStatusUpdateFactory.newLatLng(poiInfo.location);
            mBaiduMap.setMapStatus(centerPoint);
            MarkerOptions markerOptions = new MarkerOptions();
            BitmapDescriptor bitmapDescriptor4 = BitmapDescriptorFactory.fromResource(R.mipmap.maps_easyicon);
            markerOptions.position(poiInfo.location)
                    //添加图标
                    .icon(bitmapDescriptor4);
            mBaiduMap.addOverlay(markerOptions);
        }

        @Override
        public void navigationPositionCallback(View view, PoiInfo poiInfo) {
            mPoiInfo = poiInfo;
            Log.d(TAG,"POI导航回调"+poiInfo.name);
            if(dialog!=null &&  dialog.getDialog()!=null
                    && dialog.getDialog().isShowing()) {
                //dialog is showing so do something
                dialog.onStart();
                dialog = new MapRouteDialog(mPoiInfo,mLocation,navigationMap);
                dialog.show(getSupportFragmentManager(),"路线");
            } else {
                //dialog is not showing
                dialog = new MapRouteDialog(mPoiInfo,mLocation,navigationMap);
                dialog.show(getSupportFragmentManager(),"路线");
            }
        }
    }

    /**
     * 选择导航路线的回调类
     * 在百度Map上绘制路线
     */
    public class NavigationMap implements MapRouteDialog.RouteSelectionCallback {
        /**
         * 不行路线导航回调
         * @param walkingRouteLine
         */
        @Override
        public void walkingRoute(WalkingRouteLine walkingRouteLine) {
            clearRouteOverlay();
            WalkingRouteOverlay overlay = new WalkingRouteOverlay(mBaiduMap);
            mBaiduMap.setOnMarkerClickListener(overlay);
            overlay.setData(walkingRouteLine);
            //把搜索结果添加到百度地图里面
            overlay.addToMap();
            //把搜索结果显示在同一个屏幕
            overlay.zoomToSpan();
        }

        /**
         * 公交路线导航回调
         * @param transitRouteLine
         */
        @Override
        public void busRoutes(TransitRouteLine transitRouteLine) {
            clearRouteOverlay();
            TransitRouteOverlay overlay = new TransitRouteOverlay(mBaiduMap);
            mBaiduMap.setOnMarkerClickListener(overlay);
            //取出查询出来的所有路线，选择第一条
            overlay.setData(transitRouteLine);
            //把搜索结果添加到百度地图里面
            overlay.addToMap();
            //把搜索结果显示在同一个屏幕
            overlay.zoomToSpan();
        }

        /**
         * 行车路线导航
         * @param drivingRouteLine
         */
        @Override
        public void drivingDirections(DrivingRouteLine drivingRouteLine) {
            clearRouteOverlay();
            DrivingRouteOverlay overlay = new DrivingRouteOverlay(mBaiduMap);
            mBaiduMap.setOnMarkerClickListener(overlay);
            overlay.setData(drivingRouteLine);
            //把搜索结果添加到百度地图里面
            overlay.addToMap();
            //把搜索结果显示在同一个屏幕
            overlay.zoomToSpan();
        }
    }

    //创建一个热力图服务回调的函数


}
