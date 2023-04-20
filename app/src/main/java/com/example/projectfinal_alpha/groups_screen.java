package com.example.projectfinal_alpha;

import static com.example.projectfinal_alpha.FBref.refGroups;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
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
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Random;

public class groups_screen extends AppCompatActivity implements AdapterView.OnItemClickListener {

    FirebaseAuth mAuth;
    GoogleSignInClient mGoogleSignInClient;
    FirebaseUser currentUser;
    Intent siSelectedGroup;
    ListView groupsLV;
    ArrayList<String> groupIDs = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_groups_screen);
        mAuth = FirebaseAuth.getInstance();

        groupsLV = (ListView) findViewById(R.id.groups_list_view);
        groupsLV.setOnItemClickListener(this);


        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        siSelectedGroup = new Intent(this, group_view.class);
    }

    public void onStart() {
        super.onStart();
        currentUser = mAuth.getCurrentUser();

        loadGroups();
    }

    public void go_to_create_group(View view) {
        finish();
    }


    public void writeGroup(String groupName) {
        Random random = new Random();
        StringBuilder sb = new StringBuilder(5);
        String characters = "abcdefghijklmnopqrstuvwxyz0123456789";
        for (int i = 0; i < 5; i++) {
            sb.append(characters.charAt(random.nextInt(characters.length())));
        }
        String joinCode = sb.toString();

        refGroups.orderByChild("joinCode").equalTo(joinCode).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    writeGroup(groupName);
                } else {
                    Group newGroup = new Group(currentUser.getDisplayName(), currentUser.getUid(), groupName, joinCode, false);
                    refGroups.push().setValue(newGroup);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle error
            }
        });


    }


    public void loadGroups() {

        refGroups.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ArrayList<String> items = new ArrayList<>();
                groupIDs.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    // Get the name of each item and add it to the list
                    Group tmpGroup = snapshot.getValue(Group.class);
                    items.add(tmpGroup.getGroupName());
                    groupIDs.add(snapshot.getKey());
                }

                // Set up an ArrayAdapter to display the list of items in a ListView
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(groups_screen.this, android.R.layout.simple_list_item_1, items);
                groupsLV.setAdapter(adapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle error
            }
        });

    }


    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        siSelectedGroup.putExtra("keyID", groupIDs.get(i));
        startActivity(siSelectedGroup);
    }


    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.add, menu);

        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        String menuTitle = item.getTitle().toString();
        if (menuTitle.equals("Logout")) {
            mGoogleSignInClient.signOut()
                    .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            FirebaseAuth.getInstance().signOut();
                            Toast.makeText(groups_screen.this, "Signed out successfully!", Toast.LENGTH_SHORT).show();
                            Intent siMainScreen = new Intent(groups_screen.this, MainActivity.class);
                            siMainScreen.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(siMainScreen);
                        }
                    });

        } else if (menuTitle.equals("Join Class")) {


            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("הכנס שם לקבוצה");

// Set up the input
            final EditText input = new EditText(this);
            input.setFilters(new InputFilter[]{new InputFilter.LengthFilter(20)});
            input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_WORDS);
            builder.setView(input);

// Set up the buttons
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String groupName = input.getText().toString();
                    writeGroup(groupName);

                    // Do something with the short name
                }
            });
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });

            AlertDialog dialog = builder.create();
            dialog.show();
//        Intent intent = new Intent(groups_screen.this, group_create_screen.class);
//        startActivity(intent);
        }

        return true;
    }
}