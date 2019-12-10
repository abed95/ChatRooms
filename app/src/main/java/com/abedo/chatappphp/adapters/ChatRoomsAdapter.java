package com.abedo.chatappphp.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.abedo.chatappphp.ChatActivity;
import com.abedo.chatappphp.R;
import com.abedo.chatappphp.models.ChatRoom;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * created by Abedo95 on 12/5/2019
 */
public class ChatRoomsAdapter extends
        RecyclerView.Adapter<ChatRoomsAdapter.ChatRoomHolder> {

    // define list and context
    private List<ChatRoom> chatRooms;
    private Context context;

    // constructor
    public ChatRoomsAdapter(List<ChatRoom> chatRooms, Context context) {

        this.chatRooms = chatRooms;
        this.context = context;
    }


    @NonNull
    @Override
    public ChatRoomsAdapter.ChatRoomHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View root = LayoutInflater.from(context).inflate(R.layout.row_chat_room, null, false);
        return new ChatRoomsAdapter.ChatRoomHolder(root);
    }

    @Override
    public void onBindViewHolder(ChatRoomHolder holder, int position) {
        // get room
        final ChatRoom room = chatRooms.get(position);
        // set room data for row
        holder.tvRoomName.setText(room.room_name);
        holder.tvRoomDesc.setText(room.room_desc);
        // start chat activity
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ChatActivity.class);
                intent.putExtra("room_id", Integer.parseInt(room.id));
                intent.putExtra("room_name", room.room_name);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return chatRooms.size();
    }

    class ChatRoomHolder extends RecyclerView.ViewHolder {
        // declare views using butter knife
        CircleImageView imgRoom ;
        TextView tvRoomName;
        TextView tvRoomDesc;

        public ChatRoomHolder(@NonNull View itemView) {
            super(itemView);
            imgRoom=itemView.findViewById(R.id.img_rooms);
            tvRoomName=itemView.findViewById(R.id.tv_room_name);
            tvRoomDesc=itemView.findViewById(R.id.tv_room_desc);
        }
    }


}
