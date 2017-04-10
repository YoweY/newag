package com.info.aegis.lawpush4android.utils.baidu_location;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.Poi;
import com.info.aegis.lawpush4android.LawPushApp;
import com.info.aegis.lawpush4android.utils.MyLog;
import com.info.aegis.lawpush4android.utils.MyToast;

import java.util.List;

/**
 * Created by SFS on 2017/2/16.
 * Description :
 */
public class MyLocationListener implements BDLocationListener {

    private LocationClient mLocationClient;
    private Context context;

    public MyLocationListener(Context context, LocationClient mLocationClient) {
        this.mLocationClient = mLocationClient;
        this.context = context;
    }

    @Override
    public void onReceiveLocation(BDLocation location) {

        //获取定位结果
        StringBuffer sb = new StringBuffer(256);

        sb.append("time : ");
        sb.append(location.getTime());    //获取定位时间

        sb.append("\nerror code : ");
        sb.append(location.getLocType());    //获取类型类型

        sb.append("\nlatitude : ");
        sb.append(location.getLatitude());    //获取纬度信息

        sb.append("\nlontitude : ");
        sb.append(location.getLongitude());    //获取经度信息

        sb.append("\nradius : ");
        sb.append(location.getRadius());    //获取定位精准度

        if (location.getLocType() == BDLocation.TypeGpsLocation) {

            // GPS定位结果
            sb.append("\nspeed : ");
            sb.append(location.getSpeed());    // 单位：公里每小时

            sb.append("\nsatellite : ");
            sb.append(location.getSatelliteNumber());    //获取卫星数

            sb.append("\nheight : ");
            sb.append(location.getAltitude());    //获取海拔高度信息，单位米

            sb.append("\ndirection : ");
            sb.append(location.getDirection());    //获取方向信息，单位度

            sb.append("\naddr : ");
            sb.append(location.getAddrStr());    //获取地址信息

            sb.append("\ndescribe : ");
            sb.append("gps定位成功");

        } else if (location.getLocType() == BDLocation.TypeNetWorkLocation) {

            // 网络定位结果
            sb.append("\naddr : ");
            sb.append(location.getAddrStr());    //获取地址信息

            sb.append("\noperationers : ");
            sb.append(location.getOperators());    //获取运营商信息

            sb.append("\ndescribe : ");
            sb.append("网络定位成功");

        } else if (location.getLocType() == BDLocation.TypeOffLineLocation) {

            // 离线定位结果
            sb.append("\ndescribe : ");
            sb.append("离线定位成功，离线定位结果也是有效的");

        } else if (location.getLocType() == BDLocation.TypeServerError) {

            sb.append("\ndescribe : ");
            sb.append("服务端网络定位失败，可以反馈IMEI号和大体定位时间到loc-bugs@baidu.com，会有人追查原因");

        } else if (location.getLocType() == BDLocation.TypeNetWorkException) {

            sb.append("\ndescribe : ");
            sb.append("网络不同导致定位失败，请检查网络是否通畅");

        } else if (location.getLocType() == BDLocation.TypeCriteriaException) {

            sb.append("\ndescribe : ");
            sb.append("无法获取有效定位依据导致定位失败，一般是由于手机的原因，处于飞行模式下一般会造成这种结果，可以试着重启手机");

        }

        sb.append("\nlocationdescribe : ");
        sb.append(location.getLocationDescribe());    //位置语义化信息

        List<Poi> list = location.getPoiList();    // POI数据
        if (list != null) {
            sb.append("\npoilist size = : ");
            sb.append(list.size());
            for (Poi p : list) {
                sb.append("\npoi= : ");
                sb.append(p.getId() + " " + p.getName() + " " + p.getRank());
            }
        }
            // TODO: 2017/2/16   离线定位
        MyLog.e("lawPush", sb.toString());
        String addressString = location.getTime();
        if (!addressString.equals(" null")){
            SharedPreferences sharedPreferences = context.getSharedPreferences("robbotSP",Context.MODE_PRIVATE);

            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("location", addressString);
            editor.commit();

            if (mLocationClient != null){
                MyLog.e("lawPush", "location    停止监听");

                mLocationClient.stop();
                mLocationClient.unRegisterLocationListener(this);
            }else {
                MyLog.e("lawPush", "        mLocationClient == null");
            }
        }else {
            if (mLocationClient != null){
                MyLog.e("lawPush", "location    停止监听");
                mLocationClient.stop();
                mLocationClient.unRegisterLocationListener(this);
            }else {
                MyLog.e("lawPush", "        mLocationClient == null");
            }
        }
    }

    @Override
    public void onConnectHotSpotMessage(String s, int i) {

    }
}
