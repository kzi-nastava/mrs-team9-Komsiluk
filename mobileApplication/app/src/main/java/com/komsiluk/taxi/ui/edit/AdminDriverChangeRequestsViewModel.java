package com.komsiluk.taxi.ui.edit;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.komsiluk.taxi.data.remote.edit_requests.ProfileChangeRequestResponse;
import com.komsiluk.taxi.data.remote.edit_requests.EditRequestsService;
import com.komsiluk.taxi.data.remote.profile.UserProfileResponse;
import com.komsiluk.taxi.data.remote.profile.UserService;
import com.komsiluk.taxi.data.session.SessionManager;
import com.komsiluk.taxi.util.Event;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@HiltViewModel
public class AdminDriverChangeRequestsViewModel extends ViewModel {

    private final EditRequestsService editRequestsService;
    private final UserService userService;
    private final SessionManager sessionManager;

    private final MutableLiveData<List<DriverChangeRequest>> items = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<Event<String>> toastEvent = new MutableLiveData<>();
    private final MutableLiveData<Event<DriverChangeRequest>> openDialogEvent = new MutableLiveData<>();

    private final HashMap<Long, ProfileChangeRequestResponse> requestById = new HashMap<>();
    private final HashMap<Long, UserProfileResponse> profileCache = new HashMap<>();

    @Inject
    public AdminDriverChangeRequestsViewModel(EditRequestsService profileService, SessionManager sessionManager, UserService userService) {
        this.userService = userService;
        this.editRequestsService = profileService;
        this.sessionManager = sessionManager;
    }

    public LiveData<List<DriverChangeRequest>> getItems() { return items; }
    public LiveData<Event<String>> getToastEvent() { return toastEvent; }
    public LiveData<Event<DriverChangeRequest>> getOpenDialogEvent() { return openDialogEvent; }

    public void fetchPending() {
        editRequestsService.getPendingDriverEditRequests().enqueue(new Callback<List<ProfileChangeRequestResponse>>() {
            @Override
            public void onResponse(Call<List<ProfileChangeRequestResponse>> call, Response<List<ProfileChangeRequestResponse>> response) {
                if (!response.isSuccessful() || response.body() == null) {
                    toastEvent.setValue(new Event<>("Unable to load pending requests."));
                    return;
                }

                requestById.clear();

                ArrayList<DriverChangeRequest> ui = new ArrayList<>();
                for (ProfileChangeRequestResponse r : response.body()) {
                    requestById.put(r.getId(), r);

                    String email = "Driver #" + r.getDriverId();
                    String date = formatDate(r.getRequestedAt());

                    ArrayList<String> tags = new ArrayList<>();
                    ArrayList<DriverChangeRequest.FieldChange> rows = new ArrayList<>();

                    ui.add(new DriverChangeRequest(
                            r.getId(),
                            email,
                            date,
                            tags,
                            rows
                    ));

                    warmUpDriverProfile(r.getDriverId());
                }

                items.setValue(ui);
            }

            @Override
            public void onFailure(Call<List<ProfileChangeRequestResponse>> call, Throwable t) {
                toastEvent.setValue(new Event<>("Unable to connect."));
            }
        });
    }

    private void warmUpDriverProfile(Long driverId) {
        if (driverId == null) return;
        if (profileCache.containsKey(driverId)) return;

        userService.getProfile(driverId).enqueue(new Callback<UserProfileResponse>() {
            @Override
            public void onResponse(Call<UserProfileResponse> call, Response<UserProfileResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    UserProfileResponse prof = response.body();
                    profileCache.put(driverId, prof);

                    updateEmailForDriver(driverId, prof.getEmail());
                    updateTagsForDriver(driverId, prof);
                }
            }

            @Override
            public void onFailure(Call<UserProfileResponse> call, Throwable t) { }
        });
    }

    private void updateTagsForDriver(Long driverId, UserProfileResponse profile) {
        List<DriverChangeRequest> cur = items.getValue();
        if (cur == null) return;

        boolean changed = false;
        ArrayList<DriverChangeRequest> copy = new ArrayList<>(cur.size());

        for (DriverChangeRequest x : cur) {
            ProfileChangeRequestResponse r = requestById.get(x.getId());

            if (r != null && driverId.equals(r.getDriverId())) {
                ArrayList<DriverChangeRequest.FieldChange> rows = buildRowsOnly(r, profile);

                ArrayList<String> tags = buildTagsFromRows(rows);
                if (tags.isEmpty()) tags.add("No changes");

                copy.add(new DriverChangeRequest(
                        x.getId(),
                        x.getDriverEmail(),
                        x.getCreatedAt(),
                        tags,
                        x.getChanges()
                ));
                changed = true;
            } else {
                copy.add(x);
            }
        }

        if (changed) items.setValue(copy);
    }

    private ArrayList<DriverChangeRequest.FieldChange> buildRowsOnly(ProfileChangeRequestResponse r, UserProfileResponse p) {
        ArrayList<DriverChangeRequest.FieldChange> rows = new ArrayList<>();

        addIfChanged(rows, "First name", safe(p.getFirstName()), safe(r.getNewName()));
        addIfChanged(rows, "Last name",  safe(p.getLastName()),  safe(r.getNewSurname()));

        addIfChanged(rows, "Address", safe(p.getAddress()), safe(r.getNewAddress()));
        addIfChanged(rows, "City",    safe(p.getCity()),    safe(r.getNewCity()));

        addIfChanged(rows, "Phone number", safe(p.getPhoneNumber()), safe(r.getNewPhoneNumber()));

        if (p.getVehicle() != null) {
            addIfChanged(rows, "Model", safe(p.getVehicle().getModel()), safe(r.getNewModel()));
            addIfChanged(rows, "Type", safe(String.valueOf(p.getVehicle().getType())), safe(r.getNewType()));
            addIfChanged(rows, "Licence plate", safe(p.getVehicle().getLicencePlate()), safe(r.getNewLicencePlate()));

            String curSeats = p.getVehicle().getSeatCount() == null ? "-" : String.valueOf(p.getVehicle().getSeatCount());
            String reqSeats = r.getNewSeatCount() == null ? "-" : String.valueOf(r.getNewSeatCount());
            addIfChanged(rows, "Seats", curSeats, reqSeats);

            addIfChanged(rows, "Baby friendly", yesNo(p.getVehicle().getBabyFriendly()), yesNo(r.getNewBabyFriendly()));
            addIfChanged(rows, "Pet friendly",  yesNo(p.getVehicle().getPetFriendly()),  yesNo(r.getNewPetFriendly()));
        } else {
            addIfChanged(rows, "Model", "-", safe(r.getNewModel()));
            addIfChanged(rows, "Type", "-", safe(r.getNewType()));
            addIfChanged(rows, "Licence plate", "-", safe(r.getNewLicencePlate()));
            addIfChanged(rows, "Seats", "-", (r.getNewSeatCount() == null ? "-" : String.valueOf(r.getNewSeatCount())));
            addIfChanged(rows, "Baby friendly", "-", yesNo(r.getNewBabyFriendly()));
            addIfChanged(rows, "Pet friendly", "-", yesNo(r.getNewPetFriendly()));
        }

        return rows;
    }

    private void updateEmailForDriver(Long driverId, String email) {
        List<DriverChangeRequest> cur = items.getValue();
        if (cur == null) return;

        boolean changed = false;
        ArrayList<DriverChangeRequest> copy = new ArrayList<>(cur.size());

        for (DriverChangeRequest x : cur) {
            ProfileChangeRequestResponse r = requestById.get(x.getId());
            if (r != null && driverId.equals(r.getDriverId())) {
                String e = (email == null || email.trim().isEmpty()) ? x.getDriverEmail() : email.trim();
                copy.add(new DriverChangeRequest(x.getId(), e, x.getCreatedAt(), x.getTags(), x.getChanges()));
                changed = true;
            } else {
                copy.add(x);
            }
        }

        if (changed) items.setValue(copy);
    }

    public void onItemClicked(DriverChangeRequest uiItem) {
        ProfileChangeRequestResponse r = requestById.get(uiItem.getId());
        if (r == null) return;

        Long driverId = r.getDriverId();
        if (driverId == null) return;

        UserProfileResponse cached = profileCache.get(driverId);
        if (cached != null) {
            DriverChangeRequest full = buildDialogModel(r, cached, uiItem);
            openDialogEvent.setValue(new Event<>(full));
            return;
        }

        userService.getProfile(driverId).enqueue(new Callback<UserProfileResponse>() {
            @Override
            public void onResponse(Call<UserProfileResponse> call, Response<UserProfileResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    profileCache.put(driverId, response.body());
                    DriverChangeRequest full = buildDialogModel(r, response.body(), uiItem);
                    openDialogEvent.setValue(new Event<>(full));
                } else {
                    toastEvent.setValue(new Event<>("Unable to load driver profile."));
                }
            }

            @Override
            public void onFailure(Call<UserProfileResponse> call, Throwable t) {
                toastEvent.setValue(new Event<>("Unable to connect."));
            }
        });
    }

    public void approve(long requestId) {
        Long adminId = sessionManager.getUserId();
        if (adminId == null) {
            toastEvent.setValue(new Event<>("Missing admin session."));
            return;
        }

        editRequestsService.approveDriverEditRequest(requestId, adminId).enqueue(new Callback<ProfileChangeRequestResponse>() {
            @Override
            public void onResponse(Call<ProfileChangeRequestResponse> call, Response<ProfileChangeRequestResponse> response) {
                if (response.isSuccessful()) {
                    removeFromList(requestId);
                    requestById.remove(requestId);
                    toastEvent.setValue(new Event<>("Approved."));
                } else {
                    toastEvent.setValue(new Event<>("Approve failed."));
                }
            }

            @Override
            public void onFailure(Call<ProfileChangeRequestResponse> call, Throwable t) {
                toastEvent.setValue(new Event<>("Unable to connect."));
            }
        });
    }

    public void reject(long requestId) {
        Long adminId = sessionManager.getUserId();
        if (adminId == null) {
            toastEvent.setValue(new Event<>("Missing admin session."));
            return;
        }

        editRequestsService.rejectDriverEditRequest(requestId, adminId).enqueue(new Callback<ProfileChangeRequestResponse>() {
            @Override
            public void onResponse(Call<ProfileChangeRequestResponse> call, Response<ProfileChangeRequestResponse> response) {
                if (response.isSuccessful()) {
                    removeFromList(requestId);
                    requestById.remove(requestId);
                    toastEvent.setValue(new Event<>("Rejected."));
                } else {
                    toastEvent.setValue(new Event<>("Reject failed."));
                }
            }

            @Override
            public void onFailure(Call<ProfileChangeRequestResponse> call, Throwable t) {
                toastEvent.setValue(new Event<>("Unable to connect."));
            }
        });
    }

    private void removeFromList(long requestId) {
        List<DriverChangeRequest> cur = items.getValue();
        if (cur == null) return;

        ArrayList<DriverChangeRequest> copy = new ArrayList<>();
        for (DriverChangeRequest x : cur) {
            if (x.getId() != requestId) copy.add(x);
        }
        items.setValue(copy);
    }

    private ArrayList<String> buildTagsFromRows(List<DriverChangeRequest.FieldChange> rows) {
        boolean identity = false, address = false, contact = false, vehicle = false, options = false;

        for (DriverChangeRequest.FieldChange fc : rows) {
            String f = fc.getField();
            if (f == null) continue;

            switch (f) {
                case "First name":
                case "Last name":
                    identity = true; break;

                case "Address":
                case "City":
                    address = true; break;

                case "Phone number":
                    contact = true; break;

                case "Model":
                case "Type":
                case "Licence plate":
                case "Seats":
                    vehicle = true; break;

                case "Baby friendly":
                case "Pet friendly":
                    options = true; break;
            }
        }

        ArrayList<String> tags = new ArrayList<>();
        if (identity) tags.add("Identity");
        if (address)  tags.add("Address");
        if (contact)  tags.add("Contact");
        if (vehicle)  tags.add("Vehicle");
        if (options)  tags.add("Options");

        return tags;
    }

    private DriverChangeRequest buildDialogModel(ProfileChangeRequestResponse r, UserProfileResponse p, DriverChangeRequest base) {
        ArrayList<DriverChangeRequest.FieldChange> rows = new ArrayList<>();

        // identity
        addIfChanged(rows, "First name", safe(p.getFirstName()), safe(r.getNewName()));
        addIfChanged(rows, "Last name",  safe(p.getLastName()),  safe(r.getNewSurname()));

        // address
        addIfChanged(rows, "Address", safe(p.getAddress()), safe(r.getNewAddress()));
        addIfChanged(rows, "City",    safe(p.getCity()),    safe(r.getNewCity()));

        // contact
        addIfChanged(rows, "Phone number", safe(p.getPhoneNumber()), safe(r.getNewPhoneNumber()));

        // vehicle
        if (p.getVehicle() != null) {
            addIfChanged(rows, "Model", safe(p.getVehicle().getModel()), safe(r.getNewModel()));
            addIfChanged(rows, "Type", safe(String.valueOf(p.getVehicle().getType())), safe(r.getNewType()));
            addIfChanged(rows, "Licence plate", safe(p.getVehicle().getLicencePlate()), safe(r.getNewLicencePlate()));

            String curSeats = p.getVehicle().getSeatCount() == null ? "-" : String.valueOf(p.getVehicle().getSeatCount());
            String reqSeats = r.getNewSeatCount() == null ? "-" : String.valueOf(r.getNewSeatCount());
            addIfChanged(rows, "Seats", curSeats, reqSeats);

            addIfChanged(rows, "Baby friendly", yesNo(p.getVehicle().getBabyFriendly()), yesNo(r.getNewBabyFriendly()));
            addIfChanged(rows, "Pet friendly",  yesNo(p.getVehicle().getPetFriendly()),  yesNo(r.getNewPetFriendly()));
        } else {
            addIfChanged(rows, "Model", "-", safe(r.getNewModel()));
            addIfChanged(rows, "Type", "-", safe(r.getNewType()));
            addIfChanged(rows, "Licence plate", "-", safe(r.getNewLicencePlate()));
            addIfChanged(rows, "Seats", "-", (r.getNewSeatCount() == null ? "-" : String.valueOf(r.getNewSeatCount())));
            addIfChanged(rows, "Baby friendly", "-", yesNo(r.getNewBabyFriendly()));
            addIfChanged(rows, "Pet friendly", "-", yesNo(r.getNewPetFriendly()));
        }

        String email = (p.getEmail() == null || p.getEmail().trim().isEmpty()) ? base.getDriverEmail() : p.getEmail().trim();

        ArrayList<String> tags = buildTagsFromRows(rows);
        if (tags.isEmpty()) tags.add("No changes");

        return new DriverChangeRequest(
                base.getId(),
                email,
                base.getCreatedAt(),
                tags,
                rows
        );
    }

    private void addIfChanged(ArrayList<DriverChangeRequest.FieldChange> rows, String field, String current, String requested) {
        String curCmp = normCompare(current);
        String reqCmp = normCompare(requested);

        if (curCmp.equals(reqCmp)) return;

        rows.add(new DriverChangeRequest.FieldChange(field, showDash(current), showDash(requested)));
    }

    private String normCompare(String s) {
        if (s == null) return "";
        String t = s.trim();
        if (t.equals("-")) return "";
        return t.replaceAll("\\s+", " ");
    }

    private String showDash(String s) {
        String t = (s == null) ? "" : s.trim();
        if (t.isEmpty() || t.equals("-")) return "-";
        return t.replaceAll("\\s+", " ");
    }

    private boolean notBlank(String s) {
        return s != null && !s.trim().isEmpty();
    }

    private String safe(String s) {
        return (s == null || s.trim().isEmpty()) ? "-" : s.trim();
    }

    private String yesNo(Boolean b) {
        if (b == null) return "-";
        return b ? "Yes" : "No";
    }

    private String formatDate(String iso) {
        if (iso == null) return "-";
        try {
            LocalDateTime dt = LocalDateTime.parse(iso);
            DateTimeFormatter out = DateTimeFormatter.ofPattern("MMM d, yyyy, h:mm:ss a");
            return dt.format(out);
        } catch (Exception e) {
            return iso;
        }
    }
}
