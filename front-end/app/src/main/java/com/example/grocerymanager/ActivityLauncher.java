package com.example.grocerymanager;

import android.content.Context;
import android.content.Intent;

public class ActivityLauncher {
    public static void launchActivity(Context context, Class<?> cls) {
        Intent intent = new Intent(context, cls);
        context.startActivity(intent);
    }
}
