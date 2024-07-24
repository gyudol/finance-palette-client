package com.example.fin_palette;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

public class CreditLoan extends AppCompatActivity {
    Button rentHouseLoanButton;
    Button mortgageLoanButton;
    Spinner spinner1;
    Spinner spinner2;
    String myJSON;

    private static final String TAG_RESULT = "result";
    private static final String TAG_FINANCIAL_COMPANY_NAME = "financial_company_name";
    private static final String TAG_FINANCIAL_PRODUCT_NAME = "financial_product_name";
    private static final String TAG_LOWEST_LOAN_RATE = "lowest_loan_rate";
    private static final String TAG_OPTION_ID = "option_id";
    private static final String TAG_CREDIT_PRODUCT_TYPE_NAME = "credit_product_type_name";
    JSONArray products = null;

    ArrayList<HashMap<String, String>> productList;

    ListView list;
    int itemNo = 0;
    int [] selected = {0, 0};   // 아무것도 선택 안 했으면 0, 첫번째 것이 선택되면 1, 두번째 것이 선택되면 2
    String [] items1 = {"금융기관 전체", "1금융", "2금융"};
    String [] items2 = {"대출 종류 전체", "일반신용대출", "마이너스한도대출", "장기카드대출"};
    ApiServerManager asm = new ApiServerManager();
    String apiEndpoint = asm.getApiEndpoint();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.credit_loan);
        list = (ListView) findViewById(R.id.listViewLp3);
        productList = new ArrayList<HashMap<String, String>>();


        // SharedPreferences를 사용하여 데이터 초기화
        SharedPreferences preferences = getSharedPreferences("credit_loan_preferences", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();

        // 스피너 상태 저장
        editor.putInt("spinner1_position", 0);
        editor.putInt("spinner2_position", 0);
        editor.putInt("spinner3_position", 0);

        // 리스트 뷰 상태 저장
        editor.putInt("list_position", 0);
        editor.apply();

        // onResume에서 실행됨
        // getData(apiEndpoint + "/credit_loan_ext.php", "0", "0"); // API Endpoint에 맞게 수정 필요 (Default: 0, 0)

        rentHouseLoanButton = findViewById(R.id.btn_rent_house_loan);
        rentHouseLoanButton.setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(), RentHouseLoan.class);
            startActivity(intent);
            finish();
        });

        mortgageLoanButton = findViewById(R.id.btn_mortgage_loan);
        mortgageLoanButton.setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(), MortgageLoan.class);
            startActivity(intent);
            finish();
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


        ///////////////////////// 스피너 //////////////////////////////////
        spinner1 = findViewById(R.id.spinner1);
        spinner2 = findViewById(R.id.spinner2);

        ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_item, items1);
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner1.setAdapter(adapter1);
        ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_item, items2);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner2.setAdapter(adapter2);

        spinner1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selected[0] = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selected[0] = 0;
            }
        });
        spinner2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selected[1] = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selected[1] = 0;
            }
        });


        //////////////////////// 이미지 뷰 /////////////////////////////////
        ImageView imageView = findViewById(R.id.img_view_find);

        imageView.setOnClickListener(new View.OnClickListener() {   // 돋보기 이미지 뷰 클릭하면 조건에 맞게 필터링
            @Override
            public void onClick(View v) {
                itemNo = 0;
                getData(apiEndpoint + "/credit_loan_ext.php", selected[0] + "", selected[1] + "");
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
                    JSONObject clickedItem = products.getJSONObject(itemNo = position);

                    // 클릭된 아이템의 'optionId' 값 가져오기
                    String optionId = clickedItem.getString(TAG_OPTION_ID);

                    // Toast.makeText(getApplicationContext(), "Clicked item's option_id: " + optionId, Toast.LENGTH_SHORT).show();

                    // Intent를 사용하여 CreditLoanDetailScreen.java로 데이터를 전달하고 화면을 전환
                    Intent intent = new Intent(CreditLoan.this, CreditLoanDetailScreen.class);
                    intent.putExtra("optionId", optionId); // 클릭된 optionId 값을 "optionId"이란 이름의 Extra로 전달
                    startActivity(intent); // CreditLoanDetailScreen.java로 화면 전환

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }


    @Override
    protected void onPause() {      // Activity가 뒤로 돌아갈 때 데이터 저장해둠
        super.onPause();

        // SharedPreferences를 사용하여 데이터 저장
        SharedPreferences preferences = getSharedPreferences("credit_loan_preferences", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();

        // 스피너 상태 저장
        editor.putInt("spinner1_position", selected[0]);
        editor.putInt("spinner2_position", selected[1]);

        // 리스트 뷰 상태 저장
        editor.putInt("list_position", itemNo);

        // Toast.makeText(getApplicationContext(), "itemNo: " + itemNo + "\n" + Arrays.toString(selected), Toast.LENGTH_SHORT).show();

        // 변경사항을 커밋하여 저장
        editor.apply();
    }


    @Override
    public void onResume() {    // 뒤로 가기로 Activity 되돌아왔을 때 리스트 뷰, 스피너 상태 그대로
        super.onResume();

        SharedPreferences preferences = getSharedPreferences("credit_loan_preferences", MODE_PRIVATE);

        spinner1.setSelection(selected[0] = preferences.getInt("spinner1_position", 0));
        spinner2.setSelection(selected[1] = preferences.getInt("spinner2_position", 0));

        itemNo = preferences.getInt("list_position", 0);

        getData(apiEndpoint + "/credit_loan_ext.php", selected[0] + "", selected[1] + "");

        // Toast.makeText(getApplicationContext(), "itemNo: " + itemNo + "\n" + Arrays.toString(selected), Toast.LENGTH_SHORT).show();
    }


    protected void showList() {
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
                    CreditLoan.this, productList, R.layout.credit_loan_list,
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
                            crdtPrdtTypeNameTextView.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(CreditLoan.this, R.color.magenta)));
                        }
                        else if(crdtPrdtTypeName.equals("마이너스한도대출")){
                            crdtPrdtTypeNameTextView.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(CreditLoan.this, R.color.crown_flo)));
                        }
                        else {
                            crdtPrdtTypeNameTextView.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(CreditLoan.this, R.color.mid_green)));
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


    public void getData(String url, String sp1, String sp2) {
        class GetDataJSON extends AsyncTask<String, Void, String> {
            // AsyncTask에서 execute() 메서드가 호출되면 내부적으로 doInBackground() 메서드가 호출
            @Override
            protected String doInBackground(String... params) {

                String uri = params[0];
                String sp1 = params[1];       // 추가된 파라미터 a, b, c
                String sp2 = params[2];

                try {
                    URL url = new URL(uri+ "?sp1=" + sp1 + "&sp2=" + sp2);
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
                    showList();
                    if(itemNo != 0) list.setSelectionFromTop(itemNo, 0);    // onResume 했을 때 최근 조회 item을 최상단으로
                }
            }
        }

        GetDataJSON g = new GetDataJSON();
        g.execute(url, sp1, sp2);
    }
}