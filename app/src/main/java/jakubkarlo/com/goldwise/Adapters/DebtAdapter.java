package jakubkarlo.com.goldwise.Adapters;

import android.content.Context;
import android.content.res.ColorStateList;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.parse.Parse;
import com.parse.ParseObject;

import org.json.JSONException;

import java.util.ArrayList;

import jakubkarlo.com.goldwise.Models.DebtState;
import jakubkarlo.com.goldwise.Models.DisplayCurrency;
import jakubkarlo.com.goldwise.R;

/**
 * Created by Jakub on 19.01.2018.
 */

public class DebtAdapter extends RecyclerView.Adapter {

    private ArrayList<ParseObject> debts;
    private RecyclerView debtsRecyclerView;
    private View.OnClickListener mClickListener;

    private class DebtViewHolder extends RecyclerView.ViewHolder {

        private TextView title, who, toWhom, amountToPay, debtStatus;

        public DebtViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.debtTitleTextView);
            who = (TextView) itemView.findViewById(R.id.whoTextView);
            toWhom = (TextView) itemView.findViewById(R.id.toWhomTextView);
            amountToPay = (TextView) itemView.findViewById(R.id.amountToPayTextView);
            debtStatus = (TextView) itemView.findViewById(R.id.debtStatusTextView);
        }
    }


    public DebtAdapter(ArrayList<ParseObject> debts, RecyclerView recyclerView, View.OnClickListener clickListener) {
        this.debts = debts;
        this.debtsRecyclerView = recyclerView;
        this.mClickListener = clickListener;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.debts_list_item, parent, false);

        return new DebtViewHolder(view);

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ((DebtViewHolder)holder).title.setText(debts.get(position).getString("title"));
        ((DebtViewHolder)holder).amountToPay.setText(String.format("%.02f", debts.get(position).getDouble("amount")));
        try {
            ((DebtViewHolder)holder).who.setText(debts.get(position).getJSONObject("who").getString("name"));
            ((DebtViewHolder)holder).toWhom.setText(debts.get(position).getJSONObject("toWhom").getString("name"));
            ViewCompat.setBackgroundTintList(((DebtViewHolder)holder).toWhom, (ColorStateList.valueOf(debts.get(position).getJSONObject("toWhom").getInt("color"))));
            ViewCompat.setBackgroundTintList(((DebtViewHolder)holder).who, (ColorStateList.valueOf(debts.get(position).getJSONObject("who").getInt("color"))));

            int status = debts.get(position).getInt("status");

            switch (status){
                case DebtState.DEBT_PAID:
                    ((DebtViewHolder)holder).debtStatus.setText("Paid");
                    break;
                case DebtState.DEBT_NOT_PAID:
                    ((DebtViewHolder)holder).debtStatus.setText("Not paid");
                    break;
                case DebtState.DEBT_FOR_SAVING:
                    ((DebtViewHolder)holder).debtStatus.setText("For saving");
                    break;
                default:
                    ((DebtViewHolder)holder).debtStatus.setText("");

            }


            if (mClickListener!= null) {
                holder.itemView.setOnClickListener(mClickListener);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return debts.size();
    }

}
