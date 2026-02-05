package com.komsiluk.taxi;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;
import com.komsiluk.taxi.data.remote.passenger_ride_history.PassengerRideDetailsDTO;
import com.komsiluk.taxi.auth.AuthManager;
import com.komsiluk.taxi.data.remote.favorite.FavoriteRouteCreateRequest;
import com.komsiluk.taxi.data.remote.favorite.FavoriteService;
import com.komsiluk.taxi.data.remote.ride.RideCreateRequest;
import com.komsiluk.taxi.data.remote.route.RouteCreateRequest;
import com.komsiluk.taxi.data.remote.route.RouteResponse;
import com.komsiluk.taxi.data.remote.route.RouteService;
import com.komsiluk.taxi.data.session.SessionManager;
import com.komsiluk.taxi.ui.ride.FavoritesViewModel;
import com.komsiluk.taxi.ui.ride.OrderRideViewModel;
import com.komsiluk.taxi.ui.ride.map.GeoRepository;
import com.komsiluk.taxi.ui.ride.map.NominatimPlace;
import com.komsiluk.taxi.ui.ride.map.OsrmRouteResponse;
import com.komsiluk.taxi.ui.menu.BaseNavDrawerActivity;
import com.komsiluk.taxi.ui.ride.FavoriteRide;
import com.komsiluk.taxi.ui.ride.FavoritesActivity;

import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.BoundingBox;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.CustomZoomButtonsController;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Polyline;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@AndroidEntryPoint
public class UserActivity extends BaseNavDrawerActivity {

    @Override
    protected int getContentLayoutId() {
        return R.layout.activity_user;
    }

    @Inject
    SessionManager sessionManager;

    private BottomSheetBehavior<View> sheetBehavior;

    private ChipGroup chipStations, chipUsers;
    private MaterialButton btnAddStation, btnAddUser, btnBookRide;
    private AutoCompleteTextView actCarType, actTime;
    private RadioButton rbNow, rbScheduled;
    private ImageButton btnFavorite;
    private View headerRow;
    private View sheetHandle;

    private MapView map;
    private EditText etPickup, etDestination;
    private ImageButton btnSearchPickup, btnSearchDestination;

    private TextView tvKm, tvDriveTime;

    private GeoRepository geoRepo;

    private Marker pickupMarker;
    private Marker destMarker;
    private Polyline routeLine;

    private GeoPoint pickupPoint;
    private GeoPoint destPoint;

    private static final GeoPoint NOVI_SAD_CENTER = new GeoPoint(45.2671, 19.8335);

    private static final BoundingBox NS_BOX = new BoundingBox(
            45.35, 19.95,
            45.20, 19.75
    );

    private static final String NS_VIEWBOX = "19.75,45.35,19.95,45.20";

    private boolean pickupSelected = false;
    private boolean destSelected = false;

    private final List<GeoPoint> stationPoints = new ArrayList<>();
    private final List<Marker> stationMarkers = new ArrayList<>();
    private OrderRideViewModel orderVm;
    private double lastDistanceKm = 0.0;
    private int lastDurationMin = 0;

    private FavoritesViewModel favVm;

    @Inject
    FavoriteService favoriteApi;
    @Inject
    RouteService routeApi;

    private static class PlaceAdapter extends android.widget.BaseAdapter {
        private final android.content.Context ctx;
        private final List<NominatimPlace> items;

        PlaceAdapter(android.content.Context ctx, List<NominatimPlace> items) {
            this.ctx = ctx;
            this.items = items;
        }

        @Override public int getCount() { return items.size(); }
        @Override public Object getItem(int position) { return items.get(position); }
        @Override public long getItemId(int position) { return position; }

        @Override
        public View getView(int position, View convertView, android.view.ViewGroup parent) {
            View v = convertView;
            if (v == null) {
                v = android.view.LayoutInflater.from(ctx).inflate(R.layout.item_place_result, parent, false);
            }
            TextView tvMain = v.findViewById(R.id.tvMain);

            NominatimPlace p = items.get(position);
            String label = p.displayName == null ? "" : p.displayName;
            tvMain.setText(label);

            return v;
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);

        String rideJson = intent.getStringExtra("ORDER_AGAIN_DTO");
        if (rideJson != null) {
            processOrderAgain(rideJson);
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        favVm = new ViewModelProvider(this).get(FavoritesViewModel.class);

        favVm.getState().observe(this, st -> {
            if (st == null) return;
            if (st.error != null) Toast.makeText(this, st.error, Toast.LENGTH_LONG).show();
        });

        orderVm = new ViewModelProvider(this)
                .get(OrderRideViewModel.class);

        orderVm.getState().observe(this, st -> {
            if (st == null) return;

            if (st.loading) {
                Toast.makeText(this, "Booking...", Toast.LENGTH_SHORT).show();
                return;
            }

            if (st.error != null) {
                Toast.makeText(this, st.error, Toast.LENGTH_LONG).show();
                return;
            }

            if (st.success != null) {
                if(st.success.getStatus().equals("REJECTED")){
                    Toast.makeText(this, "Sorry, no drivers available at the moment or you already have ride at this time.", Toast.LENGTH_LONG).show();
                    return;
                }
                Toast.makeText(this, "Your ride has been successfully ordered!", Toast.LENGTH_LONG).show();
            }
        });

        View sheet = findViewById(R.id.bookRideSheet);
        sheetBehavior = BottomSheetBehavior.from(sheet);
        sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);

        headerRow = findViewById(R.id.headerRow);
        sheetHandle = findViewById(R.id.sheetHandle);

        setSheetDraggable(false);

        @SuppressLint("ClickableViewAccessibility")
        View.OnTouchListener headerDrag = (v, event) -> {
            switch (event.getActionMasked()) {
                case android.view.MotionEvent.ACTION_DOWN:
                case android.view.MotionEvent.ACTION_MOVE:
                    setSheetDraggable(true);
                    break;
                case android.view.MotionEvent.ACTION_UP:
                case android.view.MotionEvent.ACTION_CANCEL:
                    setSheetDraggable(false);
                    break;
            }
            return false;
        };

        headerRow.setOnTouchListener(headerDrag);
        sheetHandle.setOnTouchListener(headerDrag);

        chipStations  = findViewById(R.id.chipGroupStations);
        chipUsers     = findViewById(R.id.chipGroupUsers);

        btnAddStation = findViewById(R.id.btnAddStation);
        btnAddUser    = findViewById(R.id.btnAddUser);
        btnBookRide   = findViewById(R.id.btnBookRide);

        actCarType = findViewById(R.id.actCarType);
        actTime    = findViewById(R.id.actTime);

        rbNow = findViewById(R.id.rbNow);
        rbScheduled = findViewById(R.id.rbScheduled);

        btnFavorite = findViewById(R.id.btnFavorite);

        etPickup = findViewById(R.id.etPickup);
        etDestination = findViewById(R.id.etDestination);

        etPickup.addTextChangedListener(new android.text.TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override public void afterTextChanged(android.text.Editable s) {
                if (pickupSelected) {
                    pickupSelected = false;
                    pickupPoint = null;
                    removeMarker(true);
                    clearRouteAndStats();
                }
            }
        });

        etDestination.addTextChangedListener(new android.text.TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override public void afterTextChanged(android.text.Editable s) {
                if (destSelected) {
                    destSelected = false;
                    destPoint = null;
                    removeMarker(false);
                    clearRouteAndStats();
                }
            }
        });

        btnSearchPickup = findViewById(R.id.btnSearchPickup);
        btnSearchDestination = findViewById(R.id.btnSearchDestination);

        tvKm = findViewById(R.id.tvKm);
        tvDriveTime = findViewById(R.id.tvDriveTime);

        setupCarTypeDropdown();
        setupTimeDropdown();
        setupTimeToggle();
        setupAddButtons();
        setupFavoriteButton();
        setupBookButton();

        Configuration.getInstance().load(this, getSharedPreferences("osmdroid", MODE_PRIVATE));

        map = findViewById(R.id.map);

        map.setTileSource(TileSourceFactory.MAPNIK);
        map.getZoomController().setVisibility(CustomZoomButtonsController.Visibility.NEVER);
        map.setMultiTouchControls(true);

        map.setScrollableAreaLimitDouble(NS_BOX);
        map.setMinZoomLevel(12.0);
        map.setMaxZoomLevel(20.0);

        map.getController().setZoom(13.5);
        map.getController().setCenter(NOVI_SAD_CENTER);

        okhttp3.logging.HttpLoggingInterceptor log = new okhttp3.logging.HttpLoggingInterceptor();
        log.setLevel(okhttp3.logging.HttpLoggingInterceptor.Level.BODY);

        OkHttpClient geoClient = new OkHttpClient.Builder()
                .addInterceptor(chain -> chain.proceed(
                        chain.request().newBuilder()
                                .header("User-Agent", "KomsilukTaxiAndroid/1.0 (contact: komsiluktim@gmail.com)")
                                .header("Accept", "application/json")
                                .build()
                ))
                .addInterceptor(log)
                .connectTimeout(10, java.util.concurrent.TimeUnit.SECONDS)
                .readTimeout(10, java.util.concurrent.TimeUnit.SECONDS)
                .writeTimeout(10, java.util.concurrent.TimeUnit.SECONDS)
                .build();

        geoRepo = new GeoRepository(geoClient);

        Object extra = getIntent().getSerializableExtra(FavoritesActivity.EXTRA_BOOK_FAVORITE);
        if (extra instanceof FavoriteRide) {
            FavoriteRide ride = (FavoriteRide) extra;
            sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            applyFavoriteToBookRide(ride);
        }


        btnSearchPickup.setOnClickListener(v -> searchAndPickLocation(true));
        btnSearchDestination.setOnClickListener(v -> searchAndPickLocation(false));

        String rideJson = getIntent().getStringExtra("ORDER_AGAIN_DTO");
        if (rideJson != null) {
            processOrderAgain(rideJson);
        }

    }

    private void processOrderAgain(String json) {
        Gson gson = new Gson();
        PassengerRideDetailsDTO details = gson.fromJson(json, PassengerRideDetailsDTO.class);

        if (sheetBehavior != null) {
            sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        }

        applyRideDetailsToForm(details);
    }


    private void applyRideDetailsToForm(PassengerRideDetailsDTO details) {
        if (details == null || details.getRoute() == null) return;

        String fullPickup = details.getRoute().getStartAddress();
        String fullDest = details.getRoute().getEndAddress();

        if (etPickup != null) etPickup.setText(extractStreetAddress(fullPickup));
        if (etDestination != null) etDestination.setText(extractStreetAddress(fullDest));

        clearStations();
        List<String> stopsList = new ArrayList<>();
        String stopsData = details.getRoute().getStops();
        if (stopsData != null && !stopsData.isEmpty()) {
            String[] stopsArray = stopsData.split("\\|");
            for (String s : stopsArray) {
                String trimmed = s.trim();
                stopsList.add(trimmed);
                addStationChip(extractStreetAddress(trimmed));
            }
        }
        geocodeAndDrawRoute(fullPickup, fullDest, stopsList);
    }


    private String extractStreetAddress(String fullAddress) {
        if (fullAddress == null || fullAddress.trim().isEmpty()) return "";
        return fullAddress.split(",")[0].trim();
    }

    private void removeMarker(boolean isPickup) {
        Marker m = isPickup ? pickupMarker : destMarker;
        if (m != null) {
            map.getOverlays().remove(m);
            if (isPickup) pickupMarker = null; else destMarker = null;
            map.invalidate();
        }
    }

    private void clearRouteAndStats() {
        if (routeLine != null) {
            map.getOverlays().remove(routeLine);
            routeLine = null;
            map.invalidate();
        }
        tvKm.setText(getString(R.string.placeholder_kilometers_empty));
        tvDriveTime.setText(getString(R.string.placeholder_drive_time_empty));
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (map != null) map.onResume();
    }

    @Override
    protected void onPause() {
        if (map != null) map.onPause();
        super.onPause();
    }

    private void searchAndPickLocation(boolean isPickup) {
        String q = isPickup
                ? safeTrim(etPickup.getText())
                : safeTrim(etDestination.getText());

        if (q.isEmpty()) {
            Toast.makeText(this, getString(R.string.error_location_required), Toast.LENGTH_SHORT).show();
            return;
        }

        geoRepo.searchNoviSad(q, NS_VIEWBOX).enqueue(new Callback<List<NominatimPlace>>() {
            @Override
            public void onResponse(Call<List<NominatimPlace>> call, Response<List<NominatimPlace>> response) {
                android.util.Log.d("NOMINATIM", "code=" + response.code());

                if (!response.isSuccessful()) {
                    String err = "";
                    try { if (response.errorBody() != null) err = response.errorBody().string(); } catch (Exception ignored) {}
                    android.util.Log.d("NOMINATIM", "errorBody=" + err);
                    Toast.makeText(UserActivity.this, "Search failed (" + response.code() + ")", Toast.LENGTH_SHORT).show();
                    return;
                }

                List<NominatimPlace> raw = response.body();
                if (raw == null) {
                    Toast.makeText(UserActivity.this, "Search failed (empty body)", Toast.LENGTH_SHORT).show();
                    return;
                }

                List<NominatimPlace> filtered = filterDistinct(raw);

                if (filtered.isEmpty()) {
                    Toast.makeText(UserActivity.this, getString(R.string.error_no_results), Toast.LENGTH_SHORT).show();
                    return;
                }

                showPickDialog(filtered, isPickup);
            }

            @Override
            public void onFailure(Call<List<NominatimPlace>> call, Throwable t) {
                Toast.makeText(UserActivity.this, getString(R.string.error_geocode_failed), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showPickDialog(List<NominatimPlace> items, boolean isPickup) {
        View view = getLayoutInflater().inflate(R.layout.dialog_place_picker, null);

        TextView tvTitle = view.findViewById(R.id.tvTitle);
        tvTitle.setText(isPickup ? getString(R.string.pick_pickup_title) : getString(R.string.pick_destination_title));

        android.widget.ListView list = view.findViewById(R.id.list);
        PlaceAdapter adapter = new PlaceAdapter(this, items);
        list.setAdapter(adapter);

        androidx.appcompat.app.AlertDialog dialog =
                new androidx.appcompat.app.AlertDialog.Builder(this)
                        .setView(view)
                        .setCancelable(true)
                        .create();

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }

        list.setOnItemClickListener((parent, v, which, id) -> {
            NominatimPlace p = items.get(which);
            GeoPoint pt = new GeoPoint(parseDouble(p.lat), parseDouble(p.lon));

            String pretty = shortenDisplay(p.displayName);

            if (isPickup) {
                pickupPoint = pt;
                etPickup.setText(pretty);
                pickupSelected = true;
                setMarker(true, pt);
            } else {
                destPoint = pt;
                etDestination.setText(pretty);
                destSelected = true;
                setMarker(false, pt);
            }

            zoomToPoints();

            if (pickupPoint != null && destPoint != null) {
                drawRouteAndStatsMulti();
            }

            dialog.dismiss();
        });

        dialog.show();
    }

    private void setMarker(boolean isPickup, GeoPoint pt) {
        Marker m = isPickup ? pickupMarker : destMarker;
        if (m != null) map.getOverlays().remove(m);

        Marker marker = new Marker(map);
        marker.setPosition(pt);
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);

        marker.setIcon(androidx.core.content.ContextCompat.getDrawable(
                this,
                isPickup ? R.drawable.car : R.drawable.flag
        ));

        map.getOverlays().add(marker);

        if (isPickup) pickupMarker = marker;
        else destMarker = marker;

        map.invalidate();
    }

    private void drawPolyline(List<List<Double>> coords) {
        if (coords == null || coords.isEmpty()) return;

        if (routeLine != null) map.getOverlays().remove(routeLine);

        List<GeoPoint> pts = new ArrayList<>();
        for (List<Double> c : coords) {
            if (c.size() < 2) continue;
            double lon = c.get(0);
            double lat = c.get(1);
            pts.add(new GeoPoint(lat, lon));
        }

        Polyline line = new Polyline();
        line.setPoints(pts);
        line.getOutlinePaint().setStrokeWidth(10f);

        map.getOverlays().add(line);
        routeLine = line;

        map.invalidate();
    }

    private void zoomToPoints() {
        if (pickupPoint == null && destPoint == null) return;

        if (pickupPoint != null && destPoint == null) {
            map.getController().animateTo(pickupPoint);
            map.getController().setZoom(16.0);
            return;
        }

        if (pickupPoint == null) {
            map.getController().animateTo(destPoint);
            map.getController().setZoom(16.0);
            return;
        }

        double north = Math.max(pickupPoint.getLatitude(), destPoint.getLatitude());
        double south = Math.min(pickupPoint.getLatitude(), destPoint.getLatitude());
        double east  = Math.max(pickupPoint.getLongitude(), destPoint.getLongitude());
        double west  = Math.min(pickupPoint.getLongitude(), destPoint.getLongitude());

        BoundingBox bb = new BoundingBox(north, east, south, west);
        map.zoomToBoundingBox(bb, true, 140);
    }

    private List<NominatimPlace> filterDistinct(List<NominatimPlace> raw) {
        Map<String, NominatimPlace> m = new LinkedHashMap<>();
        for (NominatimPlace p : raw) {
            String k = shortenDisplay(p.displayName).toLowerCase(Locale.ROOT);
            if (!m.containsKey(k)) m.put(k, p);
        }
        return new ArrayList<>(m.values());
    }

    private String shortenDisplay(String display) {
        if (display == null) return "";
        String s = display;

        s = s.replace(", Serbia", "")
                .replace(", Република Србија", "")
                .replace(", Republika Srbija", "")
                .replace(", South Bačka Administrative District", "")
                .replace(", Južnobački okrug", "")
                .replace(", Град Нови Сад", "")
                .replace(", Novi Sad City", "")
                .trim();

        String[] parts = s.split(",");
        if (parts.length <= 3) return s.trim();

        return (parts[0].trim() + ", " + parts[1].trim() + ", " + parts[2].trim()).trim();
    }

    private String safeTrim(CharSequence cs) {
        return cs == null ? "" : cs.toString().trim();
    }

    private double parseDouble(String s) {
        try { return Double.parseDouble(s); } catch (Exception e) { return 0.0; }
    }

    private String formatDuration(long seconds) {
        long min = seconds / 60;
        long h = min / 60;
        long m = min % 60;

        if (h <= 0) return String.format(Locale.getDefault(), "%d min", m);
        return String.format(Locale.getDefault(), "%dh %02dmin", h, m);
    }

    private void applyFavoriteToBookRide(FavoriteRide r) {
        if (etPickup != null) etPickup.setText(r.getPickup());
        if (etDestination != null) etDestination.setText(r.getDestination());

        clearStations();
        for (String s : r.getStations()) addStationChip(s);

        clearUsers();
        for (String u : r.getUsers()) addUserChip(u);

        AutoCompleteTextView etCarType = findViewById(R.id.actCarType);
        if (etCarType != null) etCarType.setText(r.getCarType(), false);

        android.widget.CheckBox cbPet = findViewById(R.id.cbPetFriendly);
        android.widget.CheckBox cbChild = findViewById(R.id.cbChildSeat);

        if (cbPet != null) cbPet.setChecked(r.isPetFriendly());
        if (cbChild != null) cbChild.setChecked(r.isChildSeat());

//        geocodeAndDrawFavorite(r);
        geocodeAndDrawRoute(r.getPickup(), r.getDestination(), r.getStations());
    }

//    private void geocodeAndDrawFavorite(FavoriteRide fav) {
//        pickupSelected = false;
//        destSelected = false;
//        pickupPoint = null;
//        destPoint = null;
//        stationPoints.clear();
//        stationMarkers.clear();
//        clearRouteAndStats();
//        removeMarker(true);
//        removeMarker(false);
//
//        String pickupAddr = fav.getPickup();
//        String destAddr = fav.getDestination();
//        List<String> stopsAddr = fav.getStations() != null ? fav.getStations() : new ArrayList<>();
//
//        geoRepo.searchNoviSad(pickupAddr, NS_VIEWBOX).enqueue(new Callback<List<NominatimPlace>>() {
//            @Override public void onResponse(Call<List<NominatimPlace>> call, Response<List<NominatimPlace>> res) {
//                if (!res.isSuccessful() || res.body() == null || res.body().isEmpty()) {
//                    Toast.makeText(UserActivity.this, "Could not locate pickup.", Toast.LENGTH_LONG).show();
//                    return;
//                }
//                NominatimPlace p = res.body().get(0);
//                pickupPoint = new GeoPoint(parseDouble(p.lat), parseDouble(p.lon));
//                pickupSelected = true;
//                setMarker(true, pickupPoint);
//
//                geocodeStopsSequential(stopsAddr, 0, () -> {
//                    geoRepo.searchNoviSad(destAddr, NS_VIEWBOX).enqueue(new Callback<List<NominatimPlace>>() {
//                        @Override public void onResponse(Call<List<NominatimPlace>> call2, Response<List<NominatimPlace>> res2) {
//                            if (!res2.isSuccessful() || res2.body() == null || res2.body().isEmpty()) {
//                                Toast.makeText(UserActivity.this, "Could not locate destination.", Toast.LENGTH_LONG).show();
//                                return;
//                            }
//                            NominatimPlace d = res2.body().get(0);
//                            destPoint = new GeoPoint(parseDouble(d.lat), parseDouble(d.lon));
//                            destSelected = true;
//                            setMarker(false, destPoint);
//
//                            zoomToAllPoints();
//                            drawRouteAndStatsMulti();
//                        }
//
//                        @Override public void onFailure(Call<List<NominatimPlace>> call2, Throwable t) {
//                            Toast.makeText(UserActivity.this, "Destination geocode failed.", Toast.LENGTH_LONG).show();
//                        }
//                    });
//                });
//            }
//
//            @Override public void onFailure(Call<List<NominatimPlace>> call, Throwable t) {
//                Toast.makeText(UserActivity.this, "Pickup geocode failed.", Toast.LENGTH_LONG).show();
//            }
//        });
//    }

    private void geocodeAndDrawRoute(String pickupAddr, String destAddr, List<String> stopsAddr) {
        // Resetovanje stanja mape (tvoj postojeći kod)
        pickupSelected = false;
        destSelected = false;
        pickupPoint = null;
        destPoint = null;
        stationPoints.clear();
        stationMarkers.clear();
        clearRouteAndStats();
        removeMarker(true);
        removeMarker(false);

        if (pickupAddr == null || destAddr == null) return;

        // Geokodiranje polazišta
        geoRepo.searchNoviSad(pickupAddr, NS_VIEWBOX).enqueue(new Callback<List<NominatimPlace>>() {
            @Override public void onResponse(Call<List<NominatimPlace>> call, Response<List<NominatimPlace>> res) {
                if (!res.isSuccessful() || res.body() == null || res.body().isEmpty()) {
                    Toast.makeText(UserActivity.this, "Could not locate pickup: " + pickupAddr, Toast.LENGTH_LONG).show();
                    return;
                }
                NominatimPlace p = res.body().get(0);
                pickupPoint = new GeoPoint(Double.parseDouble(p.lat), Double.parseDouble(p.lon));
                pickupSelected = true;
                setMarker(true, pickupPoint);

                // Sekvencijalno geokodiranje stanica
                geocodeStopsSequential(stopsAddr != null ? stopsAddr : new ArrayList<>(), 0, () -> {
                    // Geokodiranje destinacije
                    geoRepo.searchNoviSad(destAddr, NS_VIEWBOX).enqueue(new Callback<List<NominatimPlace>>() {
                        @Override public void onResponse(Call<List<NominatimPlace>> call2, Response<List<NominatimPlace>> res2) {
                            if (!res2.isSuccessful() || res2.body() == null || res2.body().isEmpty()) {
                                Toast.makeText(UserActivity.this, "Could not locate destination: " + destAddr, Toast.LENGTH_LONG).show();
                                return;
                            }
                            NominatimPlace d = res2.body().get(0);
                            destPoint = new GeoPoint(Double.parseDouble(d.lat), Double.parseDouble(d.lon));
                            destSelected = true;
                            setMarker(false, destPoint);

                            // Finalno iscrtavanje
                            zoomToAllPoints();
                            drawRouteAndStatsMulti();
                        }

                        @Override public void onFailure(Call<List<NominatimPlace>> call2, Throwable t) {
                            Toast.makeText(UserActivity.this, "Destination geocode failed.", Toast.LENGTH_LONG).show();
                        }
                    });
                });
            }

            @Override public void onFailure(Call<List<NominatimPlace>> call, Throwable t) {
                Toast.makeText(UserActivity.this, "Pickup geocode failed.", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void geocodeStopsSequential(List<String> stops, int idx, Runnable done) {
        if (stops == null || idx >= stops.size()) {
            if (done != null) done.run();
            return;
        }

        String addr = stops.get(idx);
        geoRepo.searchNoviSad(addr, NS_VIEWBOX).enqueue(new Callback<List<NominatimPlace>>() {
            @Override public void onResponse(Call<List<NominatimPlace>> call, Response<List<NominatimPlace>> res) {
                if (res.isSuccessful() && res.body() != null && !res.body().isEmpty()) {
                    NominatimPlace p = res.body().get(0);
                    GeoPoint pt = new GeoPoint(parseDouble(p.lat), parseDouble(p.lon));
                    stationPoints.add(pt);

                    // marker za stanicu
                    Marker m = new Marker(map);
                    m.setPosition(pt);
                    m.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
                    try { m.setIcon(getDrawable(R.drawable.station)); } catch (Throwable ignored) {}
                    map.getOverlays().add(m);
                    stationMarkers.add(m);
                    map.invalidate();
                }
                geocodeStopsSequential(stops, idx + 1, done);
            }

            @Override public void onFailure(Call<List<NominatimPlace>> call, Throwable t) {
                geocodeStopsSequential(stops, idx + 1, done);
            }
        });
    }


    private void clearStations() {
        chipStations.removeAllViews();
    }

    private void clearUsers() {
        chipUsers.removeAllViews();
    }

    private void setSheetDraggable(boolean draggable) {
        try { sheetBehavior.setDraggable(draggable); } catch (Throwable ignored) {}
    }

    private void setupCarTypeDropdown() {
        String[] carTypes = new String[] {
                getString(R.string.car_type_standard),
                getString(R.string.car_type_luxury),
                getString(R.string.car_type_van)
        };

        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, carTypes);

        actCarType.setAdapter(adapter);
        actCarType.setText(getString(R.string.car_type_luxury), false);
        actCarType.setOnClickListener(v -> actCarType.showDropDown());
    }

    private void setupTimeDropdown() {
        List<String> slots = buildTimeSlots(15, 5);

        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, slots);

        actTime.setAdapter(adapter);
        actTime.setOnClickListener(v -> actTime.showDropDown());
    }

    private void setupTimeToggle() {
        rbNow.setOnCheckedChangeListener((btn, checked) -> {
            if (checked) actTime.setVisibility(View.GONE);
        });

        rbScheduled.setOnCheckedChangeListener((btn, checked) -> {
            if (checked) {
                actTime.setVisibility(View.VISIBLE);
                if (TextUtils.isEmpty(actTime.getText())) {
                    actTime.post(() -> actTime.showDropDown());
                }
            }
        });
    }

    private void setupAddButtons() {
        btnAddStation.setOnClickListener(v -> showStationSearchDialog());

        btnAddUser.setOnClickListener(v -> showAddDialogStyled(
                getString(R.string.dialog_add_user_title),
                getString(R.string.dialog_add_user_hint),
                this::addUserChip
        ));
    }

    private void showStationSearchDialog() {
        View view = getLayoutInflater().inflate(R.layout.dialog_station_search, null);

        TextView tvTitle = view.findViewById(R.id.tvTitle);
        EditText etQuery = view.findViewById(R.id.etQuery);
        ImageButton btnSearch = view.findViewById(R.id.btnSearch);
        android.widget.ListView list = view.findViewById(R.id.list);

        tvTitle.setText(getString(R.string.add_station_title));

        final List<NominatimPlace> places = new ArrayList<>();
        final PlaceAdapter placeAdapter = new PlaceAdapter(this, places);
        list.setAdapter(placeAdapter);

        androidx.appcompat.app.AlertDialog dialog =
                new androidx.appcompat.app.AlertDialog.Builder(this)
                        .setView(view)
                        .setCancelable(true)
                        .create();

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            dialog.getWindow().addFlags(android.view.WindowManager.LayoutParams.FLAG_DIM_BEHIND);
            android.view.WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
            lp.dimAmount = 0.65f;
            dialog.getWindow().setAttributes(lp);
        }

        btnSearch.setOnClickListener(v -> {
            String q = safeTrim(etQuery.getText());
            if (q.isEmpty()) {
                Toast.makeText(this, getString(R.string.error_location_required), Toast.LENGTH_SHORT).show();
                return;
            }

            geoRepo.searchNoviSad(q, NS_VIEWBOX).enqueue(new Callback<List<NominatimPlace>>() {
                @Override
                public void onResponse(Call<List<NominatimPlace>> call, Response<List<NominatimPlace>> response) {
                    if (!response.isSuccessful() || response.body() == null) {
                        Toast.makeText(UserActivity.this, getString(R.string.error_geocode_failed), Toast.LENGTH_SHORT).show();
                        return;
                    }

                    List<NominatimPlace> filtered = filterDistinct(response.body());
                    if (filtered.isEmpty()) {
                        Toast.makeText(UserActivity.this, getString(R.string.error_no_results), Toast.LENGTH_SHORT).show();
                        return;
                    }

                    places.clear();
                    places.addAll(filtered);
                    placeAdapter.notifyDataSetChanged();
                }

                @Override
                public void onFailure(Call<List<NominatimPlace>> call, Throwable t) {
                    Toast.makeText(UserActivity.this, getString(R.string.error_geocode_failed), Toast.LENGTH_SHORT).show();
                }
            });
        });

        list.setOnItemClickListener((parent, itemView, pos, id) -> {
            NominatimPlace picked = places.get(pos);

            GeoPoint pt = new GeoPoint(parseDouble(picked.lat), parseDouble(picked.lon));
            String pretty = shortenDisplay(picked.displayName);

            addStation(pt, pretty);
            dialog.dismiss();
        });

        dialog.show();
    }

    private void addStation(GeoPoint pt, String label) {
        stationPoints.add(pt);

        addStationChip(label);

        Marker m = new Marker(map);
        m.setPosition(pt);
        m.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);

        try {
            org.osmdroid.views.overlay.infowindow.MarkerInfoWindow.closeAllInfoWindowsOn(map);
        } catch (Throwable ignored) {}

        try {
            m.setIcon(getDrawable(R.drawable.station));
        } catch (Throwable ignored) {
        }

        map.getOverlays().add(m);
        stationMarkers.add(m);

        map.invalidate();

        zoomToAllPoints();

        if (pickupPoint != null && destPoint != null) {
            drawRouteAndStatsMulti();
        }
    }

    private void drawRouteAndStatsMulti() {
        if (pickupPoint == null || destPoint == null) return;

        List<GeoPoint> pts = new ArrayList<>();
        pts.add(pickupPoint);
        pts.addAll(stationPoints);
        pts.add(destPoint);

        geoRepo.routeMulti(pts).enqueue(new Callback<OsrmRouteResponse>() {
            @Override
            public void onResponse(Call<OsrmRouteResponse> call, Response<OsrmRouteResponse> response) {
                if (!response.isSuccessful() || response.body() == null || response.body().routes == null || response.body().routes.isEmpty()) {
                    Toast.makeText(UserActivity.this, getString(R.string.error_route_failed), Toast.LENGTH_SHORT).show();
                    return;
                }

                OsrmRouteResponse.Route r = response.body().routes.get(0);

                if (r.geometry != null) {
                    drawPolyline(r.geometry.coordinates);
                }

                double km = r.distance / 1000.0;
                tvKm.setText(String.format(Locale.getDefault(), "%.1f km", km));
                tvDriveTime.setText(formatDuration((long) r.duration));

                lastDistanceKm= km;
                lastDurationMin = (int) Math.round(r.duration / 60.0);
            }

            @Override
            public void onFailure(Call<OsrmRouteResponse> call, Throwable t) {
                Toast.makeText(UserActivity.this, getString(R.string.error_route_failed), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void zoomToAllPoints() {
        List<GeoPoint> pts = new ArrayList<>();
        if (pickupPoint != null) pts.add(pickupPoint);
        pts.addAll(stationPoints);
        if (destPoint != null) pts.add(destPoint);

        if (pts.isEmpty()) return;

        if (pts.size() == 1) {
            map.getController().animateTo(pts.get(0));
            map.getController().setZoom(16.0);
            return;
        }

        double north = -90, south = 90, east = -180, west = 180;
        for (GeoPoint p : pts) {
            north = Math.max(north, p.getLatitude());
            south = Math.min(south, p.getLatitude());
            east  = Math.max(east, p.getLongitude());
            west  = Math.min(west, p.getLongitude());
        }

        BoundingBox bb = new BoundingBox(north, east, south, west);
        map.zoomToBoundingBox(bb, true, 160);
    }

    private void setupFavoriteButton() {
        ImageButton btnFavorite = findViewById(R.id.btnFavorite);
        if (btnFavorite == null) return;

        btnFavorite.setOnClickListener(v -> showAddToFavoritesDialog());
    }

    private void showAddToFavoritesDialog() {
        View view = getLayoutInflater().inflate(R.layout.dialog_add_to_favorites, null);

        MaterialButton btnCancel = view.findViewById(R.id.btnFavCancel);
        MaterialButton btnConfirm = view.findViewById(R.id.btnFavConfirm);

        AlertDialog dialog =
                new AlertDialog.Builder(this)
                        .setView(view)
                        .setCancelable(true)
                        .create();

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }

        btnCancel.setOnClickListener(v -> dialog.dismiss());
        btnConfirm.setOnClickListener(x -> {
            android.widget.EditText etName = view.findViewById(R.id.etFavName);
            String title = etName != null && etName.getText() != null ? etName.getText().toString().trim() : "";

            if (title.length() < 2) {
                Toast.makeText(this, "Name must be at least 2 characters.", Toast.LENGTH_LONG).show();
                return;
            }

            Long userId = sessionManager != null ? sessionManager.getUserId() : null;
            if (userId == null) {
                Toast.makeText(this, "Not logged in.", Toast.LENGTH_LONG).show();
                return;
            }

            if (!pickupSelected || pickupPoint == null || !destSelected || destPoint == null) {
                Toast.makeText(this, "Select pickup and destination first.", Toast.LENGTH_LONG).show();
                return;
            }

            if (lastDistanceKm <= 0.0 || lastDurationMin <= 0) {
                Toast.makeText(this, "Route is not ready yet.", Toast.LENGTH_LONG).show();
                return;
            }

            RouteCreateRequest rreq = new RouteCreateRequest();
            rreq.setStartAddress(safeTrim(etPickup.getText()));
            rreq.setEndAddress(safeTrim(etDestination.getText()));
            rreq.setStops(buildStopsStringOrNull());
            rreq.setDistanceKm(lastDistanceKm);
            rreq.setEstimatedDurationMin(lastDurationMin);

            routeApi.findOrCreate(rreq).enqueue(new retrofit2.Callback<RouteResponse>() {
                @Override
                public void onResponse(retrofit2.Call<RouteResponse> call, retrofit2.Response<RouteResponse> resp) {
                    if (!resp.isSuccessful() || resp.body() == null || resp.body().getId() == null) {
                        Toast.makeText(UserActivity.this, "Route create failed (" + resp.code() + ")", Toast.LENGTH_LONG).show();
                        return;
                    }

                    Long routeId = resp.body().getId();

                    FavoriteRouteCreateRequest freq = new FavoriteRouteCreateRequest();
                    freq.setTitle(title);
                    freq.setRouteId(routeId);
                    freq.setVehicleType(mapVehicleType(actCarType.getText() == null ? "" : actCarType.getText().toString()));
                    freq.setPetFriendly(((CheckBox) findViewById(R.id.cbPetFriendly)).isChecked());
                    freq.setBabyFriendly(((CheckBox) findViewById(R.id.cbChildSeat)).isChecked());
                    freq.setPassengersEmails(readPassengerEmails()); // može null

                    favoriteApi.addFavorite(userId, freq).enqueue(new retrofit2.Callback<com.komsiluk.taxi.data.remote.favorite.FavoriteRouteResponse>() {
                        @Override
                        public void onResponse(retrofit2.Call<com.komsiluk.taxi.data.remote.favorite.FavoriteRouteResponse> call,
                                               retrofit2.Response<com.komsiluk.taxi.data.remote.favorite.FavoriteRouteResponse> resp2) {
                            if (!resp2.isSuccessful()) {
                                Toast.makeText(UserActivity.this, "Add favorite failed (" + resp2.code() + ")", Toast.LENGTH_LONG).show();
                                return;
                            }
                            Toast.makeText(UserActivity.this, "Added to favorites!", Toast.LENGTH_LONG).show();
                            dialog.dismiss();
                        }

                        @Override
                        public void onFailure(retrofit2.Call<com.komsiluk.taxi.data.remote.favorite.FavoriteRouteResponse> call, Throwable t) {
                            Toast.makeText(UserActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
                }

                @Override
                public void onFailure(retrofit2.Call<RouteResponse> call, Throwable t) {
                    Toast.makeText(UserActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        });

        dialog.show();
    }

    private String buildStopsStringOrNull() {
        ArrayList<String> stops = new ArrayList<>();
        for (int i = 0; i < chipStations.getChildCount(); i++) {
            View c = chipStations.getChildAt(i);
            if (c instanceof Chip) {
                CharSequence t = ((Chip) c).getText();
                if (t != null) {
                    String s = t.toString().trim();
                    if (!s.isEmpty()) stops.add(s);
                }
            }
        }
        if (stops.isEmpty()) return null;

        return TextUtils.join("|", stops);
    }


    private void setupBookButton() {
        btnBookRide.setOnClickListener(v -> {
            if (!pickupSelected || pickupPoint == null) {
                Toast.makeText(this, getString(R.string.error_pickup_not_selected), Toast.LENGTH_SHORT).show();
                return;
            }
            if (!destSelected || destPoint == null) {
                Toast.makeText(this, getString(R.string.error_destination_not_selected), Toast.LENGTH_SHORT).show();
                return;
            }
            showConfirmBookingDialog();
        });
    }

    private void showConfirmBookingDialog() {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_confirm_booking, null);

        TextView tvConfirmKm = dialogView.findViewById(R.id.tvConfirmKm);
        TextView tvConfirmTime = dialogView.findViewById(R.id.tvConfirmTime);

        tvConfirmKm.setText(String.format(Locale.getDefault(), "%.1f km", lastDistanceKm));
        tvConfirmTime.setText(String.format(Locale.getDefault(), "%d min", lastDurationMin));

        MaterialButton btnCancel = dialogView.findViewById(R.id.btnConfirmCancel);
        MaterialButton btnOk     = dialogView.findViewById(R.id.btnConfirmOk);

        androidx.appcompat.app.AlertDialog dialog =
                new androidx.appcompat.app.AlertDialog.Builder(this)
                        .setView(dialogView)
                        .setCancelable(true)
                        .create();

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            dialog.getWindow().addFlags(android.view.WindowManager.LayoutParams.FLAG_DIM_BEHIND);
            android.view.WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
            lp.dimAmount = 0.55f;
            dialog.getWindow().setAttributes(lp);
        }

        btnCancel.setOnClickListener(v -> dialog.dismiss());
        btnOk.setOnClickListener(v -> {
            if (!pickupSelected || pickupPoint == null) {
                Toast.makeText(this, getString(R.string.error_pickup_not_selected), Toast.LENGTH_SHORT).show();
                return;
            }
            if (!destSelected || destPoint == null) {
                Toast.makeText(this, getString(R.string.error_destination_not_selected), Toast.LENGTH_SHORT).show();
                return;
            }
            if (lastDistanceKm <= 0.0 || lastDurationMin <= 0) {
                Toast.makeText(this, getString(R.string.error_route_failed), Toast.LENGTH_SHORT).show();
                return;
            }

            RideCreateRequest req = buildRideRequest();
            orderVm.orderRide(req);
            dialog.dismiss();
        });

        dialog.show();
    }

    private RideCreateRequest buildRideRequest() {
        RideCreateRequest r = new RideCreateRequest();

        r.setCreatorId(getCreatorId());
        r.setStartAddress(safeTrim(etPickup.getText()));
        r.setEndAddress(safeTrim(etDestination.getText()));

        List<String> stops = new ArrayList<>();
        for (int i = 0; i < chipStations.getChildCount(); i++) {
            View c = chipStations.getChildAt(i);
            if (c instanceof Chip) {
                CharSequence t = ((Chip) c).getText();
                if (t != null && !t.toString().trim().isEmpty()) stops.add(t.toString().trim());
            }
        }
        r.setStops(stops.isEmpty() ? null : stops);

        r.setDistanceKm(lastDistanceKm);
        r.setEstimatedDurationMin(lastDurationMin);

        r.setStartLat(pickupPoint.getLatitude());
        r.setStartLng(pickupPoint.getLongitude());

        r.setVehicleType(mapVehicleType(actCarType.getText() == null ? "" : actCarType.getText().toString()));

        CheckBox cbPet = findViewById(R.id.cbPetFriendly);
        CheckBox cbChild = findViewById(R.id.cbChildSeat);
        r.setPetFriendly(cbPet != null && cbPet.isChecked());
        r.setBabyFriendly(cbChild != null && cbChild.isChecked());

        r.setScheduledAt(buildScheduledAtOrNull());

        r.setPassengerEmails(readPassengerEmails());

        return r;
    }

    private Long getCreatorId() {
        return sessionManager.getUserId();
    }

    private String mapVehicleType(String label) {
        String s = label.trim().toLowerCase(Locale.ROOT);
        if (s.contains("standard")) return "STANDARD";
        if (s.contains("luxury")) return "LUXURY";
        if (s.contains("van")) return "VAN";
        return "STANDARD";
    }

    private String buildScheduledAtOrNull() {
        if (rbNow != null && rbNow.isChecked()) return null;

        String t = actTime.getText() == null ? "" : actTime.getText().toString().trim();
        if (t.isEmpty()) return null;

        try {
            LocalDate today = LocalDate.now();
            LocalTime lt = LocalTime.parse(t);
            LocalDateTime dt = LocalDateTime.of(today, lt);

            if (dt.isBefore(LocalDateTime.now().plusMinutes(1))) {
                dt = dt.plusDays(1);
            }
            return dt.toString();
        } catch (Exception e) {
            return null;
        }
    }

    private List<String> readPassengerEmails() {
        List<String> emails = new ArrayList<>();
        for (int i = 0; i < chipUsers.getChildCount(); i++) {
            View c = chipUsers.getChildAt(i);
            if (c instanceof Chip) {
                CharSequence t = ((Chip) c).getText();
                if (t != null) {
                    String s = t.toString().trim();
                    if (!s.isEmpty()) emails.add(s);
                }
            }
        }
        return emails.isEmpty() ? null : emails;
    }

    private void showAddDialogStyled(String title, String hint, OnValueAdded callback) {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_add_item, null);

        android.widget.TextView tvTitle = dialogView.findViewById(R.id.tvDialogTitle);
        android.widget.TextView tvSubtitle = dialogView.findViewById(R.id.tvDialogSubtitle);
        android.widget.EditText et = dialogView.findViewById(R.id.etDialogValue);

        MaterialButton btnCancel = dialogView.findViewById(R.id.btnDialogCancel);
        MaterialButton btnConfirm = dialogView.findViewById(R.id.btnDialogConfirm);

        tvTitle.setText(title);
        tvSubtitle.setText(hint);
        et.setHint(hint);

        androidx.appcompat.app.AlertDialog dialog =
                new androidx.appcompat.app.AlertDialog.Builder(this)
                        .setView(dialogView)
                        .create();

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }

        btnCancel.setOnClickListener(v -> dialog.dismiss());

        btnConfirm.setOnClickListener(v -> {
            String value = et.getText() == null ? "" : et.getText().toString().trim();
            if (!value.isEmpty()) callback.onAdded(value);
            dialog.dismiss();
        });

        dialog.show();
    }

    private void addStationChip(String text) {
        Chip chip = new Chip(this);
        chip.setText(text);
        chip.setCloseIconVisible(true);

        chip.setChipBackgroundColorResource(R.color.white);
        chip.setTextColor(getResources().getColor(R.color.text, getTheme()));
        chip.setCloseIconTintResource(R.color.text);

        int stationIndex = stationPoints.size() - 1;
        chip.setTag(stationIndex);

        chip.setOnCloseIconClickListener(v -> {
            Integer idx = (Integer) chip.getTag();
            if (idx != null) removeStationAt(idx);
            chipStations.removeView(chip);

            for (int i = 0; i < chipStations.getChildCount(); i++) {
                View child = chipStations.getChildAt(i);
                if (child instanceof Chip) ((Chip) child).setTag(i);
            }

            if (pickupPoint != null && destPoint != null) {
                drawRouteAndStatsMulti();
            }
        });

        chipStations.addView(chip);
    }

    private void removeStationAt(int idx) {
        if (idx < 0 || idx >= stationPoints.size()) return;

        stationPoints.remove(idx);

        Marker m = stationMarkers.remove(idx);
        if (m != null) map.getOverlays().remove(m);

        map.invalidate();
    }

    private void addUserChip(String text) {
        chipUsers.addView(buildClosableChip(text));
    }

    private Chip buildClosableChip(String text) {
        Chip chip = new Chip(this);
        chip.setText(text);
        chip.setCloseIconVisible(true);

        chip.setChipBackgroundColorResource(R.color.white);
        chip.setTextColor(getResources().getColor(R.color.text, getTheme()));
        chip.setCloseIconTintResource(R.color.text);

        chip.setOnCloseIconClickListener(v -> ((ChipGroup) chip.getParent()).removeView(chip));
        return chip;
    }

    private List<String> buildTimeSlots(int stepMin, int maxHours) {
        List<String> slots = new ArrayList<>();
        Calendar now = Calendar.getInstance();

        int minute = now.get(Calendar.MINUTE);
        int add = (stepMin - (minute % stepMin)) % stepMin;
        now.add(Calendar.MINUTE, add);
        now.set(Calendar.SECOND, 0);

        Calendar end = (Calendar) now.clone();
        end.add(Calendar.HOUR_OF_DAY, maxHours);

        SimpleDateFormat fmt = new SimpleDateFormat("HH:mm", Locale.getDefault());
        Calendar t = (Calendar) now.clone();

        while (!t.after(end)) {
            slots.add(fmt.format(t.getTime()));
            t.add(Calendar.MINUTE, stepMin);
        }
        return slots;
    }

    interface OnValueAdded {
        void onAdded(String value);
    }
}