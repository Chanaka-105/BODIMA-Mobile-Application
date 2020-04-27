package com.chanaka.bodima;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
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
import android.widget.AdapterView.OnItemSelectedListener;
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
import java.util.concurrent.TimeUnit;

public class AddExpenseActivity extends AppCompatActivity {

    NotificationCompat.Builder notification;
    private static final int uniqueID=12345;


    private Context activity;
    private EditText expenseTitle, expenseAmount;
    private Spinner spinnerCategories, spinnerPayername;
    private Button btnConfirmer;
    private ImageButton btnBack;
    private ListView listUsersCheckbox;
    private CategoriesEnum expenseCategoryValue;
    private User payerName;
    private ProgressBar progressBar;
    private ArrayList<User> userList;
    private ArrayAdapter<User> adapter;
    private FirebaseAuth auth;
    private FirebaseDatabase database;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_expense);
        activity = this;


        notification= new NotificationCompat.Builder(this);
        notification.setAutoCancel(true);

        expenseTitle = (EditText) findViewById(R.id.expense_title);
        expenseAmount = (EditText) findViewById(R.id.expense_amount);
        spinnerCategories = (Spinner) findViewById(R.id.expense_category);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        spinnerPayername = (Spinner) findViewById(R.id.expense_payername);
        btnConfirmer = (Button) findViewById(R.id.btn_modify_expense);
        btnBack = (ImageButton) findViewById(R.id.btn_expense_return);
        listUsersCheckbox = (ListView) findViewById(R.id.listview_users_checkbox);

        // Authentication
        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();

        userList = new ArrayList<>();
        progressBar.setVisibility(View.VISIBLE);


        spinnerCategories.setAdapter(new ArrayAdapter<CategoriesEnum>(this, android.R.layout.simple_spinner_dropdown_item, CategoriesEnum.values()));


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
                            userList.add(new User(entry.getKey(),entry.getValue()));
                            if(entry.getKey().equals(auth.getCurrentUser().getUid())) {
                                positionMoi = i;
                            }
                            i++;
                        }

                        ArrayAdapter<User> spinnerAdapter = new ArrayAdapter<User>(activity, android.R.layout.simple_spinner_item, userList);
                        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spinnerPayername.setAdapter(spinnerAdapter);
                        spinnerPayername.setSelection(positionMoi);

                        adapter = new UserCheckboxAdapter(AddExpenseActivity.this, R.layout.row_list_users_checkbox, userList);
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
                        String nameGroupe = (String) dataSnapshot.child("groupe").getValue();
                        String nameUser = (String) dataSnapshot.child("name").getValue();

                        if(nameGroupe != null && nameUser != null) {

                            DatabaseReference ref = database.getReference("groupes/" + nameGroupe + "/expenses");

                            Map<String, String> userMap = new HashMap<>();

                            for(User user : userList) {
                                if(user.isSelected()) {
                                    userMap.put(user.getId(), user.getId());
                                }
                            }


                            Expense expense = new Expense();
                            expense.setTitle(title);
                            expense.setAmount(amount);
                            expense.setCategory(expenseCategoryValue.toString());
                            expense.setPayerid(payerName.getId());
                            expense.setPayername(payerName.getName());
                            expense.setTimestamp(TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis()) + "");
                            expense.setUsers(userMap);

                            ref.child(ref.push().getKey()).setValue(expense);

                            startActivity(new Intent(AddExpenseActivity.this, MainActivity.class));
                            finish();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
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


        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(AddExpenseActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });

        spinnerCategories.setOnItemSelectedListener(new OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view,
                                       int position, long id) {
                expenseCategoryValue = (CategoriesEnum) adapterView.getItemAtPosition(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }

        });

        spinnerPayername.setOnItemSelectedListener(new OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view,
                                       int position, long id) {
                payerName = (User) adapterView.getItemAtPosition(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }

        });

    }


}
