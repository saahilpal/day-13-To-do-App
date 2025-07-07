package com.example.service;

import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.MongoClient;

import java.time.Instant;


public class TaskService {

    private final MongoClient mongo;

    public TaskService(MongoClient mongo) {
        this.mongo = mongo;
    }

    public void createTask(String userId, String title, String description, String dueDate, String priority) {
        JsonObject task = new JsonObject()
                .put("type", "task")
                .put("userId", userId)
                .put("title", title)
                .put("description", description)
                .put("dueDate", dueDate)
                .put("priority", priority)
                .put("completed", false)
                .put("createdAt", Instant.now().toString())
                .put("updatedAt", Instant.now().toString());

        mongo.insert("data", task).onSuccess(id -> {
            System.out.println("Task created with ID: " + id);
        }).onFailure(err -> {
            System.err.println("Failed to create task: " + err.getMessage());
        });
    }

    public void viewTasks(String userId) {
        JsonObject query = new JsonObject()
                .put("type", "task")
                .put("userId", userId);

        mongo.find("data", query).onSuccess(tasks -> {
            System.out.println(" Your Tasks:");
            for (JsonObject task : tasks) {
                System.out.println("- " + task.getString("title") +
                        " | Due: " + task.getString("dueDate") +
                        " | Priority: " + task.getString("priority") +
                        " | Done: " + task.getBoolean("completed"));
            }
        }).onFailure(err -> {
            System.err.println("Failed to retrieve tasks: " + err.getMessage());
        });
    }

    public void markComplete(String taskId, boolean status) {
        JsonObject query = new JsonObject().put("_id", taskId);
        JsonObject update = new JsonObject()
                .put("$set", new JsonObject()
                        .put("completed", status)
                        .put("updatedAt", Instant.now().toString()));

        mongo.updateCollection("data", query, update).onSuccess(res -> {
            System.out.println("Task status updated.");
        }).onFailure(err -> {
            System.err.println("Failed to update task: " + err.getMessage());
        });
    }

    public void deleteTask(String taskId) {
        mongo.removeDocument("data", new JsonObject().put("_id", taskId)).onSuccess(res -> {
            System.out.println("🗑Task deleted.");
        }).onFailure(err -> {
            System.err.println(" Failed to delete task: " + err.getMessage());
        });
    }
}
