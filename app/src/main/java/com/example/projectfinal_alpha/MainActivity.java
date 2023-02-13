package com.example.projectfinal_alpha;

import static com.example.projectfinal_alpha.FBref.refStudents;
import static com.example.projectfinal_alpha.FBref.refUsers;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.identity.SignInCredential;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.annotations.Nullable;
import com.google.firebase.messaging.FirebaseMessaging;

/**
 * @author Etay Sabag <itay45520@gmail.com>
 * @version 1.0
 * @since 15/10/2022
 */
public class MainActivity extends AppCompatActivity {
    private static final int RC_SIGN_IN = 1;
    String[] whichUserType = {"תלמיד", "מורה", "שומר"};
    Intent siStudent, siGuard, siTeacher, siSignUp, siNotification;
    TextView tvtest;
    SignInButton signin;
    GoogleSignInClient mGoogleSignInClient;
    FirebaseAuth mAuth;
    FirebaseUser currentUser;
    FirebaseUser user;
    
//TODO: FIX THE SCREEN FOR REGISTERED USERS, OR MAYBE JUST BLOCK ACCESS FROM GOING BACK
    
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        siStudent = new Intent(this, student_screen.class);
        siGuard = new Intent(this, guard_screen.class);
        siTeacher = new Intent(this, teacher_screen.class);
        siSignUp = new Intent(this, new_user1.class);
        siNotification = new Intent(this, notification_screen.class);

        tvtest = (TextView) findViewById(R.id.name_tv);
        signin = findViewById(R.id.sign_in_button);
        mAuth = FirebaseAuth.getInstance();


        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestIdToken(getString(R.string.default_web_client_id))
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.sign_in_button:
                        signIn();
                        break;
                }
            }
        });



    }

    public void onStart() {
        super.onStart();
//        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);

        currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);

    }



    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);


        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            String idToken = account.getIdToken();

            if (idToken != null) {
                // Got an ID token from Google. Use it to authenticate
                // with Firebase.
                AuthCredential firebaseCredential = GoogleAuthProvider.getCredential(idToken, null);
                mAuth.signInWithCredential(firebaseCredential)
                        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // Sign in success, update UI with the signed-in user's information
                                    Log.d("Working!", "signInWithCredential:success");
                                    user = mAuth.getCurrentUser();
                                    updateUI(user);

                                } else {
                                    // If sign in fails, display a message to the user.
                                    Log.w("Error!", "signInWithCredential:failure", task.getException());
                                }
                            }

                        });

            }


            // Signed in successfully, show authenticated UI.
//            startActivity(siTeacher);
        } catch (ApiException e) {
            tvtest.setText("Error!");

            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w("Error", "signInResult:failed code=" + e.getStatusCode());
            updateUInull();
        }
    }

    //    public void updateUI(GoogleSignInAccount account){
//        if (account != null) {
//            tvtest.setText(account.getEmail());
//        }}
    public void updateUI(FirebaseUser account) {
        if (account != null) {
//            if (account.getEmail().contains("@bs.amalnet.k12.il") || account.getEmail().contains("@amalnet.k12.il")){
            if (true) {
                refUsers.child(account.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            // user exists in the database

                            String userType = dataSnapshot.child("userType").getValue(String.class);
                            if (userType != null) {
                                switch (userType) {
                                    case "Student":
                                        startActivity(siStudent);
                                        break;
                                    case "Teacher":
                                        startActivity(siTeacher);
                                        break;
                                    case "Guard":
                                        startActivity(siGuard);
                                }
                            }


                        } else {
                            // user does not exist in the database
                            AlertDialog.Builder adb = new AlertDialog.Builder(MainActivity.this);
                            adb.setTitle("אנא בחר איזה סוג משתמש אתה רוצה");
                            adb.setItems(whichUserType, new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    if (i == 0) {
                                        startActivity(siSignUp);
                                    } else if (i == 1) {
                                        Teacher newuser = new Teacher(currentUser.getDisplayName(), "Teacher","0");
                                        refUsers.child(currentUser.getUid()).setValue(newuser);
                                        Toast.makeText(MainActivity.this, "google account is now set as teacher", Toast.LENGTH_SHORT).show();
                                        startActivity(siTeacher);
                                        FirebaseMessaging.getInstance().subscribeToTopic("Teaching")
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        System.out.println("Subscription successful");
                                                    }
                                                });
                                    } else {
                                        Guard newuser = new Guard(currentUser.getDisplayName(), "Guard");
                                        refUsers.child(currentUser.getUid()).setValue(newuser);
                                        Toast.makeText(MainActivity.this, "google account is now set as guard", Toast.LENGTH_SHORT).show();
                                        startActivity(siGuard);
                                    }
                                }
                            });
                            AlertDialog ad = adb.create();
                            ad.show();

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            } else {

                Toast.makeText(this, "לא מייל של אמאל", Toast.LENGTH_LONG).show();
                mGoogleSignInClient.signOut()
                        .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                FirebaseAuth.getInstance().signOut();
                            }
                        });
            }
        }
    }

    public void updateUInull() {
        Toast.makeText(MainActivity.this,"Error occurred, please contact admin", Toast.LENGTH_SHORT).show();
    }


    public void go_to_activity_student(View view) {
    }

    public void go_to_activity_notification(View view) {
        startActivity(siNotification);
    }

    public void go_to_activity_guard(View view) {
    }

    public void go_to_activity_teacher(View view) {
        startActivity(siTeacher);

    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);

        return true;
    }
    public boolean onOptionsItemSelected(MenuItem item) {
        String st = item.getTitle().toString();
        if(st.equals("Logout")){
            mGoogleSignInClient.signOut()
                    .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            FirebaseAuth.getInstance().signOut();
                            Toast.makeText(MainActivity.this,"Signed out successfully!", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    });

        }

        return true;
    }

}