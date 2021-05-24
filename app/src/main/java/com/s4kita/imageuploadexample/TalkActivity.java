package com.s4kita.imageuploadexample;

import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class TalkActivity extends AppCompatActivity {

    ListView listView;
    TalkAdapter talkAdapter;

    ArrayList<TalkItem> talkItems = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_talk);

        //데이터를 서버에서 읽어오기
        loadDB();

        listView = findViewById(R.id.listView);
        talkAdapter = new TalkAdapter(getLayoutInflater(), talkItems);
        listView.setAdapter(talkAdapter);

    }

    void loadDB() {
        //volley library로 사용 가능
        //이 예제에서는 전통적 기법으로 함.
        new Thread() {
            @Override
            public void run() {

                String serverUri = "http://211.211.158.42/instudy/ImageExample/loadDB.php";

                try {
                    URL url = new URL(serverUri);

                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.setDoInput(true);
                    //connection.setDoOutput(true);// 이 예제는 필요 없다.
                    connection.setUseCaches(false);

                    InputStream is = connection.getInputStream();
                    InputStreamReader isr = new InputStreamReader(is);
                    BufferedReader reader = new BufferedReader(isr);

                    final StringBuffer buffer = new StringBuffer();
                    String line = reader.readLine();
                    while (line != null) {
                        buffer.append(line + "\n");
                        line = reader.readLine();
                    }

                    //읽어온 문자열에서 row(레코드)별로 분리하여 배열로 리턴하기
                    String[] rows = buffer.toString().split(";");

                    //대량의 데이터 초기화
                    talkItems.clear();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            talkAdapter.notifyDataSetChanged();
                        }
                    });

                    for (String row : rows) {
                        //한줄 데이터에서 한 칸씩 분리
                        String[] datas = row.split("&");
                        if (datas.length != 5) continue;

                        Log.i("테스트", "------------------------------------------------------------------");
                        String no = datas[0];
                        Log.i("테스트", "no = " + no);
                        String name = datas[1];
                        Log.i("테스트", "name = " + name);
                        String msg = datas[2];
                        Log.i("테스트", "msg = " + msg);
                        String imgPath = "http://211.211.158.42/instudy/ImageExample/" + datas[3];   //이미지는 상대경로라서 앞에 서버 주소를 써야한다.
                        Log.i("테스트", "imgPath = " + imgPath);
                        String date = datas[4];
                        Log.i("테스트", "date = " + date);


                        //대량의 데이터 ArrayList에 추가
                        talkItems.add(new TalkItem(no, name, msg, imgPath, date));

                        //리스트뷰 갱신
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                talkAdapter.notifyDataSetChanged();
                            }
                        });
                    }
//                    //읽어오는 작업이 성공 했는지 확인
//                    runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            new AlertDialog.Builder(TalkActivity.this).setMessage(buffer.toString()).create().show();
//
//
//                        }
//                    });

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }//loadDB() ..
}