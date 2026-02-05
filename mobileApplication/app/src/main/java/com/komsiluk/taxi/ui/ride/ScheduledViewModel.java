package com.komsiluk.taxi.ui.ride;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.komsiluk.taxi.data.remote.ride.RideRepository;
import com.komsiluk.taxi.data.remote.ride.RideResponse;

import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@HiltViewModel
public class ScheduledViewModel extends ViewModel {

    public static class State {
        public boolean loading;
        public String error;
        public List<RideResponse> data;
    }

    private final RideRepository repo;
    private final MutableLiveData<State> state = new MutableLiveData<>(new State());

    @Inject
    public ScheduledViewModel(RideRepository repo) {
        this.repo = repo;
    }

    public LiveData<State> getState() { return state; }

    public void loadScheduled(Long userId) {
        State s = new State();
        s.loading = true;
        state.setValue(s);

        repo.getScheduledRides(userId).enqueue(new Callback<List<RideResponse>>() {
            @Override
            public void onResponse(Call<List<RideResponse>> call, Response<List<RideResponse>> response) {
                State st = new State();
                if (!response.isSuccessful() || response.body() == null) {
                    st.error = "Failed: " + response.code();
                    state.setValue(st);
                    return;
                }
                st.data = response.body();
                state.setValue(st);
            }

            @Override
            public void onFailure(Call<List<RideResponse>> call, Throwable t) {
                State st = new State();
                st.error = t.getMessage() != null ? t.getMessage() : "Network error";
                state.setValue(st);
            }
        });
    }
}
