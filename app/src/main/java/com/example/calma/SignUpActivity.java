package com.example.calma;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SignUpActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    /**
     * Declare XML elements
     */
    private TextView accountTypeInfo;
    private EditText emailEditText, passwordEditText, phoneEditText, nameEditText;
    private String selection;
    private Spinner accountType;
    private ProgressBar progressBar;
    private FirebaseAuth mAuth;
    private Button regButton;

    /**
     * OnCreate start
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        emailEditText = findViewById(R.id.sign_up_email);
        passwordEditText = findViewById(R.id.sign_up_password);
        phoneEditText = findViewById(R.id.phone);
        nameEditText = findViewById(R.id.name);
        accountType = findViewById(R.id.spinner);
        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);
        regButton = findViewById(R.id.create_acc);

        mAuth = FirebaseAuth.getInstance();

        // Spinner for setting account type
        Spinner spinner = (Spinner) findViewById(R.id.spinner);
        accountType.setOnItemSelectedListener(this);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.accountType, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        accountTypeInfo = findViewById(R.id.acc_type_info);
        accountTypeInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openAccountTypeInfoPage();
            }
        });

        regButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();
            }
        });


    }



    /**
     * Registers user information and passes to DB
     */
    private void registerUser(){
        final String name = nameEditText.getText().toString().trim();
        final String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();
        final String phone = phoneEditText.getText().toString().trim();
        final String selection = accountType.getSelectedItem().toString();

        if(name.isEmpty()){
            nameEditText.setError("Name is required");
            nameEditText.requestFocus();
            return;
        }

        if(email.isEmpty()){
            emailEditText.setError("Email is required");
            emailEditText.requestFocus();
            return;
        }

        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            emailEditText.setError("Please enter a valid email address");
            emailEditText.requestFocus();
            return;
        }

        if(password.isEmpty()){
            passwordEditText.setError("Please enter a password");
            passwordEditText.requestFocus();
            return;
        }

        if(password.length() <6) {
            passwordEditText.setError("Password must be at least 6 characters long");
            passwordEditText.requestFocus();
            return;
        }

        if(phone.isEmpty()){
            phoneEditText.setError("Please enter a valid phone number");
            phoneEditText.requestFocus();
            return;
        }

        if(phone.length()!=11){
            phoneEditText.setError("Please enter a valid phone number");
            phoneEditText.requestFocus();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    // store additional fields in database
                    User user = new User(name,email,phone, selection);
                    FirebaseDatabase.getInstance().getReference("Users")
                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                    .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            progressBar.setVisibility(View.GONE);
                            if(task.isSuccessful()){
                                Toast.makeText(SignUpActivity.this, "Registration successful!", Toast.LENGTH_SHORT).show();
                                FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                                String userID = currentUser.getUid();
                                DatabaseReference loginDB = FirebaseDatabase.getInstance().getReference().child("Users").child(userID);
                                loginDB.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        String accountType = dataSnapshot.child("accountType").getValue().toString();
                                        if(accountType.equals("Dependant")){
                                            Intent intentDependant = new Intent(SignUpActivity.this, DependantDash.class);
                                            intentDependant.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                            startActivity(intentDependant);
                                            finish();
                                        } else if(accountType.equals("Trustee")){
                                            Intent intentTrustee = new Intent(SignUpActivity.this, TrusteeDash.class);
                                            intentTrustee.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                            startActivity(intentTrustee);
                                            finish();
                                        } else {
                                            Toast.makeText(getApplicationContext(),"Something went wrong, please try again.",Toast.LENGTH_SHORT).show();
                                            return;
                                        }
                                    }
                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });
                            } else {
                                if(task.getException() instanceof FirebaseAuthUserCollisionException){
                                    Toast.makeText(SignUpActivity.this, "This email is already in use.", Toast.LENGTH_LONG).show();
                                }
                            }
                        }
                    });
                } else{
                    Toast.makeText(SignUpActivity.this, task.getException().getMessage(),Toast.LENGTH_LONG).show();
                }
            }
        });

    }
    

    /**
     * Opens up hints and tips
     */
    public void openAccountTypeInfoPage(){
        Intent intent = new Intent(this,TipsAndHintsActivity.class);
        startActivity(intent);
    }

    /**
     * Sets account type of user when registered from spinner options
     * @param parent
     * @param view
     * @param position
     * @param id
     */
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        selection = parent.getItemAtPosition(position).toString();

        }

    /**
     * Displays toast if no spinner option is selected
     * @param parent
     */
    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        Toast.makeText(parent.getContext(),"Please select an account type",Toast.LENGTH_LONG).show();
    }
}
