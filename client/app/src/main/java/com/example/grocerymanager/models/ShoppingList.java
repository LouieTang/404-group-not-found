package com.example.grocerymanager.models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ShoppingList {

    private Map<String, List<String>> shoppingList;
    private static ShoppingList instance;


    public ShoppingList(){
        this.shoppingList = new HashMap<String, List<String>>();
    }

    public static synchronized ShoppingList getInstance() {
        if (instance == null) {
            instance = new ShoppingList();
        }
        return instance;
    }

    public static boolean checkInstance(){
        if(instance == null){
            return false;
        }
        return true;
    }

    public static void clearInstance() {
        instance = null;
    }

    public void addSection(String category, List<String> items){
        shoppingList.put(category, items);
    }

    public void clearSection(String category){
        shoppingList.remove(category);
    }

    public void clearShoppingList(){
        shoppingList.clear();
    }

    public Map<String, List<String>> getShoppingList(){
        return shoppingList;
    }

}
