package com.imaginers.onirban.home.base;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.imaginers.onirban.home.R;

public class ProfileActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    DatabaseReference databaseUsers;
    public String mCurrentUid;
    public String username;
    public String phone;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        mAuth = FirebaseAuth.getInstance();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        mCurrentUid = currentUser.getUid();
        TextView mName , mPhone;
        mName = findViewById(R.id.textViewName);
        mPhone = findViewById(R.id.textViewPhone);

        databaseUsers = FirebaseDatabase.getInstance().getReference("huthat/");
        mAuth = FirebaseAuth.getInstance();
//        mCurrentUid = mAuth.getUid();
//        databaseUsers.child("users").child(mCurrentUid).child("name").setValue(mName.getText().toString());
//        databaseUsers.child("users").child(mCurrentUid).child("phone").setValue(mPhone.getText().toString());


    }

}