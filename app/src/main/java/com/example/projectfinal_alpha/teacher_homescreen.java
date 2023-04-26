package com.example.projectfinal_alpha;

import static com.example.projectfinal_alpha.FBref.refGroups;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;

import androidx.appcompat.app.AlertDialog;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;

import android.support.annotation.NonNull;
import android.text.InputFilter;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

import com.example.projectfinal_alpha.ui.main.SectionsPagerAdapter;
import com.example.projectfinal_alpha.databinding.ActivityTeacherHomescreenBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.Random;

public class teacher_homescreen extends AppCompatActivity {
    GoogleSignInClient mGoogleSignInClient;
    FirebaseAuth mAuth;
    private ActivityTeacherHomescreenBinding binding;
    private AppBarLayout toolbar;
    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityTeacherHomescreenBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager());
        ViewPager viewPager = binding.viewPager;
        viewPager.setAdapter(sectionsPagerAdapter);
        TabLayout tabs = binding.tabs;
        tabs.setupWithViewPager(viewPager);
        FloatingActionButton fab = binding.fab;

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent siCreateApproval = new Intent(teacher_homescreen.this, approval_create_screen.class);
                startActivity(siCreateApproval);
            }
        });


        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        TabLayout tabLayout = findViewById(R.id.tabs);
        TabLayout.Tab thirdTab = tabLayout.getTabAt(2);
        if (thirdTab != null) {
            thirdTab.select();
        }

        mAuth = FirebaseAuth.getInstance();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

    }
    public void onStart() {
        super.onStart();
        currentUser = mAuth.getCurrentUser();

    }
    public void writeGroup(String groupName) {
        Random random = new Random();
        StringBuilder sb = new StringBuilder(5);
        String characters = "abcdefghijklmnopqrstuvwxyz0123456789";
        for (int i = 0; i < 5; i++) {
            sb.append(characters.charAt(random.nextInt(characters.length())));
        }
        String joinCode = sb.toString();

        refGroups.orderByChild("joinCode").equalTo(joinCode).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    writeGroup(groupName);
                } else {
                    Group newGroup = new Group(currentUser.getDisplayName(), currentUser.getUid(), groupName, joinCode, false);
                    refGroups.push().setValue(newGroup);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle error
            }
        });


    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.add, menu);

        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        String menuTitle = item.getTitle().toString();
        if (menuTitle.equals("Logout")) {
            mGoogleSignInClient.signOut()
                    .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            FirebaseAuth.getInstance().signOut();
                            Toast.makeText(teacher_homescreen.this, "Signed out successfully!", Toast.LENGTH_SHORT).show();
                            Intent siMainScreen = new Intent(teacher_homescreen.this, MainActivity.class);
                            siMainScreen.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(siMainScreen);
                        }
                    });

        } else if (menuTitle.equals("Join Class")) {


            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("הכנס שם לקבוצה");

// Set up the input
            final EditText input = new EditText(this);
            input.setFilters(new InputFilter[]{new InputFilter.LengthFilter(20)});
            input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_WORDS);
            builder.setView(input);

// Set up the buttons
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String groupName = input.getText().toString();
                    writeGroup(groupName);

                    // Do something with the short name
                }
            });
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });

            AlertDialog dialog = builder.create();
            dialog.show();
//        Intent intent = new Intent(groups_screen.this, group_create_screen.class);
//        startActivity(intent);
        }

        return true;
    }


}