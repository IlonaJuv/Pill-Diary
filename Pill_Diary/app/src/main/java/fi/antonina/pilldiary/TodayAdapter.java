package fi.antonina.pilldiary;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;

public class TodayAdapter extends BaseAdapter {

    TodayActivity context;
    int layout;
    ArrayList<MedicineType> medList;
    int position;
    DatabaseReference users;

    public TodayAdapter(TodayActivity context, int layout, ArrayList<MedicineType> medList, DatabaseReference users) {
        this.context = context;
        this.users = users;
        this.layout = layout;
        this.medList = medList;
    }

    @Override
    public int getCount() {
        return medList.size();
    }

    @Override
    public Object getItem(int i) {
        return i;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = inflater.inflate(layout, null);

        TextView medName = view.findViewById(R.id.medName);
        TextView medCapsule = view.findViewById(R.id.medCapsule);
        TextView medTime = view.findViewById(R.id.medTime);
        TextView medFeedback = view.findViewById(R.id.medFeedback);

        String index = medList.get(i).getIndex();
        // get position of item by i
        medName.setText(medList.get(i).getMedName());
        medCapsule.setText(medList.get(i).getMedAmount());
        medTime.setText(medList.get(i).getMedGetTime());
        medFeedback.setText(medList.get(i).getFeedBack());



        return view;
    }
}