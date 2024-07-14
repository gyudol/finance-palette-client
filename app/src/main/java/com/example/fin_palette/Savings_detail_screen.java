package com.example.fin_palette;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;

public class Savings_detail_screen extends AppCompatActivity {

    Button btn_home;

    String myJSON;
    String finPrdtCd;
    String intrRateType;
    String rsrvType;
    String fin_prdt_num_cd;

    private static final String TAG_RESULTS = "result";
    private static final String TAG_KOR_CO_NM = "kor_co_nm";
    private static final String TAG_FIN_PRDT_NM = "fin_prdt_nm";
    private static final String TAG_INTR_RATE_TYPE_NM = "intr_rate_type_nm";
    private static final String TAG_RSRV_TYPE_NM = "rsrv_type_nm";
    private static final String TAG_INTR_RATE_MAX = "intr_rate_max";
    private static final String TAG_INTR_RATE_1 = "intr_rate_1";
    private static final String TAG_INTR_RATE_3 = "intr_rate_3";
    private static final String TAG_INTR_RATE_6 = "intr_rate_6";
    private static final String TAG_INTR_RATE_12 = "intr_rate_12";
    private static final String TAG_INTR_RATE_24 = "intr_rate_24";
    private static final String TAG_INTR_RATE_36 = "intr_rate_36";
    private static final String TAG_DLCS_STRT_END_DAY = "dcls_strt_end_day";
    private static final String TAG_JOIN_MEMBER = "join_member";
    private static final String TAG_JOIN_DENY = "join_deny";
    private static final String TAG_MAX_LIMIT = "max_limit";
    private static final String TAG_SPCL_CND = "spcl_cnd";
    private static final String TAG_MTRT_INT = "mtrt_int";
    private static final String TAG_JOIN_WAY = "join_way";
    private static final String TAG_ETC_NOTE= "etc_note";
    private static final String TAG_DCLS_CHRG_MAN = "dcls_chrg_man";


    JSONArray products = null;


    getIPAddress ipAddress = new getIPAddress();
    String ipv4Address = ipAddress.getIPv4();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.savings_detail_screen);

        // Intent에서 Extra로 전달된 데이터 받아오기
        Intent intentGet = getIntent();
        if (intentGet != null) {
            String receivedFinPrdtCd = intentGet.getStringExtra("finPrdtCd");
            String receivedIntrRateType = intentGet.getStringExtra("intrRateType");
            String receivedRsrvType = intentGet.getStringExtra("rsrvType");

            // 받아온 값 확인
            if (receivedFinPrdtCd != null && receivedIntrRateType != null && receivedRsrvType != null) {
                finPrdtCd = intentGet.getStringExtra("finPrdtCd");
                intrRateType = intentGet.getStringExtra("intrRateType");
                rsrvType = intentGet.getStringExtra("rsrvType");

                getData("http://" + ipv4Address + "/PHP_savings_int.php", finPrdtCd, intrRateType, rsrvType);
            }
        }


        //////////////////////// 북마크 /////////////////////////////////
        ImageView star = findViewById(R.id.bookmark);
        fin_prdt_num_cd = 2 + "_" + finPrdtCd + '_' + intrRateType + '_' + rsrvType;
        bookmarkState bs = new bookmarkState(this);
        AAID_State as = new AAID_State();
        bs.getData("http://" + ipv4Address + "/PHP_bookmark_chk.php", fin_prdt_num_cd, as.aaid, star);

        star.setOnClickListener(new View.OnClickListener() {   // 북마크 이미지 뷰 클릭하면 북마크 기능
            @Override
            public void onClick(View v) {
                if(bs.marked) star.setImageResource(R.drawable.empty_star_small);
                else star.setImageResource(R.drawable.full_star_small);

                bs.setData("http://" + ipv4Address + "/PHP_bookmark_upd.php", 2, finPrdtCd, intrRateType + '_' + rsrvType, as.aaid);
            }
        });


        ///////////////////////////////// 조회 기록
        viewHistoryState vs = new viewHistoryState(this);
        vs.getData("http://" + ipv4Address, fin_prdt_num_cd, as.aaid, 2, finPrdtCd, intrRateType + '_' + rsrvType);


        btn_home = findViewById(R.id.btn_home);
        btn_home.setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
        });

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed(); // 뒤로 가기 버튼을 클릭하면 onBackPressed()가 호출됩니다.
            }
        });
    }

    protected void showWindow() {
        try {
            TextView kor_co_nm = findViewById(R.id.kor_co_nm);
            TextView fin_prdt_nm = findViewById(R.id.fin_prdt_nm);
            TextView intr_rate_type_nm = findViewById(R.id.intr_rate_type_nm);
            TextView rsrv_type_nm = findViewById(R.id.rsrv_type_nm);
            TextView intr_rate_max = findViewById(R.id.intr_rate_max);
            TextView intr_rate_1 = findViewById(R.id.intr_rate_1);
            TextView intr_rate_3 = findViewById(R.id.intr_rate_3);
            TextView intr_rate_6 = findViewById(R.id.intr_rate_6);
            TextView intr_rate_12 = findViewById(R.id.intr_rate_12);
            TextView intr_rate_24 = findViewById(R.id.intr_rate_24);
            TextView intr_rate_36 = findViewById(R.id.intr_rate_36);
            TextView dcls_strt_end_day = findViewById(R.id.dcls_strt_end_day);
            TextView join_member = findViewById(R.id.join_member);
            TextView join_deny = findViewById(R.id.join_deny);
            TextView max_limit = findViewById(R.id.max_limit);
            TextView spcl_cnd = findViewById(R.id.spcl_cnd);
            TextView mtrt_int = findViewById(R.id.mtrt_int);
            TextView join_way = findViewById(R.id.join_way);
            TextView etc_note = findViewById(R.id.etc_note);
            TextView dcls_chrg_man = findViewById(R.id.dcls_chrg_man);

            JSONObject jsonObj = new JSONObject(myJSON);
            products = jsonObj.getJSONArray(TAG_RESULTS);

            JSONObject c = products.getJSONObject(0);
            kor_co_nm.setText(isNull(c.getString(TAG_KOR_CO_NM)));
            fin_prdt_nm.setText(isNull(c.getString(TAG_FIN_PRDT_NM)));
            intr_rate_type_nm.setText(isNull(c.getString(TAG_INTR_RATE_TYPE_NM)));
            rsrv_type_nm.setText(isNull(c.getString(TAG_RSRV_TYPE_NM)));
            intr_rate_max.setText("최고 " + isNull(c.getString(TAG_INTR_RATE_MAX)) + "%");
            intr_rate_1.setText(isNull(c.getString(TAG_INTR_RATE_1)) + "%");
            intr_rate_3.setText(isNull(c.getString(TAG_INTR_RATE_3)) + "%");
            intr_rate_6.setText(isNull(c.getString(TAG_INTR_RATE_6)) + "%");
            intr_rate_12.setText(isNull(c.getString(TAG_INTR_RATE_12)) + "%");
            intr_rate_24.setText(isNull(c.getString(TAG_INTR_RATE_24)) + "%");
            intr_rate_36.setText(isNull(c.getString(TAG_INTR_RATE_36)) + "%");
            dcls_strt_end_day.setText(isNull(c.getString(TAG_DLCS_STRT_END_DAY)));
            join_member.setText(isNull(c.getString(TAG_JOIN_MEMBER)));
            join_deny.setText(isNull(c.getString(TAG_JOIN_DENY)).equals("1")?"제한 없음":
                    isNull(c.getString(TAG_JOIN_DENY)).equals("2")?"서민 전용":isNull(c.getString(TAG_JOIN_DENY)).equals("2")?"일부 제한":"");
            max_limit.setText(toAmount(isNull(c.getString(TAG_MAX_LIMIT))));
            spcl_cnd.setText(isNull(c.getString(TAG_SPCL_CND)));
            mtrt_int.setText(isNull(c.getString(TAG_MTRT_INT)));
            join_way.setText(isNull(c.getString(TAG_JOIN_WAY)));
            etc_note.setText(isNull(c.getString(TAG_ETC_NOTE)));
            dcls_chrg_man.setText(isNull(c.getString(TAG_DCLS_CHRG_MAN)));

            if(intr_rate_type_nm.getText().toString().equals("복리")) {
//                Toast.makeText(getApplicationContext(), "color changed", Toast.LENGTH_SHORT).show();
                intr_rate_type_nm.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.orange)));
            }
            if(rsrv_type_nm.getText().toString().equals("자유적립식")) {
//                Toast.makeText(getApplicationContext(), "color changed", Toast.LENGTH_SHORT).show();
                rsrv_type_nm.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.mid_blue)));
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public String isNull(String str) {
        if(str.equals("null")) return "";
        else return str;
    }

    public String toAmount(String str) {        // 천 단위로 콤마 붙여줌
        if(str.equals("")) return "없음";
        else {
            DecimalFormat df = new DecimalFormat("###,###");

            return df.format(Long.parseLong(str)) + "원";
        }
    }


    public void getData(String url, String finPrdtCd, String intrRateType, String rsrvType) {
        class GetDataJSON extends AsyncTask<String, Void, String> {
            // AsyncTask에서 execute() 메서드가 호출되면 내부적으로 doInBackground() 메서드가 호출
            @Override
            protected String doInBackground(String... params) {

                String uri = params[0];
                String finPrdtCd = params[1]; // 추가된 파라미터 finPrdtCd
                String intrRateType = params[2];
                String rsrvType = params[3];

                try {
                    URL url = new URL(uri + "?finPrdtCd=" + finPrdtCd + "&intrRateType=" + intrRateType + "&rsrvType=" + rsrvType); // 파라미터를 URL에 추가
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
//                    Toast.makeText(getApplicationContext(), result, Toast.LENGTH_SHORT).show();
                    myJSON = result;
                    showWindow();
                }
            }
        }

        GetDataJSON g = new GetDataJSON();
        g.execute(url, finPrdtCd, intrRateType, rsrvType);      // 아래도 수정 필요!
    }
}