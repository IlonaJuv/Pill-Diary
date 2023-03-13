package fi.antonina.pilldiary;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Locale;

public class PersonActivity extends AppCompatActivity {
    //initialize variable
    ImageView avatar;
    TextView title, userName, userAge, male, email;
    ImageButton logOut;
    AlertDialog dialog;

    FirebaseAuth auth;
    FirebaseDatabase db;
    DatabaseReference users;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.person);

        FirebaseApp.initializeApp(this);
        auth = FirebaseAuth.getInstance();
        db = FirebaseDatabase.getInstance();
        users = db.getReference("Users").child(auth.getUid());

        //Intitialize and Assign Variable
        BottomNavigationView navigationView = findViewById(R.id.bottom_nav);

        title = findViewById(R.id.title);
        avatar = findViewById(R.id.avatar);
        userName = findViewById(R.id.userName);
        email = findViewById(R.id.email);
        userAge = findViewById(R.id.ages);
        male = findViewById(R.id.male);
        logOut = findViewById(R.id.logOut);
        dialog = new AlertDialog.Builder(this, com.google.android.material.R.style.Base_V21_Theme_AppCompat_Dialog).create();
        EditText editText = new EditText(this);

        users.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                userName.setText(user.getName().toUpperCase());
                email.setText(user.getEmail());
                userAge.setText(user.getAge());

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        dialog.setView(editText);
        //edit user name
        userName.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                showNameWindow();

            }
        });
        //edit user age
        userAge.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view){
                showAgeWindow();

            }
        });

        Switch sw = (Switch) findViewById(R.id.switch1);

        sw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked) {
                    sw.setText("Male");
                } else {
                    sw.setText("Female");
                }
            }
        });
        //log out click
        logOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(PersonActivity.this);
                builder.setMessage("do you want to log out ?")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
//                                onDestroy();
                                Intent intent = new Intent(PersonActivity.this, MainActivity.class);
                                startActivity(intent);
                                FirebaseAuth.getInstance().signOut();
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.cancel();
                            }
                        });
                AlertDialog alert = builder.create();
                alert.setTitle("Please Confirm");
                alert.show();
            }
        });


        //Set icon selected
        navigationView.setSelectedItemId(R.id.person);

        //Perform ItemSelectedListener
        navigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @SuppressLint("NonConstantResourceId")
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();
                if (itemId == R.id.my_Medicine) {
                    startActivity(new Intent(getApplicationContext()
                            , MedicineActivity.class));
                    PersonActivity.this.finish();
                    overridePendingTransition(0, 0);
                    return true;
                } else if (itemId == R.id.person) {
                    return true;
                } else if (itemId == R.id.calendar) {
                    startActivity(new Intent(getApplicationContext()
                            , CalendarActivity.class));
                    PersonActivity.this.finish();
                    overridePendingTransition(0, 0);
                    return true;
                }
                return false;
            }
        });

    }


    public void showNameWindow(){
        AlertDialog.Builder dialog = new AlertDialog.Builder(PersonActivity.this);

        //created object
        LayoutInflater inflater = LayoutInflater.from(PersonActivity.this);
        View name_window = inflater.inflate(R.layout.personal_name, null);
        //as register window new layout
        dialog.setView(name_window);

        EditText name = name_window.findViewById(R.id.name);

        dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                userName.setText(userName.getText().toString());
                dialogInterface.cancel();
            }
        }).setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
              //  Kaksi alempaa riviä vanha koodi
                 String newName = name.getText().toString().trim();
                 userName.setText(newName.toUpperCase());
                 Toast.makeText(PersonActivity.this, "This data has been updated", Toast.LENGTH_SHORT).show();

                 //Adds changes to database
                 users.child("name").setValue(userName.getText().toString()) ;


            }
        });
        dialog.show();
    }

    public void showAgeWindow(){
        AlertDialog.Builder dialog = new AlertDialog.Builder(PersonActivity.this);

        //created object
        LayoutInflater inflater = LayoutInflater.from(PersonActivity.this);
        View age_window = inflater.inflate(R.layout.personal_age, null);
        //as register window new layout
        dialog.setView(age_window);

        EditText age = age_window.findViewById(R.id.age);

        dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                userAge.setText(userAge.getText().toString());
                dialogInterface.cancel();
            }
        }).setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String newAge = age.getText().toString().toUpperCase(Locale.ROOT);
                userAge.setText(newAge);

              //  dialogInterface.cancel();
                Toast.makeText(PersonActivity.this, "This data has been updated", Toast.LENGTH_SHORT).show();
                //Adds changes to database
                users.child("age").setValue(userAge.getText().toString());
            }
        });
        dialog.show();

    }

}
