package com.admin.coredge.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.admin.coredge.Adapter.HistoryAdapter;
import com.admin.coredge.MainActivity;
import com.admin.coredge.Modal.HistoryModel;
import com.admin.coredge.R;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.ismaeldivita.chipnavigation.ChipNavigationBar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class HistoryActivity extends AppCompatActivity {

    String USERNAME;
    ArrayList<HistoryModel> historyModels;
    private RecyclerView recyclerView;
    TextView speedTest;
    private ProgressBar spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        speedTest = findViewById(R.id.speedTest);
        USERNAME = getIntent().getExtras().getString("username");
        spinner = (ProgressBar) findViewById(R.id.progressBar);

        historyModels = new ArrayList<>();
        recyclerView = findViewById(R.id.rv_history);
        getHistory();

        speedTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void getHistory() {
        // Instantiate the RequestQueue.
        spinner.setVisibility(View.VISIBLE);
        RequestQueue queue = Volley.newRequestQueue(this);
        String url ="http://142.93.214.28:9200/5g-data/_search?q="+USERNAME;

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            spinner.setVisibility(View.GONE);
                            JSONObject jsonObject = new JSONObject(response);
                            String jsonObject1 = jsonObject.getString("hits");
                            JSONObject jsonObject2 = new JSONObject(jsonObject1);
                            JSONArray jsonArray = jsonObject2.getJSONArray("hits");

                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject object = jsonArray.getJSONObject(i);
                                HistoryModel historyModel = new HistoryModel();
                                String jsonObject3 = object.getString("_source");
                                JSONObject jsonObject4 = new JSONObject(jsonObject3);
                                historyModel.setTimestamp(jsonObject4.getString("@timestamp"));
                                historyModel.setPing(jsonObject4.getString("png"));
                                historyModel.setDownloading(jsonObject4.getString("downloading"));
                                historyModel.setUploading(jsonObject4.getString("uploading"));
                                historyModel.setIp(jsonObject4.getString("isp"));
                                historyModels.add(historyModel);
                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        setuprecyclerview(historyModels);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
               // textView.setText("That didn't work!");
            }
        });

// Add the request to the RequestQueue.
        queue.add(stringRequest);
    }

    private void setuprecyclerview(List<HistoryModel> historyModelList) {
        HistoryAdapter historyAdapter = new HistoryAdapter(this, historyModelList);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 1));
        recyclerView.setHasFixedSize(true);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
//        recyclerView.addItemDecoration(dividerItemDecoration);
        recyclerView.setAdapter(historyAdapter);
    }
}