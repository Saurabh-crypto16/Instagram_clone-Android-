package com.myapplication.instagram_app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.myapplication.Fragments.HomeFragment;
import com.myapplication.Fragments.NotificationFragment;
import com.myapplication.Fragments.ProfileFragment;
import com.myapplication.Fragments.SearchFragment;

public class MainActivity extends AppCompatActivity {

    //Represents a standard bottom navigation bar for application
    private BottomNavigationView bottomNavigationView;

    //A Fragment represents a behavior or a portion of user interface in a FragmentActivity
    //You can combine multiple fragments in a single activity
    //to build a multi-pane UI and reuse a fragment in multiple activities
    private Fragment selectorFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //linking with xml file
        bottomNavigationView=findViewById(R.id.bottom_navigation);

        //below code works when an item from bottom navigation bar is clicked
        //fragments are opened when they are clicked
        bottomNavigationView.setOnNavigationItemReselectedListener(new BottomNavigationView.OnNavigationItemReselectedListener() {
            @Override
            public void onNavigationItemReselected(@NonNull MenuItem item) {
                switch(item.getItemId()){
                    case R.id.nav_home:
                        selectorFragment=new HomeFragment();
                        break;

                    case R.id.nav_search:
                        selectorFragment=new SearchFragment();
                        break;

                    case R.id.nav_profile:
                        selectorFragment=new ProfileFragment();
                        break;

                    case R.id.nav_add:
                        selectorFragment=null;
                        //here we start our PostActivity
                        startActivity(new Intent(MainActivity.this,PostActivity.class));
                        break;

                    case R.id.nav_heart:
                        selectorFragment=new NotificationFragment();
                        break;
                }
                if(selectorFragment!=null){
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,selectorFragment).commit();
                }
            }
        });

        //below code starts HomeFragment by default
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new HomeFragment()).commit();
    }
}