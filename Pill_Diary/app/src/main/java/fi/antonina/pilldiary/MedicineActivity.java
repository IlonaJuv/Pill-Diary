package fi.antonina.pilldiary;

import static fi.antonina.pilldiary.R.drawable.dialog_background_custom;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MedicineActivity extends AppCompatActivity {

    //Initialize and Assign Variable
    BottomNavigationView navigationView;
    FirebaseAuth auth;
    FirebaseDatabase db;
    DatabaseReference users;
    Button addNewMedicineButton;
    ListView medListView;
    ArrayList<MedicineType> medArrayList;
    MedicineAdapter medicineAdapter;
    long counter = 0;

    String i;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.medicine);

        FirebaseApp.initializeApp(this);
        auth = FirebaseAuth.getInstance();
        db = FirebaseDatabase.getInstance();
        users = db.getReference("Users").child(auth.getUid());


        navigationView = findViewById(R.id.bottom_nav);

        //Set icon selected
        navigationView.setSelectedItemId(R.id.my_Medicine);

        //Perform ItemSelectedListener
        navigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @SuppressLint("NonConstantResourceId")
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();
                if (itemId == R.id.my_Medicine) {
                    return true;
                } else if (itemId == R.id.person) {
                    startActivity(new Intent(getApplicationContext(), PersonActivity.class));
                    MedicineActivity.this.finish();
                    overridePendingTransition(0, 0);
                    return true;
                } else if (itemId == R.id.calendar) {
                    startActivity(new Intent(getApplicationContext()
                            , CalendarActivity.class));
                    MedicineActivity.this.finish();
                    overridePendingTransition(0, 0);
                    return true;
                }
                return false;
            }
        });


        medListView = findViewById(R.id.medListView);
        medArrayList = new ArrayList<>();
        medicineAdapter = new MedicineAdapter(MedicineActivity.this, R.layout.medicineitem, medArrayList, users);
        medListView.setAdapter(medicineAdapter);
        addNewMedicineButton = findViewById(R.id.addMedicineButton);


        //When Add New Medicine is clicked, a dialog will appear
        addNewMedicineButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MedicineActivity.this);
                View v2 = getLayoutInflater().inflate(R.layout.activity_medicine_add, null);

                final EditText medNameEditText = v2.findViewById(R.id.editTextGetMedName);
                final EditText medAmountEditText = v2.findViewById(R.id.editTextMedNumber);
                final EditText medTimeEditText = v2.findViewById(R.id.editTextMedTime);
                Button addButton = v2.findViewById(R.id.addMedButton);

                builder.setView(v2);
                final Dialog dialog = builder.create();
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();

                addButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String medName = medNameEditText.getText().toString().toUpperCase();
                        String medAmount = medAmountEditText.getText().toString();
                        String medTime = medTimeEditText.getText().toString();


                        if (medName.isEmpty() || medAmount.isEmpty() || medTime.isEmpty()) {
                            Toast.makeText(MedicineActivity.this, "Please fill information!", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(MedicineActivity.this, "Successfully added!", Toast.LENGTH_SHORT).show();

                            // Write (set) data to Firebase
                            users.child("list").child((counter + 1) + "").setValue(new MedicineType(medName, "Dont have feedback!", medAmount, medTime, (counter + 1) + "")).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(MedicineActivity.this, "Successfully added!", Toast.LENGTH_SHORT).show();
                                        medicineAdapter.notifyDataSetChanged();
                                        dialog.dismiss();
                                        users.child("counter").setValue(counter+1);
                                    }
                                }
                            }).addOnFailureListener(e ->
                                    Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show());
                        }
                    }
                });
            }
        });

        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                //Read (get) data from Firebase
                if(dataSnapshot.child("counter").getValue(Integer.class)!=null){

                    counter = (long) dataSnapshot.child("counter").getValue(Integer.class);
                    Log.d("planeta", "onDataChange: " + counter);
                }

                if(dataSnapshot.child("counter").getValue(Integer.class)!=null){
                    medArrayList.clear();
                    for(DataSnapshot ds : dataSnapshot.child("list").getChildren()) {
                        String name =ds.child("medName").getValue(String.class);
                        String amount = ds.child("medAmount").getValue(String.class);
                        String time = ds.child("medGetTime").getValue(String.class);
                        String feedback = ds.child("feedBack").getValue(String.class);
                        String index = ds.child("index").getValue(String.class);
                        medArrayList.add(new MedicineType(name, feedback, amount, time, index));
                        Log.d("planeta", "onDataChange: " + name);
                    }
                }
                medicineAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("planeta", "onDataChange: " + databaseError.getMessage());
            }
        };
        users.addValueEventListener(postListener);
    }

    // Delete button onclick method
    public void deleteButton(final int position){
        String index = medArrayList.get(position).getIndex();
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(MedicineActivity.this);
        builder.setMessage("Do you want to remove this medicine?");
        builder.setCancelable(true);
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                users.child("list").child(index).removeValue(new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                        Toast.makeText(MedicineActivity.this, "Delete Successfully!", Toast.LENGTH_SHORT).show();
                        medicineAdapter.notifyDataSetChanged();
                    }
                });
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });
        android.app.AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    // Create editButton method
    public void editButton(final int position) {

        AlertDialog.Builder builder = new AlertDialog.Builder(MedicineActivity.this);
        View dialog = LayoutInflater.from(MedicineActivity.this).inflate(R.layout.medicine_edit, null);

        final EditText editTextName = dialog.findViewById(R.id.editTextEditName);
        editTextName.setText(medArrayList.get(position).getMedName());
        final EditText edittextAmount = dialog.findViewById(R.id.editTexEditNumber);
        edittextAmount.setText(medArrayList.get(position).getMedAmount());
        final EditText editTextTime = dialog.findViewById(R.id.editTexEditTime);
        editTextTime.setText(medArrayList.get(position).getMedGetTime());
        final EditText edittextFeedback = dialog.findViewById(R.id.editTextFeedback);
        edittextFeedback.setText(medArrayList.get(position).getFeedBack());
        String index = medArrayList.get(position).getIndex();

        Button updateButton = dialog.findViewById(R.id.updateMedButton);

        builder.setView(dialog);
        AlertDialog alertDialog = builder.create();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alertDialog.show();

        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String name = editTextName.getText().toString().trim().toUpperCase();
                final String amount = edittextAmount.getText().toString().trim();
                final String time = editTextTime.getText().toString().trim();
                final String feedback = edittextFeedback.getText().toString().trim();

                users.child("list").child(index).setValue(new MedicineType(name, feedback, amount, time, index)).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            medicineAdapter.notifyDataSetChanged();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("planeta", "onFailure: "+e.getMessage());
                    }
                });
                alertDialog.dismiss();
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
    }
}