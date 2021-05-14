package com.dayo.executer.data;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class MealData {
    public static String Result;
    public static String Result_date = "0";
    public static String getMealData() {
        new JsoupAsyncTask().execute();
        return Result;
    }

    private static class JsoupAsyncTask extends AsyncTask<Void, Void, Void> {
        String result_String = new String("");
        Date today = new Date();
        SimpleDateFormat date = new SimpleDateFormat("yyyyMMdd");

        String KEY = "d31921b2d9014e368cd685b00cea66c9"; //인증키
        String ATPT_OFCDC_SC_CODE = "J10"; //시도교육청코드
        String SD_SCHUL_CODE = "7530851"; //학교 코드

        protected void onPreExecute() {
            super.onPreExecute();
        }
        protected Void doInBackground(Void... params) {
            if(Result_date!=date.format(today)) {
                try {
                    Document doc = Jsoup.connect("https://open.neis.go.kr/hub/mealServiceDietInfo")
                            .data("ATPT_OFCDC_SC_CODE", ATPT_OFCDC_SC_CODE)
                            .data("SD_SCHUL_CODE", SD_SCHUL_CODE)
                            .data("MLSV_YMD", date.format(today))
                            .data("KEY", KEY)
                            .data("TYPE", "json")
                            .ignoreContentType(true).get();

                    Log.d("Meal", date.format(today));
                    String str = doc.text();
                    JSONObject jobj = new JSONObject(str);

                    JSONArray mealServiceDietInfo = jobj.getJSONArray("mealServiceDietInfo");
                    JSONArray head = (mealServiceDietInfo.getJSONObject(0)).getJSONArray("head");

                    int list_total_count = head.getJSONObject(0).getInt("list_total_count");

                    JSONArray row = mealServiceDietInfo.getJSONObject(1).getJSONArray("row");

                    for (int i = 0; i < list_total_count; i++) {
                        result_String += row.getJSONObject(i).getString("MMEAL_SC_NM") + "\n"
                                + row.getJSONObject(i).getString("DDISH_NM") + "\n\n";
                    }
                } catch (Exception e) {
                    result_String = "급식이 없습니다.";
                    e.printStackTrace();
                }
            }
            return null;
        }
        @Override
        protected void onPostExecute(Void result) {
            Result = result_String;
            Result_date = date.format(today);
        }
    }
}
