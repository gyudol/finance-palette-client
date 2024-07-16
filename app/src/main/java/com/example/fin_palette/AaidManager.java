package com.example.fin_palette;

import android.os.AsyncTask;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.ads.identifier.AdvertisingIdClient;
import com.google.android.gms.ads.identifier.AdvertisingIdClient.Info;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class AAID_State extends AppCompatActivity  {
    String aaid = "";

    public AAID_State() {
        // AAID를 비동기적으로 가져오기
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Info adInfo = AdvertisingIdClient.getAdvertisingIdInfo(getApplicationContext());
                    aaid = adInfo.getId();
//                    boolean isLimitAdTrackingEnabled = adInfo.isLimitAdTrackingEnabled();
//
                      // 광고 ID와 제한 설정 출력
//                    Log.d("AAID", "Advertising ID: " + aaid);
//                    Log.d("AAID", "Limit Ad Tracking Enabled: " + isLimitAdTrackingEnabled);
                } catch (IOException | IllegalStateException | NullPointerException |
                         GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public void checkTable(String url) {    // AAID가 userInfo 테이블에 있는지 check 하고 없으면 INSERT
        class GetDataJSON extends AsyncTask<String, Void, String> {
            // AsyncTask에서 execute() 메서드가 호출되면 내부적으로 doInBackground() 메서드가 호출
            @Override
            protected String doInBackground(String... params) {

                String uri = params[0];
                String aaid = params[1];

                try {
                    URL url = new URL(uri + "?aaid=" + aaid); // 파라미터를 URL에 추가
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
//                if (result != null && result.equals("Success")) {
//                    Toast.makeText(getApplicationContext(), "저장되었습니다.", Toast.LENGTH_SHORT).show();
//                } else {
//                    Toast.makeText(getApplicationContext(), "저장에 실패했습니다.", Toast.LENGTH_SHORT).show();
//                }
            }
        }

        GetDataJSON g = new GetDataJSON();
        g.execute(url, aaid);      // 아래도 수정 필요!
    }
}
