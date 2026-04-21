/**
 * Application NumberBook — Auteur : Chaimaa ELGADAOUI (CHELG)
 */
package ma.ensa.numberbook;

import android.Manifest;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private static final String SIGNATURE = "CHELG_v1";
    private static final String TAG_SYNC = "CHELG_SYNC";

    private Button chelgBtnLoadContacts, chelgBtnSyncContacts, chelgBtnSearch;
    private EditText etKeyword;
    private RecyclerView recyclerViewContacts;
    private ContactAdapter chelgAdapter;
    private List<Contact> chelgContactList = new ArrayList<>();
    private ContactApi chelgContactApi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        chelgBtnLoadContacts = findViewById(R.id.btnLoadContacts);
        chelgBtnSyncContacts = findViewById(R.id.btnSyncContacts);
        chelgBtnSearch = findViewById(R.id.btnSearch);
        etKeyword = findViewById(R.id.etKeyword);
        recyclerViewContacts = findViewById(R.id.recyclerViewContacts);

        recyclerViewContacts.setLayoutManager(new LinearLayoutManager(this));
        chelgAdapter = new ContactAdapter(chelgContactList);
        recyclerViewContacts.setAdapter(chelgAdapter);

        chelgContactApi = RetrofitClient.getInstance().create(ContactApi.class);

        chelgBtnLoadContacts.setOnClickListener(v -> checkPermissionAndLoadContacts());
        chelgBtnSyncContacts.setOnClickListener(v -> syncContactsToServer());
        chelgBtnSearch.setOnClickListener(v -> searchContacts());
    }

    private void checkPermissionAndLoadContacts() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS)
                == PackageManager.PERMISSION_GRANTED) {
            loadContacts();
        } else {
            requestPermissionLauncher.launch(Manifest.permission.READ_CONTACTS);
        }
    }

    private final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    loadContacts();
                } else {
                    Toast.makeText(this, "Permission refusée", Toast.LENGTH_SHORT).show();
                }
            });

    private void loadContacts() {
        chelgContactList.clear();
        Set<String> processedNumbers = new HashSet<>();

        Cursor cursor = getContentResolver().query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                null,
                null,
                null,
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC"
        );

        if (cursor != null) {
            while (cursor.moveToNext()) {
                String name = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                String phone = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.NUMBER));

                String cleanPhone = phone.replaceAll("[^0-9+]", "");

                if (!processedNumbers.contains(cleanPhone)) {
                    chelgContactList.add(new Contact(name, phone));
                    processedNumbers.add(cleanPhone);
                }
            }
            cursor.close();
        }

        chelgAdapter.updateData(chelgContactList);
        Toast.makeText(this, "Contacts uniques chargés : " + chelgContactList.size(), Toast.LENGTH_SHORT).show();
    }

    private void syncContactsToServer() {
        Log.d(TAG_SYNC, "Début de la synchronisation par " + SIGNATURE);
        Toast.makeText(this, "Synchronisation CHELG en cours...", Toast.LENGTH_SHORT).show();

        for (Contact contact : chelgContactList) {
            chelgContactApi.insertContact(contact).enqueue(new Callback<ApiResponse>() {
                @Override
                public void onResponse(@NonNull Call<ApiResponse> call, @NonNull Response<ApiResponse> response) {
                }
                @Override
                public void onFailure(@NonNull Call<ApiResponse> call, @NonNull Throwable t) {
                }
            });
        }
    }

    private void searchContacts() {
        String keyword = etKeyword.getText().toString().trim();
        if (keyword.isEmpty()) {
            Toast.makeText(this, "Saisir un nom ou un numéro", Toast.LENGTH_SHORT).show();
            return;
        }

        chelgContactApi.searchContacts(keyword).enqueue(new Callback<List<Contact>>() {
            @Override
            public void onResponse(@NonNull Call<List<Contact>> call, @NonNull Response<List<Contact>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // FILTRE DES DOUBLONS VENANT DU SERVEUR
                    Map<String, Contact> uniqueResults = new LinkedHashMap<>();
                    for (Contact c : response.body()) {
                        String cleanNumber = c.getPhone().replaceAll("[^0-9+]", "");
                        // On garde la première occurrence du numéro trouvée
                        if (!uniqueResults.containsKey(cleanNumber)) {
                            uniqueResults.put(cleanNumber, c);
                        }
                    }
                    
                    List<Contact> filteredList = new ArrayList<>(uniqueResults.values());
                    chelgAdapter.updateData(filteredList);
                    
                    if (filteredList.isEmpty()) {
                        Toast.makeText(MainActivity.this, "Aucun résultat unique", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Contact>> call, @NonNull Throwable t) {
                Toast.makeText(MainActivity.this, "Erreur lors de la recherche", Toast.LENGTH_SHORT).show();
            }
        });
    }
}