package com.example.bz_frontend_new;

import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * Custom request class for handling multipart/form-data requests with Volley.
 * This version supports file uploads directly from File objects.
 */
public class VolleyMultipartRequest extends Request<String> {

    private final Response.Listener<String> mListener;
    private final Response.ErrorListener mErrorListener;
    private final File mFile;
    private final String mBoundary = "apiclient-" + System.currentTimeMillis();
    private final String mLineEnd = "\r\n";
    private final String mTwoHyphens = "--";

    // Constructor for file upload
    public VolleyMultipartRequest(int method, String url, File file,
                                  Response.Listener<String> listener, Response.ErrorListener errorListener) {
        super(method, url, errorListener);
        this.mListener = listener;
        this.mErrorListener = errorListener;
        this.mFile = file;
    }

    @Override
    public String getBodyContentType() {
        return "multipart/form-data;boundary=" + mBoundary;
    }

    @Override
    public byte[] getBody() {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(bos);

        try {
            // Add file part
            dos.writeBytes(mTwoHyphens + mBoundary + mLineEnd);
            dos.writeBytes("Content-Disposition: form-data; name=\"file\"; filename=\"" + mFile.getName() + "\"" + mLineEnd);
            dos.writeBytes("Content-Type: image/jpeg" + mLineEnd);
            dos.writeBytes(mLineEnd);

            // Write file data
            FileInputStream fileInputStream = new FileInputStream(mFile);
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = fileInputStream.read(buffer)) != -1) {
                dos.write(buffer, 0, bytesRead);
            }
            fileInputStream.close();

            dos.writeBytes(mLineEnd);
            dos.writeBytes(mTwoHyphens + mBoundary + mTwoHyphens + mLineEnd);

            return bos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected Response<String> parseNetworkResponse(NetworkResponse response) {
        try {
            String responseString = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
            return Response.success(responseString, HttpHeaderParser.parseCacheHeaders(response));
        } catch (Exception e) {
            return Response.error(new ParseError(e));
        }
    }

    @Override
    protected void deliverResponse(String response) {
        mListener.onResponse(response);
    }

    @Override
    public void deliverError(com.android.volley.VolleyError error) {
        mErrorListener.onErrorResponse(error);
    }
}