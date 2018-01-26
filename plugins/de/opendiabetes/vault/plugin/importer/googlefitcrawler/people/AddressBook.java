package de.opendiabetes.vault.plugin.importer.googlefitcrawler.people;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import de.opendiabetes.vault.plugin.importer.googlefitcrawler.models.Contact;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class AddressBook {
    private static AddressBook instance;

    private List<Contact> contacts;

    private AddressBook() {
        contacts = new ArrayList<>();
    }

    public static AddressBook getInstance() {
        if (AddressBook.instance == null) {
            AddressBook.instance = new AddressBook();
        }
        return AddressBook.instance;
    }

    public void addContact(Contact cnt) {
        contacts.add(cnt);
    }

    public void addMultipleContacts(List<Contact> cnt) {
        contacts.addAll(cnt);
    }

    public boolean isEmpty(){
        return contacts.isEmpty();
    }

    public int size(){
        return contacts.size();
    }

    public Contact getContactById(int id){
        return contacts.get(id);
    }

    public Contact getContactByName(String name){
        return contacts.stream().filter(c -> c.getName().equals(name)).findFirst().orElse(null);
    }

    public void export() {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String output = gson.toJson(this);

        File file = new File("addressBook" + System.currentTimeMillis() / 1000 + ".json");

        try {
            FileWriter writer = new FileWriter(file, false);
            writer.write(output);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
