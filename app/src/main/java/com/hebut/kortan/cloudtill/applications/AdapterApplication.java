package com.hebut.kortan.cloudtill.applications;

import android.app.Application;

import com.hebut.kortan.cloudtill.fragment.MyClientRecyclerViewAdapter;

public class AdapterApplication {
    private static MyClientRecyclerViewAdapter adapter;

    public MyClientRecyclerViewAdapter getAdapter() {
        return adapter;
    }

    public void setAdapter(MyClientRecyclerViewAdapter adapter) {
        this.adapter = adapter;
    }
}
