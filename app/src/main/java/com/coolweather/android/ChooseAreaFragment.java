package com.coolweather.android;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.coolweather.android.db.City;
import com.coolweather.android.db.County;
import com.coolweather.android.db.Province;
import java.util.ArrayList;
import java.util.List;

public class ChooseAreaFragment extends Fragment {
    public static final int LEVEL_PROVINCE = 0;

    public static final int LEVEL_CITY = 1;

    public static final int LEVEL_COUNTY = 2;

    private ProgressDialog progressDialog; // 进度条

    private TextView titleText; // 标题栏

    private Button backButton; // 返回按钮

    private ListView listView; // 展示列表

    private ArrayAdapter<String> adapter; // ListView的适配器

    private List<String> dataList = new ArrayList<>(); // 数据链表

    /*
    * 省列表
    * */
    private List<Province> provinceList;

    /*
    * 市列表
    * */
    private List<City> cityList;

    /*
    * 县列表
    * */
    private List<County> countyList;

    /*
    * 选中省份
    * */
    private Province selectedProvince;

    /*
    * 选中的城市
    * */
    private City selectedCity;

    /*
    * 当前选中的级别
    * */
    private int currentLevel;

    /*
    * 查询全国所有的省份，优先从数据库查询，如果数据库没有就访问服务器查询
    * */
    private void queryProvinces(){

    }

    /*
    * 查询选中省内的所有市，优先从数据库查询，如果数据库没有就去服务器查询
    * */
    private void queryCities(){

    }

    /*
    * 查询所选市内的所有县，优先从数据库查询，如果数据库没有就去服务器查询
    * */
    private void queryCounties(){

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    /*
    * 根据传入的地址和类型去服务器上查询省市县的数据
    * */
    private void queryFromServer(String address,final String type){

    }

    /*
    * 显示进度对话框
    * */
    private void showProgressDialog(){

    }

    /*
    * 关闭进度对话框
    * */
    private void closeProgressDialog(){

    }

}
