package com.example.grocerymanager.fragments;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.example.grocerymanager.R;
import com.example.grocerymanager.activities.ProfilePageActivity;
import com.example.grocerymanager.activities.SettingsPageActivity;
import com.example.grocerymanager.helpers.ActivityLauncher;
import com.example.grocerymanager.models.Recipe;
import com.example.grocerymanager.models.RecipeSet;
import com.example.grocerymanager.models.ShoppingList;

import java.util.List;
import java.util.Map;

public class ListFragment extends Fragment {

    final static String TAG = "ListFragment";
    private ImageButton profileIcon;
    private ImageButton settingsIcon;

    private LinearLayout linearLayout;
    private Context context;

    public ListFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_list, container, false);
        context = getContext();

        profileIcon = view.findViewById(R.id.profile_icon);

        profileIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityLauncher.launchActivity(requireActivity(), ProfilePageActivity.class);
            }
        });

        settingsIcon = view.findViewById(R.id.settings_icon);

        settingsIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityLauncher.launchActivity(requireActivity(), SettingsPageActivity.class);
            }
        });

        linearLayout = view.findViewById(R.id.linear_layout);

        return view;
    }

    @Override
    public void onStart() {
        setList();
        super.onStart();
    }

    private void setList() {
        linearLayout.removeAllViewsInLayout();
        LayoutInflater inflater = LayoutInflater.from(context);

        ShoppingList shoppingList = ShoppingList.getInstance();
        Map<String, List<String>> shoppingListMap = shoppingList.getShoppingList();
        for(Map.Entry<String, List<String>> entry : shoppingListMap.entrySet()) {
            View shoppingListPreview = inflater.inflate(R.layout.shopping_list_item, linearLayout, false);
            TextView categoryTextView = shoppingListPreview.findViewById(R.id.category_text_view);
            categoryTextView.setText((entry.getKey()));
            TextView itemTextView = shoppingListPreview.findViewById(R.id.items_text_view);
            itemTextView.setText("");
            List<String> items = entry.getValue();
            for (String s : items) {
                itemTextView.append(s + "\n");
            }
            linearLayout.addView(shoppingListPreview);
        }
    }
}
