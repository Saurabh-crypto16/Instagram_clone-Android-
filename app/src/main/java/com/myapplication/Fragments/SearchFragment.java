package com.myapplication.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.hendraanggrian.appcompat.widget.SocialAutoCompleteTextView;
import com.myapplication.Adapter.TagAdapter;
import com.myapplication.Adapter.UserAdapter;
import com.myapplication.Model.User;
import com.myapplication.instagram_app.R;

import java.util.ArrayList;
import java.util.List;


public class SearchFragment extends Fragment {

    private RecyclerView recyclerView;
    private SocialAutoCompleteTextView searchBar;
    private List<com.myapplication.Model.User> mUsers;  //list to be sent to user Adapter
    private UserAdapter userAdapter;

    //for tag search
    private RecyclerView recyclerViewTags;
    private List<String> mHashTags;
    private List<String> mHashTagsCount;
    private TagAdapter tagAdapter;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_search, container, false);

        //linking recyclerView
        recyclerView=view.findViewById(R.id.recycler_view_users);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        recyclerViewTags=view.findViewById(R.id.recycler_view_tags);
        recyclerViewTags.setHasFixedSize(true);
        recyclerViewTags.setLayoutManager(new LinearLayoutManager(getContext()));

        mHashTags=new ArrayList<>();
        mHashTagsCount=new ArrayList<>();
        tagAdapter=new TagAdapter(getContext(),mHashTags,mHashTagsCount);
        recyclerViewTags.setAdapter(tagAdapter);    //linking tagAdapter with recycler view

        mUsers=new ArrayList<>();

        userAdapter=new UserAdapter(getContext(),mUsers,true);

        //setting adapter
        recyclerView.setAdapter(userAdapter);

        //this method adds users to mUsers list
        readUsers();

        //this method reads all available tags
        readTags();

        //linking searchBar
        searchBar=view.findViewById(R.id.search_bar);

        //when searchBar is clicked to search user
        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                searchUser(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {
                filter(editable.toString());    //this is used in search feature
            }
        });

        return view;
    }

    private void readTags() {
        //we make all tags available and store it in mHashTags and thier count in mHashTagCount
        FirebaseDatabase.getInstance().getReference().child("Hashtags")
                .addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mHashTags.clear();
                mHashTagsCount.clear();

                //for loop for all available tags
                for(DataSnapshot dataSnapshot: snapshot.getChildren()){
                    mHashTags.add(dataSnapshot.getKey());
                    mHashTagsCount.add(dataSnapshot.getChildrenCount()+""); //converting to string by ""
                    // since getChildrenCount() returns long value
                }
                tagAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void readUsers() {
        //valueEventListener() is used to receive events about data changes at a location
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference().child("Users");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //below if is true only if user is not searching about anything
                if(TextUtils.isEmpty(searchBar.getText().toString())){
                    mUsers.clear();
                    for(DataSnapshot dataSnapshot: snapshot.getChildren()){
                        //getting value
                        com.myapplication.Model.User user=dataSnapshot.getValue(User.class);
                        mUsers.add(user);
                    }
                    userAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    //this method is used to search user in database by typing in search toolbar
    private void searchUser(String s){  //s is the value entered by user in search bar

        //we use Query which is provided by firebase
        Query query=FirebaseDatabase.getInstance().getReference().child("Users")
                .orderByChild("username").startAt(s).endAt(s+"\uf8ff");    //"uf8ff is the regex value
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mUsers.clear();
                //adding user values to list
                for (DataSnapshot dataSnapshot: snapshot.getChildren()){
                    User user=dataSnapshot.getValue(User.class);
                    mUsers.add(user);
                }
                userAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    //this method provides search functionality in tags
    private void filter(String text){
        List<String> mSearchTags=new ArrayList<>();
        List<String> mSearchTagsCount=new ArrayList<>();

        for(String s:mHashTags){
            if(s.toLowerCase().contains(text.toLowerCase())){
                //if the tag exists we display by adding it to mSearchTags
                mSearchTags.add(s);
                mSearchTagsCount.add(mHashTagsCount.get(mHashTags.indexOf(s)));
            }
        }

        tagAdapter.filter(mSearchTags,mSearchTagsCount);    //this filter is in TagAdapter class
    }
}