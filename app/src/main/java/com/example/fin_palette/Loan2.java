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

public class Loan2 extends AppCompatActivity {
    Button btn_loan1;
    Button btn_loan3;
    Spinner spinner1;
    Spinner spinner2;
    Spinner spinner3;
    String myJSON;

    private static final String TAG_RESULTS = "result";
    private static final String TAG_KOR_CO_NM = "kor_co_nm";
    private static final String TAG_FIN_PRDT_NM = "fin_prdt_nm";
    private static final String TAG_LEND_RATE_MIN = "lend_rate_min";
    private static final String TAG_OPT_NUM = "opt_num";

    private static final String TAG_LEND_RATE_TYPE_NM = "lend_rate_type_nm";
    private static final String TAG_RPAY_TYPE_NM = "rpay_type_nm";
    JSONArray products = null;

    ArrayList<HashMap<String, String>> productList;

    ListView list;
    int itemNo = 0;
    int [] selected = {0, 0, 0};   // 아무것도 선택 안 했으면 0, 첫번째 것이 선택되면 1, 두번째 것이 선택되면 2
    String [] items1 = {"금융 기관 전체", "1금융", "2금융"};
    String [] items2 = {"상환 유형 전체", "분할상환", "만기일시상환"};
    String [] items3 = {"금리 유형 전체", "고정금리", "변동금리"};
    ApiServerManager asm = new ApiServerManager();
    String apiEndpoint = asm.getApiEndpoint();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.loan2);
        list = (ListView) findViewById(R.id.listViewLp2);
        productList = new ArrayList<HashMap<String, String>>();


        // SharedPreferences를 사용하여 데이터 초기화
        SharedPreferences preferences = getSharedPreferences("loan2_preferences", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();

        // 스피너 상태 저장
        editor.putInt("spinner1_position", 0);
        editor.putInt("spinner2_position", 0);
        editor.putInt("spinner3_position", 0);

        // 리스트 뷰 상태 저장
        editor.putInt("list_position", 0);
        editor.apply();

        // onResume에서 실행됨
        // getData(apiEndpoint + "/PHP_loan2_ext.php", "0", "0", "0"); // IP주소에 맞게 수정 필요 (Default: 0, 0, 0)

        btn_loan1 = findViewById(R.id.btn_loan1);
        btn_loan1.setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(), Loan1.class);
            startActivity(intent);
            finish();
        });

        btn_loan3 = findViewById(R.id.btn_loan3);
        btn_loan3.setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(), Loan3.class);
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
        spinner3 = findViewById(R.id.spinner3);

        ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_item, items1);
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner1.setAdapter(adapter1);
        ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_item, items2);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner2.setAdapter(adapter2);
        ArrayAdapter<String> adapter3 = new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_item, items3);
        adapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner3.setAdapter(adapter3);

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
        spinner3.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selected[2] = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selected[2] = 0;
            }
        });


        //////////////////////// 이미지 뷰 /////////////////////////////////
        ImageView imageView = findViewById(R.id.image1);

        imageView.setOnClickListener(new View.OnClickListener() {   // 돋보기 이미지 뷰 클릭하면 조건에 맞게 필터링
            @Override
            public void onClick(View v) {
                getData(apiEndpoint + "/PHP_loan2_ext.php", selected[0] + "", selected[1] + "", selected[2] + "");
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
                    String optNum = clickedItem.getString(TAG_OPT_NUM);

                    // Toast.makeText(getApplicationContext(), "Clicked item's opt_num: " + optNum, Toast.LENGTH_SHORT).show();

                    // Intent를 사용하여 Loan2_detail_screen.java로 데이터를 전달하고 화면을 전환
                    Intent intent = new Intent(Loan2.this, Loan2DetailScreen.class);
                    intent.putExtra("optNum", optNum); // 클릭된 optNum 값을 "optNum"이란 이름의 Extra로 전달
                    startActivity(intent); // Loan2_detail_screen.java로 화면 전환

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
        SharedPreferences preferences = getSharedPreferences("loan2_preferences", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();

        // 스피너 상태 저장
        editor.putInt("spinner1_position", selected[0]);
        editor.putInt("spinner2_position", selected[1]);
        editor.putInt("spinner3_position", selected[2]);

        // 리스트 뷰 상태 저장
        editor.putInt("list_position", itemNo);

        // Toast.makeText(getApplicationContext(), "itemNo: " + itemNo + "\n" + Arrays.toString(selected), Toast.LENGTH_SHORT).show();

        // 변경사항을 커밋하여 저장
        editor.apply();
    }


    @Override
    public void onResume() {    // 뒤로 가기로 Activity 되돌아왔을 때 리스트 뷰, 스피너 상태 그대로
        super.onResume();

        SharedPreferences preferences = getSharedPreferences("loan2_preferences", MODE_PRIVATE);

        spinner1.setSelection(selected[0] = preferences.getInt("spinner1_position", 0));
        spinner2.setSelection(selected[1] = preferences.getInt("spinner2_position", 0));
        spinner3.setSelection(selected[2] = preferences.getInt("spinner3_position", 0));

        itemNo = preferences.getInt("list_position", 0);

        getData(apiEndpoint + "/PHP_loan2_ext.php", selected[0] + "", selected[1] + "", selected[2] + "");

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
                    Loan2.this, productList, R.layout.loan2_list,
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
                            lendRateTypeTextView.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(Loan2.this, R.color.teal_700)));
                        }
                        else {
                            lendRateTypeTextView.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(Loan2.this, R.color.brown_green)));
                        }
                    }

                    if (rpayType != null) {
                        if(rpayType.equals("분할상환")){
                            rpayTypeTextView.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(Loan2.this, R.color.magenta)));
                        }
                        else {
                            rpayTypeTextView.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(Loan2.this, R.color.purple_700)));
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


    public void getData(String url, String a, String b, String c) {
        class GetDataJSON extends AsyncTask<String, Void, String> {
            // AsyncTask에서 execute() 메서드가 호출되면 내부적으로 doInBackground() 메서드가 호출
            @Override
            protected String doInBackground(String... params) {

                String uri = params[0];
                String a = params[1];       // 추가된 파라미터 a, b, c
                String b = params[2];
                String c = params[3];

                try {
                    URL url = new URL(uri+ "?a=" + a + "&b=" + b + "&c=" + c);
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
        g.execute(url, a, b, c);
    }
}