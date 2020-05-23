package icu.shaoyayu.android.baidumap.activity;

import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MapViewLayoutParams;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.model.LatLng;

import icu.shaoyayu.android.baidumap.R;

/**
 * @author shaoyayu
 */
public class LocationDomeActivity extends BaiDuMapBaseActivity {

    private static final String TAG = "LocationDomeActivity";

    public LocationClient mLocationClient = null;
    private MyLocationListener myListener = null;
    //BDAbstractLocationListener为7.2版本新增的Abstract类型的监听接口
    //原有BDLocationListener接口暂时同步保留。具体介绍请参考后文第四步的说明

    @Override
    public void init() {
        myListener = new MyLocationListener(mMapView,baiduMap);
        mLocationClient = new LocationClient(getApplicationContext());
        //声明LocationClient类
        mLocationClient.registerLocationListener(myListener);
        //注册监听函数
        initLocation();
        //开始定位
        mLocationClient.start();
        //mLocationClient为第二步初始化过的LocationClient对象
        //调用LocationClient的start()方法，便可发起定位请求
        //start()：启动定位SDK；stop()：关闭定位SDK。调用start()之后只需要等待定位结果自动回调即可。
        //开发者定位场景如果是单次定位的场景，在收到定位结果之后直接调用stop()函数即可。
        //如果stop()之后仍然想进行定位，可以再次start()等待定位结果回调即可。
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //停止供应
        mLocationClient.stop();
    }

    private void initLocation() {

        LocationClientOption option = new LocationClientOption();

        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
        //可选，设置定位模式，默认高精度
        //LocationMode.Hight_Accuracy：高精度；
        //LocationMode. Battery_Saving：低功耗；
        //LocationMode. Device_Sensors：仅使用设备；

        option.setCoorType("bd09ll");
        //可选，设置返回经纬度坐标类型，默认GCJ02
        //GCJ02：国测局坐标；
        //BD09ll：百度经纬度坐标；
        //BD09：百度墨卡托坐标；
        //海外地区定位，无需设置坐标类型，统一返回WGS84类型坐标

        option.setScanSpan(10);
        //可选，设置发起定位请求的间隔，int类型，单位ms
        //如果设置为0，则代表单次定位，即仅定位一次，默认为0
        //如果设置非0，需设置1000ms以上才有效

        option.setOpenGps(true);
        //可选，设置是否使用gps，默认false
        //使用高精度和仅用设备两种定位模式的，参数必须设置为true

        option.setLocationNotify(true);
        //可选，设置是否当GPS有效时按照1S/1次频率输出GPS结果，默认false

        option.setIgnoreKillProcess(false);
        //可选，定位SDK内部是一个service，并放到了独立进程。
        //设置是否在stop的时候杀死这个进程，默认（建议）不杀死，即setIgnoreKillProcess(true)

        option.SetIgnoreCacheException(false);
        //可选，设置是否收集Crash信息，默认收集，即参数为false

        option.setWifiCacheTimeOut(5*60*1000);
        //可选，V7.2版本新增能力
        //如果设置了该接口，首次启动定位时，会先判断当前Wi-Fi是否超出有效期，若超出有效期，会先重新扫描Wi-Fi，然后定位

        option.setEnableSimulateGps(false);
        //可选，设置是否需要过滤GPS仿真结果，默认需要，即参数为false

        option.setNeedNewVersionRgc(true);
        //可选，设置是否需要最新版本的地址信息。默认不需要，即参数为false

        //获取详细信息
        option.setIsNeedAddress(true);
        //可选，是否需要地址信息，默认为不需要，即参数为false
        //如果开发者需要获得当前点的地址信息，此处必须为true

        //获取位置描述
        option.setIsNeedLocationDescribe(true);
        //可选，是否需要位置描述信息，默认为不需要，即参数为false
        //如果开发者需要获得当前点的位置信息，此处必须为true

        mLocationClient.setLocOption(option);
        //mLocationClient为第二步初始化过的LocationClient对象
        //需将配置好的LocationClientOption对象，通过setLocOption方法传递给LocationClient对象使用
        //更多LocationClientOption的配置，请参照类参考中LocationClientOption类的详细说明
    }

     class MyLocationListener extends BDAbstractLocationListener {

         private  View pop = null;

         private TextView tvPopText = null;

        private MapView mapView = null;
        private BaiduMap baiduMap = null;

        MyLocationListener(MapView mapView,BaiduMap baiduMap){
            this.mapView = mapView;
            this.baiduMap = baiduMap;
        }

        /**
         * 注册成功的监听器坐标
         * @param location
         */
        @Override
        public void onReceiveLocation(BDLocation location) {
            //此处的BDLocation为定位结果信息类，通过它的各种get方法可获取定位相关的全部结果
            //以下只列举部分获取经纬度相关（常用）的结果信息
            //更多结果信息获取说明，请参照类参考中BDLocation类中的说明
            double latitude = location.getLatitude();    //获取纬度信息
            double longitude = location.getLongitude();    //获取经度信息
            float radius = location.getRadius();    //获取定位精度，默认值为0.0f
            String coorType = location.getCoorType();
            //获取经纬度坐标类型，以LocationClientOption中设置过的坐标类型为准
            int errorCode = location.getLocType();
            //获取定位类型、定位错误返回码，具体信息可参照类参考中BDLocation类中的说明
            Log.i(TAG,"==>>定位结果<<==：" +
                    "\n返回当前的时间:"+location.getTime()+
                    "\n获取定位类型: 参考 定位结果描述 相关的字段："+location.getLocType()+
                    "\n纬度信息："+latitude+
                    "\n经度信息: "+longitude+
                    "\n获取定位精度："+radius+
                    "\nLocationClientOption中设置过的坐标类型为准："+coorType+
                    "\n获取定位类型、定位错误返回码："+errorCode);
            //更新位置信息
            updateLocationInformation(location);
            //添加一个标记点
            placeMark(location);
            /*===============================================================================*/
            //此处的BDLocation为定位结果信息类，通过它的各种get方法可获取定位相关的全部结果
            //以下只列举部分获取地址相关的结果信息
            //更多结果信息获取说明，请参照类参考中BDLocation类中的说明
            String addr = location.getAddrStr();    //获取详细地址信息
            String country = location.getCountry();    //获取国家
            String province = location.getProvince();    //获取省份
            String city = location.getCity();    //获取城市
            String district = location.getDistrict();    //获取区县
            String street = location.getStreet();    //获取街道信息
            String adcode = location.getAdCode();    //获取adcode
            String town = location.getTown();    //获取乡镇信息
            Log.i(TAG,"==>>>获取地址<<<==："+
                    "\n获取详细地址信息:"+addr+
                    "\n获取国家:"+country+
                    "\n获取省份:"+province+
                    "\n获取城市:"+city+
                    "\n获取区县:"+district+
                    "\n获取街道信息:"+street+
                    "\n获取adcode:"+adcode+
                    "\n获取街道信息:"+town);
            String locationDescribe = location.getLocationDescribe();    //获取位置描述信息
            Log.i(TAG,"==>>>位置信息描述<<<==:\n"+locationDescribe);
            mLocationClient.stop();
        }

         void updateLocationInformation(BDLocation location){
             double latitude = location.getLatitude();    //获取纬度信息
             double longitude = location.getLongitude();    //获取经度信息
             MapStatusUpdate centerPoint = MapStatusUpdateFactory.newLatLng(new LatLng(latitude,longitude));
             baiduMap.setMapStatus(centerPoint);
             //设置一个默认的缩放
             MapStatusUpdate defaultZoom = MapStatusUpdateFactory.zoomTo(10);
             baiduMap.setMapStatus(defaultZoom);
         }

         /**
          * 添加一个位置图层个标志物点击事件
          * @param location
          */
         void placeMark(BDLocation location){

             double latitude = location.getLatitude();    //获取纬度信息
             double longitude = location.getLongitude();    //获取经度信息
             final LatLng point = new LatLng(latitude,longitude);
             final String positioningResults = "==>>基于百度地图定位结果<<==：" +
                     "\n返回当前的时间:"+location.getTime()+
                     "\n获取定位类型："+location.getLocType()+
                     "\n纬度信息："+latitude+
                     "\n经度信息: "+longitude+
                     "\n获取定位精度："+location.getRadius()+
                     "\nLocationClientOption中设置过的坐标类型为准："+location.getCoorType()+
                     "\n详细的位置信息:\n"+location.getAddrStr()+location.getLocationDescribe();
            //构建Marker图标
             MarkerOptions markerOptions = new MarkerOptions();
             BitmapDescriptor bitmapDescriptor4 = BitmapDescriptorFactory.fromResource(R.mipmap.flag_mark_red);
             markerOptions.position(point)
                     //添加图标
                     .icon(bitmapDescriptor4);
             baiduMap.addOverlay(markerOptions);
             //创建一个点击事件监听器
             BaiduMap.OnMarkerClickListener onMarkerClickListener = new BaiduMap.OnMarkerClickListener() {
                 @Override
                 public boolean onMarkerClick(Marker marker) {
                     LatLng latLng = point;
                     //显示一个顶部的View
                     if (pop==null){
                         pop = View.inflate(LocationDomeActivity.this,R.layout.layout_pop,null);
                         tvPopText = pop.findViewById(R.id.tv_pop_text);
                         mMapView.addView(pop,createLayout(latLng));
                     }{
                         //改变一下位置即可
                         mMapView.updateViewLayout(pop,createLayout(latLng));
                     }
                     //设置标题
                     tvPopText.setText(positioningResults);
                     //返回true代表消费点击事件
                     return true;
                 }
             };
             baiduMap.setOnMarkerClickListener(onMarkerClickListener);

         }

         private MapViewLayoutParams createLayout(LatLng latLng){
             MapViewLayoutParams.Builder builder = new MapViewLayoutParams.Builder();
             //指定坐标经纬度
             builder.layoutMode(MapViewLayoutParams.ELayoutMode.mapMode);
             //指定坐标
             builder.position(latLng);
             builder.yOffset(-40);
             //创建布局
             MapViewLayoutParams params = builder.build();
             return params;
         }

    }
}
