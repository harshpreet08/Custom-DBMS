package org.example.Authentication;
import org.example.UsersDatabase.AfterLogin;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import java.io.FileReader;
import java.io.IOException;
import java.security.MessageDigest;
import java.util.Random;
import java.util.Scanner;

public class UserLogin {
    private String generatedCaptcha;
    private int loginCount = 0;
    private AfterLogin userLogin = new AfterLogin();

    Scanner scanner = new Scanner(System.in);

    public void loginAccount() {
        while (loginCount <= 1) {
            System.out.println("Enter your username: ");
            String username = scanner.nextLine();
            System.out.println("Enter password: ");
            String password = scanner.nextLine();

            if (validateUser(username, password)) {
                this.generatedCaptcha = generateRandomCaptcha();
                System.out.println("Generated captcha is: " + generatedCaptcha);
                System.out.println("Enter the captcha: ");
                String userCaptcha = scanner.nextLine();
                if (checkCaptcha(userCaptcha)) {
                    System.out.println("Login Successful !");
                    userLogin.userOptions();
                }
            } else {
                System.out.println("Login failed, try again.");
                loginCount++;
                loginAccount();
            }
            scanner.close();
        }
        System.out.println("Two failed attempts. Please try again later.");
    }

    private boolean validateUser(String user_name, String password) {
        try (CSVReader reader = new CSVReader(new FileReader("user_data.csv"))) {
            String[] nextLine;
            while ((nextLine = reader.readNext()) != null) {
                String username = nextLine[0];
                String hashedPassword = nextLine[1];

                if (username.equals(user_name) && checkPassword(password, hashedPassword)) {
                    return true;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (CsvValidationException e) {
            throw new RuntimeException(e);
        }
        return false;
    }

    private boolean checkPassword(String enteredPassword, String hashedPassword) {
        boolean isPasswordVerified = false;
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            messageDigest.update(enteredPassword.getBytes());
            byte[] calculatedHash = messageDigest.digest();

            StringBuilder hashBuilder = new StringBuilder();
            for (byte hashByte : calculatedHash) {
                hashBuilder.append(String.format("%02x", hashByte));
            }

            if (hashBuilder.toString().equals(hashedPassword)) {
                isPasswordVerified = true;
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return isPasswordVerified;
    }

    public String generateRandomCaptcha() {
        int length = 4;
        StringBuilder captcha = new StringBuilder();
        Random random = new Random();

        for (int i = 0; i < length; i++) {
            int digit = random.nextInt(10);
            captcha.append(digit);
        }
        return captcha.toString();
    }

    private boolean checkCaptcha(String enteredCaptcha) {
        return enteredCaptcha.equals(generatedCaptcha);
    }
}
