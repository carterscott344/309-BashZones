package com.example.bz_frontend_new;


import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;


import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;


import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class UserListAdapter extends ArrayAdapter<UserListFragment.UserItem> {


    private static final String BASE_URL = "http://coms-3090-046.class.las.iastate.edu:8080";
    private static final String TAG = "UserListAdapter";
    private Context context;
    private List<UserListFragment.UserItem> userItems;
    private UserListFragment parentFragment;


    public UserListAdapter(Context context, List<UserListFragment.UserItem> userItems) {
        super(context, 0, userItems);
        this.context = context;
        this.userItems = userItems;


        // Find the parent fragment
        if (context instanceof ProfilePage) {
            ProfilePage activity = (ProfilePage) context;
            FragmentManager fm = activity.getSupportFragmentManager();
            if (fm.findFragmentById(R.id.fragmentContainer) instanceof UserListFragment) {
                this.parentFragment = (UserListFragment) fm.findFragmentById(R.id.fragmentContainer);
            }
        }
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
        Button removeButton = convertView.findViewById(R.id.removeButton);


        usernameTextView.setText(userItem.getUsername());


        ProfilePage activity = (ProfilePage) context;
        long userId = activity.getUserId();


        removeButton.setOnClickListener(v -> {
            if (parentFragment != null) {
                String listType = parentFragment.getListType();
                if ("friends".equals(listType)) {
                    removeFriend(userId, userItem.getId());
                } else if ("blocked".equals(listType)) {
                    unblockUser(userId, userItem.getId());
                }
            } else {
                View rootView = parent.getRootView();
                TextView headerTextView = rootView.findViewById(R.id.headerTextView);
                if (headerTextView != null) {
                    String headerText = headerTextView.getText().toString();
                    if (headerText.contains("Friends")) {
                        removeFriend(userId, userItem.getId());
                    } else if (headerText.contains("Blocked")) {
                        unblockUser(userId, userItem.getId());
                    }
                }
            }
        });


        return convertView;
    }


    private void removeFriend(long userId, long targetId) {
        String url = BASE_URL + "/accountUsers/" + userId + "/removeFriend/" + targetId;
        performAction(url, "Friend removed successfully");
    }


    private void unblockUser(long userId, long targetId) {
        String url = BASE_URL + "/accountUsers/" + userId + "/removeBlockedUser/" + targetId;
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
    public void setParentFragment(UserListFragment fragment) {
        this.parentFragment = fragment;
    }
}
