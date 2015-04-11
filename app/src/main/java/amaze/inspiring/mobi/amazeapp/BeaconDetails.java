package amaze.inspiring.mobi.amazeapp;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.altbeacon.beacon.Beacon;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class BeaconDetails extends ActionBarActivity {

    protected static final String TAG = "BeaconDetails";
    private static final String serviceUrl = "http://192.168.94.201:8080/amaze-mobile-api/restapi/beacon/identify";
    private Beacon beacon = null;
    private ProgressBar mProgressBar;
    private String jsonResponse = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_beacon_details);
        new MyAsyncTask().execute(getBeaconDataRequest());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_beacon_details, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void postData(String request) {
        HttpClient httpclient = new DefaultHttpClient();
        // specify the URL you want to post to
        HttpPost httppost = new HttpPost(serviceUrl);
        httppost.setHeader("Content-type", "application/json");
        try {
            StringEntity entity = new StringEntity(request);
            httppost.setEntity(entity);
            // send the variable and value, in other words post, to the URL
            HttpResponse response = httpclient.execute(httppost);
            jsonResponse = EntityUtils.toString(response.getEntity());
        } catch (ClientProtocolException e) {
            // process execption
        } catch (IOException e) {
            // process execption
        }
    }


    private String getBeaconDataRequest() {
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            beacon = (Beacon) extras.get("beacon");
            TextView mBeaconHeader = (TextView) this.findViewById(R.id.mBeaconDetailsHeader);
            mBeaconHeader.setText("Beacon ID1: " + beacon.getId1());
        }

        JSONObject request = new JSONObject();

        try {
            request.put("uuid", beacon.getId1());
            request.put("major", beacon.getId2());
            request.put("minor", beacon.getId3());
        } catch (JSONException e) {
            Log.e(TAG, "Error creating json request object.");
        }

        return request.toString();
    }

    private class MyAsyncTask extends AsyncTask<String, Integer, Double>{
        @Override
        protected Double doInBackground(String... params) {
            postData(params[0]);
            return null;
        }

        protected void onPostExecute(Double result){
            JSONObject jsonObject = null;
            String text = null;
            try {
                jsonObject = new JSONObject(jsonResponse);
                text = jsonObject.getString("text");
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

        protected void onProgressUpdate(Integer... progress){

        }
    }


}

