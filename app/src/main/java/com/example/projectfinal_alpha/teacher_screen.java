package com.example.projectfinal_alpha;

import static com.example.projectfinal_alpha.FBref.refApprovals;
import static com.example.projectfinal_alpha.FBref.refRequests;
import static com.example.projectfinal_alpha.FBref.refTeachers;
import static com.example.projectfinal_alpha.FBref.refUsers;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;

/**
 * @author Etay Sabag <itay45520@gmail.com>
 * @version 1.0
 * @since 26/10/2022
 *
 */
//TODO: FIX THE LIST NOT UPDATING

public class teacher_screen extends AppCompatActivity implements AdapterView.OnItemClickListener {

    ImageView pfp;
    TextView name, email, id;
    Button signout;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_screen);

        requestsListView = (ListView) findViewById(R.id.requests_list_view);
        requestsListView.setOnItemClickListener(this);
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
        if (incomingRequestsListener != null) {
            refTeachers.removeEventListener(incomingRequestsListener);
        }

    }


    public void waitForRequest() {
        Query query = refRequests;
        incomingRequestsListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dS) {
                requestsID.clear();
                requests.clear();
                students.clear();
                requestsHeadLine.clear();


                for (DataSnapshot data : dS.getChildren()) {
                    String str1 = (String) data.getKey();
                    requestsID.add(0, str1);
                    Log.i("key", str1);
                    Request rqTemp = data.getValue(Request.class);
                    if (rqTemp.isPending()) {
                        rqTemp.setRequestID(str1);
                        requests.add(0, rqTemp);
//                    try {
//                         time = Helper.stringToDateTime(rqTemp.getTimeStampRequest());
//                    } catch (ParseException e) {
//                        e.printStackTrace();
//                    }

                        refUsers.child(rqTemp.getStuID()).addListenerForSingleValueEvent(new ValueEventListener() {
                            String stuTempName = "error";
                            String stuTempGrade = "error";
                            String stuTempClass = "error";
                            String time = "00:00";

                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                // This method is called once with the initial value.
                                Student stuTemp = dataSnapshot.getValue(Student.class);
                                stuTempName = stuTemp.getName();
                                students.add(0, stuTemp);

                                stuTempGrade = Helper.getGrade(stuTemp.getGrade());
                                stuTempClass = stuTemp.getaClass();

                                try {
                                    time = Helper.stringToDateTime(rqTemp.getTimeStampRequest());
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                                Log.w("worked", stuTempName);

                                requestsHeadLine.add(0, stuTempName + " - " + stuTempGrade + stuTempClass + "      " + time);
//                            Collections.reverse(requestsHeadLine);
                                ArrayAdapter<String> adp = new ArrayAdapter<String>(teacher_screen.this, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, requestsHeadLine);
                                requestsListView.setAdapter(adp);
                            }

                            @Override
                            public void onCancelled(DatabaseError error) {
                                // Failed to read value
                                Log.w("failed", "Failed to read value.", error.toException());
                            }

                        });

                    }
                }


            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        query.addValueEventListener(incomingRequestsListener);

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
                            Toast.makeText(teacher_screen.this, "Signed out successfully!", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    });

        }

        return true;
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        Log.d("the item is", "pos:" + i + " the id belongs to " + requestsID.get(i));

        isallowedflipper = !isallowedflipper;

        Request selectedRequest = requests.get(i);
        Log.d("request","Selected request: " + selectedRequest.getReason());
        // Create an AlertDialog builder
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        // Set the message and title
        builder.setMessage
                        ("סיבת התלמיד: " + "\n" + selectedRequest.getReason() + "\n\n"
                                + "ליום " + Helper.getDayOfWeekInHebrew(selectedRequest.getDay()) + " בשעה " + selectedRequest.getHour() + "\n" +
                                "חזרה: " + !selectedRequest.isTemp()
                        )
                .setTitle(students.get(i).getName());

        // Set the buttons
        builder.setNegativeButton("דחה", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked "Deny" button, do something
                selectedRequest.setApproved(false);
                selectedRequest.setPending(false);
                refRequests.child(selectedRequest.getRequestID()).setValue(requests.get(i));

                studentsID.add(selectedRequest.getStuID());
                Approval stuApproval = new Approval(
                        Helper.getCurrentDateString(),
                        currentUser.getDisplayName(),
                        currentUser.getUid(),
                        selectedRequest.getDay(), selectedRequest.getHour(),
                        groupIDs,
                        studentsID,
                        selectedRequest.getRequestID());

                DatabaseReference currentApprovalRef = refApprovals.push();
                currentApprovalRef.setValue(stuApproval);
                refUsers.child(selectedRequest.getStuID()).child("approvalID").setValue(currentApprovalRef.getKey());

            }
        });

        builder.setPositiveButton("אשר", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked "Call" button, do something

//                refUsers.child(selectedRequest.getStuID()).child("allowed").setValue(selectedRequest.getRequestID());
                selectedRequest.setApproved(true);
                selectedRequest.setPending(false);
                refRequests.child(selectedRequest.getRequestID()).setValue(selectedRequest);

                studentsID.add(selectedRequest.getStuID());
                Approval stuApproval = new Approval(
                        Helper.getCurrentDateString(),
                        currentUser.getDisplayName(),
                        currentUser.getUid(),
                        selectedRequest.getDay(), selectedRequest.getHour(),
                        groupIDs,
                        studentsID,
                        selectedRequest.getRequestID());

                DatabaseReference currentApprovalRef = refApprovals.push();
                currentApprovalRef.setValue(stuApproval);
                refUsers.child(selectedRequest.getStuID()).child("approvalID").setValue(currentApprovalRef.getKey());



            }
        });

        builder.setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked "Allow" button, do something
//                refUsers.child(requests.get(i).getStuID()).child("allowed").setValue(false);
            }
        });


        // Create and show the dialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }


    public void go_to_create_approval(View view) {
        Intent intent = new Intent(teacher_screen.this, approval_create_screen.class);
        startActivity(intent);

    }

    public void go_to_history(View view) {
        Intent intent = new Intent(teacher_screen.this, history_screen.class);
        startActivity(intent);
    }

    public void go_to_classes(View view) {
        Intent intent = new Intent(teacher_screen.this, classes_screen.class);
        startActivity(intent);
    }
}
