package com.example.yassine.sunlamp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListAdapter;

import com.example.yassine.sunlamp.Adapter.ColorDataAdapter;
import com.example.yassine.sunlamp.Bluetooth.Scanner.DeviceScanActivity;
import com.example.yassine.sunlamp.Model.ColorData;
import com.getbase.floatingactionbutton.AddFloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;

import java.util.List;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Large screen devices (such as tablets) are supported by replacing the ListView
 * with a GridView.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnElementSelectedListener}
 * interface.
 */
public class MySearchFragment extends Fragment implements AbsListView.OnItemClickListener {

    private OnElementSelectedListener mListener;

    private Button mBluetoothButton;

    private FloatingActionsMenu menuMultipleActions;
    private AddFloatingActionButton mAddNewItemButton;
    /**
     * Code to get device
     */
    private static int DEVICE_GET_CODE = 100;

    /**
     * The fragment's ListView/GridView.
     */
    private AbsListView mListView;

    /**
     * The Adapter which will be used to populate the ListView/GridView with
     * Views.
     */
    private ArrayAdapter mAdapter;

    List<ColorData> mListOfColors = null;

    private DatabaseOperations mDatabaseOperation;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public MySearchFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDatabaseOperation = new DatabaseOperations(getActivity());
        mAdapter = new ColorDataAdapter(getActivity(), mListOfColors);
    }

    public void updateList(){
        if(mListOfColors != null)
            if(!mListOfColors.isEmpty())
                mListOfColors.clear();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.my_search_list, container, false);


                // Set the adapter
        mListView = (AbsListView) view.findViewById(android.R.id.list);

        mAddNewItemButton = (AddFloatingActionButton) view.findViewById(R.id.normal_plus);

        mAddNewItemButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), DeviceScanActivity.class);
                startActivityForResult(intent,DEVICE_GET_CODE);
            }
        });

        ((AdapterView<ListAdapter>) mListView).setAdapter(mAdapter);

        // Set OnItemClickListener so we can be notified on item clicks
        mListView.setOnItemClickListener(this);

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        //Controlla la risposta a qui stiamo rispondendo
        if(requestCode == DEVICE_GET_CODE){
            //Controlla che sia un successo

        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnElementSelectedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (null != mListener) {
            // Notify the active callbacks interface (the activity, if the
            // fragment is attached to one) that an item has been selected.
            mListener.onElementSelected(mListOfColors.get(position));
        }
    }

    @Override
    public void onResume(){
        super.onResume();
    }
}
