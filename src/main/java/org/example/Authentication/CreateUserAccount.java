package org.example.Authentication;

import org.example.CSV.CustomCsvHandler;
import org.apache.commons.codec.digest.DigestUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class CreateUserAccount {
    Scanner scanner = new Scanner(System.in);
    String username = "";
    String password = "";
    private CustomCsvHandler csv = new CustomCsvHandler("");

    public void createNewUserAccount() {
        System.out.println("Enter your username: ");
        this.username = scanner.nextLine();

        System.out.println("Enter your password: ");
        this.password = scanner.nextLine();

        System.out.println("Please hold, your account is being created");

        String hashedPassword = DigestUtils.md5Hex(this.password);
        csv.csvFileName = "user_data.csv";
        List<String[]> userData = new ArrayList<>();
        userData.add(new String[]{this.username, hashedPassword});
        csv.appendUserData(userData);

        System.out.println("Congratulations! Your account has been created");
    }
}
