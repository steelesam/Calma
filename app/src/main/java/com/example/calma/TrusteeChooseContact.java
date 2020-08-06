package com.example.calma;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class TrusteeChooseContact extends AppCompatActivity {


    FirebaseAuth mAuth;
    TextView usersName;
    ArrayList<String> arrayList = new ArrayList<>();
    ArrayAdapter<String> arrayAdapter;
    ListView listView;
    ImageView logoutButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trustee_choose_contact);

        mAuth = FirebaseAuth.getInstance();
        usersName = findViewById(R.id.usersName);
        loadUserInformation();
        logoutButton = findViewById(R.id.logoutButton);
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openLogoutPage();
            }
        });

        String userID = mAuth.getCurrentUser().getUid();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(userID).child("ContactsList");
        listView = (ListView) findViewById(R.id.trusteeChooseContactListView);
        arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, arrayList);
        listView.setAdapter(arrayAdapter);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()){
                    String value = dataSnapshot1.getValue(Contact.class).toString();
                    arrayList.add(value);
                    arrayAdapter.notifyDataSetChanged();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selection = parent.getItemAtPosition(position).toString();
                Intent intent = new Intent(getApplicationContext(), TrusteeContactIsChosen.class);
                intent.putExtra("SelectedContactTrustee", selection);
                startActivity(intent);
            }
        });



    }

    /**
     * Retrieves and displays info of current logged in user
     */
    private void loadUserInformation() {
        String userID = mAuth.getCurrentUser().getUid();
        DatabaseReference retrieveName = FirebaseDatabase.getInstance().getReference().child("Users").child(userID);
        retrieveName.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String displayName = dataSnapshot.child("name").getValue().toString();
                usersName.setText(displayName);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    /**
     * Opens logout page
     */
    private void openLogoutPage() {
        Intent intent = new Intent(this, Logout.class);
        startActivity(intent);
    }
}