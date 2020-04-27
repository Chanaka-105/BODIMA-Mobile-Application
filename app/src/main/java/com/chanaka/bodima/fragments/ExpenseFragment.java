package com.chanaka.bodima.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.chanaka.bodima.MainActivity;
import com.chanaka.bodima.ModifyExpenseActivity;
import com.chanaka.bodima.R;
import com.chanaka.bodima.adapters.Expense;
import com.chanaka.bodima.adapters.ExpenseAdapter;
import com.chanaka.bodima.adapters.User;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class ExpenseFragment extends Fragment{

    private ListView listview;
    private TextView textNoDepenses;
    private SwipeRefreshLayout swipeLayout;
    private ProgressBar progressBar;
    private FirebaseAuth auth;
    FloatingActionMenu menu;
    FloatingActionButton buttonExpense, buttonIncome;
    private ImageView ic_tutorial;
    private FirebaseDatabase database;
    private ArrayAdapter<Expense> adapter;

    public ExpenseFragment(){}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }



    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_expenses, container, false);



        listview = (ListView) view.findViewById(R.id.listview_depenses);
        textNoDepenses = (TextView) view.findViewById(R.id.text_no_depenses);
        progressBar = (ProgressBar) view.findViewById(R.id.progressBar);
        swipeLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh);
        ic_tutorial=(ImageView)view.findViewById(R.id.ic_tutorial);




        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();

        final ArrayList<Expense> items = new ArrayList<>();

        adapter = new ExpenseAdapter(this.getActivity(), R.layout.row_list_expenses, items);

        listview.setAdapter(adapter);

        progressBar.setVisibility(View.VISIBLE);




        DatabaseReference refUser = database.getReference("users/" + auth.getCurrentUser().getUid());

        refUser.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String groupe = (String) dataSnapshot.child("groupe").getValue();

                DatabaseReference ref = database.getReference("groupes/" + groupe + "/expenses");

                ref.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {



                        if(dataSnapshot.getChildrenCount() == 0) {
                            progressBar.setVisibility(View.GONE);
                            textNoDepenses.setVisibility(View.VISIBLE);
                            ic_tutorial.setVisibility(View.VISIBLE);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });


                ref.addChildEventListener(new ChildEventListener() {

                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        Expense expense = dataSnapshot.getValue(Expense.class);
                        expense.set_id(dataSnapshot.getKey());
                        items.add(expense);
                        adapter.notifyDataSetChanged();
                        progressBar.setVisibility(View.GONE);
                    }

                    @Override
                    public void onChildRemoved(DataSnapshot dataSnapshot) {
                        items.remove(dataSnapshot.getValue(Expense.class));
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onChildMoved(DataSnapshot dataSnapshot, String s) {}
                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {}
                    @Override
                    public void onCancelled(DatabaseError databaseError) {}

                });

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });



        listview.setClickable(false);
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {

                Expense expense = (Expense) listview.getItemAtPosition(position);

                if(expense.getCategory().equals("Transactions")){
                    Toast.makeText(getContext(), "Not Available For Modify", Toast.LENGTH_LONG).show();

                }else {
                    if (!expense.getPayerid().equals(auth.getCurrentUser().getUid())) {
                        Toast.makeText(getContext(), "You Have No Authorization, Ask "+expense.getPayername(), Toast.LENGTH_LONG).show();

                    }else {
                        Intent intent = new Intent(getActivity(), ModifyExpenseActivity.class);
                        intent.putExtra("expense", expense);
                        startActivity(intent);
                    }
                }
            }
        });

        return view;
    }

//    @Override public void onRefresh() {
//        new Handler().postDelayed(new Runnable() {
//            @Override public void run() {
//                swipeLayout.setRefreshing(false);
//            }
//
//        }, 1200);
//    }


}
