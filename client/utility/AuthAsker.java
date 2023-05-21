package utility;

import exceptions.MustBeNotEmptyException;
import exceptions.NotDeclaredLimitsException;

import javax.swing.*;
import java.util.NoSuchElementException;
import java.util.Scanner;



/**
 * Asks user a login and password.
 */
public class AuthAsker {
    private Scanner userScanner;
    private String str = ">>>>";

    public AuthAsker(Scanner userScanner) {
        this.userScanner = userScanner;
    }

    /**
     * Asks user a login.
     *
     * @return login.
     */
    public String askLogin() {
        String login;
        while (true) {
            try {
                Console.println("Enter login:");
                Console.print(str);
                login = userScanner.nextLine().trim();
                if (login.equals("")) throw new MustBeNotEmptyException();
                break;
            } catch (NoSuchElementException exception) {
                Console.printerror("Login does not exist");
            } catch (MustBeNotEmptyException exception) {
                Console.printerror("Name cannot be empty");
            } catch (IllegalStateException exception) {
                Console.printerror("Unpredicted mistake occurred");
                System.exit(0);
            }
        }
        return login;
    }

    /**
     * Asks user a password.
     *
     * @return password.
     */
    public String askPassword() {
        String pass;
        while (true) {
            try {
                Console.println("Enter password:");
                Console.print(str);

                pass = readPassword();

                break;
            } catch (NoSuchElementException exception) {
                Console.printerror("Incorrect password or login");
            } catch (IllegalStateException exception) {
                Console.printerror("Unpredicted mistake occurred");
                System.exit(0);
            }
        }
        return pass;
    }
    protected String readPassword() {
        if (System.console() == null) {
            return userScanner.nextLine().trim();
        }
        return new String(System.console().readPassword());
    }


    /**
     * Asks a user a question.
     *
     * @param question A question.
     * @return Answer (true/false).
     */
    public boolean askQuestion(String question) {
        String finalQuestion = question + " (+/-):";
        String answer;
        while (true) {
            try {
                Console.println(finalQuestion);
                Console.print(str);
                answer = userScanner.nextLine().trim();
                if (!answer.equals("+") && !answer.equals("-")) throw new NotDeclaredLimitsException();
                break;
            } catch (NoSuchElementException exception) {
                Console.printerror("Answer is incorrect");
            } catch (NotDeclaredLimitsException exception) {
                Console.printerror("Answer has to be '+' or '-'!");
            } catch (IllegalStateException exception) {
                Console.printerror("Unpredicted mistake occurred");
                System.exit(0);
            }
        }
        return answer.equals("+");
    }
}
