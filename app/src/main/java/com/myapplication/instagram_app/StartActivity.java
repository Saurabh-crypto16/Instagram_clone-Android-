package com.myapplication.instagram_app;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.drawable.Animatable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.google.firebase.auth.FirebaseAuth;

public class StartActivity extends AppCompatActivity {

    private ImageView icon_image;
    private Button login;
    private Button register;
    private LinearLayout linear_layout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        //Linking to xml file
        icon_image=findViewById(R.id.icon);
        linear_layout=findViewById(R.id.linerar_layout);
        login=findViewById(R.id.login);
        register=findViewById(R.id.register);

        //below code makes an animation of icon moving up when app starts and main content of
        //start screen is displayed after that
        linear_layout.animate().alpha(0f).setDuration(10);
        TranslateAnimation animation=new TranslateAnimation(0,0,0,-1000);
        animation.setDuration(1000);
        animation.setFillAfter(false);
        animation.setAnimationListener(new MyAnimationListener());
        icon_image.startAnimation(animation);

        //code when register button is clicked
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //a flag is just a integer which is power of two in binary, flags look like this 1, 10, 100, 1000, etc
                //Intents are used to launch activities on Android
                //You can set flags that control the task that will contain the activity
                //Flags exist to create a new activity, use an existing activity
                // or bring an existing instance of an activity to the front
                startActivity(new Intent(StartActivity.this,RegisterActivity.class)
                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP));
            }
        });

        //code when login button is clicked
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(StartActivity.this,LoginActivity.class)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP));
            }
        });

    }
    private class MyAnimationListener implements Animation.AnimationListener{

        @Override
        public void onAnimationStart(Animation animation) {

        }

        @Override
        public void onAnimationEnd(Animation animation) {
            icon_image.clearAnimation();
            icon_image.setVisibility(View.INVISIBLE);
            linear_layout.animate().alpha(1f).setDuration(1000);
        }

        @Override
        public void onAnimationRepeat(Animation animation) {

        }
    }

    //this code keeps us logged in until we log out
    /**/@Override
    protected void onStart() {
        super.onStart();

        //code
        if(FirebaseAuth.getInstance().getCurrentUser() != null){
            startActivity(new Intent(StartActivity.this,MainActivity.class));
            finish();
        }
    }//*/
}