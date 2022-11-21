package com.example.projectfinal_alpha;

import static com.example.projectfinal_alpha.FBref.refStorage;
import static com.example.projectfinal_alpha.FBref.refUsers;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
/**
 * @author Etay Sabag <itay45520@gmail.com>
 * @version 1.0
 * @since 4/11/2022
 */
public class new_user2 extends AppCompatActivity {

    Intent siPrevSignUp,siNextSignUp;
    static final int PICK_IMAGE_REQUEST = 1;

    Student newUser;

    Button btnNext;
    ImageView imagePlaceHolder;
    ProgressBar progressBarUpload;
    Uri mImageUri;

    StorageReference StorageRef;
    StorageTask UploadTask;
    FirebaseAuth mAuth;
    FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_user2);
        siPrevSignUp = new Intent(this, new_user1.class);
        siNextSignUp = new Intent(this, MainActivity.class);
        newUser = (Student) getIntent().getSerializableExtra("userObject");
//        mTextViewShowUploads = findViewById(R.id.text_view_show_uploads);
        imagePlaceHolder = findViewById(R.id.file_preview);
        progressBarUpload = findViewById(R.id.image_load_bar);
        btnNext = findViewById(R.id.next_btn);

        StorageRef = FirebaseStorage.getInstance().getReference("uploads");

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
    }


    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST &&  resultCode == RESULT_OK
                && data != null && data.getData()!=null) {
            mImageUri = data.getData();

            imagePlaceHolder.setImageURI(mImageUri);
        }
    }

    private String getFileExtension(Uri uri) {
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    private void uploadFile() {
        if (mImageUri != null) {
            StorageReference fileReference = StorageRef.child(currentUser.getUid()
                    + "." + getFileExtension(mImageUri));

            UploadTask = fileReference.putFile(mImageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    progressBarUpload.setProgress(0);
                                }
                            }, 500);

                            Toast.makeText(new_user2.this, "Upload successful", Toast.LENGTH_LONG).show();
                            Upload upload = new Upload(taskSnapshot.getMetadata().getReference().getDownloadUrl().toString());
//                            String uploadId = refStorage.push().getKey();
                            refStorage.child(currentUser.getUid()).setValue(upload);
                            btnNext.setEnabled(true);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(new_user2.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                            progressBarUpload.setProgress((int) progress);
                            btnNext.setEnabled(false);
                        }
                    });
        } else {
            Toast.makeText(this, "No file selected", Toast.LENGTH_SHORT).show();
        }
    }

    public void go_to_new_user1(View view) {
        startActivity(siPrevSignUp);
        overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);

    }
    public void go_to_new_user3(View view) {

        refUsers.child(currentUser.getUid()).setValue(newUser);
        siNextSignUp.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(siNextSignUp);
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
    }

    public void uploadImage(View view) {
        uploadFile();
    }
    public void chooseImage(View view) {
        openFileChooser();

    }
}