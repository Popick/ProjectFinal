package com.example.projectfinal_alpha;

import static com.example.projectfinal_alpha.FBref.refUsers;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.budiyev.android.codescanner.CodeScanner;
import com.budiyev.android.codescanner.CodeScannerView;
import com.budiyev.android.codescanner.DecodeCallback;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.Result;
import com.squareup.picasso.Picasso;

import java.text.Format;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;
/**
 * @author Etay Sabag <itay45520@gmail.com>
 * @version 1.0
 * @since 23/10/2022
 */
public class guard_screen extends AppCompatActivity {
    private CodeScanner codeScanner;
    TextView tv_result, tv_datetime;
    CodeScannerView scannerView;
    GoogleSignInClient mGoogleSignInClient;
    FirebaseAuth mAuth;
    FirebaseUser currentUser;
    ImageView iVQrCode;
    String approvalID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guard_screen);
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED){
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.CAMERA},100);
        }
        Toast.makeText(guard_screen.this, "לחץ על המסך בשביל לסרוק", Toast.LENGTH_LONG).show();
        scannerView = findViewById(R.id.scanner_view);
        tv_result =findViewById(R.id.tv_result);
        tv_datetime =findViewById(R.id.tv_view_time);
        iVQrCode = (ImageView) findViewById(R.id.iVQrCode);


        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        mAuth = FirebaseAuth.getInstance();

        codeScanner = new CodeScanner(this, scannerView);
        codeScanner.setDecodeCallback(new DecodeCallback() {
            @Override
            public void onDecoded(@NonNull final Result result) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Format dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        String dateTimeString  = dateFormat.format(new Date());
                        Toast.makeText(guard_screen.this, result.getText(), Toast.LENGTH_SHORT).show();
                        tv_datetime.setText(dateTimeString);
                        loadUser(result.getText());
                    }
                });
            }
        });

        scannerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                codeScanner.startPreview();
            }
        });
    }

    public void loadUser(String Uid){
        Picasso.get().load("https://firebasestorage.googleapis.com/v0/b/project-final-ishorim.appspot.com/o/uploads%2F" + Uid + ".jpg?alt=media").into(iVQrCode);

        refUsers.child(Uid).addListenerForSingleValueEvent(new ValueEventListener() {
            

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value.
                Student stuTemp = dataSnapshot.getValue(Student.class);

                approvalID = stuTemp.getApprovalID();
                if(approvalID==null) {
                    updateApproval(false);
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("failed", "Failed to read value.", error.toException());
            }

        });

    }

    public void updateApproval(boolean isApproved){
        if (isApproved){
            tv_result.setText("יש אישור");
        }
        else{
            tv_result.setText("אין אישור");
        }
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
                            Toast.makeText(guard_screen.this,"Signed out successfully!", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    });

        }

        return true;
    }
}
