package com.example.grocerymanager;

import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class BarcodeActivity extends AppCompatActivity implements DatePickerFragment.DatePickerListener {
    final static String TAG = "BarcodeActivity";
    private ImageButton backIcon;
    private String expiryDateString;
    private Button itemExpiryButton;
    private Button addItemButton;
    private EditText itemQuantity;
    private String upcCode;
    private Button addItemsInventoryButton;
    private List<String> expiryDateList;
    private List<Long> upcList;
    private List<Integer> quantityList;
    private List<Item> itemList;
    private OkHttpClient client;
    private Button scanBarButton;

    private NetworkManager networkManager;

    ActivityResultLauncher<ScanOptions> barcodeLauncher = registerForActivityResult(new ScanContract(), result -> {
        if (result.getContents() != null) {
            // UPC code was successfully retrieved
            upcCode = result.getContents();
//            upcCode = "11123456789012";
            // You can proceed with handling the UPC code or launching another activity if needed
            handleUPCCode(upcCode);
        } else {
            // No UPC code was retrieved
            redirectToAnotherActivity();
        }
    });


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_barcode);
        expiryDateList = new ArrayList<>();
        upcList = new ArrayList<>();
        quantityList = new ArrayList<>();

        itemExpiryButton = findViewById(R.id.set_expiry_date_barcode);
        itemExpiryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDatePickerDialog();
            }
        });

        backIcon = findViewById(R.id.back_icon_barcode);
        backIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        itemQuantity = findViewById(R.id.item_quantity_barcode);

        itemList = new ArrayList<>();
        addItemButton = findViewById(R.id.add_item_to_barcode);
        addItemButton.setOnClickListener(view -> {
            if (expiryDateString.isEmpty() || itemQuantity.getText().toString().isEmpty() || upcCode.isEmpty() || upcCode == null) {

            } else {
                long upc = Long.parseLong(upcCode);
                String itemQuantityStr = itemQuantity.getText().toString();
                Item newItem = new Item("", expiryDateString, Integer.parseInt(itemQuantityStr), -1, upc);
                itemList.add(newItem);
                addItemToInventory(newItem);
                Log.d(TAG, itemList.toString());

                expiryDateString="";
                itemQuantity.getText().clear();
                upcCode = "";


            }
        });


        networkManager = new NetworkManager(this);
        client = networkManager.getClient();

        String serURL = "https://20.104.197.24/";
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        UserData userData = SharedPrefManager.loadUserData(BarcodeActivity.this);

        addItemsInventoryButton = findViewById(R.id.save_items_to_inventory_barcode);
        addItemsInventoryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (itemList.isEmpty()){
                    launchInventoryIntent();
                }
                else {
                    for (Item item : itemList){
                        expiryDateList.add(item.getExpiry());
                        upcList.add(item.getUPC());
                        quantityList.add(item.getQuantity());
                    }
                    JSONObject postData1 = new JSONObject();
                    try{
                        postData1.put("p1", userData.getUID());
                        postData1.put("p2", new JSONArray(upcList)); // assuming upcList is a list of Integers
                        postData1.put("p3", new JSONArray(expiryDateList)); // assuming expiryDateList is a list of Strings
                        postData1.put("p4", new JSONArray(quantityList)); // assuming quantityList is a list of Integers
                    }
                    catch (JSONException e){
                        e.printStackTrace();
                    }

                    RequestBody body1 = RequestBody.create(JSON, postData1.toString());
                    Request request = new Request.Builder()
                            .url(serURL + "add/items")
                            .post(body1)
                            .build();

                    client.newCall(request).enqueue(new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                            // Handle failure
                            e.printStackTrace();
                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            if (response.isSuccessful()) {
                                // Handle successful response
                                String responseData = response.body().string();
                                Log.d(TAG, "Response: " + responseData);

                                launchInventoryIntent();
                            } else {
                                // Handle unsuccessful response
                                Log.e(TAG, "Unsuccessful response " + response.code());
                            }
                        }
                    });

                }
            }
        });




        scanBarButton = findViewById(R.id.scanBarButton);
        scanBarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                scanCode();
            }
        });


    }

    private void addItemToInventory(Item item) {
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.item_display_template, null);

        TextView itemNameTextView = view.findViewById(R.id.item_name_text_view);
        TextView expiryDateTextView = view.findViewById(R.id.expiry_date_text_view);
        TextView quantityTextView = view.findViewById(R.id.quantity_text_view);
        Button editButton = view.findViewById(R.id.edit_button);
        Button deleteButton = view.findViewById(R.id.delete_button);

        itemNameTextView.setText(Long.toString(item.getUPC()));
        expiryDateTextView.setText("Expiry Date: " + item.getExpiry());
        quantityTextView.setText("Quantity: " + item.getQuantity());

        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //need to implement editing
                displayEditPopup(item);
                itemList.remove(item);
                LinearLayout mainLayout = findViewById(R.id.inventory_container_barcode);
                mainLayout.removeView(view);

            }
        });

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //need to implement for delete
                itemList.remove(item);
                LinearLayout mainLayout = findViewById(R.id.inventory_container_barcode);
                mainLayout.removeView(view);
            }
        });

        LinearLayout mainLayout = findViewById(R.id.inventory_container_barcode);
        mainLayout.addView(view);

    }

    private void displayEditPopup(Item item) {
        Dialog dialog = new Dialog(BarcodeActivity.this);
        dialog.setContentView(R.layout.edit_item_template);

        EditText quantityEditText = dialog.findViewById(R.id.edit_quantity);
        Button editExpiryDateButton = dialog.findViewById(R.id.edit_expiry_date_button);
        Button saveButton = dialog.findViewById(R.id.save_button);

        quantityEditText.setText(String.valueOf(item.getQuantity()));
        editExpiryDateButton.setOnClickListener(v -> showDatePickerDialog());
        saveButton.setOnClickListener(v -> {
            int updatedQuantity = Integer.parseInt(quantityEditText.getText().toString());
            if(expiryDateString != null && !expiryDateString.isEmpty() && updatedQuantity > 0){
                item.setQuantity(updatedQuantity);
                item.setExpiry(expiryDateString);
                addItemToInventory(item);
                dialog.dismiss();


            }

        });

        dialog.show();
    }

    public void showDatePickerDialog() {
        DatePickerFragment newFragment = new DatePickerFragment();
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }

    @Override
    public void onDateSet(int year, int month, int day) {
        // Use the selected date here as needed
        expiryDateString = String.format("%d-%02d-%02d", year, month + 1, day);
    }

    private void scanCode() {
        ScanOptions options = new ScanOptions();
        options.setPrompt("Volume up to turn on flash");
        options.setBeepEnabled(true);
        options.setOrientationLocked(true);
        options.setCaptureActivity(CaptureAct.class);
        barcodeLauncher.launch(options);
    }

    private void launchInventoryIntent() {
        Intent inventoryIntent = new Intent(BarcodeActivity.this, InventoryActivity.class);
        startActivity(inventoryIntent);
        finish();
    }


    private void handleUPCCode(String upcCode) {
        // Handle the UPC code as needed
        // For example, store it, display it, or process it in some way
        AlertDialog.Builder builder = new AlertDialog.Builder(BarcodeActivity.this);
        builder.setMessage(upcCode);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        }).show();
    }

    private void redirectToAnotherActivity() {
        // Code to redirect to another activity when no data is retrieved
        // For example, you can start a new activity here
        Intent intent = new Intent(BarcodeActivity.this, AddItemsActivity.class);
        startActivity(intent);
    }


//    ActivityResultLauncher<ScanOptions> barcodeLauncher = registerForActivityResult(new ScanContract(), result ->{
//        if (result.getContents() != null) {
//
////            String upcCode = result.getContents();
//
//            AlertDialog.Builder builder = new AlertDialog.Builder(BarcodeActivity.this);
//            builder.setMessage(result.getContents());
//            builder.setPositiveButton("OK", new DialogInterface.OnClickListener(){
//                @Override
//                public void onClick(DialogInterface dialogInterface, int i){
//                    dialogInterface.dismiss();
//                }
//            }).show();
//        }
//    });
}
