package fi.antonina.pilldiary;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class TodayActivity extends AppCompatActivity {

    String datePicked = "";
    FirebaseAuth auth;
    FirebaseDatabase db;
    DatabaseReference users;

    ListView medListView;
    ArrayList<MedicineType> medArrayList;
    MedicineType medicineType;
    TodayAdapter medicineAdapter;
    TextView all_taken;
    long counter = 0, counter2 = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.today);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            datePicked = extras.getString("datePicked");
            counter2 = extras.getInt("counter");
        }

        FirebaseApp.initializeApp(this);
        auth = FirebaseAuth.getInstance();
        db = FirebaseDatabase.getInstance();
        users = db.getReference("Users").child(auth.getUid());

        medListView = findViewById(R.id.listMed);
        medArrayList = new ArrayList<>();
        medicineAdapter = new TodayAdapter(TodayActivity.this, R.layout.today_item, medArrayList, users);
        medListView.setAdapter(medicineAdapter);
        all_taken = findViewById(R.id.all_taken);

        //Checks the medicins taken and return back to Calendar activity, where the decoration is set
        all_taken.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                users.child("dates").child(datePicked).setValue(datePicked).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            users.child("counterdate").setValue(counter2);
                            TodayActivity.this.finish();
                        }
                    }
                });

            }
        });

        // Gets the medicine info from realtime database, adds the info to medicineAdapter
        //Can be shown at the medlist where you can go from calendar
        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
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
}
