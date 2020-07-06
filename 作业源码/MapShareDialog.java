package icu.shaoyayu.android.iearnit.dialog.map;

import android.app.ActivityManager;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;

import icu.shaoyayu.android.iearnit.R;
import icu.shaoyayu.android.iearnit.dialog.BottomDialog;
import icu.shaoyayu.android.iearnit.service.LocationSharingService;

import static android.content.Context.ACTIVITY_SERVICE;

/**
 * @author shaoyayu
 * 位置共享解决方案
 */
public class MapShareDialog extends BottomDialog {

    private ImageView mStopDialog;
    private Switch locationSharing,trackSharing,recordTrack;
    private Intent positioningService;
    private TextView mLocationInfo;
    private BDLocation mLocation;

    public MapShareDialog(BDLocation location){
        this.mLocation = location;
    }

    @Override
    protected View createView(LayoutInflater inflater, ViewGroup container) {
        View view = inflater.inflate(R.layout.dialog_map_share,container,false);
        initTheControl(view);
        initData();
        return view;
    }


    //初始化控件
    private void initTheControl(View view){
        positioningService = new Intent(getContext(), LocationSharingService.class);
        mStopDialog = view.findViewById(R.id.iv_map_share_dog_close);
        //监控关闭弹窗
        mStopDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onStop();
            }
        });
        //位置共享控件
        locationSharing = view.findViewById(R.id.sc_location_sharing);
        //判断当前的服务是不是正在执行
        if (isServiceRunning("icu.shaoyayu.android.iearnit.service.LocationSharingService")){
            //设置为执行状态
            locationSharing.setChecked(true);
        }
        //开关事假
        locationSharing.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    //开启
                    showTips("已经为你开启位置共享");
                    getActivity().startService(positioningService);
                }else {
                    //关闭
                    showTips("已经为你关闭位置共享");
                    getActivity().stopService(positioningService);
                }
            }
        });
        //轨迹共享
        trackSharing = view.findViewById(R.id.sc_track_sharing);
        trackSharing.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    //开启
                    showTips("已经为你开启轨迹共享");
                }else {
                    //关闭
                    showTips("已经为你关闭轨迹共享");
                }
            }
        });
        //轨迹录制
        recordTrack = view.findViewById(R.id.sc_track_record);
        recordTrack.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    //开启
                    showTips("已经为你开启轨迹录制");
                }else {
                    //关闭
                    showTips("已经为你关闭轨迹录制");
                }
            }
        });
        mLocationInfo = view.findViewById(R.id.tv_location_info);

    }

    //初始化数据
    private void initData(){
        if (mLocation==null){
            mLocationInfo.setText("Positioning failed");
        }else {
            String province = mLocation.getProvince();    //获取省份
            String city = mLocation.getCity();           //获取城市
            String district = mLocation.getDistrict();    //获取区县
            String street = mLocation.getStreet();       //获取街道信息
            String town = mLocation.getTown();           //获取乡镇信息
            String desc = mLocation.getLocationDescribe(); //详细信息
            String locationInfo = province + city + district + street + town +"\n"+ desc;
            mLocationInfo.setText(locationInfo);
        }
    }

    //显示一个对话框提示共享开始，
    private void showTips(String str){
        Toast.makeText(getContext(), str, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    /**
     * 根据服务的名称判断当前的服务是不是正在执行
     * @param serviceName
     * @return
     */
    private boolean isServiceRunning(String serviceName) {
        ActivityManager manager = (ActivityManager) getActivity().getSystemService(ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceName.equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
}
