package com.example.route;

import com.example.service.TaskService;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;

public class TaskRoutes {

    public static void mount(Router router, TaskService taskService) {
        router.post("/api/tasks").handler(ctx -> handleCreate(ctx, taskService));
        router.get("/api/tasks").handler(ctx -> handleView(ctx, taskService));
    }

    private static String getUserId(RoutingContext ctx) {
        return ctx.user().principal().getString("userId");
    }

    private static void handleCreate(RoutingContext ctx, TaskService taskService) {
        ctx.request().body().onSuccess(body -> {
            String userId = getUserId(ctx);
            var json = body.toJsonObject();
            taskService.createTask(
                    userId,
                    json.getString("title"),
                    json.getString("description"),
                    json.getString("dueDate"),
                    json.getString("priority")
            );
            ctx.response().setStatusCode(201).end("Task created.");
        });
    }

    private static void handleView(RoutingContext ctx, TaskService taskService) {
        String userId = getUserId(ctx);
        taskService.viewTasks(userId);
        ctx.response().end("Tasks listed (check console)");
    }
}
