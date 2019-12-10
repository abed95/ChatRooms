package com.abedo.chatappphp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.abedo.chatappphp.adapters.MessagingAdapter;
import com.abedo.chatappphp.models.MainResponse;
import com.abedo.chatappphp.models.Message;
import com.abedo.chatappphp.utils.Session;
import com.abedo.chatappphp.webservices.WebService;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class ChatActivity extends AppCompatActivity {

    private static final String TAG = "ChatActivity";

    private RecyclerView recyclerChat;
    private View divider;
    private ImageView imgAttachment, imgSend;
    private EditText etMessage;
    private RelativeLayout rlltTextBox;
    private LinearLayout contentChat;

    private int roomId = 0;
    private int userId = 0;
    private String username;
    private MessagingAdapter adapter;
    private List<Message> messages;


    private BroadcastReceiver messageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Message message = intent.getParcelableExtra("msgBroadcast");
            Log.e(TAG, "onReceive: "+intent.getParcelableExtra("msgBroadcast"));
            if (message != null) {
                Log.e(TAG, "onReceive: message content"+message.toString() );
                Message my = new Message();
                my.setType(message.getType());
                my.setContent(message.getContent());
                my.setRoomId(message.getRoomId());
                my.setTimestamp(message.getTimestamp());
                my.setUserId(message.getUserId());
                my.setUsername(message.getUsername());
                messages.add(my);
                adapter.notifyItemInserted(messages.size() - 1);
                recyclerChat.scrollToPosition(messages.size() - 1);
            }
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        Log.e(TAG, "onResume: "+"EnterOnResume" );
        registerReceiver(messageReceiver, new IntentFilter("UpdateChatActivity"));
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(messageReceiver);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        loadUi();

        roomId = getIntent().getExtras().getInt("room_id");


        userId = Session.getInstance().getUser().id;
        username = Session.getInstance().getUser().username;

        recyclerChat.setLayoutManager(new GridLayoutManager(this, 1));
        getMessages(roomId);

        // subscribe to topic of room (room+id)
        FirebaseMessaging.getInstance().subscribeToTopic("room" + roomId);
        Log.e(TAG, "room" + roomId);

        imgSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // if msg editText is empty return don't do any thing
                if (etMessage.getText().toString().isEmpty()) return;
                // get msg from edit text
                String msg = etMessage.getText().toString();
                // create new message
                Message message = new Message();
                // set type to 1 (text message)
                message.setType("1");
                // set room id int
                message.setRoomId(String.valueOf(roomId));
                // set user id int
                message.setUserId(String.valueOf(userId));
                // set user name
                message.setUsername(username);
                // set message content
                message.setContent(msg);
                // add message to messages list
                messages.add(message);
                // notify adapter that there is new message in this position
                adapter.notifyItemInserted(messages.size() - 1);
                // scroll to last item in recycler
                recyclerChat.scrollToPosition(messages.size() - 1);
                // set message box empty
                etMessage.setText("");
                // sent message to server
                addMessage(message);
            }
        });




    }


    private void loadUi() {
        recyclerChat = findViewById(R.id.recycler_chat);
        imgAttachment = findViewById(R.id.img_attachment);
        imgSend = findViewById(R.id.img_send);
        etMessage = findViewById(R.id.et_message);
        rlltTextBox = findViewById(R.id.rllt_text_box);
        contentChat = findViewById(R.id.content_chat);
        divider = findViewById(R.id.divider);
    }

    private void getMessages(int roomId) {
        try {
            WebService.getInstance().getApi().getMessages(roomId).enqueue(new Callback<List<Message>>() {
                @Override
                public void onResponse(Call<List<Message>> call
                        , Response<List<Message>> response) {
                    messages = response.body();
                    adapter = new MessagingAdapter(messages, ChatActivity.this);
                    recyclerChat.setAdapter(adapter);
                    recyclerChat.scrollToPosition(messages.size() - 1);

                }

                @Override
                public void onFailure(Call<List<Message>> call, Throwable t) {
                    Toast.makeText(ChatActivity.this, "Error :: " + t.getLocalizedMessage()
                            , Toast.LENGTH_LONG).show();

                }
            });
        } catch (Exception e) {
            Log.e(TAG, "getMessages: " + e.getLocalizedMessage());
        }
    }

    private void addMessage(Message message) {
        try {
            WebService.getInstance().getApi().addMessage(message).enqueue(new Callback<MainResponse>() {
                @Override
                public void onResponse(Call<MainResponse> call, Response<MainResponse> response) {
                    if (response.body().status == 0) {
                        Toast.makeText(ChatActivity.this, "Error while trying to send message", Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure(Call<MainResponse> call, Throwable t) {
                    Toast.makeText(ChatActivity.this, "Error :: " + t.getLocalizedMessage()
                            , Toast.LENGTH_LONG).show();
                }
            });
        } catch (Exception e) {
            Log.e(TAG, "addMessage: " + e.getLocalizedMessage());
        }
    }
}
