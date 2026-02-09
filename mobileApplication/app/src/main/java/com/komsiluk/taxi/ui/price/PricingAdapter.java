package com.komsiluk.taxi.ui.price;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.komsiluk.taxi.data.remote.price.PriceResponse;
import com.komsiluk.taxi.data.remote.price.PriceService;
import com.komsiluk.taxi.data.remote.price.PriceUpdate;
import com.komsiluk.taxi.databinding.ItemPricingBinding;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PricingAdapter extends RecyclerView.Adapter<PricingAdapter.PricingViewHolder> {

    private final List<PriceResponse> pricingList;
    private final PriceService pricingService;

    public PricingAdapter(List<PriceResponse> pricingList, PriceService pricingService) {
        this.pricingList = pricingList;
        this.pricingService = pricingService;
    }

    @NonNull
    @Override
    public PricingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemPricingBinding binding = ItemPricingBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new PricingViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull PricingViewHolder holder, int position) {
        PriceResponse item = pricingList.get(position);
        holder.bind(item);
    }

    @Override
    public int getItemCount() {
        return pricingList.size();
    }

    class PricingViewHolder extends RecyclerView.ViewHolder {
        private final ItemPricingBinding b;

        PricingViewHolder(ItemPricingBinding binding) {
            super(binding.getRoot());
            this.b = binding;
        }

        void bind(PriceResponse item) {
            b.tvVehicleType.setText(item.vehicleType);
            b.etStartPrice.setText(String.valueOf(item.startingPrice));
            b.etPricePerKm.setText(String.valueOf(item.pricePerKm));

            b.btnSavePrice.setOnClickListener(v -> {
                try {
                    int newStart = Integer.parseInt(b.etStartPrice.getText().toString());
                    int newKm = Integer.parseInt(b.etPricePerKm.getText().toString());

                    updatePriceOnServer(item.vehicleType, new PriceUpdate(newStart, newKm));
                } catch (NumberFormatException e) {
                    Toast.makeText(itemView.getContext(), "Enter valid numbers", Toast.LENGTH_SHORT).show();
                }
            });
        }

        private void updatePriceOnServer(String type, PriceUpdate dto) {
            pricingService.update(type, dto).enqueue(new Callback<PriceResponse>() {
                @Override
                public void onResponse(Call<PriceResponse> call, Response<PriceResponse> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(itemView.getContext(), "Cena za " + type + " ažurirana!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(itemView.getContext(), "Greška pri čuvanju", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<PriceResponse> call, Throwable t) {
                    Toast.makeText(itemView.getContext(), "Mrežna greška", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}