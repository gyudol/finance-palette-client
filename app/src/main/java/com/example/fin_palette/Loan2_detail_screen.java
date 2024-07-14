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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class Loan2_detail_screen extends AppCompatActivity {

    Button btn_home;
    String myJSON;
    String optNum;
    String fin_prdt_num_cd;

    private static final String TAG_RESULTS = "result";
    private static final String TAG_KOR_CO_NM = "kor_co_nm";
    private static final String TAG_FIN_PRDT_NM = "fin_prdt_nm";
    private static final String TAG_LEND_RATE_TYPE_NM = "lend_rate_type_nm";
    private static final String TAG_RPAY_TYPE_NM = "rpay_type_nm";
    private static final String TAG_LEND_RATE_MIN1 = "lend_rate_min1";
    private static final String TAG_LEND_RATE_MIN = "lend_rate_min";
    private static final String TAG_LEND_RATE_AVG = "lend_rate_avg";
    private static final String TAG_LEND_RATE_MAX = "lend_rate_max";
    private static final String TAG_DLCS_STRT_END_DAY = "dcls_strt_end_day";
    private static final String TAG_MRTQ_TYPE_NM = "mrtg_type_nm";
    private static final String TAG_LOAN_LMT = "loan_lmt";
    private static final String TAG_LOAN_INCI_EXPN = "loan_inci_expn";
    private static final String TAG_ERLY_RPAY_FEE = "erly_rpay_fee";
    private static final String TAG_DLY_RATE = "dly_rate";
    private static final String TAG_JOIN_WAY = "join_way";
    private static final String TAG_DCLS_CHRG_MAN = "dcls_chrg_man";


    JSONArray products = null;


    getIPAddress ipAddress = new getIPAddress();
    String ipv4Address = ipAddress.getIPv4();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.loan2_detail_screen);

        // Intent에서 Extra로 전달된 데이터 받아오기
        Intent intentGet = getIntent();
        if (intentGet != null) {
            String receivedOptNum = intentGet.getStringExtra("optNum");

            // 받아온 값 확인
            if (receivedOptNum != null) {
                optNum = intentGet.getStringExtra("optNum");

                getData("http://" + ipv4Address + "/PHP_loan2_int.php", optNum);
            }
        }

        //////////////////////// 북마크 /////////////////////////////////
        ImageView star = findViewById(R.id.bookmark);
        fin_prdt_num_cd = 5 + "_" + optNum + '_';
        bookmarkState bs = new bookmarkState(this);
        AAID_State as = new AAID_State();
        bs.getData("http://" + ipv4Address + "/PHP_bookmark_chk.php", fin_prdt_num_cd, as.aaid, star);

        star.setOnClickListener(new View.OnClickListener() {   // 북마크 이미지 뷰 클릭하면 북마크 기능
            @Override
            public void onClick(View v) {
                if(bs.marked) star.setImageResource(R.drawable.empty_star_small);
                else star.setImageResource(R.drawable.full_star_small);

                bs.setData("http://" + ipv4Address + "/PHP_bookmark_upd.php", 5, optNum, "", as.aaid);
            }
        });

        ///////////////////////////////// 조회 기록
        viewHistoryState vs = new viewHistoryState(this);
        vs.getData("http://" + ipv4Address, fin_prdt_num_cd, as.aaid, 5, optNum, "");



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
            TextView lend_rate_type_nm = findViewById(R.id.lend_rate_type_nm);
            TextView rpay_type_nm = findViewById(R.id.rpay_type_nm);
            TextView lend_rate_min1 = findViewById(R.id.lend_rate_min1);
            TextView lend_rate_min = findViewById(R.id.lend_rate_min);
            TextView lend_rate_avg = findViewById(R.id.lend_rate_avg);
            TextView lend_rate_max = findViewById(R.id.lend_rate_max);
            TextView dcls_strt_end_day = findViewById(R.id.dcls_strt_end_day);
            TextView mrtg_type_nm = findViewById(R.id.mrtg_type_nm);
            TextView loan_lmt = findViewById(R.id.loan_lmt);
            TextView loan_inci_expn = findViewById(R.id.loan_inci_expn);
            TextView erly_rpay_fee = findViewById(R.id.erly_rpay_fee);
            TextView dly_rate = findViewById(R.id.dly_rate);
            TextView join_way = findViewById(R.id.join_way);
            TextView dcls_chrg_man = findViewById(R.id.dcls_chrg_man);

            JSONObject jsonObj = new JSONObject(myJSON);
            products = jsonObj.getJSONArray(TAG_RESULTS);

            JSONObject c = products.getJSONObject(0);
            kor_co_nm.setText(isNull(c.getString(TAG_KOR_CO_NM)));
            fin_prdt_nm.setText(isNull(c.getString(TAG_FIN_PRDT_NM)));
            lend_rate_type_nm.setText(isNull(c.getString(TAG_LEND_RATE_TYPE_NM)));
            rpay_type_nm.setText(isNull(c.getString(TAG_RPAY_TYPE_NM).replace("방식", "")));
            lend_rate_min1.setText("최저 " + isNull(c.getString(TAG_LEND_RATE_MIN1)) + "%");
            lend_rate_min.setText(isNull(c.getString(TAG_LEND_RATE_MIN)) + "%");
            lend_rate_avg.setText(isNull(c.getString(TAG_LEND_RATE_AVG )) + "%");
            lend_rate_max.setText(isNull(c.getString(TAG_LEND_RATE_MAX)) + "%");
            dcls_strt_end_day.setText(isNull(c.getString(TAG_DLCS_STRT_END_DAY)));
            mrtg_type_nm.setText(isNull(c.getString(TAG_MRTQ_TYPE_NM)));
            loan_lmt.setText(isNull(c.getString(TAG_LOAN_LMT)));
            loan_inci_expn.setText(isNull(c.getString(TAG_LOAN_INCI_EXPN)));
            erly_rpay_fee.setText(isNull(c.getString(TAG_ERLY_RPAY_FEE)));
            dly_rate.setText(isNull(c.getString(TAG_DLY_RATE)));
            join_way.setText(isNull(c.getString(TAG_JOIN_WAY)));
            dcls_chrg_man.setText(isNull(c.getString(TAG_DCLS_CHRG_MAN)));

            if(lend_rate_type_nm.getText().toString().equals("변동금리")) {
                lend_rate_type_nm.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.teal_700)));
            }

            if(rpay_type_nm.getText().toString().equals("분할상환")) {
                rpay_type_nm.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.magenta)));
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public String isNull(String str) {
        if(str.equals("null")) return "";
        else return str;
    }


    public void getData(String url, String optNum) {
        class GetDataJSON extends AsyncTask<String, Void, String> {
            // AsyncTask에서 execute() 메서드가 호출되면 내부적으로 doInBackground() 메서드가 호출
            @Override
            protected String doInBackground(String... params) {

                String uri = params[0];
                String optNum = params[1]; // 추가된 파라미터 optNum

                try {
                    URL url = new URL(uri + "?optNum=" + optNum); // 파라미터를 URL에 추가
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
        g.execute(url, optNum);      // 아래도 수정 필요!
    }
}