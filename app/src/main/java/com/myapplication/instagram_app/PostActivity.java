package com.myapplication.instagram_app;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.hendraanggrian.appcompat.socialview.Hashtag;
import com.hendraanggrian.appcompat.widget.HashtagArrayAdapter;
import com.hendraanggrian.appcompat.widget.SocialAutoCompleteTextView;
import com.theartofdev.edmodo.cropper.CropImage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PostActivity extends AppCompatActivity {

    private ImageView close,imageAdded;
    private TextView post;

    //this textView supports hashtags and mentions
    SocialAutoCompleteTextView description;

    private String imageUrl;//to get url from firebase for download

    //to store the image
    private Uri imageUri;
    //URI(Uniform resource identifier) as its name suggests is used to identify resource
    // (whether it be a page of text, a video or sound clip, a still or animated image, or a program)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        //linking with xml file
        close=findViewById(R.id.close);
        imageAdded=findViewById(R.id.image_added);
        post=findViewById(R.id.post);
        description=findViewById(R.id.description);

        //whenever user clicks on close we should go back to main activity
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(PostActivity.this,MainActivity.class));
                finish();
            }
        });

        //adding post to firebase storage
        post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //this method uploads the image
                upload();
            }
        });

        //when user clicks on post he should be directed to gallery to select image and
        // we use Android Image cropper dependency
        CropImage.activity().start(PostActivity.this);
        //above line will return an image
        //to get the image we write onActivityResult
    }

    //method to upload image to firebase storage
    private void upload() {
        final ProgressDialog pd=new ProgressDialog(this);
        pd.setMessage("Uploading...");
        pd.show();

        //if imageUri is null means no image is selected
        if(imageUri!=null){
            final StorageReference filePath= FirebaseStorage.getInstance().getReference("Posts")
                    .child(System.currentTimeMillis()+"."+getFileExtention(imageUri));
            StorageTask uploadTask=filePath.putFile(imageUri);
            uploadTask.continueWithTask(new Continuation() {
                @Override
                public Object then(@NonNull Task task) throws Exception {
                    //if file is not uploaded
                    if(!(task.isSuccessful())){
                        throw task.getException();
                    }
                    //if file is uploaded(task is successful)
                    return filePath.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {

                    //here we get the download uri variable
                    Uri downloadUri=task.getResult();
                    imageUrl=downloadUri.toString();//getting image url from firebase storage

                    //adding details to storage
                    DatabaseReference ref= FirebaseDatabase.getInstance().getReference("Posts");

                    //creating post id
                    String postId=ref.push().getKey();  //push() creates a unique id which we store in postId

                    HashMap<String,Object> map=new HashMap<>();
                    map.put("postId",postId);
                    map.put("imageUrl",imageUrl);
                    map.put("description",description.getText().toString());
                    map.put("publisher", FirebaseAuth.getInstance().getCurrentUser().getUid());//this gives uid of current user

                    //pushing values to data base
                    ref.child("postId").setValue(map);

                    //creating another database to save hashtags
                    DatabaseReference mHashTagRef=FirebaseDatabase.getInstance().getReference().child("Hashtags");
                    List<String> hashTags=description.getHashtags();  //list to store all hashtags

                    //checking if user has added any hashtag in description
                    if(!(hashTags.isEmpty())){
                        //if list is not empty we add values to database
                        for(String tag:hashTags){
                            map.clear();
                            map.put("tag",tag.toLowerCase());   //this line saves the tag
                            map.put("postId",postId);//this saves the post in which above tag was used

                            //adding tag to database
                            mHashTagRef.child(tag.toLowerCase()).child(postId).setValue(map);
                        }
                    }
                    pd.dismiss();
                    startActivity(new Intent(PostActivity.this,MainActivity.class));
                    finish();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(PostActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }else{
            Toast.makeText(this, "No image selected...", Toast.LENGTH_SHORT).show();
        }
    }

    //this method gets the extention of image file selected
    private String getFileExtention(Uri uri) {
        return MimeTypeMap.getSingleton().getExtensionFromMimeType(this.getContentResolver().getType(uri));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode==RESULT_OK){
            //below code gets the result or image
            CropImage.ActivityResult result=CropImage.getActivityResult(data);
            imageUri=result.getUri();

            //Adding image to imageview(image_added)
            imageAdded.setImageURI(imageUri);
        }else{
            //when result code is not ok
            Toast.makeText(this, "Try Again!!", Toast.LENGTH_SHORT).show();
            //moving back user to MainActivity
            startActivity(new Intent(PostActivity.this,MainActivity.class));
            finish();
        }
    }

    //this method shows available hashtags when we search hashtags while adding any image in PostActivity
    @Override
    protected void onStart() {
        super.onStart();

        //ArrayAdapter is an Android SDK class for adapting an array of objects as a datasource
        //Adapters are used by Android to treat a result set uniformly
        // whether it's from a database, file, or in-memory objects so that it can be displayed in a UI element
        final ArrayAdapter<Hashtag> hashtagAdapter=new HashtagArrayAdapter<>(getApplicationContext());

        //getting all hashtags and thier count and storing in adapter
        FirebaseDatabase.getInstance().getReference().child("Hashtags").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot: snapshot.getChildren()){
                    //adding each value to hashTagArrayAdapter
                    //Hashtag class is in Socialview
                    hashtagAdapter.add(new Hashtag(dataSnapshot.getKey(),(int)dataSnapshot.getChildrenCount()));
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        //attaching to SocialAutoCompleteTextView
        description.setHashtagAdapter(hashtagAdapter);
    }
}