package jakubkarlo.com.goldwise.Adapters;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.view.ViewCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import jakubkarlo.com.goldwise.Models.Person;
import jakubkarlo.com.goldwise.R;

/**
 * Created by Jakub on 18.01.2018.
 */

public class ParticipantsSpinnerAdapter extends BaseAdapter {

    private ArrayList<Person> people;
    private Context context;
    private TextView name, phoneNumber;
    LinearLayout background;

    public ParticipantsSpinnerAdapter(Context context, ArrayList<Person> people) {
        this.context = context;
        this.people = people;
    }


    @Override
    public int getCount() {
        return people.size();
    }

    @Override
    public Object getItem(int position) {
        return people.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.participants_list_item, null);

            name = (TextView) convertView.findViewById(R.id.participantNameTextView);
            phoneNumber = (TextView) convertView.findViewById(R.id.participantPhoneTextView);
            name.setText(people.get(position).getName());
            phoneNumber.setText(people.get(position).getPhoneNumber());
//            name.setTextColor(people.get(position).getColor());
//            phoneNumber.setTextColor(people.get(position).getColor());
            background = (LinearLayout) convertView.findViewById(R.id.pariticpantsItemBg);
            ViewCompat.setBackgroundTintList(background, (ColorStateList.valueOf(people.get(position).getColor())));
//
        }

        return convertView;
    }
}
