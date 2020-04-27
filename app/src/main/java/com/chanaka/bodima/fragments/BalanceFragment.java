package com.chanaka.bodima.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.chanaka.bodima.R;
import com.chanaka.bodima.helpers.CalculateBalance;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


public class BalanceFragment extends Fragment {

    private FirebaseAuth auth;
    private FirebaseDatabase database;
    private CalculateBalance calculateBalance;
    private ProgressBar progressBar;
    private LinearLayout ll;
    Map<String, String> usersGroupe;

    public BalanceFragment(){}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_balance, container, false);

        ll = (LinearLayout) view.findViewById(R.id.view_eq);
        progressBar = (ProgressBar) view.findViewById(R.id.progressBar);

        // Authentification
        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();

        calculateBalance = new CalculateBalance();
        progressBar.setVisibility(View.VISIBLE);


        DatabaseReference refUser = database.getReference("users/" + auth.getCurrentUser().getUid());

        refUser.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final String groupe = (String) dataSnapshot.child("groupe").getValue();

                DatabaseReference refUsersGroupe = database.getReference("groupes/" + groupe + "/users");

                refUsersGroupe.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        progressBar.setVisibility(View.GONE);
                        usersGroupe = (HashMap<String, String>) dataSnapshot.getValue();

                        for (String id : usersGroupe.keySet()) {
                            calculateBalance.addUser(id);
                        }

                        DatabaseReference refExpenseGroup = database.getReference("groupes/" + groupe + "/expenses");

                        refExpenseGroup.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {

                                Map<String, Map<String, Object>> expenseGroup = (Map<String, Map<String, Object>>) dataSnapshot.getValue();
                                calculateBalance.addExpense(expenseGroup);

                                Iterator it = calculateBalance.getUsersBalance().entrySet().iterator();
                                while (it.hasNext()) {

                                    Map.Entry pair1 = (Map.Entry)it.next();
                                    Map.Entry pair2 = (Map.Entry) (it.hasNext() ? it.next() : null);

                                    View balanceElem = LayoutInflater.from(getContext()).inflate(R.layout.row_balance, ll, false);

                                    TextView textView = (TextView) balanceElem.findViewById(R.id.balance_amount_1);
                                    TextView textView2 = (TextView) balanceElem.findViewById(R.id.balance_user_1);
                                    TextView textView3 = (TextView) balanceElem.findViewById(R.id.balance_amount_2);
                                    TextView textView4 = (TextView) balanceElem.findViewById(R.id.balance_user_2);

                                    String expense1 = calculateBalance.getUserBalance((String) pair1.getKey());
                                    String expense2 = "";
                                    if(pair2 != null) {
                                        expense2 = calculateBalance.getUserBalance((String) pair2.getKey());
                                    }

                                    if(Float.parseFloat(expense1) >= 0.0f ) {
                                        textView.setBackgroundDrawable(getResources().getDrawable(R.drawable.circle_green));
                                        textView.setText("+"+String.format("%.2f", Float.parseFloat(expense1))+"LKR");
                                    } else {
                                        textView.setText(String.format("%.2f", Float.parseFloat(expense1))+"LKR");
                                    }
                                    textView2.setText(usersGroupe.get(pair1.getKey()) + "");


                                    if(pair2 != null) {
                                        if(Float.parseFloat(expense2) >= 0.0f ) {
                                            textView3.setBackgroundDrawable(getResources().getDrawable(R.drawable.circle_green));
                                            textView3.setText("+"+String.format("%.2f", Float.parseFloat(expense2))+"LKR");
                                        } else {
                                            textView3.setText(String.format("%.2f", Float.parseFloat(expense2))+"LKR");
                                        }
                                        textView4.setText(usersGroupe.get(pair2.getKey()) + "");
                                    } else {
                                        textView3.setVisibility(View.INVISIBLE);
                                        textView4.setVisibility(View.INVISIBLE);
                                    }

                                    ll.addView(balanceElem);
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {}
                        });

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {}
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });

        return view;
    }
}
