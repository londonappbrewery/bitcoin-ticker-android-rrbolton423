package com.londonappbrewery.bitcointicker;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class MainActivity extends AppCompatActivity {

    // Constants:
    // TODO: Create the base URL
    private final String BASE_URL = "https://apiv2.bitcoinaverage.com/indices/global/ticker/";

    // Member Variables:
    TextView mPriceTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mPriceTextView = (TextView) findViewById(R.id.priceLabel);
        Spinner spinner = (Spinner) findViewById(R.id.currency_spinner);

        // Create an ArrayAdapter using the String array and a spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.currency_array, R.layout.spinner_item);

        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);

        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);

        // TODO: Set an OnItemSelected listener on the spinner
        // The Spinner reports back on two events
        // 1. If nothing was selected
        // 2. If an item was selected
        spinner.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                // The "onItemSelected()" method is triggered when something is selected on the Spinner
                Log.d("Bitcoin", "" + parentView.getItemAtPosition(position)); // Print the chosen currency

                String chosenCurrency = (String) parentView.getItemAtPosition(position); // Put the currency into a String called "chosenCurrency"
                String toBeParsed = "BTC" + chosenCurrency; // Append the "chosenCurrency" String variable to the "toBeParsed" String variable

                // Call "letsDoSomeNetWorking()" in onItemSelected() and pass in the final URL that includes the user's chosenCurrency (toBeParsed)
                letsDoSomeNetworking(BASE_URL + toBeParsed);
                Log.d("Bitcoin", "" + BASE_URL + toBeParsed);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // The "onNothingSelected()" method is triggered when nothing is selected on the Spinner
                Log.d("Bitcoin", "Nothing selected");
            }

        });


    }

    // TODO: complete the letsDoSomeNetworking() method
    private void letsDoSomeNetworking(String url) {

        /* Create a AsyncHttpClient object to make the API call.
         This Class is included in the library we added in the Gradle dependency*/
        AsyncHttpClient client = new AsyncHttpClient();

        // Supply the parameters needed for the API call (the url with the chosen currency appended to the end)
        client.get(url, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                // the "onSuccess()" method is called when response HTTP status is "200 OK"
                Log.d("Bitcoin", "JSON: " + response.toString());

                String mPrice = null; // Create a temporary variable to hold the price of the given currency
                try { // Try to...
                    mPrice = response.getString("ask"); // Get the String value of the the "ask"
                } catch (JSONException e) { // Catch a JSON exception if there is a problem parsing it
                    e.printStackTrace(); // Print the error
                }

                updateUI(mPrice); // Pass the "mPrice" String variable as a parameter to the "updateUI()" method

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable e, JSONObject response) {
                // The "onFailure()" method is called when response HTTP status is "4XX" (eg. 401, 403, 404)
                Log.d("Bitcoin", "Request fail! Status code: " + statusCode);
                Log.d("Bitcoin", "Fail response: " + response);
                Log.e("ERROR", e.toString());
                Toast.makeText(MainActivity.this, "Request Failed", Toast.LENGTH_SHORT).show(); // Create Toast message
            }
        });
    }

    private void updateUI(String price) {
        Log.d("Clima", "updateUI() callback received");
        // The "updateUI()" method takes care of updating all of the views on screen

        // Update the "mPriceTextView" with the currency price
        mPriceTextView.setText(price);

    }


}
