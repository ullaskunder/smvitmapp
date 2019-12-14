package com.xoi.smvitm;


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
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class circularsFragment extends Fragment {
    private static final String URL_DATA = "http://smvitmapp.xtoinfinity.tech/php/circulars.php";
    private RecyclerView recyclerView;
    private  RecyclerView.Adapter adapter;
    private List<CircularItems> circularItems;
    LottieAnimationView loadAnim;
    TextView loadTxt;


    public circularsFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.fragment_circulars, container, false);

        loadAnim = (LottieAnimationView)view.findViewById(R.id.loadanim);
        loadTxt = (TextView)view.findViewById(R.id.loadtxt);

        loadAnim.setVisibility(View.VISIBLE);
        loadTxt.setVisibility(View.VISIBLE);

        recyclerView = (RecyclerView)view.findViewById(R.id.recyclerview);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        circularItems = new ArrayList<>();

        getItems();

        return view;
    }

    private void getItems(){

        // final ProgressDialog loading =  ProgressDialog.show(getActivity(),"Loading","please wait",false,true);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, URL_DATA, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    // loading.dismiss();
                    loadAnim.setVisibility(View.GONE);
                    loadTxt.setVisibility(View.GONE);
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray jsonArray = jsonObject.getJSONArray("result");

                    for(int i=0;i<jsonArray.length();i++){
                        JSONObject jo = jsonArray.getJSONObject(i);

                        CircularItems items = new CircularItems(

                                jo.getString("circular_title"),
                                jo.getString("circular_date"),
                                jo.getString("circular_pdflink")
                        );

                        circularItems.add(items);
                    }

                    adapter = new CircularAdapter(circularItems,getActivity().getApplicationContext());
                    recyclerView.setAdapter(adapter);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // loading.dismiss();
                loadAnim.setVisibility(View.GONE);
                loadTxt.setVisibility(View.GONE);
                Toast.makeText(getActivity().getApplicationContext(),error.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });

        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        requestQueue.add(stringRequest);

    }

}
