package com.komsiluk.taxi.auth;

import com.komsiluk.taxi.data.session.SessionManager;

import javax.inject.Inject;

import okhttp3.Authenticator;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.Route;

public class TokenAuthenticator implements Authenticator {

    private final SessionManager sessionManager;
    @Inject
    public TokenAuthenticator(SessionManager sessionManager) {
        this.sessionManager = sessionManager;
    }

    @Override
    public Request authenticate(Route route, Response response) {
        // Obrisati stari token
        sessionManager.clear();

        // Opcionalno: možeš obavestiti korisnika ili redirectovati na login
        // Na OkHttp nivou, vraća null -> request se prekida
        return null;
    }
}

