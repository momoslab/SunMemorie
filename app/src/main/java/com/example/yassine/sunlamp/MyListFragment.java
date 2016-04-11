package com.example.yassine.sunlamp;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPropertyAnimatorCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewPropertyAnimator;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.Toast;

import com.example.yassine.sunlamp.Adapter.ColorDataAdapter;
import com.example.yassine.sunlamp.Adapter.RecyclerListAdapter;
import com.example.yassine.sunlamp.Bluetooth.Scanner.DeviceScanActivity;
import com.example.yassine.sunlamp.Model.ColorData;
import com.getbase.floatingactionbutton.AddFloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;

import java.util.List;

import co.dift.ui.SwipeToAction;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Large screen devices (such as tablets) are supported by replacing the ListView
 * with a GridView.
 * <p/>
 * Activities containing this fragment MUST implement the {@link com.example.yassine.sunlamp.OnElementSelectedListener}
 * interface.
 */
public class MyListFragment extends Fragment implements AbsListView.OnItemClickListener {

    private OnElementSelectedListener mListener;

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

    RecyclerView mRecyclerList;

    SwipeToAction mSwipeToAction;

    RecyclerListAdapter mRecyclerAdapter;

    List<ColorData> mListOfColors = null;

    private DatabaseOperations mDatabaseOperation;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public MyListFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDatabaseOperation = new DatabaseOperations(getActivity());
        mListOfColors = mDatabaseOperation.getAllData();
        mAdapter = new ColorDataAdapter(getActivity(), mListOfColors);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_list, container, false);


                // Set the adapter
        mListView = (AbsListView) view.findViewById(android.R.id.list);

        mRecyclerList = (RecyclerView) view.findViewById(R.id.recyclerList);

        mAddNewItemButton = (AddFloatingActionButton) view.findViewById(R.id.normal_plus);

        mAddNewItemButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), AddNewItemActivity.class);
                startActivityForResult(intent, DEVICE_GET_CODE);
            }
        });

        mRecyclerAdapter  = new RecyclerListAdapter(mListOfColors, getActivity());

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());

        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        mRecyclerList.setLayoutManager(linearLayoutManager);

        mRecyclerList.setHasFixedSize(true);

        mRecyclerList.setAdapter(mRecyclerAdapter);

        mSwipeToAction = new SwipeToAction(mRecyclerList, new SwipeToAction.SwipeListener<ColorData>() {
            @Override
            public boolean swipeLeft(ColorData data) {
                return false;
            }

            @Override
            public boolean swipeRight(ColorData data) {
                Toast.makeText(getActivity(), "im tring to swipe", Toast.LENGTH_LONG).show();
                return true;
            }

            @Override
            public void onClick(ColorData data) {
                Intent intent = new Intent(getActivity(), ViewSingleItemActivity.class);
                intent.putExtra("colorItemSelected", data);
                getActivity().startActivity(intent);
            }

            @Override
            public void onLongClick(ColorData data) {

            }

        });

        mListView.setAdapter(mAdapter);

        // Set OnItemClickListener so we can be notified on item clicks
        mListView.setOnItemClickListener(this);

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        //Controlla la risposta a qui stiamo rispondendo
        if(requestCode == DEVICE_GET_CODE){
            //Controlla che sia un successo
            if(resultCode == getActivity().RESULT_OK){
                mListOfColors.clear();
                mListOfColors.addAll(mDatabaseOperation.getAllData());
                mRecyclerAdapter.addItems(mListOfColors);
                mRecyclerAdapter.notifyDataSetChanged();
            }
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
        mListOfColors.clear();
        mListOfColors.addAll(mDatabaseOperation.getAllData());
        mRecyclerAdapter.addItems(mListOfColors);
        mRecyclerAdapter.notifyDataSetChanged();
        mAdapter.notifyDataSetChanged();
    }
}
