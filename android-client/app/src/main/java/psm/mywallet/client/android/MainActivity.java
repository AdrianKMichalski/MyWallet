package psm.mywallet.client.android;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
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

    public static final String SERVER_ADDRESS_MESSAGE = "psm.mywallet.client.android.MainActivity.SERVER_ADDRESS_MESSAGE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final SharedPreferences preferences = getPreferences(Context.MODE_PRIVATE);
        String serverAddress = preferences.getString(getString(R.string.preferences_address), "");

        if (!serverAddress.isEmpty()) {
            startEntriesActivity(serverAddress);
        } else {
            setContentView(R.layout.activity_main);

            Button button = (Button) findViewById(R.id.enterAddressButton);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    EditText editText = (EditText) findViewById(R.id.enterAddressEditText);
                    String serverUrl = editText.getText().toString();

                    if (URLUtil.isValidUrl(serverUrl)) {
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putString(SERVER_ADDRESS_MESSAGE, serverUrl);
                        editor.apply();

                        startEntriesActivity(serverUrl);
                    } else {
                        Toast.makeText(MainActivity.this, "Wrong MyWallet server address", Toast.LENGTH_LONG).show();
                    }
                }
            });

        }
    }

    private void startEntriesActivity(String serverAddress) {
        Intent intent = new Intent(this, EntriesActivity.class);
        intent.putExtra(SERVER_ADDRESS_MESSAGE, serverAddress);
        startActivity(intent);
    }

}
