package jakubkarlo.com.goldwise.Adapters;

import android.content.Context;
import android.content.res.ColorStateList;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.parse.ParseObject;

import java.util.ArrayList;

import jakubkarlo.com.goldwise.Models.DisplayCurrency;
import jakubkarlo.com.goldwise.R;

/**
 * Created by Jakub on 18.01.2018.
 */

public class SavingAdapter extends RecyclerView.Adapter {

    private ArrayList<ParseObject> savings;
    private Context context;
    private RecyclerView recyclerView;
    private View.OnClickListener itemClickListener;
    private boolean isClickable;


    private class SavingViewHolder extends RecyclerView.ViewHolder {

        private TextView title, amountLeft, goalAmount, currentState;
        private ProgressBar progressBar;

        public SavingViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.savingTitleTextView);
            amountLeft = (TextView) itemView.findViewById(R.id.amountLeftTextView);
            goalAmount = (TextView) itemView.findViewById(R.id.goalAmountTextView);
            currentState = (TextView) itemView.findViewById(R.id.currentStateTextView);
            progressBar = (ProgressBar) itemView.findViewById(R.id.savingProgressBar);
        }
    }


    public SavingAdapter(ArrayList<ParseObject> savings, RecyclerView recyclerView, Context context, View.OnClickListener itemClickListener){
        this.savings = savings;
        this.recyclerView = recyclerView;
        this.context = context;
        this.itemClickListener = itemClickListener;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.savings_list_item, parent, false);

        return new SavingViewHolder(view);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        SavingViewHolder savingHolder = ((SavingViewHolder) holder);

        savingHolder.title.setText(savings.get(position).getString("title"));
        savingHolder.progressBar.setProgress((int)savings.get(position).getDouble("currentState"));
        savingHolder.progressBar.setMax((int)savings.get(position).getDouble("goal"));
        savingHolder.goalAmount.setText(String.format("%.02f", savings.get(position).getDouble("goal")) + " " + DisplayCurrency.currency);
        savingHolder.currentState.setText(String.format("%.02f", savings.get(position).getDouble("currentState")) + " " + DisplayCurrency.currency);
        double toGo = (savings.get(position).getDouble("goal") - savings.get(position).getDouble("currentState"));
        String textToDisplay;
        if (savingHolder.progressBar.getProgress() >= savingHolder.progressBar.getMax()){
            textToDisplay = context.getResources().getString(R.string.goalReached_textView);
            savingHolder.progressBar.setProgressTintList(ColorStateList.valueOf(context.getResources().getColor(R.color.colorPrimary)));

        }
        else {
            textToDisplay = context.getResources().getString(R.string.amountLeft_textView) + " " + String.format("%.02f", toGo)  + " " + DisplayCurrency.currency;
            savingHolder.progressBar.setProgressTintList(ColorStateList.valueOf(context.getResources().getColor(R.color.progressGreen)));
        }

        savingHolder.amountLeft.setText(textToDisplay);

        if (itemClickListener!= null){
            savingHolder.itemView.setOnClickListener(itemClickListener);
        }

    }


    @Override
    public int getItemCount() {
        return savings.size();
    }

//    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
//    @Override
//    public View getView(int position, View convertView, ViewGroup parent) {
//
//        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//
//        if (convertView == null) {
//            convertView = inflater.inflate(R.layout.savings_list_item, null);
//
//
//
//            title.setText(savings.get(position).getString("title"));
//            progressBar.setProgress((int)savings.get(position).getDouble("currentState"));
//            progressBar.setMax((int)savings.get(position).getDouble("goal"));
//            goalAmount.setText(String.valueOf(savings.get(position).getDouble("goal")));
//            currentState.setText(String.valueOf(savings.get(position).getDouble("currentState")));
//            double toGo = (savings.get(position).getDouble("goal") - savings.get(position).getDouble("currentState"));
//            String textToDisplay;
//            if (progressBar.getProgress() >= progressBar.getMax()){
//                textToDisplay = context.getResources().getString(R.string.goalReached_textView);
//                progressBar.setProgressTintList(ColorStateList.valueOf(context.getResources().getColor(R.color.colorPrimary)));
//
//            }
//            else {
//                textToDisplay = context.getResources().getString(R.string.amountLeft_textView) + " " + String.valueOf(toGo);
//                progressBar.setProgressTintList(ColorStateList.valueOf(context.getResources().getColor(R.color.progressGreen)));
//            }
//
//            amountLeft.setText(textToDisplay);
//
//        }
//
//
//
//        return convertView;
//    }
}
