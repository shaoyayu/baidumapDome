package icu.shaoyayu.android.baidumap.activity;

import com.baidu.mapapi.map.CircleOptions;
import com.baidu.mapapi.map.Stroke;
import com.baidu.mapapi.model.LatLng;

/**
 * @author shaoyayu
 * 圆形覆盖物
 */
public class CircelOverlayActivity extends BaiDuMapBaseActivity {
    @Override
    public void init() {
        CircleOptions options = new CircleOptions () ;
        //创建一个圆形覆盖物的参数
        options . center (new LatLng(30.35645,112.158437))
                //圆心
                . radius (1000)
                //半径
                . stroke (new Stroke(20,0x55FF0000))//线条宽度、颜色
                .fillColor (0x5500FF00) ; //圆的填充颜色
        baiduMap. addOverlay (options) ; //添加一 个覆盖物
    }
}
