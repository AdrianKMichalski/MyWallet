package psm.mywallet.client.android;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import psm.mywallet.client.android.pojo.ListEntry;

/**
 * @author Adrian Michalski
 */
public class EntriesActivity extends AppCompatActivity {

    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entries);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshEntriesLayout);

        swipeRefreshLayout.setColorSchemeColors(
                getResources().getColor(R.color.colorPrimary),
                getResources().getColor(R.color.colorPrimaryDark),
                getResources().getColor(R.color.colorPrimary),
                getResources().getColor(R.color.colorAccent)
        );

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                queryEntries();
            }
        });

        queryEntries();


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(EntriesActivity.this, NewEntryActivity.class);
                startActivity(intent);
            }
        });
    }

    private void queryEntries() {
        RequestQueue queue = Volley.newRequestQueue(this);
        String balanceUrl = "http://localhost/mywallet/entries/balance";
        String entriesUrl = "http://localhost/mywallet/entries";

        StringRequest balanceRequest = new StringRequest(Request.Method.GET, balanceUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                TextView accountBalanceTextView = (TextView) findViewById(R.id.accountBalanceTextView);
                accountBalanceTextView.setText("Account balance: " + response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // TODO: handle loading errors
            }
        });

        JsonArrayRequest entriesRequest = new JsonArrayRequest(Request.Method.GET, entriesUrl, null, new Response.Listener<JSONArray>() {

            @Override
            public void onResponse(JSONArray response) {
                try {
                    ListView listView = (ListView) findViewById(R.id.entriesListView);
                    ArrayList<ListEntry> entries = new ArrayList<>();

                    for (int i = 0; i < response.length(); i++) {
                        JSONObject jsonObject = response.getJSONObject(i);
                        String description = jsonObject.getString("description");
                        JSONArray tagsArray = jsonObject.getJSONArray("tags");
                        String tags = "";
                        for (int j = 0; j < tagsArray.length(); j++) {
                            tags = "#" + tagsArray.getString(j) + " ";
                        }

                        String value = jsonObject.getString("value");

                        SimpleDateFormat sdfDate = new SimpleDateFormat("dd.MM.yyyy HH:mm");
                        String date = sdfDate.format(new Date(jsonObject.getLong("createDate")));

                        entries.add(new ListEntry(description, tags, value, date));
                        Log.i("ENTRIES", description + " " + tags + " " + value);
                    }

                    ArrayAdapter<ListEntry> adapter = new ListEntryAdapter(EntriesActivity.this, R.layout.single_entry_layout, entries);

                    listView.setAdapter(adapter);
                } catch (Exception e) {

                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                // TODO: handle loading errors
            }
        });


        ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);

        queue.add(entriesRequest);
        queue.add(balanceRequest);

        queue.addRequestFinishedListener(new RequestQueue.RequestFinishedListener<Object>() {
            @Override
            public void onRequestFinished(Request<Object> request) {
                ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar);
                progressBar.setVisibility(View.INVISIBLE);
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

}
