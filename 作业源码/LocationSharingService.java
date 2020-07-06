package icu.shaoyayu.android.iearnit.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.baidu.location.BDLocation;

import icu.shaoyayu.android.presenter.udpservice.LocationSharingNetService;

/**
 * @author shaoyayu
 * 实现位置实时共享的一个服务
 */
public class LocationSharingService extends Service implements PositioningService.ContinuousPositioningCallback {

    private LocationSharingNetService netService = new LocationSharingNetService();

    private static final String TAG = "LocationSharingService";

    private PositioningService positioningService;
    public LocationSharingService() {

    }

    @Override
    public void onCreate() {
        super.onCreate();
        positioningService = new PositioningService(getApplicationContext(),this,1500);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        positioningService.startPositioning();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        positioningService.stopPositioning();
    }

    //定位后的回调方法
    @Override
    public void getLocation(BDLocation bdLocation, long timestamp) {
        Log.d(TAG,"后台定位："+timestamp+","+bdLocation.getLatitude()+","+bdLocation.getLongitude());
        //没一秒上传一次记录到云端，如果上传失败，整合下次一起上传到云端，删除数据。
        String country = bdLocation.getCountry();    //获取国家
        String province = bdLocation.getProvince();    //获取省份
        String city = bdLocation.getCity();           //获取城市
        String district = bdLocation.getDistrict();    //获取区县
        String street = bdLocation.getStreet();       //获取街道信息
        String adcode = bdLocation.getAdCode();        //获取adcode
        String town = bdLocation.getTown();           //获取乡镇信息
        String desc = bdLocation.getLocationDescribe(); //详细信息
        String locationInfo = timestamp+","+bdLocation.getLatitude()+","+bdLocation.getLongitude()+","+System.currentTimeMillis()+","+country+","+province+","+city+","+district+","+street+","+adcode+","+town+","+desc;
        netService.locationSharing("soxswmddl4546dsf45dsf，"+locationInfo);
        //直接上传到Redis数据库，LocationSharingService
        //写入文件系统里面去存储
    }
}
