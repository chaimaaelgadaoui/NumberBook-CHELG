/**
 * Application NumberBook — Auteur : Chaimaa ELGADAOUI (CHELG)
 */
package ma.ensa.numberbook;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {

    private static final String BASE_URL = "http://10.0.2.2/numberbook-api/api/";
    private static Retrofit retrofit;

    // Constructeur privé pour respecter le pattern Singleton
    private RetrofitClient() {}

    public static Retrofit getInstance() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
}