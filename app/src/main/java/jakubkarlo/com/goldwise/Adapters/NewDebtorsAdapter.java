//package jakubkarlo.com.goldwise.Adapters;
//
//import android.content.Context;
//import android.database.DataSetObserver;
//import android.support.annotation.LayoutRes;
//import android.support.annotation.NonNull;
//import android.support.annotation.Nullable;
//import android.support.v7.widget.RecyclerView;
//import android.util.Log;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.ArrayAdapter;
//import android.widget.BaseAdapter;
//import android.widget.CheckBox;
//import android.widget.CompoundButton;
//import android.widget.EditText;
//import android.widget.ListView;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.concurrent.RecursiveAction;
//
//import jakubkarlo.com.goldwise.Models.Person;
//import jakubkarlo.com.goldwise.R;
//
///**
// * Created by Jakub on 27.12.2017.
// */
//
//public class NewDebtorsAdapter extends RecyclerView.Adapter {
//    private ArrayList<Person> people;
//    private RecyclerView debtorsRecyclerView;
//
//
//
//
//    private class DebtorsViewHolder extends RecyclerView.ViewHolder{
//
//        TextView debtorName;
//        CheckBox debtorCheckBox;
//        EditText debtAmount;
//
//        public DebtorsViewHolder(View itemView) {
//            super(itemView);
//            debtorName = (TextView) itemView.findViewById(R.id.debtorNameTextView);
//            debtorCheckBox = (CheckBox) itemView.findViewById(R.id.debtorCheckBox);
//            debtAmount = (EditText) itemView.findViewById(R.id.debtorAmountEditText);
//
//        }
//    }
//
//
//
//    public NewDebtorsAdapter(ArrayList<Person> people, RecyclerView recyclerView) {
//        this.people = people;
//        this.debtorsRecyclerView = recyclerView;
//    }
//
//
//
//    @Override
//    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//        View view = LayoutInflater.from(parent.getContext())
//                .inflate(R.layout.debtors_list_item, parent, false);
//
//        return new DebtorsViewHolder(view);
//    }
//
//    @Override
//    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
//        // to get the checked value in AddExpenseActivity
//        ((DebtorsViewHolder)holder).debtorCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                if (isChecked) {
//                    ((DebtorsViewHolder)holder).debtorCheckBox.setChecked(position, true);
//
//                }
//                else{
//                    ((ListView) parentView).setItemChecked(position, false);
//                }
//            }
//        });
//
//
//
//        ((DebtorsViewHolder)holder).debtorName.setText(people.get(position).getName());
//    }
//
//
//    @Override
//    public int getItemCount() {
//        return people.size();
//    }
//
//
//}
