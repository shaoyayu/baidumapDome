package icu.shaoyayu.android.baidumap.activity;

import android.view.KeyEvent;
import android.widget.Toast;

import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.poi.PoiCitySearchOption;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiDetailSearchOption;
import com.baidu.mapapi.search.poi.PoiResult;

/**
 * @author shaoyayu
 * 城市范围查询
 * 详细查询
 */
public class SearchInCityActivity extends PoiSearchBaseActivity {

    private int pag = 0;

    @Override
    protected void poiSearchInit() {
        //普通的查找
        poiSearch.searchInCity(getPoiCitySearchOption());
        //松桃坐标 109.207144,28.169238
        MapStatusUpdate centerPoint = MapStatusUpdateFactory.newLatLng(new LatLng(28.169238,109.207144));
        baiduMap.setMapStatus(centerPoint);
    }

    /**
     * 详细搜索
     * @return
     */
    private PoiDetailSearchOption getPoiDetailSearchOption(String poiUid) {
        PoiDetailSearchOption poiIndoorOption = new PoiDetailSearchOption();
        poiIndoorOption.poiUid(poiUid);
        return poiIndoorOption;
    }

    /**
     * 详细搜索回调函数
     * @param poiResult 详细信息分装在这个任务里面
     */
    @Override
    public void onGetPoiDetailResult(PoiDetailResult poiResult) {
        if (poiResult == null
                || poiResult.error == SearchResult.ERRORNO.RESULT_NOT_FOUND) {// 没有找到检索结果
            Toast.makeText(SearchInCityActivity.this, "未找到结果",
                    Toast.LENGTH_LONG).show();
            return;
        }
        showToast(poiResult.getName());
    }

    /**
     * 重写父类的方法
     * @param poiResult
     * @param index
     * @return
     */
    @Override
    public boolean onPoiClick(PoiResult poiResult, int index) {
        PoiInfo poiInfo = poiResult.getAllPoi().get(index);
        poiSearch.searchPoiDetail(getPoiDetailSearchOption(poiInfo.uid));
        return true;
    }

    /**
     * 普通的查找
     * @return
     */
    private PoiCitySearchOption getPoiCitySearchOption() {
        PoiCitySearchOption citySearchOption = new PoiCitySearchOption();
        citySearchOption.city("铜仁市");
        citySearchOption.keyword("松桃");
        citySearchOption.pageCapacity(10);
        citySearchOption.pageNum(pag);
        return citySearchOption;
    }



    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode==KeyEvent.KEYCODE_1){
            pag++;
        }else {
            if (pag!=0){
                pag--;
            }
        }
        poiSearch.searchInCity(getPoiCitySearchOption());
        return super.onKeyDown(keyCode, event);
    }

    /*
    关键词分类
    一级行业分类	        二级行业分类
    美食	            中餐厅、外国餐厅、小吃快餐店、蛋糕甜品店、咖啡厅、茶座、酒吧
    酒店	            星级酒店、快捷酒店、公寓式酒店
    购物	            购物中心、百货商场、超市、便利店、家居建材、家电数码、商铺、集市
    生活服务            	通讯营业厅、邮局、物流公司、售票处、洗衣店、图文快印店、照相馆、房产中介机构、公用事业、维修点、家政服务、殡葬服务、彩票销售点、宠物服务、报刊亭、公共厕所
    丽人	            美容、美发、美甲、美体
    旅游景点            	公园、动物园、植物园、游乐园、博物馆、水族馆、海滨浴场、文物古迹、教堂、风景区
    休闲娱乐            	度假村、农家院、电影院、KTV、剧院、歌舞厅、网吧、游戏场所、洗浴按摩、休闲广场
    运动健身            	体育场馆、极限运动场所、健身中心
    教育培训            	高等院校、中学、小学、幼儿园、成人教育、亲子教育、特殊教育学校、留学中介机构、科研机构、培训机构、图书馆、科技馆
    文化传媒            	新闻出版、广播电视、艺术团体、美术馆、展览馆、文化宫
    医疗	            综合医院、专科医院、诊所、药店、体检机构、疗养院、急救中心、疾控中心
    汽车服务            	汽车销售、汽车维修、汽车美容、汽车配件、汽车租赁、汽车检测场
    交通设施            	飞机场、火车站、地铁站、地铁线路、长途汽车站、公交车站、公交线路、港口、停车场、加油加气站、服务区、收费站、桥、充电站、路侧停车位
    金融	            银行、ATM、信用社、投资理财、典当行
    房地产	            写字楼、住宅区、宿舍
    公司企业            	公司、园区、农林园艺、厂矿
    政府机构            	中央机构、各级政府、行政单位、公检法机构、涉外机构、党派团体、福利机构、政治教育机构
    出入口	            高速公路出口、高速公路入口、机场出口、机场入口、车站出口、车站入口、门（备注：建筑物和建筑物群的门）、停车场出入口
    自然地物            	岛屿、山峰、水系
     */
}
