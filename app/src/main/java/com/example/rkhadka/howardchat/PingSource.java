package com.example.rkhadka.howardchat;

import android.content.Context;
import android.os.Message;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PingSource {

    public interface MessageListener {
        void onMessageReceived(List<Ping> messageList);
    }

    private static PingSource sNewsSource;

    private Context mContext;

    public static PingSource get(Context context) {
        if (sNewsSource == null) {
            sNewsSource = new PingSource(context);
        }
        return sNewsSource;
    }

    private PingSource(Context context) {
        mContext = context;
    }

    // Firebase methods for you to implement.

    public void getMessage(final MessageListener messageListener) {
        final DatabaseReference pingsRef = FirebaseDatabase.getInstance().getReference();
        DatabaseReference articleRef = pingsRef.child("messages");
        Query last50PingsQuery = articleRef.limitToLast(50);
        final List<Ping> messageList = new ArrayList<Ping>();
        last50PingsQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterable<DataSnapshot> pingSnaphots = dataSnapshot.getChildren();
                for(DataSnapshot datas : pingSnaphots){
                    Ping ping = new Ping(datas);
                    messageList.add(ping);
                }
                messageListener.onMessageReceived(messageList);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void sendMessage(Ping message) {
        final DatabaseReference pingsRef = FirebaseDatabase.getInstance().getReference();
        DatabaseReference articleRef = pingsRef.child("messages");
        DatabaseReference newPingRef = articleRef.push();
        Map<String, Object> pingValMap = new HashMap<String, Object>();
        pingValMap.put("content", message.getContent());
        pingValMap.put("fromUserName", message.getUserName());
        pingValMap.put("fromUserId", message.getUserId());
        newPingRef.setValue(pingValMap);
    }
}
