package com.example.projectfinal_alpha;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
/**
 * @author Etay Sabag <itay45520@gmail.com>
 * @version 1.0
 * @since 26/10/2022
 *
 */
public class teacher_screen extends AppCompatActivity {

    ImageView pfp;
    TextView name,email,id;
    Button signout;
    GoogleSignInClient mGoogleSignInClient;
    FirebaseAuth mAuth;
    FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_screen);

        pfp = (ImageView) findViewById(R.id.pfp);
        name = (TextView) findViewById(R.id.name);
        email = (TextView) findViewById(R.id.mail);
        id = (TextView) findViewById(R.id.id);
        signout = (Button) findViewById(R.id.singout);

        mAuth = FirebaseAuth.getInstance();


        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

//        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(this);
//        if(acct != null)
//        {
//            String personName = acct.getDisplayName();
//            String personGivenName = acct.getGivenName();
//            String personFamilyName = acct.getFamilyName();
//            String personEmail = acct.getEmail();
//            String personId = acct.getId();
//            Uri personPhoto = acct.getPhotoUrl();
//            Glide.with(this).load(String.valueOf(personPhoto)).into(pfp);
//            name.setText(personName);
//            email.setText(personEmail);
//            id.setText(personId);
//
//
//
//        }
    }

    public void onStart() {
        super.onStart();
//        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);

        currentUser = mAuth.getCurrentUser();

        Glide.with(this).load(String.valueOf(currentUser.getPhotoUrl())).into(pfp);
        name.setText(currentUser.getDisplayName());
        email.setText(currentUser.getEmail());
        id.setText(currentUser.getUid());

    }

    public void singOut(View view) {
        mGoogleSignInClient.signOut()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        FirebaseAuth.getInstance().signOut();
                        Toast.makeText(teacher_screen.this,"Signed out successfully!", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                });

    }
}