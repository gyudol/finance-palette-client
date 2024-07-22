package com.example.fin_palette;

import android.content.Context;
import android.os.AsyncTask;

import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.StringTokenizer;

public class ViewHistoryManager extends AppCompatActivity {
    public int cnt = 0;
    public boolean viewHistoryContains = false;
    private Context context;    // toast message 뿌리기 위해 context 가져옴

    public ViewHistoryManager(Context context) {this.context = context;}

    public void getData(String url, String historyId, String aaid, int prdtNum, String finPrdtId, String opts) {
        class GetDataJSON extends AsyncTask<String, Void, String> {
            // AsyncTask에서 execute() 메서드가 호출되면 내부적으로 doInBackground() 메서드가 호출
            @Override
            protected String doInBackground(String... params) {

                String uri = params[0];
                String historyId = params[1];
                String aaid = params[2];

                try {
                    URL url = new URL(uri + "/view_history_chk.php?historyId=" + historyId + "&aaid=" + aaid); // 파라미터를 URL에 추가
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();
                    StringBuilder sb = new StringBuilder();
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(con.getInputStream()));
                    String json;
                    while ((json = bufferedReader.readLine()) != null) {
                        sb.append(json + "\n");
                    }
                    return sb.toString().trim();
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            }
            @Override
            protected void onPostExecute(String result) {
                if(result != null) {
                    // Toast.makeText(context, result, Toast.LENGTH_SHORT).show();
                    StringTokenizer st = new StringTokenizer(result);

                    cnt = Integer.parseInt(st.nextToken());
                    viewHistoryContains = st.nextToken().equals("true");

                    setData(url, prdtNum, finPrdtId, opts, aaid);   // 비동기로 처리되므로 여기서 처리

                    // Toast.makeText(context, result, Toast.LENGTH_SHORT).show();
                }
            }
        }

        GetDataJSON g = new GetDataJSON();
        g.execute(url, historyId, aaid);      // 아래도 수정 필요!
    }



    public void setData(String url, int prdtNum, String finPrdtId, String opts, String aaid) {
        class SetDataJSON extends AsyncTask<String, Void, String> {

            @Override
            protected String doInBackground(String... params) {
                String uri = params[0];
                String prdtNum = params[1];
                String finPrdtId = params[2];
                String opts = params[3];
                String aaid = params[4];

                try {
                    URL url = new URL(uri + "/view_history_upd.php?prdtNum=" + prdtNum + "&finPrdtId=" + finPrdtId + "&opts=" + opts + "&aaid=" + aaid +
                            "&cnt=" + cnt + "&viewHistoryContains=" + viewHistoryContains);
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();

                    StringBuilder sb = new StringBuilder();
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(con.getInputStream()));
                    String json;
                    while ((json = bufferedReader.readLine()) != null) {
                        sb.append(json + "\n");
                    }

                    return sb.toString().trim();       // INSERT/UPDATE 성공
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            }

            @Override
            protected void onPostExecute(String result) {
                if(result != null) {
                   // Toast.makeText(context, result, Toast.LENGTH_SHORT).show();
                }
            }
        }

        SetDataJSON g = new SetDataJSON();
        g.execute(url, Integer.toString(prdtNum), finPrdtId, opts, aaid);
    }
}
