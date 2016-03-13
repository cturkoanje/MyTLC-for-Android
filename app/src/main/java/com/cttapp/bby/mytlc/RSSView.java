package com.cttapp.bby.mytlc;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.net.http.SslError;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.cttapp.bby.mytlc.layer8apps.Preferences;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

public class RSSView extends Activity {

    Preferences pf;
    String scanData;
    ProgressDialog ringProgressDialog;

    public static final int DIALOG_DOWNLOAD_PROGRESS = 0;
    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rssview);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);

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
            //webview.loadUrl("https://retailapps.bestbuy.com");
            webview.loadUrl("http://beta.ctthosting.com/rss/");

        }

        pf = new Preferences(this);

    }

    private void getSKUForUPC(String upc) {
        scanData = upc;
        //showDialog(DIALOG_DOWNLOAD_PROGRESS);
        new GetBBYAPIData().execute();
    }

    private class MyWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if (Uri.parse(url).getHost().equals("retailapps.bestbuy.com")) {
                return false;
            }
            if (Uri.parse(url).getHost().equals("beta.ctthosting.com")) {
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

        @Override
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            Toast.makeText(getApplicationContext(), description, Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            String email = pf.getEmail();

            if(email == null || email.length() <= 1)
            {
                AlertDialog.Builder builder = new AlertDialog.Builder(RSSView.this);
                builder.setTitle("Enter Email");
                builder.setMessage("Please enter your Best Buy email address. It should be something like\nFirstName.LastName@BestBuy.com");

                final EditText input = new EditText(getApplicationContext());
                input.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);

                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT);

                lp.setMargins(30,0,30,0);
                input.setLayoutParams(lp);

                builder.setView(input);

                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String tmpemail = input.getText().toString();
                        pf.setEmail(tmpemail);

                        String password = pf.getPassword();
                        String jsScript = "javascript:" + "if($(\"#EmailId\")){$(\"#EmailId\").val(\"" + tmpemail + "\");$(\"#Password\").val(\"" + password + "\");$('input[type=\"submit\"]').click();}";
                        Log.d("JS",jsScript);
                        WebView mWebView = (WebView)findViewById(R.id.mainWebView);
                        mWebView.loadUrl(jsScript);
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                builder.show();
            }
            else {
                String password = pf.getPassword();
                String jsScript = "javascript:" + "if($(\"#EmailId\")){$(\"#EmailId\").val(\"" + email + "\");$(\"#Password\").val(\"" + password + "\");$('input[type=\"submit\"]').click();}";
                Log.d("JS", jsScript);
                WebView mWebView = (WebView) findViewById(R.id.mainWebView);
                mWebView.loadUrl(jsScript);
            }
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

            if(fullData == null)
                return;


            Log.d("BARCODEDATA", "Full Data: " + fullData);
            Log.d("BARCODEDATA", "important: " + fullData.indexOf("http://bby.us/?c=BB0"));

            if(fullData.indexOf("http://bby.us/?c=BB0") >= 0)
            {
                String important = fullData.substring(20);
                String store = important.substring(0, 4);
                String sku = important.substring(4);

                //[NSString stringWithFormat:@"$(\"#EmailId\").val(\"%@\");$(\"#Password\").val(\"%@\");$('input[type=\"submit\"]').click();", email, password];
                // [NSString stringWithFormat:@"$(\"#SkuForCurrentStore\").val(\"%@\");$(\"#StoreNoForCurrentStore\").val(\"%@\");", [data objectForKey:@"sku"], [data objectForKey:@"store"]];

                String jsScript = "javascript:$(\"#SkuForCurrentStore\").val(\"" + sku + "\");$(\"#StoreNoForCurrentStore\").val(\"" + store + "\");$('#SearchCurrentStore').click();";

                WebView mWebView = (WebView)findViewById(R.id.mainWebView);
                mWebView.loadUrl(jsScript);

            }
            else if(fullData.length() == 12) {
                getSKUForUPC(fullData);
                launchRingDialog();
            }


        }
    }


    public void launchRingDialog() {
        ringProgressDialog = ProgressDialog.show(RSSView.this, "Please wait ...",	"Fetching Product Info ...", true);
        ringProgressDialog.setCancelable(false);
    }


    private class GetBBYAPIData extends
            AsyncTask<Void, Void, Void> {

        private String result;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... arg0)
        {
            // create a HttpURLConnection
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;


            try {
                URL url = new URL("https://api.bestbuy.com/v1/products(upc=" + scanData + ")?apiKey=gvkaj3zvkmpqbdhy3cghv7b4&sort=upc.asc&show=upc,sku,name&format=json");
                urlConnection = (HttpURLConnection)url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                InputStream inStream = urlConnection.getInputStream();
                // if no input is received, then return from method
                if (inStream == null)
                {
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inStream));
                result = "";
                String str;
                while ((str = reader.readLine()) != null)
                {
                    result += str;
                }
                Log.d("RESULT", "The string is " + result);
            } catch (Exception e) {
                Log.i("HttpAsyncTask", "EXCEPTION: " +
                        e.getMessage());
            } finally {

            }
            return null;
        }

        protected void onProgressUpdate(Integer... progress) {

        }

        @Override
        protected void onPostExecute(Void r)
        {
            super.onPostExecute(r);

            if(ringProgressDialog != null)
                ringProgressDialog.dismiss();

            if (result != null)
            {
                try {
                    JSONObject jsonObj = new JSONObject(result);

                    Log.d("JSONRETURN", "JSON Object");
                    Log.d("JSONRETURN", jsonObj.toString());

                    JSONArray products = jsonObj.getJSONArray("products");
                    if(products.length() >= 1)
                    {
                        JSONObject product = products.getJSONObject(0);
                        String sku = product.getString("sku");
                        String store = pf.getLocation();

                        Toast.makeText(getApplicationContext(), product.getString("name"), Toast.LENGTH_SHORT).show();

                        String jsScript = "javascript:$(\"#SkuForCurrentStore\").val(\"" + sku + "\");$(\"#StoreNoForCurrentStore\").val(\"" + store + "\");$('#SearchCurrentStore').click();";
                        WebView mWebView = (WebView)findViewById(R.id.mainWebView);
                        mWebView.loadUrl(jsScript);

                    }
                    else {
                        Toast.makeText(getApplicationContext(), "UPC Code could not be found on BestBuy.com.", Toast.LENGTH_SHORT).show();
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            else {
                Log.d("DATA", "No data received");
            }

        }
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case DIALOG_DOWNLOAD_PROGRESS:
                mProgressDialog = new ProgressDialog(this);
                mProgressDialog.setMessage("Loading product data");
                mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                mProgressDialog.setCancelable(false);
                mProgressDialog.setMax(100);
                mProgressDialog.show();
                return mProgressDialog;
            default:
                return null;
        }
    }
}
