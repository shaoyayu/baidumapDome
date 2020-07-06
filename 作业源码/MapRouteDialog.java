package icu.shaoyayu.android.iearnit.dialog.map;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.baidu.location.BDLocation;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.route.BikingRouteResult;
import com.baidu.mapapi.search.route.DrivingRouteLine;
import com.baidu.mapapi.search.route.DrivingRoutePlanOption;
import com.baidu.mapapi.search.route.DrivingRouteResult;
import com.baidu.mapapi.search.route.IndoorRouteResult;
import com.baidu.mapapi.search.route.MassTransitRouteResult;
import com.baidu.mapapi.search.route.OnGetRoutePlanResultListener;
import com.baidu.mapapi.search.route.PlanNode;
import com.baidu.mapapi.search.route.RoutePlanSearch;
import com.baidu.mapapi.search.route.TransitRouteLine;
import com.baidu.mapapi.search.route.TransitRoutePlanOption;
import com.baidu.mapapi.search.route.TransitRouteResult;
import com.baidu.mapapi.search.route.WalkingRouteLine;
import com.baidu.mapapi.search.route.WalkingRoutePlanOption;
import com.baidu.mapapi.search.route.WalkingRouteResult;

import java.util.List;

import icu.shaoyayu.android.iearnit.R;
import icu.shaoyayu.android.iearnit.dialog.BottomDialog;
import icu.shaoyayu.android.iearnit.manager.FullyLinearLayoutManager;

/**
 * @author shaoyayu
 * 路线规划类
 * 驾车
 */
public class MapRouteDialog extends BottomDialog {

    private static final String TAG = "MapRouteDialog";
    private ImageView mCloseNavigation;
    private PoiInfo poiInfo;
    private BDLocation location;
    private RoutePlanSearch routePlanSearch;
    private RecyclerView mRecyclerView;
    private RouteSelectionCallback routeSelectionCallback;
    //保存三个搜索结果
    private List<WalkingRouteLine> walkingRouteLines = null; //步行 walk
    private List<TransitRouteLine> transitRouteLines = null; //公交 bus
    private List<DrivingRouteLine> drivingRouteLines = null; //自驾 self_driving

    public MapRouteDialog(PoiInfo poiInfo,BDLocation location,RouteSelectionCallback routeSelectionCallback){
        this.poiInfo = poiInfo;
        this.location = location;
        this.routeSelectionCallback = routeSelectionCallback;
    }

    @Override
    protected View createView(LayoutInflater inflater, ViewGroup container) {
        if (poiInfo!=null&&location!=null){
            //自驾导航
            routePlanSearch = RoutePlanSearch.newInstance();
            routePlanSearch.setOnGetRoutePlanResultListener(new PathOnGetRoutePlanResultListener());
            routePlanSearch.drivingSearch(getDrivingRoutePlanOption());
            routePlanSearch.transitSearch(getTransitRoutePlanOption());
            routePlanSearch.walkingSearch(getWalkingRoutePlanOption());
        }
        View view = inflater.inflate(R.layout.dialog_map_route,container,false);
        mCloseNavigation = view.findViewById(R.id.iv_map_navigation_dog_close);
        mRecyclerView = view.findViewById(R.id.rv_navigation_type);
        mCloseNavigation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onStop();
            }
        });
        //给ViewPage注册适配器
        return view;
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    private void initRecyclerViewAdapter(){
        if (walkingRouteLines!=null && transitRouteLines!=null && drivingRouteLines!=null){
            mRecyclerView.setAdapter(new NavigationAdapter());
            FullyLinearLayoutManager mLinearLayoutManager = new FullyLinearLayoutManager(getContext());
            mRecyclerView.setLayoutManager(mLinearLayoutManager);
            //更新适配器
//            mRecyclerView.swapAdapter();
        }
    }

    /**
     * 驾车导航
     * @return
     */
    private DrivingRoutePlanOption getDrivingRoutePlanOption() {
        DrivingRoutePlanOption drivingRoutePlanOption = new DrivingRoutePlanOption();
        drivingRoutePlanOption.from(PlanNode.withLocation(new LatLng(location.getLatitude(),location.getLongitude())));
        drivingRoutePlanOption.to(PlanNode.withLocation(poiInfo.location));
        return drivingRoutePlanOption;
    }

    /**
     * 公交导航
     * @return
     */
    private TransitRoutePlanOption getTransitRoutePlanOption() {
        TransitRoutePlanOption transitRoutePlanOption = new TransitRoutePlanOption();
//        transitRoutePlanOption.city(location.getCity());
        transitRoutePlanOption.city("荆州");
        transitRoutePlanOption.from(PlanNode.withLocation(new LatLng(location.getLatitude(),location.getLongitude())));
        transitRoutePlanOption.to(PlanNode.withLocation(poiInfo.location));
        //路线选型
        /*
        EBUS_NO_SUBWAY
            公交检索策略常量：不含地铁
            EBUS_TIME_FIRST
            公交检索策略常量：时间优先
            EBUS_TRANSFER_FIRST
            公交检索策略常量：最少换乘
            EBUS_WALK_FIRST
            公交检索策略常量：最少步行距离
         */
        transitRoutePlanOption.policy(TransitRoutePlanOption.TransitPolicy.EBUS_WALK_FIRST);
        return transitRoutePlanOption;
    }

    /**
     * 步行导航
     * @return
     */
    private WalkingRoutePlanOption getWalkingRoutePlanOption() {
        WalkingRoutePlanOption walkingRoutePlanOption = new WalkingRoutePlanOption();
        walkingRoutePlanOption.from(PlanNode.withLocation(new LatLng(location.getLatitude(),location.getLongitude())));
        walkingRoutePlanOption.to(PlanNode.withLocation(poiInfo.location));
        return walkingRoutePlanOption;
    }

    //规划路线回调
    class PathOnGetRoutePlanResultListener implements OnGetRoutePlanResultListener{

        /**
         * 不行路线回调
         * @param walkingRouteResult
         */
        @Override
        public void onGetWalkingRouteResult(WalkingRouteResult walkingRouteResult) {
//            mParcelableRouteLines.add(walkingRouteResult.getRouteLines());
            walkingRouteLines = walkingRouteResult.getRouteLines();
            for (int i = 0; i < walkingRouteLines.size(); i++) {
                Log.d(TAG,i+"步行路线耗时："+walkingRouteLines.get(i).getDuration()/60+"分钟");
                List<WalkingRouteLine.WalkingStep> allStep = walkingRouteLines.get(i).getAllStep();
                for (int j = 0; j < allStep.size(); j++) {
                    Log.d(TAG,i+","+j+",步行路线:"+allStep.get(j).getInstructions());
                }
            }
            initRecyclerViewAdapter();
        }

        @Override
        public void onGetTransitRouteResult(TransitRouteResult transitRouteResult) {
            transitRouteLines = transitRouteResult.getRouteLines();
            for (int i = 0; i < transitRouteLines.size(); i++) {
                Log.d(TAG,i+"公交路线："+transitRouteLines.get(i).getDuration()/60+"分钟");
                List<TransitRouteLine.TransitStep> allStep = transitRouteLines.get(i).getAllStep();
                for (int j = 0; j < allStep.size(); j++) {
                    Log.d(TAG,i+","+j+",公交路线:"+allStep.get(j).getInstructions());
                }
            }
            initRecyclerViewAdapter();
        }

        @Override
        public void onGetMassTransitRouteResult(MassTransitRouteResult massTransitRouteResult) {

        }

        @Override
        public void onGetDrivingRouteResult(DrivingRouteResult drivingRouteResult) {
            //所有选择方案
            drivingRouteLines = drivingRouteResult.getRouteLines();
            for (int i = 0; i < drivingRouteLines.size(); i++) {
                Log.d(TAG,i+"自驾："+drivingRouteLines.get(i).getDuration()/60+"分钟");
                DrivingRouteLine drivingRouteLine = drivingRouteLines.get(i);
                List<DrivingRouteLine.DrivingStep> allStep = drivingRouteLine.getAllStep();
                for (int j = 0; j < allStep.size(); j++) {
                    Log.d(TAG,i+","+j+",自驾:"+allStep.get(j).getInstructions());
                }
            }
            initRecyclerViewAdapter();
        }

        @Override
        public void onGetIndoorRouteResult(IndoorRouteResult indoorRouteResult) {

        }

        @Override
        public void onGetBikingRouteResult(BikingRouteResult bikingRouteResult) {

        }
    }


    /**
     * 适配器，需要在填充的时候填写数据
     */
    class NavigationAdapter extends RecyclerView.Adapter<MyRouteDetails>{

        @Override
        public int getItemViewType(int position) {
            return position;
        }

        @NonNull
        @Override
        public MyRouteDetails onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            View view = inflater.inflate(R.layout.view_navigation_route,parent,false);
            return new MyRouteDetails(view);
        }

        @Override
        public void onBindViewHolder(@NonNull MyRouteDetails holder, int position) {
            if (position<walkingRouteLines.size()){
                holder.routeType.setText("步行路线："+(position+1));
                holder.timeConsumingRoute.setText("耗时："+walkingRouteLines.get(position).getDuration()/60+" Minute");
                holder.startNavigation.setImageResource(R.drawable.ic_walk_foreground);
                String content = "";
                List<WalkingRouteLine.WalkingStep> allStep = walkingRouteLines.get(position).getAllStep();
                for (int j = 0; j < allStep.size(); j++) {
                    content = content + allStep.get(j).getInstructions()+"\n";
                }
                holder.routeDetails.setText(content);
                //设置一个点击事件，回调
                final int finalPosition = position;
                holder.startNavigation.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onStop();
                        routeSelectionCallback.walkingRoute(walkingRouteLines.get(finalPosition));
                    }
                });
            }else if (walkingRouteLines.size()<=position && position<(walkingRouteLines.size()+transitRouteLines.size())){
                position = position - walkingRouteLines.size();
                holder.routeType.setText("公交路线："+(position+1));
                holder.timeConsumingRoute.setText("耗时："+transitRouteLines.get(position).getDuration()/60+" Minute");
                holder.startNavigation.setImageResource(R.drawable.ic_bus_foreground);
                String content = "";
                List<TransitRouteLine.TransitStep> allStep = transitRouteLines.get(position).getAllStep();
                for (int j = 0; j < allStep.size(); j++) {
                    content = content + allStep.get(j).getInstructions()+"\n";
                }
                holder.routeDetails.setText(content);
                final int finalPosition = position;
                holder.startNavigation.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onStop();
                        routeSelectionCallback.busRoutes(transitRouteLines.get(finalPosition));
                    }
                });
            }else{
                position = position - (walkingRouteLines.size()+transitRouteLines.size());
                holder.routeType.setText("驾车路线："+(position+1));
                holder.timeConsumingRoute.setText("耗时："+drivingRouteLines.get(position).getDuration()/60+" Minute");
                holder.startNavigation.setImageResource(R.drawable.ic_self_driving_foreground);
                String content = "";
                DrivingRouteLine drivingRouteLine = drivingRouteLines.get(position);
                List<DrivingRouteLine.DrivingStep> allStep = drivingRouteLine.getAllStep();
                for (int j = 0; j < allStep.size(); j++) {
                    content = content + allStep.get(j).getInstructions()+"\n";
                }
                holder.routeDetails.setText(content);
                final int finalPosition = position;
                holder.startNavigation.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onStop();
                        routeSelectionCallback.drivingDirections(drivingRouteLines.get(finalPosition));
                    }
                });
            }
        }

        @Override
        public int getItemCount() {
            int size = 0;
            if (walkingRouteLines!=null && walkingRouteLines.size()!=0){
                size = size +walkingRouteLines.size();
            }
            if (transitRouteLines!=null && transitRouteLines.size()!=0){
                size = size + transitRouteLines.size();
            }
            if (drivingRouteLines!=null && drivingRouteLines.size()!=0){
                size = size +drivingRouteLines.size();
            }
            return size;
        }
    }

    class MyRouteDetails extends RecyclerView.ViewHolder {
        //四个控件
        TextView routeType,timeConsumingRoute,routeDetails;
        ImageView startNavigation;
        public MyRouteDetails(@NonNull View itemView) {
            super(itemView);
            routeType = itemView.findViewById(R.id.tv_route_type);
            timeConsumingRoute = itemView.findViewById(R.id.tv_time_consuming_route);
            routeDetails = itemView.findViewById(R.id.tv_details);
            startNavigation = itemView.findViewById(R.id.iv_start_navigation);
        }
    }

    public interface RouteSelectionCallback{
        //步行路线规划
        void walkingRoute(WalkingRouteLine walkingRouteLine);
        //换车路线规划
        void busRoutes(TransitRouteLine transitRouteLine );
        //驾车路线规划
        void drivingDirections(DrivingRouteLine drivingRouteLine);
    }

}
