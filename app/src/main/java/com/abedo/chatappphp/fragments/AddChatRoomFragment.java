package com.abedo.chatappphp.fragments;


import android.os.Bundle;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.abedo.chatappphp.MainActivity;
import com.abedo.chatappphp.R;
import com.abedo.chatappphp.models.ChatRoom;
import com.abedo.chatappphp.models.MainResponse;
import com.abedo.chatappphp.webservices.WebService;
import com.fourhcode.forhutils.FUtilsValidation;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class AddChatRoomFragment extends DialogFragment {

    private EditText etRoomName, etRoomDesc;
    private Button btnAddRoomChat;
    public static final String TAG = "AddChatRoomFragment";
    private LinearLayout con_frag;
    private RelativeLayout rrlLoad;
    private ProgressBar prgsLoadingFrag;

    public AddChatRoomFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_add_chat_room, container, false);
        etRoomName = root.findViewById(R.id.et_room_name);
        etRoomDesc = root.findViewById(R.id.et_room_desc);
        btnAddRoomChat = root.findViewById(R.id.btn_add_room);
        con_frag = root.findViewById(R.id.lnlt_body);
        rrlLoad = root.findViewById(R.id.rllt_loading);
        prgsLoadingFrag = root.findViewById(R.id.prgs_loading);
        try {
            btnAddRoomChat.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!FUtilsValidation.isEmpty(etRoomName, getString(R.string.please_enter_room_name))
                            && !FUtilsValidation.isEmpty(etRoomDesc, getString(R.string.please_enter_room_desc))) {

                        ChatRoom chatRoom = new ChatRoom();
                        chatRoom.room_name = etRoomName.getText().toString();
                        chatRoom.room_desc = etRoomDesc.getText().toString();
                        // call addChatRoom method
                        addChatRoom(chatRoom);
                    } else {
                        Log.e(TAG, "onClick: " + "room not add");
                    }
                }
            });
        }catch (Exception e){
            Log.e(TAG, "onCreateView: "+e.getMessage());
        }
            return root;
        }

        private void addChatRoom (ChatRoom chatRoom){
            // show progress laoding
            setLoadingMode();
            try {
                WebService.getInstance().getApi().addChatRoom(chatRoom).enqueue(new Callback<MainResponse>() {
                    @Override
                    public void onResponse(Call<MainResponse> call, Response<MainResponse> response) {
                        if (response.body().status == 1) {
                            Toast.makeText(getActivity(), response.body().message, Toast.LENGTH_SHORT).show();
                            // get the prarent activity and cast it to main activity because we want to call reload chat rooms method
                            MainActivity mainActivity = (MainActivity) getActivity();
                            mainActivity.reloadChatRooms();
                            dismiss();
                        } else {
                            Toast.makeText(getActivity(), response.body().message, Toast.LENGTH_SHORT).show();
                            dismiss();

                        }
                        // dismiss progressloading
                        setNormalMode();

                    }

                    @Override
                    public void onFailure(Call<MainResponse> call, Throwable t) {
                        Log.e(TAG, "Error :" + t.getLocalizedMessage());
                        dismiss();
                        setNormalMode();
                        Toast.makeText(getActivity(), "Error :" + t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }catch (Exception e){
                Log.e(TAG, "addChatRoom: "+e.getLocalizedMessage() );
            }
        }
        private void setLoadingMode () {
            rrlLoad.setVisibility(View.VISIBLE);
            con_frag.setVisibility(View.GONE);
        }

        // set body layout visible and hide loading layout
        private void setNormalMode () {
            rrlLoad.setVisibility(View.GONE);
            con_frag.setVisibility(View.VISIBLE);
        }

    }
