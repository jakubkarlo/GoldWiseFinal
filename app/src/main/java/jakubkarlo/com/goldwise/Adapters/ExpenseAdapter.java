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

import jakubkarlo.com.goldwise.Models.DisplayCurrency;
import jakubkarlo.com.goldwise.R;

/**
 * Created by Jakub on 19.01.2018.
 */

public class ExpenseAdapter extends RecyclerView.Adapter {

    private ArrayList<ParseObject> expenses;
    private Context context;
    private View.OnClickListener itemClickListener;

    private class ExpenseViewHolder extends RecyclerView.ViewHolder {

        private TextView title, whoPaid, amount;
        private LinearLayout background;

        public ExpenseViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.expenseTitleTextView);
            amount = (TextView) itemView.findViewById(R.id.expenseAmountTextView);
            whoPaid = (TextView) itemView.findViewById(R.id.expenseWhoPaidTextView);
            background = (LinearLayout) itemView.findViewById(R.id.expenseItemBg);
        }
    }



    public ExpenseAdapter(ArrayList<ParseObject> expenses, Context context, View.OnClickListener itemClickListener){

        this.expenses = expenses;
        this.context = context;
        this.itemClickListener = itemClickListener;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.expenses_list_item, parent, false);

        return new ExpenseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ((ExpenseViewHolder)holder).title.setText(expenses.get(position).getString("title").toUpperCase());
        ((ExpenseViewHolder)holder).amount.setText(context.getResources().getString(R.string.price_textView) + " " + String.format("%.02f", expenses.get(position).getDouble("amount")) + " " + DisplayCurrency.currency);
        try {
            ((ExpenseViewHolder)holder).whoPaid.setText(context.getResources().getString(R.string.paidBy_textView) + " " + expenses.get(position).getJSONObject("whoPaid").getString("name"));
            ViewCompat.setBackgroundTintList(((ExpenseViewHolder)holder).background, (ColorStateList.valueOf(expenses.get(position).getJSONObject("whoPaid").getInt("color"))));


        } catch (JSONException e) {
            e.printStackTrace();
        }
        ((ExpenseViewHolder)holder).itemView.setOnClickListener(itemClickListener);

    }

    @Override
    public int getItemCount() {
        return expenses.size();
    }

}
