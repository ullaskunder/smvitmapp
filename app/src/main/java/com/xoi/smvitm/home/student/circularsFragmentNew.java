package com.xoi.smvitm.home.student;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.xoi.smvitm.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class circularsFragmentNew extends Fragment {
    private String url = "http://smvitmapp.xtoinfinity.tech/php/home/circulars.php";
    private ArrayList<String> title = new ArrayList<>();
    private ArrayList<String> date = new ArrayList<>();
    private ArrayList<String> pdflink = new ArrayList<>();

    circularsFragmentRVAdapter adapter;
    LottieAnimationView loadAnim;
    TextView loadTxt;
    FloatingActionButton fab;
    private RecyclerView recyclerView;
    private View view;
    SharedPreferences sp;

    public circularsFragmentNew() {
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_events, container, false);
        loadAnim = (LottieAnimationView) view.findViewById(R.id.loadanim);
        loadTxt = (TextView) view.findViewById(R.id.loadtxt);

        sp = getActivity().getSharedPreferences("com.xoi.smvitm", Context.MODE_PRIVATE);

        loadAnim.setVisibility(View.VISIBLE);
        loadTxt.setVisibility(View.VISIBLE);

        fab = (FloatingActionButton)view.findViewById(R.id.fab);

        if(sp.contains("fid")){
            fab.show();
        }else{
            fab.hide();
        }


        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String uriString = "http://smvitmapp.xtoinfinity.tech/php/home/circularfileupload.php";

                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uriString));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setPackage("com.android.chrome");
                try {
                    startActivity(intent);
                } catch (ActivityNotFoundException ex) {
                    intent.setPackage(null);
                    startActivity(Intent.createChooser(intent, "Select Browser"));
                }
            }
        });
        getFeed();


        return view;
    }

    private void getFeed() {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {

                StringRequest stringRequest = new StringRequest(Request.Method.GET, url ,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {

                                parseItems(response);

                            }
                        },

                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                loadAnim.setVisibility(View.GONE);
                                loadTxt.setVisibility(View.GONE);
                                Toast.makeText(getActivity(), ""+error, Toast.LENGTH_SHORT).show();

                            }
                        }
                );
                int socketTimeOut = 50000;
                RetryPolicy policy = new DefaultRetryPolicy(socketTimeOut, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
                stringRequest.setRetryPolicy(policy);
                RequestQueue queue = Volley.newRequestQueue(getActivity());
                queue.add(stringRequest);
            }
        });
    }

    private void parseItems(String jsonResposnce) {
        try {
            JSONObject jobj = new JSONObject(jsonResposnce);
            JSONArray jarray = jobj.getJSONArray("result");

            for (int i = 0; i < jarray.length(); i++) {
                JSONObject jo = jarray.getJSONObject(i);
                title.add(jo.optString("circular_title"));
                date.add(jo.optString("circular_date"));
                pdflink.add(jo.optString("circular_pdflink"));
            }
            loadAnim.setVisibility(View.GONE);
            loadTxt.setVisibility(View.GONE);

            recyclerView = view.findViewById(R.id.recyclerview);
            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            adapter = new circularsFragmentRVAdapter(title,date,pdflink,getActivity());
            recyclerView.setAdapter(adapter);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}

