package com.admin.coredge.Activity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.PendingIntent;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.admin.coredge.MainActivity;
import com.admin.coredge.R;

import net.openid.appauth.AuthState;
import net.openid.appauth.AuthorizationException;
import net.openid.appauth.AuthorizationRequest;
import net.openid.appauth.AuthorizationResponse;
import net.openid.appauth.AuthorizationService;
import net.openid.appauth.AuthorizationServiceConfiguration;
import net.openid.appauth.ClientAuthentication;
import net.openid.appauth.ClientSecretBasic;
import net.openid.appauth.ResponseTypeValues;
import net.openid.appauth.TokenRequest;
import net.openid.appauth.TokenResponse;

import org.json.JSONObject;

public class LendingActivity extends AppCompatActivity {
    AuthorizationRequest authRequest;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lending);
    }

    public void OpenLogin(View view) {
        AuthorizationServiceConfiguration mServiceConfiguration =
                new AuthorizationServiceConfiguration(
                        Uri.parse("http://159.65.144.39:8080/auth/realms/Clients/protocol/openid-connect/auth"), // Authorization endpoint
                        Uri.parse("http://159.65.144.39:8080/auth/realms/Clients/protocol/openid-connect/token")); // Token endpoint

        ClientAuthentication mClientAuthentication =
                new ClientSecretBasic("5b47fde6-383a-4e21-8032-e60388d18ddf");

         authRequest = new AuthorizationRequest.Builder(
                mServiceConfiguration,
                "app", // Client ID
                ResponseTypeValues.CODE,
                Uri.parse("com.example.app:/oauth2callback") // Redirect URI
        ).build();

        AuthorizationService service = new AuthorizationService(this);

        Intent intent = service.getAuthorizationRequestIntent(authRequest);
        startActivityForResult(intent, 101);


//        AuthorizationRequest.Builder authRequestBuilder =
//                new AuthorizationRequest.Builder(
//                        serviceConfig, // the authorization service configuration
//                        "app", // the client ID, typically pre-registered and static
//                        ResponseTypeValues.CODE, // the response_type value: we want a code
//                        "");

//        AuthorizationServiceConfiguration.fetchFromIssuer(
//                Uri.parse("http://159.65.144.39:8080/auth"),
//                new AuthorizationServiceConfiguration.RetrieveConfigurationCallback() {
//                    public void onFetchConfigurationCompleted(
//                            @Nullable AuthorizationServiceConfiguration serviceConfiguration,
//                            @Nullable AuthorizationException ex) {
//                        if (ex != null) {
//                            Log.e("anp", "failed to fetch configuration");
//                            return;
//                        }
//
//                        // use serviceConfiguration as needed
//                    }
//                });
  //      startActivity(new Intent(this, MainActivity.class));

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (requestCode != 101) {
            return;
        }

        AuthorizationResponse authResponse = AuthorizationResponse.fromIntent(intent);
        AuthorizationException authException = AuthorizationException.fromIntent(intent);

        AuthState mAuthState = new AuthState(authResponse, authException);

        AuthorizationService authService = new AuthorizationService(this);

        authService.performAuthorizationRequest(
                authRequest,
                PendingIntent.getActivity(this, 0, new Intent(this, MainActivity.class), 0),
                PendingIntent.getActivity(this, 0, new Intent(this, MainActivity.class), 0));



        // Handle authorization response error here

       // retrieveTokens(authResponse);
    }

//    private void retrieveTokens(AuthorizationResponse authResponse) {
//        TokenRequest tokenRequest = response.createTokenExchangeRequest();
//
//        AuthorizationService service = new AuthorizationService(this);
//
//        service.performTokenRequest(request, mClientAuthentication,
//                new AuthorizationService.TokenResponseCallback() {
//                    @Override
//                    public void onTokenRequestCompleted(TokenResponse tokenResponse,
//                                                        AuthorizationException tokenException) {
//                        mAuthState.update(tokenResponse, tokenException);
//
//                        // Handle token response error here
//
//                        persistAuthState(mAuthState);
//                    }
//                });
//    }




    public void OpenReg(View view) {
      //  startActivity(new Intent(this, RegisterActivity.class));

        startActivity(new Intent(this, MainActivity.class));

    }
}