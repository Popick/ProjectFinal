package com.example.projectfinal_alpha;

import static com.example.projectfinal_alpha.FBref.refGroups;
import static com.example.projectfinal_alpha.FBref.refTeachers;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
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

/**
 * This class is the groups screen of the app.
 * It shows the groups the teacher is in.
 * The teacher can create a new group, or join an existing group.
 */
public class groups_screen extends Fragment implements AdapterView.OnItemClickListener {

    FirebaseAuth mAuth;
    GoogleSignInClient mGoogleSignInClient;
    FirebaseUser currentUser;
    Intent siSelectedGroup;
    ListView groupsLV;
    ArrayList<String> groupIDs = new ArrayList<String>();

    ArrayList<String> teacherGroups = new ArrayList<String>();


    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_groups_screen, container, false);

        groupsLV = (ListView) rootView.findViewById(R.id.groups_list_view);
        groupsLV.setOnItemClickListener(this);


        mAuth = FirebaseAuth.getInstance();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(getContext(), gso);

        siSelectedGroup = new Intent(getContext(), group_view.class);

        return rootView;
    }

    /**
     * This method is called when the activity is starting.
     * It loads the groups the teacher is in.
     */

    public void onStart() {
        super.onStart();
        currentUser = mAuth.getCurrentUser();


        refTeachers.child(currentUser.getUid()).child("groups").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    teacherGroups = (ArrayList<String>) dataSnapshot.getValue();
                }

                loadGroups();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle any errors that may occur during the operation
            }
        });


    }


    /**
     * This method loads the groups the teacher is in.
     * It loads the groups from the database and displays them in a list view.
     * The teacher can click on a group to view it.
     *  If the teacher is not in any groups, the list view will be empty.
     */
    public void loadGroups() {

        refGroups.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ArrayList<String> items = new ArrayList<>();
                groupIDs.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    if (teacherGroups.contains(snapshot.getKey())) {
                        // Get the name of each item and add it to the list
                        Group tmpGroup = snapshot.getValue(Group.class);
                        items.add(tmpGroup.getGroupName());
                        groupIDs.add(snapshot.getKey());
                    }
                }

                // Set up an ArrayAdapter to display the list of items in a ListView
                if (getContext() != null) {
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, items);
                    groupsLV.setAdapter(adapter);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle error
            }
        });

    }

    /**
     * This method is called when the teacher clicks on a group in the list view.
     * @param adapterView
     * @param view
     * @param i
     * @param l
     */
    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        siSelectedGroup.putExtra("keyID", groupIDs.get(i));
        startActivity(siSelectedGroup);
    }


}