package com.example.projectfinal_alpha;

import static com.example.projectfinal_alpha.FBref.refUsers;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class request_screen extends AppCompatActivity {
    ArrayList<String> teacherIDs = new ArrayList<String>();
    GoogleSignInClient mGoogleSignInClient;
    FirebaseAuth mAuth;
    FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_screen);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        mAuth = FirebaseAuth.getInstance();

    }

    protected void onResume() {
        super.onResume();
        currentUser = mAuth.getCurrentUser();
    }

    public void send_request(View view) {
        refUsers.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                teacherIDs.clear();
                for(DataSnapshot data : dataSnapshot.getChildren()) {
                    String str1 = (String) data.getKey();
                    String userType = dataSnapshot.child(str1).child("usertype").getValue(String.class);
                    if(userType != null && userType.equals("Teacher")){
                        Log.i("found", "Teacher");
                        teacherIDs.add(str1);

                    }else{

                    }
                }
//                TODO: add grades
                Request currentRequest = new Request(currentUser.getUid(), currentUser.getDisplayName(), "יג13", false);
                currentRequest.setCurrentTime();
                Log.i("list", teacherIDs.toString());
                for (int i=0; i<teacherIDs.size(); i++) {
                    refUsers.child(teacherIDs.get(i)).child("requests").child(currentUser.getUid()).setValue(currentRequest);
                }
                Toast.makeText(request_screen.this,"success",Toast.LENGTH_SHORT).show();
                finish();
            }



            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }
}