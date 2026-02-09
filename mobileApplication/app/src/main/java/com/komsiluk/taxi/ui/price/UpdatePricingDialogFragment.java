package com.komsiluk.taxi.ui.price;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.komsiluk.taxi.R;
import com.komsiluk.taxi.data.remote.price.PriceResponse;
import com.komsiluk.taxi.data.remote.price.PriceService;

import java.util.List;

import dagger.hilt.android.AndroidEntryPoint;
import jakarta.inject.Inject;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@AndroidEntryPoint
public class UpdatePricingDialogFragment extends DialogFragment {

    @Inject
    PriceService pricingService;
    private PricingAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.activity_update_pricing, container, false);

        RecyclerView rv = v.findViewById(R.id.rvPricing);
        rv.setLayoutManager(new LinearLayoutManager(requireContext()));

        loadPricing(rv);

        return v;
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
                Toast.makeText(getContext(), "Error loading prices", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
