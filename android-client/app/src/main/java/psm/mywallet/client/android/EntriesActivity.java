package psm.mywallet.client.android;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.common.collect.ImmutableSet;

import org.json.JSONArray;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;

import psm.mywallet.api.EntryDTO;

/**
 * @author Adrian Michalski
 */
public class EntriesActivity extends AppCompatActivity {

    private String baseUrl;
    private String balanceUrl;
    private String entriesUrl;

    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entries);

        checkServerConfiguration();

        setupServerEndpoints();
        setupToolbar();
        setupHashtagButton();
        setupSettingsButton();
        setUpSwipeRefresh();
        setupFloatingAddEntryButton();

        queryEntries();
    }

    private void checkServerConfiguration() {
        if (getServerAddressFromSharedPreferences().isEmpty()) {
            Toast.makeText(this, "Server address not configured", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(EntriesActivity.this, ConfigurationActivity.class);
            startActivity(intent);
        }
    }

    private void setupServerEndpoints() {
        baseUrl = getServerAddressFromSharedPreferences();
        balanceUrl = getAccountBalanceUrl(baseUrl);
        entriesUrl = getEntriesUrl(baseUrl);
    }

    private void setupToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    private void setupHashtagButton() {
        ImageButton hashTagImageButton = (ImageButton) findViewById(R.id.hashTagImageButton);
        hashTagImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(EntriesActivity.this, "TODO", Toast.LENGTH_SHORT).show(); // TODO
            }
        });
    }

    private void setupSettingsButton() {
        ImageButton settingsImageButton = (ImageButton) findViewById(R.id.settingsImageButton);
        settingsImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EntriesActivity.this, ConfigurationActivity.class);
                startActivity(intent);
            }
        });
    }

    private void setUpSwipeRefresh() {
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
    }

    private void setupFloatingAddEntryButton() {
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

        StringRequest balanceRequest = new StringRequest(Request.Method.GET, balanceUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                TextView accountBalanceTextView = (TextView) findViewById(R.id.accountBalanceTextView);
                accountBalanceTextView.setText(getString(R.string.entries_title_account_balance) + response);
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
                    ArrayList<EntryDTO> entries = new ArrayList<>();

                    for (int i = 0; i < response.length(); i++) {
                        JSONObject jsonObject = response.getJSONObject(i);
                        EntryDTO entryDTO = new EntryDTO();

                        String description = jsonObject.getString("description");
                        entryDTO.setDescription(description);

                        JSONArray tagsArray = jsonObject.getJSONArray("tags");
                        ImmutableSet.Builder<String> tags = ImmutableSet.builder();
                        for (int j = 0; j < tagsArray.length(); j++) {
                            tags.add(tagsArray.getString(j));
                        }
                        entryDTO.setTags(tags.build());

                        String value = jsonObject.getString("value");
                        entryDTO.setValue(new BigDecimal(value));

                        Date date = new Date(jsonObject.getLong("createDate"));
                        entryDTO.setCreateDate(date);

                        entries.add(entryDTO);
                    }

                    ArrayAdapter<EntryDTO> adapter = new ListEntryAdapter(EntriesActivity.this, R.layout.single_entry_layout, entries);

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

        swipeRefreshLayout.setRefreshing(true);

        queue.add(entriesRequest);
        queue.add(balanceRequest);

        queue.addRequestFinishedListener(new RequestQueue.RequestFinishedListener<Object>() {
            @Override
            public void onRequestFinished(Request<Object> request) {
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    private String getServerAddressFromSharedPreferences() {
        final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        return preferences.getString(ConfigurationActivity.SERVER_ADDRESS, "");
    }

    @NonNull
    private String getAccountBalanceUrl(String baseUrl) {
        return Uri.parse(baseUrl)
                .buildUpon()
                .appendPath("entries")
                .appendPath("balance")
                .build()
                .toString();
    }

    @NonNull
    private String getEntriesUrl(String baseUrl) {
        return Uri.parse(baseUrl)
                .buildUpon()
                .appendPath("entries")
                .build()
                .toString();
    }

}
