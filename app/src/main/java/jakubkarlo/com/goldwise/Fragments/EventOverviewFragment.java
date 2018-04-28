package jakubkarlo.com.goldwise.Fragments;

import android.content.Context;
import android.content.res.ColorStateList;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.parse.ParseObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import jakubkarlo.com.goldwise.Downloaders.EventBudgetDownloader;
import jakubkarlo.com.goldwise.Downloaders.OverviewSavingDataDownloader;
import jakubkarlo.com.goldwise.Downloaders.ParticipantsDownloader;
import jakubkarlo.com.goldwise.Models.DisplayCurrency;
import jakubkarlo.com.goldwise.Models.Person;
import jakubkarlo.com.goldwise.R;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link EventOverviewFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link EventOverviewFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EventOverviewFragment extends Fragment {

    PieChart shareChart;
    ProgressBar budgetBar;
    TextView budgetInfoTextView, maxBudgetTextView;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String EVENT_ID = "";


    // TODO: Rename and change types of parameters
    private String eventID;


    private OnFragmentInteractionListener mListener;

    public EventOverviewFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @return A new instance of fragment EventOverviewFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static EventOverviewFragment newInstance(String param1) {
        EventOverviewFragment fragment = new EventOverviewFragment();
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
        View root = inflater.inflate(R.layout.fragment_event_overview, container, false);
        shareChart = (PieChart) root.findViewById(R.id.shareChart);
        budgetBar = (ProgressBar) root.findViewById(R.id.budgetProgressBar);
        budgetInfoTextView = (TextView) root.findViewById(R.id.budgetInfoTextView);

        return root;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ParticipantsDownloader participantsDownloader = new ParticipantsDownloader();
        JSONArray participants = null;
        ArrayList<Person> people = new ArrayList<>();

        try {
            participants = participantsDownloader.execute(EventOverviewFragment.this.eventID).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        //write all JSONs to Person Array and sum their shares
        if (participants != null) {

            double sharesSum = 0;
            for (int i = 0; i < participants.length(); i++) {

                try {
                    JSONObject currentPerson = participants.getJSONObject(i);
                    people.add(new Person(currentPerson.getString("name"), currentPerson.getString("phoneNumber"), currentPerson.getDouble("share"), currentPerson.getInt("color")));
                    sharesSum += currentPerson.getDouble("share");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
            // zobaczymy czy to będzie w ogóle potrzebne
//            sharesSum = (double) Math.round(sharesSum * 100) / 100;

            //pie cost participation chart
            ArrayList<PieEntry> entries = new ArrayList<>();
            ArrayList<Integer> colors = new ArrayList<>();

            for (Person person : people) {
                entries.add(new PieEntry((float) person.getShare(), person.getName()));
                colors.add(person.getColor());

            }

            PieDataSet sharesDataSet = new PieDataSet(entries, null);
            sharesDataSet.setColors(colors);
            sharesDataSet.setValueTextSize(10f);

            PieData sharesData = new PieData(sharesDataSet);
            shareChart.getDescription().setEnabled(false);
            shareChart.setDrawEntryLabels(false);
            shareChart.setData(sharesData);


            // asynchronous data downloaders
            EventBudgetDownloader budgetDownloader = new EventBudgetDownloader();

            //budget progress bar
            double eventBudget = 0;
            try {
                eventBudget = budgetDownloader.execute(eventID).get();
                eventBudget = (double) Math.round(eventBudget * 100) / 100;
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }

            budgetInfoTextView.setText(getResources().getString(R.string.budgetLeft_textView) + " " + String.format("%.02f", eventBudget - sharesSum) + " " + DisplayCurrency.currency);

            if (eventBudget > 0) {
                budgetBar.setMax((int) eventBudget);
                budgetBar.setProgress((int) sharesSum);
            }

            if (sharesSum >= eventBudget) {
                budgetBar.setProgressTintList(ColorStateList.valueOf(getResources().getColor(R.color.red)));
                budgetInfoTextView.setText(getResources().getString(R.string.overBudget_textView) + " " + String.format("%.02f", sharesSum - eventBudget) + " " + DisplayCurrency.currency);
            }


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
