package com.chimtek.app;

import android.content.Intent;
import android.os.StrictMode;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class MainActivity extends ActionBarActivity {
    List<Map<String, String>> jobsList = new ArrayList<Map<String,String>>();
    JSONArray jArray;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        StrictMode.enableDefaults();
        //getData();
        final String dataResult = pullData();

        JSONArray dataResultToJSON;
        try {
            dataResultToJSON = new JSONArray(dataResult);

            for(int i=0; i<dataResultToJSON.length();i++){
                JSONObject JSONdata = dataResultToJSON.getJSONObject(i);
                jobsList.add(createJob("job" , JSONdata.getString("addressStreet")));
            }
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
        Log.e("Log_tag", "dataResult: " + dataResult);
        ListView lv = (ListView) findViewById(R.id.jobList);
        SimpleAdapter simpleAdpt = new SimpleAdapter(this, jobsList, android.R.layout.simple_list_item_1, new String[] {"job"}, new int[] {android.R.id.text1});

        lv.setAdapter(simpleAdpt);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

        public void onItemClick(AdapterView<?> parentAdapter, View view, int position, long id) {
            // We know the View is a TextView so we can cast it
            TextView clickedView = (TextView) view;
            Intent myIntent = new Intent(MainActivity.this, JobActivity.class);
            int jobID = (int) id;
            Log.e("Log_tag", "Result before put extra: " + dataResult);

            myIntent.putExtra("result" , dataResult);
            Log.e("Log_tag", "ID: " + id);
            myIntent.putExtra("ID" , id);
            MainActivity.this.startActivity(myIntent);
            Toast.makeText(MainActivity.this, "Item with id [" + id + "] - Position [" + position + "] - job [" + clickedView.getText() + "]", Toast.LENGTH_SHORT).show();
        }
        });
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }
    }
    public String pullData(){

        String result = "";
        //JSONArray = new JSONArray(something);
        InputStream isr = null;
        String username = "jacbwest";
        Log.e("Log_tag", "Logging Test");
        try{
            Log.e("Log_tag", "Trying to get");
            HttpClient httpclient = new DefaultHttpClient();
            String url = "http://chimtek-chimtech.rhcloud.com/records/jsonJobs/";
            HttpGet httpget = new HttpGet(url);
            HttpResponse response = httpclient.execute(httpget);
            HttpEntity entity = response.getEntity();
            isr = entity.getContent();
        }
        catch(Exception e){
            Log.e("log_tag", "Error in http connection " + e.toString());
        }
        try{
            Log.e("Log_tag", "Trying to result");
            BufferedReader reader = new BufferedReader(new InputStreamReader(isr,"iso-8859-1"),8);
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
            isr.close();
            result=sb.toString();
            //JSONArray somethingelse = new JSONArray(result);
            Log.e("Log_tag", "showing result: " + result);
        }
        catch(Exception e){
            Log.e("log_tag", "Error  converting result " +e.toString());
        }

        return result;
    }


    private HashMap<String, String> createJob(String key, String name) {
        HashMap<String, String> job = new HashMap<String, String>();
        job.put(key, name);

        return job;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            return rootView;
        }
    }



}
