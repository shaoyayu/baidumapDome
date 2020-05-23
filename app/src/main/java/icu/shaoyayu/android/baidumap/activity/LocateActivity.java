package icu.shaoyayu.android.baidumap.activity;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import icu.shaoyayu.android.baidumap.R;

/**
 * @author shaoyayu
 * 开发文档地址
 * https://developer.android.com/reference/android/location/Location?hl=en
 */
public class LocateActivity extends AppCompatActivity {

    private TextView mTvLocateText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_locate);
        mTvLocateText = findViewById(R.id.tv_locate_text);
        //获取位置管理器
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        //检查权限
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            String text = printMsg(location);
            if (text==null||text.length()==0){
                mTvLocateText.setText("暂无数据");
            }
            mTvLocateText.setText(text);
            return;
        }else {
            mTvLocateText.setText("当前应用没有权限获取地理位置信息");
        }

    }

    private String printMsg(Location location) {
        if (location==null){
            return null;
        }
        return "以米为单位获取此位置的估计水平精度（径向）"+location.getAccuracy()+
                "\n获取海拔高度（如果可用），以WGS 84参考椭球上方的米为单位:"+location.getAltitude()+
                "\n以度为单位获取方位角: "+location.	getBearing()+
                "\n获取以度为单位的纬度:"+	location.getLatitude()+
                "\n获取经度以度为单位:"+	location.getLongitude()+
                "\n返回生成此修复程序的提供程序的名称:"+	location.getProvider()+
                "\n获取速度（如果有）以米/秒为单位:"+location.getSpeed()+
                "\n返回此修复程序的UTC时间，以毫秒为单位，从1970年1月1日开始:"+location.getTime()+
                "\n如果该位置具有水平精度，则为True:"+location.hasAccuracy()+
                "\n如果该位置有海拔，则为True:"+location.hasAltitude()
                ;
    }


}
