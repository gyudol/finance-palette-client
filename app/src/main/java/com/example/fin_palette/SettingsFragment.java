package com.example.fin_palette;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class SettingsFragment extends Fragment {
    private View view;
    private Button mySurveyButton;
    private Button myBookmarkButton;
    private Button myViewHistoryButton;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.settings, container, false);

        Toolbar toolbar = view.findViewById(R.id.toolbar);
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);

        mySurveyButton = (Button) view.findViewById(R.id.btn_my_survey);
        myBookmarkButton = (Button) view.findViewById(R.id.btn_my_bookmark);
        myViewHistoryButton = (Button) view.findViewById(R.id.btn_my_view_history);
        
        ApiServerManager asm = new ApiServerManager();
        String apiEndpoint = asm.getApiEndpoint();

        AaidManager am = new AaidManager();

        mySurveyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), Survey.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
            }
        });

        myBookmarkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getMostData(apiEndpoint + "/most_stars.php", am.aaid, true); // API Endpoint에 맞게 수정 필요
            }
        });

        myViewHistoryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getMostData(apiEndpoint + "/most_recent_view.php", am.aaid, false); // API Endpoint에 맞게 수정 필요
            }
        });

        return view;
    }


    public void getMostData(String url, String aaid, boolean isBookmark) {
        class GetDataJSON extends AsyncTask<String, Void, String> {
            // AsyncTask에서 execute() 메서드가 호출되면 내부적으로 doInBackground() 메서드가 호출
            @Override
            protected String doInBackground(String... params) {

                String uri = params[0];
                String aaid = params[1];

                try {
                    URL url = new URL(uri + "?aaid=" + aaid);
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
                    if(isBookmark) {
                        if(result.equals("0")) {
                            // 토스트 메시지 들어갈 수 없음
                            Toast.makeText(getActivity(),"북마크 등록된 상품이 없습니다.", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            Intent intent = new Intent(getActivity(), BookmarkList.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                            intent.putExtra("prdtNum", result);
                            startActivity(intent);
                        }
                    }
                    else {
                        if(result.equals("0")) {
                            // 토스트 메시지 들어갈 수 없음
                            Toast.makeText(getActivity(),"조회 기록이 없습니다.", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            Intent intent = new Intent(getActivity(), ViewHistory.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                            intent.putExtra("prdtNum", result);
                            startActivity(intent);
                        }
                    }
                }
            }
        }

        GetDataJSON g = new GetDataJSON();
        g.execute(url, aaid);
    }
}