package com.example.rkhadka.howardchat;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class MainFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_main, container, false);

        final ListView mListView = v.findViewById(R.id.list_view);
        final Button mButton = v.findViewById(R.id.send_button);
        final EditText mEditText = v.findViewById(R.id.message_sent);

        PingSource.get(getContext()).getMessage(new PingSource.MessageListener() {
            @Override
            public void onMessageReceived(List<Ping> messageList) {
                PingArrayAdapter adapter = new PingArrayAdapter(getContext(), messageList);
                mListView.setAdapter(adapter);
                // Whenever we set a new adapter (which usually scrolls to the top of the contents), scroll to the bottom of the contents.
                mListView.setSelection(adapter.getCount() - 1);
            }
        });

        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                ConnectivityManager cm = (ConnectivityManager)getContext().getSystemService(Context.CONNECTIVITY_SERVICE);

                NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
                boolean isConnected = activeNetwork != null &&
                        activeNetwork.isConnectedOrConnecting();
                if (!isConnected) {
                    Toast.makeText(getContext(), "Message not sent", Toast.LENGTH_SHORT).show();
                    return;
                }
                else {
                    Toast.makeText(getContext(), "Succesfully sent", Toast.LENGTH_SHORT).show();
                    Ping message = new Ping(user.getDisplayName(), user.getUid(), mEditText.getText().toString());
                    PingSource.get(getContext()).sendMessage(message);
                }
                mEditText.setText("");

            }
        });

        return v;
    }

    private class PingArrayAdapter extends BaseAdapter {
        protected Context mContext;
        protected List<Ping> mPingList;
        protected LayoutInflater mLayoutInflater;
        public PingArrayAdapter(Context context, List<Ping> pingList) {
            mContext = context;
            mPingList = pingList;
            mLayoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            return mPingList.size();
        }

        @Override
        public Object getItem(int position) {
            return mPingList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final Ping ping = mPingList.get(position);
            View rowView = mLayoutInflater.inflate(R.layout.list_item, parent, false);

            TextView title = rowView.findViewById(R.id.user_name);
            title.setText(ping.getUserName());

            TextView subtitle = rowView.findViewById(R.id.message);
            subtitle.setText(ping.getContent());

            return rowView;
        }
    }
}

