package com.example.fin_palette;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.net.Uri;
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

public class CreditLoanDetailScreen extends AppCompatActivity {

    Button homepageButton;
    String myJSON;
    String optionId;
    String bookmarkId;
    String historyId;
    String homepageUrl;

    private static final String TAG_RESULT = "result";
    private static final String TAG_FINANCIAL_COMPANY_NAME = "financial_company_name";
    private static final String TAG_FINANCIAL_PRODUCT_NAME = "financial_product_name";
    private static final String TAG_CREDIT_PRODUCT_TYPE_NAME = "credit_product_type_name";
    private static final String TAG_CB_COMPANY_NAME = "cb_company_name";
    private static final String TAG_LOWEST_LOAN_RATE = "lowest_loan_rate";
    private static final String TAG_AVERAGE_CREDIT_LOAN_RATE = "average_credit_loan_rate";
    private static final String TAG_DISCLOSURE_START_TO_END_DATE = "disclosure_start_to_end_date";
    private static final String TAG_JOIN_WAY = "join_way";
    private static final String TAG_CREDIT_LOAN_RATE_TYPE_NAME = "credit_loan_rate_type_name";
    private static final String TAG_DISCLOSURE_OFFICER = "disclosure_officer";
    private static final String TAG_HOMEPAGE_URL = "homepage_url";


    /////////////////////// 여기부턴 credit_loan_detail_list ///////////////////////
    ListView list;
    String myJSON2;
    String lowestLoanRateValue;
    JSONArray products2 = null;
    ArrayList<HashMap<String, String>> productList;

    private static final String TAG_CREDIT_SCORE_ABOVE_900 = "credit_score_above_900";
    private static final String TAG_CREDIT_SCORE_801_TO_900 = "credit_score_801_to_900";
    private static final String TAG_CREDIT_SCORE_701_TO_800 = "credit_score_701_to_800";
    private static final String TAG_CREDIT_SCORE_601_TO_700 = "credit_score_601_to_700";
    private static final String TAG_CREDIT_SCORE_501_TO_600 = "credit_score_501_to_600";
    private static final String TAG_CREDIT_SCORE_401_TO_500 = "credit_score_401_to_500";
    private static final String TAG_CREDIT_SCORE_301_TO_400 = "credit_score_301_to_400";
    private static final String TAG_CREDIT_SCORE_BELOW_300 = "credit_score_below_300";

    /////////////////////////////////////////////////////////////////////////////////

    JSONArray products = null;


    ApiServerManager asm = new ApiServerManager();
    String apiEndpoint = asm.getApiEndpoint();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.credit_loan_detail_screen);

        // Intent에서 Extra로 전달된 데이터 받아오기
        Intent intentGet = getIntent();
        if (intentGet != null) {
            String receivedOptionId = intentGet.getStringExtra("optionId");

            // 받아온 값 확인
            if (receivedOptionId != null) {
                optionId = intentGet.getStringExtra("optionId");

                getData(apiEndpoint + "/credit_loan_int.php", optionId, false);
                getData(apiEndpoint + "/credit_loan_opt_list.php", optionId, true);
            }
        }

        //////////////////////// 북마크 /////////////////////////////////
        ImageView star = findViewById(R.id.bookmark);
        bookmarkId = historyId = 6 + "_" + optionId + '_';
        BookmarkManager bs = new BookmarkManager(this);
        AaidManager am = new AaidManager();
        bs.getData(apiEndpoint + "/bookmark_chk.php", bookmarkId, am.aaid, star);

        star.setOnClickListener(new View.OnClickListener() {   // 북마크 이미지 뷰 클릭하면 북마크 기능
            @Override
            public void onClick(View v) {
                if(bs.marked) star.setImageResource(R.drawable.empty_star_small);
                else star.setImageResource(R.drawable.full_star_small);

                bs.setData(apiEndpoint + "/bookmark_upd.php", 6, optionId, "", am.aaid);
            }
        });

        ///////////////////////////////// 조회 기록
        ViewHistoryManager vs = new ViewHistoryManager(this);
        vs.getData(apiEndpoint, historyId, am.aaid, 6, optionId, "");


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
            TextView crdtPrdtTypeNameTextView = findViewById(R.id.crdt_prdt_type_nm);
            TextView cbCoNameTextView = findViewById(R.id.cb_co_nm);
            TextView lowestLoanRateValueTextView = findViewById(R.id.lowest_loan_rate_value);
            TextView lowestLoanRateTextView = findViewById(R.id.lowest_loan_rate);
            TextView avgCrdtLoanRateTextView = findViewById(R.id.avg_crdt_loan_rate);
            TextView dsclStrtEndDayTextView = findViewById(R.id.dscl_strt_end_day);
            TextView joinWayTextView = findViewById(R.id.join_way);
            TextView crdtLoanRateTypeNameTextView = findViewById(R.id.crdt_loan_rate_type_nm);
            TextView dsclOfficerTextView = findViewById(R.id.dscl_officer);

            JSONObject jsonObj = new JSONObject(myJSON);
            products = jsonObj.getJSONArray(TAG_RESULT);

            JSONObject c = products.getJSONObject(0);
            lowestLoanRateValue = nullToSpace(c.getString(TAG_LOWEST_LOAN_RATE)) + "%";

            finCoNameTextView.setText(nullToSpace(c.getString(TAG_FINANCIAL_COMPANY_NAME)));
            finPrdtNameTextView.setText(nullToSpace(c.getString(TAG_FINANCIAL_PRODUCT_NAME)));
            crdtPrdtTypeNameTextView.setText(nullToSpace(c.getString(TAG_CREDIT_PRODUCT_TYPE_NAME)));
            cbCoNameTextView.setText(nullToSpace(c.getString(TAG_CB_COMPANY_NAME)));
            lowestLoanRateValueTextView.setText("최저 " + lowestLoanRateValue);
            lowestLoanRateTextView.setText(lowestLoanRateValue);
            avgCrdtLoanRateTextView.setText(nullToSpace(c.getString(TAG_AVERAGE_CREDIT_LOAN_RATE )) + "%");
            dsclStrtEndDayTextView.setText(nullToSpace(c.getString(TAG_DISCLOSURE_START_TO_END_DATE)));
            joinWayTextView.setText(nullToSpace(c.getString(TAG_JOIN_WAY)));
            crdtLoanRateTypeNameTextView.setText(nullToSpace(c.getString(TAG_CREDIT_LOAN_RATE_TYPE_NAME)));
            dsclOfficerTextView.setText(nullToSpace(c.getString(TAG_DISCLOSURE_OFFICER)));
            homepageUrl = nullToSpace(c.getString(TAG_HOMEPAGE_URL));


            if(crdtPrdtTypeNameTextView.getText().toString().equals("일반신용대출")) {
                crdtPrdtTypeNameTextView.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.magenta)));
            }
            else if(crdtPrdtTypeNameTextView.getText().toString().equals("마이너스한도대출")) {
                crdtPrdtTypeNameTextView.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.crown_flo)));
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


    protected void showList() {
        try {
            JSONObject jsonObj = new JSONObject(myJSON2);
            products = jsonObj.getJSONArray(TAG_RESULT);

            LinearLayout linearLayout = findViewById(R.id.linearLpo3); // ScrollView 안의 LinearLayout ID 설정
            LayoutInflater inflater = LayoutInflater.from(this);
            linearLayout.removeAllViews(); // 기존 뷰들을 모두 제거

            for (int i = 0; i < products.length(); i++) {
                JSONObject c = products.getJSONObject(i);
                String crdtScrAbove900 = nullToSpace(c.getString(TAG_CREDIT_SCORE_ABOVE_900)) + "%";
                String crdtScr801To900 = nullToSpace(c.getString(TAG_CREDIT_SCORE_801_TO_900)) + "%";
                String crdtScr701To800 = nullToSpace(c.getString(TAG_CREDIT_SCORE_701_TO_800)) + "%";
                String crdtScr601To700 = nullToSpace(c.getString(TAG_CREDIT_SCORE_601_TO_700)) + "%";
                String crdtScr501To600 = nullToSpace(c.getString(TAG_CREDIT_SCORE_501_TO_600)) + "%";
                String crdtScr401To500 = nullToSpace(c.getString(TAG_CREDIT_SCORE_401_TO_500)) + "%";
                String crdtScr301To400 = nullToSpace(c.getString(TAG_CREDIT_SCORE_301_TO_400)) + "%";
                String crdtScrBelow300 = nullToSpace(c.getString(TAG_CREDIT_SCORE_BELOW_300)) + "%";

                // 최저가 어딘 구간인지 설정
                TextView lowestLoanRateNameTextView = findViewById(R.id.lowest_loan_rate_name);

                if(lowestLoanRateValue.equals(crdtScrAbove900)) lowestLoanRateNameTextView.setText("900점 초과");
                else if(lowestLoanRateValue.equals(crdtScr801To900)) lowestLoanRateNameTextView.setText("801~900점");
                else if(lowestLoanRateValue.equals(crdtScr701To800)) lowestLoanRateNameTextView.setText("701~800점");
                else if(lowestLoanRateValue.equals(crdtScr601To700)) lowestLoanRateNameTextView.setText("601~700점");
                else if(lowestLoanRateValue.equals(crdtScr501To600)) lowestLoanRateNameTextView.setText("501~600점");
                else if(lowestLoanRateValue.equals(crdtScr401To500)) lowestLoanRateNameTextView.setText("401~500점");
                else if(lowestLoanRateValue.equals(crdtScr301To400)) lowestLoanRateNameTextView.setText("301~400점");
                else lowestLoanRateNameTextView.setText("300점 이하");

                // annuity_saving_detail_list.xml을 inflate하여 새로운 뷰 생성
                View itemView = inflater.inflate(R.layout.credit_loan_detail_list, null);

                TextView crdtScrAbove900TextView = itemView.findViewById(R.id.crdt_scr_above_900);
                TextView crdtScr801To900TextView = itemView.findViewById(R.id.crdt_scr_801_to_900);
                TextView crdtScr701To800TextView = itemView.findViewById(R.id.crdt_scr_701_to_800);
                TextView crdtScr601To700TextView = itemView.findViewById(R.id.crdt_scr_601_to_700);
                TextView crdtScr501To600TextView = itemView.findViewById(R.id.crdt_scr_501_to_600);
                TextView crdtScr401To500TextView = itemView.findViewById(R.id.crdt_scr_401_to_500);
                TextView crdtScr301To400TextView = itemView.findViewById(R.id.crdt_scr_301_to_400);
                TextView crdtScrBelow300TextView = itemView.findViewById(R.id.crdt_scr_below_300);

                crdtScrAbove900TextView.setText(crdtScrAbove900);
                crdtScr801To900TextView.setText(crdtScr801To900);
                crdtScr701To800TextView.setText(crdtScr701To800);
                crdtScr601To700TextView.setText(crdtScr601To700);
                crdtScr501To600TextView.setText(crdtScr501To600);
                crdtScr401To500TextView.setText(crdtScr401To500);
                crdtScr301To400TextView.setText(crdtScr301To400);
                crdtScrBelow300TextView.setText(crdtScrBelow300);

                // 생성된 뷰를 ScrollView 안의 LinearLayout에 추가
                linearLayout.addView(itemView);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


    public String nullToSpace(String str) {
        if(str.equals("null") || str.equals("없음")) return "";
        else return str;
    }


    public void getData(String url, String optionId, boolean isOptList) {
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
                    if (result != null) {
//                    Toast.makeText(getApplicationContext(), result, Toast.LENGTH_SHORT).show();
                        if(isOptList) {
                            myJSON2 = result;
                            showList();
                        }
                        else {
                            myJSON = result;
                            showWindow();
                        }
                    }
                }
            }

            GetDataJSON g = new GetDataJSON();
            g.execute(url, optionId);      // 아래도 수정 필요!

    }
}