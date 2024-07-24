package com.example.fin_palette;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.net.Uri;
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

public class RentHouseLoanDetailScreen extends AppCompatActivity {

    Button homepageButton;
    String myJSON;
    String optionId;
    String bookmarkId;
    String historyId;
    String homepageUrl;

    private static final String TAG_RESULT = "result";
    private static final String TAG_FINANCIAL_COMPANY_NAME = "financial_company_name";
    private static final String TAG_FINANCIAL_PRODUCT_NAME = "financial_product_name";
    private static final String TAG_LOAN_RATE_TYPE = "loan_rate_type";
    private static final String TAG_LOAN_REPAYMENT_TYPE = "loan_repayment_type";
    private static final String TAG_LOWEST_LOAN_RATE = "lowest_loan_rate";
    private static final String TAG_MINIMUM_LOAN_RATE = "minimum_loan_rate";
    private static final String TAG_AVERAGE_LOAN_RATE = "average_loan_rate";
    private static final String TAG_MAXIMUM_LOAN_RATE = "maximum_loan_rate";
    private static final String TAG_DISCLOSURE_START_TO_END_DATE = "disclosure_start_to_end_date";
    private static final String TAG_LOAN_LIMIT = "loan_limit";
    private static final String TAG_LOAN_INCIDENTAL_EXPENSES = "loan_incidental_expenses";
    private static final String TAG_EARLY_REPAYMENT_FEE = "early_repayment_fee";
    private static final String TAG_DELINQUENCY_RATE = "delinquency_rate";
    private static final String TAG_JOIN_WAY = "join_way";
    private static final String TAG_DISCLOSURE_OFFICER = "disclosure_officer";
    private static final String TAG_HOMEPAGE_URL = "homepage_url";


    JSONArray products = null;


    ApiServerManager asm = new ApiServerManager();
    String apiEndpoint = asm.getApiEndpoint();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rent_house_loan_detail_screen);

        // Intent에서 Extra로 전달된 데이터 받아오기
        Intent intentGet = getIntent();
        if (intentGet != null) {
            String receivedOptionId = intentGet.getStringExtra("optionId");

            // 받아온 값 확인
            if (receivedOptionId != null) {
                optionId = intentGet.getStringExtra("optionId");

                getData(apiEndpoint + "/rent_house_loan_int.php", optionId);
            }
        }


        //////////////////////// 북마크 /////////////////////////////////
        ImageView star = findViewById(R.id.bookmark);
        bookmarkId = historyId = 4 + "_" + optionId + '_';
        BookmarkManager bs = new BookmarkManager(this);
        AaidManager am = new AaidManager();
        bs.getData(apiEndpoint + "/bookmark_chk.php", bookmarkId, am.aaid, star);

        star.setOnClickListener(new View.OnClickListener() {   // 북마크 이미지 뷰 클릭하면 북마크 기능
            @Override
            public void onClick(View v) {
                if(bs.marked) star.setImageResource(R.drawable.empty_star_small);
                else star.setImageResource(R.drawable.full_star_small);

                bs.setData(apiEndpoint + "/bookmark_upd.php", 4, optionId, "", am.aaid);
            }
        });

        ///////////////////////////////// 조회 기록
        ViewHistoryManager vs = new ViewHistoryManager(this);
        vs.getData(apiEndpoint, historyId, am.aaid, 4, optionId, "");


        ///// 홈페이지 버튼
        homepageButton = findViewById(R.id.btn_homepage);
        homepageButton.setOnClickListener(view -> {
            if(homepageUrl.equals("")) Toast.makeText(getApplicationContext(), "공식 홈페이지 정보가 없습니다", Toast.LENGTH_SHORT).show();
            else {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(homepageUrl));
                startActivity(intent);
            }
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
            TextView loanRateTypeTextView = findViewById(R.id.loan_rate_type);
            TextView loanRpayTypeTextView = findViewById(R.id.loan_rpay_type);
            TextView lowestLoanRateTextView = findViewById(R.id.lowest_loan_rate);
            TextView minLoanRateTextView = findViewById(R.id.min_loan_rate);
            TextView avgLoanRateTextView = findViewById(R.id.avg_loan_rate);
            TextView maxLoanRateTextView = findViewById(R.id.max_loan_rate);
            TextView dsclStrtEndDayTextView = findViewById(R.id.dscl_strt_end_day);
            TextView loanLmtTextView = findViewById(R.id.loan_lmt);
            TextView loanInciExpnTextView = findViewById(R.id.loan_inci_expn);
            TextView erlyRpayFeeTextView = findViewById(R.id.erly_rpay_fee);
            TextView dlyRateTextView = findViewById(R.id.dly_rate);
            TextView joinWayTextView = findViewById(R.id.join_way);
            TextView dsclOfficerTextView = findViewById(R.id.dscl_officer);

            JSONObject jsonObj = new JSONObject(myJSON);
            products = jsonObj.getJSONArray(TAG_RESULT);

            JSONObject c = products.getJSONObject(0);
            finCoNameTextView.setText(nullToSpace(c.getString(TAG_FINANCIAL_COMPANY_NAME)));
            finPrdtNameTextView.setText(nullToSpace(c.getString(TAG_FINANCIAL_PRODUCT_NAME)));
            loanRateTypeTextView.setText(nullToSpace(c.getString(TAG_LOAN_RATE_TYPE)));
            loanRpayTypeTextView.setText(nullToSpace(c.getString(TAG_LOAN_REPAYMENT_TYPE).replace("방식", "")));
            lowestLoanRateTextView.setText("최저 " + nullToSpace(c.getString(TAG_LOWEST_LOAN_RATE)) + "%");
            minLoanRateTextView.setText(nullToSpace(c.getString(TAG_MINIMUM_LOAN_RATE)) + "%");
            avgLoanRateTextView.setText(nullToSpace(c.getString(TAG_AVERAGE_LOAN_RATE )) + "%");
            maxLoanRateTextView.setText(nullToSpace(c.getString(TAG_MAXIMUM_LOAN_RATE)) + "%");
            dsclStrtEndDayTextView.setText(nullToSpace(c.getString(TAG_DISCLOSURE_START_TO_END_DATE)));
            loanLmtTextView.setText(nullToSpace(c.getString(TAG_LOAN_LIMIT)));
            loanInciExpnTextView.setText(nullToSpace(c.getString(TAG_LOAN_INCIDENTAL_EXPENSES)));
            erlyRpayFeeTextView.setText(nullToSpace(c.getString(TAG_EARLY_REPAYMENT_FEE)));
            dlyRateTextView.setText(nullToSpace(c.getString(TAG_DELINQUENCY_RATE)));
            joinWayTextView.setText(nullToSpace(c.getString(TAG_JOIN_WAY)));
            dsclOfficerTextView.setText(nullToSpace(c.getString(TAG_DISCLOSURE_OFFICER)));
            homepageUrl = nullToSpace(c.getString(TAG_HOMEPAGE_URL));

            if(loanRateTypeTextView.getText().toString().equals("변동금리")) {
                loanRateTypeTextView.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.teal_700)));
            }

            if(loanRpayTypeTextView.getText().toString().equals("분할상환")) {
                loanRpayTypeTextView.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.magenta)));
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public String nullToSpace(String str) {
        if(str.equals("null") || str.equals("없음")) return "";
        else return str;
    }


    public void getData(String url, String optionId) {
        class GetDataJSON extends AsyncTask<String, Void, String> {
            // AsyncTask에서 execute() 메서드가 호출되면 내부적으로 doInBackground() 메서드가 호출
            @Override
            protected String doInBackground(String... params) {

                String uri = params[0];
                String optionId = params[1]; // 추가된 파라미터 optionId

                try {
                    URL url = new URL(uri + "?optionId=" + optionId); // 파라미터를 URL에 추가
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
        g.execute(url, optionId);      // 아래도 수정 필요!
    }
}