package com.example.fin_palette;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import android.content.res.ColorStateList;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import android.content.Intent;
import android.os.Bundle;

import android.os.AsyncTask;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
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
public class Rec5000 extends AppCompatActivity  {
    String myJSON;

    private static final String TAG_RESULT = "result";
    private static final String TAG_FINANCIAL_COMPANY_NAME = "financial_company_name";
    private static final String TAG_FINANCIAL_PRODUCT_NAME = "financial_product_name";
    private static final String TAG_HIGHEST_INTEREST_RATE = "highest_interest_rate";
    private static final String TAG_FINANCIAL_PRODUCT_ID = "financial_product_id";
    private static final String TAG_INTEREST_RATE_TYPE = "interest_rate_type";
    JSONArray products = null;

    ArrayList<HashMap<String, String>> productList;

    ListView list;

    ApiServerManager asm = new ApiServerManager();
    String apiEndpoint = asm.getApiEndpoint();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rec5000);
        list = (ListView) findViewById(R.id.listViewDp);
        productList = new ArrayList<HashMap<String, String>>();
        getData(apiEndpoint + "/recommend.php"); // API Endpoint에 맞게 수정 필요

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
        ListView listView = findViewById(R.id.listViewDp);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // 사용자가 선택한 항목의 위치(인덱스)를 position 변수로 얻을 수 있음
                // position은 선택된 아이템의 인덱스를 나타냄

                try {
                    // 클릭된 위치(position)에 해당하는 JSON 객체 가져오기
                    JSONObject clickedItem = products.getJSONObject(position);

                    // 클릭된 아이템의 'finPrdtId' 값 가져오기
                    String finPrdtId = clickedItem.getString(TAG_FINANCIAL_PRODUCT_ID);
                    String intrRateType = clickedItem.getString(TAG_INTEREST_RATE_TYPE);

                    // Toast.makeText(getApplicationContext(), "Clicked item's financial_product_id: " + finPrdtId, Toast.LENGTH_SHORT).show();

                    // Intent를 사용하여 Deposit_detail_screen.java로 데이터를 전달하고 화면을 전환
                    Intent intent = new Intent(Rec5000.this, DepositDetailScreen.class);
                    intent.putExtra("finPrdtId", finPrdtId); // 클릭된 finPrdtId 값을 "finPrdtId"이란 이름의 Extra로 전달
                    intent.putExtra("intrRateType", intrRateType);
                    startActivity(intent); // Deposit_detail_screen.java로 화면 전환

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
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
                    Rec5000.this, productList, R.layout.deposit_list,
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
                            intrRateTypeNameTextView.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(Rec5000.this, R.color.orange)));
                        }
                        else {
                            intrRateTypeNameTextView.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(Rec5000.this, R.color.blue)));
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

    public void getData(String url) {
        class GetDataJSON extends AsyncTask<String, Void, String> {
            // AsyncTask에서 execute() 메서드가 호출되면 내부적으로 doInBackground() 메서드가 호출
            @Override
            protected String doInBackground(String... params) {

                String uri = params[0];

                try {
                    URL url = new URL(uri);
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
                }
            }
        }

        GetDataJSON g = new GetDataJSON();
        g.execute(url);
    }
}