package com.example.route;

import com.example.security.JwtProvider;
import com.example.service.AuthService;
import com.example.service.PasswordResetService;
import com.example.service.TaskService;
import io.vertx.core.Vertx;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.JWTAuthHandler;

public class RouterFactory {

    public static Router create(Vertx vertx,
                                AuthService authService,
                                TaskService taskService,
                                PasswordResetService resetService,
                                JwtProvider jwtProvider) {

        Router router = Router.router(vertx);

        // Enable body parsing for JSON
        router.route().handler(BodyHandler.create());

        // Mount public (auth) routes
        AuthRoutes.mount(router, authService, resetService);

        // Secure task routes
        router.route("/api/tasks/*").handler(JWTAuthHandler.create(jwtProvider.getJwtAuth()));

        // Mount task routes
        TaskRoutes.mount(router, taskService);

        return router;
    }
}
