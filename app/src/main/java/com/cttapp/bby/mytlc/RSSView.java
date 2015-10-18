package com.cttapp.bby.mytlc;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.net.http.SslError;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

public class RSSView extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rssview);


        WifiManager wifiManager = (WifiManager) getSystemService(WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        Log.d("SSID", wifiInfo.getSSID());

        if (!wifiInfo.getSSID().equalsIgnoreCase("\"BBYDemo\"") && !wifiInfo.getSSID().equalsIgnoreCase("\"BBYDemoFast\"") && !wifiInfo.getSSID().equalsIgnoreCase("\"iD Tech - Staff\""))
        {
            AlertDialog alertDialog = new AlertDialog.Builder(this).create();
            alertDialog.setTitle("WiFi Error");
            alertDialog.setMessage("To use this feature, you must be connected to BBYDemo or BBYDemoFast");
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            finish();
                        }
                    });
            alertDialog.show();

        }
        else
        {
            WebView webview = (WebView) findViewById(R.id.mainWebView);
            WebSettings webSettings = webview.getSettings();
            webSettings.setJavaScriptEnabled(true);

            webview.setWebViewClient(new MyWebViewClient());
            webview.loadUrl("https://retailapps.bestbuy.com");

        }

    }

    private class MyWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if (Uri.parse(url).getHost().equals("retailapps.bestbuy.com")) {
                return false;
            }
            // Otherwise, the link is not for a page on my site, so launch another Activity that handles URLs
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(intent);
            return true;
        }

        @Override
        public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
            handler.proceed(); // Ignore SSL certificate errors
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_rssview, menu);

        getActionBar().setTitle("Best Buy Inventory");
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.scan_button) {

            IntentIntegrator integrator = new IntentIntegrator(this);
            integrator.initiateScan();

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
        if (scanResult != null) {

            String fullData = scanResult.getContents();
            String important = fullData.substring(20);

            Log.d("BARCODEDATA", "Full Data: " + fullData);
            Log.d("BARCODEDATA", "important: " + fullData.indexOf("http://bby.us/?c=BB0"));

            if(fullData.indexOf("http://bby.us/?c=BB0") >= 0)
            {
                String store = important.substring(0, 4);
                String sku = important.substring(4);

                //[NSString stringWithFormat:@"$(\"#EmailId\").val(\"%@\");$(\"#Password\").val(\"%@\");$('input[type=\"submit\"]').click();", email, password];
                // [NSString stringWithFormat:@"$(\"#SkuForCurrentStore\").val(\"%@\");$(\"#StoreNoForCurrentStore\").val(\"%@\");", [data objectForKey:@"sku"], [data objectForKey:@"store"]];

                String jsScript = "javascript:$(\"#SkuForCurrentStore\").val(\"" + sku + "\");$(\"#StoreNoForCurrentStore\").val(\"" + store + "\");$('#SearchCurrentStore').click();";

                WebView mWebView = (WebView)findViewById(R.id.mainWebView);
                mWebView.loadUrl(jsScript);

            }


        }
    }
}
