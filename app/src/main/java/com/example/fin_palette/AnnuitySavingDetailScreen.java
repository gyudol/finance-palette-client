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
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
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
import java.util.ArrayList;
import java.util.HashMap;

public class Annuity_Saving_detail_screen extends AppCompatActivity {

    Button btn_home;

    String myJSON;
    String finPrdtCd;
    String fin_prdt_num_cd;

    private static final String TAG_RESULTS = "result";
    private static final String TAG_KOR_CO_NM = "kor_co_nm";
    private static final String TAG_FIN_PRDT_NM = "fin_prdt_nm";
    private static final String TAG_PNSN_KIND_NM = "pnsn_kind_nm";
    private static final String TAG_PRDT_TYPE_NM = "prdt_type_nm";
    private static final String TAG_AVG_PRFT_RATE = "avg_prft_rate";
    private static final String TAG_BTRM_PRFT_RATE_1 = "btrm_prft_rate_1";
    private static final String TAG_BTRM_PRFT_RATE_2 = "btrm_prft_rate_2";
    private static final String TAG_BTRM_PRFT_RATE_3 = "btrm_prft_rate_3";
    private static final String TAG_SALE_STRT_DAY = "sale_strt_day";
    private static final String TAG_DLCS_STRT_END_DAY = "dcls_strt_end_day";
    private static final String TAG_JOIN_WAY = "join_way";
    private static final String TAG_SALE_CO = "sale_co";
    private static final String TAG_ETC = "etc";
    private static final String TAG_MNTN_CNT = "mntn_cnt";
    private static final String TAG_DCLS_RATE = "dcls_rate";
    private static final String TAG_GUAR_RATE = "guar_rate";


    /////////////////////// 여기부턴 annuity_saving_detail_list ///////////////////////
    ListView list;
    String myJSON2;
    JSONArray products2 = null;
    ArrayList<HashMap<String, String>> productList;

    private static final String TAG_PNSN_RECP_TRM_NM = "pnsn_recp_trm_nm";
    private static final String TAG_PNSN_ENTR_AGE_NM = "pnsn_entr_age_nm";
    private static final String TAG_MON_PAYM_ATM_NM = "mon_paym_atm_nm";
    private static final String TAG_PAYM_PRD_NM = "paym_prd_nm";
    private static final String TAG_PNSN_STRT_AGE_NM = "pnsn_strt_age_nm";
    private static final String TAG_PNSN_RECP_AMT = "pnsn_recp_amt";

    /////////////////////////////////////////////////////////////////////////////////

    JSONArray products = null;


    getIPAddress ipAddress = new getIPAddress();
    String ipv4Address = ipAddress.getIPv4();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.annuity_saving_detail_screen);
        productList = new ArrayList<HashMap<String, String>>();

        // Intent에서 Extra로 전달된 데이터 받아오기
        Intent intentGet = getIntent();
        if (intentGet != null) {
            String receivedFinPrdtCd = intentGet.getStringExtra("finPrdtCd");

            // 받아온 값 확인
            if (receivedFinPrdtCd != null) {
                finPrdtCd = intentGet.getStringExtra("finPrdtCd");

                getData("http://" + ipv4Address + "/PHP_annuity_saving_int.php", finPrdtCd);
                getData("http://" + ipv4Address + "/PHP_annuity_saving_in_list.php", finPrdtCd);
            }
        }

        //////////////////////// 북마크 /////////////////////////////////
        ImageView star = findViewById(R.id.bookmark);
        fin_prdt_num_cd = 3 + "_" + finPrdtCd + '_';
        bookmarkState bs = new bookmarkState(this);
        AAID_State as = new AAID_State();
        bs.getData("http://" + ipv4Address + "/PHP_bookmark_chk.php", fin_prdt_num_cd, as.aaid, star);

        star.setOnClickListener(new View.OnClickListener() {   // 북마크 이미지 뷰 클릭하면 북마크 기능
            @Override
            public void onClick(View v) {
                if(bs.marked) star.setImageResource(R.drawable.empty_star_small);
                else star.setImageResource(R.drawable.full_star_small);

                bs.setData("http://" + ipv4Address + "/PHP_bookmark_upd.php", 3, finPrdtCd, "", as.aaid);
            }
        });

        ///////////////////////////////// 조회 기록
        viewHistoryState vs = new viewHistoryState(this);
        vs.getData("http://" + ipv4Address, fin_prdt_num_cd, as.aaid, 3, finPrdtCd, "");


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
            TextView pnsn_kind_nm = findViewById(R.id.pnsn_kind_nm);
            TextView prdt_type_nm = findViewById(R.id.prdt_type_nm);
            TextView avg_prft_rate = findViewById(R.id.avg_prft_rate);
            TextView btrm_prft_rate_1 = findViewById(R.id.btrm_prft_rate_1);
            TextView btrm_prft_rate_2 = findViewById(R.id.btrm_prft_rate_2);
            TextView btrm_prft_rate_3 = findViewById(R.id.btrm_prft_rate_3);
            TextView sale_strt_day = findViewById(R.id.sale_strt_day);
            TextView dcls_strt_end_day = findViewById(R.id.dcls_strt_end_day);
            TextView join_way = findViewById(R.id.join_way);
            TextView sale_co = findViewById(R.id.sale_co);
            TextView etc = findViewById(R.id.etc);
            TextView mntn_cnt = findViewById(R.id.mntn_cnt);
            TextView dcls_rate = findViewById(R.id.dcls_rate);
            TextView guar_rate = findViewById(R.id.guar_rate);

            JSONObject jsonObj = new JSONObject(myJSON);
            products = jsonObj.getJSONArray(TAG_RESULTS);

            JSONObject c = products.getJSONObject(0);
            kor_co_nm.setText(isNull(c.getString(TAG_KOR_CO_NM)));
            fin_prdt_nm.setText(isNull(c.getString(TAG_FIN_PRDT_NM)));
            pnsn_kind_nm.setText(isNull(c.getString(TAG_PNSN_KIND_NM)));
            prdt_type_nm.setText(isNull(c.getString(TAG_PRDT_TYPE_NM )));
            avg_prft_rate.setText(isNull(c.getString(TAG_AVG_PRFT_RATE)) + "%");
            btrm_prft_rate_1.setText(isNull(c.getString(TAG_BTRM_PRFT_RATE_1)) + "%");
            btrm_prft_rate_2.setText(isNull(c.getString(TAG_BTRM_PRFT_RATE_2)) + "%");
            btrm_prft_rate_3.setText(isNull(c.getString(TAG_BTRM_PRFT_RATE_3)) + "%");
            sale_strt_day.setText(isNull(c.getString(TAG_SALE_STRT_DAY)));
            dcls_strt_end_day.setText(isNull(c.getString(TAG_DLCS_STRT_END_DAY)));
            join_way.setText(isNull(c.getString(TAG_JOIN_WAY)));
            sale_co.setText(isNull(c.getString(TAG_SALE_CO)));
            etc.setText(isNull(c.getString(TAG_ETC)));
            mntn_cnt.setText(isNull(c.getString(TAG_MNTN_CNT)));
            dcls_rate.setText(isNull(c.getString(TAG_DCLS_RATE)));
            guar_rate.setText(isNull(c.getString(TAG_GUAR_RATE)));

            if(pnsn_kind_nm.getText().toString().equals("연금저축보험(생명)")) {
                pnsn_kind_nm.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.purple_500)));
            }
            else if(pnsn_kind_nm.getText().toString().equals("연금저축보험(손해)")) {
                pnsn_kind_nm.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.purple_700)));
            }

            if(prdt_type_nm.getText().toString().equals("재간접형")) {
                prdt_type_nm.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.crown_flo)));
            }
            else if(prdt_type_nm.getText().toString().equals("채권형")) {
                prdt_type_nm.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.mid_green)));
            }
            else if(prdt_type_nm.getText().toString().equals("금리연동형")) {
                prdt_type_nm.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.brown_green)));
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    protected void showList() {
        try {
            JSONObject jsonObj = new JSONObject(myJSON2);
            products = jsonObj.getJSONArray(TAG_RESULTS);

            LinearLayout linearLayout = findViewById(R.id.linearAspo); // ScrollView 안의 LinearLayout ID 설정
            LayoutInflater inflater = LayoutInflater.from(this);
            linearLayout.removeAllViews(); // 기존 뷰들을 모두 제거

            for (int i = 0; i < products.length(); i++) {
                JSONObject c = products.getJSONObject(i);
                String opt_num = "상품옵션 " + (i + 1);
                String pnsn_recp_trm_nm = c.getString(TAG_PNSN_RECP_TRM_NM);
                String pnsn_entr_age_nm = c.getString(TAG_PNSN_ENTR_AGE_NM);
                String mon_paym_atm_nm = c.getString(TAG_MON_PAYM_ATM_NM);
                String paym_prd_nm = c.getString(TAG_PAYM_PRD_NM);
                String pnsn_strt_age_nm = c.getString(TAG_PNSN_STRT_AGE_NM);
                String pnsn_recp_amt = toAmount(c.getString(TAG_PNSN_RECP_AMT));

                // annuity_saving_detail_list.xml을 inflate하여 새로운 뷰 생성
                View itemView = inflater.inflate(R.layout.annuity_saving_detail_list, null);

                TextView opt_numTextView = itemView.findViewById(R.id.opt_num);
                TextView pnsn_recp_trm_nmTextView = itemView.findViewById(R.id.pnsn_recp_trm_nm);
                TextView pnsn_entr_age_nmTextView = itemView.findViewById(R.id.pnsn_entr_age_nm);
                TextView mon_paym_atm_nmTextView = itemView.findViewById(R.id.mon_paym_atm_nm);
                TextView paym_prd_nmTextView = itemView.findViewById(R.id.paym_prd_nm);
                TextView pnsn_strt_age_nmTextView = itemView.findViewById(R.id.pnsn_strt_age_nm);
                TextView pnsn_recp_amtTextView = itemView.findViewById(R.id.pnsn_recp_amt);

                opt_numTextView.setText(opt_num);
                pnsn_recp_trm_nmTextView.setText(pnsn_recp_trm_nm);
                pnsn_entr_age_nmTextView.setText(pnsn_entr_age_nm);
                mon_paym_atm_nmTextView.setText(mon_paym_atm_nm);
                paym_prd_nmTextView.setText(paym_prd_nm);
                pnsn_strt_age_nmTextView.setText(pnsn_strt_age_nm);
                pnsn_recp_amtTextView.setText(pnsn_recp_amt);

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

    public String toAmount(String str) {        // 천 단위로 콤마 붙여줌
        if(str.equals("")) return "없음";
        else {
            DecimalFormat df = new DecimalFormat("###,###");

            return df.format(Long.parseLong(str)) + "원";
        }
    }

    public void getData(String url, String finPrdtCd) {
        if(url.contains("PHP_annuity_saving_int")) {            // 연금 상품 출력
            class GetDataJSON extends AsyncTask<String, Void, String> {
                // AsyncTask에서 execute() 메서드가 호출되면 내부적으로 doInBackground() 메서드가 호출
                @Override
                protected String doInBackground(String... params) {

                    String uri = params[0];
                    String finPrdtCd = params[1]; // 추가된 파라미터 finPrdtCd

                    try {
                        URL url = new URL(uri + "?finPrdtCd=" + finPrdtCd); // 파라미터를 URL에 추가
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
            g.execute(url, finPrdtCd);      // 아래도 수정 필요!
        }

        else if(url.contains("PHP_annuity_saving_in_list")) {              // 연금 상품 옵션 리스트 출력
            class GetDataJSON extends AsyncTask<String, Void, String> {
                // AsyncTask에서 execute() 메서드가 호출되면 내부적으로 doInBackground() 메서드가 호출
                @Override
                protected String doInBackground(String... params) {

                    String uri = params[0];
                    String finPrdtCd = params[1]; // 추가된 파라미터 finPrdtCd

                    try {
                        URL url = new URL(uri + "?finPrdtCd=" + finPrdtCd); // 파라미터를 URL에 추가
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
            g.execute(url, finPrdtCd);      // 아래도 수정 필요!
        }
    }
}