package com.komsiluk.taxi.ui.add_driver;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.komsiluk.taxi.data.remote.add_driver.DriverCreate;
import com.komsiluk.taxi.data.remote.add_driver.DriverResponse;
import com.komsiluk.taxi.data.remote.add_driver.VehicleCreate;
import com.komsiluk.taxi.data.remote.driver_history.DriverService;
import com.komsiluk.taxi.util.Event;

import java.io.IOException;

import dagger.hilt.android.lifecycle.HiltViewModel;
import jakarta.inject.Inject;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@HiltViewModel
public class AddDriverSubmitViewModel extends ViewModel {

    private final DriverService driverService;

    private final MutableLiveData<Event<String>> success = new MutableLiveData<>();
    private final MutableLiveData<Event<String>> error = new MutableLiveData<>();

    @Inject
    public AddDriverSubmitViewModel(DriverService driverService) {
        this.driverService = driverService;
    }

    public LiveData<Event<String>> getSuccess() { return success; }
    public LiveData<Event<String>> getError() { return error; }

    public void submit(AddDriverViewModel vm) {
        DriverCreate req = new DriverCreate();
        req.setFirstName(vm.firstName);
        req.setLastName(vm.lastName);
        req.setAddress(vm.address);
        req.setCity(vm.city);
        req.setPhoneNumber(vm.phone);
        req.setEmail(vm.email);

        VehicleCreate vehicle = new VehicleCreate();
        vehicle.setModel(vm.carModel);
        vehicle.setType(normalizeVehicleType(vm.carType));
        vehicle.setLicencePlate(vm.licencePlate);
        vehicle.setSeatCount(vm.seats);
        vehicle.setPetFriendly(vm.petFriendly);
        vehicle.setBabyFriendly(vm.childSeat);

        req.setVehicle(vehicle);

        String json = new com.google.gson.Gson().toJson(req);

        RequestBody dataPart = RequestBody.create(
                okhttp3.MediaType.parse("application/json"),
                json
        );

        Log.d("AddDriverSubmitVM", "Submitting driver: " + json);

        MultipartBody.Part imagePart = null;
        if (vm.profileImageFile != null) {
            RequestBody rb = RequestBody.create(
                    okhttp3.MediaType.parse("image/*"),
                    vm.profileImageFile
            );

            imagePart = MultipartBody.Part.createFormData(
                    "profileImage",
                    vm.profileImageFile.getName(),
                    rb
            );
        }

        driverService.registerDriver(dataPart, imagePart).enqueue(new Callback<DriverResponse>() {
            @Override
            public void onResponse(Call<DriverResponse> call, Response<DriverResponse> response) {
                if (response.isSuccessful()) {
                    success.setValue(new Event<>("Driver created."));
                } else if(response.errorBody() != null) {
                    try {
                        error.setValue(new Event<>("Create driver failed (" + response.code() +", "+ response.errorBody().string() +")."));
                    } catch (IOException e) {
                        error.setValue(new Event<>("Create driver failed (" + response.code() + ")."));
                    }
                }
            }

            @Override
            public void onFailure(Call<DriverResponse> call, Throwable t) {
                error.setValue(new Event<>("Unable to connect."));
            }
        });
    }

    private String normalizeVehicleType(String ui) {
        if (ui == null) return null;
        return ui.trim().toUpperCase();
    }

}
