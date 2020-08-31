package com.myapplication.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.myapplication.Model.User;
import com.myapplication.instagram_app.R;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHoler> {

    Context mContext;
    List<User> mUsers;

    boolean isFragment;

    public UserAdapter(Context mContext, List<User> mUsers, boolean isFragment) {
        this.mContext = mContext;
        this.mUsers = mUsers;
        this.isFragment = isFragment;
    }

    FirebaseUser firebaseUser;

    @NonNull
    @Override
    public ViewHoler onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(mContext).inflate(R.layout.user_item,parent,false);
        return new UserAdapter.ViewHoler(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHoler holder, int position) {
        firebaseUser= FirebaseAuth.getInstance().getCurrentUser();

        final User user=mUsers.get(position);
        holder.btnFollow.setVisibility(View.VISIBLE);
        holder.username.setText(user.getUsername());
        holder.name.setText(user.getName());

        //loading image we use Picasso (we can also use Glide)
        Picasso.get().load(user.getImageUrl()).placeholder(R.mipmap.ic_launcher).into(holder.imageProfile);

        isFollow(user.getId(),holder.btnFollow);

        if(user.getId().equals(firebaseUser.getUid())){
            holder.btnFollow.setVisibility(View.GONE);
        }

        //to send a follow req to user when followBtn is clicked
        holder.btnFollow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(holder.btnFollow.getText().toString().equals("follow")){
                    FirebaseDatabase.getInstance().getReference().child("Follow").child(firebaseUser.getUid())
                            .child("following").child(user.getId()).setValue(true);
                    //above code will create a database which will have a child "Follow" which
                    //will hav another child as UserId whose branch will be "following"
                    //inside which Uid will be stored with boolean true indicating user is being followed

                    FirebaseDatabase.getInstance().getReference().child("follow").child(user.getId())
                            .child("followers").child(firebaseUser.getUid()).setValue(true);
                    //this will create a database which will store no of followers
                }else{
                    //if text on btnFollow is not follow then user is already being followed
                    //hence if user clicks this btn he should be able to unfollow

                    //here we just need to remove the values stored in the above if from Database
                    FirebaseDatabase.getInstance().getReference().child("Follow").child(firebaseUser.getUid())
                            .child("following").child(user.getId()).removeValue();

                    FirebaseDatabase.getInstance().getReference().child("follow").child(user.getId())
                            .child("followers").child(firebaseUser.getUid()).removeValue();
                }
            }
        });
    }

    private void isFollow(final String id, final Button btnFollow) {
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference().child("follow")
                .child(firebaseUser.getUid()).child("following");
        reference.addValueEventListener(new ValueEventListener() {
            //below code changes text of btnFollow from follow to following
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.child(id).exists()){
                    btnFollow.setText("following");
                }else {
                    btnFollow.setText("follow");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return mUsers.size();
    }

    public class ViewHoler extends RecyclerView.ViewHolder{

        public CircleImageView imageProfile;
        public TextView username,name;
        public Button btnFollow;


        public ViewHoler(@NonNull View itemView) {
            super(itemView);

            //linking with xml
            imageProfile=itemView.findViewById(R.id.image_profile);
            username=itemView.findViewById(R.id.username);
            name=itemView.findViewById(R.id.fullname);
            btnFollow=itemView.findViewById(R.id.button_follow);
        }
    }
}
