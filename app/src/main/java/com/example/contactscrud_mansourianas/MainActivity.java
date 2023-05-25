package com.example.contactscrud_mansourianas;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private ContactAdapter adapter;
    private List<Contact> contactList = new ArrayList<>();

    String url = "http://10.0.2.2:8080/contact/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.rv_contacts);
        adapter = new ContactAdapter(this, contactList);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);

        //Retrofit Instance
        Retrofit retrofit = new Retrofit.Builder( )
                .baseUrl(url)
                .addConverterFactory(GsonConverterFactory.create( ))
                .build();

       ContactsAPI contactsAPI = retrofit.create(ContactsAPI.class);


        // call the API to get all contacts
        Call<List<Contact>> call = contactsAPI.getContacts();
        call.enqueue(new Callback<List<Contact>>() {
            @Override
            public void onResponse(Call<List<Contact>> call, Response<List<Contact>> response) {
                System.out.println("im here");
                contactList.addAll(response.body());
               // adapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Call<List<Contact>> call, Throwable t) {
                System.out.println("im here failure");
            }
        });
        // handle click on add contact button
        Button btnAddContact = findViewById(R.id.btn_add);
        btnAddContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // create a new contact dialog
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("Add New Contact");


                LayoutInflater inflater = getLayoutInflater();
                View view = inflater.inflate(R.layout.add_contact, null);
                builder.setView(view);

                // get the input fields
                EditText etFirstName = view.findViewById(R.id.etFirstName);
                EditText etLastName = view.findViewById(R.id.etLastName);
                EditText etEmail = view.findViewById(R.id.etEmail);
                EditText etPhone = view.findViewById(R.id.etPhone);

                // handle click on add button in the dialog
                builder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // create a new contact object
                        Contact newContact = new Contact(
                                etFirstName.getText().toString(),
                                etLastName.getText().toString(),
                                etEmail.getText().toString(),
                                etPhone.getText().toString()
                        );

                        // add the new contact to the server
                        Call<Contact> call = contactsAPI.createContact(newContact);
                        call.enqueue(new Callback<Contact>() {
                            @Override
                            public void onResponse(Call<Contact> call, Response<Contact> response) {

                                contactList.add(response.body());
                                adapter.notifyDataSetChanged();
                            }

                            @Override
                            public void onFailure(Call<Contact> call, Throwable t) {
                                Toast.makeText(MainActivity.this, "Failed to add contact", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });


                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });


                builder.show();
            }
        });
    }
    }