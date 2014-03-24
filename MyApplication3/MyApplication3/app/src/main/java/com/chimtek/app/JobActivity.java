package com.chimtek.app;


import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.TextView;

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
import java.util.Iterator;
import java.util.List;

public class JobActivity extends Activity {

    ExpandableListAdapter listAdapter;
    ExpandableListView expListView;
    List<String> listDataHeader;
    HashMap<String, List<String>> listDataChild;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_job);
        listDataHeader = new ArrayList<String>();
        listDataChild = new HashMap<String, List<String>>();

        expListView = (ExpandableListView) findViewById(R.id.expandableListView);
        // preparing list data
        prepareListData();



        listAdapter = new ExpandableListAdapter(this, listDataHeader, listDataChild);

        // setting list adapter
        expListView.setAdapter(listAdapter);

        expListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener(){

            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                String textValue = listDataChild.get(listDataHeader.get(groupPosition)).get(childPosition);
                String intType = "";
                String intVal = "";
                if (textValue.contains("Primary Phone")){
                    intVal= textValue.substring(14);
                    Log.e("TAg", "phone number to dialer" + intVal);
                    intType = "p";
                }
                else if (textValue.contains("Alt Phone")){
                    intVal = textValue.substring(9);
                    Log.e("TAg", "phone number to dialer" + intVal);
                    intType = "p";
                }
                else if (textValue.contains("Primary Email")){
                    intVal = textValue.substring(14);
                    Log.e("TAg", "email to gmail" + intVal);
                    intType = "e";
                }
                else if (textValue.contains("Alt Email")){
                    intVal = textValue.substring(9);
                    Log.e("TAg", "email to gmail" + intVal);
                    intType = "e";
                }
                else if (textValue.contains("Location")){
                    intVal = textValue.substring(9);
                    Log.e("TAg", "address to map" + intVal);
                    intType = "m";
                }
                else if (textValue.contains("form")){
                    intVal = textValue.substring(0,1);
                    intType = "k";
                }
                else{
                    Log.e("TAg", "Well shit!");
                }

                if (intType == "p"){
                    Intent intent = new Intent(Intent.ACTION_DIAL);
                    intent.setData(Uri.parse("tel:" + intVal));
                    startActivity(intent);
                }
                else if (intType == "e"){
                    Intent intent = new Intent(Intent.CATEGORY_APP_EMAIL);
                    //intent.putExtra(Intent.EXTRA_EMAIL, intVal);
                    //intent.setData(Intent.EXTRA_EMAIL, intVal);
                    startActivity(intent);
                }
                else if (intType == "k"){
                    Intent intent = new Intent(JobActivity.this, FormActivity.class);
                    Log.e("Tag", "intVal: " + intVal);
                    intent.putExtra("ID" , intVal);
                    startActivity(intent);

                }
                else if (intType == "m"){
                    Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                            Uri.parse("http://maps.google.com/maps?saddr=" + intVal));
                    startActivity(intent);

                }
                return false;
            }

        });

    }

    private void prepareListData() {

        // Adding child data
        listDataHeader.add("Job Details");
        listDataHeader.add("Contact Customer");
        listDataHeader.add("Work History");
        listDataHeader.add("Job Forms");
        Log.e("Log_tag", "Pulling form data");
        String formData = pullData();
        Bundle data = getIntent().getExtras();
        final String result = (String) data.get("result");
        Long ID = (Long) data.get("ID");
        final Integer jobId = ID.intValue();
        //Log.e("Log_tag", "Result of job activity: " + ID);
        try {
            JSONArray dataResult = new JSONArray(result);
            JSONObject curJob = dataResult.getJSONObject(jobId);
            String Name = curJob.getString("person");

            List<String> JD = new ArrayList<String>();
            List<String> CC = new ArrayList<String>();
            List<String> WH = new ArrayList<String>();
            WH.add("No Work History");
            try{
                String address = curJob.getString("address");
                String addressStreet = curJob.getString("addressStreet");
                String addressCity = curJob.getString("addressCity");
                String dateTime = curJob.getString("time");
                JD.add("Location: " + addressStreet + "\n\t" + addressCity);
                JD.add("Time: " + dateTime);
                final String mapAddress = address;
                mapAddress.replace(" ", "+");
                Log.e("map", "Should have plus signs: " + mapAddress);

            }
            catch (JSONException e) {
                JD.add("No Address");
            }
            try{
                String pPhone = curJob.getString("Pphone");
                CC.add("Primary Phone: " + pPhone);
            }
            catch (JSONException e) {
                CC.add("No Phone Number");
            }
            try{
                String aPhone = curJob.getString("Aphone");
                CC.add("Alt Phone: " + aPhone);
            }
            catch (JSONException e) {
                CC.add("No Phone Number");
            }
            try{
                String pEmail = curJob.getString("Pemail");
                CC.add("Primary Email: " + pEmail);
            }
            catch (JSONException e) {
                CC.add("No Email");
            }
            try{
                String aEmail = curJob.getString("Aemail");
                CC.add("Alt Email: " + aEmail);
            }
            catch (JSONException e) {
                CC.add("No Email");
            }
        listDataChild.put(listDataHeader.get(0), JD);
        listDataChild.put(listDataHeader.get(1), CC);
        listDataChild.put(listDataHeader.get(2), WH);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.e("Log_tag", "Result of job activity555555: " + result);

        try {
            Log.e("Log_tag", "Trying to get the keys");
            JSONArray dataResult = new JSONArray(formData);
            List<String> JF = new ArrayList<String>();
            for (int i=0; i < dataResult.length() ; i++){
                Log.e("tag", "in the loop");
                JSONObject form = dataResult.getJSONObject(i);
                Iterator<?> keys=form.keys();
                while (keys.hasNext()){
                    String key = (String) keys.next();

                    Log.e("Log_tag", "Key: " + key + ": " + form.get(key) + " form");
                    JF.add(key + ". " + form.getString(key) + " form");
                }
                //String aPhone = form.keys();
                Log.e("tag", "Were trying to loop");
                listDataChild.put(listDataHeader.get(3), JF);
            }
        }
        catch(JSONException e){
                Log.e("Log_tag", "Result of job activity555555: " + result);
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
            String url = "http://chimtek-chimtech.rhcloud.com/records/jsonForms/";
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
