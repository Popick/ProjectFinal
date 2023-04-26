package com.example.projectfinal_alpha;

import static com.example.projectfinal_alpha.FBref.refGroups;
import static com.example.projectfinal_alpha.FBref.refRequests;
import static com.example.projectfinal_alpha.FBref.refStudents;
import static com.example.projectfinal_alpha.Helper.addToGroup;
import static com.example.projectfinal_alpha.Helper.removeFromGroup;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class group_view extends AppCompatActivity implements AdapterView.OnItemClickListener {
    String groupID;
    TextView groupNameHeader;
    TextView groupCodeHeader;
    ListView insideLV;
    ListView waitingLV;
    ArrayList<String> inStudentNames = new ArrayList<String>();
    ArrayList<String> waitStudentNames = new ArrayList<String>();;
    Group selectedGroup;
    ValueEventListener incomingRequestsListener;
    


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_view);

        Intent intent = getIntent();
        groupID = intent.getStringExtra("keyID");

        insideLV = (ListView) findViewById(R.id.insideLV);
        waitingLV = (ListView) findViewById(R.id.waitingLV);
        insideLV.setOnItemClickListener(this);
        waitingLV.setOnItemClickListener(this);


        groupNameHeader = (TextView) findViewById(R.id.groupNameTV);
        groupCodeHeader = (TextView) findViewById(R.id.groupCodeTV);


        groupCodeHeader.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("text", groupCodeHeader.getText().toString().split(": ")[1]);
                clipboard.setPrimaryClip(clip);
                Toast.makeText(getApplicationContext(), "Code copied to clipboard", Toast.LENGTH_SHORT).show();
                return true;
            }
        });


        Query query = refGroups.child(groupID);
        incomingRequestsListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                inStudentNames.clear();
                waitStudentNames.clear();

                selectedGroup = dataSnapshot.getValue(Group.class);
                if (selectedGroup != null) {
                    groupNameHeader.setText(selectedGroup.getGroupName());
                    groupCodeHeader.setText("קוד כיתה: " + selectedGroup.getJoinCode());

                    if (selectedGroup.getStudentsIDs() != null) {
                        Log.d("checkLists", selectedGroup.getStudentsIDs().toString());

                        refStudents.addListenerForSingleValueEvent(new ValueEventListener() {

                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                for (DataSnapshot data : dataSnapshot.getChildren()) {
                                    String stkey = (String) data.getKey();
                                    Student stTemp = data.getValue(Student.class);

                                    if (selectedGroup.getStudentsIDs().contains(stkey)) {
                                        Log.d("whos first?", stTemp.getName());
                                        int index = selectedGroup.getStudentsIDs().indexOf(stkey);
                                        if (index >= 0 && index < inStudentNames.size()) {
                                            inStudentNames.add(index, stTemp.getName());
                                        } else {
                                            inStudentNames.add(stTemp.getName());
                                        }
                                    }
                                }

                                ArrayAdapter<String> adapterIn = new ArrayAdapter<String>(group_view.this, android.R.layout.simple_list_item_1, inStudentNames);
                                insideLV.setAdapter(adapterIn);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                // Handle any errors
                            }
                        });
                    } else {
                        insideLV.setAdapter(null);
                    }
                    if (selectedGroup.getWaitingStudentsIDs() != null) {
                        refStudents.addListenerForSingleValueEvent(new ValueEventListener() {

                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                for (DataSnapshot data : dataSnapshot.getChildren()) {
                                    String stkey = (String) data.getKey();
                                    Student stTemp = data.getValue(Student.class);

                                    if (selectedGroup.getWaitingStudentsIDs().contains(stkey)) {
                                        waitStudentNames.add(stTemp.getName());
                                    }
                                }
                                ArrayAdapter<String> adapterWait = new ArrayAdapter<String>(group_view.this, android.R.layout.simple_list_item_1, waitStudentNames);
                                waitingLV.setAdapter(adapterWait);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                // Handle any errors
                            }
                        });
                    } else {
                        waitingLV.setAdapter(null);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle any errors
            }
        };

        query.addValueEventListener(incomingRequestsListener);

    }

    @Override
    protected void onResume() {
        super.onResume();
        loadGroup();
    }

    public void loadGroup() {


    }

    public void go_to_add_students(View view) {
        Intent siAddTo = new Intent(this, addto_group_screen.class);
        siAddTo.putExtra("keyID", groupID);
        startActivity(siAddTo);
    }
    // TODO:fix the searching and selection error


    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        if (adapterView == insideLV) {


            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(inStudentNames.get(i));
            builder.setMessage("would you like to remove this student? " + inStudentNames.get(i));

            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });

            builder.setPositiveButton("Remove", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
//                    selectedGroup.getStudentsIDs().remove(i);
                    removeFromGroup(selectedGroup.getStudentsIDs().get(i),groupID);
//                    refGroups.child(groupID).child("studentsIDs").setValue(selectedGroup.getStudentsIDs());
                    dialog.dismiss();
                }
            });

            AlertDialog dialog = builder.create();
            dialog.show();
        } else if (adapterView == waitingLV) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(waitStudentNames.get(i));
            builder.setMessage("would you like to add this student?");

            builder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
//                    if (selectedGroup.getStudentsIDs() == null) {
//                        selectedGroup.setStudentsIDs(new ArrayList<String>());
//                    }
//                    selectedGroup.getStudentsIDs().add(selectedGroup.getWaitingStudentsIDs().get(i));
                    addToGroup(selectedGroup.getWaitingStudentsIDs().get(i),groupID, true);
//                    selectedGroup.getWaitingStudentsIDs().remove(i);
//
//                    refGroups.child(groupID).setValue(selectedGroup);
                    dialog.dismiss();
                }
            });

            builder.setNegativeButton("Dismiss", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    selectedGroup.getWaitingStudentsIDs().remove(i);
                    refGroups.child(groupID).child("waitingStudentsIDs").setValue(selectedGroup.getWaitingStudentsIDs());
                    dialog.dismiss();
                }
            });

            AlertDialog dialog = builder.create();
            dialog.show();
        }
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.group_menu, menu);

        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        String menuTitle = item.getTitle().toString();

        if (menuTitle.equals("delete group")) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Delete Group");
            builder.setMessage("Are you sure you want to delete this group?");

            builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    refGroups.child(groupID).removeValue();
                    finish();
                }
            });

            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });

            AlertDialog dialog = builder.create();
            dialog.show();
        }

        return true;
    }
}













