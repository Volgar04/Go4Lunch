package com.nicolappli.go4lunch;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.nicolappli.go4lunch.Controllers.Activities.MainActivity;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SignInActivity extends AppCompatActivity {

    @BindView(R.id.activity_sign_in_coordinator_layout)
    CoordinatorLayout mActivitySignInCoordinatorLayout;

    //for data
    private static final int RC_SIGN_IN = 123;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        ButterKnife.bind(this);

    }

    public void createSignInIntent() {
        List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.GoogleBuilder().build());

        // Create and launch sign-in intent
        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(providers)
                        .setIsSmartLockEnabled(false, true)
                        .build(),
                RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);

            if (resultCode == RESULT_OK) {
                // Successfully signed in
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                this.showSnackBar(this.mActivitySignInCoordinatorLayout, "Authentication succeed !");
                Intent mainActivity = new Intent(SignInActivity.this, MainActivity.class);
                startActivity(mainActivity);
                // ...
            } else {
                if (response == null) {
                    this.showSnackBar(this.mActivitySignInCoordinatorLayout, "Authentication canceled !");
                } else if (Objects.requireNonNull(response.getError()).getErrorCode() == ErrorCodes.NO_NETWORK) {
                    this.showSnackBar(this.mActivitySignInCoordinatorLayout, "Error: No Internet");
                } else if (response.getError().getErrorCode() == ErrorCodes.UNKNOWN_ERROR) {
                    this.showSnackBar(this.mActivitySignInCoordinatorLayout, "Error: Unknown");
                }
            }
        }
    }

    // 2 - Show Snack Bar with a message
    private void showSnackBar(CoordinatorLayout coordinatorLayout, String message) {
        Snackbar.make(coordinatorLayout, message, Snackbar.LENGTH_SHORT).show();
    }

    @OnClick(R.id.activity_sign_in_btn)
    public void onViewClicked() {
        this.createSignInIntent();
    }
}
