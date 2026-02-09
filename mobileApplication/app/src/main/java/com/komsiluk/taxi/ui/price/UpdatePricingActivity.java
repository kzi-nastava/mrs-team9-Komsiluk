package com.komsiluk.taxi.ui.price;

import android.os.Bundle;
import android.widget.Toast;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.komsiluk.taxi.R;
import com.komsiluk.taxi.data.remote.price.PriceResponse;
import com.komsiluk.taxi.data.remote.price.PriceService;
import com.komsiluk.taxi.ui.menu.BaseNavDrawerActivity;
import java.util.List;
import javax.inject.Inject;
import dagger.hilt.android.AndroidEntryPoint;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@AndroidEntryPoint
public class UpdatePricingActivity extends BaseNavDrawerActivity {

    @Inject
    PriceService pricingService;

    private PricingAdapter adapter;

    @Override
    protected int getContentLayoutId() {
        // Ovo je bitno - tvoj BaseNavDrawerActivity ce ovo ubaciti u contentContainer
        return R.layout.activity_update_pricing;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Postavi naslov u Navbaru ako tvoj BaseActivity ima tu metodu
        // setToolbarTitle("Pricing Management");

        RecyclerView rv = findViewById(R.id.rvPricing);
        rv.setLayoutManager(new LinearLayoutManager(this));

        loadPricing(rv);
    }

    private void loadPricing(RecyclerView rv) {
        pricingService.getAll().enqueue(new Callback<List<PriceResponse>>() {
            @Override
            public void onResponse(Call<List<PriceResponse>> call, Response<List<PriceResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    adapter = new PricingAdapter(response.body(), pricingService);
                    rv.setAdapter(adapter);
                }
            }
            @Override
            public void onFailure(Call<List<PriceResponse>> call, Throwable t) {
                Toast.makeText(UpdatePricingActivity.this, "Error loading prices", Toast.LENGTH_SHORT).show();
            }
        });
    }
}