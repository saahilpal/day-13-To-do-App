package com.example;

import com.example.config.AppConfig;
import com.example.db.MongoProvider;
import com.example.db.RedisProvider;
import com.example.security.JwtProvider;
import com.example.service.AuthService;
import com.example.service.EmailService;
import com.example.service.TaskService;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.MongoClient;
import io.vertx.redis.client.RedisAPI;

import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();

        // Load config.json
        AppConfig.loadConfig(vertx, "src/main/resources/config.json", configPromise -> {
            if (configPromise.future().failed()) {
                System.err.println(" Failed to load config: " + configPromise.future().cause().getMessage());
                return;
            }

            JsonObject config = configPromise.future().result();

            // Setup dependencies
            MongoClient mongoClient = MongoProvider.get(vertx, config);
            RedisAPI redis = RedisProvider.get(vertx, config.getString("redis_host"));
            EmailService emailService = new EmailService(vertx, config);
            JwtProvider jwtProvider = new JwtProvider(config.getString("jwt_secret"));

            // Initialize services
            AuthService authService = new AuthService(mongoClient, redis, jwtProvider, emailService);
            TaskService taskService = new TaskService(mongoClient);

            // Start terminal UI
            runTerminal(authService, taskService);
        });
    }

    private static void runTerminal(AuthService authService, TaskService taskService) {
        Scanner scanner = new Scanner(System.in);
        String currentUserId = null;

        while (true) {
            System.out.println("\n1. Register");
            System.out.println("2. Login");
            System.out.println("3. Create Task");
            System.out.println("4. View Tasks");
            System.out.println("5. Logout");
            System.out.println("6. Exit");
            System.out.print("Choose: ");
            int choice = Integer.parseInt(scanner.nextLine());

            switch (choice) {
                case 1 -> {
                    System.out.print("Email: ");
                    String email = scanner.nextLine();
                    System.out.print("Name: ");
                    String name = scanner.nextLine();
                    authService.register(email, name);
                }
                case 2 -> {
                    System.out.print("Email: ");
                    String email = scanner.nextLine();
                    System.out.print("Password: ");
                    String password = scanner.nextLine();
                    currentUserId = authService.loginAndReturnUserId(email, password);
                }
                case 3 -> {
                    if (currentUserId == null) {
                        System.out.println("⚠️ Login first.");
                        break;
                    }
                    System.out.print("Title: ");
                    String title = scanner.nextLine();
                    System.out.print("Description: ");
                    String desc = scanner.nextLine();
                    System.out.print("Due Date (yyyy-mm-dd): ");
                    String dueDate = scanner.nextLine();
                    System.out.print("Priority (LOW/MEDIUM/HIGH): ");
                    String priority = scanner.nextLine();
                    taskService.createTask(currentUserId, title, desc, dueDate, priority);
                }
                case 4 -> {
                    if (currentUserId == null) {
                        System.out.println("⚠️ Login first.");
                        break;
                    }
                    taskService.viewTasks(currentUserId);
                }
                case 5 -> {
                    currentUserId = null;
                    System.out.println("🔒 Logged out.");
                }
                case 6 -> {
                    System.out.println("👋 Exiting...");
                    return;
                }
                default -> System.out.println("❌ Invalid option.");
            }
        }
    }
}
