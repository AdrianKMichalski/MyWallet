package psm.mywallet.client.android;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import psm.mywallet.api.EntryDTO;

/**
 * @author Adrian Michalski
 */
public class NewEntryActivity extends AppCompatActivity {

    private EditText descriptionEditText;
    private View progressBar;
    private View newEntryForm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_entry);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        descriptionEditText = (EditText) findViewById(R.id.descriptionEditText);

        Button addEntryButton = (Button) findViewById(R.id.addEntryButton);
        addEntryButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptToAddEntry();
            }
        });

        newEntryForm = findViewById(R.id.newEntryForm);
        progressBar = findViewById(R.id.addingEntryProgress);
    }

    private void attemptToAddEntry() {
        String description = descriptionEditText.getText().toString();

        List<EntryDTO> parsedEntries = new ListEntryParser().parse(description);
        RequestQueue queue = Volley.newRequestQueue(this);

        String baseUrl = getServerAddressFromSharedPreferences();
        String entriesUrl = getEntriesUrl(baseUrl);

        showProgress(true);
        for (EntryDTO entry : parsedEntries) {
            JSONObject jsonBody = new JSONObject();
            try {
                jsonBody.put("value", entry.getValue());
                jsonBody.put("description", entry.getDescription());
                jsonBody.put("tags", new JSONArray(entry.getTags()));
            } catch (JSONException e) {
                e.printStackTrace();
            }

            JsonObjectRequest newEntryRequest = new JsonObjectRequest(entriesUrl, jsonBody, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                }
            });

            queue.add(newEntryRequest);
        }
        queue.addRequestFinishedListener(new RequestQueue.RequestFinishedListener<Object>() {
            @Override
            public void onRequestFinished(Request<Object> request) {
                showProgress(false);

                if (!request.hasHadResponseDelivered()) {
                    Toast.makeText(NewEntryActivity.this, "Could not add entry - check your Internet connection", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(NewEntryActivity.this, "Entry saved", Toast.LENGTH_SHORT).show();
                    NavUtils.navigateUpFromSameTask(NewEntryActivity.this);
                }
            }
        });
    }

    private String getServerAddressFromSharedPreferences() {
        final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        return preferences.getString(ConfigurationActivity.SERVER_ADDRESS, "");
    }

    @NonNull
    private String getEntriesUrl(String baseUrl) {
        return Uri.parse(baseUrl)
                .buildUpon()
                .appendPath("entries")
                .build()
                .toString();
    }

    private void showProgress(final boolean show) {
        int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

        newEntryForm.setVisibility(show ? View.GONE : View.VISIBLE);
        newEntryForm.animate().setDuration(shortAnimTime).alpha(
                show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                newEntryForm.setVisibility(show ? View.GONE : View.VISIBLE);
            }
        });

        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        progressBar.animate().setDuration(shortAnimTime).alpha(
                show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
            }
        });
    }


}

