package com.chanaka.bodima.adapters;

import android.content.Context;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.chanaka.bodima.R;

import java.util.ArrayList;


public class ExpenseAdapter extends ArrayAdapter<Expense> {

    private ArrayList<Expense> objects;

    public ExpenseAdapter(Context context, int textViewResourceId, ArrayList<Expense> objects) {
        super(context, textViewResourceId, objects);
        this.objects = objects;
    }

    @Override
    @Nullable
    public Expense getItem(int position) {
        return super.getItem(getCount() - position - 1);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public View getView(int position, View convertView, ViewGroup parent){

        View v = convertView;

        if (v == null) {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.row_list_expenses, null);
        }

        Expense i = getItem(position);

        if (i != null) {

            TextView title = (TextView) v.findViewById(R.id.view_title);
            TextView payername = (TextView) v.findViewById(R.id.view_payername);
            TextView category = (TextView) v.findViewById(R.id.view_category);
            TextView amount = (TextView) v.findViewById(R.id.view_amount);
            RelativeLayout rel1=(RelativeLayout)v.findViewById(R.id.rel1);
            ImageView icon_expense=(ImageView)v.findViewById(R.id.icon_expense);


            if (rel1!=null) {
                if(i.getCategory().equals("Transactions")) {
                    icon_expense.setImageResource(R.drawable.ic_income);
                    rel1.setBackgroundResource(R.drawable.expense_green);

                }else {
                    icon_expense.setImageResource(R.drawable.ic_outcome);
                    rel1.setBackgroundResource(R.drawable.expense_red);
                }
            }


            if (title != null){
                title.setText(i.getTitle());
            }

            if (payername != null) {
                if (i.getCategory().equals("Transactions")) {
                    payername.setText("Transfer From " + i.getPayername());
                } else {
                    payername.setText("Paid By " + i.getPayername());
                }
            }

            if (category != null){
                category.setText(i.getCategory());
            }
            if (amount != null){
                amount.setText(i.getAmount()+" LKR");
            }
        }

        return v;

    }


}