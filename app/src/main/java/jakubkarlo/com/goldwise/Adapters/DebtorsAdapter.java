package jakubkarlo.com.goldwise.Adapters;

import android.content.Context;
import android.database.DataSetObserver;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import jakubkarlo.com.goldwise.Models.Person;
import jakubkarlo.com.goldwise.R;

/**
 * Created by Jakub on 27.12.2017.
 */

public class DebtorsAdapter extends BaseAdapter {
    private ArrayList<Person> people;
    private Context context;

    private class DebtorsHolder {

        private TextView debtorName;
        private CheckBox debtorCheckBox;

        public DebtorsHolder(View itemView){
            debtorName = (TextView) itemView.findViewById(R.id.debtorNameTextView);
            debtorCheckBox = (CheckBox) itemView.findViewById(R.id.debtorCheckBox);
        }

    }

    public DebtorsAdapter(Context context, ArrayList<Person> people) {
        this.context = context;
        this.people = people;
    }

    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final ViewGroup parentView = parent;
        final int itemPosition = position;
        DebtorsHolder holder;

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.debtors_list_item, null);
            holder = new DebtorsHolder(convertView);
            convertView.setTag(holder);
        }
            else{
               holder = (DebtorsHolder) convertView.getTag();
            }


            // to get the checked value in AddExpenseActivity
            holder.debtorCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        ((ListView) parentView).setItemChecked(itemPosition, true);

                    }
                    else{
                        ((ListView) parentView).setItemChecked(itemPosition, false);
                    }
                }
            });

            holder.debtorName.setText(people.get(position).getName());


        return convertView;

    }

    @Override
    public boolean isEnabled(int position) {
        return super.isEnabled(position);
    }

    //    public View setAmount(double amount, int position, View convertView){
//
//        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//        if (convertView == null) {
//            convertView = inflater.inflate(R.layout.debtors_list_item, null);
//
//            debtAmount.setText(String.valueOf(amount));
//        }
//        return convertView;
//    }


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



}
