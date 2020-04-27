package com.chanaka.bodima.enums;



public enum CategoriesEnum {

    FOOD ("Food Stuff"),
    HOUSING ("Housing"),
    HOBBIES ("Hobbies"),
    TRANSPORTS ("Transport"),
    HEALTH ("Health"),
    OTHER ("Other");

    private String category = "";

    CategoriesEnum(String category){
        this.category = category;
    }

    public String toString(){
        return category;
    }

    public static CategoriesEnum getEnum(String s) {
        switch (s) {
            case "Food Stuff":
                return CategoriesEnum.FOOD;
            case "Housing":
                return CategoriesEnum.HOUSING;
            case "Hobbies":
                return CategoriesEnum.HOBBIES;
            case "Transport":
                return CategoriesEnum.TRANSPORTS;
            case "Health":
                return CategoriesEnum.HEALTH;
            case "Other":
                return CategoriesEnum.OTHER;
            default:
                return null;
        }
    }
}
