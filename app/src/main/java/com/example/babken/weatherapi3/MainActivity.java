package com.example.babken.weatherapi3;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.net.sip.SipSession;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    TextView textViewCityName;
    TextView textViewCityTemp;
    TextView textViewCountry;
    TextView textViewLocalTime;
    Button buttonForCheck;

    TextView erorTextView;
    ImageView imageView;
    LinearLayout linearLayout;
    int dayOrNight;
    TextView textViewHumidity;
    TextView  textViewWind;

    AutoCompleteTextView  autoCompleteTextView;
    ArrayList<String> arrayList;
    ArrayAdapter<String> adapter;
    String resualtEditText;
    private static final String   WEATHER_JSON_CURRENT= "http://api.apixu.com/v1/current.json?key=10ca73317a224c22a71112242170405&q=";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Dont allow ORIENTATION_PORTRAIT
        setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);;

        buttonForCheck=(Button)findViewById(R.id.button_for_check);
        textViewCityName=(TextView)findViewById(R.id.city_name);
        textViewCityTemp=(TextView)findViewById(R.id.city_temp);
        textViewCountry=(TextView)findViewById(R.id.country_id);
        textViewLocalTime=(TextView)findViewById(R.id.local_time_id);
        textViewHumidity=(TextView)findViewById(R.id.humidity_id);
        textViewWind=(TextView)findViewById(R.id.wind_id) ;
        imageView=(ImageView)findViewById(R.id.imageview_id);
        erorTextView=(TextView)findViewById(R.id.erorTextview);
        linearLayout=(LinearLayout)findViewById(R.id.layout_id);

        arrayList=new ArrayList<>();
        autoCompleteTextView=(AutoCompleteTextView) findViewById(R.id.autocomplete_country);

        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, arrayList);

         add();
        autoCompleteTextView.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {

                if (keyEvent.getAction() == KeyEvent.ACTION_DOWN)
                {
                    switch (i)
                    {
                        case KeyEvent.KEYCODE_ENTER:
                            String resualtEditText= autoCompleteTextView.getText().toString();
                            String a=WEATHER_JSON_CURRENT+resualtEditText.replace(' ','_');
                            HttpRequest json=new HttpRequest();
                            json.execute(a);

                            //hide keyboard
                            InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);

                            return true;
                        default:
                            break;
                    }
                }
                return false;
            }
        });


        buttonForCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                //hide keyboard
                InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);

                //get  data from edit text andthen execute
                resualtEditText= autoCompleteTextView.getText().toString();
                String a=WEATHER_JSON_CURRENT+resualtEditText.replace(' ','_');

                HttpRequest json=new HttpRequest();
                json.execute(a);

            }
        });

    }

    private void stugum(String name){
        boolean check=true;
            for (int i = 0; i <arrayList.size() ; i++) {
                if(arrayList.get(i).equals(name)){
                    check=false;
                    break;
                }
            }
            if(check){
                arrayList.add(name);
                add();
            }
    }
    private void add() {
        ArrayAdapter<String> adp=new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line,arrayList);

        adp.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        autoCompleteTextView.setThreshold(1);
        autoCompleteTextView.setAdapter(adp);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.action_exit) {
          System.exit(0);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    private class HttpRequest extends AsyncTask<String,String,String>{
        HttpURLConnection connection=null;
        BufferedReader reader=null;
        String nameCity;
        String tempC;
        String urlImage;
        String country;
        String localtime;
        String humidity;
        String windkph;
        String wind_dir;
        @Override
        protected String doInBackground(String... param) {


            try {
                URL url=new URL(param[0]);

                connection=(HttpURLConnection) url.openConnection();
                connection.connect();
                InputStream stream=connection.getInputStream();
                reader=new BufferedReader(new InputStreamReader(stream));
                StringBuffer buffer=new StringBuffer();

                String line=""  ;
                while ((line=reader.readLine())!=null){
                    buffer.append(line);
                }
                return buffer.toString();

            } catch (IOException e) {
                e.printStackTrace();
            }
            finally {
                if(connection!=null) {
                    connection.disconnect();
                }
                try {
                    if(reader!=null) {
                        reader.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
            return null;


        }

        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
        @Override
        protected void onPostExecute(String resualt) {
            super.onPostExecute(resualt);


            try {
                //getting data from  json
                JSONObject jsonObject=new JSONObject(resualt);
                JSONObject locationObject=jsonObject.getJSONObject("location");
                nameCity=locationObject.getString("name");
                country=locationObject.getString("country");
                localtime=locationObject.getString("localtime");
                JSONObject currentObject=jsonObject.getJSONObject("current");
                windkph=currentObject.getString("wind_kph");
                wind_dir=currentObject.getString("wind_dir");
                humidity=currentObject.getString("humidity");
                tempC=currentObject.getString("temp_c");
                dayOrNight=currentObject.getInt("is_day");

                JSONObject conditionObject=currentObject.getJSONObject("condition");
                urlImage=conditionObject.getString("icon");

                //set data to UI
                Picasso.with(getBaseContext()).load("http:"+urlImage).into(imageView);
                erorTextView.setText("");
                textViewCountry.setText(getResources().getString(R.string.country)+"   "+country);
                textViewCityName.setText(getResources().getString(R.string.city)+"  "+nameCity);
                textViewCityTemp.setText(getResources().getString(R.string.temperature)+"   "+tempC+"C");
                textViewHumidity.setText(getResources().getString(R.string.humidity)+"   "+humidity+"%");
                textViewWind.setText(getResources().getString(R.string.wind)+"   "+windkph+"kph  "+wind_dir);
                textViewLocalTime.setText(getResources().getString(R.string.date)+"   "+   localtime);

                if(dayOrNight==1){
                  linearLayout.setBackground(getDrawable(R.drawable.day));
                 }
            else if(dayOrNight==0){
                    linearLayout.setBackground(getDrawable(R.drawable.night));
                }


                String resualtEditText= autoCompleteTextView.getText().toString();
                stugum(resualtEditText);
            }
            catch (JSONException e) {
                textViewCountry.setText(R.string.eror);
                e.printStackTrace();
            }
            catch (NullPointerException  e){
               erorTextView.setText(R.string.eror);

            }
        }
    }
}
