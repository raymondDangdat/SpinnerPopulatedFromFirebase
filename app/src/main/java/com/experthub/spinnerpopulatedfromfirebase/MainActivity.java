package com.experthub.spinnerpopulatedfromfirebase;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private Spinner spinnerSchoolName;
    private EditText editTextSchoolName;
    private Button buttonAddSchool;
    private String schoolName = "";
    private DatabaseReference databaseReference;
    private ValueEventListener listener;
    private ArrayAdapter<String> adapter;
    private ArrayList<String> spinnerDataList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        spinnerSchoolName = findViewById(R.id.spinner_schools);
        editTextSchoolName = findViewById(R.id.editTextTextSchoolName);
        buttonAddSchool = findViewById(R.id.button_add_chool);

        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        databaseReference = FirebaseDatabase.getInstance().getReference("SchoolNames");
        databaseReference.keepSynced(true);
        spinnerDataList = new ArrayList<>();
        adapter = new ArrayAdapter<String>(MainActivity.this,
                android.R.layout.simple_spinner_dropdown_item, spinnerDataList);
        spinnerSchoolName.setAdapter(adapter);
        retrieveData();

    }

    public void AddSchool(View view) {
        schoolName = editTextSchoolName.getText().toString().trim();
        if (TextUtils.isEmpty(schoolName)){
            Toast.makeText(this, "Please enter a valid school name", Toast.LENGTH_LONG).show();
        }else {
            databaseReference.push().setValue(schoolName).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    editTextSchoolName.setText("");
                    spinnerDataList.clear();
                    retrieveData();
                    adapter.notifyDataSetChanged();
                    Toast.makeText(MainActivity.this, "School added!", Toast.LENGTH_SHORT).show();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(MainActivity.this, "Error!" + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }

    }

    public void retrieveData(){
        listener = databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot item: snapshot.getChildren()){
                    spinnerDataList.add(item.getValue().toString());
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}