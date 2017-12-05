package org.mikko.unrooter;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebView;

/** Shows a WebView that can show license documentation.
 */
public class LicensesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_licenses);

        WebView licensesWebView = findViewById(R.id.licensesWebView);

        licensesWebView.loadUrl("file:///android_asset/licenses.html");
    }
}
