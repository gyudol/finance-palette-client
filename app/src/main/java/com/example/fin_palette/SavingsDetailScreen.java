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

public class SavingsDetailScreen extends AppCompatActivity {

    Button homeButton;

    String myJSON;
    String finPrdtId;
    String intrRateType;
    String accrualType;
    String bookmarkId;
    String historyId;

    private static final String TAG_RESULT = "result";
    private static final String TAG_FINANCIAL_COMPANY_NAME = "financial_company_name";
    private static final String TAG_FINANCIAL_PRODUCT_NAME = "financial_product_name";
    private static final String TAG_INTEREST_RATE_TYPE_NAME = "interest_rate_type_name";
    private static final String TAG_ACCRUAL_TYPE_NAME = "accrual_type_name";
    private static final String TAG_HIGHEST_INTEREST_RATE = "highest_interest_rate";
    private static final String TAG_INTEREST_RATE_1 = "interest_rate_1";
    private static final String TAG_INTEREST_RATE_3 = "interest_rate_3";
    private static final String TAG_INTEREST_RATE_6 = "interest_rate_6";
    private static final String TAG_INTEREST_RATE_12 = "interest_rate_12";
    private static final String TAG_INTEREST_RATE_24 = "interest_rate_24";
    private static final String TAG_INTEREST_RATE_36 = "interest_rate_36";
    private static final String TAG_DISCLOSURE_START_TO_END_DATE = "disclosure_start_to_end_date";
    private static final String TAG_JOIN_TARGET = "join_target";
    private static final String TAG_JOIN_RESTRICTION = "join_restriction";
    private static final String TAG_MAXIMUM_LIMIT = "maximum_limit";
    private static final String TAG_PREFERENTIAL_CONDITION = "preferential_condition";
    private static final String TAG_INTEREST_RATE_AFTER_MATURITY = "interest_rate_after_maturity";
    private static final String TAG_JOIN_WAY = "join_way";
    private static final String TAG_OTHER_PRECAUTION= "other_precaution";
    private static final String TAG_DISCLOSURE_OFFICER = "disclosure_officer";


    JSONArray products = null;


    ApiServerManager asm = new ApiServerManager();
    String apiEndpoint = asm.getApiEndpoint();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.savings_detail_screen);

        // Intent에서 Extra로 전달된 데이터 받아오기
        Intent intentGet = getIntent();
        if (intentGet != null) {
            String receivedfinPrdtId = intentGet.getStringExtra("finPrdtId");
            String receivedIntrRateType = intentGet.getStringExtra("intrRateType");
            String receivedAccrualType = intentGet.getStringExtra("accrualType");

            // 받아온 값 확인
            if (receivedfinPrdtId != null && receivedIntrRateType != null && receivedAccrualType != null) {
                finPrdtId = intentGet.getStringExtra("finPrdtId");
                intrRateType = intentGet.getStringExtra("intrRateType");
                accrualType = intentGet.getStringExtra("accrualType");

                getData(apiEndpoint + "/savings_int.php", finPrdtId, intrRateType, accrualType);
            }
        }


        //////////////////////// 북마크 /////////////////////////////////
        ImageView star = findViewById(R.id.bookmark);
        bookmarkId = historyId = 2 + "_" + finPrdtId + '_' + intrRateType + '_' + accrualType;
        BookmarkManager bs = new BookmarkManager(this);
        AaidManager am = new AaidManager();
        bs.getData(apiEndpoint + "/bookmark_chk.php", bookmarkId, am.aaid, star);

        star.setOnClickListener(new View.OnClickListener() {   // 북마크 이미지 뷰 클릭하면 북마크 기능
            @Override
            public void onClick(View v) {
                if(bs.marked) star.setImageResource(R.drawable.empty_star_small);
                else star.setImageResource(R.drawable.full_star_small);

                bs.setData(apiEndpoint + "/bookmark_upd.php", 2, finPrdtId, intrRateType + '_' + accrualType, am.aaid);
            }
        });


        ///////////////////////////////// 조회 기록
        ViewHistoryManager vs = new ViewHistoryManager(this);
        vs.getData(apiEndpoint, historyId, am.aaid, 2, finPrdtId, intrRateType + '_' + accrualType);


        homeButton = findViewById(R.id.btn_home);
        homeButton.setOnClickListener(view -> {
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
            TextView finCoNameTextView = findViewById(R.id.fin_co_nm);
            TextView finPrdtNameTextView = findViewById(R.id.fin_prdt_nm);
            TextView intrRateTypeNameTextView = findViewById(R.id.intr_rate_type_nm);
            TextView accrualTypeNameTextView = findViewById(R.id.accrual_type_nm);
            TextView highestIntrRateTextView = findViewById(R.id.highest_intr_rate);
            TextView intrRate1TextView = findViewById(R.id.intr_rate_1);
            TextView intrRate3TextView = findViewById(R.id.intr_rate_3);
            TextView intrRate6TextView = findViewById(R.id.intr_rate_6);
            TextView intrRate12TextView = findViewById(R.id.intr_rate_12);
            TextView intrRate24TextView = findViewById(R.id.intr_rate_24);
            TextView intrRate36TextView = findViewById(R.id.intr_rate_36);
            TextView dsclStrtEndDayTextView = findViewById(R.id.dscl_strt_end_day);
            TextView joinTargetTextView = findViewById(R.id.join_target);
            TextView joinRestrictionTextView = findViewById(R.id.join_restriction);
            TextView maxLimitTextView = findViewById(R.id.max_limit);
            TextView preferCndTextView = findViewById(R.id.prefer_cnd);
            TextView intrRateAfterMtrtTextView = findViewById(R.id.intr_rate_after_mtrt);
            TextView joinWayTextView = findViewById(R.id.join_way);
            TextView otherPrecTextView = findViewById(R.id.other_prec);
            TextView dsclOfficerTextView = findViewById(R.id.dscl_officer);

            JSONObject jsonObj = new JSONObject(myJSON);
            products = jsonObj.getJSONArray(TAG_RESULT);

            JSONObject c = products.getJSONObject(0);
            finCoNameTextView.setText(nullToSpace(c.getString(TAG_FINANCIAL_COMPANY_NAME)));
            finPrdtNameTextView.setText(nullToSpace(c.getString(TAG_FINANCIAL_PRODUCT_NAME)));
            intrRateTypeNameTextView.setText(nullToSpace(c.getString(TAG_INTEREST_RATE_TYPE_NAME)));
            accrualTypeNameTextView.setText(nullToSpace(c.getString(TAG_ACCRUAL_TYPE_NAME)));
            highestIntrRateTextView.setText("최고 " + nullToSpace(c.getString(TAG_HIGHEST_INTEREST_RATE)) + "%");
            intrRate1TextView.setText(nullToSpace(c.getString(TAG_INTEREST_RATE_1)) + "%");
            intrRate3TextView.setText(nullToSpace(c.getString(TAG_INTEREST_RATE_3)) + "%");
            intrRate6TextView.setText(nullToSpace(c.getString(TAG_INTEREST_RATE_6)) + "%");
            intrRate12TextView.setText(nullToSpace(c.getString(TAG_INTEREST_RATE_12)) + "%");
            intrRate24TextView.setText(nullToSpace(c.getString(TAG_INTEREST_RATE_24)) + "%");
            intrRate36TextView.setText(nullToSpace(c.getString(TAG_INTEREST_RATE_36)) + "%");
            dsclStrtEndDayTextView.setText(nullToSpace(c.getString(TAG_DISCLOSURE_START_TO_END_DATE)));
            joinTargetTextView.setText(nullToSpace(c.getString(TAG_JOIN_TARGET)));
            joinRestrictionTextView.setText(nullToSpace(c.getString(TAG_JOIN_RESTRICTION)).equals("1")?"제한 없음":
                    nullToSpace(c.getString(TAG_JOIN_RESTRICTION)).equals("2")?"서민 전용":nullToSpace(c.getString(TAG_JOIN_RESTRICTION)).equals("2")?"일부 제한":"");
            maxLimitTextView.setText(toAmount(nullToSpace(c.getString(TAG_MAXIMUM_LIMIT))));
            preferCndTextView.setText(nullToSpace(c.getString(TAG_PREFERENTIAL_CONDITION)));
            intrRateAfterMtrtTextView.setText(nullToSpace(c.getString(TAG_INTEREST_RATE_AFTER_MATURITY)));
            joinWayTextView.setText(nullToSpace(c.getString(TAG_JOIN_WAY)));
            otherPrecTextView.setText(nullToSpace(c.getString(TAG_OTHER_PRECAUTION)));
            dsclOfficerTextView.setText(nullToSpace(c.getString(TAG_DISCLOSURE_OFFICER)));

            if(intrRateTypeNameTextView.getText().toString().equals("복리")) {
//                Toast.makeText(getApplicationContext(), "color changed", Toast.LENGTH_SHORT).show();
                intrRateTypeNameTextView.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.orange)));
            }
            if(accrualTypeNameTextView.getText().toString().equals("자유적립식")) {
//                Toast.makeText(getApplicationContext(), "color changed", Toast.LENGTH_SHORT).show();
                accrualTypeNameTextView.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.mid_blue)));
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public String nullToSpace(String str) {
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


    public void getData(String url, String finPrdtId, String intrRateType, String accrualType) {
        class GetDataJSON extends AsyncTask<String, Void, String> {
            // AsyncTask에서 execute() 메서드가 호출되면 내부적으로 doInBackground() 메서드가 호출
            @Override
            protected String doInBackground(String... params) {

                String uri = params[0];
                String finPrdtId = params[1]; // 추가된 파라미터 finPrdtId
                String intrRateType = params[2];
                String accrualType = params[3];

                try {
                    URL url = new URL(uri + "?finPrdtId=" + finPrdtId + "&intrRateType=" + intrRateType + "&accrualType=" + accrualType); // 파라미터를 URL에 추가
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
                    // Toast.makeText(getApplicationContext(), result, Toast.LENGTH_SHORT).show();
                    myJSON = result;
                    showWindow();
                }
            }
        }

        GetDataJSON g = new GetDataJSON();
        g.execute(url, finPrdtId, intrRateType, accrualType);      // 아래도 수정 필요!
    }
}