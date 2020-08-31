package com.myapplication.Fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.myapplication.Adapter.PostAdapter;
import com.myapplication.Model.Post;
import com.myapplication.instagram_app.R;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private RecyclerView recyclerViewPosts;
    private PostAdapter postAdapter;
    private List<Post> postList;
    private List<String> followingList;

    private Button camera_home;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_home, container, false);

        recyclerViewPosts=view.findViewById(R.id.recycler_view_posts);
        recyclerViewPosts.setHasFixedSize(true);

        camera_home=view.findViewById(R.id.camera_home);
        camera_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //code to start Post activity
            }
        });

        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(getContext());

        //below code makes latest tag visible on top
        linearLayoutManager.setStackFromEnd(true);
        linearLayoutManager.setReverseLayout(true);
        recyclerViewPosts.setLayoutManager(linearLayoutManager);

        postList=new ArrayList<>();
        postAdapter=new PostAdapter(getContext(),postList);
        recyclerViewPosts.setAdapter(postAdapter);

        followingList=new ArrayList<>();

        //this will add users in followingList who are being followed by current user
        checkFollowingUsers();

        return view;
    }

    private void checkFollowingUsers() {
        FirebaseDatabase.getInstance().getReference().child("follow").child(FirebaseAuth.getInstance()
                .getCurrentUser().getUid()).child("following").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                followingList.clear();

                for(DataSnapshot dataSnapshot:snapshot.getChildren()){
                    followingList.add(dataSnapshot.getKey());
                }

                readPosts();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void readPosts() {
        FirebaseDatabase.getInstance().getReference().child("Posts").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                postList.clear();

                for(DataSnapshot dataSnapshot:snapshot.getChildren()){
                    Post post=dataSnapshot.getValue(Post.class);

                    for(String id:followingList){
                        if(post.getPublisher().equals(id)){
                            postList.add(post);
                        }
                    }
                }
                postAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}