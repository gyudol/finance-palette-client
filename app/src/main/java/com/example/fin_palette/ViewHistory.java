package com.example.fin_palette;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

public class ViewHistory extends AppCompatActivity {
    Button depositButton;
    Button savingsButton;
    Button annuitySavingButton;
    Button rentHouseLoanButton;
    Button mortgageLoanButton;
    Button creditLoanButton;
    TextView titleTextView;

    private static final String TAG_RESULT = "result";
    private static final String TAG_FINANCIAL_COMPANY_NAME = "financial_company_name";
    private static final String TAG_FINANCIAL_PRODUCT_NAME = "financial_product_name";
    private static final String TAG_HIGHEST_INTEREST_RATE = "highest_interest_rate";
    private static final String TAG_FINANCIAL_PRODUCT_ID = "financial_product_id";
    private static final String TAG_INTEREST_RATE_TYPE = "interest_rate_type";
    private static final String TAG_ACCRUAL_TYPE = "accrual_type";            // savings_products
    private static final String TAG_AVERAGE_PROFIT_RATE = "average_profit_rate";    // annuity_saving_products
    private static final String TAG_PENSION_TYPE_NAME = "pension_type_name";      // annuity_saving_products

    ///////////////////////////////////////////////// 여기는 rent_house_loan ~ mortgage_loan
    private static final String TAG_MINIMUM_LOAN_RATE = "minimum_loan_rate";
    private static final String TAG_OPTION_ID = "option_id";
    private static final String TAG_LOAN_RATE_TYPE = "loan_rate_type";
    private static final String TAG_LOAN_REPAYMENT_TYPE = "loan_repayment_type";
    ///////////////////////////////////////////////
    private static final String TAG_CREDIT_PRODUCT_TYPE_NAME = "credit_product_type_name";
    private static final String TAG_LOWEST_LOAN_RATE = "lowest_loan_rate";  // credit_loan

    String myJSON;
    String prdtNum;

    JSONArray products = null;

    ArrayList<HashMap<String, String>> productList;

    ListView list;
    ApiServerManager asm = new ApiServerManager();
    String apiEndpoint = asm.getApiEndpoint();
    AaidManager am = new AaidManager();
    String aaid = am.aaid;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_history);

        list = (ListView) findViewById(R.id.listViewViewHistory);
        productList = new ArrayList<HashMap<String, String>>();

        // Intent에서 Extra로 전달된 데이터 받아오기
        Intent intentGet = getIntent();
        if (intentGet != null) {
            String receivedPrdtNum = intentGet.getStringExtra("prdtNum");

            // 받아온 값 확인
            if (receivedPrdtNum != null) {
                prdtNum = intentGet.getStringExtra("prdtNum");

                depositButton = findViewById(R.id.btn_deposit);
                savingsButton = findViewById(R.id.btn_savings);
                annuitySavingButton = findViewById(R.id.btn_annuity_saving);
                rentHouseLoanButton = findViewById(R.id.btn_rent_house_loan);
                mortgageLoanButton = findViewById(R.id.btn_mortgage_loan);
                creditLoanButton = findViewById(R.id.btn_credit_loan);
                titleTextView = findViewById(R.id.title);


                // onResume에서 실행됨
                // getData(apiEndpoint + "/view_history_list.php");
            }
        }


        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed(); // 뒤로 가기 버튼을 클릭하면 onBackPressed()가 호출됩니다.
            }
        });


        //////////////////////// 리스트 뷰 /////////////////////////////////
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // 사용자가 선택한 항목의 위치(인덱스)를 position 변수로 얻을 수 있음
                // position은 선택된 아이템의 인덱스를 나타냄

                try {
                    // 클릭된 위치(position)에 해당하는 JSON 객체 가져오기
                    JSONObject clickedItem = products.getJSONObject(position);
                    Intent intent = new Intent();

                    switch(prdtNum) {
                        case "1": {
                            // 클릭된 아이템의 'finPrdtId' 값 가져오기
                            String finPrdtId = clickedItem.getString(TAG_FINANCIAL_PRODUCT_ID);
                            String intrRateType = clickedItem.getString(TAG_INTEREST_RATE_TYPE);

                            // Intent를 사용하여 Deposit_detail_screen.java로 데이터를 전달하고 화면을 전환
                            intent = new Intent(ViewHistory.this, DepositDetailScreen.class);
                            intent.putExtra("finPrdtId", finPrdtId); // 클릭된 finPrdtId 값을 "finPrdtId"이란 이름의 Extra로 전달
                            intent.putExtra("intrRateType", intrRateType);
                        } break;
                        case "2": {
                            // 클릭된 아이템의 'finPrdtId' 값 가져오기
                            String finPrdtId = clickedItem.getString(TAG_FINANCIAL_PRODUCT_ID);
                            String intrRateType = clickedItem.getString(TAG_INTEREST_RATE_TYPE);
                            String accrualType = clickedItem.getString(TAG_ACCRUAL_TYPE);

                            // Intent를 사용하여 Savings_detail_screen.java로 데이터를 전달하고 화면을 전환
                            intent = new Intent(ViewHistory.this, SavingsDetailScreen.class);
                            intent.putExtra("finPrdtId", finPrdtId); // 클릭된 finPrdtId 값을 "finPrdtId"이란 이름의 Extra로 전달
                            intent.putExtra("intrRateType", intrRateType);
                            intent.putExtra("accrualType", accrualType);
                        } break;
                        case "3": {
                            // 클릭된 아이템의 'finPrdtId' 값 가져오기
                            String finPrdtId = clickedItem.getString(TAG_FINANCIAL_PRODUCT_ID);

                            // Intent를 사용하여 Annuity_Saving_detail_screen.java로 데이터를 전달하고 화면을 전환
                            intent = new Intent(ViewHistory.this, AnnuitySavingDetailScreen.class);
                            intent.putExtra("finPrdtId", finPrdtId); // 클릭된 finPrdtId 값을 "finPrdtId"이란 이름의 Extra로 전달
                        } break;
                        case "4": case "5": case "6": {
                            // 클릭된 아이템의 'optionId' 값 가져오기
                            String optionId = clickedItem.getString(TAG_OPTION_ID);

                            // Intent를 사용하여 데이터를 전달하고 화면을 전환
                            intent = new Intent(ViewHistory.this, (prdtNum.equals("4") ? RentHouseLoanDetailScreen.class : prdtNum.equals("5") ? MortgageLoanDetailScreen.class : CreditLoanDetailScreen.class));
                            intent.putExtra("optionId", optionId); // 클릭된 optionId 값을 "optionId"이란 이름의 Extra로 전달
                        } break;
                    }

                    startActivity(intent); //  화면 전환

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });


        /////////////////////// 버튼 클릭 시 해당 상품 조회 기록 화면으로 전환
        depositButton = findViewById(R.id.btn_deposit);
        savingsButton = findViewById(R.id.btn_savings);
        annuitySavingButton = findViewById(R.id.btn_annuity_saving);
        rentHouseLoanButton = findViewById(R.id.btn_rent_house_loan);
        mortgageLoanButton = findViewById(R.id.btn_mortgage_loan);
        creditLoanButton = findViewById(R.id.btn_credit_loan);

        depositButton.setOnClickListener(view -> {
            prdtNum = "1";
            getData(apiEndpoint + "/view_history_list.php");
        });
        savingsButton.setOnClickListener(view -> {
            prdtNum = "2";
            getData(apiEndpoint + "/view_history_list.php");
        });
        annuitySavingButton.setOnClickListener(view -> {
            prdtNum = "3";
            getData(apiEndpoint + "/view_history_list.php");
        });
        rentHouseLoanButton.setOnClickListener(view -> {
            prdtNum = "4";
            getData(apiEndpoint + "/view_history_list.php");
        });
        mortgageLoanButton.setOnClickListener(view -> {
            prdtNum = "5";
            getData(apiEndpoint + "/view_history_list.php");
        });
        creditLoanButton.setOnClickListener(view -> {
            prdtNum = "6";
            getData(apiEndpoint + "/view_history_list.php");
        });
    }


    @Override
    public void onResume() {    // 뒤로 가기로 Activity 되돌아왔을 때 listView Update
        super.onResume();

        // 저장해둘 필요가 없음 (최근 조회된 게 최상단으로 가므로)

        getData(apiEndpoint + "/view_history_list.php");
    }


    protected void showDpList() {   // deposit_products
        try {
            JSONObject jsonObj = new JSONObject(myJSON);
            products = jsonObj.getJSONArray(TAG_RESULT);

            productList.clear(); // 기존 데이터 클리어  => 클리어해야 제대로 갱신됨

            for (int i = 0; i < products.length(); i++) {
                JSONObject c = products.getJSONObject(i);
                String finCoName = c.getString(TAG_FINANCIAL_COMPANY_NAME);
                String finPrdtName = c.getString(TAG_FINANCIAL_PRODUCT_NAME);
                String highestIntrRate = "최고 " + c.getString(TAG_HIGHEST_INTEREST_RATE) + "%";
                String intrRateTypeName = (c.getString(TAG_INTEREST_RATE_TYPE)).equals("S")?"단리":"복리";

                HashMap<String, String> prdtBuff = new HashMap<String, String>();

                prdtBuff.put(TAG_FINANCIAL_COMPANY_NAME, finCoName);
                prdtBuff.put(TAG_FINANCIAL_PRODUCT_NAME, finPrdtName);
                prdtBuff.put(TAG_HIGHEST_INTEREST_RATE, highestIntrRate);
                prdtBuff.put(TAG_INTEREST_RATE_TYPE, intrRateTypeName);

                productList.add(prdtBuff);
            }

            // 데이터의 각 열(Column)에 맞게 TextView에 데이터를 바인딩하여 리스트뷰에 표시하는 역할
            ListAdapter adapter = new SimpleAdapter(
                    ViewHistory.this, productList, R.layout.deposit_list,
                    new String[]{TAG_FINANCIAL_COMPANY_NAME, TAG_FINANCIAL_PRODUCT_NAME, TAG_HIGHEST_INTEREST_RATE, TAG_INTEREST_RATE_TYPE},
                    new int[]{R.id.fin_co_nm, R.id.fin_prdt_nm, R.id.highest_intr_rate, R.id.intr_rate_type_nm}
            ) {
                @Override
                public View getView(int position, View convertView, ViewGroup parent) {
                    View view = super.getView(position, convertView, parent);
                    TextView intrRateTypeNameTextView = view.findViewById(R.id.intr_rate_type_nm);

                    String intrRateTypeName = productList.get(position).get(TAG_INTEREST_RATE_TYPE);

                    if (intrRateTypeName != null) {
                        if(intrRateTypeName.equals("복리")){
                            intrRateTypeNameTextView.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(ViewHistory.this, R.color.orange)));
                        }
                        else {
                            intrRateTypeNameTextView.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(ViewHistory.this, R.color.blue)));
                        }
                    }
                    return view;
                }
            };

            list.setAdapter(adapter);

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


    protected void showSpList() { // savings_products
        try {
            JSONObject jsonObj = new JSONObject(myJSON);
            products = jsonObj.getJSONArray(TAG_RESULT);

            productList.clear(); // 기존 데이터 클리어  => 클리어해야 제대로 갱신됨

            for (int i = 0; i < products.length(); i++) {
                JSONObject c = products.getJSONObject(i);
                String finCoName = c.getString(TAG_FINANCIAL_COMPANY_NAME);
                String finPrdtName = c.getString(TAG_FINANCIAL_PRODUCT_NAME);
                String highestIntrRate = "최고 " + c.getString(TAG_HIGHEST_INTEREST_RATE) + "%";
                String intrRateTypeName = c.getString(TAG_INTEREST_RATE_TYPE).equals("S")?"단리":"복리";
                String accrualTypeName = c.getString(TAG_ACCRUAL_TYPE).equals("S")?"정액적립식":"자유적립식";

                HashMap<String, String> prdtBuff = new HashMap<String, String>();

                prdtBuff.put(TAG_FINANCIAL_COMPANY_NAME, finCoName);
                prdtBuff.put(TAG_FINANCIAL_PRODUCT_NAME, finPrdtName);
                prdtBuff.put(TAG_HIGHEST_INTEREST_RATE, highestIntrRate);
                prdtBuff.put(TAG_INTEREST_RATE_TYPE, intrRateTypeName);
                prdtBuff.put(TAG_ACCRUAL_TYPE, accrualTypeName);

                productList.add(prdtBuff);
            }

            // 데이터의 각 열(Column)에 맞게 TextView에 데이터를 바인딩하여 리스트뷰에 표시하는 역할
            ListAdapter adapter = new SimpleAdapter(
                    ViewHistory.this, productList, R.layout.savings_list,
                    new String[]{TAG_FINANCIAL_COMPANY_NAME, TAG_FINANCIAL_PRODUCT_NAME, TAG_HIGHEST_INTEREST_RATE, TAG_INTEREST_RATE_TYPE, TAG_ACCRUAL_TYPE},
                    new int[]{R.id.fin_co_nm, R.id.fin_prdt_nm, R.id.highest_intr_rate, R.id.intr_rate_type_nm, R.id.accrual_type_nm}
            ){
                @Override
                public View getView(int position, View convertView, ViewGroup parent) {
                    View view = super.getView(position, convertView, parent);
                    TextView intrRateTypeNameTextView = view.findViewById(R.id.intr_rate_type_nm);
                    TextView accrualTypeNameTextView = view.findViewById(R.id.accrual_type_nm);

                    String intrRateTypeName = productList.get(position).get(TAG_INTEREST_RATE_TYPE);
                    String accrualTypeName = productList.get(position).get(TAG_ACCRUAL_TYPE);

                    if (intrRateTypeName != null) {
                        if(intrRateTypeName.equals("복리")){
                            intrRateTypeNameTextView.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(ViewHistory.this, R.color.orange)));
                        }
                        else {
                            intrRateTypeNameTextView.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(ViewHistory.this, R.color.blue)));
                        }
                    }

                    if (accrualTypeName != null) {
                        if(accrualTypeName.equals("자유적립식")){
                            accrualTypeNameTextView.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(ViewHistory.this, R.color.mid_blue)));
                        }
                        else {
                            accrualTypeNameTextView.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(ViewHistory.this, R.color.mid_green)));
                        }
                    }
                    return view;
                }
            };

            list.setAdapter(adapter);

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


    protected void showAspList() { // annuity_saving_products
        try {
            JSONObject jsonObj = new JSONObject(myJSON);
            products = jsonObj.getJSONArray(TAG_RESULT);

            productList.clear(); // 기존 데이터 클리어  => 클리어해야 제대로 갱신됨

            for (int i = 0; i < products.length(); i++) {
                JSONObject c = products.getJSONObject(i);
                String finCoName = c.getString(TAG_FINANCIAL_COMPANY_NAME);
                String finPrdtName = c.getString(TAG_FINANCIAL_PRODUCT_NAME);
                String avgPrftRate = "평균 " + c.getString(TAG_AVERAGE_PROFIT_RATE) + "%";
                String pnsnTypeName = c.getString(TAG_PENSION_TYPE_NAME);

                HashMap<String, String> prdtBuff = new HashMap<String, String>();

                prdtBuff.put(TAG_FINANCIAL_COMPANY_NAME, finCoName);
                prdtBuff.put(TAG_FINANCIAL_PRODUCT_NAME, finPrdtName);
                prdtBuff.put(TAG_AVERAGE_PROFIT_RATE, avgPrftRate);
                prdtBuff.put(TAG_PENSION_TYPE_NAME, pnsnTypeName);

                productList.add(prdtBuff);
            }

            // 데이터의 각 열(Column)에 맞게 TextView에 데이터를 바인딩하여 리스트뷰에 표시하는 역할
            ListAdapter adapter = new SimpleAdapter(
                    ViewHistory.this, productList, R.layout.annuity_saving_list,
                    new String[]{TAG_FINANCIAL_COMPANY_NAME, TAG_FINANCIAL_PRODUCT_NAME, TAG_AVERAGE_PROFIT_RATE, TAG_PENSION_TYPE_NAME},
                    new int[]{R.id.fin_co_nm, R.id.fin_prdt_nm, R.id.avg_prft_rate, R.id.pnsn_type_nm}
            ){
                @Override
                public View getView(int position, View convertView, ViewGroup parent) {
                    View view = super.getView(position, convertView, parent);
                    TextView pnsnTypeNameTextView = view.findViewById(R.id.pnsn_type_nm);

                    String pnsnTypeName = productList.get(position).get(TAG_PENSION_TYPE_NAME);

                    if (pnsnTypeName != null) {
                        if(pnsnTypeName.equals("연금저축보험(생명)")){
                            pnsnTypeNameTextView.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(ViewHistory.this, R.color.purple_500)));
                        }
                        else if(pnsnTypeName.equals("연금저축보험(손해)")) {
                            pnsnTypeNameTextView.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(ViewHistory.this, R.color.purple_700)));
                        }
                        else {
                            pnsnTypeNameTextView.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(ViewHistory.this, R.color.purple_200)));
                        }
                    }

                    return view;
                }
            };

            list.setAdapter(adapter);

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


    protected void showLp12List(int value) {    // value = 1이면 rent_house_loan_product_options, 아니면 mortgage_loan_product_options
        try {
            JSONObject jsonObj = new JSONObject(myJSON);
            products = jsonObj.getJSONArray(TAG_RESULT);

            productList.clear(); // 기존 데이터 클리어  => 클리어해야 제대로 갱신됨

            for (int i = 0; i < products.length(); i++) {
                JSONObject c = products.getJSONObject(i);
                String finCoName = c.getString(TAG_FINANCIAL_COMPANY_NAME);
                String finPrdtName = c.getString(TAG_FINANCIAL_PRODUCT_NAME);
                String minLoanRate = "최저 " + c.getString(TAG_MINIMUM_LOAN_RATE) + "%";
                String loanRateType = c.getString(TAG_LOAN_RATE_TYPE);
                String loanRpayType = c.getString(TAG_LOAN_REPAYMENT_TYPE).replace("방식", "");

                HashMap<String, String> prdtBuff = new HashMap<String, String>();

                prdtBuff.put(TAG_FINANCIAL_COMPANY_NAME, finCoName);
                prdtBuff.put(TAG_FINANCIAL_PRODUCT_NAME, finPrdtName);
                prdtBuff.put(TAG_MINIMUM_LOAN_RATE, minLoanRate);
                prdtBuff.put(TAG_LOAN_RATE_TYPE, loanRateType);
                prdtBuff.put(TAG_LOAN_REPAYMENT_TYPE, loanRpayType);

                productList.add(prdtBuff);
            }

            // 데이터의 각 열(Column)에 맞게 TextView에 데이터를 바인딩하여 리스트뷰에 표시하는 역할
            ListAdapter adapter = new SimpleAdapter(
                    ViewHistory.this, productList, (value == 1 ? R.layout.rent_house_loan_list : R.layout.mortgage_loan_list),
                    new String[]{TAG_FINANCIAL_COMPANY_NAME, TAG_FINANCIAL_PRODUCT_NAME, TAG_MINIMUM_LOAN_RATE, TAG_LOAN_RATE_TYPE, TAG_LOAN_REPAYMENT_TYPE},
                    new int[]{R.id.fin_co_nm, R.id.fin_prdt_nm, R.id.min_loan_rate, R.id.loan_rate_type, R.id.loan_rpay_type}
            ){
                @Override
                public View getView(int position, View convertView, ViewGroup parent) {
                    View view = super.getView(position, convertView, parent);
                    TextView loanRateTypeTextView = view.findViewById(R.id.loan_rate_type);
                    TextView loanRpayTypeTextView = view.findViewById(R.id.loan_rpay_type);

                    String loanRateType = productList.get(position).get(TAG_LOAN_RATE_TYPE);
                    String loanRpayType = productList.get(position).get(TAG_LOAN_REPAYMENT_TYPE);

                    if (loanRateType != null) {
                        if(loanRateType.equals("변동금리")){
                            loanRateTypeTextView.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(ViewHistory.this, R.color.teal_700)));
                        }
                        else {
                            loanRateTypeTextView.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(ViewHistory.this, R.color.brown_green)));
                        }
                    }

                    if (loanRpayType != null) {
                        if(loanRpayType.equals("분할상환")){
                            loanRpayTypeTextView.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(ViewHistory.this, R.color.magenta)));
                        }
                        else {
                            loanRpayTypeTextView.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(ViewHistory.this, R.color.purple_700)));
                        }
                    }
                    return view;
                }
            };

            list.setAdapter(adapter);

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


    protected void showLp3List() {
        try {
            JSONObject jsonObj = new JSONObject(myJSON);
            products = jsonObj.getJSONArray(TAG_RESULT);

            productList.clear(); // 기존 데이터 클리어  => 클리어해야 제대로 갱신됨

            for (int i = 0; i < products.length(); i++) {
                JSONObject c = products.getJSONObject(i);
                String finCoName = c.getString(TAG_FINANCIAL_COMPANY_NAME);
                String finPrdtName = c.getString(TAG_FINANCIAL_PRODUCT_NAME);
                String lowestLoanRate = "최저 " + c.getString(TAG_LOWEST_LOAN_RATE) + "%";
                String crdtPrdtTypeName = c.getString(TAG_CREDIT_PRODUCT_TYPE_NAME);

                HashMap<String, String> prdtBuff = new HashMap<String, String>();

                prdtBuff.put(TAG_FINANCIAL_COMPANY_NAME, finCoName);
                prdtBuff.put(TAG_FINANCIAL_PRODUCT_NAME, finPrdtName);
                prdtBuff.put(TAG_LOWEST_LOAN_RATE, lowestLoanRate);
                prdtBuff.put(TAG_CREDIT_PRODUCT_TYPE_NAME, crdtPrdtTypeName);

                productList.add(prdtBuff);
            }

            // 데이터의 각 열(Column)에 맞게 TextView에 데이터를 바인딩하여 리스트뷰에 표시하는 역할
            ListAdapter adapter = new SimpleAdapter(
                    ViewHistory.this, productList, R.layout.credit_loan_list,
                    new String[]{TAG_FINANCIAL_COMPANY_NAME, TAG_FINANCIAL_PRODUCT_NAME, TAG_LOWEST_LOAN_RATE, TAG_CREDIT_PRODUCT_TYPE_NAME},
                    new int[]{R.id.fin_co_nm, R.id.fin_prdt_nm, R.id.lowest_loan_rate, R.id.crdt_prdt_type_nm}
            ){
                @Override
                public View getView(int position, View convertView, ViewGroup parent) {
                    View view = super.getView(position, convertView, parent);
                    TextView crdtPrdtTypeNameTextView = view.findViewById(R.id.crdt_prdt_type_nm);

                    String crdtPrdtTypeName = productList.get(position).get(TAG_CREDIT_PRODUCT_TYPE_NAME);

                    if (crdtPrdtTypeName != null) {
                        if(crdtPrdtTypeName.contains("일반신용대출")){
                            crdtPrdtTypeNameTextView.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(ViewHistory.this, R.color.magenta)));
                        }
                        else if(crdtPrdtTypeName.equals("마이너스한도대출")){
                            crdtPrdtTypeNameTextView.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(ViewHistory.this, R.color.crown_flo)));
                        }
                        else {
                            crdtPrdtTypeNameTextView.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(ViewHistory.this, R.color.mid_green)));
                        }
                    }
                    return view;
                }
            };

            list.setAdapter(adapter);

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }



    //////////////////////////////////////////////////// listView Update
    public void getData(String url) {
        class GetDataJSON extends AsyncTask<String, Void, String> {
            // AsyncTask에서 execute() 메서드가 호출되면 내부적으로 doInBackground() 메서드가 호출
            @Override
            protected String doInBackground(String... params) {

                String uri = params[0];
                String prdtNum = params[1];
                String aaid = params[2];

                try {
                    URL url = new URL(uri+ "?prdtNum=" + prdtNum + "&aaid=" + aaid);
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
                    myJSON = result;

                    // Toast.makeText(getApplicationContext(), result, Toast.LENGTH_SHORT).show();

                    // 버튼 모두 활성화
                    depositButton.setVisibility(depositButton.VISIBLE);
                    savingsButton.setVisibility(savingsButton.VISIBLE);
                    annuitySavingButton.setVisibility(annuitySavingButton.VISIBLE);
                    rentHouseLoanButton.setVisibility(rentHouseLoanButton.VISIBLE);
                    mortgageLoanButton.setVisibility(mortgageLoanButton.VISIBLE);
                    creditLoanButton.setVisibility(creditLoanButton.VISIBLE);


                    // 적용된 버튼만 안 보이게 & showList
                    switch(prdtNum) {
                        case "1": depositButton.setVisibility(depositButton.INVISIBLE); titleTextView.setText("정기 예금 최근 조회 기록"); showDpList(); break;
                        case "2": savingsButton.setVisibility(savingsButton.INVISIBLE); titleTextView.setText("적금 최근 조회 기록"); showSpList(); break;
                        case "3": annuitySavingButton.setVisibility(annuitySavingButton.INVISIBLE); titleTextView.setText("연금 저축 최근 조회 기록"); showAspList(); break;
                        case "4": rentHouseLoanButton.setVisibility(rentHouseLoanButton.INVISIBLE); titleTextView.setText("전세 자금 대출 최근 조회 기록"); showLp12List(1); break;
                        case "5": mortgageLoanButton.setVisibility(mortgageLoanButton.INVISIBLE); titleTextView.setText("주택 담보 대출 최근 조회 기록"); showLp12List(2); break;
                        case "6": creditLoanButton.setVisibility(creditLoanButton.INVISIBLE); titleTextView.setText("개인 신용 대출 최근 조회 기록"); showLp3List(); break;
                    }
                }
            }
        }

        GetDataJSON g = new GetDataJSON();
        g.execute(url, prdtNum, aaid);
    }


}
