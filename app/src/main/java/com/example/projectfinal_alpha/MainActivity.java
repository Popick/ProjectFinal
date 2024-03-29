package com.example.projectfinal_alpha;

import static com.example.projectfinal_alpha.FBref.refGuards;
import static com.example.projectfinal_alpha.FBref.refStudents;
import static com.example.projectfinal_alpha.FBref.refTeachers;
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
import android.text.InputFilter;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
 * The MainActivity class is the main entry point of the application. It handles user authentication
 * using Google Sign-In and redirects users to different screens based on their user type (student, teacher, or guard).
 *
 * @version 1.0
 * @since 15/10/2022
 */
public class MainActivity extends AppCompatActivity {
    private static final int RC_SIGN_IN = 1;
    String[] whichUserType = {"תלמיד", "מורה", "שומר"};
    Intent siStudent, siGuard, siTeacher, siSignUp;
    TextView tvtest;
    SignInButton signin;
    GoogleSignInClient mGoogleSignInClient;
    FirebaseAuth mAuth;
    FirebaseUser currentUser;

//TODO: FIX THE SCREEN FOR REGISTERED USERS, OR MAYBE JUST BLOCK ACCESS FROM GOING BACK

    /**
     * This method is called when the activity is starting or resuming. It checks if the user is already signed in
     * and updates the UI accordingly.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        siStudent = new Intent(this, student_screen.class);
        siGuard = new Intent(this, guard_screen.class);
        siTeacher = new Intent(this, teacher_homescreen.class);

        siStudent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        siGuard.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        siTeacher.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        siSignUp = new Intent(this, new_user1.class);

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

    /**
     * This method handles the sign-in process using Google Sign-In. It starts the sign-in intent and waits for
     * the result in the onActivityResult method.
     */
    public void onStart() {
        super.onStart();
//        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);

        currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);

    }


    /**
     * This method handles the sign-in process using Google Sign-In. It starts the sign-in intent and waits for
     * the result in the onActivityResult method.
     */
    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);

    }

    /**
     * This method is called when the sign-in activity returns a result. It handles the result and authenticates
     * the user with Firebase if the sign-in was successful.
     *
     * @param requestCode The request code passed to startActivityForResult().
     * @param resultCode  The result code returned by the child activity.
     * @param data        The intent data returned by the child activity.
     */
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

    /**
     * This method handles the sign-in result from Google Sign-In. It authenticates the user with Firebase
     * using the ID token obtained from Google Sign-In.
     *
     * @param completedTask The completed sign-in task containing the GoogleSignInAccount.
     */
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
                                    currentUser = mAuth.getCurrentUser();

                                    updateUI(currentUser);

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
            Toast.makeText(MainActivity.this, "Error occurred, please contact admin", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * This method updates the UI based on the user's authentication status. It checks the user's email
     * domain to determine the user type (student, teacher, or guard) and redirects the user to the
     * appropriate screen. If the user is not registered, it prompts the user to choose a user type
     * and enter a code for teacher or guard registration.
     *
     * @param account The FirebaseUser object representing the authenticated user.
     */
    public void updateUI(FirebaseUser account) {
        if (account != null) {
//            if (account.getEmail().contains("@bs.amalnet.k12.il") || account.getEmail().contains("@amalnet.k12.il")){
            if (true) {
                refStudents.child(account.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            // user exists in the database
                            startActivity(siStudent);

                        } else {
                            refTeachers.child(account.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.exists()) {
                                        // user exists in the database
                                        startActivity(siTeacher);

                                    } else {
                                        refGuards.child(account.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                if (dataSnapshot.exists()) {
                                                    // user exists in the database
                                                    startActivity(siGuard);

                                                } else {
                                                    // user does not exist in the database
                                                    AlertDialog.Builder adb=new AlertDialog.Builder(MainActivity.this);
                                                    adb.setTitle("אנא בחר איזה סוג משתמש אתה רוצה");
                                                    adb.setItems(whichUserType,new DialogInterface.OnClickListener(){

                                                        @Override
                                                        public void onClick(DialogInterface dialogInterface,int i){
                                                            if(i==0){
                                                                startActivity(siSignUp);
                                                            }else if(i==1){
//
                                                                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                                                                builder.setTitle("הכנס קוד");

                                                                EditText editTextCode = new EditText(MainActivity.this);
                                                                editTextCode.setInputType(InputType.TYPE_CLASS_NUMBER);
                                                                editTextCode.setHint("הכנס קוד");

                                                                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                                                                        LinearLayout.LayoutParams.MATCH_PARENT,
                                                                        LinearLayout.LayoutParams.WRAP_CONTENT);

                                                                editTextCode.setLayoutParams(layoutParams);

                                                                builder.setView(editTextCode);

                                                                builder.setPositiveButton("אישור", (dialog, which) -> {
                                                                    String enteredCode = editTextCode.getText().toString();
                                                                    if (enteredCode.equals("12345")) {
                                                                        // Code matches, navigate to a new activity
                                                                        Intent siTeacherSignUp = new Intent(MainActivity.this, new_teacher.class);
                                                                        startActivity(siTeacherSignUp);
                                                                    } else {
                                                                        Toast.makeText(MainActivity.this, "Invalid code", Toast.LENGTH_SHORT).show();
                                                                    }
                                                                });

                                                                builder.setNegativeButton("Cancel", null);

                                                                AlertDialog dialog = builder.create();
                                                                dialog.show();

                                                            }else{

                                                                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                                                                builder.setTitle("הכנס קוד");

                                                                EditText editTextCode = new EditText(MainActivity.this);
                                                                editTextCode.setInputType(InputType.TYPE_CLASS_NUMBER);
                                                                editTextCode.setHint("הכנס קוד");

                                                                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                                                                        LinearLayout.LayoutParams.MATCH_PARENT,
                                                                        LinearLayout.LayoutParams.WRAP_CONTENT);

                                                                editTextCode.setLayoutParams(layoutParams);

                                                                builder.setView(editTextCode);

                                                                builder.setPositiveButton("אישור", (dialog, which) -> {
                                                                    String enteredCode = editTextCode.getText().toString();
                                                                    if (enteredCode.equals("54321")) {
                                                                        Guard newuser = new Guard(account.getDisplayName(),"Guard");
                                                                        refGuards.child(account.getUid()).setValue(newuser);
                                                                        startActivity(siGuard);
                                                                    } else {
                                                                        Toast.makeText(MainActivity.this, "Invalid code", Toast.LENGTH_SHORT).show();
                                                                    }
                                                                });

                                                                builder.setNegativeButton("Cancel", null);

                                                                AlertDialog dialog = builder.create();
                                                                dialog.show();

                                                            }
                                                        }
                                                    });
                                                    AlertDialog ad=adb.create();
                                                    ad.show();
                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                            }
                                        });
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
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




    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);

        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        String st = item.getTitle().toString();
        if (st.equals("Logout")) {
            mGoogleSignInClient.signOut()
                    .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            FirebaseAuth.getInstance().signOut();
                            Toast.makeText(MainActivity.this, "Signed out successfully!", Toast.LENGTH_SHORT).show();
                        }
                    });

        }

        return true;
    }

}




