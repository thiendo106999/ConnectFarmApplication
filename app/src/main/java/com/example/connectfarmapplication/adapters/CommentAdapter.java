package com.example.connectfarmapplication.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.example.connectfarmapplication.R;
import com.example.connectfarmapplication.databinding.ItemCommentBinding;
import com.example.connectfarmapplication.models.Comment;
import com.example.connectfarmapplication.models.User;
import com.example.connectfarmapplication.models.UserInfo;
import com.example.connectfarmapplication.retrofit.APIUtils;
import com.example.connectfarmapplication.retrofit.DataClient;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.annotations.NotNull;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.MyViewHolder>{
    Context context;
    ArrayList<Comment> list;
    ItemCommentBinding itemCommentBinding;

    public CommentAdapter(Context context, ArrayList<Comment> list) {
        this.context = context;
        this.list = list;
    }


    @Override
    public MyViewHolder onCreateViewHolder( ViewGroup parent, int viewType) {
        itemCommentBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.item_comment, parent, false);
        return new MyViewHolder(itemCommentBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull CommentAdapter.MyViewHolder holder, int position) {
        holder.itemCommentBinding.setComment(list.get(position));
        setAvatarAndNickName(holder, list.get(position).getUser_id());
    }

    private void setAvatarAndNickName(MyViewHolder holder, String token){
        DataClient client = APIUtils.getDataClient();
        client.getUserInfo(token).enqueue(new Callback<UserInfo>() {
            @Override
            public void onResponse(Call<UserInfo> call, Response<UserInfo> response) {
                if (response.isSuccessful()) {
                    UserInfo userInfo = response.body();
                    holder.itemCommentBinding.setUser(userInfo);
                }
            }

            @Override
            public void onFailure(Call<UserInfo> call, Throwable t) {

            }
        });
    }


    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        ItemCommentBinding itemCommentBinding;
        public MyViewHolder(@NonNull @NotNull ItemCommentBinding itemCommentBinding) {
            super(itemCommentBinding.getRoot());
            this.itemCommentBinding = itemCommentBinding;
        }
    }
}