package com.example.dogapp.data.repository;

import android.content.Context;
import android.util.Log;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

public class ApiService {
    private static final String BASE_URL = "http://192.168.1.3:5000";
    private RequestQueue requestQueue;

    public ApiService(Context context) {
        requestQueue = Volley.newRequestQueue(context);
    }

    public void getAllDogs(Response.Listener<List<com.example.dogapp.data.model.Dog>> successListener,
                           Response.ErrorListener errorListener) {
        String url = BASE_URL + "/api/dog/all";
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, null,
                response -> {
                    try {
                        JSONArray jsonArray = response.getJSONArray("data");
                        List<com.example.dogapp.data.model.Dog> dogList = new ArrayList<>();
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            String id = jsonObject.getString("_id");
                            String name = jsonObject.getString("name");
                            String size = jsonObject.getString("size");
                            String lifeSpan = jsonObject.getString("life_span");
                            String temperament = jsonObject.getString("temperament");
                            String des = jsonObject.getString("des");
                            String takeCare = jsonObject.getString("take_care");
                            String sick = jsonObject.getString("sick");
                            String image = jsonObject.getString("image");
                            dogList.add(new com.example.dogapp.data.model.Dog(id, name, size, lifeSpan, temperament, des, takeCare, sick, image));
                        }
                        successListener.onResponse(dogList);
                    } catch (JSONException e) {
                        e.printStackTrace();
                        errorListener.onErrorResponse(new VolleyError(e));
                    }
                }, error -> {
            Log.e("API_ERROR", error.toString());
            errorListener.onErrorResponse(error);
        });
        requestQueue.add(request);
    }

    public void getAllProducts(Response.Listener<List<com.example.dogapp.data.model.Product>> successListener,
                               Response.ErrorListener errorListener) {
        String url = BASE_URL + "/api/product/all";
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, null,
                response -> {
                    try {
                        JSONArray jsonArray = response.getJSONArray("data");
                        List<com.example.dogapp.data.model.Product> productList = new ArrayList<>();
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            String id = jsonObject.getString("_id");
                            String name = jsonObject.getString("name");
                            String size = jsonObject.getString("size");
                            String color = jsonObject.getString("color");
                            String des = jsonObject.getString("des");
                            int price = jsonObject.getInt("price");
                            JSONArray imageArray = jsonObject.getJSONArray("image");
                            List<String> images = new ArrayList<>();
                            for (int j = 0; j < imageArray.length(); j++) {
                                images.add(imageArray.getString(j));
                            }
                            productList.add(new com.example.dogapp.data.model.Product(id, name, size, color, des, price, images));
                        }
                        successListener.onResponse(productList);
                    } catch (JSONException e) {
                        e.printStackTrace();
                        errorListener.onErrorResponse(new VolleyError(e));
                    }
                }, error -> {
            Log.e("API_ERROR", error.toString());
            errorListener.onErrorResponse(error);
        });
        requestQueue.add(request);
    }
}