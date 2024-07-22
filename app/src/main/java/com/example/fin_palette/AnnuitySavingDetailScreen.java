package com.example.fin_palette;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AnnuitySavingDetailScreen extends AppCompatActivity {

    Button homeButton;

    String myJSON;
    String finPrdtId;
    String bookmarkId;
    String historyId;

    private static final String TAG_RESULT = "result";
    private static final String TAG_FINANCIAL_COMPANY_NAME = "financial_company_name";
    private static final String TAG_FINANCIAL_PRODUCT_NAME = "financial_product_name";
    private static final String TAG_PENSION_TYPE_NAME = "pension_type_name";
    private static final String TAG_PRODUCT_TYPE_NAME = "product_type_name";
    private static final String TAG_AVERAGE_PROFIT_RATE = "average_profit_rate";
    private static final String TAG_PAST_PROFIT_RATE_1 = "past_profit_rate_1";
    private static final String TAG_PAST_PROFIT_RATE_2 = "past_profit_rate_2";
    private static final String TAG_PAST_PROFIT_RATE_3 = "past_profit_rate_3";
    private static final String TAG_SALE_START_DATE = "sale_start_date";
    private static final String TAG_DISCLOSURE_START_TO_END_DATE = "disclosure_start_to_end_date";
    private static final String TAG_JOIN_WAY = "join_way";
    private static final String TAG_SALES_COMPANY = "sales_company";
    private static final String TAG_REMARKS = "remarks";
    private static final String TAG_MAINTENANCE_COUNT = "maintenance_count";
    private static final String TAG_DISCLOSURE_INTEREST_RATE = "disclosure_interest_rate";
    private static final String TAG_GUARANTEED_INTEREST_RATE = "guaranteed_interest_rate";


    /////////////////////// 여기부턴 annuity_saving_detail_list ///////////////////////
    ListView list;
    String myJSON2;
    JSONArray products2 = null;
    ArrayList<HashMap<String, String>> productList;

    private static final String TAG_PENSION_RECEPTION_TERM = "pension_reception_term";
    private static final String TAG_PENSION_ENTRY_AGE = "pension_entry_age";
    private static final String TAG_MONTHLY_PAYMENT_AMOUNT = "monthly_payment_amount";
    private static final String TAG_PAYMENT_PERIOD = "payment_period";
    private static final String TAG_PENSION_START_AGE = "pension_start_age";
    private static final String TAG_PENSION_RECEPTION_AMOUNT = "pension_reception_amount";

    /////////////////////////////////////////////////////////////////////////////////

    JSONArray products = null;


    ApiServerManager asm = new ApiServerManager();
    String apiEndpoint = asm.getApiEndpoint();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.annuity_saving_detail_screen);
        productList = new ArrayList<HashMap<String, String>>();

        // Intent에서 Extra로 전달된 데이터 받아오기
        Intent intentGet = getIntent();
        if (intentGet != null) {
            String receivedfinPrdtId = intentGet.getStringExtra("finPrdtId");

            // 받아온 값 확인
            if (receivedfinPrdtId != null) {
                finPrdtId = intentGet.getStringExtra("finPrdtId");

                getData(apiEndpoint + "/annuity_saving_int.php", finPrdtId, false);
                getData(apiEndpoint + "/annuity_saving_opt_list.php", finPrdtId, true);
            }
        }

        //////////////////////// 북마크 /////////////////////////////////
        ImageView star = findViewById(R.id.bookmark);
        bookmarkId = historyId = 3 + "_" + finPrdtId + '_';
        BookmarkManager bs = new BookmarkManager(this);
        AaidManager am = new AaidManager();
        bs.getData(apiEndpoint + "/bookmark_chk.php", bookmarkId, am.aaid, star);

        star.setOnClickListener(new View.OnClickListener() {   // 북마크 이미지 뷰 클릭하면 북마크 기능
            @Override
            public void onClick(View v) {
                if(bs.marked) star.setImageResource(R.drawable.empty_star_small);
                else star.setImageResource(R.drawable.full_star_small);

                bs.setData(apiEndpoint + "/bookmark_upd.php", 3, finPrdtId, "", am.aaid);
            }
        });

        ///////////////////////////////// 조회 기록
        ViewHistoryManager vs = new ViewHistoryManager(this);
        vs.getData(apiEndpoint, historyId, am.aaid, 3, finPrdtId, "");


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
            TextView pnsnTypeNameTextView = findViewById(R.id.pnsn_type_nm);
            TextView prdtTypeNameTextView = findViewById(R.id.prdt_type_nm);
            TextView avgPrftRateTextView = findViewById(R.id.avg_prft_rate);
            TextView pastPrftRate1TextView = findViewById(R.id.past_prft_rate_1);
            TextView pastPrftRate2TextView = findViewById(R.id.past_prft_rate_2);
            TextView pastPrftRate3TextView = findViewById(R.id.past_prft_rate_3);
            TextView saleStrtDayTextView = findViewById(R.id.sale_strt_day);
            TextView dsclStrtEndDayTextView = findViewById(R.id.dscl_strt_end_day);
            TextView joinWayTextView = findViewById(R.id.join_way);
            TextView saleCoTextView = findViewById(R.id.sale_co);
            TextView remarksTextView = findViewById(R.id.remarks);
            TextView mntnCntTextView = findViewById(R.id.mntn_cnt);
            TextView dsclRateTextView = findViewById(R.id.dscl_rate);
            TextView guarRateTextView = findViewById(R.id.guar_rate);

            JSONObject jsonObj = new JSONObject(myJSON);
            products = jsonObj.getJSONArray(TAG_RESULT);

            JSONObject c = products.getJSONObject(0);
            finCoNameTextView.setText(nullToSpace(c.getString(TAG_FINANCIAL_COMPANY_NAME)));
            finPrdtNameTextView.setText(nullToSpace(c.getString(TAG_FINANCIAL_PRODUCT_NAME)));
            pnsnTypeNameTextView.setText(nullToSpace(c.getString(TAG_PENSION_TYPE_NAME)));
            prdtTypeNameTextView.setText(nullToSpace(c.getString(TAG_PRODUCT_TYPE_NAME )));
            avgPrftRateTextView.setText(nullToSpace(c.getString(TAG_AVERAGE_PROFIT_RATE)) + "%");
            pastPrftRate1TextView.setText(nullToSpace(c.getString(TAG_PAST_PROFIT_RATE_1)) + "%");
            pastPrftRate2TextView.setText(nullToSpace(c.getString(TAG_PAST_PROFIT_RATE_2)) + "%");
            pastPrftRate3TextView.setText(nullToSpace(c.getString(TAG_PAST_PROFIT_RATE_3)) + "%");
            saleStrtDayTextView.setText(nullToSpace(c.getString(TAG_SALE_START_DATE)));
            dsclStrtEndDayTextView.setText(nullToSpace(c.getString(TAG_DISCLOSURE_START_TO_END_DATE)));
            joinWayTextView.setText(nullToSpace(c.getString(TAG_JOIN_WAY)));
            saleCoTextView.setText(nullToSpace(c.getString(TAG_SALES_COMPANY)));
            remarksTextView.setText(nullToSpace(c.getString(TAG_REMARKS)));
            mntnCntTextView.setText(nullToSpace(c.getString(TAG_MAINTENANCE_COUNT)));
            dsclRateTextView.setText(nullToSpace(c.getString(TAG_DISCLOSURE_INTEREST_RATE)));
            guarRateTextView.setText(nullToSpace(c.getString(TAG_GUARANTEED_INTEREST_RATE)));

            if(pnsnTypeNameTextView.getText().toString().equals("연금저축보험(생명)")) {
                pnsnTypeNameTextView.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.purple_500)));
            }
            else if(pnsnTypeNameTextView.getText().toString().equals("연금저축보험(손해)")) {
                pnsnTypeNameTextView.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.purple_700)));
            }

            if(prdtTypeNameTextView.getText().toString().equals("재간접형")) {
                prdtTypeNameTextView.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.crown_flo)));
            }
            else if(prdtTypeNameTextView.getText().toString().equals("채권형")) {
                prdtTypeNameTextView.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.mid_green)));
            }
            else if(prdtTypeNameTextView.getText().toString().equals("금리연동형")) {
                prdtTypeNameTextView.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.brown_green)));
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    protected void showList() {
        try {
            JSONObject jsonObj = new JSONObject(myJSON2);
            products = jsonObj.getJSONArray(TAG_RESULT);

            LinearLayout linearLayout = findViewById(R.id.linearAspo); // ScrollView 안의 LinearLayout ID 설정
            LayoutInflater inflater = LayoutInflater.from(this);
            linearLayout.removeAllViews(); // 기존 뷰들을 모두 제거

            for (int i = 0; i < products.length(); i++) {
                JSONObject c = products.getJSONObject(i);
                String optNum = "상품옵션 " + (i + 1);
                String pnsnRecpTrm = c.getString(TAG_PENSION_RECEPTION_TERM);
                String pnsnEntrAge = c.getString(TAG_PENSION_ENTRY_AGE);
                String monPaymAmt = c.getString(TAG_MONTHLY_PAYMENT_AMOUNT);
                String paymPrd = c.getString(TAG_PAYMENT_PERIOD);
                String pnsnStrtAge = c.getString(TAG_PENSION_START_AGE);
                String pnsnRecpAmt = toAmount(c.getString(TAG_PENSION_RECEPTION_AMOUNT));

                // annuity_saving_detail_list.xml을 inflate하여 새로운 뷰 생성
                View itemView = inflater.inflate(R.layout.annuity_saving_detail_list, null);

                TextView optNumTextView = itemView.findViewById(R.id.opt_num);
                TextView pnsnRecpTrmTextView = itemView.findViewById(R.id.pnsn_recp_trm);
                TextView pnsnEntrAgeTextView = itemView.findViewById(R.id.pnsn_entr_age);
                TextView monPaymAmtTextView = itemView.findViewById(R.id.mon_paym_atm);
                TextView paymPrdTextView = itemView.findViewById(R.id.paym_prd);
                TextView pnsnStrtAgeTextView = itemView.findViewById(R.id.pnsn_strt_age);
                TextView pnsnRecpAmtTextView = itemView.findViewById(R.id.pnsn_recp_amt);

                optNumTextView.setText(optNum);
                pnsnRecpTrmTextView.setText(pnsnRecpTrm);
                pnsnEntrAgeTextView.setText(pnsnEntrAge);
                monPaymAmtTextView.setText(monPaymAmt);
                paymPrdTextView.setText(paymPrd);
                pnsnStrtAgeTextView.setText(pnsnStrtAge);
                pnsnRecpAmtTextView.setText(pnsnRecpAmt);

                // 생성된 뷰를 ScrollView 안의 LinearLayout에 추가
                linearLayout.addView(itemView);
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

    public void getData(String url, String finPrdtId, boolean isOptList) {
            class GetDataJSON extends AsyncTask<String, Void, String> {
                // AsyncTask에서 execute() 메서드가 호출되면 내부적으로 doInBackground() 메서드가 호출
                @Override
                protected String doInBackground(String... params) {

                    String uri = params[0];
                    String finPrdtId = params[1]; // 추가된 파라미터 finPrdtId

                    try {
                        URL url = new URL(uri + "?finPrdtId=" + finPrdtId); // 파라미터를 URL에 추가
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
                        if (!isOptList) {
                            myJSON = result;
                            showWindow();
                        } else {
                            myJSON2 = result;
                            showList();
                        }
                    }
                }
            }

            GetDataJSON g = new GetDataJSON();
            g.execute(url, finPrdtId);      // 아래도 수정 필요!
    }

}