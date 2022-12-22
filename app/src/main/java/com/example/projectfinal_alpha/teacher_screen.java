package com.example.projectfinal_alpha;

import static com.example.projectfinal_alpha.FBref.refTeachers;
import static com.example.projectfinal_alpha.FBref.refUsers;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * @author Etay Sabag <itay45520@gmail.com>
 * @version 1.0
 * @since 26/10/2022
 *
 */
public class teacher_screen extends AppCompatActivity implements AdapterView.OnItemClickListener {

    ImageView pfp;
    TextView name,email,id;
    Button signout;
    GoogleSignInClient mGoogleSignInClient;
    FirebaseAuth mAuth;
    FirebaseUser currentUser;
    ArrayList<String> stuRequestID = new ArrayList<String>();
    ArrayList<String> stuRequestHeadLine = new ArrayList<String>();
    ArrayList<Request> stuRequest = new ArrayList<Request>();
    ListView requestsListView;
    ValueEventListener teacherListener;
    boolean isallowedflipper = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_screen);

        requestsListView = (ListView) findViewById(R.id.requests_list_view);
        requestsListView.setOnItemClickListener (this);
//        pfp = (ImageView) findViewById(R.id.pfp);
//        name = (TextView) findViewById(R.id.name);
//        email = (TextView) findViewById(R.id.mail);
//        id = (TextView) findViewById(R.id.id);
//        signout = (Button) findViewById(R.id.singout);

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
        waitForRequest();
//        Glide.with(this).load(String.valueOf(currentUser.getPhotoUrl())).into(pfp);
//        name.setText(currentUser.getDisplayName());
//        email.setText(currentUser.getEmail());
//        id.setText(currentUser.getUid());

    }

    public void onPause() {
        super.onPause();
        if (teacherListener!=null) {
            refTeachers.removeEventListener(teacherListener);
        }

    }


    public void waitForRequest(){

        Query query = refUsers.child(currentUser.getUid()).child("requests");
        teacherListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dS) {
                stuRequestID.clear();
                stuRequest.clear();
                stuRequestHeadLine.clear();
                for(DataSnapshot data : dS.getChildren()) {
                    String str1 = (String) data.getKey();
                    stuRequestID.add(str1);

                    Request rqTemp = data.getValue(Request.class);
                    stuRequest.add(rqTemp);

                    stuRequestHeadLine.add(rqTemp.getName()+" "+rqTemp.getGrade()+" "+rqTemp.getTimeStamp());
                }
                ArrayAdapter<String> adp = new ArrayAdapter<String>(teacher_screen.this, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, stuRequestHeadLine);
                requestsListView.setAdapter(adp);

            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        query.addValueEventListener(teacherListener);

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
                            Toast.makeText(teacher_screen.this,"Signed out successfully!", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    });

        }

        return true;
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        Log.d("the item is","pos:"+i+" the id belongs to "+ stuRequestID.get(i));
        refUsers.child(stuRequestID.get(i)).child("allowed").setValue(isallowedflipper);
        isallowedflipper = !isallowedflipper;
    }

    public void go_to_create_approval(View view) {
        Intent intent = new Intent(teacher_screen.this, approval_create_screen.class);

    }
}
