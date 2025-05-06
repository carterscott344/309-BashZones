package com.example.bz_frontend_new;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserListAdapter extends ArrayAdapter<UserListFragment.UserItem> {
    private static final String TAG = "UserListAdapter";
    private Context context;
    private List<UserListFragment.UserItem> userItems;
    private UserListFragment parentFragment;
    private boolean adminMode = false;


    public UserListAdapter(Context context, List<UserListFragment.UserItem> userItems) {
        super(context, 0, userItems);
        this.context = context;
        this.userItems = userItems;
    }

    public void setAdminMode(boolean adminMode) {
        this.adminMode = adminMode;
        notifyDataSetChanged();
    }

    public void setParentFragment(UserListFragment fragment) {
        this.parentFragment = fragment;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_user, parent, false);
        }

        UserListFragment.UserItem userItem = getItem(position);
        if (userItem == null) return convertView;

        TextView usernameTextView = convertView.findViewById(R.id.usernameTextView);
        Button actionButton1 = convertView.findViewById(R.id.button1);
        Button actionButton2 = convertView.findViewById(R.id.button2);
        ImageView profilePicture = convertView.findViewById(R.id.profilePicture);

        usernameTextView.setText(userItem.getUsername());


        loadProfilePicture(userItem.getId(), profilePicture);

        if (adminMode) {
            // ADMIN MODE - show both buttons
            actionButton1.setVisibility(View.VISIBLE);
            actionButton2.setVisibility(View.VISIBLE);

            // Configure Button 1 (Ban/Unban)
            boolean isBanned;
            if (userItem instanceof UserListFragment.AdminUserItem) {
                isBanned = ((UserListFragment.AdminUserItem) userItem).isBanned();
            } else {
                isBanned = false;
            }
            actionButton1.setText(isBanned ? "Unban" : "Ban");
            actionButton1.setOnClickListener(v -> {
                if (parentFragment != null && parentFragment.getActivity() instanceof AdminPage) {
                    AdminPage activity = (AdminPage) parentFragment.getActivity();
                    if (isBanned) {
                        activity.unban_user(userItem.getId());
                    } else {
                        activity.ban_user(userItem.getId());
                    }
                }
            });

            // Configure Button 2 (Promote)
            actionButton2.setText("Remove Profile Picture");
            actionButton2.setOnClickListener(v -> {
                if (parentFragment != null && parentFragment.getActivity() instanceof AdminPage) {
                    AdminPage activity = (AdminPage) parentFragment.getActivity();
                    activity.delete_pfp(userItem.getId());
                }
            });
        } else {
            // PROFILE MODE - only show button1
            actionButton1.setVisibility(View.VISIBLE);
            actionButton2.setVisibility(View.GONE);

            if (context instanceof ProfilePage) {
                ProfilePage activity = (ProfilePage) context;
                long userId = activity.getUserId();

                if (parentFragment != null) {
                    String listType = parentFragment.getListType();
                    if ("friends".equals(listType)) {
                        actionButton1.setText("Remove");
                        actionButton1.setOnClickListener(v -> removeFriend(userId, userItem.getId()));
                    } else if ("blocked".equals(listType)) {
                        actionButton1.setText("Unblock");
                        actionButton1.setOnClickListener(v -> unblockUser(userId, userItem.getId()));
                    }
                }
            }
        }
        return convertView;
    }

    private void removeFriend(long userId, long targetId) {
        String url = "http://coms-3090-046.class.las.iastate.edu:8080/accountUsers/" + userId + "/removeFriend/" + targetId;
        performAction(url, "Friend removed successfully");
    }

    private void unblockUser(long userId, long targetId) {
        String url = "http://coms-3090-046.class.las.iastate.edu:8080/accountUsers/" + userId + "/removeBlockedUser/" + targetId;
        performAction(url, "User unblocked successfully");
    }


    private void performAction(String url, String successMessage) {
        StringRequest request = new StringRequest(
                Request.Method.DELETE,
                url,
                response -> {
                    Toast.makeText(context, successMessage, Toast.LENGTH_SHORT).show();
                    if (context instanceof ProfilePage) {
                        ProfilePage activity = (ProfilePage) context;
                        if (successMessage.contains("Friend")) {
                            activity.loadFriendsList();
                        } else {
                            activity.loadBlockedList();
                        }
                    }
                },
                error -> {
                    Toast.makeText(context, "Action failed: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Action failed: " + error.toString());
                }
        ) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                return headers;
            }
        };

        VolleySingleton.getInstance(context).addToRequestQueue(request);
    }


    private void loadProfilePicture(long userId, ImageView imageView) {
        if (imageView == null) {
            return;
        }

        // Set default image first
        imageView.setImageResource(R.drawable.default2);

        String url = "http://coms-3090-046.class.las.iastate.edu:8080/accountUsers/" + userId + "/profilePicture";

        Request<byte[]> byteRequest = new Request<byte[]>(
                Request.Method.GET,
                url,
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Don't log 404 errors as they're expected for users without profile pictures
                        if (error.networkResponse == null || error.networkResponse.statusCode != 404) {
                            Log.w(TAG, "Error loading profile picture for user " + userId +
                                    ": " + (error.getMessage() != null ? error.getMessage() : "Unknown error"));
                        }

                    }
                }
        ) {
            @Override
            protected Response<byte[]> parseNetworkResponse(NetworkResponse response) {
                try {
                    if (response.statusCode == 404) {
                        // User has no profile picture - this is normal
                        return Response.error(new VolleyError("No profile picture exists"));
                    }
                    return Response.success(response.data, HttpHeaderParser.parseCacheHeaders(response));
                } catch (Exception e) {
                    return Response.error(new VolleyError(e));
                }
            }

            @Override
            protected void deliverResponse(byte[] response) {
                try {
                    Bitmap bitmap = BitmapFactory.decodeByteArray(response, 0, response.length);
                    if (bitmap != null) {
                        imageView.setImageBitmap(bitmap);
                    }
                } catch (Exception e) {
                    Log.w(TAG, "Error decoding profile picture", e);
                }
            }
        };


        VolleySingleton.getInstance(context).addToRequestQueue(byteRequest);
    }


    private static class ImageLoaderTask extends android.os.AsyncTask<byte[], Void, Bitmap> {
        private final ImageView imageView;

        public ImageLoaderTask(ImageView imageView) {
            this.imageView = imageView;
        }

        @Override
        protected Bitmap doInBackground(byte[]... data) {
            if (data == null || data.length == 0 || data[0] == null) {
                return null;
            }
            return BitmapFactory.decodeByteArray(data[0], 0, data[0].length);
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            if (result != null) {
                imageView.setImageBitmap(result);
            }
        }
    }











}
