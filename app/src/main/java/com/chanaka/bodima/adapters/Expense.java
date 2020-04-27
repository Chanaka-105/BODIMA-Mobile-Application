package com.chanaka.bodima.adapters;

import java.io.Serializable;
import java.util.Map;



public class Expense implements Serializable {

    private String _id;
    private String category;
    private String amount;
    private String payerid;
    private String payername;
    private String title;
    private String timestamp;
    private Map<String,String> users;

    public Expense() {
    }

    public Expense(String _id, String category, String amount, String payerid, String payername, String title, String timestamp, Map<String, String> users) {
        this._id = _id;
        this.category = category;
        this.amount = amount;
        this.payerid = payerid;
        this.payername = payername;
        this.title = title;
        this.timestamp = timestamp;
        this.users = users;


    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String montant) {
        this.amount = montant;
    }

    public String getPayerid() {
        return payerid;
    }

    public void setPayerid(String payerid) {
        this.payerid = payerid;
    }

    public String getPayername() {
        return payername;
    }

    public void setPayername(String payername) {
        this.payername = payername;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public Map<String, String> getUsers() {
        return users;
    }

    public void setUsers(Map<String, String> users) {
        this.users = users;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Expense expense = (Expense) o;


        if (_id != null ? !_id.equals(expense._id) : expense._id != null) return false;
        if (category != null ? !category.equals(expense.category) : expense.category != null)
            return false;
        if (amount != null ? !amount.equals(expense.amount) : expense.amount != null)
            return false;
        if (payerid != null ? !payerid.equals(expense.payerid) : expense.payerid != null)
            return false;
        if (payername != null ? !payername.equals(expense.payername) : expense.payername != null)
            return false;
        if (title != null ? !title.equals(expense.title) : expense.title != null) return false;
        if (timestamp != null ? !timestamp.equals(expense.timestamp) : expense.timestamp != null)
            return false;
        return users != null ? users.equals(expense.users) : expense.users == null;
    }

    @Override
    public int hashCode() {
        int result = _id != null ? _id.hashCode() : 0;

        result = 31 * result + (category != null ? category.hashCode() : 0);
        result = 31 * result + (amount != null ? amount.hashCode() : 0);
        result = 31 * result + (payerid != null ? payerid.hashCode() : 0);
        result = 31 * result + (payername != null ? payername.hashCode() : 0);
        result = 31 * result + (title != null ? title.hashCode() : 0);
        result = 31 * result + (timestamp != null ? timestamp.hashCode() : 0);
        result = 31 * result + (users != null ? users.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Expense{" +
                "_id='" + _id + '\'' +
                ", category='" + category + '\'' +
                ", amount='" + amount + '\'' +
                ", payerid='" + payerid + '\'' +
                ", payername='" + payername + '\'' +
                ", title='" + title + '\'' +
                ", timestamp='" + timestamp + '\'' +
                ", users=" + users +
                '}';
    }
}
