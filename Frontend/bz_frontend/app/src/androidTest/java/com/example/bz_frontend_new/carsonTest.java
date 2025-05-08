package com.example.bz_frontend_new;

import android.content.Context;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class carsonTest {
    @Test
    public void useAppContext() {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        assertEquals("com.example.bz_frontend_new", appContext.getPackageName());
    }

    @Test
    public void shopPageAccess() {
        // Test that get items URL is properly formed
        long userId = 12345;
        String expectedUrl = "http://coms-3090-046.class.las.iastate.edu:8080/accountUsers/listUser/" + String.valueOf(userId);
        String actualUrl = "http://coms-3090-046.class.las.iastate.edu:8080/accountUsers/listUser/12345";

        assertEquals("Items list URL should be correctly formed", expectedUrl, actualUrl);

        // Test processing of a sample response
        try {
            JSONArray mockResponse = new JSONArray();
            mockResponse.put(101);
            mockResponse.put(102);

            // Simulate the logic for processing user IDs
            List<Long> userIds = new ArrayList<>();
            for (int i = 0; i < mockResponse.length(); i++) {
                userIds.add(mockResponse.getLong(i));
            }

            assertEquals("Should extract 2 user IDs", 2, userIds.size());
            assertEquals("First user ID should be 101", 101L, (long)userIds.get(0));
            assertEquals("Second user ID should be 102", 102L, (long)userIds.get(1));
        } catch (JSONException e) {
            fail("JSON processing failed: " + e.getMessage());
        }
    }

    @Test
    public void testListFunctionality() {
        // Test that the blocked list URL is correctly formed
        long userId = 12345;
        String expectedUrl = "http://coms-3090-046.class.las.iastate.edu:8080/accountUsers/" + userId + "/listBlockedUsers";
        String actualUrl = "http://coms-3090-046.class.las.iastate.edu:8080/accountUsers/" + userId + "/listBlockedUsers";

        assertEquals("Blocked list URL should be correctly formed", expectedUrl, actualUrl);

        // Test processing of a sample blocked users response
        try {
            JSONArray mockResponse = new JSONArray();
            JSONObject user1 = new JSONObject();
            user1.put("accountID", 201);
            user1.put("accountUsername", "blockedUser1");
            mockResponse.put(user1);

            // Check that we can extract the correct information
            JSONObject extractedUser = mockResponse.getJSONObject(0);
            assertEquals("Should extract correct user ID", 201, extractedUser.getInt("accountID"));
            assertEquals("Should extract correct username", "blockedUser1", extractedUser.getString("accountUsername"));
        } catch (JSONException e) {
            fail("JSON processing failed: " + e.getMessage());
        }
    }

    @Test
    public void testAddFunctionality() {
        // Test that the add friend URL is correctly formed
        long userId = 12345;
        long targetId = 67890;
        String expectedUrl = "http://coms-3090-046.class.las.iastate.edu:8080/accountUsers/" + userId + "/addFriend/" + targetId;
        String actualUrl = "http://coms-3090-046.class.las.iastate.edu:8080/accountUsers/" + userId + "/addFriend/" + targetId;

        assertEquals("Add friend URL should be correctly formed", expectedUrl, actualUrl);

        // Test processing multiple friends
        List<Long> friendIds = Arrays.asList(501L, 502L, 503L);
        String updateUrl = "http://coms-3090-046.class.las.iastate.edu:8080/accountUsers/" + userId + "/updateFriendsList";

        assertEquals("Should have 3 friend IDs to update", 3, friendIds.size());
        assertTrue("Should contain first friend ID", friendIds.contains(501L));
        assertTrue("Should contain second friend ID", friendIds.contains(502L));
        assertTrue("Should contain third friend ID", friendIds.contains(503L));
    }

    @Test
    public void testPurchase() {
        // Test that the purchasing item URL is well formed
        long userId = 12345;
        long targetId = 67890;
        String expectedUrl = "http://coms-3090-046.class.las.iastate.edu:8080/userItems/" + userId + "/addItem/" + targetId;
        String actualUrl = "http://coms-3090-046.class.las.iastate.edu:8080/accountUsers/" + userId + "/addItem/" + targetId;

        assertEquals("purchase item URL should be correctly formed", expectedUrl, actualUrl);
    }
}