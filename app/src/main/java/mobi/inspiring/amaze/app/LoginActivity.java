package mobi.inspiring.amaze.app;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;


public class LoginActivity extends Activity {

    protected static final String TAG = "BeaconDetails";
    private static final String loginURL  = "http://192.168.88.254:9090/amaze-mobile-api/restapi/account/login";
    private static final String signupURL = "http://192.168.88.254:9090/amaze-mobile-api/restapi/account/signin";

    private String jsonResponse = null;
    private String login = null;
    private String name = null;
    private String token = null;

    EditText mEtLogin = null;
    EditText mEtPassword = null;
    Button mBtLogin = null;
    Button mBtSignIn = null;

    CallbackManager callbackManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mEtLogin = (EditText) findViewById(R.id.mEtLogin);
        mEtPassword = (EditText) findViewById(R.id.mEtPassword);
        mBtLogin = (Button) findViewById(R.id.mBtLogin);
        mBtSignIn = (Button) findViewById(R.id.mBtSignIn);

        mBtLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onLogin();
            }
        });

        mBtSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSignIn();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
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

    public void onSignIn() {
        String login = mEtLogin.getText().toString();
        String password = mEtPassword.getText().toString();

        if (isLoginAndPasswordEmpty(login, password)) {
            Toast.makeText(LoginActivity.this, "Preencha todos os campos.", Toast.LENGTH_SHORT).show();
        }
        String[] params = {signupURL, login, password};
        new AccountAsyncTask().execute(params);

    }

    public void onLogin() {
        String login = mEtLogin.getText().toString();
        String password = mEtPassword.getText().toString();

        if (isLoginAndPasswordEmpty(login, password)) {
            Toast.makeText(LoginActivity.this, "Preencha todos os campos.", Toast.LENGTH_SHORT).show();
        }
        String[] params = {loginURL, login, password};
        new AccountAsyncTask().execute(params);
    }

    private boolean isLoginAndPasswordEmpty(String login, String password) {
        boolean response = false;
        if ((login == null || login.isEmpty()) || password == null || password.isEmpty()) {
            response = true;
        }

        return response;
    }

    private class AccountAsyncTask extends AsyncTask<String, Integer, Double> {
        @Override
        protected Double doInBackground(String... params) {
            postData(params);
            return null;
        }

        protected void onPostExecute(Double result){
            JSONObject jsonObject = null;
            String text = null;
            try {
                jsonObject = new JSONObject(jsonResponse);
                if (jsonResponse != null) {
                    renderJSONResponseToActivity(jsonObject);
                }
            } catch (JSONException e) {
                Log.e(TAG, "Error executing onPostExecute.");
            }

        }

        protected void onProgressUpdate(Integer... progress){

        }
    }

    public void postData(String[] request) {
        HttpClient httpclient = new DefaultHttpClient();
        // specify the URL you want to post to
        HttpPost httppost = new HttpPost(request[0]);
        httppost.setHeader("Content-type", "application/json");
        try {
            JSONObject jsonRequest = new JSONObject();
            jsonRequest.put("login", request[1]);
            jsonRequest.put("password", request[2]);
            StringEntity entity = new StringEntity(jsonRequest.toString());
            httppost.setEntity(entity);
            // send the variable and value, in other words post, to the URL
            HttpResponse response = httpclient.execute(httppost);
            jsonResponse = EntityUtils.toString(response.getEntity());
        } catch (ClientProtocolException e) {
            // process execption
        } catch (IOException e) {
            Toast.makeText(LoginActivity.this, "Não foi possível acessar o servidor remoto.", Toast.LENGTH_SHORT).show();
            // process execption
        } catch (JSONException e) {

        }
    }

    public void renderJSONResponseToActivity(JSONObject jsonResponse) {
        String status = null;
        String login = null;
        try {
            status = jsonResponse.getString("responseCode");
            login = jsonResponse.getJSONObject("profile").getString("login");
        } catch (JSONException e) {
            Log.e(TAG, "Error reading JSONResponse in method renderJSONResponseToActivity");
        }

        if (status == null) {
            Toast.makeText(LoginActivity.this, "Um erro aconteceu. Favor verificar nos logs do app.", Toast.LENGTH_SHORT).show();
        } else if (status.equals("ACC003")) {
            //Login successful -- proceed to main screen
            Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
            intent.putExtra("login", login) ;
            LoginActivity.this.startActivity(intent);
        } else if (status.equals("ACC004")) {
            //Login successful -- proceed to main screen
            Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
            intent.putExtra("login", login);
            LoginActivity.this.startActivity(intent);
            Toast.makeText(LoginActivity.this, "Seja bem vindo ao Zoone!", Toast.LENGTH_SHORT).show();
        } else if (status.equals("ACC001")){
            Toast.makeText(LoginActivity.this, "Esse login já existe no app. Insira outro por favor.", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(LoginActivity.this, "Usuario ou senha incorretos.", Toast.LENGTH_SHORT).show();
        }

    }

    private void saveLogin() {
        SharedPreferences sharedPref = this.getSharedPreferences("zoone_login_preferences",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("login", login);
        editor.putBoolean("isLogged", true);
        editor.commit();
    }

}

