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


        if ("friends".equals(type)) {
            headerTextView.setText("Friends List");
        } else if ("blocked".equals(type)) {
            headerTextView.setText("Blocked Users");
        }


        try {
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject userObj = jsonArray.getJSONObject(i);
                int id = userObj.getInt("id");
                String username = userObj.getString("accountUsername");
                UserItem userItem = new UserItem(id, username);
                userItems.add(userItem);
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


    public String getListType() {
        return listType;
    }


    public UserListAdapter getAdapter() {
        return adapter;
    }


    public static class UserItem {
        private int id;
        private String username;


        public UserItem(int id, String username) {
            this.id = id;
            this.username = username;
        }


        public int getId() {
            return id;
        }


        public String getUsername() {
            return username;
        }
    }
}
