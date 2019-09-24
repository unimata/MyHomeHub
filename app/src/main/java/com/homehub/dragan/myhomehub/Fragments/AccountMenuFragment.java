package com.homehub.dragan.myhomehub.Fragments;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.homehub.dragan.myhomehub.Activities.AccountSubActivity;
import com.homehub.dragan.myhomehub.R;

public class AccountMenuFragment extends ListFragment {

    //private OnFragmentInteractionListener mListener;
    private ListView accMenuListView;

    public AccountMenuFragment() {
        // Required empty public constructor
    }

    public static AccountMenuFragment newInstance(String param1, String param2) {
        AccountMenuFragment fragment = new AccountMenuFragment();
        Bundle args = new Bundle();
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
        View view = inflater.inflate(R.layout.fragment_account_menu, container, false);

        //get view id of menu list view
        accMenuListView = view.findViewById(R.id.list);

        //set up array adapter for the list view
        ArrayAdapter<CharSequence> accMenuOptionArray = ArrayAdapter.createFromResource(getActivity().getApplicationContext(), R.array.AccountMenuOptions, android.R.layout.simple_list_item_1);
        accMenuListView.setAdapter(accMenuOptionArray);

        return view;
    }

    @Override
    public void onResume(){
        super.onResume();

        //re-enable list view to prevent double click
        accMenuListView.setEnabled(true);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        //disable after click to prevent double click
        l.setEnabled(false);

        //setup intent for activity load
        Bundle localBundle = new Bundle();
        Intent myIntent = new Intent(getActivity(), AccountSubActivity.class);

        //setup local bundle to store selected ListItem
        localBundle.putInt("selectedListItem", position);
        myIntent.putExtras(localBundle);

        startActivity(myIntent);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        //if (mListener != null) {
          //  mListener.onFragmentInteraction(uri);
        //}
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        //if (context instanceof OnFragmentInteractionListener) {
          //  mListener = (OnFragmentInteractionListener) context;
        //} else {
          //  throw new RuntimeException(context.toString()
                    //+ " must implement OnFragmentInteractionListener");
        //}
    }

    @Override
    public void onDetach() {
        super.onDetach();
        //mListener = null;
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
    //public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
      //  void onFragmentInteraction(Uri uri);
    //}
}
