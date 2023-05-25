package com.example.contactscrud_mansourianas;

import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ContactViewHolder> {

    private List<Contact> contactList = new ArrayList<>();
    private Context context;


    public ContactAdapter(Context context, List<Contact> contactList) {
        this.contactList = contactList;
        this.context = context;
    }

    @Override
    public ContactViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.contact_item, parent, false);
        return new ContactViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ContactViewHolder holder, int position) {
        final Contact contact = contactList.get(position);
        holder.tvFirstName.setText(contact.getFirstname());
        holder.tvLastName.setText(contact.getLastname());
        holder.tvEmail.setText(contact.getEmail());
        holder.tvPhone.setText(contact.getPhone());
        holder.btnEdit.setTag(contact);


        holder.btnEdit.setOnClickListener(view -> {
            Contact contactToEdit = (Contact) view.getTag();
            showEditContactDialog(contactToEdit);
        });


        holder.btnDelete.setTag(contact);
        holder.btnDelete.setOnClickListener(view -> {
            Contact contactToDelete = (Contact) view.getTag();
            showDeleteContactDialog(contactToDelete.getId());
        });
    }

    @Override
    public int getItemCount() {
        return contactList.size();
    }

    public static class ContactViewHolder extends RecyclerView.ViewHolder {
        public TextView tvFirstName, tvLastName, tvEmail, tvPhone;
        public AppCompatImageButton btnEdit, btnDelete;

        public ContactViewHolder(View view) {
            super(view);
            tvFirstName = view.findViewById(R.id.tvFirstName);
            tvLastName = view.findViewById(R.id.tvLastName);
            tvEmail = view.findViewById(R.id.tvEmail);
            tvPhone = view.findViewById(R.id.tvPhone);
            btnEdit = view.findViewById(R.id.btnEdit);
            btnDelete = view.findViewById(R.id.btnDelete);
        }
    }

    private void showEditContactDialog(Contact contact) {
        // Show dialog to edit contact
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Edit Contact");

        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.add_contact, null);
        builder.setView(view);

        final EditText etFirstName = view.findViewById(R.id.etFirstName);
        final EditText etLastName = view.findViewById(R.id.etLastName);
        final EditText etEmail = view.findViewById(R.id.etEmail);
        final EditText etPhone = view.findViewById(R.id.etPhone);

        // Set current contact details in the dialog
        etFirstName.setText(contact.getFirstname());
        etLastName.setText(contact.getLastname());
        etEmail.setText(contact.getEmail());
        etPhone.setText(contact.getPhone());

        builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Retrofit retrofit = new Retrofit.Builder( )
                        .baseUrl("http://10.0.2.2:8080/contact/")
                        .addConverterFactory(GsonConverterFactory.create( ))
                        .build();

                ContactsAPI contactsAPI = retrofit.create(ContactsAPI.class);
                // Update contact details in the database
                String firstName = etFirstName.getText().toString().trim();
                String lastName = etLastName.getText().toString().trim();
                String email = etEmail.getText().toString().trim();
                String phone = etPhone.getText().toString().trim();

                // Make API call to update contact in the database
                Call<Contact> call = contactsAPI.updateContact(contact.getId(),new Contact(firstName,lastName,email,phone));
                call.enqueue(new Callback<Contact>() {
                    @Override
                    public void onResponse(Call<Contact> call, Response<Contact> response) {
                      //  if (response.isSuccessful()) {
                            // Update contact in the contact list and notify adapter
                            contact.setFirstname(firstName);
                            contact.setLastname(lastName);
                            contact.setEmail(email);
                            contact.setPhone(phone);
                            notifyDataSetChanged();
                            Toast.makeText(context, "Contact updated successfully", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(Call<Contact> call, Throwable t) {
                        Toast.makeText(context, "Failed to update contact", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void showDeleteContactDialog(int id) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Delete Contact");
        builder.setMessage("Are you sure you want to delete this contact?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                Retrofit retrofit = new Retrofit.Builder( )
                        .baseUrl("http://10.0.2.2:8080/contact/")
                        .addConverterFactory(GsonConverterFactory.create( ))
                        .build();

                ContactsAPI contactsAPI = retrofit.create(ContactsAPI.class);
                Call<Void> call = contactsAPI.deleteContact(id);
                call.enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        notifyItemRemoved(id);
                        Toast.makeText(context, "Contact deleted successfully", Toast.LENGTH_SHORT).show();


                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        Toast.makeText(context, "Failed to delete contact", Toast.LENGTH_SHORT).show();

                    }


                });
            }
        });
        builder.setNegativeButton("No", null);
        AlertDialog dialog = builder.create();
        dialog.show();
    }



}





