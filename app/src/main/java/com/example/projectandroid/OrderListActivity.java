package com.example.projectandroid;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class OrderListActivity extends AppCompatActivity {

    String id, password, name, phone;
    private SharedPreferences appData;

    LinearLayout layout;
    Context context;

    JSONObject requestData = new JSONObject();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_list);

        appData = getSharedPreferences("appData", MODE_PRIVATE);
        load();

        layout = (LinearLayout)findViewById(R.id.layout);
        context=this;

        try {
            requestData.accumulate("id", id);
            Log.d("requestData", requestData.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        new postTask().execute("http://192.168.64.157:8080/biz/order/list.do");
    }

    class postTask extends AsyncTask<String, String, String> {


        @Override
        protected String doInBackground(String... urls) {
            try {

                HttpURLConnection con = null;
                BufferedReader reader = null;

                try {
                    URL url = new URL(urls[0]);
                    //연결을 함
                    con = (HttpURLConnection) url.openConnection();

                    con.setRequestMethod("POST");//POST방식으로 보냄
//                    con.setRequestProperty("Cache-Control", "no-cache");//캐시 설정
                    con.setRequestProperty("Content-Type", "application/json");//application JSON 형식으로 전송
                    con.setDoOutput(true);//Outstream으로 post 데이터를 넘겨주겠다는 의미
                    con.setDoInput(true);//Inputstream으로 서버로부터 응답을 받겠다는 의미
                    con.connect();
                    Log.d("requestData", "1");
                    //서버로 보내기위해서 스트림 만듬
                    OutputStream outStream = con.getOutputStream();
                    //버퍼를 생성하고 넣음
                    BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outStream));
                    writer.write(requestData.toString());
                    writer.flush();
                    writer.close();//버퍼를 받아줌
                    Log.d("requestData", "2");
                    //서버로 부터 데이터를 받음
                    InputStream stream = con.getInputStream();

                    reader = new BufferedReader(new InputStreamReader(stream));

                    StringBuffer buffer = new StringBuffer();

                    String line = "";
                    while ((line = reader.readLine()) != null) {
                        buffer.append(line);
                    }
                    Log.d("requestData", "3");
                    return buffer.toString();//서버로 부터 받은 값을 리턴해줌 아마 OK!!가 들어올것임

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    if (con != null) {
                        con.disconnect();
                    }
                    try {
                        if (reader != null) {
                            reader.close();//버퍼를 닫아줌
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            Log.d("postData", result);
            try {
                JSONArray jArrObject = new JSONArray(result);
                int list_count = jArrObject.length();
                int[] orderNum = new int[list_count];
                String[] orderTime = new String[list_count];
                String[] orderRequest = new String[list_count];
                String[] orderState = new String[list_count];
                int[] orderTotalPrice = new int[list_count];
                int[] detailCount = new int[list_count];
                String[] shopName = new String[list_count];
                String[] menuName = new String[list_count];
                int[] menuPrice = new int[list_count];

                ArrayList<String> arrayList = new ArrayList<String>();
                ArrayList<String> arrayList2 = new ArrayList<String>();

                for (int i = 0; i < list_count; i++) {
                    JSONObject jsonObject = jArrObject.getJSONObject(i);
                    orderNum[i] = jsonObject.getInt("orderNum");
                    orderTime[i] = jsonObject.getString("orderTime");
                    orderRequest[i] = jsonObject.getString("orderRequest");
                    orderState[i] = jsonObject.getString("orderState");
                    orderTotalPrice[i] = jsonObject.getInt("orderTotalPrice");
                    detailCount[i] = jsonObject.getInt("detailCount");
                    shopName[i] = jsonObject.getString("shopName");
                    menuName[i] = jsonObject.getString("menuName");
                    menuPrice[i] = jsonObject.getInt("menuPrice");
                }

                for(String time : orderTime) {
                    if(!arrayList.contains(time)) {
                        arrayList.add(time);
                    }
                }

                for(String list : arrayList){
                    TextView view1 = new TextView(context);
                    view1.setText(list);
                    for(int j = 0;j<list_count;j++){
                        if(list.equals(orderTime[j])){
                            view1.append("\n주문번호 : "+orderNum[j]+" 주문 상태 : "+orderState[j]);
                            break;
                        }
                    }
                    layout.addView(view1);
                    for(int j = 0;j<list_count;j++){
                        if(list.equals(orderTime[j])){
                            TextView text = new TextView(context);
                            text.setText("메뉴명 : "+menuName[j]+"\n수량 : " + detailCount[j] +"\n가격 : " +(menuPrice[j]*detailCount[j]));
                            layout.addView(text);
                        }
                    }
                    TextView text2 = new TextView(context);
                    text2.setText("");
                    layout.addView(text2);
                }


            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    // 설정값을 불러오는 함수
    private void load() {
        // SharedPreferences 객체.get타입( 저장된 이름, 기본값 )
        // 저장된 이름이 존재하지 않을 시 기본값
        id = appData.getString("id", "");
        password = appData.getString("password", "");
        name = appData.getString("name","");
        phone = appData.getString("phone", "");
    }
}
