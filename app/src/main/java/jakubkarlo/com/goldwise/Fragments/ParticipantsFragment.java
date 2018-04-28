package jakubkarlo.com.goldwise.Fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SearchView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import jakubkarlo.com.goldwise.Adapters.ParticipantsSpinnerAdapter;
import jakubkarlo.com.goldwise.Downloaders.ParticipantsDownloader;
import jakubkarlo.com.goldwise.Models.Person;
import jakubkarlo.com.goldwise.R;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ParticipantsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ParticipantsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ParticipantsFragment extends Fragment {
    private static final String EVENT_ID = "";
    private String eventID;
    private OnFragmentInteractionListener mListener;

    ListView participantsListView;
    SearchView searchPersonView;


    public ParticipantsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.

     * @return A new instance of fragment ParticipantsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ParticipantsFragment newInstance(String param1) {
        ParticipantsFragment fragment = new ParticipantsFragment();
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
        View root = inflater.inflate(R.layout.fragment_participants, container, false);
        searchPersonView = (SearchView) root.findViewById(R.id.searchPersonView);
        participantsListView = (ListView) root.findViewById(R.id.participantsListView);
        return root;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ParticipantsDownloader participantsDownloader = new ParticipantsDownloader();
        JSONArray participants = null;
        final ArrayList<Person> people = new ArrayList<>();


        try {
            participants = participantsDownloader.execute(eventID).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        //write all JSONs to Person Array and sum their shares
        if (participants != null) {

            for (int i = 0; i < participants.length(); i++) {

                try {
                    JSONObject currentPerson = participants.getJSONObject(i);
                    people.add(new Person(currentPerson.getString("name"), currentPerson.getString("phoneNumber"), currentPerson.getDouble("share"), currentPerson.getInt("color")));
                } catch (JSONException e) {
                    e.printStackTrace();
                }


                ParticipantsSpinnerAdapter participantsAdapter = null;
                if (!people.isEmpty()) {
                    participantsAdapter = new ParticipantsSpinnerAdapter(getContext(), people);
                    participantsListView.setAdapter(participantsAdapter);
                }
            }
        }

        searchPersonView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                ArrayList<Person> templist = new ArrayList<Person>();

                for (Person temp : people) {
                    if (temp.getName().toLowerCase().contains(newText.toLowerCase())) {
                        templist.add(temp);
                    }
                }
                ParticipantsSpinnerAdapter adapter = new ParticipantsSpinnerAdapter(getContext(),templist);
                participantsListView.setAdapter(adapter);
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
