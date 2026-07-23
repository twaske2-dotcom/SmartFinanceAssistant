package com.example.smartfinanceassistant.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartfinanceassistant.R;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ProductSearchActivity extends AppCompatActivity {

    private TextInputEditText etSearch, etBudget;
    private Button btnSearch;
    private ProgressBar progressBar;
    private TextView tvStatus;
    private RecyclerView rvProducts;
    private ProductAdapter productAdapter;

    private static final String API_KEY =
            "87e7675cb0mshbe1e71d0b728763p1d8bb5jsnb9f0254d53d6";
    private static final String API_HOST =
            "real-time-amazon-data.p.rapidapi.com";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_search);

        etSearch    = findViewById(R.id.etSearch);
        etBudget    = findViewById(R.id.etBudget);
        btnSearch   = findViewById(R.id.btnSearch);
        progressBar = findViewById(R.id.progressBar);
        tvStatus    = findViewById(R.id.tvStatus);
        rvProducts  = findViewById(R.id.rvProducts);

        productAdapter = new ProductAdapter(new ArrayList<>());
        rvProducts.setLayoutManager(
                new LinearLayoutManager(this));
        rvProducts.setAdapter(productAdapter);

        btnSearch.setOnClickListener(v -> searchProducts());
    }

    private void searchProducts() {
        String query = etSearch.getText()
                .toString().trim();
        String budgetStr = etBudget.getText()
                .toString().trim();

        if (query.isEmpty()) {
            etSearch.setError("Enter product name!");
            return;
        }

        double budget = budgetStr.isEmpty() ?
                Double.MAX_VALUE :
                Double.parseDouble(budgetStr);

        progressBar.setVisibility(View.VISIBLE);
        tvStatus.setText("Searching best deals...");
        btnSearch.setEnabled(false);

        String url = "https://real-time-amazon-data.p.rapidapi.com"
                + "/search?query="
                + query.replace(" ", "%20")
                + "&page=1&country=IN"
                + "&sort_by=RELEVANCE"
                + "&product_condition=ALL"
                + "&is_prime=false";

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .get()
                .addHeader("x-rapidapi-key", API_KEY)
                .addHeader("x-rapidapi-host", API_HOST)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(() -> {
                    progressBar.setVisibility(View.GONE);
                    btnSearch.setEnabled(true);
                    tvStatus.setText("Network error!");
                    Toast.makeText(ProductSearchActivity.this,
                            "Check internet connection!",
                            Toast.LENGTH_SHORT).show();
                });
            }

            @Override
            public void onResponse(Call call,
                                   Response response) throws IOException {
                String body = response.body().string();
                android.util.Log.d("API_RESPONSE", body);
                runOnUiThread(() -> {
                    progressBar.setVisibility(View.GONE);
                    btnSearch.setEnabled(true);
                    parseAndShowResults(body, budget);
                });
            }
        });
    }

    private void parseAndShowResults(
            String json, double budget) {
        try {
            List<ProductAdapter.Product> products =
                    new ArrayList<>();

            JSONObject root = new JSONObject(json);

            // Check status
            String status = root.optString("status", "");
            if (!status.equals("OK")) {
                tvStatus.setText("API Error: " + status);
                return;
            }

            JSONObject data = root.optJSONObject("data");
            if (data == null) {
                tvStatus.setText("No data received!");
                return;
            }

            JSONArray items = data.optJSONArray("products");
            if (items == null || items.length() == 0) {
                tvStatus.setText("No products found!");
                return;
            }

            for (int i = 0; i < items.length(); i++) {
                JSONObject item = items.getJSONObject(i);

                String title = item.optString(
                        "product_title", "Unknown");
                String price = item.optString(
                        "product_price", "Price N/A");
                String rating = item.optString(
                        "product_star_rating", "N/A");
                String url = item.optString(
                        "product_url", "");
                String image = item.optString(
                        "product_photo", "");

                // Budget filter
                double priceVal = 0;
                // Exception Handling
                try {
                    String clean = price
                            .replace("₹", "")
                            .replace(",", "")
                            .replace("$", "")
                            .trim();
                    // Take first number only
                    if (clean.contains(" ")) {
                        clean = clean.split(" ")[0];
                    }
                    priceVal = Double.parseDouble(clean);
                } catch (Exception e) {
                    priceVal = 0;
                }

                if (priceVal == 0 || priceVal <= budget) {
                    products.add(
                            new ProductAdapter.Product(
                                    title, price,
                                    rating, url, image));
                }
            }

            if (products.isEmpty()) {
                tvStatus.setText(
                        "No products in your budget!");
            } else {
                tvStatus.setText(
                        products.size() + " products found! 🎉");
                productAdapter.updateList(products);
            }

        } catch (Exception e) {
            tvStatus.setText("Parse error: "
                    + e.getMessage());
            android.util.Log.e("PARSE_ERROR",
                    e.getMessage());
        }
    }
}