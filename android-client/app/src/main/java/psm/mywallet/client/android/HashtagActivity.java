package psm.mywallet.client.android;

import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.common.collect.ImmutableList;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

public class HashtagActivity extends AppCompatActivity {

    private RequestQueue queue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hashtag);

        queue = Volley.newRequestQueue(this);

        final AutoCompleteTextView searchTextView = (AutoCompleteTextView) findViewById(R.id.hashtagSearchTextView);
        queryTagsForAutocompletion(searchTextView);

        setupSearchButton(searchTextView);

    }

    private void queryTagsForAutocompletion(final AutoCompleteTextView searchTextView) {
        String baseUrl = getServerAddressFromSharedPreferences();
        String tagsUrl = getTagsUrl(baseUrl);

        JsonArrayRequest tagNamesRequest = new JsonArrayRequest(Request.Method.GET, tagsUrl, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                ImmutableList.Builder<String> tags = ImmutableList.builder();
                try {
                    for (int i = 0; i < response.length(); i++) {
                        tags.add(response.getString(i));
                    }
                } catch (Exception e) {

                }

                ArrayAdapter<String> adapter = new ArrayAdapter<>(HashtagActivity.this,
                        android.R.layout.simple_dropdown_item_1line,
                        tags.build());

                searchTextView.setAdapter(adapter);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // TODO: handle loading errors
            }
        });

        this.queue.add(tagNamesRequest);

        this.queue.addRequestFinishedListener(new RequestQueue.RequestFinishedListener<Object>() {
            @Override
            public void onRequestFinished(Request<Object> request) {
            }
        });
    }

    private void setupSearchButton(final TextView searchTextView) {
        Button searchButton = (Button) findViewById(R.id.hashtagSearchButton);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String tagName = searchTextView.getText().toString();
                queryForTagSum(tagName);
                queryForLatestEntriesByTag(tagName);
            }
        });
    }

    private String getServerAddressFromSharedPreferences() {
        final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        return preferences.getString(ConfigurationActivity.SERVER_ADDRESS, "");
    }

    @NonNull
    private String getTagsUrl(String baseUrl) {
        return Uri.parse(baseUrl)
                .buildUpon()
                .appendPath("tags")
                .build()
                .toString();
    }

    private void queryForTagSum(final String tagName) {
        String baseUrl = getServerAddressFromSharedPreferences();
        String tagSumUrl = getTagSumUrl(baseUrl, tagName);

        StringRequest balanceRequest = new StringRequest(Request.Method.GET, tagSumUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                TextView sumForTagTextView = (TextView) findViewById(R.id.sumForTagTextView);

                if (response.isEmpty()) {
                    sumForTagTextView.setText(response);
                    sumForTagTextView.setVisibility(View.INVISIBLE);
                } else {
                    sumForTagTextView.setText(getString(R.string.sum_for_tag) + " #" + tagName + ": " + response);
                    sumForTagTextView.setVisibility(View.VISIBLE);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // TODO: handle loading errors
            }
        });

        this.queue.add(balanceRequest);

    }

    private void queryForLatestEntriesByTag(String tagName) {
        String baseUrl = getServerAddressFromSharedPreferences();
        String entriesByTagUrl = getLatestEntriesByTagUrl(baseUrl, tagName);

        JsonArrayRequest tagNamesRequest = new JsonArrayRequest(Request.Method.GET, entriesByTagUrl, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                try {
                    String result = "";
                    int limit = (response.length() > 10) ? 10 : response.length();
                    for (int i = 0; i < limit; i++) {
                        JSONObject jsonObject = response.getJSONObject(i);

                        String description = jsonObject.getString("description");

                        String value = jsonObject.getString("value");

                        Date date = new Date(jsonObject.getLong("createDate"));

                        SimpleDateFormat sdfDate = new SimpleDateFormat("dd.MM.yyyy HH:mm");
                        String dateFormatted = sdfDate.format(date);

                        result += dateFormatted + ": " + description + " (" + value + ")\n";
                    }

                    TextView lastTagEntriesTextView = (TextView) findViewById(R.id.lastTagEntriesTextView);

                    if (result.isEmpty()) {
                        lastTagEntriesTextView.setText(result);
                        lastTagEntriesTextView.setVisibility(View.INVISIBLE);
                    } else {
                        lastTagEntriesTextView.setText(getString(R.string.hashtag_latest_results) + "\n" + result);
                        lastTagEntriesTextView.setVisibility(View.VISIBLE);
                    }

                } catch (Exception e) {

                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // TODO: handle loading errors
            }
        });

        this.queue.add(tagNamesRequest);
    }

    @NonNull
    private String getTagSumUrl(String baseUrl, String tagName) {
        return Uri.parse(baseUrl)
                .buildUpon()
                .appendPath("entries")
                .appendPath("tag")
                .appendPath(tagName)
                .appendPath("sum")
                .build()
                .toString();
    }

    @NonNull
    private String getLatestEntriesByTagUrl(String baseUrl, String tagName) {
        return Uri.parse(baseUrl)
                .buildUpon()
                .appendPath("entries")
                .appendPath("tag")
                .appendPath(tagName)
                .build()
                .toString();
    }

}
