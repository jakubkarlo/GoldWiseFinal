package jakubkarlo.com.goldwise.Fragments;

import android.content.Context;
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
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;

import com.parse.ParseObject;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import jakubkarlo.com.goldwise.Activities.AddSavingActivity;
import jakubkarlo.com.goldwise.Adapters.SavingAdapter;
import jakubkarlo.com.goldwise.Downloaders.EventDataDownloader;
import jakubkarlo.com.goldwise.R;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SavingsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SavingsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SavingsFragment extends Fragment {
    private static final String EVENT_ID = "";
    private String eventID;
    private OnFragmentInteractionListener mListener;

    RecyclerView savingsListView;
    SearchView searchSavingView;
    FloatingActionButton newSavingFAB;


    public SavingsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *

     * @return A new instance of fragment SavingsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SavingsFragment newInstance(String param1) {
        SavingsFragment fragment = new SavingsFragment();
        Bundle args = new Bundle();
        fragment.eventID = param1;
        args.putString(EVENT_ID, param1);
        fragment.setArguments(args);
        return fragment;
    }


    public void startAddSavingActivity(){

        Intent intent = new Intent(getActivity(), AddSavingActivity.class);
        intent.putExtra("eventID", eventID);
        startActivity(intent);

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_savings, container, false);
        savingsListView = (RecyclerView) root.findViewById(R.id.savingsListView);
        searchSavingView = (SearchView) root.findViewById(R.id.searchSavingView);
        newSavingFAB = (FloatingActionButton) root.findViewById(R.id.newSavingFAB);

        newSavingFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startAddSavingActivity();
            }
        });

        return root;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        EventDataDownloader savingsDownloader = new EventDataDownloader();
        ArrayList<ParseObject> savings = null;

        try {
            savings = savingsDownloader.execute(eventID, "Saving").get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        savingsListView.setHasFixedSize(true);
        savingsListView.setLayoutManager(new LinearLayoutManager(getContext()));


        if (savings != null && !savings.isEmpty()) {
            SavingAdapter savingAdapter = new SavingAdapter(savings, savingsListView, getContext(), null);
            savingsListView.setAdapter(savingAdapter);
        }


        final ArrayList<ParseObject> finalSavings = savings;
        searchSavingView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                ArrayList<ParseObject> templist = new ArrayList<ParseObject>();

                for (ParseObject temp : finalSavings) {
                    if (temp.getString("title").toLowerCase().contains(newText.toLowerCase())) {
                        templist.add(temp);
                    }
                }
                SavingAdapter savingAdapter = new SavingAdapter(templist, savingsListView, getContext(), null);
                savingsListView.setAdapter(savingAdapter);
                return true;
            }
        });


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
