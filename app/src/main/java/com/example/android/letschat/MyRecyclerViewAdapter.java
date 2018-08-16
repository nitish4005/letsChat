package com.example.android.letschat;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Prasad on 19-Jan-18.
 */

public class MyRecyclerViewAdapter extends RecyclerView.Adapter<MyRecyclerViewAdapter.CustomViewHolder> {
    private ArrayList<Users> usersList;
    private Context mContext;
    public MyRecyclerViewAdapter(Context context, ArrayList<Users> UsersList) {
        this.usersList = UsersList;
        this.mContext = context;
    }

    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.users_layout, null);
        CustomViewHolder viewHolder = new CustomViewHolder(view);
        return viewHolder;
    }


    @Override
    public void onBindViewHolder(CustomViewHolder customViewHolder, int i) {
        Users users = usersList.get(i);
        if(users.getThumb_image().equals("default")){
            customViewHolder.userProfilePicture.setImageResource(R.drawable.default_pic);
        }else {
            Picasso.with(mContext).load(users.getThumb_image()).placeholder(R.drawable.default_pic).into(customViewHolder.userProfilePicture);
        }
        customViewHolder.usernameView.setText(users.getName());
        customViewHolder.userStausView.setText(users.getStatus());

    }

    @Override
    public int getItemCount() {
        return (null != usersList ? usersList.size() : 0);
    }

    class CustomViewHolder extends RecyclerView.ViewHolder {
        protected TextView usernameView,userStausView;
        protected CircleImageView userProfilePicture;


        public CustomViewHolder(View view) {
            super(view);
            usernameView = (TextView) view.findViewById(R.id.contactName);
            userStausView = (TextView) view.findViewById(R.id.contactStatus);
            userProfilePicture = (CircleImageView) view.findViewById(R.id.userProfilePic);
        }
    }
}
