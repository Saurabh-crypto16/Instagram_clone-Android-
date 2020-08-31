package com.myapplication.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.myapplication.instagram_app.R;

import java.util.List;

public class TagAdapter extends RecyclerView.Adapter<TagAdapter.ViewHolder>{

    private Context mContext;
    List<String> mTags;
    List<String> mTagsCount;

    //constructor
    public TagAdapter(Context mContext, List<String> mTags, List<String> mTagsCount) {
        this.mContext = mContext;
        this.mTags = mTags;
        this.mTagsCount = mTagsCount;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        //LayoutInflater is one of the Android System Services that is responsible for
        // taking your XML files that define a layout, and converting them into View objects
        View view= LayoutInflater.from(mContext).inflate(R.layout.tag_item,parent,false);
        //returning value as object of below ViewHolder class
        return new TagAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.tag.setText("#"+mTags.get(position));    //this will show '#' before tag
        holder.noOfPosts.setText(mTagsCount.get(position)+" posts");     //this show display no of posts as 123 posts
    }

    @Override
    public int getItemCount() {
        return mTags.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        //components of tag_item.xml file
        public TextView tag,noOfPosts;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            //linking with xml file
            tag=itemView.findViewById(R.id.hash_tag);
            noOfPosts=itemView.findViewById(R.id.no_of_posts);
        }
    }

    //this method will be called in searchFragment
    public void filter(List<String> filterTags,List<String> filterTagsCount){
        //linking them with mTags amd mTagsCount
        this.mTags=filterTags;
        this.mTagsCount=filterTagsCount;

        notifyDataSetChanged();
    }
}
