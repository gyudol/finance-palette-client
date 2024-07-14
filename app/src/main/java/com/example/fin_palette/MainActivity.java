package com.example.fin_palette;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    HomeFragment home;
    MenuFragment menu;
    SettingsFragment settings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        home = new HomeFragment();
        menu = new MenuFragment();
        settings = new SettingsFragment();

        // 앱이 처음 실행되었을 때 home 화면이 보이게 지정
        getSupportFragmentManager().beginTransaction().replace(R.id.container, home).commit();

        // AAID Check
        AAID_State as = new AAID_State();
        getIPAddress ipAddress = new getIPAddress();
        as.checkTable("http://" + ipAddress.getIPv4() + "/PHP_aaid_chk.php");


        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnItemSelectedListener(
                new BottomNavigationView.OnItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        int itemId = item.getItemId();
                        if (itemId == R.id.tab1) {
                            //Toast.makeText(getApplicationContext(), "홈 탭 선택됨", Toast.LENGTH_LONG).show();
                            getSupportFragmentManager().beginTransaction().replace(R.id.container, home).commit();
                            return true;
                        } else if (itemId == R.id.tab2) {
                            //Toast.makeText(getApplicationContext(), "메뉴 탭 선택됨", Toast.LENGTH_LONG).show();
                            getSupportFragmentManager().beginTransaction().replace(R.id.container, menu).commit();
                            return true;
                        } else if (itemId == R.id.tab3) {
                            //Toast.makeText(getApplicationContext(), "설정 탭 선택됨", Toast.LENGTH_LONG).show();
                            getSupportFragmentManager().beginTransaction().replace(R.id.container, settings).commit();
                            return true;
                        }

                        return false;
                    }
                }
        );
    }

}
