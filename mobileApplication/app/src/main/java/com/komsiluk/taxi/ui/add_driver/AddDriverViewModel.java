package com.komsiluk.taxi.ui.add_driver;

import android.net.Uri;

import androidx.lifecycle.ViewModel;

import java.io.File;

import dagger.hilt.android.lifecycle.HiltViewModel;
import javax.inject.Inject;

@HiltViewModel
public class AddDriverViewModel extends ViewModel {

    public String firstName = "";
    public String lastName = "";
    public String address = "";
    public String city = "";
    public String phone = "";
    public String email = "";
    public File profileImageFile = null;
    public String carModel = "";
    public String carType = "";
    public String licencePlate = "";
    public int seats = 0;
    public boolean petFriendly = false;
    public boolean childSeat = false;

    @Inject
    public AddDriverViewModel() {}
}
