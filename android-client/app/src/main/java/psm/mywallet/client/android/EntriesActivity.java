package psm.mywallet.client.android;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
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
import com.android.volley.toolbox.JsonObjectRequest;
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

    RequestQueue queue;
    private String baseUrl;
    private String balanceUrl;
    private String entriesUrl;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entries);

        checkServerConfiguration();

        setupServerEndpoints();
        queue = Volley.newRequestQueue(this);
        setupToolbar();
        setupHashtagButton();
        setupSettingsButton();
        setupSwipeRefresh();
        setupListView();
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
                Intent intent = new Intent(EntriesActivity.this, HashtagActivity.class);
                startActivity(intent);
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

    private void setupSwipeRefresh() {
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

    private void setupListView() {
        listView = (ListView) findViewById(R.id.entriesListView);
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                final EntryDTO entryDTO = (EntryDTO) ((ListView) parent).getAdapter().getItem(position);

                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case DialogInterface.BUTTON_POSITIVE:
                                removeEntry(entryDTO.getId());
                                dialog.dismiss();
                                break;

                            case DialogInterface.BUTTON_NEGATIVE:
                                dialog.dismiss();
                                break;
                        }
                    }
                };

                new AlertDialog.Builder(EntriesActivity.this)
                        .setMessage("Delete entry?")
                        .setPositiveButton("Yes", dialogClickListener)
                        .setNegativeButton("No", dialogClickListener)
                        .show();

                return false;
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
        StringRequest balanceRequest = new StringRequest(Request.Method.GET, balanceUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                TextView accountBalanceTextView = (TextView) findViewById(R.id.accountBalanceTextView);
                accountBalanceTextView.setText(getString(R.string.entries_title_account_balance) + " " + response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(EntriesActivity.this, "Can not load balance - check your internet connection or server address", Toast.LENGTH_SHORT).show();
            }
        });

        JsonArrayRequest entriesRequest = new JsonArrayRequest(Request.Method.GET, entriesUrl, null, new Response.Listener<JSONArray>() {

            @Override
            public void onResponse(JSONArray response) {
                try {
                    ArrayList<EntryDTO> entries = new ArrayList<>();

                    for (int i = 0; i < response.length(); i++) {
                        JSONObject jsonObject = response.getJSONObject(i);
                        EntryDTO entryDTO = new EntryDTO();

                        long id = jsonObject.getLong("id");
                        entryDTO.setId(id);

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
                Toast.makeText(EntriesActivity.this, "Can not load entries - check your internet connection or server address", Toast.LENGTH_LONG).show();
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

    private void removeEntry(Long id) {
        String removeEntryUrl = getRemoveEntryUrl(baseUrl, id);
        StringRequest removeEntryRequest = new StringRequest(Request.Method.GET, removeEntryUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Toast.makeText(EntriesActivity.this, "Entry removed successfully", Toast.LENGTH_SHORT).show();
                queryEntries();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(EntriesActivity.this, "Can not remove entry - check your internet connection and try again", Toast.LENGTH_SHORT).show();
            }
        });

        queue.add(removeEntryRequest);
    }

    @NonNull
    private String getRemoveEntryUrl(String baseUrl, Long id) {
        return Uri.parse(baseUrl)
                .buildUpon()
                .appendPath("entries")
                .appendPath("delete")
                .appendPath(String.valueOf(id))
                .build()
                .toString();
    }

    @Override
    protected void onResume() {
        super.onResume();
        queryEntries();
    }

}
