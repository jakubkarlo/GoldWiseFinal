package jakubkarlo.com.goldwise.Fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import jakubkarlo.com.goldwise.Activities.AddExpenseActivity;
import jakubkarlo.com.goldwise.Activities.SpecificEventActivity;
import jakubkarlo.com.goldwise.Adapters.ExpenseAdapter;
import jakubkarlo.com.goldwise.Downloaders.EventDataDownloader;
import jakubkarlo.com.goldwise.Models.DisplayCurrency;
import jakubkarlo.com.goldwise.R;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ExpensesFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ExpensesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ExpensesFragment extends Fragment {
    private static final String EVENT_ID = "";
    private String eventID;

    private OnFragmentInteractionListener mListener;

    // UI elements
    RecyclerView expensesListView;
    SearchView searchExpenseView;
    FloatingActionButton fab;

    //for dialog
    TextView expenseTitle, expenseAmount, expenseWhoPaid;
    Button deleteButton;
    ImageButton closeButton;

    public ExpensesFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *

     * @return A new instance of fragment ExpensesFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ExpensesFragment newInstance(String param1) {
        ExpensesFragment fragment = new ExpensesFragment();
        Bundle args = new Bundle();
        fragment.eventID = param1;
        args.putString(EVENT_ID, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_expenses, container, false);
        expensesListView = (RecyclerView) root.findViewById(R.id.expensesListView);
        searchExpenseView = (SearchView) root.findViewById(R.id.searchExpenseView);
        fab = (FloatingActionButton) root.findViewById(R.id.newExpenseFAB);

        return root;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);



        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), AddExpenseActivity.class);
                intent.putExtra("eventID", getActivity().getIntent().getStringExtra("eventID"));
                startActivity(intent);
            }
        });


        // says it does too much work on a main thread
        EventDataDownloader expensesDownloader = new EventDataDownloader();
        ArrayList<ParseObject> expenses = null;

        try {
            expenses = expensesDownloader.execute(eventID, "Expense").get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        final ArrayList<ParseObject> finalExpenses = expenses;
        final View.OnClickListener itemClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ParseObject expense = null;
                if (finalExpenses != null) {
                    expense = finalExpenses.get(expensesListView.getChildAdapterPosition(v));
                }
                createDialog(expense);
            }
        };

        expensesListView.setHasFixedSize(true);
        expensesListView.setLayoutManager(new LinearLayoutManager(getContext()));
        ExpenseAdapter expenseAdapter = null;
        if (expenses != null && !expenses.isEmpty()) {
            expenseAdapter = new ExpenseAdapter(expenses, getContext(), itemClickListener);
            expensesListView.setAdapter(expenseAdapter);
        }

        //set search expense bar
        final ArrayList<ParseObject> finalExpenses1 = expenses;
        searchExpenseView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                ArrayList<ParseObject> templist = new ArrayList<ParseObject>();

                for (ParseObject temp : finalExpenses1) {
                    if (temp.getString("title").toLowerCase().contains(newText.toLowerCase())) {
                        templist.add(temp);
                    }
                }
                ExpenseAdapter adapter = new ExpenseAdapter(templist, getContext(), itemClickListener);
                expensesListView.setAdapter(adapter);
                return true;
            }
        });




    }


    public void createDialog(final ParseObject expense){

        AlertDialog.Builder mBuilder = new AlertDialog.Builder(getActivity());
        View mView = getActivity().getLayoutInflater().inflate(R.layout.popup_expense, null);
        mBuilder.setView(mView);
        final AlertDialog dialog = mBuilder.create();

        expenseTitle = (TextView) mView.findViewById(R.id.expenseTitleTextView);
        expenseAmount = (TextView) mView.findViewById(R.id.expenseAmountTextView);
        expenseWhoPaid = (TextView) mView.findViewById(R.id.expenseWhoPaidTextView);

        expenseTitle.setText(expense.getString("title"));
        expenseAmount.setText("Price: " + String.format("%.02f", expense.getDouble("amount")) + " " + DisplayCurrency.currency);
        try {
            expenseWhoPaid.setText("Paid by: " + expense.getJSONObject("whoPaid").get("name").toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        closeButton = (ImageButton)mView.findViewById(R.id.closeExpensePopupButton);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        deleteButton = (Button) mView.findViewById(R.id.deleteExpenseButton);


        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDeleteExpenseDialog(expense);
                dialog.dismiss();

            }
        });


        dialog.show();

    }


    public void deleteExpense(ParseObject expense){

        expense.deleteInBackground();
        try {
            deleteRelatedDebts(expense);
        } catch (ParseException e) {
            e.printStackTrace();
        }

//        expensesListView.getAdapter().notifyItemRemoved(expensesListView.getChildAdapterPosition(expensesListView));

    }

    public void deleteRelatedDebts(ParseObject expense) throws ParseException {

        ParseObject expensePointer = ParseObject.createWithoutData("Expense", expense.getObjectId());
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Debt");
        query.include("expenseID")
                .whereEqualTo("expenseID", expensePointer)
                .findInBackground(new FindCallback<ParseObject>() {
                    @Override
                    public void done(List<ParseObject> objects, ParseException e) {

                        if (e == null) {
                            for (ParseObject object:objects){
                                object.deleteInBackground();
                            }
                        }
                        else{
                            e.printStackTrace();
                        }
                    }
                });



    }

    public void showDeleteExpenseDialog(final ParseObject expense){

        new AlertDialog.Builder(getActivity())
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Deleting expense")
                .setMessage("Are you sure you want to delete this expense? Paid debts would not be returned!")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteExpense(expense);
                        dialog.dismiss();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .show();

    }




    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
