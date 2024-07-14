package com.example.fin_palette;

import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import org.json.JSONArray;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class Survey extends AppCompatActivity {
    Button btn_finish;

    JSONArray products = null;

    getIPAddress ipAddress = new getIPAddress();
    String ipv4Address = ipAddress.getIPv4();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.survey);

        String URL = "http://" + ipv4Address + "/PHP_userInfo_upd.php"; // IP주소에 맞게 수정 필요

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed(); // 뒤로 가기 버튼을 클릭하면 onBackPressed()가 호출됩니다.
            }
        });

        RadioGroup rg_age = findViewById(R.id.rg_age);
        RadioGroup rg_gender = findViewById(R.id.rg_gender);
        RadioGroup rg_select1 = findViewById(R.id.rg_select1);
        RadioGroup rg_select2 = findViewById(R.id.rg_select2);
        RadioGroup rg_select3 = findViewById(R.id.rg_select3);
        RadioGroup rg_select4 = findViewById(R.id.rg_select4);
        RadioGroup rg_select5 = findViewById(R.id.rg_select5);

        btn_finish = findViewById(R.id.btn_finish);
        btn_finish.setOnClickListener(view -> {     // 사전조사 데이터 DB에 저장하고

            // 선택된 라디오 그룹들의 선택된 아이디를 가져와 초기값을 설정합니다.
            String ageDistValue = "0";      // 선택 안 한 건 0
            String preferValue = "";

            // 첫 번째 라디오 그룹 처리
            int checkedRadioIdGroup1 = rg_age.getCheckedRadioButtonId();
            if (checkedRadioIdGroup1 != -1) {       // 라디오 버튼 선택됐을 때 (선택 안 됐으면 그대로)
                RadioButton radioButton = findViewById(checkedRadioIdGroup1);
                int selectedRadioGroup1 = rg_age.indexOfChild(radioButton) + 1;
                ageDistValue = (selectedRadioGroup1 * 10) + "";
            }

            // 나머지 라디오 그룹들 처리
            RadioGroup [] otherRadioGroups = {rg_gender, rg_select1, rg_select2, rg_select3, rg_select4, rg_select5};
            for (int i = 0; i < otherRadioGroups.length; i++) {
                RadioGroup currentRadioGroup = otherRadioGroups[i];
                int checkedRadioId = currentRadioGroup.getCheckedRadioButtonId();

                if (checkedRadioId != -1) {         // 라디오 버튼 선택됐을 때
                    RadioButton radioButton = findViewById(checkedRadioId);
                    int selectedRadio = currentRadioGroup.indexOfChild(radioButton) + 1;
                    preferValue += selectedRadio;
                }
                else {          // 선택 안 됐으면 0 붙여줌
                    preferValue += 0;
                }
            }

            setData(URL, ageDistValue, preferValue);


            /////////////////////////////////// MainActivity로 이동
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
        });

        TextView q1 = findViewById(R.id.q1);
        SpannableStringBuilder span = new SpannableStringBuilder("Q1 연령대를 알려주세요.");
        span.setSpan(new android.text.style.ForegroundColorSpan(Color.BLUE), 0, 2, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        q1.setText(span);

        TextView q2 = findViewById(R.id.q2);
        SpannableStringBuilder span2 = new SpannableStringBuilder("Q2 성별을 알려주세요.");
        span2.setSpan(new android.text.style.ForegroundColorSpan(Color.BLUE), 0, 2, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        q2.setText(span2);

        TextView q3 = findViewById(R.id.q3);
        SpannableStringBuilder span3 = new SpannableStringBuilder("Q3 금융상품을 선택할 때 나는");
        span3.setSpan(new android.text.style.ForegroundColorSpan(Color.BLUE), 0, 2, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        q3.setText(span3);

        TextView q4 = findViewById(R.id.q4);
        SpannableStringBuilder span4 = new SpannableStringBuilder("Q4 나는");
        span4.setSpan(new android.text.style.ForegroundColorSpan(Color.BLUE), 0, 2, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        q4.setText(span4);

        TextView q5 = findViewById(R.id.q5);
        SpannableStringBuilder span5 = new SpannableStringBuilder("Q5 최고한도(예치금액)");
        span5.setSpan(new android.text.style.ForegroundColorSpan(Color.BLUE), 0, 2, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        q5.setText(span5);

        TextView q6 = findViewById(R.id.q6);
        SpannableStringBuilder span6 = new SpannableStringBuilder("Q6 정기예금 가입 시 원하는 저축기간");
        span6.setSpan(new android.text.style.ForegroundColorSpan(Color.BLUE), 0, 2, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        q6.setText(span6);

        TextView q7 = findViewById(R.id.q7);
        SpannableStringBuilder span7 = new SpannableStringBuilder("Q7 정기적금 가입 시 원하는 저축기간");
        span7.setSpan(new android.text.style.ForegroundColorSpan(Color.BLUE), 0, 2, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        q7.setText(span7);

    }

    public void setData(String url, String age_dist, String prefer) {
        class SetDataJSON extends AsyncTask<String, Void, String> {
            @Override
            protected String doInBackground(String... params) {
                String uri = params[0];
                String age_dist = params[1]; // 추가된 파라미터 a, b
                String prefer = params[2];

                try {
                    URL url = new URL(uri + "?user_name=금린이&age_dist=" + age_dist + "&prefer=" + prefer);
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();

                    StringBuilder sb = new StringBuilder();
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(con.getInputStream()));
                    String json;
                    while ((json = bufferedReader.readLine()) != null) {
                        sb.append(json + "\n");
                    }

                    return "Success";       // INSERT/UPDATE 성공
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            }
            @Override
            protected void onPostExecute(String result) {
                if (result != null && result.equals("Success")) {
                    Toast.makeText(getApplicationContext(), "저장되었습니다.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), "저장에 실패했습니다.", Toast.LENGTH_SHORT).show();
                }
            }
        }

        SetDataJSON g = new SetDataJSON();
        g.execute(url, age_dist, prefer);
    }
}
