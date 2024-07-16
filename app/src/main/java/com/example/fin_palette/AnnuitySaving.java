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

public class Annuity_Saving extends AppCompatActivity {

    Button btn_deposit;
    Button btn_savings;
    Spinner spinner1;

    String myJSON;

    private static final String TAG_RESULTS = "result";
    private static final String TAG_KOR_CO_NM = "kor_co_nm";
    private static final String TAG_FIN_PRDT_NM = "fin_prdt_nm";
    private static final String TAG_AVG_PRFT_RATE = "avg_prft_rate";
    private static final String TAG_FIN_PRDT_CD = "fin_prdt_cd";
    private static final String TAG_PNSN_KIND_NM = "pnsn_kind_nm";
    JSONArray products = null;

    ArrayList<HashMap<String, String>> productList;

    ListView list;
    int itemNo = 0;

    int selected = 0;   // 아무것도 선택 안 했으면 0, 첫번째 것이 선택되면 1, 두번째 것이 선택되면 2, 세번째 것이 선택되면 3
    String [] items1 = {"연금 종류 전체", "연금저축펀드", "연금저축보험(생명)", "연금저축보험(손해)"};
    getIPAddress ipAddress = new getIPAddress();
    String ipv4Address = ipAddress.getIPv4();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.annuity_saving);
        list = (ListView) findViewById(R.id.listViewAsp);
        productList = new ArrayList<HashMap<String, String>>();

        // SharedPreferences를 사용하여 데이터 초기화
        SharedPreferences preferences = getSharedPreferences("annuity_saving_preferences", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();

        // 스피너 상태 저장
        editor.putInt("spinner1_position", 0);

        // 리스트 뷰 상태 저장
        editor.putInt("list_position", 0);
        editor.apply();

        // onResume에서 실행됨
        // getData("http://" + ipv4Address + "/PHP_annuity_saving_ext.php", "0");

        btn_deposit = findViewById(R.id.btn_deposit);
        btn_deposit.setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(), Deposit.class);
            startActivity(intent);
            finish();   // 현재 Activity를 스택에서 제거 (뒤로 가기 하면 메인 화면으로)
        });

        btn_savings = findViewById(R.id.btn_savings);
        btn_savings.setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(), Savings.class);
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

        ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_item, items1);
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner1.setAdapter(adapter1);

        spinner1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selected = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selected = 0;
            }
        });


        //////////////////////// 이미지 뷰 /////////////////////////////////
        ImageView imageView = findViewById(R.id.image1);

        imageView.setOnClickListener(new View.OnClickListener() {   // 돋보기 이미지 뷰 클릭하면 조건에 맞게 필터링
            @Override
            public void onClick(View v) {
                getData("http://" + ipv4Address + "/PHP_annuity_saving_ext.php", selected + "");
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

                    // Toast.makeText(getApplicationContext(), "Clicked item's fin_prdt_cd: " + finPrdtCd, Toast.LENGTH_SHORT).show();

                    // Intent를 사용하여 Annuity_Saving_detail_screen.java로 데이터를 전달하고 화면을 전환
                    Intent intent = new Intent(Annuity_Saving.this, Annuity_Saving_detail_screen.class);
                    intent.putExtra("finPrdtCd", finPrdtCd); // 클릭된 finPrdtCd 값을 "finPrdtCd"이란 이름의 Extra로 전달
                    startActivity(intent); // Annuity_Saving_detail_screen.java로 화면 전환

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
        SharedPreferences preferences = getSharedPreferences("annuity_saving_preferences", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();

        // 스피너 상태 저장
        editor.putInt("spinner1_position", selected);

        // 리스트 뷰 상태 저장
        editor.putInt("list_position", itemNo);

        // Toast.makeText(getApplicationContext(), "itemNo: " + itemNo + "\n" + Arrays.toString(selected), Toast.LENGTH_SHORT).show();

        // 변경사항을 커밋하여 저장
        editor.apply();
    }


    @Override
    public void onResume() {    // 뒤로 가기로 Activity 되돌아왔을 때 리스트 뷰, 스피너 상태 그대로
        super.onResume();

        SharedPreferences preferences = getSharedPreferences("annuity_saving_preferences", MODE_PRIVATE);

        spinner1.setSelection(selected = preferences.getInt("spinner1_position", 0));

        itemNo = preferences.getInt("list_position", 0);

        getData("http://" + ipv4Address + "/PHP_annuity_saving_ext.php", selected + "");

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
                    Annuity_Saving.this, productList, R.layout.annuity_saving_list,
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
                            pnsnKindTextView.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(Annuity_Saving.this, R.color.purple_500)));
                        }
                        else if(pnsnKind.equals("연금저축보험(손해)")) {
                            pnsnKindTextView.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(Annuity_Saving.this, R.color.purple_700)));
                        }
                        else {
                            pnsnKindTextView.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(Annuity_Saving.this, R.color.purple_200)));
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


    public void getData(String url, String a) {
        class GetDataJSON extends AsyncTask<String, Void, String> {
            // AsyncTask에서 execute() 메서드가 호출되면 내부적으로 doInBackground() 메서드가 호출
            @Override
            protected String doInBackground(String... params) {

                String uri = params[0];
                String a = params[1];       // 추가된 파라미터 a

                try {
                    URL url = new URL(uri+ "?a=" + a);
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
        g.execute(url, a);
    }
}