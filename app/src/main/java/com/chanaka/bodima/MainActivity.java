package com.chanaka.bodima;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.chanaka.bodima.auth.Startup_View;
import com.chanaka.bodima.dialogs.DialogClear;
import com.chanaka.bodima.dialogs.Dialog_About_us;
import com.chanaka.bodima.dialogs.IdentifyGroupDialog;
import com.chanaka.bodima.fragments.ExpenseFragment;
import com.chanaka.bodima.fragments.BalanceFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {


    private ProgressBar progressBar;
    private TextView headerName, headerEmail,headerAdmin;
    private FirebaseAuth auth;
    private FirebaseDatabase database;
    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private DrawerLayout mDrawerLayout;
    private NavigationView navigationView;
    private ImageButton buttonChat;
    private FloatingActionButton buttonAddExpense,fab_messege,fab_delete;
    private ImageButton btnClear;
    private ProgressBar progressBar2;
    private String groupeID;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        buttonChat=findViewById(R.id.btn_chat);
        mDrawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        toolbar = (Toolbar) findViewById(R.id.mytoolbar);
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        tabLayout = (TabLayout) findViewById(R.id.mytabs);
        headerName = (TextView) navigationView.getHeaderView(0).findViewById(R.id.header_name);
        headerEmail = (TextView) navigationView.getHeaderView(0).findViewById(R.id.header_email);
        headerAdmin=(TextView)navigationView.getHeaderView(0).findViewById(R.id.header_admin);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar2 = (ProgressBar) findViewById(R.id.progressBar2);
        btnClear=(ImageButton)findViewById(R.id.btn_clear);

      //Making custom Floating action menu
        buttonAddExpense = (FloatingActionButton) findViewById(R.id.add_depense_button);
        final Button button_addexpense=(Button) findViewById(R.id.delete);
        Button button_transfer=(Button)findViewById(R.id.Transaction);
        final LinearLayout layout_delete=(LinearLayout)findViewById(R.id.layout_delete);
        final LinearLayout layout_transaction=(LinearLayout)findViewById(R.id.layout_transaction);

        layout_delete.setVisibility(View.GONE);
        layout_transaction.setVisibility(View.GONE);

        final Animation mShowButton= AnimationUtils.loadAnimation(MainActivity.this,R.anim.show_button);
        final Animation mHideButton= AnimationUtils.loadAnimation(MainActivity.this,R.anim.hide_button);
        final Animation mShowLayout= AnimationUtils.loadAnimation(MainActivity.this,R.anim.show_layout);
        final Animation mHideLayout= AnimationUtils.loadAnimation(MainActivity.this,R.anim.hide_layout);
        final Animation mshowTransaction= AnimationUtils.loadAnimation(MainActivity.this,R.anim.show_transaction);
        final Animation mhideTransaction= AnimationUtils.loadAnimation(MainActivity.this,R.anim.hide_transaction);

        buttonAddExpense.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(layout_delete.getVisibility()==View.VISIBLE && layout_transaction.getVisibility()==View.VISIBLE){
                    layout_delete.setVisibility(View.GONE);
                    layout_transaction.setVisibility(View.GONE);
                    layout_delete.startAnimation(mHideLayout);
                    layout_transaction.startAnimation(mhideTransaction);
                    buttonAddExpense.startAnimation(mHideButton);

                }else{
                    layout_delete.setVisibility(View.VISIBLE);
                    layout_transaction.setVisibility(View.VISIBLE);
                    buttonAddExpense.startAnimation(mShowButton);
                    layout_delete.startAnimation(mShowLayout);
                    layout_transaction.startAnimation(mshowTransaction);

                }
            }
        });

        button_addexpense.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(MainActivity.this, AddExpenseActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                layout_delete.setVisibility(View.GONE);
                layout_transaction.setVisibility(View.GONE);
                layout_delete.startAnimation(mHideLayout);
                layout_transaction.startAnimation(mhideTransaction);
                buttonAddExpense.startAnimation(mHideButton);
            }
        });

        button_transfer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Tempory_Transaction dialog_tempory = new Tempory_Transaction(MainActivity.this);
                dialog_tempory.show();
                layout_delete.setVisibility(View.GONE);
                layout_transaction.setVisibility(View.GONE);
                layout_delete.startAnimation(mHideLayout);
                layout_transaction.startAnimation(mhideTransaction);
                buttonAddExpense.startAnimation(mHideButton);

            }
        });

        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();

        setSupportActionBar(toolbar);
        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(R.mipmap.ic_menu_white_24dp);
        progressBar2.setVisibility(View.VISIBLE);

        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        mDrawerLayout.closeDrawers();
                        menuItem.setChecked(true);

                        switch (menuItem.getItemId()) {
                            case R.id.nav_group_settings:
                                startActivity(new Intent(MainActivity.this, GroupSettingsActivity.class));
                                break;
                            case R.id.nav_invite_friends:
                                IdentifyGroupDialog identifyGroupDialog = new IdentifyGroupDialog(MainActivity.this);
                                identifyGroupDialog.show();
                                break;
                            case R.id.nav_editProfile:
                                startActivity(new Intent(MainActivity.this, EditProfileActivity.class));
                                break;
                            case R.id.nav_about_us:
                               Dialog_About_us dialog_about_us = new Dialog_About_us(MainActivity.this);
                               dialog_about_us.show();
                                break;
                            case R.id.nav_disconnect:
                                disconnectDialog().show();
                                break;
                            default:
                                break;
                        }

                        return true;
                    }
                });

            ConnectivityManager connManager=(ConnectivityManager)this.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo=connManager.getActiveNetworkInfo();

            if(networkInfo==null){
                progressBar2.setVisibility(View.GONE);
                buttonAddExpense.setVisibility(View.GONE);
                makeAndShowConnection().show();
                return;
            }
            if(!networkInfo.isAvailable()){
                makeAndShowConnection().show();
            }



        if (auth.getCurrentUser() == null) {
            Intent intent=new Intent(MainActivity.this,Startup_View.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        } else {

            if (progressBar != null) {
                progressBar.setVisibility(View.GONE);
            }


            DatabaseReference ref = database.getReference("users/" + auth.getCurrentUser().getUid());
            ref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    progressBar2.setVisibility(View.GONE);
                    String groupe = (String) dataSnapshot.child("groupe").getValue();
                    String admin=(String)dataSnapshot.child("Admin").getValue();

                    if(groupe == null || groupe.length() == 0) {
                        startActivity(new Intent(MainActivity.this, CreateGroupActivity.class));
                        finish();
                    } else {

                        setViewPager(viewPager);
                        tabLayout.setupWithViewPager(viewPager);

                        headerName.setText((String) dataSnapshot.child("name").getValue());
                        headerAdmin.setText("("+admin+")");
                        headerEmail.setText(auth.getCurrentUser().getEmail());
                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {}
            });

        }



        buttonChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MainActivity.this,ChatActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);

            }
        });

        DatabaseReference ref = database.getReference("users/" + auth.getCurrentUser().getUid());
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                progressBar2.setVisibility(View.GONE);
                final String admin=(String)dataSnapshot.child("Admin").getValue();

                btnClear.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(admin.equals("Admin")){
                            DialogClear removeGroupeDialog = new DialogClear(MainActivity.this, groupeID);
                            removeGroupeDialog.show();
                        }else{
                            Toast.makeText(MainActivity.this,"Only Admins Can Clear History",Toast.LENGTH_LONG).show();
                        }
                    }
                });

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
    }



    @Override protected void onResume() {
        super.onResume();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new ExpenseFragment(), "History");
        adapter.addFragment(new BalanceFragment(),"Account Log");
        viewPager.setAdapter(adapter);
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

    private void disconnect() {
        FirebaseAuth.getInstance().signOut();
        Toast.makeText(getApplicationContext(), "You have been disconnected", Toast.LENGTH_SHORT).show();
        Intent intent=new Intent(MainActivity.this,Startup_View.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    private AlertDialog makeAndShowConnection(){
        AlertDialog myquitterDialogBox=new AlertDialog.Builder(this)
                .setTitle("OOOPZ..!")
                .setMessage("Check Your Network Connection.")
                .setCancelable(false)
                .setIcon(R.drawable.ic_network_bar_black_24dp)
                .setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent intent=new Intent(MainActivity.this,MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                    }
                }).setNegativeButton("Quit", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        MainActivity.super.onBackPressed();
                    }
                }).create();
        return myquitterDialogBox;
    }

    private AlertDialog disconnectDialog(){
        AlertDialog mydisconnectDialog=new AlertDialog.Builder(this)
                .setTitle("Are You Sure Want To Logout?")
                .setCancelable(false)
                .setIcon(R.mipmap.power)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                       disconnect();
                    }
                }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                }).create();
        return mydisconnectDialog;
    }

    @Override
    public void onBackPressed() {

        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder
                .setCancelable(false)
                .setIcon(R.drawable.ic_exit_to_app_black_24dp)
                .setTitle("Are You Sure Want To Exit?")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                MainActivity.super.onBackPressed();
            }
        }).setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        AlertDialog alertDialog=builder.create();
        alertDialog.show();

    }

}
