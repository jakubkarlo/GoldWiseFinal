package jakubkarlo.com.goldwise.Fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.Parse;
import com.parse.ParseObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import jakubkarlo.com.goldwise.Adapters.DebtAdapter;
import jakubkarlo.com.goldwise.Adapters.DebtorsAdapter;
import jakubkarlo.com.goldwise.Adapters.SavingAdapter;
import jakubkarlo.com.goldwise.Downloaders.EventDataDownloader;
import jakubkarlo.com.goldwise.Models.DebtState;
import jakubkarlo.com.goldwise.Models.DisplayCurrency;
import jakubkarlo.com.goldwise.R;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link DebtsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link DebtsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DebtsFragment extends Fragment {
    private static final String EVENT_ID = "";

    private String eventID;


    RecyclerView debtsListView;
    CheckBox paidCheckbox, notPaidCheckbox, forSavingCheckbox;
    TextView debtWhoPaid, debtTitle, debtAmount, debtDebtor;
    ImageButton closeButton;
    Button setAsPaidButton, setForSavingButton, deleteDebtButton;
    RecyclerView savingsListView;
    View.OnClickListener mClickListener;

    ArrayList<ParseObject> debts;

    private OnFragmentInteractionListener mListener;

    public DebtsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment DebtsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static DebtsFragment newInstance(String param1) {
        DebtsFragment fragment = new DebtsFragment();
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
        View root = inflater.inflate(R.layout.fragment_debts, container, false);
        debtsListView = (RecyclerView) root.findViewById(R.id.debtsListView);
        paidCheckbox = (CheckBox) root.findViewById(R.id.paidCheckbox);
        notPaidCheckbox = (CheckBox) root.findViewById(R.id.notPaidCheckbox);
        forSavingCheckbox = (CheckBox) root.findViewById(R.id.savingsCheckbox);
        return root;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        EventDataDownloader debtsDownloader = new EventDataDownloader();
        debts = null;


        try {
            debts = debtsDownloader.execute(eventID, "Debt").get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        debtsListView.setHasFixedSize(true);
        debtsListView.setLayoutManager(new LinearLayoutManager(getContext()));

        mClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ParseObject debt = debts.get(debtsListView.getChildAdapterPosition(v));
                createDialog(debt);
            }
        };


        DebtAdapter adapter = null;
        if (debts != null && !debts.isEmpty()) {
            adapter = new DebtAdapter(debts, debtsListView, mClickListener);
            debtsListView.setAdapter(adapter);
        }


        paidCheckbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                whichChecked();

            }
        });

        notPaidCheckbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                whichChecked();

            }
        });

        forSavingCheckbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                whichChecked();

            }
        });

    }


    public void createDialog(final ParseObject debt) {

        AlertDialog.Builder mBuilder = new AlertDialog.Builder(getActivity());
        View mView = getActivity().getLayoutInflater().inflate(R.layout.popup_debt, null);
        mBuilder.setView(mView);
        final AlertDialog dialog = mBuilder.create();

        debtTitle = (TextView) mView.findViewById(R.id.debtTitleTextView);
        debtAmount = (TextView) mView.findViewById(R.id.debtAmountTextView);
        debtWhoPaid = (TextView) mView.findViewById(R.id.debtWhoPaidTextView);
        debtDebtor = (TextView) mView.findViewById(R.id.debtorTextView);
        setForSavingButton = (Button) mView.findViewById(R.id.setForSavingButton);
        setAsPaidButton = (Button) mView.findViewById(R.id.setAsPaidButton);
        deleteDebtButton = (Button) mView.findViewById(R.id.deleteDebtButton);


        try {
            debtTitle.setText(debt.getString("title"));
            debtAmount.setText(String.format("%.02f", debt.getDouble("amount")) + " " + DisplayCurrency.currency);
            debtWhoPaid.setText("Paid by: " + debt.getJSONObject("toWhom").getString("name"));
            debtDebtor.setText("Debtor: " + debt.getJSONObject("who").getString("name"));
        } catch (Exception e) {
            e.printStackTrace();
        }

        //conditions for setting debt buttons
        if (debt.getNumber("status").equals(DebtState.DEBT_PAID)) {
            setAsPaidButton.setEnabled(false);
        } else if (debt.getNumber("status").equals(DebtState.DEBT_FOR_SAVING)) {
            setForSavingButton.setEnabled(false);
        }


        setAsPaidButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSetAsPaidAlert(debt);
            }
        });

        setForSavingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSavingsList(debt);
            }
        });

        deleteDebtButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteDebt(debt);
            }
        });

        closeButton = (ImageButton) mView.findViewById(R.id.closeDebtPopupButton);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });


        dialog.show();

    }


    public void showSavingsList(final ParseObject debt) {
        final AlertDialog.Builder mBuilder = new AlertDialog.Builder(getActivity());
        View mView = getActivity().getLayoutInflater().inflate(R.layout.popup_savings, null);
        savingsListView = (RecyclerView) mView.findViewById(R.id.savingsToAddRecyclerView);
        mBuilder.setView(mView);

        ParseObject event =  debt.getParseObject("eventID");

        EventDataDownloader savingsDownloader = new EventDataDownloader();
        ArrayList<ParseObject> savings = null;

        try {
            savings = savingsDownloader.execute(event.getObjectId(), "Saving").get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        savingsListView.setHasFixedSize(true);
        savingsListView.setLayoutManager(new LinearLayoutManager(getContext()));

        final ArrayList<ParseObject> finalSavings = savings;
        final View.OnClickListener itemClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ParseObject saving = finalSavings.get(savingsListView.getChildAdapterPosition(v));
                double amount = debt.getDouble("amount");
                double currentState = saving.getDouble("currentState");
                double goal = saving.getDouble("goal");

                if (currentState >= goal){
                    Toast.makeText(getContext(), "This goal is already reached", Toast.LENGTH_SHORT).show();
                }
                else {
//                    if (previousStatus == DebtState.DEBT_NOT_PAID) {
//                        debt.put("status", DebtState.DEBT_FOR_SAVING);
//                        ParseObject expense = ParseObject.createWithoutData("Expense", debt.getParseObject("expenseID").getObjectId());
//
//                    }
                    showSetForSavingAlert(saving, debt, amount);
//                    saving.put("currentState", saving.getDouble("currentState") + amount);
//                    saving.saveInBackground();
//                    updateDebt(debt, debt.getInt("status"), DebtState.DEBT_FOR_SAVING);
//                    Toast.makeText(getContext(), "Money added!", Toast.LENGTH_SHORT).show();
                }
            }
        };

        if (savings != null && !savings.isEmpty()) {
            SavingAdapter savingAdapter = new SavingAdapter(savings, savingsListView, getContext(), itemClickListener);
            savingsListView.setAdapter(savingAdapter);
        }

        final AlertDialog dialog = mBuilder.create();
        dialog.show();
    }

    public void showSetAsPaidAlert(final ParseObject debt) {

        new AlertDialog.Builder(getActivity())
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Setting as paid")
                .setMessage("Are you sure you want to change the debt status to PAID? After this operation debt cannot go back to NOT PAID state!")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        updateDebt(debt, debt.getInt("status"), DebtState.DEBT_PAID);
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


    public void showSetForSavingAlert(final ParseObject saving, final ParseObject debt, final double amount) {

        new AlertDialog.Builder(getActivity())
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Adding money to saving")
                .setMessage("Are you sure you want to add the money for saving? This operation is irreversible!")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        saving.put("currentState", saving.getDouble("currentState") + amount);
                        saving.saveInBackground();
                        updateDebt(debt, debt.getInt("status"), DebtState.DEBT_FOR_SAVING);
                        Toast.makeText(getContext(), "Money added!", Toast.LENGTH_SHORT).show();
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

    public void deleteDebt(final ParseObject debt){

        new AlertDialog.Builder(getActivity())
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Removing debt")
                .setMessage("Are you sure you want to delete this debt? If money was paid it will be lost!")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        debt.deleteInBackground();
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


    public void updateDebt(ParseObject debt, int previousStatus, int updatedStatus) {

        try {
            JSONObject debtor = debt.getJSONObject("who");
            debt.put("status", updatedStatus);
            ParseObject expense = ParseObject.createWithoutData("Expense", debt.getParseObject("expenseID").getObjectId());
            ParseObject event = ParseObject.createWithoutData("Event", expense.getParseObject("eventID").getObjectId());
            JSONArray eventParticipants = event.getJSONArray("participants");

            addShare(eventParticipants, previousStatus, debtor, debt, event, expense);

            event.saveInBackground();
            expense.saveInBackground();
            debt.saveInBackground();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    public void addShare(JSONArray eventParticipants, int previousStatus, JSONObject debtor, ParseObject debt, ParseObject event, ParseObject expense) throws JSONException {
        for (int i = 0; i < eventParticipants.length(); i++) {
            if (previousStatus == DebtState.DEBT_NOT_PAID) {
                if (eventParticipants.getJSONObject(i).getString("name").equals(debtor.getString("name"))) {
                    double currentShare = eventParticipants.getJSONObject(i).getDouble("share");
                    currentShare += debt.getDouble("amount");
                    eventParticipants.getJSONObject(i).put("share", currentShare);
                    event.put("participants", eventParticipants);
                    expense.put("participants", eventParticipants);
                }
            }
        }
    }


    public void whichChecked() {

        ArrayList<ParseObject> templist = new ArrayList<ParseObject>();
        DebtAdapter adapter;

        //when all are checked
        if (paidCheckbox.isChecked() && notPaidCheckbox.isChecked() && forSavingCheckbox.isChecked()) {

            adapter = new DebtAdapter(debts, debtsListView, mClickListener);
            debtsListView.setAdapter(adapter);

        }

        //when paid checkbox is checked
        else if (paidCheckbox.isChecked()) {

            //paid and not paid are checked
            if (notPaidCheckbox.isChecked()) {

                for (ParseObject temp : debts) {
                    if (temp.getNumber("status").equals(2) || temp.getNumber("status").equals(1)) {
                        templist.add(temp);
                    }
                }
                adapter = new DebtAdapter(templist, debtsListView, mClickListener);
                debtsListView.setAdapter(adapter);

            }

            //paid and for saving are checked
            else if (forSavingCheckbox.isChecked()) {

                for (ParseObject temp : debts) {
                    if (temp.getNumber("status").equals(2) || temp.getNumber("status").equals(3)) {
                        templist.add(temp);
                    }
                }
                adapter = new DebtAdapter(templist, debtsListView, mClickListener);
                debtsListView.setAdapter(adapter);

            }

            //only paid is checked
            else {
                for (ParseObject temp : debts) {
                    if (temp.getNumber("status").equals(2)) {
                        templist.add(temp);
                    }
                }
                adapter = new DebtAdapter(templist, debtsListView, mClickListener);
                debtsListView.setAdapter(adapter);
            }


        } else if (notPaidCheckbox.isChecked()) {

            // if not paid and for saving are checked
            if (forSavingCheckbox.isChecked()) {

                for (ParseObject temp : debts) {
                    if (temp.getNumber("status").equals(1) || temp.getNumber("status").equals(3)) {
                        templist.add(temp);
                    }
                }
                adapter = new DebtAdapter(templist, debtsListView, mClickListener);
                debtsListView.setAdapter(adapter);

            }

            // if only not paid is checked (other variants were done before)
            else {

                for (ParseObject temp : debts) {
                    if (temp.getNumber("status").equals(1)) {
                        templist.add(temp);
                    }
                }
                adapter = new DebtAdapter(templist, debtsListView, mClickListener);
                debtsListView.setAdapter(adapter);


            }

        }
        // if for saving is checked
        else if (forSavingCheckbox.isChecked()) {

            for (ParseObject temp : debts) {
                if (temp.getNumber("status").equals(3)) {
                    templist.add(temp);
                }
            }
            adapter = new DebtAdapter(templist, debtsListView, mClickListener);
            debtsListView.setAdapter(adapter);

        }

        // nothing is checked
        else {

            adapter = new DebtAdapter(templist, debtsListView, mClickListener);
            debtsListView.setAdapter(adapter);

        }


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
