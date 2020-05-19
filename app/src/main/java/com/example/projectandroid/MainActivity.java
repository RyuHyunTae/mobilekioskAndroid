package com.example.projectandroid;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.content.Intent;


import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

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


public class MainActivity extends AppCompatActivity {

    private Button btnRegist, btnLogin;//회원가입화면,로그인화면으로 이동하기위한 변수
    private EditText etId, etPWD; //아이디,비밀번호 변수
    private SharedPreferences appData;


    String id, password, name, phone;

    JSONObject requestData;

    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etId = (EditText) findViewById(R.id.etID);
        etPWD = (EditText) findViewById(R.id.etPWD);

        btnLogin = (Button) findViewById(R.id.btnLogin);
        btnRegist = (Button) findViewById(R.id.btnRegist);

        appData = getSharedPreferences("appData", MODE_PRIVATE);
        load();

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    requestData= new JSONObject();
                    id = etId.getText().toString();
                    password = etPWD.getText().toString();
                    requestData.accumulate("id", id);
                    requestData.accumulate("password", password);
                    Log.d("requestData", requestData.toString());

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                new postTask().execute("http://192.168.64.157:8080/biz/user/login.do");

            }
        });

        btnRegist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), HomeActivity.class);

                intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

                startActivityForResult(intent, 1000);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //setResult를 통해 받아온 요청번호, 상태, 데이터
        Log.d("RESULT", requestCode + "");
        Log.d("RESULT", resultCode + "");
        Log.d("RESULT", data + "");

        if (requestCode == 1000 && resultCode == RESULT_OK) {
            Toast.makeText(MainActivity.this, "회원가입 되었습니다!", Toast.LENGTH_SHORT).show();
            etId.setText(data.getStringExtra("ID"));
        }
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
                JSONObject jsonObject = new JSONObject(result);
                user = new User(jsonObject.getString("id"), jsonObject.getString("password"), jsonObject.getString("name"),jsonObject.getString("phone"));
                Log.d("UserInfo", user.toString());

                save();

                if(user.getId()!=null) {
                    Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivityForResult(intent, 1000);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }
    private void save() {
        // SharedPreferences 객체만으론 저장 불가능 Editor 사용
        SharedPreferences.Editor editor = appData.edit();

        // 에디터객체.put타입( 저장시킬 이름, 저장시킬 값 )
        // 저장시킬 이름이 이미 존재하면 덮어씌움
        editor.putString("id", user.getId());
        editor.putString("password", user.getPasswoard());
        editor.putString("name", user.getName());
        editor.putString("phone",user.getPhone());

        // apply, commit 을 안하면 변경된 내용이 저장되지 않음
        editor.apply();
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

