package com.myapplication.instagram_app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {

    private EditText username,name,password,email,cpassword;
    private Button register;
    private TextView login_user;

    //reference to the database
    private DatabaseReference mRootRef;

    //FireBase Auth variable
    private FirebaseAuth mAuth;

    //Android ProgressDialog is a dialog box/dialog window which shows the progress of a task
    //Android Progress Dialog is almost same as ProgressBar with the exception that this is displayed as a dialog box
    ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        username=findViewById(R.id.username);
        name=findViewById(R.id.name);
        password=findViewById(R.id.password);
        cpassword=findViewById(R.id.cPassword);
        email=findViewById(R.id.email);
        register=findViewById(R.id.registerBtn);
        login_user=findViewById(R.id.login_user);

        //initializing database reference
        mRootRef= FirebaseDatabase.getInstance().getReference();

        //initializing mauth variable
        mAuth =FirebaseAuth.getInstance();

        //initializing progress dialog
        pd=new ProgressDialog(this);

        //if user is already registered and clicks textView
        //we redirect him to login activity
        login_user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(RegisterActivity.this,LoginActivity.class));
            }
        });

        //Code to register user when he had filled all credentials
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String txtUserName=username.getText().toString();
                String txtName=name.getText().toString();
                String txtEmail=email.getText().toString();
                String txtPassword=password.getText().toString();
                String txtcPassword=cpassword.getText().toString();

                //we use TextUtils to check if no value is empty
                //if password and confirm password same
                //if password not too short
                if(TextUtils.isEmpty(txtUserName) || TextUtils.isEmpty(txtName)
                        || TextUtils.isEmpty(txtPassword) || TextUtils.isEmpty(txtEmail)){
                    Toast.makeText(RegisterActivity.this, "Empty Credentials", Toast.LENGTH_SHORT).show();
                }else if(!(txtcPassword.equals(txtPassword))){
                    Toast.makeText(RegisterActivity.this, "Check password", Toast.LENGTH_SHORT).show();
                }else if(password.length()<6){
                    Toast.makeText(RegisterActivity.this, "Password too short", Toast.LENGTH_SHORT).show();
                }else{
                    registerUser(txtUserName,txtEmail,txtName,txtPassword);
                }
            }
        });
    }

    private void registerUser(final String userName, final String email, final String name, String password) {

        //as the user clicks on register btn progress dialog should start
        pd.setMessage("Please Wait...");
        pd.show();

        mAuth.createUserWithEmailAndPassword(email,password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {

            @Override
            public void onSuccess(AuthResult authResult) {
                //code to add user value to database
                HashMap<String,Object> map=new HashMap<>();
                map.put("name",name);
                map.put("email",email);
                map.put("username",userName);
                //adding id of user to map
                map.put("id", mAuth.getCurrentUser().getUid());
                map.put("bio","");
                map.put("imageUrl","default");

                //adding map to database
                mRootRef.child("Users").child(mAuth.getCurrentUser().getUid()).setValue(map)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){ //if success we redirect to MainActivity

                            //as soon as registration is successful public dialog should stop
                            pd.dismiss();

                            Toast.makeText(RegisterActivity.this,
                                    "Update Profile in settings", Toast.LENGTH_SHORT).show();

                            //code to start MainActivity
                            Intent intent=new Intent(RegisterActivity.this,MainActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                            finish();
                        }
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                //if registration is unsuccessful then also stop
                pd.dismiss();

                Toast.makeText(RegisterActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                //e.getMessage() will show the actual message of the exception
            }
        });
    }


}