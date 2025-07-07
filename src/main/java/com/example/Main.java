package com.example;
import com.example.service.AuthService;
import com.example.service.TaskService;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        AuthService authService = new AuthService();
        TaskService taskService = new TaskService();
        Scanner scanner = new Scanner(System.in);

        String currentUserId = null;

        while (true) {
            System.out.println("\n1. Register\n2. Login\n3. Create Task\n4. View Tasks\n5. Exit");
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
                    currentUserId = authService.login(email, password);
                }
                case 3 -> {
                    if (currentUserId == null) {
                        System.out.println("Login first.");
                        break;
                    }
                    System.out.print("Title: ");
                    String title = scanner.nextLine();
                    System.out.print("Description: ");
                    String desc = scanner.nextLine();
                    taskService.createTask(currentUserId, title, desc);
                }
                case 4 -> {
                    if (currentUserId == null) {
                        System.out.println("Login first.");
                        break;
                    }
                    taskService.viewTasks(currentUserId);
                }
                case 5 -> {
                    System.out.println("Exiting...");
                    return;
                }
            }
        }
    }
}
