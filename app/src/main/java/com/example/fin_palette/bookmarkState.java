package com.example.fin_palette;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class bookmarkState extends AppCompatActivity {
    public boolean marked = false;
    private Context context;    // toast message 뿌리기 위해 context 가져옴

    public bookmarkState(Context context) {
        this.context = context;
    }

    public void getData(String url, String fin_prdt_num_cd, String aaid, ImageView star) {
        class GetDataJSON extends AsyncTask<String, Void, String> {
            // AsyncTask에서 execute() 메서드가 호출되면 내부적으로 doInBackground() 메서드가 호출
            @Override
            protected String doInBackground(String... params) {

                String uri = params[0];
                String fin_prdt_num_cd = params[1];
                String aaid = params[2];

                try {
                    URL url = new URL(uri + "?fin_prdt_num_cd=" + fin_prdt_num_cd + "&aaid=" + aaid); // 파라미터를 URL에 추가
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
                    marked = result.equals("true")?true:false;

                    // 저장돼있으면 full_star image로 변경!
                    if(marked) star.setImageResource(R.drawable.full_star_small);
                }
            }
        }

        GetDataJSON g = new GetDataJSON();
        g.execute(url, fin_prdt_num_cd, aaid);      // 아래도 수정 필요!
    }

    public void setData(String url, int prdtNum, String finPrdtCd, String opts, String aaid) {
        class SetDataJSON extends AsyncTask<String, Void, String> {

            @Override
            protected String doInBackground(String... params) {
                String uri = params[0];
                String prdtNum = params[1];
                String finPrdtCd = params[2];
                String opts = params[3];
                String aaid = params[4];
                String mark = marked ? "true" : "false";

                try {
                    URL url = new URL(uri + "?prdtNum=" + prdtNum + "&finPrdtCd=" + finPrdtCd + "&opts=" + opts + "&aaid=" + aaid + "&mark=" + mark);
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();

                    StringBuilder sb = new StringBuilder();
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(con.getInputStream()));
                    String json;
                    while ((json = bufferedReader.readLine()) != null) {
                        sb.append(json + "\n");
                    }

                    return "Success";       // INSERT/UPDATE 성공
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            }
            @Override
            protected void onPostExecute(String result) {
                marked = !marked;

                if (result != null && result.equals("Success")) {
                    if(marked) Toast.makeText(context, "북마크 등록되었습니다.", Toast.LENGTH_SHORT).show();
                    else Toast.makeText(context, "북마크 해제되었습니다.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, "저장에 실패했습니다.", Toast.LENGTH_SHORT).show();
                }
            }
        }

        SetDataJSON g = new SetDataJSON();
        g.execute(url, Integer.toString(prdtNum), finPrdtCd, opts, aaid);
    }
}
