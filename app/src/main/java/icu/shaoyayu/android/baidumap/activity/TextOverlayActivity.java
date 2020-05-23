package icu.shaoyayu.android.baidumap.activity;

import com.baidu.mapapi.map.TextOptions;
import com.baidu.mapapi.model.LatLng;

/**
 * @author shaoyayu
 * 文字覆盖物
 */
public class TextOverlayActivity extends BaiDuMapBaseActivity {
    @Override
    public void init() {

        TextOptions textOptions = new TextOptions();
        //设置位置
        textOptions.position(new LatLng(30.35645,112.158437))
                //设置内容
                .text("长江大学文理学院西校区")
                //设置文本大小
                .fontSize(50)
                //设置为黑色
                .fontColor(0XFF000000)
                //设置一个背景颜色
                .bgColor(0X55FF0000);
        baiduMap.addOverlay(textOptions);
    }
}
