package com.nicolappli.go4lunch.Controllers.Activities;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.RelativeLayout;
import android.widget.Toast;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.AutocompletePrediction;
import com.google.android.libraries.places.api.model.AutocompleteSessionToken;
import com.google.android.libraries.places.api.model.RectangularBounds;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.firebase.auth.FirebaseAuth;
import com.nicolappli.go4lunch.Adapters.PageAdapter;
import com.nicolappli.go4lunch.R;
import com.nicolappli.go4lunch.SignInActivity;
import com.nicolappli.go4lunch.Utils.RefreshEvent;
import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, GoogleApiClient.OnConnectionFailedListener {

    @BindView(R.id.input_search)
    AutoCompleteTextView mInputSearch;
    //for design
    private Toolbar mToolbar;
    private DrawerLayout mDrawerLayout;
    private RelativeLayout mRelativeLayoutSearch;

    //for data
    private static final String TAG = "MainActivity";
    private static final int ERROR_DIALOG_REQUEST = 9001;
    public static final int SIGN_OUT_TASK = 83;
    private PlacesClient mPlacesClient;
    private ArrayList<String> predictionsArray = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        mRelativeLayoutSearch = findViewById(R.id.relative_layout_search);
        String apiKey = getString(R.string.api_key);

        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            Intent mainActivity = new Intent(MainActivity.this, SignInActivity.class);
            startActivity(mainActivity);
        }

        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), apiKey);
        }
        mPlacesClient = Places.createClient(this);

        this.configureViewPagerAndTabs();
        this.configureToolbar();
        this.configureDrawerLayout();
        this.configureNavigationView();
        this.init();
    }

    //******************************************************************************
    //CONFIGURATION
    //******************************************************************************

    //*****************************
    //TOOLBAR
    //*****************************

    private void configureToolbar() {
        // Get the toolbar view inside the activity
        this.mToolbar = findViewById(R.id.toolbar);
        // Sets the Toolbar
        setSupportActionBar(mToolbar);
    }

    //*****************************
    //VIEW PAGER AND TABS
    //*****************************

    private void configureViewPagerAndTabs() {
        ViewPager pager = findViewById(R.id.activity_main_view_pager);

        pager.setAdapter(new PageAdapter(getSupportFragmentManager()) {
        });

        TabLayout tabs = findViewById(R.id.activity_main_tabs);
        tabs.setupWithViewPager(pager);
        tabs.setTabMode(TabLayout.MODE_FIXED);
    }

    //*****************************
    //NAVIGATION DRAWER
    //*****************************

    private void configureDrawerLayout() {
        this.mDrawerLayout = findViewById(R.id.activity_main_drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, mDrawerLayout, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawerLayout.addDrawerListener(toggle);
        toggle.syncState();
    }

    private void configureNavigationView() {
        NavigationView navigationView = findViewById(R.id.activity_main_nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onBackPressed() {
        if (this.mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            this.mDrawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.activity_main_drawer_lunch:

                break;
            case R.id.activity_main_drawer_settings:

                break;
            case R.id.activity_main_drawer_logout:
                signOut();
                Intent singInActivity = new Intent(MainActivity.this, SignInActivity.class);
                startActivity(singInActivity);
                break;
            default:
                break;
        }
        this.mDrawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    //*****************************
    //OPTION MENU
    //*****************************

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu and add it to the toolbar
        getMenuInflater().inflate(R.menu.menu_activity_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_activity_main_search:
                mRelativeLayoutSearch.setVisibility(View.VISIBLE);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    //******************************************************************************
    //LOCATION
    //******************************************************************************



    //******************************************************************************
    //GOOGLE MAPS
    //******************************************************************************

    public boolean isServicesOK() {
        Log.d(TAG, "isServicesOK: checking google services version");

        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(MainActivity.this);

        if (available == ConnectionResult.SUCCESS) {
            //everything is fine and the user can make map requests
            Log.d(TAG, "isServicesOK: Google Play Services is working");
            return true;
        } else if (GoogleApiAvailability.getInstance().isUserResolvableError(available)) {
            //an error occured but we can resolve it
            Log.d(TAG, "isServicesOK: an error occured but we can fix it");
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(MainActivity.this, available, ERROR_DIALOG_REQUEST);
            dialog.show();
        } else {
            Toast.makeText(this, "You can't make map request", Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    private void signOut() {
        AuthUI.getInstance().signOut(this).addOnSuccessListener(this, updateUiAfterHttpRequest(SIGN_OUT_TASK));
    }

    private OnSuccessListener<Void> updateUiAfterHttpRequest(final int task) {
        return aVoid -> {
            switch (task) {
                case SIGN_OUT_TASK:
                    finish();
                    break;
                default:
                    break;
            }
        };
    }

    //******************************************************************************
    //GOOGLE MAPS
    //******************************************************************************

    private void init() {
        //see when the text change
        mInputSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {  }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                setupAutocomplete();
                mInputSearch.setAdapter(new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_list_item_1, predictionsArray));
            }

            @Override
            public void afterTextChanged(Editable s) {  }
        });

        //set click listener when user clicks in an item in the dropdown list
        mInputSearch.setOnItemClickListener((parent, view, position, id) -> {
            EventBus.getDefault().post(new RefreshEvent(mInputSearch.getText().toString()));

            hideSoftKeyboard(MainActivity.this);
            mRelativeLayoutSearch.setVisibility(View.GONE);
            mInputSearch.setText("");
            predictionsArray.clear();
        });
    }

    private void setupAutocomplete(){
        AutocompleteSessionToken token = AutocompleteSessionToken.newInstance();
        // Create a RectangularBounds object.
        RectangularBounds bounds = RectangularBounds.newInstance(
                new LatLng(-33.880490, 151.184363),
                new LatLng(-33.858754, 151.229596));
        // Use the builder to create a FindAutocompletePredictionsRequest.
        FindAutocompletePredictionsRequest request = FindAutocompletePredictionsRequest.builder()
        // Call either setLocationBias() OR setLocationRestriction().
                .setLocationBias(bounds)
                //.setLocationRestriction(bounds)
                .setCountry("fr")
                .setTypeFilter(TypeFilter.CITIES)
                .setSessionToken(token)
                .setQuery(mInputSearch.getText().toString())
                .build();

        mPlacesClient.findAutocompletePredictions(request).addOnSuccessListener((response) -> {
            for (AutocompletePrediction prediction : response.getAutocompletePredictions()) {
                Log.i(TAG, prediction.getPlaceId());
                Log.i(TAG, prediction.getPrimaryText(null).toString());
                //test if the prediction isn't present in the list
                if(!predictionsArray.contains(prediction.getPrimaryText(null).toString())){
                    predictionsArray.add(prediction.getPrimaryText(null).toString());
                }
            }
        }).addOnFailureListener((exception) -> {
            if (exception instanceof ApiException) {
                ApiException apiException = (ApiException) exception;
                Log.e(TAG, "Place not found: " + apiException.getStatusCode());
            }
        });
    }

    private void hideSoftKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}