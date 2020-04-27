package com.chanaka.bodima.helpers;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;

public class CalculateBalance {

    Map<String, Float> usersBalance;

    public CalculateBalance() {
        usersBalance = new HashMap<String, Float>();
    }



    public void addUser(String id) {
        usersBalance.put(id, 0.0f);
    }


    public void addExpense(Map<String, Map<String, Object>> depenses) {
        if(depenses != null) {
            for (Map<String, Object> expense : depenses.values()) {
                String id = (String) expense.get("payerid");

                Map<String, String> users = (Map<String, String>) expense.get("users");

                Float amount = Float.parseFloat((String) expense.get("amount"));

                if(users.containsKey(id)) {
                    for (String user : users.values()) {
                        if (usersBalance.get(user) != null) {
                            if(user.equals(id)) {
                                Float result = amount-amount/users.size();
                                usersBalance.put(user, usersBalance.get(user) + result);
                            }else{
                                    Float result=amount/users.size();
                                    usersBalance.put(user, usersBalance.get(user) - result);
                            }
                        }
                    }
                }else {
                    for (String user : users.values()) {
                        if (usersBalance.get(user) != null) {

                            if(!user.contains(id)){
                                Float result=amount/users.size();
                                usersBalance.put(id,usersBalance.get(id)+result);

                               result = amount / users.size();
                                usersBalance.put(user, usersBalance.get(user) - result);

                            }
                        }
                    }
                }
            }
        }
    }



    public String getUserBalance(String id) {
        return Float.toString(usersBalance.get(id));
    }


    public Map<String, Float> getUsersBalance() {
        return usersBalance;
    }
}
