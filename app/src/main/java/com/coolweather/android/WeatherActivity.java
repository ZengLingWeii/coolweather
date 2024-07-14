package com.coolweather.android;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import com.coolweather.android.gson.Forecast;
import com.coolweather.android.gson.Weather;
import com.coolweather.android.util.HttpUtil;
import com.coolweather.android.util.Utility;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class WeatherActivity extends AppCompatActivity {

    private ImageView bingPicImg;   /*背景图片*/

    private ScrollView weatherLayout;  /*滚动视图*/

    private TextView titleCity;     /*城市名称*/

    private TextView titleUpdateTime;   /*更新时间*/

    private TextView degreeText;    /*温度*/

    private TextView weatherInfoText;  /*晴、阴、雨*/

    private LinearLayout forecastLayout;    /*一周天气预告的布局*/

    private TextView aqiText;   /*空气质量-AQI指标*/

    private TextView pm25Text;  /*空气质量-PM2.5指标*/

    private TextView comfortText;   /*suggestion-1*/

    private TextView carWashText;   /*suggestion-2*/

    private TextView sportText;     /*suggestion-3*/

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //背景图和状态栏的融合
        if (Build.VERSION.SDK_INT > 21){
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
        setContentView(R.layout.activity_weather);

        //获取各个控件
        weatherLayout = (ScrollView) findViewById(R.id.weather_layout);
        titleCity = (TextView) findViewById(R.id.title_city);
        titleUpdateTime = (TextView)findViewById(R.id.title_update_time);
        degreeText = (TextView) findViewById(R.id.degree_text);
        weatherInfoText = (TextView) findViewById(R.id.weather_info_text);
        forecastLayout = (LinearLayout) findViewById(R.id.forecast_layout);
        aqiText = (TextView) findViewById(R.id.aqi_text);
        pm25Text = (TextView) findViewById(R.id.pm25_text);
        comfortText = (TextView) findViewById(R.id.comfort_text);
        carWashText = (TextView) findViewById(R.id.car_wash_text);
        sportText = (TextView) findViewById(R.id.sport_text);
        bingPicImg = (ImageView)findViewById(R.id.bing_pic_img);

        /*key-value的存储机制，用于存储信息*/
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String weatherString = prefs.getString("weather", null);
        String bingPic = prefs.getString("bing_pic",null);
        if (bingPic != null) {
            Glide.with(WeatherActivity.this).load(bingPic).into(bingPicImg);
        } else {
            loadBingPic();
        }

        if (weatherString != null){
            //有缓存时直接解析天气数据
            Weather weather = Utility.handleWeatherResponse(weatherString);
            showWeatherInfo(weather);
        } else {
            //没有缓存信息，去服务器获取信息
            String weatherId = getIntent().getStringExtra("weather_id"); /*获取Activity的Intent内容，也能传递*/
            weatherLayout.setVisibility(View.INVISIBLE);
            requestWeather(weatherId);
        }
    }

    private void loadBingPic() {
        String requestBingPic = "https://cn.bing.com/HPImageArchive.aspx?format=js&idx=0&n=1";;
        HttpUtil.sendOkHttpRequest(requestBingPic, new Callback() {
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String bingPic = response.body().string();

                try {
                    //获取的是一个images集合
                    JSONArray jsonArray = new JSONObject(bingPic).getJSONArray("images");
                    //遍历找到url字符串
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        String url = jsonObject.getString("url");
                        bingPic = "http://cn.bing.com" + url;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this).edit();
                editor.putString("bing_pic", bingPic);
                editor.apply();
                String finalBingPic = bingPic;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Glide.with(WeatherActivity.this).load(finalBingPic).into(bingPicImg);
                    }
                });
            }
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }
        });
    }

    /*处理信息并展示Weather实体类中的信息*/
    private void showWeatherInfo(Weather weather) {
        String cityName = weather.basic.cityName;
        String updateTime = weather.basic.update.updateTime.split(" ")[1];
        String degree = weather.now.temperature + "℃";
        String weatherInfo = weather.now.more.info;
        titleCity.setText(cityName);
        titleUpdateTime.setText(updateTime);
        degreeText.setText(degree);
        weatherInfoText.setText(weatherInfo);
        if(weather.aqi!=null){
            aqiText.setText(weather.aqi.city.aqi);
            pm25Text.setText(weather.aqi.city.pm25);
        }
        String comfort="舒适度："+weather.suggestion.comfort.info;
        String carWash="洗车指数："+weather.suggestion.carWash.info;
        String sport="运动建议："+weather.suggestion.sport.info;
        comfortText.setText(comfort);
        carWashText.setText(carWash);
        sportText.setText(sport);
        weatherLayout.setVisibility(View.VISIBLE);

        forecastLayout.removeAllViews();
        for (Forecast forecast : weather.forecastList) {
            View view = LayoutInflater.from(this).inflate(R.layout.forecast_item, forecastLayout, false);
            TextView dateText = (TextView) view.findViewById(R.id.date_text);
            TextView infoText = (TextView) view.findViewById(R.id.info_text);
            TextView maxText = (TextView) view.findViewById(R.id.max_text);
            TextView minText = (TextView) view.findViewById(R.id.min_text);
            dateText.setText(forecast.date);
            infoText.setText(forecast.more.info);
            maxText.setText(forecast.temperature.max);
            minText.setText(forecast.temperature.min);
            forecastLayout.addView(view);
        }
    }

    /*根据天气的Id请求天气信息*/
    private void requestWeather(final String weatherId) {
        String weatherUrl = "http://guolin.tech/api/weather?cityid="+weatherId;
        HttpUtil.sendOkHttpRequest(weatherUrl, new Callback() {

            @Override
            public void onResponse(Call call, Response response) throws IOException {
               final String responseText = response.body().string();
               final Weather weather = Utility.handleWeatherResponse(responseText);
               runOnUiThread(new Runnable() {
                   @Override
                   public void run() {
                        if (weather != null && "ok".equals(weather.status)){
                            SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this).edit();
                            editor.putString("weather",responseText);
                            editor.apply();
                            showWeatherInfo(weather);
                        } else {
                            Toast.makeText(WeatherActivity.this,"获取天气信息失败", Toast.LENGTH_SHORT).show();
                        }
                   }
               });
            }

            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(WeatherActivity.this,"获取天气信息失败", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
        loadBingPic();
    }
}
