package psm.mywallet.client.android;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.webkit.URLUtil;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

/**
 * @author Adrian Michalski
 */
public class MainActivity extends AppCompatActivity {

    public static final String SERVER_ADDRESS = "psm.mywallet.client.android.MainActivity.SERVER_ADDRESS";
    public static final String SERVER_ADDRESS_MESSAGE = "psm.mywallet.client.android.MainActivity.SERVER_ADDRESS_MESSAGE";
    public static final String SHARED_PREFERENCES_NAME = "psm.mywallet.client.android.MyWalletSharedPreferences";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
                    startEntriesActivity();
                } else {
                    Toast.makeText(MainActivity.this, R.string.wrong_server_address, Toast.LENGTH_LONG).show();
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

    private void startEntriesActivity() {
        Intent intent = new Intent(this, EntriesActivity.class);
        startActivity(intent);
    }

}
