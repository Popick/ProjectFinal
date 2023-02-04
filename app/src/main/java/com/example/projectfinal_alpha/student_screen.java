package com.example.projectfinal_alpha;

import static com.example.projectfinal_alpha.FBref.refApprovals;
import static com.example.projectfinal_alpha.FBref.refStudents;
import static com.example.projectfinal_alpha.FBref.refUsers;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.identity.SignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;

/**
 * @author Etay Sabag <itay45520@gmail.com>
 * @version 1.2
 * @since 15/10/2022
 */
public class student_screen extends AppCompatActivity {
    ImageView iVQrCode;
    TextView tVStatusBtn, name_tv, status_tv;
    Button requestBtn;
    ValueEventListener usrListener;
    Student currentStudent;
    CountDownLatch done = new CountDownLatch(1);
    ProgressBar loader;
    GoogleSignInClient mGoogleSignInClient;
    FirebaseAuth mAuth;
    FirebaseUser currentUser;
    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageRef = storage.getReference().child("uploads");
    Intent siRequests;
    boolean isAllowed = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_screen);
        iVQrCode = (ImageView) findViewById(R.id.QrcodeImageView);
        tVStatusBtn = (TextView) findViewById(R.id.QrStatusBtn);
        status_tv = (TextView) findViewById(R.id.status_tv);
        name_tv = (TextView) findViewById(R.id.name_tv);
        loader = (ProgressBar) findViewById(R.id.progress_loader);
        requestBtn = (Button) findViewById(R.id.request_btn);

        siRequests = new Intent(this, request_screen.class);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        mAuth = FirebaseAuth.getInstance();

    }

    protected void onResume() {
        super.onResume();
        currentUser = mAuth.getCurrentUser();
        readStudent(currentUser.getUid());
    }

    public void qr_visibility_switch(View view) {
        if (iVQrCode.getVisibility() == View.VISIBLE) {
            iVQrCode.setVisibility(View.GONE);
            tVStatusBtn.setText("הצג ברקוד");
        } else {
            iVQrCode.setVisibility(View.VISIBLE);
            tVStatusBtn.setText("הסתר ברקוד");

        }
    }

    public void create_qr_code(String content) {
        QRCodeWriter writer = new QRCodeWriter();
        try {
            BitMatrix bitMatrix = writer.encode(content, BarcodeFormat.QR_CODE, 512, 512);
            int width = bitMatrix.getWidth();
            int height = bitMatrix.getHeight();
            Bitmap bmp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    bmp.setPixel(x, y, bitMatrix.get(x, y) ? Color.BLACK : Color.WHITE);
                }
            }
            ((ImageView) findViewById(R.id.QrcodeImageView)).setImageBitmap(bmp);

        } catch (WriterException e) {
            e.printStackTrace();
        }
        loader.setVisibility(View.GONE);

    }

    public boolean readStudent(String filterPar) {
        Query query = refUsers;
        usrListener = new ValueEventListener() {


            /**
             * This method will be called with a snapshot of the data at this location. It will also be called
             * each time that data changes.
             *
             * @param dS The current data at the location
             */

            @Override
            public void onDataChange(DataSnapshot dS) {


                for (DataSnapshot data : dS.getChildren()) {
                    String uID = (String) data.getKey();
                    Student tempStd = data.getValue(Student.class);
                    if (uID.equals(filterPar)) {
                        currentStudent = tempStd;
                    }
                }

                if (currentStudent.getApprovalID() != null) {
//                    isAllowed = currentStudent.checkApproval();
                        refApprovals.child(currentStudent.getApprovalID()).addListenerForSingleValueEvent(new ValueEventListener() {

                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {

                                Approval appTemp = dataSnapshot.getValue(Approval.class);
                                if (appTemp != null) {

                                    Log.d("boolboolean",!Helper.isMoreThan30Minutes(appTemp.getTimeStampApproval())+" --> isMoreThan30Minutes");
                                    Log.d("boolboolean",(Helper.getClassNumber(appTemp.getTimeStampApproval()) == Helper.getClassNumber(Helper.getCurrentDateString()))+" --> getClassNumber");

                                    isAllowed = (
                                            !(Helper.isMoreThan30Minutes(appTemp.getTimeStampApproval())) ||
                                            ((Helper.getClassNumber(appTemp.getTimeStampApproval()) == Helper.getClassNumber(Helper.getCurrentDateString())
                                                    && Helper.getClassNumber(Helper.getCurrentDateString()) != -1)));

                                    Log.d("caman", isAllowed +" fuck no");

                                } else {
                                    Log.d("caman", isAllowed +" jkm");

                                    isAllowed = false;
                                }
                                Log.d("caman", isAllowed +" !!!");
                                fillUI(currentStudent);

                            }

                            @Override
                            public void onCancelled(DatabaseError error) {
                                // Failed to read value
                                Log.w("failed", "Failed to read value.", error.toException());
                                isAllowed = false;

                            }

                        });
                        Log.d("boolean","is allowed on data change "+ isAllowed);

                    }

            }


            /**
             * This method will be triggered in the event that this listener either failed at the server, or
             * is removed as a result of the security and Firebase Database rules. For more information on
             * securing your data, see: <a
             * href="https://firebase.google.com/docs/database/security/quickstart" target="_blank"> Security
             * Quickstart</a>
             *
             * @param error A description of the error that occurred
             */
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        };
        query.addValueEventListener(usrListener);

        return true;
    }


//    public void writeStudent(){
//        Student me = new Student("Etay Sabag", "214881310", "12","5","Cyber","Physics","None","Student",null,true );
//        refStudents.child(me.getID()).setValue(me);
//    }


    public void sendRequest(View view) {
        startActivity(siRequests);
    }

    public void fillUI(Student currentStudent) {
        Log.d("diff","the request btn");
        if (currentStudent.getLastRequest() == null || Helper.isMoreThan30Minutes(currentStudent.getLastRequest())) {
            requestBtn.setEnabled(true);
        } else {
            requestBtn.setEnabled(false);
        }


        name_tv.setText("שלום " + currentStudent.getName());
        name_tv.setVisibility(View.VISIBLE);
        Log.d("caman", isAllowed +"  ???");
        if (isAllowed) {
            status_tv.setText("יש אישור");
            tVStatusBtn.setVisibility(View.VISIBLE);
            requestBtn.setVisibility(View.GONE);
            iVQrCode.setVisibility(View.VISIBLE);
            create_qr_code(currentUser.getUid());
        } else {
            status_tv.setText("אין אישור");
            tVStatusBtn.setVisibility(View.GONE);
            requestBtn.setVisibility(View.VISIBLE);
//            Picasso.get().load("https://firebasestorage.googleapis.com/v0/b/project-final-ishorim.appspot.com/o/uploads%2F" + currentUser.getUid() + ".jpg?alt=media").into(iVQrCode);
//            Picasso.get().load("https://firebasestorage.googleapis.com/v0/b/project-final-ishorim.appspot.com/o/uploads%2F"+currentUser.getUid()+".png?alt=media").into(iVQrCode);


            loader.setVisibility(View.GONE);

        }
        status_tv.setVisibility(View.VISIBLE);
//        tVStatusBtn.setVisibility(View.VISIBLE);

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
                            Toast.makeText(student_screen.this, "Signed out successfully!", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    });

        }

        return true;
    }

}