package com.example.projectfinal_alpha;

import static com.example.projectfinal_alpha.FBref.refApprovals;
import static com.example.projectfinal_alpha.FBref.refGroups;
import static com.example.projectfinal_alpha.FBref.refStudents;
import static com.example.projectfinal_alpha.teacher_homescreen.currentTeacher;
import static com.example.projectfinal_alpha.teacher_homescreen.teacherStudentsIds;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.collection.ArraySet;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Represents the approval creation screen.
 */
public class approval_create_screen extends AppCompatActivity implements SearchView.OnQueryTextListener {

    ArrayList<String> studentNames = new ArrayList<String>();
    ArrayList<String> studentIDs = new ArrayList<String>();
    ArrayList<Student> studentsList = new ArrayList<Student>();
    ArrayList<String> groupNames = new ArrayList<String>();
    ArrayList<String> groupIDs = new ArrayList<String>();
    ArrayList<Group> groupsList = new ArrayList<Group>();
    GoogleSignInClient mGoogleSignInClient;
    FirebaseAuth mAuth;
    FirebaseUser currentUser;
    ListView listView;
    ArrayAdapter<String> adapter;
    SearchView searchView;

    ArrayList<String> selectedStudents;
    String groupID;
    ValueEventListener incomingRequestsListener;
    boolean isStudentsSelected = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_approval_create_screen);

        mAuth = FirebaseAuth.getInstance();


        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);


        listView = findViewById(android.R.id.list);
        searchView = findViewById(R.id.search_view);


        searchView.setOnQueryTextListener(this);
        searchView.setQueryHint("Search students...");


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (isStudentsSelected) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(approval_create_screen.this);
                    builder.setTitle("לתת אישור לתלמיד?");
                    builder.setMessage("האם אתה רוצה לתת אישור ל" + parent.getItemAtPosition(position));
                    builder.setPositiveButton("אשר", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Calendar calendar = Calendar.getInstance();
                            ArrayList<Integer> hours = new ArrayList<Integer>();
                            int selectedHour = Helper.getClassNumber(Helper.getCurrentDateString());
                            hours.add(selectedHour);
                            if (selectedHour != -1 && selectedHour != 6) {
                                hours.add(selectedHour + 1);
                            }


                            Approval stuApproval = new Approval(
                                    Helper.getCurrentDateString(),
                                    currentUser.getDisplayName(),
                                    currentUser.getUid(),
                                    calendar.get(Calendar.DAY_OF_WEEK), hours,
                                    studentIDs.get(studentNames.indexOf(parent.getItemAtPosition(position))),
                                    null,
                                    null,
                                    Helper.getNextWeekDateString(),
                                    true, false);

                            DatabaseReference currentApprovalRef = refApprovals.push();
                            currentApprovalRef.setValue(stuApproval);

                            if (studentsList.get(studentNames.indexOf(parent.getItemAtPosition(position))).getApprovalID() == null) {
                                studentsList.get(studentNames.indexOf(parent.getItemAtPosition(position))).setApprovalID(new ArrayList<String>());
                            }
                            studentsList.get(studentNames.indexOf(parent.getItemAtPosition(position))).getApprovalID().add(currentApprovalRef.getKey());
                            refStudents.child(studentIDs.get(studentNames.indexOf(parent.getItemAtPosition(position)))).child("approvalID").setValue(studentsList.get(studentNames.indexOf(parent.getItemAtPosition(position))).getApprovalID());
//                            refStudents.child(studentIDs.get(studentNames.indexOf(parent.getItemAtPosition(position)))).child("approvalID").setValue(currentApprovalRef.getKey());

                        }
                    });
                    builder.setNegativeButton("בטל", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    AlertDialog dialog = builder.create();
                    dialog.show();
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(approval_create_screen.this);
                    builder.setTitle("לתת אישור לקבוצה?");
                    builder.setMessage("האם אתה רוצה לתת אישור לקבוצה " + parent.getItemAtPosition(position));
                    builder.setPositiveButton("אשר", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Calendar calendar = Calendar.getInstance();
                            ArrayList<Integer> hours = new ArrayList<Integer>();
                            int selectedHour = Helper.getClassNumber(Helper.getCurrentDateString());
                            hours.add(selectedHour);
                            if (selectedHour != -1 && selectedHour != 6) {
                                hours.add(selectedHour + 1);
                            }
                            Approval stuApproval = new Approval(
                                    Helper.getCurrentDateString(),
                                    currentUser.getDisplayName(),
                                    currentUser.getUid(),
                                    calendar.get(Calendar.DAY_OF_WEEK), hours,
                                    null,
                                    groupIDs.get(groupNames.indexOf(parent.getItemAtPosition(position))),
                                    null,
                                    Helper.getNextWeekDateString(),
                                    true, false);

                            DatabaseReference currentApprovalRef = refApprovals.push();
                            currentApprovalRef.setValue(stuApproval);
                            refGroups.child(groupIDs.get(groupNames.indexOf(parent.getItemAtPosition(position)))).child("approvalID").setValue(currentApprovalRef.getKey());
                        }
                    });
                    builder.setNegativeButton("בטל", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }

            }
        });
    }

    public void onStart() {
        super.onStart();
        currentUser = mAuth.getCurrentUser();
        loadStudents(null);
    }

    @Override
    public boolean onQueryTextSubmit(String s) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String s) {
        adapter.getFilter().filter(s);
        return false;
    }


    /**
     * Loads the students into the list view.
     */
    public void loadStudents(View view) {
        isStudentsSelected = true;
        refStudents.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                studentNames.clear();
                studentIDs.clear();
                studentsList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    if (teacherStudentsIds.contains(snapshot.getKey())) {
                        Student tempStu = snapshot.getValue(Student.class);
                        studentNames.add(tempStu.getName());
                        studentIDs.add(snapshot.getKey());
                        studentsList.add(tempStu);
                    }
                }

                // Load student names into ListView
                adapter = new ArrayAdapter<>(approval_create_screen.this,
                        android.R.layout.simple_list_item_1, studentNames);
                listView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle error
            }

        });
    }


    /**
     * Loads the groups into the list view.
     */
    public void loadGroups(View view) {
        isStudentsSelected = false;
        refGroups.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                groupNames.clear();
                groupIDs.clear();
                groupsList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    if (currentTeacher.getGroups().contains(snapshot.getKey())) {
                        Group tempGrp = snapshot.getValue(Group.class);
                        groupNames.add(tempGrp.getGroupName());
                        groupIDs.add(snapshot.getKey());
                        groupsList.add(tempGrp);
                    }
                }

                adapter = new ArrayAdapter<>(approval_create_screen.this,
                        android.R.layout.simple_list_item_1, groupNames);
                listView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle error
            }

        });
    }

    /**
     * Handles the click event of the "Add students" button.
     */
    public void add_students(View view) {
        finish();
    }
}




