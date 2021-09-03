package com.example.connectfarmapplication.adapters;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.connectfarmapplication.R;
import com.example.connectfarmapplication.databinding.ItemNewBinding;
import com.example.connectfarmapplication.models.Comment;
import com.example.connectfarmapplication.models.Image;
import com.example.connectfarmapplication.models.New;
import com.example.connectfarmapplication.models.User;
import com.example.connectfarmapplication.utils.Utils;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.annotations.NotNull;

import java.util.ArrayList;

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.MyViewHolder> {
    private Context context;
    private ArrayList<New> list;
    ItemNewBinding mBinding;
    ImageAdapter adapter;
    CommentAdapter commentAdapter;
    DatabaseReference databaseReference;
    public static int FRAGMENT_LOVE = 1;
    public static int FRAGMENT_NEW = 0;
    int type;
    int click = 0;


    public NewsAdapter(Context context, ArrayList<New> list, int type) {
        this.context = context;
        this.list = list;
        this.type = type;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        mBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.item_new, parent, false);

        return new MyViewHolder(mBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        holder.mBinding.setNews(list.get(position));

        getListImages(position, holder);
        String token = list.get(position).getUser_id();
        getUser(token, holder);
        getListComment(list.get(position).getId(), holder);

    }

    private void getListImages(int position, MyViewHolder holder) {
        ArrayList<Image> listImage = new ArrayList<>();
        try {
            databaseReference = FirebaseDatabase.getInstance().getReference("News").child(list.get(position).getId()).child("list_image");

            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    for (DataSnapshot s : snapshot.getChildren()) {
                        listImage.add(new Image(s.getValue(String.class)));
                    }
                    adapter = new ImageAdapter(context, listImage);
                    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context, RecyclerView.HORIZONTAL, false);
                    holder.mBinding.rcvListImage.setLayoutManager(linearLayoutManager);
                    holder.mBinding.rcvListImage.setAdapter(adapter);

                }

                @Override
                public void onCancelled(DatabaseError error) {
                    Log.e("Loi", "onCancelled: " + error.getMessage());
                }
            });

        } catch (Exception e) {
        }
        ;

    }

    private void getUser(String token, MyViewHolder holder) {

        try {
            databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(token);
            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                    User user = snapshot.getValue(User.class);

                    holder.mBinding.setUser(user);
                }

                @Override
                public void onCancelled(@NonNull @NotNull DatabaseError error) {

                }
            });

        } catch (Exception e) {
        }
    }

    private void getListComment(String idNew, MyViewHolder holder) {
        ArrayList<Comment> comments = new ArrayList<>();

        try {
            databaseReference = FirebaseDatabase.getInstance().getReference("Comments").child(idNew);

            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                    comments.clear();
                    for (DataSnapshot s : snapshot.getChildren()
                    ) {
                        Comment comment = s.getValue(Comment.class);
                        comments.add(comment);
                    }
                    commentAdapter = new CommentAdapter(context, comments);
                    commentAdapter.notifyDataSetChanged();
                    holder.mBinding.rcrComment.setAdapter(commentAdapter);
                    holder.mBinding.rcrComment.setLayoutManager(new LinearLayoutManager(context));
                    holder.mBinding.rcrComment.setHasFixedSize(true);
                    holder.mBinding.rcrComment.setNestedScrollingEnabled(false);

                }

                @Override
                public void onCancelled(@NonNull @NotNull DatabaseError error) {
                    Log.e("TAG", "onCancelled: " + error.getMessage());
                }
            });
        } catch (Exception e) {
        }


    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        ItemNewBinding mBinding;
        RecyclerView recyclerView, rc;


        public MyViewHolder(@NonNull ItemNewBinding itemView) {
            super(itemView.getRoot());

            this.mBinding = itemView;
            recyclerView = itemView.rcvListImage;
            rc = itemView.rcrComment;
            if (type == FRAGMENT_NEW) {
                itemView.heart.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        click = click == 0 ? 1 : 0;
                        if (click == 1) {
                            Glide.with(context)
                                    .load(R.drawable.heartdo)
                                    .into(mBinding.heart);
                            addToFavorite();
                        } else {
                            Glide.with(context)
                                    .load(R.drawable.heartden)
                                    .into(mBinding.heart);
                        }
                    }

                    private void addToFavorite() {
                        String token = Utils.getToken((Activity) context);
                        databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(token);

                        databaseReference.child("favorite_posts").addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                                boolean temp = true;
                                if (snapshot.exists()) {
                                    for (DataSnapshot s : snapshot.getChildren()) {
                                        if (getAdapterPosition() != -1) {
                                            if (s.getValue(String.class).equals(list.get(getAdapterPosition()).getId())) {
                                                temp = false;
                                                break;
                                            }
                                        }
                                    }
                                }
                                if (temp) {
                                    databaseReference.child("favorite_posts").push().setValue(list.get(getAdapterPosition()).getId());
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull @NotNull DatabaseError error) {

                            }
                        });
                    }
                });
            }
            if (type == FRAGMENT_LOVE) {
                Glide.with(context)
                        .load(R.drawable.heartdo)
                        .into(mBinding.heart);
            }
            itemView.comment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    click = click == 0 ? 1 : 0;
                    if (itemView.showComment.isShown()) {
                        itemView.showComment.setVisibility(View.GONE);
                    } else {
                        itemView.showComment.setVisibility(View.VISIBLE);
                        itemView.edtComment.requestFocus();

                    }
                    // getListComment(list.get(getAdapterPosition()).getId());
                }
            });

            itemView.imvComment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String content = itemView.edtComment.getText().toString();

                    if ((!content.equals(""))) {
                        databaseReference = FirebaseDatabase.getInstance().getReference("Comments");
                        Comment comment = new Comment();
                        comment.setContent(content);
                        comment.setUser_id(Utils.getToken((Activity) context));

                        databaseReference.child(list.get(getLayoutPosition()).getId()).push().setValue(comment);

                        itemView.edtComment.setText("");
                    }
                }
            });


        }
    }


}
