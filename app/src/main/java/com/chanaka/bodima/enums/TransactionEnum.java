package com.chanaka.bodima.enums;


public enum  TransactionEnum {

    TRANSACTIONS ("Transactions");


    private String category = "";

    TransactionEnum(String category) {
        this.category = category;
    }

    public String toString() {
        return category;
    }

    public static TransactionEnum getEnum(String s) {
        switch (s) {
            case "Transaction":
                return TransactionEnum.TRANSACTIONS;

            default:
                return null;
        }
    }
}
