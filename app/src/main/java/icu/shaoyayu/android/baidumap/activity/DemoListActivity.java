package icu.shaoyayu.android.baidumap.activity;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.baidu.mapapi.SDKInitializer;

/**
 * @author  shaoyayu
 * 程序主页的功能列表
 */
public class DemoListActivity extends AuthorityManagementActivity {

    private static final String TAG = "DemoListActivity";

    protected BroadcastReceiver receiver = null;

    private ActivityBindClass[] activityBindClasses = {
            new ActivityBindClass(String.class,"地图与定位"),
            new ActivityBindClass(LocateActivity.class,"简单的定位"),
            new ActivityBindClass(MainActivity.class,"简单入门"),
            new ActivityBindClass(MapLayerActivity.class,"地图图形"),
            new ActivityBindClass(CircelOverlayActivity.class,"圆形覆盖物"),
            new ActivityBindClass(TextOverlayActivity.class,"文本覆盖物"),
            new ActivityBindClass(MarkerOverlayActivity.class,"Marker覆盖物"),
            new ActivityBindClass(SearchInBoundActivity.class,"范围搜索"),
            new ActivityBindClass(SearchInCityActivity.class,"城市搜索"),
            new ActivityBindClass(SearchInNearbyActivity.class,"范围搜索"),
            new ActivityBindClass(DrivingSearchActivity.class,"驾车路线规划"),
            new ActivityBindClass(TransitSearchActivity.class,"换成规划"),
            new ActivityBindClass(WalkingSearchActivity.class,"步行规划"),
            new ActivityBindClass(LocationDomeActivity.class,"定位功能")
    };

    //程序需要的权限集和

    private String[] aut = {
            Manifest.permission.ACCESS_NETWORK_STATE
            ,Manifest.permission.READ_EXTERNAL_STORAGE
            ,Manifest.permission.WRITE_EXTERNAL_STORAGE
            ,Manifest.permission.ACCESS_COARSE_LOCATION
            ,Manifest.permission.ACCESS_FINE_LOCATION
            ,Manifest.permission.ACCESS_WIFI_STATE
            ,Manifest.permission.CHANGE_WIFI_STATE
    };

    @Override
    protected void initActivity(Bundle savedInstanceState) {
        registerSDKCheckReceiver();
        ArrayAdapter<ActivityBindClass> arrayAdapter =
                new ArrayAdapter<>(this,android.R.layout.simple_list_item_1,activityBindClasses);
        setListAdapter(arrayAdapter);
        initAuthority(aut);
        requestForAccess(noGrantedPermissions);
    }

    /**
     * 权限申请后的回调
     */
    @Override
    void authorizationProcessing() {
        for (int i = 0; i < authority.size(); i++) {
            Log.i(TAG,"申请的所有权限："+authority.get(i));
        }
        for (int i = 0; i < grantedPermissions.size(); i++) {
            Log.i(TAG,"已经授权的权限："+grantedPermissions.get(i));
        }
        for (int i = 0; i < noGrantedPermissions.size(); i++) {
            Log.i(TAG,"没有收取的权限有："+noGrantedPermissions.get(i));
        }
        if (deniedPermission.size()!=0){
            //打开应用中心授权
            showToast("请授权应用申请的权限");
            openAppSettings();
        }
        for (int i = 0; i < deniedPermission.size(); i++) {
            Log.i(TAG,"用户驳回申请的权限有："+deniedPermission.get(i));
        }
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        //第一个不跳转
        if (position==0){
            showToast("请选择你的功能");
            return;
        }
        Intent intent = null;
        ActivityBindClass activity = (ActivityBindClass) l.getItemAtPosition(position);
        intent = new Intent(this,activity.cls);
        startActivity(intent);
    }

    protected void registerSDKCheckReceiver(){
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if (SDKInitializer.SDK_BROADCAST_ACTION_STRING_NETWORK_ERROR.equals(action)){
                    showToast("网络错误");
                    Log.w(TAG,"网络无法连接");
                }else if (SDKInitializer.SDK_BROADTCAST_ACTION_STRING_PERMISSION_CHECK_ERROR.equals(action)){
                    showToast("key验证失败");
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
     * 在屏幕中央显示Toast
     * @param text
     */
    public void showToast(CharSequence text){
        Toast toast = Toast.makeText(this,text,Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER,0,0);
        toast.show();
    }

    class ActivityBindClass{
        //类名
        public Class<?> cls;
        //Activity的名称
        public String name;

        public ActivityBindClass(Class<?> cls, String name) {
            this.cls = cls;
            this.name = name;
        }

        @NonNull
        @Override
        public String toString() {
            return name;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
    }

}
