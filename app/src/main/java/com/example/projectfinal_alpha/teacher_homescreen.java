package com.example.projectfinal_alpha;

import static com.example.projectfinal_alpha.FBref.refGroups;
import static com.example.projectfinal_alpha.FBref.refTeachers;

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
import com.google.android.material.tabs.TabLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;

import android.text.InputFilter;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

import com.example.projectfinal_alpha.databinding.ActivityTeacherHomescreenBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Random;

/**
 * This class is the teacher's home screen of the app.
 * It shows the teacher's history, requests, and groups.
 * The teacher can create a new approval.
 */
public class teacher_homescreen extends AppCompatActivity {
    GoogleSignInClient mGoogleSignInClient;
    FirebaseAuth mAuth;
    private ActivityTeacherHomescreenBinding binding;
    private AppBarLayout toolbar;
    private FirebaseUser currentUser;

    public static Teacher currentTeacher;
    public static ArrayList<String> teacherStudentsIds = new ArrayList<String>();
    int selectedTabIndex = 2;
    TabLayout tabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityTeacherHomescreenBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


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


        mAuth = FirebaseAuth.getInstance();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        currentUser = mAuth.getCurrentUser();

        tabLayout = findViewById(R.id.tabs);


        loadTeacherData(2, true);


    }


    public void onStart() {
        super.onStart();
        loadTeacherData(tabLayout.getSelectedTabPosition(), false);
    }

    /**
     * This method loads the teacher's data from the database.
     * @param tabIndex The index of the tab that the teacher is currently on.
     * @param isFirstLoad Whether this is the first time the teacher is loading the data.
     */
    public void loadTeacherData(int tabIndex, boolean isFirstLoad) {
        refTeachers.child(currentUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    currentTeacher = (Teacher) dataSnapshot.getValue(Teacher.class);
                    if (currentTeacher.getGroups() != null) {
                        teacherStudentsIds.clear();
                        for (String groupId : currentTeacher.getGroups()) {
                            refGroups.child(groupId).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.exists()) {
                                        Group currentGroup = (Group) dataSnapshot.getValue(Group.class);
                                        if (currentGroup.getStudentsIDs() != null) {
                                            for (String studentId : currentGroup.getStudentsIDs()) {
                                                if (!teacherStudentsIds.contains(studentId)) {
                                                    teacherStudentsIds.add(studentId);
                                                }
                                            }
                                        }
                                    }
                                    if (isFirstLoad) {
                                        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(teacher_homescreen.this, getSupportFragmentManager());
                                        ViewPager viewPager = binding.viewPager;
                                        viewPager.setAdapter(sectionsPagerAdapter);
                                        TabLayout tabs = binding.tabs;
                                        tabs.setupWithViewPager(viewPager);
                                        TabLayout.Tab thirdTab = tabLayout.getTabAt(tabIndex);
                                        if (thirdTab != null) {
                                            thirdTab.select();
                                        }
                                    }
                                    Log.d("homescreenwork", "teacherStudentsIds homescreen: " + teacherStudentsIds.toString());
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                    // Handle any errors that may occur during the operation
                                }
                            });

                        }
                    } else if (isFirstLoad) {
                        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(teacher_homescreen.this, getSupportFragmentManager());
                        ViewPager viewPager = binding.viewPager;
                        viewPager.setAdapter(sectionsPagerAdapter);
                        TabLayout tabs = binding.tabs;
                        tabs.setupWithViewPager(viewPager);
                        TabLayout tabLayout = findViewById(R.id.tabs);
                        TabLayout.Tab thirdTab = tabLayout.getTabAt(tabIndex);
                        if (thirdTab != null) {
                            thirdTab.select();
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle any errors that may occur during the operation
            }
        });
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.add, menu);
        MenuItem menuItem = menu.findItem(R.id.joinGroup);
        menuItem.setTitle("Create Group");


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

        } else if (menuTitle.equals("Create Group")) {


            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("צור קבוצה");
            builder.setMessage("שם הקבוצה:");

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
                    Helper.writeGroup(groupName, currentUser);
                    loadTeacherData(tabLayout.getSelectedTabPosition(), true);
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