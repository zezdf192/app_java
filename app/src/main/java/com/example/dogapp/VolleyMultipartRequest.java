package com.example.dogapp;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import android.util.Log;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class VolleyMultipartRequest extends Request<NetworkResponse> {
    private final Response.Listener<NetworkResponse> mListener;
    private final Response.ErrorListener mErrorListener;
    private final Map<String, DataPart> mByteData;
    private final String boundary = "boundary_" + System.currentTimeMillis();

    public VolleyMultipartRequest(int method, String url, Response.Listener<NetworkResponse> listener,
                                  Response.ErrorListener errorListener) {
        super(method, url, errorListener);
        this.mListener = listener;
        this.mErrorListener = errorListener;
        this.mByteData = new HashMap<>();
    }

    protected Map<String, DataPart> getByteData() {
        return mByteData;
    }

    @Override
    protected Response<NetworkResponse> parseNetworkResponse(NetworkResponse response) {
        return Response.success(response, null);
    }

    @Override
    protected void deliverResponse(NetworkResponse response) {
        mListener.onResponse(response);
    }

    @Override
    public void deliverError(VolleyError error) {
        mErrorListener.onErrorResponse(error);
    }

    @Override
    public String getBodyContentType() {
        return "multipart/form-data; boundary=" + boundary;
    }

    @Override
    public byte[] getBody() {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            for (Map.Entry<String, DataPart> entry : getByteData().entrySet()) {
                String key = entry.getKey();
                DataPart dataPart = entry.getValue();
                // Header của phần multipart
                bos.write(("--" + boundary + "\r\n").getBytes());
                bos.write(("Content-Disposition: form-data; name=\"" + key + "\"; filename=\"" + dataPart.getFileName() + "\"\r\n").getBytes());
                bos.write(("Content-Type: " + dataPart.getType() + "\r\n").getBytes());
                bos.write(("Content-Length: " + dataPart.getData().length + "\r\n").getBytes());
                bos.write("\r\n".getBytes());
                // Dữ liệu file
                bos.write(dataPart.getData());
                bos.write("\r\n".getBytes());
            }
            // Kết thúc multipart
            bos.write(("--" + boundary + "--\r\n").getBytes());

            // Log dữ liệu gửi đi (dưới dạng text, chỉ log một phần để tránh quá dài)
            String requestBody = new String(bos.toByteArray());
            Log.d("VolleyMultipartRequest", "Request body (first 500 chars): " + requestBody.substring(0, Math.min(500, requestBody.length())));
        } catch (IOException e) {
            Log.e("VolleyMultipartRequest", "Error building multipart body: " + e.getMessage());
        }
        return bos.toByteArray();
    }

    public static class DataPart {
        private final String fileName;
        private final byte[] data;
        private final String type;

        public DataPart(String fileName, byte[] data, String type) {
            this.fileName = fileName;
            this.data = data;
            this.type = type;
        }

        public String getFileName() {
            return fileName;
        }

        public byte[] getData() {
            return data;
        }

        public String getType() {
            return type;
        }
    }
}