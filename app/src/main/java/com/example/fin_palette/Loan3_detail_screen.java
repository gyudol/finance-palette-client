package com.example.fin_palette;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

public class Loan3_detail_screen extends AppCompatActivity {

    Button btn_home;
    String myJSON;
    String optNum;
    String fin_prdt_num_cd;

    private static final String TAG_RESULTS = "result";
    private static final String TAG_KOR_CO_NM = "kor_co_nm";
    private static final String TAG_FIN_PRDT_NM = "fin_prdt_nm";
    private static final String TAG_CRDT_PRDT_TYPE_NM = "crdt_prdt_type_nm";
    private static final String TAG_CB_NAME = "cb_name";
    private static final String TAG_LEND_RATE_MIN = "lend_rate_min";
    private static final String TAG_CRDT_GRAD_AVG = "crdt_grad_avg";
    private static final String TAG_DLCS_STRT_END_DAY = "dcls_strt_end_day";
    private static final String TAG_JOIN_WAY = "join_way";
    private static final String TAG_CRDT_LEND_RATE_TYPE_NM = "crdt_lend_rate_type_nm";
    private static final String TAG_DCLS_CHRG_MAN = "dcls_chrg_man";


    /////////////////////// 여기부턴 loan3_detail_list ///////////////////////
    ListView list;
    String myJSON2;
    String crdt_grad_1_min_value;
    JSONArray products2 = null;
    ArrayList<HashMap<String, String>> productList;

    private static final String TAG_CRDT_GRAD_1 = "crdt_grad_1";
    private static final String TAG_CRDT_GRAD_4 = "crdt_grad_4";
    private static final String TAG_CRDT_GRAD_5 = "crdt_grad_5";
    private static final String TAG_CRDT_GRAD_6 = "crdt_grad_6";
    private static final String TAG_CRDT_GRAD_10 = "crdt_grad_10";
    private static final String TAG_CRDT_GRAD_11 = "crdt_grad_11";
    private static final String TAG_CRDT_GRAD_12 = "crdt_grad_12";
    private static final String TAG_CRDT_GRAD_13 = "crdt_grad_13";

    /////////////////////////////////////////////////////////////////////////////////

    JSONArray products = null;


    getIPAddress ipAddress = new getIPAddress();
    String ipv4Address = ipAddress.getIPv4();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.loan3_detail_screen);

        // Intent에서 Extra로 전달된 데이터 받아오기
        Intent intentGet = getIntent();
        if (intentGet != null) {
            String receivedOptNum = intentGet.getStringExtra("optNum");

            // 받아온 값 확인
            if (receivedOptNum != null) {
                optNum = intentGet.getStringExtra("optNum");

                getData("http://" + ipv4Address + "/PHP_loan3_int.php", optNum);
                getData("http://" + ipv4Address + "/PHP_loan3_in_list.php", optNum);
            }
        }

        //////////////////////// 북마크 /////////////////////////////////
        ImageView star = findViewById(R.id.bookmark);
        fin_prdt_num_cd = 6 + "_" + optNum + '_';
        bookmarkState bs = new bookmarkState(this);
        AAID_State as = new AAID_State();
        bs.getData("http://" + ipv4Address + "/PHP_bookmark_chk.php", fin_prdt_num_cd, as.aaid, star);

        star.setOnClickListener(new View.OnClickListener() {   // 북마크 이미지 뷰 클릭하면 북마크 기능
            @Override
            public void onClick(View v) {
                if(bs.marked) star.setImageResource(R.drawable.empty_star_small);
                else star.setImageResource(R.drawable.full_star_small);

                bs.setData("http://" + ipv4Address + "/PHP_bookmark_upd.php", 6, optNum, "", as.aaid);
            }
        });

        ///////////////////////////////// 조회 기록
        viewHistoryState vs = new viewHistoryState(this);
        vs.getData("http://" + ipv4Address, fin_prdt_num_cd, as.aaid, 6, optNum, "");


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
            TextView crdt_prdt_type_nm = findViewById(R.id.crdt_prdt_type_nm);
            TextView cb_name = findViewById(R.id.cb_name);
            TextView crdt_grad_1_min = findViewById(R.id.crdt_grad_1_min);
            TextView lend_rate_min = findViewById(R.id.lend_rate_min);
            TextView crdt_grad_avg = findViewById(R.id.crdt_grad_avg);
            TextView dcls_strt_end_day = findViewById(R.id.dcls_strt_end_day);
            TextView join_way = findViewById(R.id.join_way);
            TextView crdt_lend_rate_type_nm = findViewById(R.id.crdt_lend_rate_type_nm);
            TextView dcls_chrg_man = findViewById(R.id.dcls_chrg_man);

            JSONObject jsonObj = new JSONObject(myJSON);
            products = jsonObj.getJSONArray(TAG_RESULTS);

            JSONObject c = products.getJSONObject(0);
            kor_co_nm.setText(isNull(c.getString(TAG_KOR_CO_NM)));
            fin_prdt_nm.setText(isNull(c.getString(TAG_FIN_PRDT_NM)));
            crdt_prdt_type_nm.setText(isNull(c.getString(TAG_CRDT_PRDT_TYPE_NM)));
            cb_name.setText(isNull(c.getString(TAG_CB_NAME)));
            crdt_grad_1_min.setText("최저 " + isNull(c.getString(TAG_LEND_RATE_MIN)) + "%");
            lend_rate_min.setText(isNull(c.getString(TAG_LEND_RATE_MIN)) + "%");
            crdt_grad_avg.setText(isNull(c.getString(TAG_CRDT_GRAD_AVG )) + "%");
            dcls_strt_end_day.setText(isNull(c.getString(TAG_DLCS_STRT_END_DAY)));
            join_way.setText(isNull(c.getString(TAG_JOIN_WAY)));
            crdt_lend_rate_type_nm.setText(isNull(c.getString(TAG_CRDT_LEND_RATE_TYPE_NM)));
            dcls_chrg_man.setText(isNull(c.getString(TAG_DCLS_CHRG_MAN)));

            crdt_grad_1_min_value = c.getString(TAG_LEND_RATE_MIN);

            if(crdt_prdt_type_nm.getText().toString().equals("일반신용대출")) {
                crdt_prdt_type_nm.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.magenta)));
            }
            else if(crdt_prdt_type_nm.getText().toString().equals("마이너스한도대출")) {
                crdt_prdt_type_nm.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.crown_flo)));
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


    protected void showList() {
        try {
            JSONObject jsonObj = new JSONObject(myJSON2);
            products = jsonObj.getJSONArray(TAG_RESULTS);

            LinearLayout linearLayout = findViewById(R.id.linearLpo3); // ScrollView 안의 LinearLayout ID 설정
            LayoutInflater inflater = LayoutInflater.from(this);
            linearLayout.removeAllViews(); // 기존 뷰들을 모두 제거

            for (int i = 0; i < products.length(); i++) {
                JSONObject c = products.getJSONObject(i);
                String crdt_grad_1 = isNull(c.getString(TAG_CRDT_GRAD_1)) + "%";
                String crdt_grad_4 = isNull(c.getString(TAG_CRDT_GRAD_4)) + "%";
                String crdt_grad_5 = isNull(c.getString(TAG_CRDT_GRAD_5)) + "%";
                String crdt_grad_6 = isNull(c.getString(TAG_CRDT_GRAD_6)) + "%";
                String crdt_grad_10 = isNull(c.getString(TAG_CRDT_GRAD_10)) + "%";
                String crdt_grad_11 = isNull(c.getString(TAG_CRDT_GRAD_11)) + "%";
                String crdt_grad_12 = isNull(c.getString(TAG_CRDT_GRAD_12)) + "%";
                String crdt_grad_13 = isNull(c.getString(TAG_CRDT_GRAD_13)) + "%";

                // 최저가 어딘 구간인지 설정
                TextView crdt_grad_1_min_nm = findViewById(R.id.crdt_grad_1_min_nm);

                if(crdt_grad_1_min_value.equals(crdt_grad_1)) crdt_grad_1_min_nm.setText("900점 초과");
                else if(crdt_grad_1_min_value.equals(crdt_grad_4)) crdt_grad_1_min_nm.setText("801~900점");
                else if(crdt_grad_1_min_value.equals(crdt_grad_5)) crdt_grad_1_min_nm.setText("701~800점");
                else if(crdt_grad_1_min_value.equals(crdt_grad_6)) crdt_grad_1_min_nm.setText("601~700점");
                else if(crdt_grad_1_min_value.equals(crdt_grad_10)) crdt_grad_1_min_nm.setText("501~600점");
                else if(crdt_grad_1_min_value.equals(crdt_grad_11)) crdt_grad_1_min_nm.setText("401~500점");
                else if(crdt_grad_1_min_value.equals(crdt_grad_12)) crdt_grad_1_min_nm.setText("301~400점");
                else crdt_grad_1_min_nm.setText("300점 이하");

                // annuity_saving_detail_list.xml을 inflate하여 새로운 뷰 생성
                View itemView = inflater.inflate(R.layout.loan3_detail_list, null);

                TextView crdt_grad_1TextView = itemView.findViewById(R.id.crdt_grad_1);
                TextView crdt_grad_4TextView = itemView.findViewById(R.id.crdt_grad_4);
                TextView crdt_grad_5TextView = itemView.findViewById(R.id.crdt_grad_5);
                TextView crdt_grad_6TextView = itemView.findViewById(R.id.crdt_grad_6);
                TextView crdt_grad_10TextView = itemView.findViewById(R.id.crdt_grad_10);
                TextView crdt_grad_11TextView = itemView.findViewById(R.id.crdt_grad_11);
                TextView crdt_grad_12TextView = itemView.findViewById(R.id.crdt_grad_12);
                TextView crdt_grad_13TextView = itemView.findViewById(R.id.crdt_grad_13);

                crdt_grad_1TextView.setText(crdt_grad_1);
                crdt_grad_4TextView.setText(crdt_grad_4);
                crdt_grad_5TextView.setText(crdt_grad_5);
                crdt_grad_6TextView.setText(crdt_grad_6);
                crdt_grad_10TextView.setText(crdt_grad_10);
                crdt_grad_11TextView.setText(crdt_grad_11);
                crdt_grad_12TextView.setText(crdt_grad_12);
                crdt_grad_13TextView.setText(crdt_grad_13);

                // 생성된 뷰를 ScrollView 안의 LinearLayout에 추가
                linearLayout.addView(itemView);
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
        if (url.contains("PHP_loan3_int")) {            // 대출 상품 출력
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
                    if (result != null) {
//                    Toast.makeText(getApplicationContext(), result, Toast.LENGTH_SHORT).show();
                        myJSON = result;
                        showWindow();
                    }
                }
            }

            GetDataJSON g = new GetDataJSON();
            g.execute(url, optNum);      // 아래도 수정 필요!
        }

        else if(url.contains("PHP_loan3_in_list")) {              // 대출 상품 옵션 리스트 출력
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
                    if (result != null) {
//                        Toast.makeText(getApplicationContext(), result, Toast.LENGTH_SHORT).show();
                        myJSON2 = result;
                        showList();
                    }
                }
            }

            GetDataJSON g = new GetDataJSON();
            g.execute(url, optNum);      // 아래도 수정 필요!
        }

    }
}