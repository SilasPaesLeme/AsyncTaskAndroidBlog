package br.com.guisj.asynctask;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class MainActivity extends ActionBarActivity {

    private TextView tvEndereco;
    private LinearLayout baseProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvEndereco = (TextView)findViewById(R.id.tvEndereco);
        baseProgressBar = (LinearLayout)findViewById(R.id.baseProgressBar);

        ((Button)findViewById(R.id.btnBuscar)).setOnClickListener(onClickBuscarCep());

    }

    private View.OnClickListener onClickBuscarCep() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String cep = ((EditText)findViewById(R.id.etCep)).getText().toString();
                if(cep != null){
                    new ConsultaCEPCorreios().execute(cep);
                }
            }
        };
    }

    private class ConsultaCEPCorreios extends AsyncTask<String, Integer, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            baseProgressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(String... params) {
            String result = "";
            String url = "http://cep.republicavirtual.com.br/web_cep.php?formato=json&cep="+params[0];
            HttpClient httpClient = new DefaultHttpClient();
            try {
                HttpResponse response = httpClient.execute(new HttpGet(url));
                result += getStringFromInputStream(response.getEntity().getContent());
            } catch (IOException e) {
                Log.e("TAG_ASYNC_TASK", e.getMessage());
                baseProgressBar.setVisibility(View.INVISIBLE);
            }
            return result;
        }

        @Override
        protected void onPostExecute(String s) {
            baseProgressBar.setVisibility(View.INVISIBLE);
            tvEndereco.setText(s);
        }

        private String getStringFromInputStream(InputStream is) {
            BufferedReader br = null;
            StringBuilder sb = new StringBuilder();
            String line;
            try {
                br = new BufferedReader(new InputStreamReader(is));
                while ((line = br.readLine()) != null) {
                    sb.append(line);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (br != null) {
                    try {
                        br.close();
                    } catch (IOException e) {
                        Log.e("ASYNC_TASK", e.getMessage());
                    }
                }
            }
            return sb.toString();
        }
    }
}
