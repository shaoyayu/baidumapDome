package icu.shaoyayu.android.iearnit.service;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;

import icu.shaoyayu.android.iearnit.R;
import icu.shaoyayu.android.iearnit.activity.service.LocationServiceActivity;

/**
 * @author shaoyayu
 */
public class PositioningService {

    private Context context;
    private ContinuousPositioningCallback callback;
    private int spacing = 0;
    LocationClient locationClient;

    public PositioningService(Context context,final ContinuousPositioningCallback callback,int spacing){
        this.context = context;
        this.callback = callback;
        this.spacing = spacing;
        locationClient = new LocationClient(context);
        initLocationOption();
    }

    /**
     * 联系位置定位在后台
     */
    public void initLocationOption() {
        //声明LocationClient类实例并配置定位参数
        LocationClientOption locationOption = new LocationClientOption();
        LocationSharingLocationListener myLocationListener = new LocationSharingLocationListener();
        //注册监听函数
        locationClient.registerLocationListener(myLocationListener);
        //可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
        locationOption.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
        //可选，默认gcj02，设置返回的定位结果坐标系，如果配合百度地图使用，建议设置为bd09ll;
        locationOption.setCoorType("bd09ll");
        //可选，默认0，即仅定位一次，设置发起连续定位请求的间隔需要大于等于1000ms才是有效的
        locationOption.setScanSpan(this.spacing);
        //可选，设置是否需要地址信息，默认不需要
        locationOption.setIsNeedAddress(true);
        //可选，设置是否需要地址描述
        locationOption.setIsNeedLocationDescribe(true);
        //可选，设置是否需要设备方向结果
        locationOption.setNeedDeviceDirect(false);
        //可选，默认false，设置是否当gps有效时按照1S1次频率输出GPS结果
        locationOption.setLocationNotify(true);
        //可选，默认true，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认不杀死
        locationOption.setIgnoreKillProcess(true);
        //可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”
        locationOption.setIsNeedLocationDescribe(true);
        //可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
        locationOption.setIsNeedLocationPoiList(true);
        //可选，默认false，设置是否收集CRASH信息，默认收集
        locationOption.SetIgnoreCacheException(false);
        //可选，默认false，设置是否开启Gps定位
        locationOption.setOpenGps(true);
        //可选，默认false，设置定位时是否需要海拔信息，默认不需要，除基础定位版本都可用
        locationOption.setIsNeedAltitude(false);
        //设置打开自动回调位置模式，该开关打开后，期间只要定位SDK检测到位置变化就会主动回调给开发者，该模式下开发者无需再关心定位间隔是多少，定位SDK本身发现位置变化就会及时回调给开发者
        locationOption.setOpenAutoNotifyMode();
        //设置打开自动回调位置模式，该开关打开后，期间只要定位SDK检测到位置变化就会主动回调给开发者
        locationOption.setOpenAutoNotifyMode(3000,1, LocationClientOption.LOC_SENSITIVITY_HIGHT);
        //需将配置好的LocationClientOption对象，通过setLocOption方法传递给LocationClient对象使用
        locationClient.setLocOption(locationOption);
        //核心实现代码如下，详细代码请参考官网Demo。
        //开启前台定位服务：
        Notification.Builder builder = new Notification.Builder (context);
        //获取一个Notification构造器
        Intent nfIntent = new Intent(context, LocationServiceActivity.class);
        builder.setContentIntent(PendingIntent.getActivity(context, 0, nfIntent, 0)) // 设置PendingIntent
                .setContentTitle("正在进行后台定位") // 设置下拉列表里的标题
                .setSmallIcon(R.mipmap.ic_launcher) // 设置状态栏内的小图标
                .setContentText("后台定位通知") // 设置上下文内容
                .setAutoCancel(true)
                .setWhen(System.currentTimeMillis()); // 设置该通知发生的时间
        Notification notification = null;
        notification = builder.build();
        notification.defaults = Notification.DEFAULT_SOUND; //设置为默认的声音
        locationClient.enableLocInForeground(1001, notification);// 调起前台定位
        //停止前台定位服务：
        locationClient.disableLocInForeground(true);// 关闭前台定位，同时移除通知栏


    }

    /**
     * 开始定位
     */
    public void startPositioning(){
        locationClient.start();
    }

    /**
     * 停止定位
     */
    public void stopPositioning(){
        locationClient.stop();
    }


    /**
     *
     * 实现定位回调
     */
    public class LocationSharingLocationListener extends BDAbstractLocationListener {
        @Override
        public void onReceiveLocation(BDLocation location){
            callback.getLocation(location,System.currentTimeMillis());
        }
    }

    public interface ContinuousPositioningCallback{
        void getLocation(BDLocation bdLocation,long timestamp);
    }


}
