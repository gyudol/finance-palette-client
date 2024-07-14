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
import android.widget.Toast;

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

public class View_History extends AppCompatActivity {
    Button btn_deposit;
    Button btn_savings;
    Button btn_annuity_saving;
    Button btn_loan1;
    Button btn_loan2;
    Button btn_loan3;
    TextView textView14;

    private static final String TAG_RESULTS = "result";
    private static final String TAG_KOR_CO_NM = "kor_co_nm";
    private static final String TAG_FIN_PRDT_NM = "fin_prdt_nm";
    private static final String TAG_HIGHEST_INTR_RATE = "highest_intr_rate";
    private static final String TAG_FIN_PRDT_CD = "fin_prdt_cd";
    private static final String TAG_INTR_RATE_TYPE = "intr_rate_type";
    private static final String TAG_RSRV_TYPE = "rsrv_type";            // savingProducts
    private static final String TAG_AVG_PRFT_RATE = "avg_prft_rate";    // annuitySavingProducts
    private static final String TAG_PNSN_KIND_NM = "pnsn_kind_nm";      // annuitySavingProducts

    ///////////////////////////////////////////////// 여기는 loan1 ~ loan2
    private static final String TAG_LEND_RATE_MIN = "lend_rate_min";
    private static final String TAG_OPT_NUM = "opt_num";
    private static final String TAG_LEND_RATE_TYPE_NM = "lend_rate_type_nm";
    private static final String TAG_RPAY_TYPE_NM = "rpay_type_nm";
    ///////////////////////////////////////////////
    private static final String TAG_CRDT_PRDT_TYPE_NM = "crdt_prdt_type_nm";    // loan3

    String myJSON;
    String prdtNum;

    JSONArray products = null;

    ArrayList<HashMap<String, String>> productList;

    ListView list;
    getIPAddress ipAddress = new getIPAddress();
    String ipv4Address = ipAddress.getIPv4();
    AAID_State as = new AAID_State();
    String aaid = as.aaid;

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

                btn_deposit = findViewById(R.id.btn_deposit);
                btn_savings = findViewById(R.id.btn_savings);
                btn_annuity_saving = findViewById(R.id.btn_annuity_saving);
                btn_loan1 = findViewById(R.id.btn_loan1);
                btn_loan2 = findViewById(R.id.btn_loan2);
                btn_loan3 = findViewById(R.id.btn_loan3);
                textView14 = findViewById(R.id.textView14);


                // onResume에서 실행됨
                // getData("http://" + ipv4Address + "/PHP_view_history_list.php");
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
                            // 클릭된 아이템의 'fin_prdt_nm' 값 가져오기
                            String finPrdtCd = clickedItem.getString(TAG_FIN_PRDT_CD);
                            String intrRateType = clickedItem.getString(TAG_INTR_RATE_TYPE);

                            // Intent를 사용하여 Deposit_detail_screen.java로 데이터를 전달하고 화면을 전환
                            intent = new Intent(View_History.this, Deposit_detail_screen.class);
                            intent.putExtra("finPrdtCd", finPrdtCd); // 클릭된 finPrdtCd 값을 "finPrdtCd"이란 이름의 Extra로 전달
                            intent.putExtra("intrRateType", intrRateType);
                        } break;
                        case "2": {
                            // 클릭된 아이템의 'fin_prdt_nm' 값 가져오기
                            String finPrdtCd = clickedItem.getString(TAG_FIN_PRDT_CD);
                            String intrRateType = clickedItem.getString(TAG_INTR_RATE_TYPE);
                            String rsrvType = clickedItem.getString(TAG_RSRV_TYPE);

                            // Intent를 사용하여 Savings_detail_screen.java로 데이터를 전달하고 화면을 전환
                            intent = new Intent(View_History.this, Savings_detail_screen.class);
                            intent.putExtra("finPrdtCd", finPrdtCd); // 클릭된 finPrdtCd 값을 "finPrdtCd"이란 이름의 Extra로 전달
                            intent.putExtra("intrRateType", intrRateType);
                            intent.putExtra("rsrvType", rsrvType);
                        } break;
                        case "3": {
                            // 클릭된 아이템의 'fin_prdt_nm' 값 가져오기
                            String finPrdtCd = clickedItem.getString(TAG_FIN_PRDT_CD);

                            // Intent를 사용하여 Annuity_Saving_detail_screen.java로 데이터를 전달하고 화면을 전환
                            intent = new Intent(View_History.this, Annuity_Saving_detail_screen.class);
                            intent.putExtra("finPrdtCd", finPrdtCd); // 클릭된 finPrdtCd 값을 "finPrdtCd"이란 이름의 Extra로 전달
                        } break;
                        case "4": case "5": case "6": {
                            // 클릭된 아이템의 'fin_prdt_nm' 값 가져오기
                            String optNum = clickedItem.getString(TAG_OPT_NUM);

                            // Intent를 사용하여 Loan1_detail_screen.java로 데이터를 전달하고 화면을 전환
                            intent = new Intent(View_History.this, (prdtNum.equals("4") ? Loan1_detail_screen.class : prdtNum.equals("5") ? Loan2_detail_screen.class : Loan3_detail_screen.class));
                            intent.putExtra("optNum", optNum); // 클릭된 optNum 값을 "optNum"이란 이름의 Extra로 전달
                        } break;
                    }

                    startActivity(intent); //  화면 전환

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });


        /////////////////////// 버튼 클릭 시 해당 상품 조회 기록 화면으로 전환
        btn_deposit = findViewById(R.id.btn_deposit);
        btn_savings = findViewById(R.id.btn_savings);
        btn_annuity_saving = findViewById(R.id.btn_annuity_saving);
        btn_loan1 = findViewById(R.id.btn_loan1);
        btn_loan2 = findViewById(R.id.btn_loan2);
        btn_loan3 = findViewById(R.id.btn_loan3);

        btn_deposit.setOnClickListener(view -> {
            prdtNum = "1";
            getData("http://" + ipv4Address + "/PHP_view_history_list.php");
        });
        btn_savings.setOnClickListener(view -> {
            prdtNum = "2";
            getData("http://" + ipv4Address + "/PHP_view_history_list.php");
        });
        btn_annuity_saving.setOnClickListener(view -> {
            prdtNum = "3";
            getData("http://" + ipv4Address + "/PHP_view_history_list.php");
        });
        btn_loan1.setOnClickListener(view -> {
            prdtNum = "4";
            getData("http://" + ipv4Address + "/PHP_view_history_list.php");
        });
        btn_loan2.setOnClickListener(view -> {
            prdtNum = "5";
            getData("http://" + ipv4Address + "/PHP_view_history_list.php");
        });
        btn_loan3.setOnClickListener(view -> {
            prdtNum = "6";
            getData("http://" + ipv4Address + "/PHP_view_history_list.php");
        });
    }


    @Override
    public void onResume() {    // 뒤로 가기로 Activity 되돌아왔을 때 listView Update
        super.onResume();

        // 저장해둘 필요가 없음 (최근 조회된 게 최상단으로 가므로)

        getData("http://" + ipv4Address + "/PHP_view_history_list.php");
    }


    protected void showDpList() {   // depositProducts
        try {
            JSONObject jsonObj = new JSONObject(myJSON);
            products = jsonObj.getJSONArray(TAG_RESULTS);

            productList.clear(); // 기존 데이터 클리어  => 클리어해야 제대로 갱신됨

            for (int i = 0; i < products.length(); i++) {
                JSONObject c = products.getJSONObject(i);
                String kor_co_nm = c.getString(TAG_KOR_CO_NM);
                String fin_prdt_nm = c.getString(TAG_FIN_PRDT_NM);
                String highest_intr_rate = "최고 " + c.getString(TAG_HIGHEST_INTR_RATE) + "%";
                String intr_rate_type = (c.getString(TAG_INTR_RATE_TYPE)).equals("S")?"단리":"복리";

                HashMap<String, String> prdt_buff = new HashMap<String, String>();

                prdt_buff.put(TAG_KOR_CO_NM, kor_co_nm);
                prdt_buff.put(TAG_FIN_PRDT_NM, fin_prdt_nm);
                prdt_buff.put(TAG_HIGHEST_INTR_RATE, highest_intr_rate);
                prdt_buff.put(TAG_INTR_RATE_TYPE, intr_rate_type);

                productList.add(prdt_buff);
            }

            // 데이터의 각 열(Column)에 맞게 TextView에 데이터를 바인딩하여 리스트뷰에 표시하는 역할
            ListAdapter adapter = new SimpleAdapter(
                    View_History.this, productList, R.layout.deposit_list,
                    new String[]{TAG_KOR_CO_NM, TAG_FIN_PRDT_NM, TAG_HIGHEST_INTR_RATE, TAG_INTR_RATE_TYPE},
                    new int[]{R.id.kor_co_nm, R.id.fin_prdt_nm, R.id.highest_intr_rate, R.id.intr_rate_type_nm}
            ) {
                @Override
                public View getView(int position, View convertView, ViewGroup parent) {
                    View view = super.getView(position, convertView, parent);
                    TextView intrRateTypeTextView = view.findViewById(R.id.intr_rate_type_nm);

                    String intrRateType = productList.get(position).get(TAG_INTR_RATE_TYPE);

                    if (intrRateType != null) {
                        if(intrRateType.equals("복리")){
                            intrRateTypeTextView.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(View_History.this, R.color.orange)));
                        }
                        else {
                            intrRateTypeTextView.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(View_History.this, R.color.blue)));
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


    protected void showSpList() { // savingProducts
        try {
            JSONObject jsonObj = new JSONObject(myJSON);
            products = jsonObj.getJSONArray(TAG_RESULTS);

            productList.clear(); // 기존 데이터 클리어  => 클리어해야 제대로 갱신됨

            for (int i = 0; i < products.length(); i++) {
                JSONObject c = products.getJSONObject(i);
                String kor_co_nm = c.getString(TAG_KOR_CO_NM);
                String fin_prdt_nm = c.getString(TAG_FIN_PRDT_NM);
                String highest_intr_rate = "최고 " + c.getString(TAG_HIGHEST_INTR_RATE) + "%";
                String intr_rate_type = c.getString(TAG_INTR_RATE_TYPE).equals("S")?"단리":"복리";
                String rsrv_type = c.getString(TAG_RSRV_TYPE).equals("S")?"정액적립식":"자유적립식";

                HashMap<String, String> prdt_buff = new HashMap<String, String>();

                prdt_buff.put(TAG_KOR_CO_NM, kor_co_nm);
                prdt_buff.put(TAG_FIN_PRDT_NM, fin_prdt_nm);
                prdt_buff.put(TAG_HIGHEST_INTR_RATE, highest_intr_rate);
                prdt_buff.put(TAG_INTR_RATE_TYPE, intr_rate_type);
                prdt_buff.put(TAG_RSRV_TYPE, rsrv_type);

                productList.add(prdt_buff);
            }

            // 데이터의 각 열(Column)에 맞게 TextView에 데이터를 바인딩하여 리스트뷰에 표시하는 역할
            ListAdapter adapter = new SimpleAdapter(
                    View_History.this, productList, R.layout.savings_list,
                    new String[]{TAG_KOR_CO_NM, TAG_FIN_PRDT_NM, TAG_HIGHEST_INTR_RATE, TAG_INTR_RATE_TYPE, TAG_RSRV_TYPE},
                    new int[]{R.id.kor_co_nm, R.id.fin_prdt_nm, R.id.highest_intr_rate, R.id.intr_rate_type_nm, R.id.rsrv_type_nm}
            ){
                @Override
                public View getView(int position, View convertView, ViewGroup parent) {
                    View view = super.getView(position, convertView, parent);
                    TextView intrRateTypeTextView = view.findViewById(R.id.intr_rate_type_nm);
                    TextView rsrvTypeTextView = view.findViewById(R.id.rsrv_type_nm);

                    String intrRateType = productList.get(position).get(TAG_INTR_RATE_TYPE);
                    String rsrvType = productList.get(position).get(TAG_RSRV_TYPE);

                    if (intrRateType != null) {
                        if(intrRateType.equals("복리")){
                            intrRateTypeTextView.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(View_History.this, R.color.orange)));
                        }
                        else {
                            intrRateTypeTextView.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(View_History.this, R.color.blue)));
                        }
                    }

                    if (rsrvType != null) {
                        if(rsrvType.equals("자유적립식")){
                            rsrvTypeTextView.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(View_History.this, R.color.mid_blue)));
                        }
                        else {
                            rsrvTypeTextView.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(View_History.this, R.color.mid_green)));
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


    protected void showAspList() { // annuitySavingProducts
        try {
            JSONObject jsonObj = new JSONObject(myJSON);
            products = jsonObj.getJSONArray(TAG_RESULTS);

            productList.clear(); // 기존 데이터 클리어  => 클리어해야 제대로 갱신됨

            for (int i = 0; i < products.length(); i++) {
                JSONObject c = products.getJSONObject(i);
                String kor_co_nm = c.getString(TAG_KOR_CO_NM);
                String fin_prdt_nm = c.getString(TAG_FIN_PRDT_NM);
                String avg_prft_rate = "평균 " + c.getString(TAG_AVG_PRFT_RATE) + "%";
                String pnsn_kind = c.getString(TAG_PNSN_KIND_NM);

                HashMap<String, String> prdt_buff = new HashMap<String, String>();

                prdt_buff.put(TAG_KOR_CO_NM, kor_co_nm);
                prdt_buff.put(TAG_FIN_PRDT_NM, fin_prdt_nm);
                prdt_buff.put(TAG_AVG_PRFT_RATE, avg_prft_rate);
                prdt_buff.put(TAG_PNSN_KIND_NM, pnsn_kind);

                productList.add(prdt_buff);
            }

            // 데이터의 각 열(Column)에 맞게 TextView에 데이터를 바인딩하여 리스트뷰에 표시하는 역할
            ListAdapter adapter = new SimpleAdapter(
                    View_History.this, productList, R.layout.annuity_saving_list,
                    new String[]{TAG_KOR_CO_NM, TAG_FIN_PRDT_NM, TAG_AVG_PRFT_RATE, TAG_PNSN_KIND_NM},
                    new int[]{R.id.kor_co_nm, R.id.fin_prdt_nm, R.id.avg_prft_rate, R.id.pnsn_kind_nm}
            ){
                @Override
                public View getView(int position, View convertView, ViewGroup parent) {
                    View view = super.getView(position, convertView, parent);
                    TextView pnsnKindTextView = view.findViewById(R.id.pnsn_kind_nm);

                    String pnsnKind = productList.get(position).get(TAG_PNSN_KIND_NM);

                    if (pnsnKind != null) {
                        if(pnsnKind.equals("연금저축보험(생명)")){
                            pnsnKindTextView.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(View_History.this, R.color.purple_500)));
                        }
                        else if(pnsnKind.equals("연금저축보험(손해)")) {
                            pnsnKindTextView.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(View_History.this, R.color.purple_700)));
                        }
                        else {
                            pnsnKindTextView.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(View_History.this, R.color.purple_200)));
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


    protected void showLp12List(int value) {    // value = 1이면 rentHouseLoanProductsOptions(loan1), 아니면 mortgageLoanProductsOptions(loan2)
        try {
            JSONObject jsonObj = new JSONObject(myJSON);
            products = jsonObj.getJSONArray(TAG_RESULTS);

            productList.clear(); // 기존 데이터 클리어  => 클리어해야 제대로 갱신됨

            for (int i = 0; i < products.length(); i++) {
                JSONObject c = products.getJSONObject(i);
                String kor_co_nm = c.getString(TAG_KOR_CO_NM);
                String fin_prdt_nm = c.getString(TAG_FIN_PRDT_NM);
                String lend_rate_min = "최저 " + c.getString(TAG_LEND_RATE_MIN) + "%";
                String lend_rate_type_nm = c.getString(TAG_LEND_RATE_TYPE_NM);
                String rpay_type_nm = c.getString(TAG_RPAY_TYPE_NM).replace("방식", "");

                HashMap<String, String> prdt_buff = new HashMap<String, String>();

                prdt_buff.put(TAG_KOR_CO_NM, kor_co_nm);
                prdt_buff.put(TAG_FIN_PRDT_NM, fin_prdt_nm);
                prdt_buff.put(TAG_LEND_RATE_MIN, lend_rate_min);
                prdt_buff.put(TAG_LEND_RATE_TYPE_NM, lend_rate_type_nm);
                prdt_buff.put(TAG_RPAY_TYPE_NM, rpay_type_nm);

                productList.add(prdt_buff);
            }

            // 데이터의 각 열(Column)에 맞게 TextView에 데이터를 바인딩하여 리스트뷰에 표시하는 역할
            ListAdapter adapter = new SimpleAdapter(
                    View_History.this, productList, (value == 1 ? R.layout.loan1_list : R.layout.loan2_list),
                    new String[]{TAG_KOR_CO_NM, TAG_FIN_PRDT_NM, TAG_LEND_RATE_MIN, TAG_LEND_RATE_TYPE_NM, TAG_RPAY_TYPE_NM},
                    new int[]{R.id.kor_co_nm, R.id.fin_prdt_nm, R.id.lend_rate_min, R.id.lend_rate_type_nm, R.id.rpay_type_nm}
            ){
                @Override
                public View getView(int position, View convertView, ViewGroup parent) {
                    View view = super.getView(position, convertView, parent);
                    TextView lendRateTypeTextView = view.findViewById(R.id.lend_rate_type_nm);
                    TextView rpayTypeTextView = view.findViewById(R.id.rpay_type_nm);

                    String lendRateType = productList.get(position).get(TAG_LEND_RATE_TYPE_NM);
                    String rpayType = productList.get(position).get(TAG_RPAY_TYPE_NM);

                    if (lendRateType != null) {
                        if(lendRateType.equals("변동금리")){
                            lendRateTypeTextView.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(View_History.this, R.color.teal_700)));
                        }
                        else {
                            lendRateTypeTextView.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(View_History.this, R.color.brown_green)));
                        }
                    }

                    if (rpayType != null) {
                        if(rpayType.equals("분할상환")){
                            rpayTypeTextView.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(View_History.this, R.color.magenta)));
                        }
                        else {
                            rpayTypeTextView.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(View_History.this, R.color.purple_700)));
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
            products = jsonObj.getJSONArray(TAG_RESULTS);

            productList.clear(); // 기존 데이터 클리어  => 클리어해야 제대로 갱신됨

            for (int i = 0; i < products.length(); i++) {
                JSONObject c = products.getJSONObject(i);
                String kor_co_nm = c.getString(TAG_KOR_CO_NM);
                String fin_prdt_nm = c.getString(TAG_FIN_PRDT_NM);
                String lend_rate_min = "최저 " + c.getString(TAG_LEND_RATE_MIN) + "%";
                String crdt_prdt_type_nm = c.getString(TAG_CRDT_PRDT_TYPE_NM);

                HashMap<String, String> prdt_buff = new HashMap<String, String>();

                prdt_buff.put(TAG_KOR_CO_NM, kor_co_nm);
                prdt_buff.put(TAG_FIN_PRDT_NM, fin_prdt_nm);
                prdt_buff.put(TAG_LEND_RATE_MIN, lend_rate_min);
                prdt_buff.put(TAG_CRDT_PRDT_TYPE_NM, crdt_prdt_type_nm);

                productList.add(prdt_buff);
            }

            // 데이터의 각 열(Column)에 맞게 TextView에 데이터를 바인딩하여 리스트뷰에 표시하는 역할
            ListAdapter adapter = new SimpleAdapter(
                    View_History.this, productList, R.layout.loan3_list,
                    new String[]{TAG_KOR_CO_NM, TAG_FIN_PRDT_NM, TAG_LEND_RATE_MIN, TAG_CRDT_PRDT_TYPE_NM},
                    new int[]{R.id.kor_co_nm, R.id.fin_prdt_nm, R.id.lend_rate_min, R.id.crdt_prdt_type_nm}
            ){
                @Override
                public View getView(int position, View convertView, ViewGroup parent) {
                    View view = super.getView(position, convertView, parent);
                    TextView crdtPrdtTypeTextView = view.findViewById(R.id.crdt_prdt_type_nm);

                    String crdtPrdtType = productList.get(position).get(TAG_CRDT_PRDT_TYPE_NM);

                    if (crdtPrdtType != null) {
                        if(crdtPrdtType.contains("일반신용대출")){
                            crdtPrdtTypeTextView.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(View_History.this, R.color.magenta)));
                        }
                        else if(crdtPrdtType.equals("마이너스한도대출")){
                            crdtPrdtTypeTextView.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(View_History.this, R.color.crown_flo)));
                        }
                        else {
                            crdtPrdtTypeTextView.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(View_History.this, R.color.mid_green)));
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
                    btn_deposit.setVisibility(btn_deposit.VISIBLE);
                    btn_savings.setVisibility(btn_savings.VISIBLE);
                    btn_annuity_saving.setVisibility(btn_annuity_saving.VISIBLE);
                    btn_loan1.setVisibility(btn_loan1.VISIBLE);
                    btn_loan2.setVisibility(btn_loan2.VISIBLE);
                    btn_loan3.setVisibility(btn_loan3.VISIBLE);


                    // 적용된 버튼만 안 보이게 & showList
                    switch(prdtNum) {
                        case "1": btn_deposit.setVisibility(btn_deposit.INVISIBLE); textView14.setText("정기 예금 최근 조회 기록"); showDpList(); break;
                        case "2": btn_savings.setVisibility(btn_savings.INVISIBLE); textView14.setText("적금 최근 조회 기록"); showSpList(); break;
                        case "3": btn_annuity_saving.setVisibility(btn_annuity_saving.INVISIBLE); textView14.setText("연금 저축 최근 조회 기록"); showAspList(); break;
                        case "4": btn_loan1.setVisibility(btn_loan1.INVISIBLE); showLp12List(1); textView14.setText("전세 자금 대출 최근 조회 기록"); break;
                        case "5": btn_loan2.setVisibility(btn_loan2.INVISIBLE); showLp12List(2); textView14.setText("주택 담보 대출 최근 조회 기록"); break;
                        case "6": btn_loan3.setVisibility(btn_loan3.INVISIBLE); showLp3List(); textView14.setText("개인 신용 대출 최근 조회 기록"); break;
                    }
                }
            }
        }

        GetDataJSON g = new GetDataJSON();
        g.execute(url, prdtNum, aaid);
    }


}
