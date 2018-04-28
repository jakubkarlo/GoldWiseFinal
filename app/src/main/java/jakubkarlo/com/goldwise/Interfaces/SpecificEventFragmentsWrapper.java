package jakubkarlo.com.goldwise.Interfaces;

import jakubkarlo.com.goldwise.Fragments.DebtsFragment;
import jakubkarlo.com.goldwise.Fragments.EventOverviewFragment;
import jakubkarlo.com.goldwise.Fragments.ExpensesFragment;
import jakubkarlo.com.goldwise.Fragments.ParticipantsFragment;
import jakubkarlo.com.goldwise.Fragments.SavingsFragment;

/**
 * Created by Jakub on 05.12.2017.
 */

//Wrapper interface for all fragments interaction listeners in SpecificEventActivity

public interface SpecificEventFragmentsWrapper extends ExpensesFragment.OnFragmentInteractionListener, EventOverviewFragment.OnFragmentInteractionListener, DebtsFragment.OnFragmentInteractionListener, ParticipantsFragment.OnFragmentInteractionListener, SavingsFragment.OnFragmentInteractionListener {
}
