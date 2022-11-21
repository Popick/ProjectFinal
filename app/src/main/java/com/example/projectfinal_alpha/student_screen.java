package com.example.projectfinal_alpha;

import static com.example.projectfinal_alpha.FBref.refStudents;
import static com.example.projectfinal_alpha.FBref.refUsers;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

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
        if (iVQrCode.getVisibility() == View.VISIBLE){
            iVQrCode.setVisibility(View.GONE);
            tVStatusBtn.setText("הצג ברקוד");
        }
        else{
            iVQrCode.setVisibility(View.VISIBLE);
            tVStatusBtn.setText("הסתר ברקוד");

        }
    }

    public void create_qr_code(String content){
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
                    if (uID.equals(filterPar)){
                        currentStudent = tempStd;
                    }
                }
                fillUI(currentStudent);
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



    public void fillUI(Student currentStudent){
        name_tv.setText("שלום "+currentStudent.getNAME());
        name_tv.setVisibility(View.VISIBLE);
        if(currentStudent.isALLOWED()){
            status_tv.setText("יש אישור");
            tVStatusBtn.setVisibility(View.VISIBLE);
            requestBtn.setVisibility(View.GONE);
        }else{
            status_tv.setText("אין אישור");
            tVStatusBtn.setVisibility(View.GONE);
            requestBtn.setVisibility(View.VISIBLE);


        }
        status_tv.setVisibility(View.VISIBLE);
        tVStatusBtn.setVisibility(View.VISIBLE);
        create_qr_code(String.valueOf(currentStudent.isALLOWED()));

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
                            Toast.makeText(student_screen.this,"Signed out successfully!", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    });

        }

        return true;
    }
}