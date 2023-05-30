package com.example.projectfinal_alpha;

import static com.example.projectfinal_alpha.FBref.refApprovals;
import static com.example.projectfinal_alpha.FBref.refRequests;
import static com.example.projectfinal_alpha.FBref.refStudents;
import static com.example.projectfinal_alpha.FBref.refTeachers;
import static com.example.projectfinal_alpha.FBref.refUsers;
import static com.example.projectfinal_alpha.teacher_homescreen.teacherStudentsIds;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;

/**
 * This class is the teacher first screen of the app.
 * It shows the teacher the requests he got from students.
 * @author Etay Sabag <itay45520@gmail.com>
 * @version 1.0
 * @since 26/10/2022
 */
//TODO: FIX THE LIST NOT UPDATING

public class teacher_screen extends Fragment implements AdapterView.OnItemClickListener {


    TextView noRequestsTV;

    GoogleSignInClient mGoogleSignInClient;
    FirebaseAuth mAuth;
    FirebaseUser currentUser;
    ArrayList<String> requestsID = new ArrayList<String>();
    ArrayList<String> studentsID = new ArrayList<String>();
    ArrayList<String> groupIDs = new ArrayList<String>();
    ArrayList<String> requestsHeadLine = new ArrayList<String>();
    ArrayList<Request> requests = new ArrayList<Request>();
    ArrayList<Student> students = new ArrayList<Student>();
    ListView requestsListView;
    ValueEventListener incomingRequestsListener;
    boolean isallowedflipper = true;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_teacher_screen, container, false);
        requestsListView = (ListView) rootView.findViewById(R.id.requests_list_view);
        requestsListView.setOnItemClickListener(this);

        noRequestsTV = (TextView) rootView.findViewById(R.id.noRequestsTV);

        mAuth = FirebaseAuth.getInstance();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(getContext(), gso);


        return rootView;
    }


    /**
     * this method is called on start of the activity.
     * it gets the requests from the database and shows them to the teacher.
     * it also gets the students that sent the requests.
     */
    public void onStart() {
        super.onStart();
//        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);

        currentUser = mAuth.getCurrentUser();
        Query query = refRequests;
        incomingRequestsListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dS) {
                requests.clear();
                students.clear();
                requestsHeadLine.clear();


                for (DataSnapshot data : dS.getChildren()) {
                    String requestKey = (String) data.getKey();
                    Request rqTemp = data.getValue(Request.class);
                    if (rqTemp.isPending()) {
                        rqTemp.setRequestID(requestKey);

//todo somehow fix multiple times the same request on data change.
                        refStudents.child(rqTemp.getStuID()).addListenerForSingleValueEvent(new ValueEventListener() {
                            String stuTempName = "error";
                            String stuTempGrade = "error";
                            String stuTempClass = "error";
                            String time = "00:00";

                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (teacherStudentsIds.contains(dataSnapshot.getKey())) {

                                    // This method is called once with the initial value.
                                    Student stuTemp = dataSnapshot.getValue(Student.class);
                                    stuTempName = stuTemp.getName();
                                    requests.add(0, rqTemp);
                                    students.add(0, stuTemp);

                                    stuTempGrade = Helper.getGrade(stuTemp.getGrade());
                                    stuTempClass = stuTemp.getaClass();

                                    time = Helper.stringToDateTime(rqTemp.getTimeStampRequest());

                                    Log.w("worked", stuTempName);

                                    requestsHeadLine.add(0, stuTempName + " - " + stuTempGrade + stuTempClass + "      " + time);

                                    ArrayAdapter<String> adp = new ArrayAdapter<String>(getContext(), androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, requestsHeadLine);
                                    requestsListView.setAdapter(adp);
                                    if (requestsListView.getAdapter() == null) {
                                        noRequestsTV.setVisibility(View.VISIBLE);
                                    } else {
                                        noRequestsTV.setVisibility(View.INVISIBLE);
                                    }

                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError error) {
                                // Failed to read value
                                Log.w("failed", "Failed to read value.", error.toException());
                            }

                        });

                    } else {
                        requestsListView.setAdapter(null);
                        noRequestsTV.setVisibility(View.VISIBLE);

                    }
                }


            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        }

        ;
        query.addValueEventListener(incomingRequestsListener);


    }

    /**
     * this method is called when the activity is paused.
     * it removes the listener from the database.
     */
    public void onPause() {
        super.onPause();
        if (incomingRequestsListener != null) {
            refRequests.removeEventListener(incomingRequestsListener);
        }

    }






    /**
     * this method is called when the teacher clicks on a request.
     * it shows the teacher the request details and allows him to accept or decline the request.
     * @param adapterView
     * @param view
     * @param i
     * @param l
     */

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

        isallowedflipper = !isallowedflipper;

        Request selectedRequest = requests.get(i);
        Log.d("request", "Selected request: " + selectedRequest.getReason());
        // Create an AlertDialog builder
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        String bodyString = "סיבת התלמיד: " + "\n" + selectedRequest.getReason() + "\n\n"
                + "ליום " + Helper.getDayOfWeekInHebrew(selectedRequest.getDay()) + "\n"
                + "בשעה " + selectedRequest.getHour().toString().substring(1, selectedRequest.getHour().toString().length() - 1) + "\n";

        if (selectedRequest.isTemp()) {
            bodyString = bodyString + "אישור חד פעמי";
        } else {
            bodyString = bodyString + "אישור שבועי";
        }
        builder.setMessage(bodyString).setTitle(students.get(i).getName());

        //TODO: delete the request from the database after a new request is requested by the student
        // Set the buttons
        builder.setNegativeButton("דחה", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked "Deny" button, do something
                selectedRequest.setApproved(false);
                selectedRequest.setPending(false);
                refRequests.child(selectedRequest.getRequestID()).setValue(requests.get(i));

                Approval stuApproval = new Approval(
                        Helper.getCurrentDateString(),
                        currentUser.getDisplayName(),
                        currentUser.getUid(),
                        selectedRequest.getDay(), selectedRequest.getHour(),
                        selectedRequest.getStuID(),
                        null,
                        selectedRequest.getRequestID(),
                        null,
                        false, false);


                DatabaseReference currentApprovalRef = refApprovals.push();
                currentApprovalRef.setValue(stuApproval);

//                refStudents.child(selectedRequest.getStuID()).child("approvalID").setValue(currentApprovalRef.getKey());
            }
        });

        builder.setPositiveButton("אשר", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked "Call" button, do something

//                refUsers.child(selectedRequest.getStuID()).child("allowed").setValue(selectedRequest.getRequestID());
                selectedRequest.setApproved(true);
                selectedRequest.setPending(false);
                refRequests.child(selectedRequest.getRequestID()).setValue(selectedRequest);

                Approval stuApproval = new Approval(
                        Helper.getCurrentDateString(),
                        currentUser.getDisplayName(),
                        currentUser.getUid(),
                        selectedRequest.getDay(), selectedRequest.getHour(),
                        selectedRequest.getStuID(),
                        null,
                        selectedRequest.getRequestID(),
                        null
                        , true, !selectedRequest.isTemp());


                if (selectedRequest.isTemp()) {
                    stuApproval.setExpirationDate(Helper.getNextWeekDateString());
                    DatabaseReference currentApprovalRef = refApprovals.push();
                    currentApprovalRef.setValue(stuApproval);
                    refStudents.child(selectedRequest.getStuID()).child("approvalID").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            ArrayList<String> currentAppr;
                            if (dataSnapshot.exists()) {
                                currentAppr = (ArrayList<String>) dataSnapshot.getValue();
                            } else {
                                currentAppr = new ArrayList<String>();
                            }
                            currentAppr.add(currentApprovalRef.getKey());
                            refStudents.child(selectedRequest.getStuID()).child("approvalID").setValue(currentAppr);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            // Handle any errors that may occur during the operation
                        }
                    });
                } else {
                    stuApproval.setExpirationDate(Helper.getNextMonthDateString());
                    DatabaseReference currentApprovalRef = refApprovals.push();
                    currentApprovalRef.setValue(stuApproval);
                    refStudents.child(selectedRequest.getStuID()).child("permanentApprovalID").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {


                            ArrayList<String> currentPerAppr;
                            if (dataSnapshot.exists()) {
                                currentPerAppr = (ArrayList<String>) dataSnapshot.getValue();
                            } else {
                                currentPerAppr = new ArrayList<String>();
                            }
                            currentPerAppr.add(currentApprovalRef.getKey());
                            refStudents.child(selectedRequest.getStuID()).child("permanentApprovalID").setValue(currentPerAppr);

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            // Handle any errors that may occur during the operation
                        }
                    });
                }

            }
        });

        builder.setNeutralButton("בטל", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked "Allow" button, do something
//                refUsers.child(requests.get(i).getStuID()).child("allowed").setValue(false);
            }
        });


        // Create and show the dialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }



}
