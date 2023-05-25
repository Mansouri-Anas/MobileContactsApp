package com.example.contactscrud_mansourianas;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ContactsAPI {

        @GET("getallcontacts.php")
        Call<List<Contact>> getContacts();

        @POST("contacts.php")
        Call<Contact> createContact(@Body Contact contact);

        @PUT("contacts.php")
        Call<Contact> updateContact(@Query("id") int id, @Body Contact contact);

        @DELETE("contacts.php")
        Call<Void> deleteContact(@Query("id") int id);


}
