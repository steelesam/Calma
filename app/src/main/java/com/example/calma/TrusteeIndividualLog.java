package com.example.calma;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class TrusteeIndividualLog extends AppCompatActivity {

    FirebaseAuth mAuth;
    DatabaseReference databaseReference;
    TextView nameOfContact;
    ListView listView;
    ArrayAdapter<String> arrayAdapter;
    ArrayList<String> arrayList = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trustee_individual_log);
        nameOfContact = findViewById(R.id.pageDesc);
        listView = (ListView) findViewById(R.id.individualAlertLogListView);

        Intent intent = getIntent();
        final String selectedName = intent.getStringExtra("SelectedContact");
        String[] separated = selectedName.split(":");
        final String selectedContactName = separated[1].trim();


        databaseReference = FirebaseDatabase.getInstance().getReference().child("Users");
        final Query query = databaseReference.getRef().orderByChild("name");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()){
                    String dependantName = dataSnapshot1.child("name").getValue().toString();
                    if(selectedContactName.equals(dependantName)){
                        nameOfContact.setText(selectedContactName);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
            // TODO: FIX THIS SHIT
        arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, arrayList);
        listView.setAdapter(arrayAdapter);
        final DatabaseReference databaseReference1 = FirebaseDatabase.getInstance().getReference().child("Users");
        final Query query1 = databaseReference1.getRef().orderByChild("name").equalTo(selectedContactName);
        query1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()){
                    String greenAlert = dataSnapshot1.child("Green Alert").getKey();
                    String eachAlert = dataSnapshot1.getValue(GreenAlert.class).toString();
                    String againTest = dataSnapshot1.child("Green Alert").getValue().toString();
                    arrayList.add(greenAlert);
                    arrayList.add(eachAlert);
                    arrayList.add(againTest);
                    arrayAdapter.notifyDataSetChanged();
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });










    }
}