package icu.shaoyayu.android.baidumap.activity;

import android.annotation.SuppressLint;
import android.app.ListActivity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import java.util.ArrayList;
import java.util.List;

/**
 * @author shaoyayu
 * 权限管理工具，
 * 负责权限的申请和管理
 */
public abstract class AuthorityManagementActivity extends ListActivity {

    private static final String TAG = "AuthorityManagementActivity";

    /**
     * 所有权限
     */
    protected List<String> authority = new ArrayList<>();

    /**
     * 已经授权的权限
     */
    protected List<String> grantedPermissions = new ArrayList<>();

    /**
     * 没有授权的权限
     */
    protected List<String> noGrantedPermissions = new ArrayList<>();

    /**
     * 用户已经拒绝申请的权限需要打开应用新详情才能授权
     */
    protected List<String> deniedPermission = new ArrayList<>();

    protected static final int CODE_AUTHORITY = 0x00010;



    @Override
    protected final void onCreate(@Nullable Bundle savedInstanceState) {
        //提供初始化的布局
        super.onCreate(savedInstanceState);
        initActivity(savedInstanceState);
    }

    protected abstract void initActivity(Bundle savedInstanceState);

    /**
     * 初始权限
     * @param authority
     */
    protected void setAuthority(String authority[]){
        for (int i = 0; i < authority.length; i++) {
            this.authority.add(authority[i]);
        }
        /**
         * 初始换权限
         */
       initAuthority();
    };

    private void initAuthority(){
        for (int i = 0; i < authority.size(); i++) {
            if (ActivityCompat.checkSelfPermission(this, this.authority.get(i)) == PackageManager.PERMISSION_GRANTED) {
                //提出授权
                this.grantedPermissions.add(authority.get(i));
            }else {
                //用户尚未授权
                this.noGrantedPermissions.add(authority.get(i));
                if (!ActivityCompat.shouldShowRequestPermissionRationale(this,
                        authority.get(i))) {
                    /**
                     * 权限已经在上一次申请的时候被永久的驳回
                     * 解释一下权限需求的重要性，打开应用详情去设置权限给予
                     * 我采用的是对话框的形式显示和解释给用户
                     */
                    deniedPermission.add(authority.get(i));
                } else {
                    //没有被驳回的权限，在这里我会采用统一的方式去申请
                }
            }
        }
        requestForAccess(noGrantedPermissions);
    }

    /**
     * 初始化权限，但不申请
     * @param authority
     */
    @SuppressLint("LongLogTag")
    protected void initAuthority(String[] authority){
        for (int i = 0; i < authority.length; i++) {
            this.authority.add(authority[i]);
            Log.i(TAG,"初始化的权限有:"+authority[i]);
        }
        for (int i = 0; i < this.authority.size(); i++) {
            if (ActivityCompat.checkSelfPermission(this, this.authority.get(i)) == PackageManager.PERMISSION_GRANTED) {
                //提出授权
                this.grantedPermissions.add(this.authority.get(i));
                Log.i(TAG,"已经授权的权限:"+this.authority.get(i));
            }else {
                //用户尚未授权
                this.noGrantedPermissions.add(this.authority.get(i));
                Log.i(TAG,"尚未授权的权限有:"+this.authority.get(i));
                if (!ActivityCompat.shouldShowRequestPermissionRationale(this,
                        this.authority.get(i))) {
                    /**
                     * 权限已经在上一次申请的时候被永久的驳回
                     * 解释一下权限需求的重要性，打开应用详情去设置权限给予
                     * 我采用的是对话框的形式显示和解释给用户
                     */
                    deniedPermission.add(this.authority.get(i));
                    Log.i(TAG,"永久驳回的权限有:"+this.authority.get(i));
                } else {
                    //没有被驳回的权限，在这里我会采用统一的方式去申请
                }
            }
        }
    }



    /**
     * 取出已经授权得权限
     * @return
     */
    protected List<String> getGrantedPermissions(){
        return this.grantedPermissions;
    }

    protected List<String> getNoGrantedPermissions(){
        return this.noGrantedPermissions;
    }

    public List<String> getDeniedPermission() {
        return deniedPermission;
    }

    /**
     * 申请权限
     */
    protected void requestForAccess(List<String> atu){
        String[] aut = new String[atu.size()];
        for (int i = 0; i < atu.size(); i++) {
            aut[i] = atu.get(i);
        }
        if (aut.length!=0){
            //没有权限，向用户请求权限
            ActivityCompat.requestPermissions(this, aut,
                    AuthorityManagementActivity.CODE_AUTHORITY);
        }
    }

    /**
     * 权限回调
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode,permissions,grantResults);
        //从新定义申请的权限和未申请的权限
        this.grantedPermissions.clear();
        this.noGrantedPermissions.clear();
        this.deniedPermission.clear();
        if (requestCode==AuthorityManagementActivity.CODE_AUTHORITY){
            for (int i = 0; i < permissions.length; i++) {
                //从新定义授权的情况
                if (grantResults[i]==PackageManager.PERMISSION_GRANTED){
                    //同意授权
                    grantedPermissions.add(permissions[i]);
                }else {
                    noGrantedPermissions.add(permissions[i]);
                    if (!ActivityCompat.shouldShowRequestPermissionRationale(this,
                            permissions[i])) {
                        /**
                         * 权限已经在上一次申请的时候被永久的驳回
                         * 解释一下权限需求的重要性，打开应用详情去设置权限给予
                         * 我采用的是对话框的形式显示和解释给用户
                         */
                        deniedPermission.add(permissions[i]);
                    } else {
                        //没有被驳回的权限，在这里我会采用统一的方式去申请
                    }
                }
            }
        }
        authorizationProcessing();
    }

    abstract void authorizationProcessing();

    /**
     * 打开应用详情设置授权
     */
    protected void openAppSettings(){
        Intent localIntent = new Intent();
        localIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (Build.VERSION.SDK_INT >= 9) {
            localIntent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
            localIntent.setData(Uri.fromParts("package", getPackageName(), null));
        } else if (Build.VERSION.SDK_INT <= 8) {
            localIntent.setAction(Intent.ACTION_VIEW);
            localIntent.setClassName("com.android.settings", "com.android.settings.InstalledAppDetails");
            localIntent.putExtra("com.android.settings.ApplicationPkgName", getPackageName());
        }
        startActivity(localIntent);
    }
}
