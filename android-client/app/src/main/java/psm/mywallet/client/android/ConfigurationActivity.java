package psm.mywallet.client.android;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.webkit.URLUtil;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

/**
 * @author Adrian Michalski
 */
public class ConfigurationActivity extends AppCompatActivity {

    public static final String SERVER_ADDRESS = "psm.mywallet.client.android.ConfigurationActivity.SERVER_ADDRESS";
    public static final String SERVER_ADDRESS_MESSAGE = "psm.mywallet.client.android.ConfigurationActivity.SERVER_ADDRESS_MESSAGE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuration);

        String serverAddress = getServerAddressFromPreferences();

        final EditText editText = (EditText) findViewById(R.id.enterAddressEditText);
        if (!serverAddress.isEmpty()) {
            editText.setText(serverAddress);
        }

        Button button = (Button) findViewById(R.id.enterAddressButton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String serverUrl = editText.getText().toString();

                if (URLUtil.isValidUrl(serverUrl)) {
                    saveServerAddressToPreferences(serverUrl);
                    NavUtils.navigateUpFromSameTask(ConfigurationActivity.this);
                } else {
                    Toast.makeText(ConfigurationActivity.this, R.string.wrong_server_address, Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private String getServerAddressFromPreferences() {
        final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        return preferences.getString(SERVER_ADDRESS, "");
    }

    private void saveServerAddressToPreferences(String serverUrl) {
        final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(SERVER_ADDRESS, serverUrl);
        editor.apply();
    }

}
