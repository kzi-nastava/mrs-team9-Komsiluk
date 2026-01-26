package com.komsiluk.taxi.ui.auth.rider_registration;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.gson.Gson;
import com.komsiluk.taxi.data.remote.auth.AuthService;
import com.komsiluk.taxi.data.remote.auth.PassengerRegistrationRequest;
import com.komsiluk.taxi.data.remote.auth.PassengerRegistrationResponse;
import com.komsiluk.taxi.util.Event;

import org.json.JSONObject;

import java.io.File;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@HiltViewModel
public class PassengerRegistrationViewModel extends ViewModel {


    private MutableLiveData<Event<String>> successMessageEvent = new MutableLiveData<>();

    private MutableLiveData<Event<String>> errorMessageEvent = new MutableLiveData<>();

    /** Live Data za polja **/

    public final MutableLiveData<String> firstName = new MutableLiveData<>("");
    public final MutableLiveData<String> lastName  = new MutableLiveData<>("");
    public final MutableLiveData<String> address   = new MutableLiveData<>("");
    public final MutableLiveData<String> city      = new MutableLiveData<>("");
    public final MutableLiveData<String> phone     = new MutableLiveData<>("");
    public final MutableLiveData<String> email     = new MutableLiveData<>("");
    public final MutableLiveData<String> password  = new MutableLiveData<>("");
    public final MutableLiveData<String> repeat    = new MutableLiveData<>();

    public File profileImageFile;

    public LiveData<Event<String>> getSuccessMessageEvent() { return this.successMessageEvent; }

    public LiveData<Event<String>> getErrorMessageEvent() { return this.errorMessageEvent; }

    private AuthService authService;

    @Inject
    public PassengerRegistrationViewModel(AuthService authService) { this.authService = authService;}


    public void submit() {
        PassengerRegistrationRequest request = new PassengerRegistrationRequest(
                firstName.getValue(),
                lastName.getValue(),
                address.getValue(),
                city.getValue(),
                phone.getValue(),
                email.getValue(),
                password.getValue(),
                repeat.getValue()
        );

        Gson gson = new Gson();
        String json = gson.toJson(request);

        RequestBody dataPart = RequestBody.create(
                MediaType.parse("application/json"),
                json
        );

        /** PRIPREMA SLIKE **/


        MultipartBody.Part imagePart = null;

        if (profileImageFile != null) {
            RequestBody imageRequestBody = RequestBody.create(
                    MediaType.parse("image/*"),
                    profileImageFile
            );

            imagePart = MultipartBody.Part.createFormData(
                    "profileImage",
                    profileImageFile.getName(),
                    imageRequestBody
            );
        }



        Call<PassengerRegistrationResponse> call = authService.registerPassenger(dataPart, imagePart);
        call.enqueue(new Callback<PassengerRegistrationResponse>() {

            @Override
            public void onResponse(Call<PassengerRegistrationResponse> call, Response<PassengerRegistrationResponse> response) {
                if(response.isSuccessful() && response.body() != null) {
                    successMessageEvent.setValue(new Event<>(response.body().getMessage()));
                } else {
                    String backendMessage = parseErrorMessage(response);
                    errorMessageEvent.setValue(new Event<>("Registration Failed." + backendMessage));
                }
            }

            @Override
            public void onFailure(Call<PassengerRegistrationResponse> call, Throwable t) {
                errorMessageEvent.postValue(new Event<>("Unable to connect. Please check your internet connection."));
            }
        });
    }

    private String parseErrorMessage(Response<?> response) {
        try {
            if (response.errorBody() != null) {
                String body = response.errorBody().string();
                try {
                    JSONObject obj = new JSONObject(body);
                    return obj.optString("message", body); // koristi plain text ako nema message polja
                } catch (Exception e) {
                    return body; // plain text
                }
            }
        } catch (Exception e) {
            return "Unexpected error occurred";
        }
        return "Unknown error";
    }


}
