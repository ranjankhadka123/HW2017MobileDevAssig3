package com.example.rkhadka.howardchat;

import android.os.Message;

import com.google.firebase.database.DataSnapshot;

public class Ping {
    private String mUserName;
    private String mUserId;
    private String mContent;

    public Ping(String userName, String userId, String content) {
        mUserName = userName;
        mUserId = userId;
        mContent = content;
        // Note we do not generate a timestamp...that's only generated by the server.
    }

    public Ping(DataSnapshot pingSnapshot) {
        mUserName = pingSnapshot.child("fromUserName").getValue(String.class);
        mUserId = pingSnapshot.child("fromUserId").getValue(String.class);
        mContent= pingSnapshot.child("content").getValue(String.class);
    }

    public String getUserName() {
        return mUserName;
    }

    public String getContent() {
        return mContent;
    }

    public String getUserId() {
        return mUserId;
    }
}