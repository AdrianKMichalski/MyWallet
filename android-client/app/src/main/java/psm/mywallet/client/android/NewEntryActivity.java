package psm.mywallet.client.android;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;

import android.os.Build;
import android.os.Bundle;
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

/**
 * @author Adrian Michalski
 */
public class NewEntryActivity extends AppCompatActivity {

    private EditText descriptionEditText;
    private EditText valueEditText;
    private View progressBar;
    private View newEntryForm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_entry);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        descriptionEditText = (EditText) findViewById(R.id.descriptionEditText);
        valueEditText = (EditText) findViewById(R.id.valueEditText);

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
        String value = valueEditText.getText().toString();

        showProgress(true);

        RequestQueue queue = Volley.newRequestQueue(this);
        String entriesUrl = "http://localhost/mywallet/entries"; // TODO: use URL from settings

        JSONObject jsonBody = new JSONObject();
        try {
            JSONArray tagsArray = new JSONArray(); // TODO: parse tags

            jsonBody.put("value", Double.parseDouble(value));
            jsonBody.put("description", description);
            jsonBody.put("tags", tagsArray);
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
        queue.addRequestFinishedListener(new RequestQueue.RequestFinishedListener<Object>() {
            @Override
            public void onRequestFinished(Request<Object> request) {
                showProgress(false);

                if (!request.hasHadResponseDelivered()) {
                    Toast.makeText(NewEntryActivity.this, "Could not add entry - check your Internet connection", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(NewEntryActivity.this, "Entry saved", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(NewEntryActivity.this, EntriesActivity.class);
                    startActivity(intent);
                }
            }
        });


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

