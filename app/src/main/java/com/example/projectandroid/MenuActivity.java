package com.example.projectandroid;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class MenuActivity extends AppCompatActivity {

    String id, password, name, phone;
    private SharedPreferences appData;

    String businessNum,shopName;
    JSONObject requestData = new JSONObject();

    LinearLayout layout;
    Context context;
    TextView hits;


    int topMenuNum1;
    int topMenuNum2;
    int topMenuNum3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        appData = getSharedPreferences("appData", MODE_PRIVATE);
        load();

        layout = (LinearLayout)findViewById(R.id.layout);
        hits=(TextView)findViewById(R.id.hits);
        context=this;



        Intent intent = getIntent();
        businessNum = intent.getExtras().getString("businessNum");
        shopName = intent.getExtras().getString("shopName");

        try {
            requestData.accumulate("businessNum", businessNum);
            Log.d("requestData", requestData.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        TextView view0 = new TextView(context);
        view0.setText(shopName);
        view0.setTextSize(70);
        view0.setTextColor(Color.parseColor("#000000"));
        view0.setGravity(Gravity.CENTER_VERTICAL|Gravity.CENTER_HORIZONTAL);
        layout.addView(view0);


        new postTask3().execute("http://192.168.64.157:8080/biz/user/preMenuList.do");

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
                    String[] hitsTime = new String[list_count];
                    String[] businessNum = new String[list_count];
                    int[] menuNum = new int[list_count];
                    int[] menuCountNum = new int[list_count];
                    String[] menuCountName = new String[list_count];
                    int[] menuCount = new int[list_count];

                    String[] menuName = new String[list_count];
                    int count = 0;
                    int nowTime;
                    int getTime;

                    ArrayList<Integer> arrayList = new ArrayList<Integer>();

                    long now = System.currentTimeMillis();
                    Date mDate = new Date(now);
                    SimpleDateFormat simpleDate = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                    String time1 = simpleDate.format(mDate);
                    nowTime = Integer.parseInt(time1.substring(11, 13));
                    for (int i = 0; i < list_count; i++) {
                        JSONObject jsonObject = jArrObject.getJSONObject(i);
                        hitsTime[i] = jsonObject.getString("hitsTime");
                        businessNum[i] = jsonObject.getString("businessNum");
                        menuNum[i] = jsonObject.getInt("menuNum");
                        menuName[i] = jsonObject.getString("menuName");
                        getTime = Integer.parseInt(hitsTime[i].substring(11, 13));
                        if (time1.substring(0, 10).equals(hitsTime[i].substring(0, 10))) {
                            if (nowTime - getTime <= 3) {
                                for (int a = 0; a < count + 1; a++) {
                                    if (menuCountNum[a] == menuNum[i]) {
                                        menuCount[a]++;
                                        break;
                                    } else {
                                        if (a == count) {
                                            menuCountNum[count] = menuNum[i];
                                            menuCountName[count] = menuName[i];
                                            menuCount[count] = 1;
                                            count++;
                                            break;
                                        }
                                    }
                                }
                            }
                        }

                    }

                    int empty;
                    Sempty sempty = new Sempty();




                    for (int i = 0; i < count; i++) {
                        for (int j = 0; j < count - i - 1; j++) {
                            if (menuCount[j] < menuCount[j + 1]) {
                                //조회횟수정렬
                                empty = menuCount[j];
                                menuCount[j] = menuCount[j + 1];
                                menuCount[j + 1] = empty;

                                //메뉴번호정렬
                                empty = menuCountNum[j];
                                menuCountNum[j] = menuCountNum[j + 1];
                                menuCountNum[j + 1] = empty;

                                //메뉴이름정렬
                                sempty.setSempty(menuCountName[j]);
                                sempty.setSempty1(menuCountName[j+1]);
                                menuCountName[j]=sempty.getSempty1();
                                menuCountName[j+1]=sempty.getSempty();
                            }
                        }


                    }

                    if(jArrObject.isNull(0)) {          //빈값체크
                        TextView view1 = new TextView(context);
                        view1.setText("실시간 검색 순위\n");
                        view1.append("검색 부족");
                        layout.addView(view1);
                    }else {
                        TextView view2 = new TextView(context);
                        view2.setText("실시간 검색 순위\n");
                        view2.append("1."+menuCountName[0]+"\n");
                        for(int i=1;i<count;i++){
                            view2.append(i+1+"."+menuCountName[i]+"\n");
                        }
                        layout.addView(view2);
                    }


                    new postTask2().execute("http://192.168.64.157:8080/biz/user/menuList.do");

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }


    }

    class postTask2 extends AsyncTask<String, String, String> {


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

        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            Log.d("postData", result);
            try {
                JSONArray jArrObject = new JSONArray(result);
                int list_count = jArrObject.length();
                int[] menuNum = new int[list_count];
                String[] menuName = new String[list_count];
                int[] menuPrice = new int[list_count];
                String[] menuPicture = new String[list_count];
                String[] menuCategory= new String[list_count];
                final String[] businessNum = new String[list_count];

                ArrayList<String> arrayList = new ArrayList<String>();

                for (int i = 0; i < list_count; i++) {
                    JSONObject jsonObject = jArrObject.getJSONObject(i);
                    menuNum[i] = jsonObject.getInt("menuNum");
                    menuName[i] = jsonObject.getString("menuName");
                    menuPrice[i] = jsonObject.getInt("menuPrice");
                    menuPicture[i] = jsonObject.getString("menuPicture");
                    menuCategory[i] = jsonObject.getString("menuCategory");
                    businessNum[i] = jsonObject.getString("businessNum");



                }

                for(String ca : menuCategory) {
                    if(!arrayList.contains(ca)) {
                        arrayList.add(ca);
                    }
                }

                for(String list : arrayList){
                    TextView view1 = new TextView(context);
                    view1.setText(list);
                    layout.addView(view1);
                    for(int j = 0;j<list_count;j++){
                        if(list.equals(menuCategory[j])){
                            final Button btn = new Button(context);
                            btn.setId(menuNum[j]);
                            btn.setBackground(ContextCompat.getDrawable(context,R.drawable.button2));
                            if(menuNum[j]==topMenuNum1){
                                btn.setText(menuName[j]+" "+menuPrice[j]+"원"+"         전날 매출 1등 ");
                            }else if(menuNum[j]==topMenuNum2){
                                btn.setText(menuName[j]+" "+menuPrice[j]+"원"+"         전날 매출 2등 ");
                            }else if(menuNum[j]==topMenuNum3){
                                btn.setText(menuName[j]+" "+menuPrice[j]+"원"+"         전날 매출 3등 ");
                            }else{
                                btn.setText(menuName[j]+" "+menuPrice[j]+"원");
                            }
                                layout.addView(btn);
                            final int finalJ1 = j;
                            btn.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Intent intent = new Intent(getApplicationContext(), DetailActivity.class);
                                    intent.putExtra("menuNum", btn.getId());
                                    intent.putExtra("businessNum", businessNum[finalJ1]);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                    startActivityForResult(intent, 1000);
                                }
                            });
                        }
                    }
                }


            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }

    class postTask3 extends AsyncTask<String, String, String> {


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
                int[] menuNum = new int[list_count];
                int[] rank = new int [list_count];

                for (int i = 0; i < list_count; i++) {
                    JSONObject jsonObject = jArrObject.getJSONObject(i);
                    menuNum[i] = jsonObject.getInt("menuNum");
                }

                if(list_count==1){
                    topMenuNum1=menuNum[0];
                } else if(list_count==2){
                    topMenuNum1=menuNum[0];
                    topMenuNum2=menuNum[1];
                } else if(list_count==3){
                    topMenuNum1=menuNum[0];
                    topMenuNum2=menuNum[1];
                    topMenuNum3=menuNum[2];
                }

                new postTask().execute("http://192.168.64.157:8080/biz/user/menuhits.do");

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
