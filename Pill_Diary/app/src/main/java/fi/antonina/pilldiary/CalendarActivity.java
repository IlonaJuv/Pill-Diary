package fi.antonina.pilldiary;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;

import org.threeten.bp.LocalDate;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CalendarActivity extends AppCompatActivity {


    final List<String> dateList = new ArrayList<String>();
    final String DATE_FORMAT = "dd-MM-yyyy";
    int blue = 0;
    MaterialCalendarView calendarView;
    Button remiderButton;

    FirebaseAuth auth;
    FirebaseDatabase db;
    DatabaseReference users;
    long dateCounter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.calendar);

        FirebaseApp.initializeApp(this);
        auth = FirebaseAuth.getInstance();
        db = FirebaseDatabase.getInstance();
        users = db.getReference("Users").child(auth.getUid());


        calendarView = findViewById(R.id.calendarView);
        calendarView.setShowOtherDates(MaterialCalendarView.SHOW_ALL);
        // Calendar set for a year
        final LocalDate min = getLocalDate("01-01-2022");
        final LocalDate max = getLocalDate("31-12-2022");

        calendarView.state().edit().setMinimumDate(min).setMaximumDate(max).commit();
        calendarView.setOnDateChangedListener(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(@NonNull MaterialCalendarView materialCalendarView, @NonNull CalendarDay calendarDay, boolean b) {

                Intent intent = new Intent(CalendarActivity.this, TodayActivity.class);
                intent.putExtra("datePicked", calendarDay.getDay()+"-"+calendarDay.getMonth()+"-"+calendarDay.getYear());
                intent.putExtra("counter", dateCounter+1);

                startActivity(intent);
            }
        });


        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if(dataSnapshot.child("counterdate").getValue(Integer.class)!=null){
                    dateCounter = dataSnapshot.child("counterdate").getValue(Integer.class);
                    dateList.clear();
                    for(DataSnapshot ds : dataSnapshot.child("dates").getChildren()) {
                        String date =ds.getValue(String.class);
                        dateList.add(date);
                        Log.d("planeta", "onDataChange: " + date);
                    }
                }
                setEvent(dateList, blue);
                calendarView.invalidateDecorators();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("planeta", "onDataChange: " + databaseError.getMessage());
            }
        };
        users.addValueEventListener(postListener);


        BottomNavigationView navigationView = findViewById(R.id.bottom_nav);
        navigationView.setSelectedItemId(R.id.calendar);
        navigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();
                if (itemId == R.id.calendar) {
                    return true;
                } else if (itemId == R.id.person) {
                    startActivity(new Intent(getApplicationContext()
                            , PersonActivity.class));

                    CalendarActivity.this.finish();
                    overridePendingTransition(0, 0);
                    return true;
                } else if (itemId == R.id.my_Medicine) {
                    startActivity(new Intent(getApplicationContext()
                            , MedicineActivity.class));
                    CalendarActivity.this.finish();

                    overridePendingTransition(0, 0);
                    return true;
                }
                return false;
            }
        });

        remiderButton = findViewById(R.id.remiderButton);
        remiderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CalendarActivity.this, RemiderActivity.class);
                CalendarActivity.this.startActivity(intent);
            }
        });
    }

    // Method for a defined calendar decoration
    void setEvent(List<String> dateList, int color) {

        List<LocalDate> localDateList = new ArrayList<>();

        for (String string : dateList) {
            LocalDate calendar = getLocalDate(string);
            if (calendar != null) {
                localDateList.add(calendar);
            }
        }

        List<CalendarDay> datesLeft = new ArrayList<>();
        List<CalendarDay> datesCenter = new ArrayList<>();
        List<CalendarDay> datesRight = new ArrayList<>();
        List<CalendarDay> datesIndependent = new ArrayList<>();


        for (LocalDate localDate : localDateList) {
            boolean right = false;
            boolean left = false;

            for (LocalDate day1 : localDateList) {
                if (localDate.isEqual(day1.plusDays(1))) {
                    left = true;
                }
                if (day1.isEqual(localDate.plusDays(1))) {
                    right = true;
                }
            }

            if (left && right) {
                datesCenter.add(CalendarDay.from(localDate));
            } else if (left) {
                datesLeft.add(CalendarDay.from(localDate));
            } else if (right) {
                datesRight.add(CalendarDay.from(localDate));
            } else {
                datesIndependent.add(CalendarDay.from(localDate));
            }
        }

        if (color == blue) {
            setDecor(datesCenter, R.drawable.p_center);
            setDecor(datesLeft, R.drawable.p_left);
            setDecor(datesRight, R.drawable.p_right);
            setDecor(datesIndependent, R.drawable.p_independent);
        }
    }

    void setDecor(List<CalendarDay> calendarDayList, int drawable) {
        calendarView.addDecorators(new EventDecorator(CalendarActivity.this
                , drawable
                , calendarDayList));
    }

    LocalDate getLocalDate(String date) {
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT, Locale.ENGLISH);
        try {
            Date input = sdf.parse(date);
            Calendar cal = Calendar.getInstance();
            cal.setTime(input);
            return LocalDate.of(cal.get(Calendar.YEAR),
                    cal.get(Calendar.MONTH) + 1,
                    cal.get(Calendar.DAY_OF_MONTH));


        } catch (NullPointerException e) {
            return null;
        } catch (ParseException e) {
            return null;
        }
    }
}