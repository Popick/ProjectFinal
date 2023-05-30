package com.example.projectfinal_alpha;

import static com.example.projectfinal_alpha.FBref.refGroups;
import static com.example.projectfinal_alpha.FBref.refStudents;
import static com.example.projectfinal_alpha.Helper.addToGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


/**
 * Represents the activity for adding students to a group.
 */
public class addto_group_screen extends AppCompatActivity implements SearchView.OnQueryTextListener {
    ArrayList<String> studentnames = new ArrayList<String>();
    ArrayList<String> studentIDs = new ArrayList<String>();
    ArrayList<String> allStudentIDs = new ArrayList<String>();

    ListView listView;
    ArrayAdapter<String> adapter;
    SearchView searchView;

    ArrayList<String> selectedStudents;
    String groupID;
    ValueEventListener incomingRequestsListener;

    /**
     * Called when the activity is starting. Sets up the layout and initializes variables.
     *
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addto_group_screen);

        Intent intent = getIntent();
        groupID = intent.getStringExtra("keyID");
        Query query = refGroups.child(groupID);
        incomingRequestsListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Group tmpGroup = dataSnapshot.getValue(Group.class);


                refStudents.addListenerForSingleValueEvent(new ValueEventListener() {

                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        studentnames.clear();
                        studentIDs.clear();

                        for (DataSnapshot data : dataSnapshot.getChildren()) {

                            String stkey = (String) data.getKey();
                            if (tmpGroup.getStudentsIDs() != null) {
                                Log.d("checkContent", tmpGroup.getStudentsIDs().toString());
                                allStudentIDs = tmpGroup.getStudentsIDs();
                            }

                            if ((tmpGroup.getStudentsIDs() == null || !tmpGroup.getStudentsIDs().contains(stkey))) {
                                if (tmpGroup.getWaitingStudentsIDs() == null || !tmpGroup.getWaitingStudentsIDs().contains(stkey)) {
                                    Student stTemp = data.getValue(Student.class);
                                    studentnames.add(stTemp.getName());
                                    studentIDs.add(stkey);
                                }
                            }


                        }

                        loadStudents();

                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                        // Failed to read value
                        Log.w("failed", "Failed to read value.", error.toException());
                    }

                });

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle any errors
            }
        };
        query.addValueEventListener(incomingRequestsListener);



        listView = findViewById(android.R.id.list);
        searchView = findViewById(R.id.search_view);


        searchView.setOnQueryTextListener(this);
        searchView.setQueryHint("Search students...");


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                AlertDialog.Builder builder = new AlertDialog.Builder(addto_group_screen.this);
                builder.setTitle("הוסף תלמיד");
                builder.setMessage("האם אתה רוצה להוסיף את " + parent.getItemAtPosition(position) + " לקבוצה");
                builder.setPositiveButton("הוסף", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        addToGroup(studentIDs.get(studentnames.indexOf(parent.getItemAtPosition(position))),groupID, false);
                        searchView.setQuery("", false);
                    }
                });
                builder.setNegativeButton("בטל", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });
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
     * Loads the student names into the ListView.
     */
    public void loadStudents() {
        adapter = new ArrayAdapter<>(this, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, studentnames);
        listView.setAdapter(adapter);
    }



    /**
     * Handles the click event of the "add_students" button .
     */
    public void add_students(View view) {
        finish();
    }
}


