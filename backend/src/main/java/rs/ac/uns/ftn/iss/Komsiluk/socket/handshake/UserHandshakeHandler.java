package rs.ac.uns.ftn.iss.Komsiluk.socket.handshake;

import org.jspecify.annotations.NonNull;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;

import java.security.Principal;
import java.util.Map;

@Component
public class UserHandshakeHandler extends DefaultHandshakeHandler {

    @Override
    protected Principal determineUser(@NonNull ServerHttpRequest request, @NonNull WebSocketHandler wsHandler, @NonNull Map<String, Object> attributes) {
        String email = (String) attributes.get("user");
        return () -> email;
    }
}
