package icu.shaoyayu.android.baidumap.activity;

import android.view.View;
import android.widget.TextView;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapViewLayoutParams;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.model.LatLng;

import icu.shaoyayu.android.baidumap.R;

/**
 * @author shaoyayu
 * marker覆盖物
 */
public class MarkerOverlayActivity extends BaiDuMapBaseActivity {

    private  View pop = null;

    private TextView tvPopText = null;

    @Override
    public void init() {
        //初始化覆盖物
        initMarkerOverlay();
        //注册监听器
        registerToListen();
    }

    /**
     * 注册监听器
     */
    private void registerToListen() {
        //创建一个点击事件监听器
        BaiduMap.OnMarkerClickListener onMarkerClickListener = new BaiduMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                LatLng latLng = marker.getPosition();
                //显示一个顶部的View
                if (pop==null){
                    pop = View.inflate(MarkerOverlayActivity.this,R.layout.layout_pop,null);
                    tvPopText = pop.findViewById(R.id.tv_pop_text);
                    mMapView.addView(pop,createLayout(latLng));
                }{
                    //改变一下位置即可
                    mMapView.updateViewLayout(pop,createLayout(latLng));
                }
                //设置标题
                tvPopText.setText(marker.getTitle());
                //返回true代表消费点击事件
                return true;
            }
        };
        baiduMap.setOnMarkerClickListener(onMarkerClickListener);

        //拖动监听器
        BaiduMap.OnMarkerDragListener onMarkerDragListener = new BaiduMap.OnMarkerDragListener() {

            /**
             * 正在拖动
             * @param marker
             */
            @Override
            public void onMarkerDrag(Marker marker) {
                //改变一下位置即可
                mMapView.updateViewLayout(pop,createLayout(marker.getPosition()));
            }

            /**
             * 移动结束事件
             * @param marker
             */
            @Override
            public void onMarkerDragEnd(Marker marker) {
                //改变一下位置即可
                mMapView.updateViewLayout(pop,createLayout(marker.getPosition()));
            }

            /**
             * 标志开始移动
             * @param marker
             */
            @Override
            public void onMarkerDragStart(Marker marker) {
                //改变一下位置即可
                mMapView.updateViewLayout(pop,createLayout(marker.getPosition()));
            }
        };
        baiduMap.setOnMarkerDragListener(onMarkerDragListener);

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

    /**
     * 初始地图的marker覆盖物
     */
    private void initMarkerOverlay(){
        MarkerOptions markerOptions = new MarkerOptions();
        BitmapDescriptor bitmapDescriptor1 = BitmapDescriptorFactory.fromResource(R.mipmap.flag_mark_blue);
        BitmapDescriptor bitmapDescriptor2 = BitmapDescriptorFactory.fromResource(R.mipmap.flag_mark_gray);
        BitmapDescriptor bitmapDescriptor3 = BitmapDescriptorFactory.fromResource(R.mipmap.flag_mark_green);
        BitmapDescriptor bitmapDescriptor4 = BitmapDescriptorFactory.fromResource(R.mipmap.flag_mark_red);
        BitmapDescriptor bitmapDescriptor5 = BitmapDescriptorFactory.fromResource(R.mipmap.flag_mark_violet);
        BitmapDescriptor bitmapDescriptor6 = BitmapDescriptorFactory.fromResource(R.mipmap.flag_mark_yellow);

        //设置位置
        markerOptions.position(new LatLng(30.35645,112.158437))
                //名称
                .title("长大文理西校区")
                //添加图标
                .icon(bitmapDescriptor1)
                //设置为可用移动
                .draggable(true);
        baiduMap.addOverlay(markerOptions);
        //112.154148,30.360548 操场
        markerOptions.position(new LatLng(30.360548,112.154148))
                //名称
                .title("操场")
                //添加图标
                .icon(bitmapDescriptor2)
                //设置为可用移动
                .draggable(true);
        baiduMap.addOverlay(markerOptions);
        //112.15338,30.357158 西校区南门
        markerOptions.position(new LatLng(30.357158,112.15338))
                //名称
                .title("西校区南门")
                //添加图标
                .icon(bitmapDescriptor3)
                //设置为可用移动
                .draggable(true);
        baiduMap.addOverlay(markerOptions);
        //112.153421,30.362082 学生宿舍
        markerOptions.position(new LatLng(30.362082,112.153421))
                //名称
                .title("学生宿舍")
                //添加图标
                .icon(bitmapDescriptor4)
                //设置为可用移动
                .draggable(true);
        baiduMap.addOverlay(markerOptions);

        //112.15821,30.35998 里仁堂公寓
        markerOptions.position(new LatLng(30.35998,112.15821))
                //名称
                .title("里仁堂公寓")
                //添加图标
                .icon(bitmapDescriptor5)
                //设置为可用移动
                .draggable(true);
        baiduMap.addOverlay(markerOptions);

        //112.153849,30.359403 图书馆
        markerOptions.position(new LatLng(30.359403,112.153849))
                //名称
                .title("图书馆")
                //添加图标
                .icon(bitmapDescriptor6)
                //设置为可用移动
                .draggable(true);
        baiduMap.addOverlay(markerOptions);
    }

    /*
    MarkerOptions	alpha(float alpha)
        设置 Marker 覆盖物图标的透明度，取值为[0,1]，默认1.0，若超出范围则默认为1.0
        MarkerOptions	anchor(float anchorX, float anchorY)
        设置 marker 覆盖物的锚点比例，默认（0.5f, 1.0f）水平居中，垂直下对齐
        MarkerOptions	animateType(MarkerOptions.MarkerAnimateType type)
        设置marker动画类型，见 MarkerAnimateType，默认无动画
        MarkerOptions	clickable(boolean isClickable)
        设置Marker是否可点击
        MarkerOptions	draggable(boolean draggable)
        设置 marker 是否允许拖拽，默认不可拖拽
        MarkerOptions	extraInfo(Bundle extraInfo)
        设置 marker 覆盖物的额外信息
        MarkerOptions	fixedScreenPosition(Point point)
        设置 Marker 覆盖物屏幕位置点
        MarkerOptions	flat(boolean flat)
        设置 marker设置 是否平贴地图
        float	getAlpha()
        获取Marker图标透明度
        float	getAnchorX()
        获取 marker 覆盖物水平方向锚点比例
        float	getAnchorY()
        获取 marker 覆盖物垂直方向锚点比例
        MarkerOptions.MarkerAnimateType	getAnimateType()
        获取marker动画类型
        Bundle	getExtraInfo()
        获取marker覆盖物的额外信息
        BitmapDescriptor	getIcon()
        获取 Marker 覆盖物的图标
        java.util.ArrayList<BitmapDescriptor>	getIcons()
        返回Marker的动画帧列表，动画的描点和大小以第一帧为准，建议图片大小保持一致
        int	getPeriod()
        得到多少帧刷新一次图片资源，值越小动画越快
        LatLng	getPosition()
        获取 marker 覆盖物的位置坐标
        float	getRotate()
        获取 marker 覆盖物旋转角度
        java.lang.String	getTitle()
        Deprecated.
        int	getZIndex()
        获取 marker 覆盖物的 zIndex
        MarkerOptions	icon(BitmapDescriptor icon)
        设置 Marker 覆盖物的图标，相同图案的 icon 的 marker 最好使用同一个 BitmapDescriptor 对象以节省内存空间。
        MarkerOptions	icons(java.util.ArrayList<BitmapDescriptor> icons)
        设置 Marker 覆盖物的图标，相同图案的 icon 的 marker 最好使用同一个 BitmapDescriptor 对象以节省内存空间。
        MarkerOptions	infoWindow(InfoWindow infoWindow)
        设置 Marker 绑定的InfoWindow
        boolean	isDraggable()
        获取 marker 覆盖物是否可以拖拽
        boolean	isFlat()
        获取 marker 是否平贴地图
        boolean	isPerspective()
        获取 marker 覆盖物是否开启近大远小效果
        boolean	isVisible()
        获取 marker 覆盖物的可见性
        MarkerOptions	period(int period)
        设置多少帧刷新一次图片资源，Marker动画的间隔时间，值越小动画越快
        MarkerOptions	perspective(boolean perspective)
        设置是否开启 marker 覆盖物近大远小效果，默认开启
        MarkerOptions	position(LatLng position)
        设置 marker 覆盖物的位置坐标
        MarkerOptions	rotate(float rotate)
        设置 marker 覆盖物旋转角度，逆时针
        MarkerOptions	scaleX(float scaleX)
        设置 Marker 覆盖物X方向缩放
        MarkerOptions	scaleY(float scaleY)
        设置 Marker 覆盖物X方向缩放
        MarkerOptions	title(java.lang.String title)
        Deprecated.
        MarkerOptions	visible(boolean visible)
        设置 marker 覆盖物的可见性
        MarkerOptions	yOffset(int yOffset)
        设置Marker坐标的Y偏移量
        MarkerOptions	zIndex(int zIndex)
        设置 marker 覆盖物的 zIndex
     */
}
