package com.example.projectfinal_alpha;

import static com.example.projectfinal_alpha.FBref.refApprovals;
import static com.example.projectfinal_alpha.FBref.refGroups;
import static com.example.projectfinal_alpha.FBref.refRequests;
import static com.example.projectfinal_alpha.FBref.refStudents;
import static com.example.projectfinal_alpha.FBref.refTeachers;
import static com.example.projectfinal_alpha.FBref.refUsers;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;

public class history_screen extends Fragment implements AdapterView.OnItemClickListener {
    ImageView pfp;
    TextView name, email, id;
    Button signout;
    GoogleSignInClient mGoogleSignInClient;
    FirebaseAuth mAuth;
    FirebaseUser currentUser;
    ArrayList<String> studentsApprovalsID = new ArrayList<String>();
    ArrayList<String> studentsApprovalsHeadLine = new ArrayList<String>();
    ArrayList<Approval> studentsApprovals = new ArrayList<Approval>();
    ArrayList<String> studentsNames = new ArrayList<String>();
    ArrayList<String> groupsApprovalsID = new ArrayList<String>();
    ArrayList<String> groupsApprovalsHeadLine = new ArrayList<String>();
    ArrayList<Approval> groupsApprovals = new ArrayList<Approval>();
    ArrayList<String> groupsNames = new ArrayList<String>();
    long approvalCount = 0;
    long approvalTotal = 0;
    ListView approvalsListView;
    ValueEventListener incomingRequestsListener;
    boolean isStudentsSelected = true;
    private Query query;
    private boolean validSelected = true, permanentSelected = true, temporarySelected = true;

    /**
     * @author Etay Sabag <itay45520@gmail.com>
     * @version 1.0
     * @since 29/12/2022
     */

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_history_screen, container, false);

        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        approvalsListView = (ListView) view.findViewById(R.id.requests_list_view);
        approvalsListView.setOnItemClickListener(this);


        mAuth = FirebaseAuth.getInstance();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(getContext(), gso);


        RadioButton radioStudents = view.findViewById(R.id.radio_students);
        RadioButton radioGroups = view.findViewById(R.id.radio_groups);
        ImageView filterBtn = view.findViewById(R.id.filterIVbtn);
        radioStudents.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isStudentsSelected = true;
                filterBtn.setVisibility(View.VISIBLE);
                ArrayAdapter<String> adp = new ArrayAdapter<String>(getContext(), androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, studentsApprovalsHeadLine);
                approvalsListView.setAdapter(adp);
            }
        });

        radioGroups.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isStudentsSelected = false;
                filterBtn.setVisibility(View.INVISIBLE);
                ArrayAdapter<String> adp = new ArrayAdapter<String>(getContext(), androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, groupsApprovalsHeadLine);
                approvalsListView.setAdapter(adp);
            }
        });


        filterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Inflate the custom filter dialog layout
                View filterView = LayoutInflater.from(getContext()).inflate(R.layout.history_filter_dialog_layout, null);

// Create a new AlertDialog.Builder and set its view to the custom layout
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setView(filterView);

//// Set up the filter options
                CheckBox activeCheckbox = filterView.findViewById(R.id.active_checkbox);
                CheckBox permanentCheckbox = filterView.findViewById(R.id.permanent_checkbox);
                CheckBox temporaryCheckbox = filterView.findViewById(R.id.temporary_checkbox);

// Add a positive button to the dialog
                builder.setPositiveButton("Filter", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Get the selected filter options
                        validSelected = activeCheckbox.isChecked();
                        permanentSelected = permanentCheckbox.isChecked();
                        temporarySelected = temporaryCheckbox.isChecked();
                        if (incomingRequestsListener != null) {
                            refTeachers.removeEventListener(incomingRequestsListener);
                        }
                        waitForRequest();
                    }
                });


// Add a negative button to the dialog
                builder.setNegativeButton("Cancel", null);

// Create and show the AlertDialog
                AlertDialog dialog = builder.create();
                dialog.show();


            }
        });
    }


//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_history_screen);
//
//        approvalsListView = (ListView) findViewById(R.id.requests_list_view);
//        approvalsListView.setOnItemClickListener(this);
////        pfp = (ImageView) findViewById(R.id.pfp);
////        name = (TextView) findViewById(R.id.name);
////        email = (TextView) findViewById(R.id.mail);
////        id = (TextView) findViewById(R.id.id);
////        signout = (Button) findViewById(R.id.singout);
//
//        mAuth = FirebaseAuth.getInstance();
//
//
//        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
//                .requestEmail()
//                .build();
//        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
//
//
////        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(this);
////        if(acct != null)
////        {
////            String personName = acct.getDisplayName();
////            String personGivenName = acct.getGivenName();
////            String personFamilyName = acct.getFamilyName();
////            String personEmail = acct.getEmail();
////            String personId = acct.getId();
////            Uri personPhoto = acct.getPhotoUrl();
////            Glide.with(this).load(String.valueOf(personPhoto)).into(pfp);
////            name.setText(personName);
////            email.setText(personEmail);
////            id.setText(personId);
////
////
////
////        }
//    }

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
        if (incomingRequestsListener != null) {
            refTeachers.removeEventListener(incomingRequestsListener);
        }

    }


    public void waitForRequest() {
        Query query = refApprovals.orderByChild("timeStampApproval");


        incomingRequestsListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dS) {
//                approvalsID.clear();
//                approvals.clear();
//                studentsAndGroupsNames.clear();
//                approvalsHeadLine.clear();
//                timeArray.clear();
                studentsApprovalsID.clear();
                studentsApprovalsHeadLine.clear();
                studentsApprovals.clear();
                studentsNames.clear();
                groupsApprovalsID.clear();
                groupsApprovalsHeadLine.clear();
                groupsApprovals.clear();
                groupsNames.clear();
                approvalCount = 0;
                approvalTotal = dS.getChildrenCount();
                String stuID;
                for (DataSnapshot data : dS.getChildren()) {
                    stuID = (String) data.getKey();
                    Log.i("key", stuID);
                    Approval appTemp = data.getValue(Approval.class);

                    if (appTemp.getStudentsID() != null) {
                        if ((validSelected && appTemp.isValid()) && ((permanentSelected && appTemp.isPermanent()) || (temporarySelected && !appTemp.isPermanent()))) {
                            Log.d("booleans", "valid: " + validSelected + " permanent: " + permanentSelected + " temporary: " + temporarySelected);
                            Log.d("booleans", "appValid: " + appTemp.isValid() + " appPermanent: " + appTemp.isPermanent());
                            studentsApprovalsID.add(0, stuID);
                            studentsApprovals.add(0, appTemp);
                            Log.i("getStudentsID", appTemp.getStudentsID());

                            refStudents.child(appTemp.getStudentsID()).addListenerForSingleValueEvent(new ValueEventListener() {
                                String stuTempName = "error";
                                String stuTempGrade = "error";
                                String stuTempClass = "error";
                                int stuIndex = 0;
                                String time = "00:00";

                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    // This method is called once with the initial value.
                                    Student stuTemp = dataSnapshot.getValue(Student.class);
                                    stuTempName = stuTemp.getName();

                                    stuTempGrade = Helper.getGrade(stuTemp.getGrade());
                                    stuTempClass = stuTemp.getaClass();

//                                timeArray.add(appTemp.getTimeStampApproval());
//                                Collections.sort(timeArray, Collections.reverseOrder());
                                    time = Helper.stringToDateTime(appTemp.getTimeStampApproval());


                                    studentsNames.add(0, "התלמיד " + stuTemp.getName());
                                    studentsApprovalsHeadLine.add(0, "התלמיד  " + stuTempName + " - " + stuTempGrade + stuTempClass + "      " + time);
                                    Log.w("worked", studentsApprovalsHeadLine.get(0));
                                    if (isStudentsSelected) {
                                        ArrayAdapter<String> adp = new ArrayAdapter<String>(getContext(), androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, studentsApprovalsHeadLine);
                                        approvalsListView.setAdapter(adp);
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError error) {
                                    // Failed to read value
                                    Log.w("failed", "Failed to read value.", error.toException());

                                }

                            });
                        }else{
                            approvalsListView.setAdapter(null);
                        }


                    } else if (appTemp.getGroupsID() != null) {
                        groupsApprovalsID.add(0, stuID);
                        groupsApprovals.add(0, appTemp);
                        refGroups.child(appTemp.getGroupsID()).addListenerForSingleValueEvent(new ValueEventListener() {
                            String grpTempName = "error";
                            String grpTempTeacher = "error";
                            int grpIndex = 0;
                            //                            String grpTempClass = "error";
                            String time = "00:00";

                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                // This method is called once with the initial value.
                                Group grpTemp = dataSnapshot.getValue(Group.class);
                                if (grpTemp != null) {
                                    grpTempName = grpTemp.getGroupName();

                                    grpTempTeacher = grpTemp.getTeacherName();
//                                stuTempClass = stuTemp.getaClass();

//                                    timeArray.add(appTemp.getTimeStampApproval());
//                                    Collections.sort(timeArray, Collections.reverseOrder());

                                    time = Helper.stringToDateTime(appTemp.getTimeStampApproval());

                                    groupsNames.add(0, "הקבוצה " + grpTemp.getGroupName());
                                    groupsApprovalsHeadLine.add(0, "הקבוצה  " + grpTempName + " - " + grpTempTeacher + "      " + time);

                                } else {
                                    groupsNames.add(0, "קבוצה נמחקה");
                                    groupsApprovalsHeadLine.add(0, "הקבוצה נמחקה");
                                }
                                if (!isStudentsSelected) {
                                    ArrayAdapter<String> adp = new ArrayAdapter<String>(getContext(), androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, groupsApprovalsHeadLine);
                                    approvalsListView.setAdapter(adp);
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError error) {
                                // Failed to read value
                                Log.w("failed", "Failed to read value.", error.toException());

                            }

                        });
                    } else {
                    }


                }

            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("in thread", "step 5");

            }
        };
        query.addValueEventListener(incomingRequestsListener);

    }


//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.main, menu);
//
//        return true;
//    }
//
//    public boolean onOptionsItemSelected(MenuItem item) {
//        String st = item.getTitle().toString();
//        if (st.equals("Logout")) {
//            mGoogleSignInClient.signOut()
//                    .addOnCompleteListener(this, new OnCompleteListener<Void>() {
//                        @Override
//                        public void onComplete(@NonNull Task<Void> task) {
//                            FirebaseAuth.getInstance().signOut();
//                            Toast.makeText(history_screen.this, "Signed out successfully!", Toast.LENGTH_SHORT).show();
//                            finish();
//                        }
//                    });
//
//        }
//
//        return true;
//    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        if (isStudentsSelected) {
            Log.d("the item is", "pos:" + i + " the id belongs to " + studentsApprovalsID.get(i));
            Approval selectedApproval = studentsApprovals.get(i);
            // Create an AlertDialog builder
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

            // Set the message and title
            String bodyString = "מורה מאשר: " + "\n" + selectedApproval.getTeAnswer() + "\n\n"
                    + "ליום " + Helper.getDayOfWeekInHebrew(selectedApproval.getDay()) + "\n"
                    + "בשעה " + selectedApproval.getHour().toString().substring(1, selectedApproval.getHour().toString().length() - 1) + "\n";

            if (selectedApproval.isPermanent()) {
                bodyString = bodyString + "אישור שבועי\n";
            } else {
                bodyString = bodyString + "אישור חד פעמי\n";
            }
            bodyString = bodyString + "אישור ניתן ב " + Helper.stringToDateFull(selectedApproval.getTimeStampApproval());


            builder.setMessage(bodyString).setTitle(studentsNames.get(i));

            // Create and show the dialog
            AlertDialog dialog = builder.create();
            dialog.show();
        } else {
            Log.d("the item is", "pos:" + i + " the id belongs to " + groupsApprovalsID.get(i));
            Approval selectedApproval = groupsApprovals.get(i);
            // Create an AlertDialog builder
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

            // Set the message and title
            String bodyString = "מורה מאשר: " + selectedApproval.getTeAnswer() + "\n"
                    + "ליום " + Helper.getDayOfWeekInHebrew(selectedApproval.getDay()) + "\n"
                    + "בשעה " + selectedApproval.getHour().toString().substring(1, selectedApproval.getHour().toString().length() - 1) + "\n";

            if (true) {
                bodyString = bodyString + "אישור חד פעמי\n";
            } else {
                bodyString = bodyString + "אישור שבועי\n";
            }
            bodyString = bodyString + "אישור ניתן ב " + Helper.stringToDateFull(selectedApproval.getTimeStampApproval());


            builder.setMessage(bodyString).setTitle(groupsNames.get(i));

            // Create and show the dialog
            AlertDialog dialog = builder.create();
            dialog.show();
        }


    }


//    public void go_back(View view) {
//        finish();
//    }


}

