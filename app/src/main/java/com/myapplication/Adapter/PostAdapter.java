package com.myapplication.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hendraanggrian.appcompat.widget.SocialTextView;
import com.myapplication.Model.Post;
import com.myapplication.Model.User;
import com.myapplication.instagram_app.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder>{

    private Context mContext;
    private List<Post> mPosts;

    private FirebaseUser firebaseUser;

    //constructor
    public PostAdapter(Context mContext, List<Post> mPosts) {
        this.mContext = mContext;
        this.mPosts = mPosts;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(mContext).inflate(R.layout.post_item,parent,false);
        return new PostAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        //registering fireBaseUser variable
        firebaseUser= FirebaseAuth.getInstance().getCurrentUser();

        //getting the post from mPosts list
        Post post=mPosts.get(position);

        //displaying post
        Picasso.get().load(post.getImageUrl()).into(holder.postImage);

        //setting description
        holder.description.setText(post.getDescription());

        //accessing user branch from firebase to get the publisher from post branch
        FirebaseDatabase.getInstance().getReference().child("users").child(post.getPublisher())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        User user=snapshot.getValue(User.class);

                        //displaying profile pic in post_item view
                        if(user.getImageUrl().equals("default")){
                            holder.imageProfile.setImageResource(R.mipmap.ic_launcher);
                        }else{
                            Picasso.get().load(user.getImageUrl()).placeholder(R.mipmap.ic_launcher)
                                    .into(holder.imageProfile);
                        }

                        //displaying username in post_item view
                        holder.username.setText(user.getUsername());

                        //displaying name in post_item view
                        holder.aurthur.setText(user.getName());
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    @Override
    public int getItemCount() {
        return mPosts.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        //linking all components of post_item.xml
        public ImageView imageProfile,postImage,like,comment,save,more;
        public TextView username,noOfLikes,aurthur,noOfComments;
        SocialTextView description;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imageProfile=itemView.findViewById(R.id.profile_image);
            postImage=itemView.findViewById(R.id.post_image);
            like=itemView.findViewById(R.id.like);
            comment=itemView.findViewById(R.id.comment);
            save=itemView.findViewById(R.id.save);
            more=itemView.findViewById(R.id.more);

            username=itemView.findViewById(R.id.username);
            noOfComments=itemView.findViewById(R.id.no_of_comments);
            noOfLikes=itemView.findViewById(R.id.no_of_likes);
            aurthur=itemView.findViewById(R.id.aurthur);

            description=itemView.findViewById(R.id.description);

        }
    }
}
