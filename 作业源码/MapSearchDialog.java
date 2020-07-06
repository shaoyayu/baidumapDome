package icu.shaoyayu.android.iearnit.dialog.map;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.baidu.location.BDLocation;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiCitySearchOption;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiDetailSearchResult;
import com.baidu.mapapi.search.poi.PoiIndoorResult;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiSearch;
import com.baidu.mapapi.utils.DistanceUtil;

import java.util.List;

import icu.shaoyayu.android.iearnit.R;
import icu.shaoyayu.android.iearnit.dialog.BottomDialog;
import icu.shaoyayu.android.iearnit.manager.FullyLinearLayoutManager;

import static android.content.Context.INPUT_METHOD_SERVICE;

/**
 * @author shaoyayu
 * 地图搜索对话框
 */
public class MapSearchDialog extends BottomDialog {

    private static final String  TAG = "MapSearchDialog";
    PoiSearch mPoiSearch = PoiSearch.newInstance();
    //当前的我一个位置
    private RecyclerView mShowSearchResults;
    private BDLocation location;
    private EditText mEnterLocation;
    private ImageView mCloseSession;
    private Button mFindALocation;
    //
    private POICallback poiCallback;

    public MapSearchDialog(BDLocation location,POICallback poiCallback){
        this.location = location;
        this.poiCallback = poiCallback;
    }

    @Override
    protected View createView(LayoutInflater inflater, ViewGroup container) {
        View view = inflater.inflate(R.layout.dialog_map_search,container,false);
        mEnterLocation = view.findViewById(R.id.et_map_location_search);
        mCloseSession = view.findViewById(R.id.iv_map_search_dog_close);
        mFindALocation = view.findViewById(R.id.bt_find_location);
        mShowSearchResults = view.findViewById(R.id.rv_map_search_results);

        mCloseSession.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onStop();
            }
        });
        mFindALocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //移除输入的焦点
                mEnterLocation.clearFocus();//取消焦点
                InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(mEnterLocation.getWindowToken(), 0);
                String findContent = mEnterLocation.getText().toString().trim();
                Log.d(TAG,"搜索内容："+findContent);
                mPoiSearch.setOnGetPoiSearchResultListener(SearchMonitor);
//                Log.d(TAG,"当前城市："+location.getCity());
                PoiCitySearchOption citySearchOption = new PoiCitySearchOption();
//                citySearchOption.city(location.getCity());
                //测试使用
                citySearchOption.city("荆州");
                citySearchOption.keyword(findContent);
                citySearchOption.pageCapacity(100);
                citySearchOption.pageNum(0);
                mPoiSearch.searchInCity(citySearchOption);
            }
        });
        return view;
    }

    @Override
    public void onStop() {
        //回收资源，适配器等
        super.onStop();
        //关闭的时候释放资源
        mPoiSearch.destroy();
    }

    public interface SearchResultCallback{
        void showChooseALocation();
        void showReachLocation();
    }

    /**
     * 搜索回调的监听函数
     */
    OnGetPoiSearchResultListener SearchMonitor = new OnGetPoiSearchResultListener() {
        @Override
        public void onGetPoiResult(PoiResult poiResult) {
            if (poiResult == null
                    || poiResult.error == SearchResult.ERRORNO.RESULT_NOT_FOUND) {// 没有找到检索结果
                Log.d(TAG,"为能找到搜索结果");
                Toast.makeText(getContext(), "没有搜索结果", Toast.LENGTH_SHORT).show();
                return;
            }
            if (poiResult.error == SearchResult.ERRORNO.NO_ERROR) {// 检索结果正常返回
                List<PoiInfo> allPoi = poiResult.getAllPoi();
                for (PoiInfo poiInfo : allPoi) {
                    Log.d(TAG,poiInfo.name);
                }
                //Sequence
                mShowSearchResults.setAdapter(new ShowSearchDataAdapter(allPoi,poiCallback));
                FullyLinearLayoutManager mLinearLayoutManager = new FullyLinearLayoutManager(getContext());
                mShowSearchResults.setLayoutManager(mLinearLayoutManager);
            }
            //调用初始化参数显示
        }
        @Override
        public void onGetPoiDetailResult(PoiDetailSearchResult poiDetailSearchResult) {

        }
        @Override
        public void onGetPoiIndoorResult(PoiIndoorResult poiIndoorResult) {

        }
        //废弃
        @Override
        public void onGetPoiDetailResult(PoiDetailResult poiDetailResult) {

        }
    };


    //适配器

    class ShowSearchDataAdapter extends RecyclerView.Adapter<MyPolicyInfo>{

        List<PoiInfo> policyInfos;
        POICallback mPoiCallback;
        public ShowSearchDataAdapter(List<PoiInfo> policyInfos,POICallback poiCallback){
            this.policyInfos = policyInfos;
            this.mPoiCallback = poiCallback;
        }

        @Override
        public int getItemViewType(int position) {
            return position;
        }

        @NonNull
        @Override
        public MyPolicyInfo onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            View view = inflater.inflate(R.layout.view_search_results_item,parent,false);
            return new MyPolicyInfo(view);
        }

        @Override
        public void onBindViewHolder(@NonNull MyPolicyInfo holder, final int position) {
            holder.poiName.setText( policyInfos.get(position).name);
            LatLng current = new LatLng(location.getLatitude(),location.getLongitude());
            final LatLng poiPlace = policyInfos.get(position).getLocation();
            holder.poiDistance.setText(DistanceUtil.getDistance(current,poiPlace)+"米");
            holder.poiDetail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //设置一个新的位置，然后关闭当前的弹窗
                    onStop();
                    mPoiCallback.callbackForDetails(v,policyInfos.get(position));
                }
            });
            holder.poiNavigate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //导航
                    onStop();
                    mPoiCallback.navigationPositionCallback(v,policyInfos.get(position));
                }
            });
        }
        @Override
        public int getItemCount() {
            return policyInfos.size();
        }
    }
    class MyPolicyInfo extends RecyclerView.ViewHolder {
        //四个控件
        TextView poiName;
        TextView poiDistance;
        Button poiDetail;
        Button poiNavigate;
        public MyPolicyInfo(@NonNull View itemView) {
            super(itemView);
            //实例化参数
            poiName = itemView.findViewById(R.id.tv_poi_name);
            poiDistance = itemView.findViewById(R.id.tv_poi_distance);
            poiDetail = itemView.findViewById(R.id.bt_poi_detail);
            poiNavigate = itemView.findViewById(R.id.bt_poi_navigate);
            //注册一个点击事件
        }
    }


    public interface POICallback {

        /**
         * 显示搜索结果
         * @param poiInfos
         */
        void showSearchResults(List<PoiInfo> poiInfos);

        /**
         * 详情位置回调
         * @param view
         * @param poiInfo
         */
        void callbackForDetails(View view, PoiInfo poiInfo);


        /**
         * 导航位置回调
         * @param view
         * @param poiInfo
         */
        void navigationPositionCallback(View view, PoiInfo poiInfo);

    }

}
