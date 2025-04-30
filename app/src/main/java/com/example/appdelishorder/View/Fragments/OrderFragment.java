package com.example.appdelishorder.View.Fragments;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.appdelishorder.Model.AccountResponse;
import com.example.appdelishorder.R;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class OrderFragment extends Fragment {

    private TabLayout tabLayout;
    private ViewPager2 viewPager;

    public OrderFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_order, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tabLayout = view.findViewById(R.id.tabLayout);
        viewPager = view.findViewById(R.id.viewPager);

        viewPager.setAdapter(new OrderPagerAdapter(getActivity()));

        // Liên kết TabLayout với ViewPager
        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            if (position == 0) {
                tab.setText("Đang đến");
            } else {
                tab.setText("Lịch sử");
            }
        }).attach();

        // Mặc định set vào tab "Đang đến" (vị trí 0) hoặc "Đang giao" tùy bạn đổi
        viewPager.setCurrentItem(0, false);
    }

    private static class OrderPagerAdapter extends FragmentStateAdapter {

        public OrderPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
            super(fragmentActivity);
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            if (position == 0) {
                return new OnGoingOrdersFragment(); // Fragment đơn hàng đang đến
            } else {
                return new OrderHistoryFragment(); // Fragment lịch sử đơn hàng
            }
        }

        @Override
        public int getItemCount() {
            return 2; // Có 2 tab
        }
    }
}
