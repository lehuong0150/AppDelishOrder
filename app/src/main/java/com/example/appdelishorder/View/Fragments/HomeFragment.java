package com.example.appdelishorder.View.Fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.appdelishorder.Adapter.adapterCategory;
import com.example.appdelishorder.Adapter.adapterProduct;
import com.example.appdelishorder.Contract.categoryContract;
import com.example.appdelishorder.Contract.productContract;
import com.example.appdelishorder.Model.Category;
import com.example.appdelishorder.Model.Product;
import com.example.appdelishorder.Presenter.categoryPresenter;
import com.example.appdelishorder.Presenter.productPresenter;
import com.example.appdelishorder.R;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment implements categoryContract.View , productContract.View{
    private RecyclerView recyclerListCategory,recyclerListProduct;
    private TextView txtSeeAll;
    private EditText edSearch;
    private ImageButton btnFilter;
    private ProgressBar progressBarCategory, progressBarProduct;
    private adapterCategory categoryAdapter;
    private adapterProduct adapterProduct;
    private categoryPresenter presenterCategory;
    private productPresenter presenterProduct;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);
        // Khởi tạo RecyclerView và LayoutManager
        //danh sach danh muc
        btnFilter = rootView.findViewById(R.id.btn_filter);
        edSearch = rootView.findViewById(R.id.ed_search);
        txtSeeAll = rootView.findViewById(R.id.btnSeeAll);
        recyclerListCategory = rootView.findViewById(R.id.categoryView);
        progressBarCategory = rootView.findViewById(R.id.progressBarCategory);
        recyclerListCategory.setLayoutManager(new GridLayoutManager(getContext(), 3)); // 3 cột trong mỗi hàng

        //danh sach san pham
        recyclerListProduct = rootView.findViewById(R.id.bestFoodView);
        progressBarProduct = rootView.findViewById(R.id.progressBarBestFoods);
        recyclerListProduct.setLayoutManager(new GridLayoutManager(getContext(), 3)); // 3 cột trong mỗi hàng

        // Khởi tạo presenter và gọi API để load danh mục
        presenterCategory = new categoryPresenter(HomeFragment.this);
        presenterCategory.loadCategories(); // Gọi API

        // Khởi tạo presenter và gọi API để load san pham
        presenterProduct = new productPresenter(HomeFragment.this);
        presenterProduct.loadProducts(); // Gọi API

        //xem tat ca sp
        txtSeeAll.setOnClickListener(v -> {
            presenterProduct.loadProducts(); // Gọi API lấy toàn bộ sản phẩm
        });

        // Tìm kiếm sản phẩm khi người dùng nhập từ khóa
        edSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                String query = charSequence.toString().trim();
                if (!query.isEmpty()) {
                    // Gọi API tìm kiếm khi người dùng nhập từ khóa
                    presenterProduct.searchProducts(query);
                } else {
                    // Nếu không có từ khóa tìm kiếm, có thể gọi lại API để tải tất cả sản phẩm
                    presenterProduct.loadProducts();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) { }
        });
        return rootView;
    }

    @Override
    public void showLoading() {

    }

    @Override
    public void hideLoading() {

    }

    // hien thi danh muc
    @Override
    public void showCategories(List<Category> categories) {
        progressBarCategory.setVisibility(View.GONE);
        categoryAdapter =  new adapterCategory(getContext(),categories, new adapterCategory.OnCategoryClickListener(){
            // Khi người dùng chọn một danh mục, lọc sản phẩm
            @Override
            public void onCategoryClick(Category category) {
                Log.d("CategoryClick", "Clicked on category: " + category.getId() + " - " + category.getName());
                presenterProduct.loadProductsByCategory(category.getId());
            }
        });
        recyclerListCategory.setAdapter(categoryAdapter);


    }

    @Override
    public void showError(String message) {
        progressBarCategory.setVisibility(View.VISIBLE);
        Log.d("ShowError", "showError: "+ message);
    }
    //hien thi san pham
    @Override
    public void showProducts(List<Product> products) {
        progressBarProduct.setVisibility(View.GONE);
        adapterProduct =  new adapterProduct(getContext(),products);
        recyclerListProduct.setAdapter(adapterProduct);
    }

    @Override
    public void showErrorProduct(String message) {
        progressBarProduct.setVisibility(View.VISIBLE);
        Log.d("ShowError", "showError: "+ message);
    }



}