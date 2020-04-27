package com.chanaka.bodima;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.chanaka.bodima.adapters.Expense;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.chanaka.bodima.adapters.UserCheckboxAdapter;
import com.chanaka.bodima.enums.CategoriesEnum;
import com.chanaka.bodima.adapters.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ModifyExpenseActivity extends AppCompatActivity {

    private Context activity;
    private EditText expenseTitle, expenseAmount;
    private Spinner spinnerCategories, spinnerPayerName;
    private Button btnConfirmer, btnDelete;
    private ImageButton btnBack;
    private ListView listUsersCheckbox;
    private CategoriesEnum expenseCategoryValue;
    private User payeName;
    private ArrayList<User> userList;
    private ArrayAdapter<User> adapter;
    private ProgressBar progressBar;

    private FirebaseAuth auth;
    private FirebaseDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_expense);
        activity = this;


        expenseTitle = (EditText) findViewById(R.id.expense_title);
        expenseAmount = (EditText) findViewById(R.id.expense_amount);
        spinnerCategories = (Spinner) findViewById(R.id.expense_category);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        spinnerPayerName = (Spinner) findViewById(R.id.expense_payername);
        btnConfirmer = (Button) findViewById(R.id.btn_modify_expense);
        btnDelete = (Button) findViewById(R.id.btn_suppression_depense);
        btnBack = (ImageButton) findViewById(R.id.btn_expense_return);
        listUsersCheckbox = (ListView) findViewById(R.id.listview_users_checkbox);


        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();

        progressBar.setVisibility(View.VISIBLE);

        final Expense expense = (Expense) getIntent().getSerializableExtra("expense");
        expenseTitle.setText(expense.getTitle());
        expenseAmount.setText(expense.getAmount());
        spinnerCategories.setAdapter(new ArrayAdapter<CategoriesEnum>(this, android.R.layout.simple_spinner_dropdown_item, CategoriesEnum.values()));
        spinnerCategories.setSelection(getIndex(spinnerCategories, expense.getCategory()));


        DatabaseReference ref = database.getReference("users/" + auth.getCurrentUser().getUid() + "/groupe");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                DatabaseReference ref = database.getReference("groupes/" + dataSnapshot.getValue() + "/users");

                ref.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        progressBar.setVisibility(View.GONE);
                        Map<String, String> usersGroupe = (Map<String, String>) dataSnapshot.getValue();
                        userList = new ArrayList<User>();
                        int positionMoi = 0;
                        int i = 0;
                        for (Map.Entry<String, String> entry : usersGroupe.entrySet()) {
                            User user = new User(entry.getKey(), entry.getValue());
                            if(expense.getUsers() != null && !expense.getUsers().containsValue(user.getId())) {
                                user.setSelected(false);
                            }
                            userList.add(user);
                            if(entry.getKey().equals(expense.getPayerid())) {
                                positionMoi = i;
                            }
                            i++;
                        }

                        ArrayAdapter<User> spinnerAdapter = new ArrayAdapter<User>(activity, android.R.layout.simple_spinner_item, userList);
                        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spinnerPayerName.setAdapter(spinnerAdapter);
                        spinnerPayerName.setSelection(positionMoi);
                        spinnerPayerName.setSelection(getIndex(spinnerPayerName, expense.getPayername()));

                        adapter = new UserCheckboxAdapter(ModifyExpenseActivity.this, R.layout.row_list_users_checkbox, userList);
                        listUsersCheckbox.setAdapter(adapter);

                        ViewGroup.LayoutParams params = listUsersCheckbox.getLayoutParams();
                        params.height = userList.size() * 102;
                        listUsersCheckbox.setLayoutParams(params);
                        listUsersCheckbox.requestLayout();

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        listUsersCheckbox.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                User user = (User) parent.getItemAtPosition(position);

                CheckBox checkBox = view.findViewById(R.id.checkbox_user);

                if(checkBox.isChecked()) {
                    userList.get(userList.indexOf(user)).setSelected(true);
                } else {
                    userList.get(userList.indexOf(user)).setSelected(false);
                }
            }
        });


        btnConfirmer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                    final String title = expenseTitle.getText().toString().trim();
                    final String amount = expenseAmount.getText().toString().trim();


                    if (TextUtils.isEmpty(title)) {
                        Toast.makeText(getApplicationContext(), "Enter A Title", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (TextUtils.isEmpty(amount)) {
                        Toast.makeText(getApplicationContext(), "Enter A Amount", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (expenseCategoryValue.toString() == null || TextUtils.isEmpty(expenseCategoryValue.toString())) {
                        Toast.makeText(getApplicationContext(), "Select An Category for Expense", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    DatabaseReference ref = database.getReference("users/" + auth.getCurrentUser().getUid());

                    ref.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            String nomGroupe = (String) dataSnapshot.child("groupe").getValue();
                            String nameUser = (String) dataSnapshot.child("name").getValue();

                            if (nomGroupe != null && nameUser != null) {

                                DatabaseReference ref = database.getReference("groupes/" + nomGroupe + "/expenses/" + expense.get_id());

                                Map<String, String> userMap = new HashMap<>();

                                for (User user : userList) {
                                    if (user.isSelected()) {
                                        userMap.put(user.getId(), user.getId());
                                    }
                                }

                                if (!title.equals(expense.getTitle()))
                                    ref.child("title").setValue(title);
                                if (!amount.equals(expense.getAmount()))
                                    ref.child("amount").setValue(amount);
                                if (!expenseCategoryValue.toString().equals(expense.getCategory()))
                                    ref.child("category").setValue(expenseCategoryValue.toString());
                                if (!expenseCategoryValue.toString().equals(expense.getCategory()))
                                    ref.child("category").setValue(expenseCategoryValue.toString());
                                if (!payeName.getId().equals(expense.getPayerid())) {
                                    ref.child("payerid").setValue(payeName.getId());
                                    ref.child("payername").setValue(payeName.getName());
                                }
                                ref.child("users").setValue(userMap);
                                startActivity(new Intent(ModifyExpenseActivity.this, MainActivity.class));
                                finish();
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            // @todo toast
                        }
                    });
                }
        });

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!payeName.getId().equals(auth.getCurrentUser().getUid())) {
                    Toast.makeText(getApplicationContext(), "You Have No Authorization", Toast.LENGTH_LONG).show();
                } else {
                    DatabaseReference ref = database.getReference("users/" + auth.getCurrentUser().getUid());

                    ref.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            String nomGroupe = (String) dataSnapshot.child("groupe").getValue();

                            if (nomGroupe != null) {
                                DatabaseReference refExpense = database.getReference("groupes/" + nomGroupe + "/expenses/" + expense.get_id());
                                refExpense.removeValue();
                                startActivity(new Intent(ModifyExpenseActivity.this, MainActivity.class));
                                finish();
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                        }
                    });
                }
            }
        });


        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(ModifyExpenseActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });

        spinnerCategories.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                expenseCategoryValue = (CategoriesEnum) adapterView.getItemAtPosition(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }

        });

        spinnerPayerName.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                payeName = (User) adapterView.getItemAtPosition(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }

        });


    }

    private int getIndex(Spinner spinner, String string)
    {
        int index = 0;

        for (int i=0;i<spinner.getCount();i++){
            if (spinner.getItemAtPosition(i).toString().equalsIgnoreCase(string)){
                index = i;
                break;
            }
        }
        return index;
    }
}
