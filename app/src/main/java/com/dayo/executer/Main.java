package com.dayo.executer;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class Main {
	public static void Main(){
        String result_String = new String("");
        Date today = new Date();
        SimpleDateFormat date = new SimpleDateFormat("yyyyMMdd");

        String KEY = "d31921b2d9014e368cd685b00cea66c9"; //����Ű
        String ATPT_OFCDC_SC_CODE = "J10"; //�õ�����û�ڵ�
        String SD_SCHUL_CODE = "7530851"; //�б� �ڵ�

        try{
            Document doc = Jsoup.connect("https://open.neis.go.kr/hub/mealServiceDietInfo")
                    .data("ATPT_OFCDC_SC_CODE", ATPT_OFCDC_SC_CODE)
                    .data("SD_SCHUL_CODE", SD_SCHUL_CODE)
                    .data("MLSV_YMD" , date.format(today))
                    .data("KEY", KEY)
                    .data("TYPE","json")
                    .ignoreContentType(true).get();

            String str = doc.text();
            JSONObject jobj = new JSONObject(str);

            JSONArray mealServiceDietInfo = jobj.getJSONArray("mealServiceDietInfo");
            JSONArray head = (mealServiceDietInfo.getJSONObject(0)).getJSONArray("head");

            if(head.length()==1){
                String MESSAGE = head.getJSONObject(0).getJSONObject("RESULT").getString("MESSAGE");
            }

            int list_total_count = head.getJSONObject(0).getInt("list_total_count");

            JSONArray row = mealServiceDietInfo.getJSONObject(1).getJSONArray("row");

            for(int i=0;i<list_total_count;i++)
            {
                result_String += row.getJSONObject(i).getString("MMEAL_SC_NM") + "\n"
                               + row.getJSONObject(i).getString("DDISH_NM") + "\n\n";
            }

            System.out.print(result_String);
        }catch(Exception e){}
    }
}
