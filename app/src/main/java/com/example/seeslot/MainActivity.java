package com.example.seeslot;

import android.content.Context;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.method.ScrollingMovementMethod;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.button.MaterialButtonToggleGroup;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.appcompat.app.AppCompatActivity;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    String date, age, availability, centername;
    TextView textView, counttext,note,lastcheck;
    JSONArray jarray, sesn;
    EditText pin;
    Button button, button2, addcount, subcount;

    Handler handler = new Handler();
    Runnable runnable;
    int count, delay = 15000;
    ToggleButton tgl18, tgl45;
    MediaPlayer mp;
    Context mContext;
    String url;
    List<String> statelist,districtlist;
    ArrayAdapter<String> stateadapter,districtadapter;
    MaterialButtonToggleGroup mbtg;
    View distview,pinview;
//    Spinner statespinner,distSpiner;
    int firstDistId = 0;


    public void getStates(){
        String stateurl = "https://cdn-api.co-vin.in/api/v2/admin/location/states";
        RequestQueue requestQueue;
        requestQueue = Volley.newRequestQueue(this);


        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, stateurl,
                null, new Response.Listener<JSONObject>()
        {
            @Override
            public void onResponse(JSONObject response) {
                try
                {
                    for (int i = 0; i < response.getJSONArray("states").length(); i++) {
                        statelist.add(response.getJSONArray("states").getJSONObject(i).getString("state_name"));
                    }
                    stateadapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("myapp", "something went Wrong" + error);
            }

        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/56.0.2924.76 Safari/537.36");

                return params;
            }
        };

        requestQueue.add(jsonObjectRequest);


    }



    public void getdist(long id){
        String disturl = "https://cdn-api.co-vin.in/api/v2/admin/location/districts/"+id;
        RequestQueue requestQueue;
        requestQueue = Volley.newRequestQueue(this);


        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, disturl,
                null, new Response.Listener<JSONObject>()
        {
            @Override
            public void onResponse(JSONObject response) {
                try
                {   districtlist.clear();
                    firstDistId = Integer.parseInt(response.getJSONArray("districts").getJSONObject(0).getString("district_id"));
                    for (int i = 0; i < response.getJSONArray("districts").length(); i++) {
                        districtlist.add(response.getJSONArray("districts").getJSONObject(i).getString("district_name"));
                    }
                    districtadapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("myapp", "something went Wrong" + error);
            }

        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/56.0.2924.76 Safari/537.36");

                return params;
            }
        };

        requestQueue.add(jsonObjectRequest);


    }

























    public void datafetch(Editable pin) {


        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        String currentDateandTime = sdf.format(new Date());
        RequestQueue requestQueue;
        requestQueue = Volley.newRequestQueue(this);

        url = ("https://cdn-api.co-vin.in/api/v2/appointment/sessions/public/calendarByPin?pincode=" + pin + "&date=" + currentDateandTime);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>()
        {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    textView.setText("\n");
                    lastcheck.setText("Slot Details: Last Checked On: [" + new SimpleDateFormat("hh:mm:ss aa").format(new Date())+"]");
                    for (int i = 0; i < response.getJSONArray("centers").length(); i++) {
                        centername = response.getJSONArray("centers").getJSONObject(i).getString("name");
                        sesn = response.getJSONArray("centers").getJSONObject(i).getJSONArray("sessions");
                        textView.append("\nCENTER NAME      : " + centername);

                        for (int j = 0; j < sesn.length(); j++) {
                            date = sesn.getJSONObject(j).getString("date");
                            availability = sesn.getJSONObject(j).getString("available_capacity");
                            age = sesn.getJSONObject(j).getString("min_age_limit");

                            textView.append("\n DATE                    : " + date);
                            textView.append("\n AGE                      : " + age+"+");
                            Spannable word = new SpannableString(availability);
                            if(Integer.parseInt(availability)>0)
                            {
                                word.setSpan(new ForegroundColorSpan(Color.GREEN), 0, word.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                            }
                            else
                            {
                                word.setSpan(new ForegroundColorSpan(Color.RED), 0, word.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                            }

                            textView.append("\n AVAILABILITY : " + word);



                            if(tgl18.isChecked() && age.equals("18") && !availability.equals("0") ){
                                mp.start();
                            }
                            if(tgl45.isChecked() && age.equals("45") && !availability.equals("0") ){

                                mp.start();
                            }



                        }
                        textView.append("\n");
                    }

                    jarray = response.getJSONArray("centers");
                    Log.d("myapp", "The Response is : " + response.getJSONArray("centers").getJSONObject(0).getJSONArray("session").getString(0));
                    Log.d("Jarray", "jason array is : " + jarray);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                textView.setMovementMethod(new ScrollingMovementMethod());
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("myapp", "something went Wrong" + error);
            }

        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/56.0.2924.76 Safari/537.36");


                return params;
            }
        };

        requestQueue.add(jsonObjectRequest);


    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textView = findViewById(R.id.textView);
        note = findViewById(R.id.note);
        note.append("\n    1. SET THE MEDIA VOLUME UP");
        note.append("\n    2. KEEP THE APP OPEN IN BACKGROUND");
        note.append("\n    3. EDIT PIN > SET TIMER > PRESS THE BELL > PRESS GET SLOT");
        note.append("\nWith regards: HRITIK KHATRI");
        pin = findViewById(R.id.pincode);
        datafetch(pin.getText());
        button = findViewById(R.id.button);
        button2 = findViewById(R.id.button2);
        button.setOnClickListener(this);
        button2.setOnClickListener(this);
        subcount = findViewById(R.id.subcount);
        addcount = findViewById(R.id.addcount);
        lastcheck = findViewById(R.id.lastcheck);
        counttext = findViewById(R.id.counttext);
        tgl45 = findViewById(R.id.tgl45);
        tgl45.setChecked(false);
        Spinner statespinner = findViewById(R.id.State_spinner);
        Spinner distspinner = findViewById(R.id.District_spinner);
        statelist = new ArrayList<String>();
        districtlist = new ArrayList<String>();
        tgl18 = findViewById(R.id.tgl18);
        tgl18.setChecked(false);
        mContext = getApplicationContext();
        mp = MediaPlayer.create(mContext,R.raw.siren_alert);
        AudioManager audioManager = (AudioManager) getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
        mp.setLooping(true);
        mbtg = findViewById(R.id.toggleGroup);
        distview = findViewById(R.id.distview);
        pinview = findViewById(R.id.pinview);
        distview.setVisibility(View.GONE);
        statespinner = findViewById(R.id.State_spinner);
        distspinner = findViewById(R.id.District_spinner);
        //set these on search buy district
        stateadapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, statelist);
        statespinner.setAdapter(stateadapter);
        districtadapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, districtlist);
        distspinner.setAdapter(districtadapter);





        mbtg.addOnButtonCheckedListener(new MaterialButtonToggleGroup.OnButtonCheckedListener()
        {

            @Override
            public void onButtonChecked(MaterialButtonToggleGroup group, int checkedId, boolean isChecked)
            {
                getStates();

                if(mbtg.findViewById(checkedId) == mbtg.findViewById(R.id.pinbtn))
                {
                    distview.setVisibility(View.GONE);
                    pinview.setVisibility(View.VISIBLE);
                }
                else
                {
                    pinview.setVisibility(View.GONE);
                    distview.setVisibility(View.VISIBLE);
                }
            }
        });
        addcount.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        count = Integer.parseInt(String.valueOf(counttext.getText()).split(" ")[0]);
                        count++;
                        counttext.setText(""+ count +" Sec.");
                        delay = count * 1000;

                    }
                });

        subcount.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {


                            count = Integer.parseInt(String.valueOf(counttext.getText()).split(" ")[0]);
                        if(!(count <= 4))
                        {
                            count--;
                            counttext.setText("" + count + " Sec.");
                            delay = count * 1000;
                        }
                        else
                        {
                            Toast.makeText(getApplicationContext(),"You cannot set timer less then 4 seocnds as you can only make 100 request per 5 minutes",Toast.LENGTH_SHORT).show();

                        }
                    }
                });



        statespinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                // your code here
               Log.i("STATE","this state"+id);
                getdist(id);

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }

        });




        distspinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                // your code here
                Log.i("DISTRICT","this district id  "+(id+firstDistId));

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }

        });

    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button:
                handler.removeCallbacks(runnable);
                if(mp.isPlaying()){mp.pause();}
                Editable pn = pin.getText();
                datafetch(pn);
                handler.postDelayed(runnable = new Runnable() {
                    public void run() {
                        handler.postDelayed(runnable, delay);
                        datafetch(pn);
                    }
                }, delay);
                break;

            case R.id.button2:
                handler.removeCallbacks(runnable);
                tgl18.setChecked(false);
                tgl45.setChecked(false);
                if(mp.isPlaying()){mp.pause();}
                break;

            default:
                throw new IllegalStateException("Unexpected value: " + view.getId());
        }
    }
}



