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
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

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

public class HomeActivity extends AppCompatActivity {

    String id, password, name, phone;
    private SharedPreferences appData;



    LinearLayout layout;
    Context context;

    private Button scanQRBtn;
    Button one,basketBtn;

    JSONObject requestData;

    private LinearLayout area;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        appData = getSharedPreferences("appData", MODE_PRIVATE);
        load();


        try {
            requestData= new JSONObject();
            requestData.accumulate("id", id);
            Log.d("requestData", requestData.toString());

        } catch (JSONException e) {
            e.printStackTrace();
        }
        new postTask().execute("http://192.168.64.157:8080/biz/user/shopList.do");

        one=(Button) findViewById(R.id.addBtn);
        basketBtn=(Button) findViewById(R.id.basketBtn);
        layout = (LinearLayout)findViewById(R.id.layout);
        context=this;

        one.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), OrderListActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivityForResult(intent, 1000);
            }
        });
        scanQRBtn = (Button) findViewById(R.id.scanQR);

        scanQRBtn.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                Intent intent = new Intent(HomeActivity.this, ScanQR.class);
                startActivity(intent);
            }
        });


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

        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            Log.d("postData", result);
            try {
                JSONArray jArrObject = new JSONArray(result);
                int list_count=jArrObject.length();
                final String[] shopName = new String[list_count];
                final String[] businessNum = new String[list_count];
                final int[] shopOpen = new int[list_count];

                for(int i =0; i<list_count;i++){
                    JSONObject jsonObject = jArrObject.getJSONObject(i);
                    shopName[i]=jsonObject.getString("shopName");
                    businessNum[i]=jsonObject.getString("businessNum");
                    shopOpen[i]=jsonObject.getInt("shopOpen");

                    final Button btn = new Button(context);
                    btn.setId(i);
                    btn.setText(shopName[i]);
                    layout.addView(btn);
                    final int finalI = i;
                    final int finalI1 = i;
                    btn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if(shopOpen[finalI1]==2){
                                Toast.makeText(context.getApplicationContext(),"매장 오픈전 입니다.", Toast.LENGTH_SHORT).show();
                            } else if(shopOpen[finalI1]==1) {
                                Intent intent = new Intent(getApplicationContext(), MenuActivity.class);
                                intent.putExtra("shopName", shopName[finalI]);
                                intent.putExtra("businessNum", businessNum[finalI]);
                                intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                startActivityForResult(intent, 1000);
                            }
                        }
                    });
                    TextView v1 = new TextView(context);
                    v1.setText("");
                    layout.addView(v1);
                }


            } catch (JSONException e) {
                e.printStackTrace();
            }


            basketBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getApplicationContext(), BasketActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivityForResult(intent, 1000);
                }
            });


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
