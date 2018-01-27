package de.opendiabetes.vault.plugin.importer.googlecrawler.people;

import com.google.api.services.people.v1.PeopleService;
import com.google.api.services.people.v1.model.Person;
import de.opendiabetes.vault.plugin.importer.googlecrawler.helper.Credentials;
import de.opendiabetes.vault.plugin.importer.googlecrawler.models.Contact;

import java.io.IOException;
import java.util.List;

public class GooglePeople {
    private static GooglePeople instance;
    private Person profile;

    private PeopleService peopleService;


    private GooglePeople() {
        construct();
    }

    public static GooglePeople getInstance() {
        if (GooglePeople.instance == null) {
            GooglePeople.instance = new GooglePeople();
        }
        return GooglePeople.instance;
    }

    private void getOwnProfile() {
        try {
            profile = peopleService.people().get("people/me")
                    .setPersonFields("names,addresses")
                    .execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void getAllProfiles() {
        try {
            List<Person> persons = peopleService.people().connections().list("people/me").setPersonFields("names,addresses").setPageSize(500).execute().getConnections();

            for (Person p : persons) {
                if (p.getAddresses() != null) {
                    Contact c = new Contact(p.getNames().get(0).getDisplayName(), p.getAddresses());
                    AddressBook.getInstance().addContact(c);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void construct() {
        peopleService = new PeopleService.Builder(Credentials.getInstance().getHttpTransport(), Credentials.getInstance().getJsonFactory(), Credentials.getInstance().getCredential())
                .setApplicationName(Credentials.getInstance().getApplicationName())
                .build();
        getOwnProfile();
    }

    public Person getProfile() {
        return profile;
    }
}
