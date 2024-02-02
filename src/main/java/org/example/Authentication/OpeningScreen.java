package org.example.Authentication;
import java.util.Scanner;

public class OpeningScreen {
    private Scanner scanner = new Scanner(System.in);

    public void options() {
        while (true) {
            System.out.println("Press 1 for Login");
            System.out.println("Press 2 for Signup");
            System.out.println("Press 3 to Exit");

            if (scanner.hasNextInt()) {
                int input = scanner.nextInt();
                scanner.nextLine();

                switch (input) {
                    case 1:
                        login();
                        break;
                    case 2:
                        signup();
                        break;
                    case 3:
                        System.out.println("Application terminated");
                        scanner.close(); // Closing scanner here as I have to exit the loop
                        System.exit(0);
                    default:
                        System.out.println("Invalid input. Please select a valid option.");
                }
            } else {
                System.out.println("Invalid input. Please select a valid option.");
                scanner.nextLine();
            }
        }
    }

    private void login() {
        UserLogin login = new UserLogin();
        login.loginAccount();
    }

    private void signup() {
        CreateUserAccount create = new CreateUserAccount();
        create.createNewUserAccount();
    }

}