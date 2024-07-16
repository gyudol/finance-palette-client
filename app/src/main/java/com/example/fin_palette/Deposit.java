package com.example.fin_palette;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;

import android.content.Intent;
import android.os.Bundle;

import android.os.AsyncTask;
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

public class Deposit extends AppCompatActivity {
    Button btn_savings;
    Button btn_annuity_saving;

    Spinner spinner1;
    Spinner spinner2;

    String myJSON;

    private static final String TAG_RESULTS = "result";
    private static final String TAG_KOR_CO_NM = "kor_co_nm";
    private static final String TAG_FIN_PRDT_NM = "fin_prdt_nm";
    private static final String TAG_HIGHEST_INTR_RATE = "highest_intr_rate";
    private static final String TAG_FIN_PRDT_CD = "fin_prdt_cd";
    private static final String TAG_INTR_RATE_TYPE = "intr_rate_type";
    JSONArray products = null;

    ArrayList<HashMap<String, String>> productList;

    ListView list;
    int itemNo = 0;   // 리스트 뷰 선택됐을 때 item Number

    int [] selected = {0, 0};   // 아무것도 선택 안 했으면 0, 첫번째 것이 선택되면 1, 두번째 것이 선택되면 2
    String [] items1 = {"금융 기관 전체", "1금융", "2금융"};
    String [] items2 = {"금리 유형 전체", "단리", "복리"};
    ApiServerManager asm = new ApiServerManager();
    String apiEndpoint = asm.getApiEndpoint();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.deposit);
        list = (ListView) findViewById(R.id.listViewDp);
        productList = new ArrayList<HashMap<String, String>>();

        // SharedPreferences를 사용하여 데이터 초기화
        SharedPreferences preferences = getSharedPreferences("deposit_preferences", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();

        // 스피너 상태 저장
        editor.putInt("spinner1_position", 0);
        editor.putInt("spinner2_position", 0);

        // 리스트 뷰 상태 저장
        editor.putInt("list_position", 0);
        editor.apply();

        // onResume에서 실행됨
        // getData(apiEndpoint + "/PHP_deposit_ext.php", "0", "0"); // IP주소에 맞게 수정 필요 (Default: 0, 0)

        btn_savings = findViewById(R.id.btn_savings);
        btn_savings.setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(), Savings.class);
            startActivity(intent);
            finish();   // 현재 Activity를 스택에서 제거 (뒤로 가기 하면 메인 화면으로)
        });

        btn_annuity_saving = findViewById(R.id.btn_annuity_saving);
        btn_annuity_saving.setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(), AnnuitySaving.class);
            startActivity(intent);
            finish();   // 현재 Activity를 스택에서 제거 (뒤로 가기 하면 메인 화면으로)
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
        ImageView imageView = findViewById(R.id.image1);

        imageView.setOnClickListener(new View.OnClickListener() {   // 돋보기 이미지 뷰 클릭하면 조건에 맞게 필터링
            @Override
            public void onClick(View v) {
                getData(apiEndpoint + "/PHP_deposit_ext.php", selected[0] + "", selected[1] + "");
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

                    // 클릭된 아이템의 'fin_prdt_nm' 값 가져오기
                    String finPrdtCd = clickedItem.getString(TAG_FIN_PRDT_CD);
                    String intrRateType = clickedItem.getString(TAG_INTR_RATE_TYPE);

                    // Toast.makeText(getApplicationContext(), "Clicked item's intrRateType: " + intrRateType, Toast.LENGTH_SHORT).show();

                    // Intent를 사용하여 Deposit_detail_screen.java로 데이터를 전달하고 화면을 전환
                    Intent intent = new Intent(Deposit.this, DepositDetailScreen.class);
                    intent.putExtra("finPrdtCd", finPrdtCd); // 클릭된 finPrdtCd 값을 "finPrdtCd"이란 이름의 Extra로 전달
                    intent.putExtra("intrRateType", intrRateType);
                    startActivity(intent); // Deposit_detail_screen.java로 화면 전환

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
        SharedPreferences preferences = getSharedPreferences("deposit_preferences", MODE_PRIVATE);
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

        SharedPreferences preferences = getSharedPreferences("deposit_preferences", MODE_PRIVATE);

        spinner1.setSelection(selected[0] = preferences.getInt("spinner1_position", 0));
        spinner2.setSelection(selected[1] = preferences.getInt("spinner2_position", 0));

        itemNo = preferences.getInt("list_position", 0);

        getData(apiEndpoint + "/PHP_deposit_ext.php", selected[0] + "", selected[1] + "");

        // Toast.makeText(getApplicationContext(), "itemNo: " + itemNo + "\n" + Arrays.toString(selected), Toast.LENGTH_SHORT).show();
    }


    protected void showList() {
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
                    Deposit.this, productList, R.layout.deposit_list,
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
                            intrRateTypeTextView.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(Deposit.this, R.color.orange)));
                        }
                        else {
                            intrRateTypeTextView.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(Deposit.this, R.color.blue)));
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

    public void getData(String url, String a, String b) {
        class GetDataJSON extends AsyncTask<String, Void, String> {
            // AsyncTask에서 execute() 메서드가 호출되면 내부적으로 doInBackground() 메서드가 호출
            @Override
            protected String doInBackground(String... params) {

                String uri = params[0];
                String a = params[1];       // 추가된 파라미터 a, b
                String b = params[2];

                try {
                    URL url = new URL(uri+ "?a=" + a + "&b=" + b);
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
        g.execute(url, a, b);
    }
}