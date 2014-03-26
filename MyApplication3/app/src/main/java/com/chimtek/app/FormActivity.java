package com.chimtek.app;

import android.graphics.Color;
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
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.CookieStore;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FormActivity extends ActionBarActivity {
    List<Map<String, String>> questionList = new ArrayList<Map<String,String>>();
    private LinearLayout myLayout;
    private TextView questionText;
    private EditText textBox;
    private RadioGroup singleSelect;
    private RadioButton select;
    private CheckBox checkBox;
    private Button submitButton;

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_form);

        myLayout = (LinearLayout) findViewById(R.id.linearLayout);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
        myLayout.setOrientation(LinearLayout.VERTICAL);

        Bundle data = getIntent().getExtras();
        final String formID = (String) data.get("ID");
        String url = "http://chimtek-chimtech.rhcloud.com/records/jsonQuestions/" + formID + "/";
        final String questionsData = pullData(url);
        Log.e("Tag", "QuestionData: " + questionsData);

        JSONArray questionToJSON;
        try {
            questionToJSON = new JSONArray(questionsData);
            int questionIDs = 1;
            int radioIDs = 201;
            int radioGroupIDs = 101;
            int mmIDs = 301;
            int TxIDs = 401;
            List mmDivides = new ArrayList();
            List responseTypesList = new ArrayList();
            for(int i=0; i<questionToJSON.length();i++){
                int q = i + 1;
                String questionNum = (String) "" + q;

                JSONObject JSONdata = questionToJSON.getJSONObject(i);
                //questionList.add(createquestion("question", JSONdata.getString(questionNum)));
                Log.e("tag", JSONdata.getString(questionNum));
                questionText = new TextView(this);
                questionText.setId(questionIDs);
                questionIDs ++;
                questionText.setText(JSONdata.getString(questionNum));
                questionText.setLayoutParams(params);
                myLayout.addView(questionText);
                //Log.e("Tag" , JSONdata.getString("response"));
                //String response = (String) JSONdata.getString("response");
                //Log.e("Tag" , response);
                if (JSONdata.getString("response").contains("TX")){
                    textBox = new EditText(this);
                    textBox.setId(TxIDs);
                    TxIDs ++;
                    //textBox.setText("five");
                    textBox.setLayoutParams(params);
                    myLayout.addView(textBox);
                    responseTypesList.add("TX");
                }
                else if (JSONdata.getString("response").contains("MS")){
                    Log.e("tag", "This is for the question: " + JSONdata.getString(questionNum));
                    Log.e("tag" , "Did we make it this far");
                    singleSelect = new RadioGroup(this);
                    singleSelect.setId(radioGroupIDs);
                    radioGroupIDs ++;
                    Log.e("tag", "the id for this radio group is: " + i);
                    singleSelect.setLayoutParams(params);
                    //myLayout.addView(singleSelect);
                    url = "http://chimtek-chimtech.rhcloud.com/records/jsonResponses/" + JSONdata.getString("ID") + "/";
                    final String responsesData = pullData(url);
                    Log.e("tag", "the url is :" + url);
                    JSONArray responsesToJSON;
                    try {
                        responsesToJSON = new JSONArray(responsesData);

                        for (int j = 0; j < responsesToJSON.length(); j++) {
                            Log.e("tag", "How about this far");
                            int r = j + 1;
                            String responseNum = (String) "" + r;
                            JSONObject JSONresponse = responsesToJSON.getJSONObject(j);
                            select = new RadioButton(this);
                            select.setId(radioIDs);
                            radioIDs ++;
                            String call = "" + r;
                            select.setText(JSONresponse.getString(call));
                            Log.e("tag", "The choice was :" + JSONresponse.getString(call) + " for :" + call);
                            Log.e("tag", "This is part of the group: " + singleSelect.getId());
                            singleSelect.addView(select);
                        }
                        myLayout.addView(singleSelect);
                        responseTypesList.add("MS");
                    }
                    catch (JSONException e){
                        e.printStackTrace();
                    }
                }
                else if (JSONdata.getString("response").contains("MM")){
                    Log.e("MM", "This is for the question: " + JSONdata.getString(questionNum));
                    Log.e("MM" , "Did we make it this far");
                    url = "http://chimtek-chimtech.rhcloud.com/records/jsonResponses/" + JSONdata.getString("ID") + "/";
                    final String mmData = pullData(url);
                    Log.e("MM", "the url is :" + url);
                    JSONArray mmToJSON;
                    try {
                        mmToJSON = new JSONArray(mmData);

                        for (int j = 0; j < mmToJSON.length(); j++) {
                            Log.e("MM", "How about this far");
                            int r = j + 1;
                            String responseNum = (String) "" + r;
                            JSONObject JSONmm = mmToJSON.getJSONObject(j);
                            checkBox = new CheckBox(this);
                            checkBox.setId(mmIDs);
                            mmIDs ++;
                            String call = "" + r;
                            checkBox.setText(JSONmm.getString(call));
                            Log.e("MM", "The choice was :" + JSONmm.getString(call) + " for :" + call);
                            myLayout.addView(checkBox);
                        }
                        responseTypesList.add("MM");
                        mmDivides.add(mmToJSON.length());
                    }
                    catch (JSONException e){
                        e.printStackTrace();
                    }
                }
                else {
                    textBox = new EditText(this);
                    textBox.setId(i);
                    textBox.setText("This was not a TX response");
                    textBox.setLayoutParams(params);
                    myLayout.addView(textBox);
                }
                //Log.e("Tag", "QUESTIONNUM: " + questionNum);
            }
            submitButton = new Button(this);
            submitButton.setText("Submit Form");
            submitButton.setTextSize(30);
            submitButton.setBackgroundColor(Color.RED);
            final int count = questionIDs;
            final List finallist = responseTypesList;
            final List mmfinallist = mmDivides;
            submitButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View my) {
                    JSONArray jsonSubmit = new JSONArray();
                    int five = (int) 5;
                    Dictionary Dmain = null;
                    List submitQuestions = new ArrayList();
                    List submitresponses = new ArrayList();
                    HashMap<String, String> submitHash = new HashMap<String, String>();
                    HashMap<String , List> testHash = new HashMap<String , List>();

                    Log.e("clicked", "clicked" + five);
                    Log.e("QandA" , mmfinallist.toString());
                    for (int i = 1; i < count; i ++) {
                        if (finallist.get(i - 1) == "MS") {
                            RadioGroup vals = (RadioGroup) findViewById(100 + i);
                            RadioButton Rvalue = (RadioButton) findViewById(vals.getCheckedRadioButtonId());
                            TextView Tquestion = (TextView) findViewById(i);
                            String response = (String) Rvalue.getText();
                            String question = (String) Tquestion.getText();
                            finallist.get(i - 1);
                            Log.e("QandA", "The question was :" + question + " and the response was :" + response);
                            submitQuestions.add(question);
                            submitresponses.add(response);
                            submitHash.put(question , response);
                            //Dmain.put(question , response);
                        }
                        else if (finallist.get(i -1) == "TX") {
                            EditText Rvalue = (EditText) findViewById(400 + 1);
                            TextView Tquestion = (TextView) findViewById(i);
                            String response = (String) Rvalue.getText().toString();
                            String question = (String) Tquestion.getText();
                            Log.e("QandA" , "The question was :" + question + " and the response was :" + response);
                            //Dmain.put(question , response);
                            submitHash.put(question,response);
                            submitQuestions.add(question);
                            submitresponses.add(response);
                        }
                        else if (finallist.get(i-1) == "MM"){
                            List checkedBoxes = new ArrayList();
                            String checked = "";
                            TextView Tquestion = (TextView) findViewById(i);
                            String question = (String) Tquestion.getText();
                            for (int j = 1; j <= Integer.parseInt(mmfinallist.get(i - 1).toString()); j ++){
                                CheckBox box = (CheckBox) findViewById(300 + j);
                                Log.e("QandA", box.getText().toString());
                                if (box.isChecked()==true){
                                    checkedBoxes.add(box.getText().toString());
                                    if (checked == ""){
                                        checked += box.getText().toString();
                                    }
                                    else{
                                        checked += " , " + box.getText().toString();

                                    }
                                }
                            }
                            //Log.e("QandA" , checkedBoxes.toString());
                            //Dmain.put(question , checkedBoxes.toString());
                            submitQuestions.add(question);
                            submitresponses.add(checked);
                            submitHash.put(question, checkedBoxes.toString());
                        }

                        //Log.e("Dict" , "" + Dmain.size());
                    }
                    testHash.put("questions" , submitQuestions);
                    testHash.put("responses" , submitresponses);
                    JSONArray maybe = new JSONArray(submitQuestions);
                    JSONArray maybe1 = new JSONArray(submitresponses);
                    JSONObject hashToJson = new JSONObject(testHash);
                    Log.e("QandA" , jsonSubmit.toString());
                    Log.e("QandA" , "JSON reply: " + maybe.toString());
                    Log.e("QandA" , "JSON reply: " + maybe1.toString());
                    Log.e("QandA" , "Question list: " + submitQuestions.toString());
                    Log.e("QandA" , "Response list:" + submitresponses.toString());
                    Log.e("QandA" , "HASH :" + submitHash.toString());
                    Log.e("QandA" , testHash.toString());
                    Log.e("QandA" , "HASH TO JSON RESULTS IN: " + hashToJson.toString());
                    putData(hashToJson);

                }
            });



            submitButton.setLayoutParams(params);
            myLayout.addView(submitButton);
        }
        catch (JSONException e) {
            e.printStackTrace();
        }

        ListView lv = (ListView) findViewById(R.id.questionList);
        SimpleAdapter simpleAdpt = new SimpleAdapter(this, questionList, android.R.layout.simple_list_item_1, new String[] {"question"}, new int[] {android.R.id.text1});
        lv.setAdapter(simpleAdpt);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.form, menu);
        return true;
    }



    public boolean putData(JSONObject jsonData){
        InputStream isr = null;
        InputStream input = null;
        String result = "";
        //try{
        //    HttpClient tokenGrabber = new DefaultHttpClient();
        //    HttpResponse tokens = tokenGrabber.execute(new HttpGet("http//www.google.com"));
        //    Header[] cookies = tokens.getHeaders("set-cookie");
        //    Log.e("tag" , cookies.toString());
        //} catch (ClientProtocolException e) {
        //    e.printStackTrace();
        //} catch (IOException e) {
        //    e.printStackTrace();
        //}
        try{
            String json = "";
            HttpClient tokens = new DefaultHttpClient();
            HttpGet httpGet = new HttpGet("http://chimtek-chimtech.rhcloud.com/records/dataSubmit/");
            Header[] head = httpGet.getAllHeaders();
            Log.e("cookies" , head.toString());

            HttpClient putclient = new DefaultHttpClient();
            HttpPost httpost = new HttpPost("http://chimtek-chimtech.rhcloud.com/records/dataSubmit/");

            JSONObject jobject = new JSONObject();
            jobject.accumulate("1" , "one");
            jobject.accumulate("2" , "two");
            jobject.accumulate("3" , "three");
            json = jobject.toString();
            StringEntity se = new StringEntity(jsonData.toString());
            httpost.setEntity(se);
            httpost.setHeader("Accept", "application/json");
            httpost.setHeader("Content-type" , "application/json");
            StrictMode.enableDefaults();
            HttpResponse p = putclient.execute(httpost);
            HttpEntity entity = p.getEntity();
            isr = entity.getContent();
            try {
                Log.e("Log_tag", "Trying to result");
                BufferedReader reader = new BufferedReader(new InputStreamReader(isr, "iso-8859-1"), 8);
                StringBuilder sb = new StringBuilder();
                String line = null;
                while ((line = reader.readLine()) != null) {
                    sb.append(line + "\n");
                }
                isr.close();
                result = sb.toString();
                //JSONArray somethingelse = new JSONArray(result);
                Log.e("Log_tag", "showing result: " + result);
            }
            catch(Exception e) {
                Log.e("log_tag", "Error in http connection ");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }


    public String pullData(String url){

        String result = "";
        //Bundle data = getIntent().getExtras();
        //final String formID = (String) data.get("ID");
        //JSONArray = new JSONArray(something);
        InputStream isr = null;
        String username = "jacbwest";
        Log.e("Log_tag", "Logging Test");
        try{
            Log.e("Log_tag", "Trying to get");
            HttpClient httpclient = new DefaultHttpClient();
            //String url = "http://chimtek-chimtech.rhcloud.com/records/jsonQuestions/" + formID + "/";
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




    private HashMap<String, String> createquestion(String key, String name) {
        HashMap<String, String> question = new HashMap<String, String>();
        question.put(key, name);

        return question;
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
            View rootView = inflater.inflate(R.layout.fragment_form, container, false);
            return rootView;
        }
    }

}
