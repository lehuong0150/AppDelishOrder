package com.example.appdelishorder.View.Activities;

import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.appdelishorder.R;
import com.example.appdelishorder.View.Fragments.HistoryFragment;
import com.example.appdelishorder.View.Fragments.HomeFragment;
import com.example.appdelishorder.View.Fragments.OrderFragment;
import com.example.appdelishorder.View.Fragments.ProfileFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class HomeActivity extends AppCompatActivity {
    private BottomNavigationView menuBottom;
    private enum CurrentPage {
        HOME, ORDER, HISTORY, PROFILE
    }
    private CurrentPage currentPage = CurrentPage.HOME;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home);

        replaceFragment(new HomeFragment());

        //find id
        menuBottom = findViewById(R.id.bottomNavigationView);

        if (getIntent() != null) {
            String fragmentTag = "home";
            Log.d("curr", "onCreate: "+ fragmentTag);

            if (fragmentTag != null) {
                // Hiển thị Fragment tương ứng với fragmentTag
                if (fragmentTag.equals("home")) {
                    HomeFragment homeFragment =new HomeFragment();
                    replaceFragment(homeFragment);
                    currentPage = CurrentPage.HOME;
                } else if (fragmentTag.equals("order")) {
                    replaceFragment(new OrderFragment());
                    currentPage = CurrentPage.ORDER;
                    menuBottom.setSelectedItemId(R.id.menu_Order);

                }
                } else if (fragmentTag.equals("history")) {
                    replaceFragment(new HistoryFragment());
                    currentPage = CurrentPage.HISTORY;
                    menuBottom.setSelectedItemId(R.id.menu_History);

                }else if (fragmentTag.equals("profile")) {
                    replaceFragment(new ProfileFragment());
                    currentPage = CurrentPage.PROFILE;
                    menuBottom.setSelectedItemId(R.id.menu_Profile);
                }
            }
        //action : Thiết lập listener cho BottomNavigationView để xử lý sự kiện khi người dùng chọn một mục
        menuBottom.setOnItemSelectedListener(item -> {
            int i = item.getItemId();
            if (i == R.id.menu_Home) {
                replaceFragment(new HomeFragment());
                currentPage = CurrentPage.HOME;
            } else if (i == R.id.menu_Order) {
                replaceFragment(new OrderFragment());
                currentPage = CurrentPage.ORDER;

            } else if (i == R.id.menu_History) {
                replaceFragment(new HistoryFragment());
                currentPage = CurrentPage.HISTORY;
            } else if (i == R.id.menu_Profile) {
                replaceFragment(new ProfileFragment());
                currentPage = CurrentPage.PROFILE;
            }
            return true;
        });

    }


    public void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_layout, fragment);
        fragmentTransaction.commit();
    }
}
