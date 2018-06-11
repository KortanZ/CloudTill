package com.hebut.kortan.cloudtill.fragment;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.hebut.kortan.cloudtill.R;
import com.hebut.kortan.cloudtill.activities.MainActivity;
import com.hebut.kortan.cloudtill.data.SensorData;
import com.hebut.kortan.cloudtill.utilities.NetworkUtils;

import java.util.HashMap;

public class fragmentInfo extends Fragment {
    @Nullable

    EditText searcDate;
    TextView searchResult;
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_info,container,false);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        searcDate = (EditText) getActivity().findViewById(R.id.historyDate);
        searchResult = (TextView) getActivity().findViewById(R.id.historyData);
        Button searchButton = (Button) getActivity().findViewById(R.id.historyButton);

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new historyTask().execute();
            }
        });
    }

    public class historyTask extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... voids) {
            String response = "";
            try {
                HashMap<String, String> map = new HashMap<>();
                map.put("date", searcDate.getText().toString());
                NetworkUtils handler = new NetworkUtils();
                response = handler.Http_Post(map,"/api/query.php");
            } catch (Exception e) {
                e.printStackTrace();
            }
            return response;
        }

        @Override
        protected void onPostExecute(String s) {
            searchResult.setText(s);
        }
    }
}
