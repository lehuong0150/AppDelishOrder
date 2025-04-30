package com.example.appdelishorder.Model;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appdelishorder.Adapter.adapterOrderOnGoing;
import com.example.appdelishorder.Contract.orderContract;
import com.example.appdelishorder.Presenter.orderPresenter;
import com.example.appdelishorder.R;
import com.example.appdelishorder.Utils.SessionManager;

import java.util.ArrayList;
import java.util.List;

public class AccountResponse {
    private String email;
    private String password;
    private String fullname;

    public AccountResponse(String email, String password, String fullname) {
        this.email = email;
        this.password = password;
        this.fullname = fullname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }
}

