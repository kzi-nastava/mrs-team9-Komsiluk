package com.komsiluk.taxi.ui.ride;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.komsiluk.taxi.data.remote.ride.RideCreateRequest;
import com.komsiluk.taxi.data.remote.ride.RideRepository;
import com.komsiluk.taxi.data.remote.ride.RideResponse;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class OrderRideViewModel extends ViewModel {

    private final RideRepository repo;
    private final ExecutorService io = Executors.newSingleThreadExecutor();

    private final MutableLiveData<State> state = new MutableLiveData<>(State.idle());

    @Inject
    public OrderRideViewModel(RideRepository repo) {
        this.repo = repo;
    }

    public LiveData<State> getState() {
        return state;
    }

    public void orderRide(RideCreateRequest req) {
        state.setValue(State.loading());
        io.execute(() -> {
            RideRepository.Result<RideResponse> r = repo.orderRideSync(req);
            if (r.isSuccess()) state.postValue(State.success(r.data));
            else state.postValue(State.error(r.error));
        });
    }

    public static class State {
        public final boolean loading;
        public final RideResponse success;
        public final String error;

        private State(boolean loading, RideResponse success, String error) {
            this.loading = loading;
            this.success = success;
            this.error = error;
        }

        public static State idle() { return new State(false, null, null); }
        public static State loading() { return new State(true, null, null); }
        public static State success(RideResponse r) { return new State(false, r, null); }
        public static State error(String e) { return new State(false, null, e); }
    }
}
