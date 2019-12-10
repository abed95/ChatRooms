package com.abedo.chatappphp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.abedo.chatappphp.adapters.ChatRoomsAdapter;
import com.abedo.chatappphp.fragments.AddChatRoomFragment;
import com.abedo.chatappphp.models.ChatRoom;
import com.abedo.chatappphp.models.MainResponse;
import com.abedo.chatappphp.models.User;
import com.abedo.chatappphp.utils.Session;
import com.abedo.chatappphp.webservices.WebService;
import com.fourhcode.forhutils.FUtilsProgress;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private FUtilsProgress progress;
    private TextView tvUserName, tvLogout, tvDesc;
    private RecyclerView rcRooms;
    private RelativeLayout contentMain;
    private FloatingActionButton fab;

    private Call<List<ChatRoom>> getChatRoomsCall;
    private List<ChatRoom> chatRooms;
    private ChatRoomsAdapter adapter;

    ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            final int position = viewHolder.getAdapterPosition();
            int chatRoomID = Integer.parseInt(chatRooms.get(position).id);
            WebService.getInstance().getApi().deleteChatRoom(chatRoomID).enqueue(new Callback<MainResponse>() {
                @Override
                public void onResponse(Call<MainResponse> call, Response<MainResponse> response) {
                    if (response.body().status == 1) {
                        Toast.makeText(MainActivity.this, response.body().message, Toast.LENGTH_SHORT).show();
                        chatRooms.remove(position);
                        adapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(MainActivity.this, response.body().message, Toast.LENGTH_SHORT).show();

                    }
                }

                @Override
                public void onFailure(Call<MainResponse> call, Throwable t) {
                    Log.e(TAG, "onFailure: " + t.getLocalizedMessage());
                }
            });
        }

        @Override
        public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
            if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
                View itemView = viewHolder.itemView;
                Paint p = new Paint();
                if (dX <= 0) {
                    p.setColor(Color.parseColor("#ED1220"));
                    c.drawRect((float) itemView.getRight() + dX, (float) itemView.getTop(),
                            (float) itemView.getRight(), (float) itemView.getBottom(), p);

                }

                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        loadUi();

        progress = FUtilsProgress.newProgress(this, contentMain);
        rcRooms.setLayoutManager(new GridLayoutManager(this, 1));

        getChatRooms();

        User user = Session.getInstance().getUser();
        if (user != null) {
            tvUserName.setText("Welcome " + user.username);
            if (user.isAdmin) {
                tvDesc.setText(R.string.nice_to_meet_you_admin);
                fab.setVisibility(View.VISIBLE);

                ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
                itemTouchHelper.attachToRecyclerView(rcRooms);
            } else {
                fab.setVisibility(View.GONE);
            }
        }

        tvLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Session.getInstance().logoutAndGoToLogin(MainActivity.this);
            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddChatRoomFragment addChatRoomFragment = new AddChatRoomFragment();
                addChatRoomFragment.show(getSupportFragmentManager(), addChatRoomFragment.TAG);
            }
        });
    }

    private void loadUi() {
        tvUserName = findViewById(R.id.toolbar_title);
        tvDesc = findViewById(R.id.toolbar_desc);
        tvLogout = findViewById(R.id.toolbar_tv_logout);
        rcRooms = findViewById(R.id.recycler_chat_rooms);
        contentMain = findViewById(R.id.content_main);
        fab = findViewById(R.id.add_char_room_fab);

    }

    public void reloadChatRooms() {
        getChatRooms();
    }

    private void getChatRooms() {
        try {
            progress.showTransparentProgress();
            getChatRoomsCall = WebService.getInstance().getApi().getAllChatRooms();
            getChatRoomsCall.enqueue(new Callback<List<ChatRoom>>() {
                @Override
                public void onResponse(Call<List<ChatRoom>> call, Response<List<ChatRoom>> response) {
                    chatRooms = response.body();
                    adapter = new ChatRoomsAdapter(chatRooms, MainActivity.this);
                    rcRooms.setAdapter(adapter);
                    progress.dismiss();
                }

                @Override
                public void onFailure(Call<List<ChatRoom>> call, Throwable t) {
                    progress.dismiss();

                    Log.e(TAG, "onFailure: " + t.getLocalizedMessage());
                }
            });
        } catch (Exception e) {
            Log.e(TAG, "getChatRooms: " + e.getMessage());
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        getChatRoomsCall.cancel();
    }
}
