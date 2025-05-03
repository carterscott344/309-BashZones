package com.example.bz_frontend_new;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import java.util.ArrayList;
import java.util.List;


public class UserListFragment extends Fragment {


    private ListView listView;
    private TextView headerTextView;
    private UserListAdapter adapter;
    private List<UserItem> userItems;
    private String listType;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_list, container, false);


        listView = view.findViewById(R.id.userListView);
        headerTextView = view.findViewById(R.id.headerTextView);


        userItems = new ArrayList<>();
        adapter = new UserListAdapter(getContext(), userItems);
        listView.setAdapter(adapter);


        return view;
    }


    public void updateUserList(JSONArray jsonArray, String type) {
        this.listType = type;
        userItems.clear();

        if ("players".equals(type)) {
            headerTextView.setText("All Players");
        } else if ("friends".equals(type)) {
            headerTextView.setText("Friends List");
        } else if ("blocked".equals(type)) {
            headerTextView.setText("Blocked Users");
        }

        try {
            for (int i = 0; i < jsonArray.length(); i++) {
                Object item = jsonArray.get(i);
                if (item instanceof JSONObject) {
                    JSONObject userObj = (JSONObject) item;
                    long id = userObj.getLong("id");
                    String username = userObj.getString("accountUsername");

                    if ("players".equals(type) && userObj.has("isBanned")) {
                        boolean isBanned = userObj.getBoolean("isBanned");
                        userItems.add(new AdminUserItem(id, username, isBanned));
                    } else {
                        userItems.add(new UserItem(id, username));
                    }
                } else if (item instanceof Number) {
                    long id = ((Number) item).longValue();
                    userItems.add(new UserItem(id, "User " + id));
                }
            }
            adapter.notifyDataSetChanged();
            adapter.setParentFragment(this);
        } catch (JSONException e) {
            e.printStackTrace();
            if (getContext() != null) {
                Toast.makeText(getContext(), "Error parsing user data", Toast.LENGTH_SHORT).show();
            }
        }
    }


    private boolean adminMode = false;

    public void setAdminMode(boolean adminMode) {
        this.adminMode = adminMode;
        if (adapter != null) {
            adapter.setAdminMode(adminMode);
        }
    }


    public static class AdminUserItem extends UserItem {
        private boolean isBanned;

        public AdminUserItem(long id, String username, boolean isBanned) {
            super(id, username);
            this.isBanned = isBanned;
        }

        public boolean isBanned() {
            return isBanned;
        }
    }


    public String getListType() {
        return listType;
    }


    public UserListAdapter getAdapter() {
        return adapter;
    }


    public static class UserItem {
        private long id;
        private String username;


        public UserItem(long id, String username) {
            this.id = id;
            this.username = username;
        }


        public long getId() {
            return id;
        }


        public String getUsername() {
            return username;
        }
    }
}
