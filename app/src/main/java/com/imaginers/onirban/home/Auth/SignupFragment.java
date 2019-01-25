package com.imaginers.onirban.home.Auth;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.imaginers.onirban.home.MainActivity;
import com.imaginers.onirban.home.R;

import java.util.HashMap;

/**
 * A simple {@link Fragment} subclass.
 */
public class SignupFragment extends Fragment {

    Button signupButton;
    EditText mName , mPhone;
    private FirebaseAuth mAuth;
    DatabaseReference databaseUsers;
    String mCurrentUid;


    public SignupFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        if (activity != null) {
            activity.getSupportActionBar().hide();
        }


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_signup, container, false);



        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mName = view.findViewById(R.id.nameET);
        mPhone = view.findViewById(R.id.phoneET);

        // [START initialize_auth]
        mAuth = FirebaseAuth.getInstance();
        // [END initialize_auth]
        FirebaseUser currentUser = mAuth.getCurrentUser();
        mCurrentUid = currentUser.getUid();
        signupButton = view.findViewById(R.id.register_button);
        if(currentUser!=null) {
            signupButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    register();
                }
            });
        }
    }

    private boolean validateForm() {
        boolean valid = true;

        String nameData = mName.getText().toString();
        if (TextUtils.isEmpty(nameData)) {
            mName.setError("Required.");
            valid = false;
        } else {
            mName.setError(null);
        }

        String phoneData = mPhone.getText().toString();
        if (TextUtils.isEmpty(phoneData)) {
            mPhone.setError("Required.");
            valid = false;
        } else {
            mPhone.setError(null);
        }

        return valid;
    }

    public void register(){
        if (!validateForm()) {
            return;
        }
        HashMap<String, Object> userDataMap = new HashMap<>();
        userDataMap.put("name", mName.getText().toString());
        userDataMap.put("phone", mPhone.getText().toString());

        databaseUsers = FirebaseDatabase.getInstance().getReference("huthat/");
        mAuth = FirebaseAuth.getInstance();
        mCurrentUid = mAuth.getUid();
        if(mCurrentUid!=null) {
            databaseUsers.child("users").child(mCurrentUid).setValue(userDataMap,
                    new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                    if (databaseError != null) {
                        System.out.println("Data could not be saved " + databaseError.getMessage());
                    } else {
                        Intent intent = new Intent(getContext(), MainActivity.class);
                        startActivity(intent);
                    }
                }
            });
        }
    }
}

