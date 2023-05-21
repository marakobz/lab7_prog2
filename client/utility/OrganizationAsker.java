package utility;

import exceptions.IncorrectScriptException;
import exceptions.MustBeNotEmptyException;
import exceptions.NotDeclaredLimitsException;
import models.Coordinates;
import models.Country;
import models.Person;
import models.TicketType;

import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.NoSuchElementException;
import java.util.Random;

import static java.time.LocalDateTime.parse;


/**
 * Ask from user or file ticket fields.
 */
public class OrganizationAsker {
    public static final String clack = "*";
    private final Float min_x = (float) -534;
    private final long min_discount = 0;
    private final long max_discount = 100;
    private final int min_price = 0;

    private boolean fileMode;
    private UserIO userIO;

    public OrganizationAsker(UserIO userIO) {
        fileMode = false;
        this.userIO = userIO;
    }


    /**
     * asks about ticket name
     *
     * @return new name
     */
    public String askName() throws IncorrectScriptException {
        String name;

        while (true) {
            try {
                Console.println("Enter name:");
                Console.print(clack);
                name = userIO.readline();
                if (fileMode) Console.println(name);
                if (name.equals("")) throw new MustBeNotEmptyException();
                break;
            } catch (NoSuchElementException exception) {
                Console.printerror("Name is entered incorrectly");
                if (fileMode) throw new IncorrectScriptException();
            } catch (MustBeNotEmptyException exception) {
                Console.printerror("Name cannot be empty");
                if (fileMode) throw new IncorrectScriptException();
            } catch (IllegalStateException exception) {
                Console.printerror("Mistake occurred");
                System.exit(0);
            }
        }
        return name;
    }

    /**
     * asks coordinates of ticket
     *
     * @return new coordinates
     */
    public Coordinates askCoordinates() throws IncorrectScriptException {
        Float x;
        float y;
        x = askX();
        y = askY();
        return new Coordinates(x, y);
    }

    /**
     * asks about date of person.
     *
     * @return new birthday
     */

    public LocalDateTime askDate() throws IncorrectScriptException {
       // LocalDate birthday;
        LocalDateTime birthday = LocalDateTime.now();
       /* String strDate;
        while (true) {
            try {
                Console.println("Enter date of birth:");
                Console.print(clack);
                strDate = userIO.readline();
                if (fileMode) Console.println(strDate);
                birthday = LocalDate.parse(((strDate)));
                break;
            } catch (NoSuchElementException exception) {
                Console.printerror("Date of birth is entered incorrectly");
                if (fileMode) throw new IncorrectScriptException();


            } catch (IllegalStateException exception) {
                Console.printerror("Mistake occurred");

                System.exit(0);
            }
        }*/
        return birthday;
    }

    /**
     * asks y of coordinates
     *
     * @return new y
     */
    public float askY() throws IncorrectScriptException {
        String strY;
        float y;
        while (true) {
            try {
                Console.println("Enter coordinate Y:");
                Console.print(clack);
                strY = userIO.readline();
                if (fileMode) Console.println(strY);
                y = Float.parseFloat(strY);
                break;
            } catch (NoSuchElementException exception) {
                Console.printerror("Coordinate Y is entered incorrectly");
                if (fileMode) throw new IncorrectScriptException();
            } catch (NumberFormatException exception) {
                Console.printerror("Coordinate Y has to be a number");
                if (fileMode) throw new IncorrectScriptException();
            } catch (NullPointerException | IllegalStateException exception) {
                Console.printerror("Mistake occurred");
                System.exit(0);
            }
        }
        return y;
    }

    /**
     * asks about x of coordinates
     *
     * @return new x
     */
    public Float askX() throws IncorrectScriptException {
        String strX;
        Float x;
        while (true) {
            try {
                Console.println("Enter coordinate X > " + min_x + ":");
                Console.print(clack);
                strX = userIO.readline();
                if (fileMode) Console.println(strX);
                x = Float.parseFloat(strX);
                if (x < min_x) throw new NotDeclaredLimitsException();
                break;
            } catch (NoSuchElementException exception) {
                Console.printerror("Coordinate X is entered incorrectly");
                if (fileMode) throw new IncorrectScriptException();
            } catch (NotDeclaredLimitsException exception) {
                Console.printerror("Coordinate X cannot be less than " + min_x);
                if (fileMode) throw new IncorrectScriptException();
            } catch (NumberFormatException exception) {
                Console.printerror("Coordinate X has to be a number");
                if (fileMode) throw new IncorrectScriptException();
            } catch (NullPointerException | IllegalStateException exception) {
                Console.printerror("Mistake occurred");
                System.exit(0);
            }
        }
        return x;
    }


    /**
     * asks about type of ticket
     *
     * @return new type
     */
    public TicketType askTicketType() throws IncorrectScriptException {
        String strTicketType;
        TicketType type;
        while (true){
            try {
                Console.println("List of ticket's types: " + TicketType.nameList());
                Console.println("Enter type of ticket:");

                Console.print(clack);
                strTicketType = userIO.readline();
                if (fileMode) Console.println(strTicketType);
                type = TicketType.valueOf(strTicketType.toUpperCase());
                break;
            } catch (NoSuchElementException exception) {
                Console.printerror("Type of ticket is entered incorrectly");
                if (fileMode) throw new IncorrectScriptException();
            } catch (IllegalArgumentException exception) {
                Console.printerror("This type of ticket does not exist");
                if (fileMode) throw new IncorrectScriptException();
            } catch (IllegalStateException exception) {
                Console.printerror("Mistake occurred");
                System.exit(0);
            }
        }
        return type;
    }

    /**
     * asks about price of a ticket
     *
     * @return new price
     */
    public int askPrice() throws IncorrectScriptException {
        String strPrice;
        int price;
        while (true) {
            try {
                Console.println("Enter price:");
                Console.print(clack);
                strPrice = userIO.readline();
                if (fileMode) Console.println(strPrice);
                price = Integer.parseInt(strPrice);
                if (price < min_price) throw new NotDeclaredLimitsException();
                break;
            } catch (NoSuchElementException exception) {
                Console.printerror("Price is entered incorrectly");
                if (fileMode) throw new IncorrectScriptException();
            } catch (NumberFormatException exception) {
                Console.printerror("Price has to be entered as a number");
                if (fileMode) throw new IncorrectScriptException();
            } catch (NullPointerException | IllegalStateException exception) {
                Console.printerror("Mistake occurred");
                System.exit(0);
            }catch (NotDeclaredLimitsException exception){
                Console.println("Price has to be more than zero");
                if (fileMode) throw new IncorrectScriptException();
            }
        }
        return price;

    }

    /**
     * asks about discount of ticket
     *
     * @return new discount
     */
    public long askDiscount() throws IncorrectScriptException {
        String strDiscount;
        long dicsount;
        while (true) {
            try {
                Console.println("Enter a discount:");
                Console.print(clack);
                strDiscount = userIO.readline();
                if (fileMode) Console.println(strDiscount);
                dicsount = Long.parseLong(strDiscount);
                if (dicsount > max_discount) throw new NotDeclaredLimitsException();
                if (min_discount > dicsount) throw new NotDeclaredLimitsException();
                break;
            } catch (NoSuchElementException exception) {
                Console.printerror("Discount is entered incorrectly");
                if (fileMode) throw new IncorrectScriptException();
            } catch (NumberFormatException exception) {
                Console.printerror("Discount has to be a number");
                if (fileMode) throw new IncorrectScriptException();
            } catch (NullPointerException | IllegalStateException exception) {
                Console.printerror("Mistake occurred");
                System.exit(0);
            } catch (NotDeclaredLimitsException exception) {
                Console.println("Discount has to be more than 0 and less than 100");
                if (fileMode) throw new IncorrectScriptException();
            }

        }
        return dicsount;

    }

    /**
     * asks about refund(true or false)
     *
     * @return new refund
     */
    public Boolean askRefund(String answer) throws IncorrectScriptException{
        String strRefund = answer + "(true/false): ";
        Boolean refund;
        while (true) {
            try {
                Console.println("Is ticket refundable?");
                Console.print(strRefund);
                Console.print(clack);
                strRefund = userIO.readline();

                if (fileMode) Console.println(strRefund);
                refund = Boolean.parseBoolean(strRefund);
                if (!strRefund.equals("true") && !strRefund.equals("false")) throw new NotDeclaredLimitsException();
                break;

            } catch (NoSuchElementException exception) {
                Console.printerror("Ticket is not refundable");
                if (fileMode) throw new IncorrectScriptException();
            }catch (NotDeclaredLimitsException e){
                Console.printerror("Enter true/false");
                if (fileMode) throw new IncorrectScriptException();
            } catch (NullPointerException | IllegalStateException exception) {
                Console.printerror("Mistake occurred");
                System.exit(0);
            }
        }

        return refund;
    }

    /**
     * asks for person in ticket
     *
     * @return new Person
     */
    public Person askPerson() throws IncorrectScriptException{
        LocalDateTime birthday;
        Float height;
        Float weight;
        Country nationality;

        birthday = askDate();
        height = askHeight();
        weight = askWeight();
        nationality = askNationality();
        return new Person(birthday, height, weight, nationality);
    }

    /**
     * asks about height of person
     *
     * @return new height
     */
    public Float askHeight() throws IncorrectScriptException {
        String strHeight;
        Float height;
        while (true) {
            try {
                Console.println("Enter height of person:");
                Console.print(clack);
                strHeight = userIO.readline();
                if (fileMode) Console.println(strHeight);
                height = Float.parseFloat(strHeight);
                if (height > 300) throw new NotDeclaredLimitsException();
                break;

            }catch (NotDeclaredLimitsException exception){
                Console.println("Height of person has to be less than 300 cm");
                if (fileMode) throw new IncorrectScriptException();
            } catch (NoSuchElementException exception) {
                Console.printerror("Cannot identify weight");
                if (fileMode) throw new IncorrectScriptException();
            } catch (NumberFormatException exception) {
                Console.printerror("Height has to be a number");
                if (fileMode) throw new IncorrectScriptException();
            } catch (NullPointerException | IllegalStateException exception) {
                Console.printerror("Mistake occurred");
                System.exit(0);
            }
        }
        return height;
    }

    /**
     * asks about weight of person
     *
     * @return new weight
     */
    public Float askWeight() throws IncorrectScriptException {
        String strWeight;
        Float weight;
        while (true) {
                try {
                    Console.println("Enter weight of person:");
                    Console.print(clack);
                    strWeight = userIO.readline();
                    if (fileMode) Console.println(strWeight);
                    weight = Float.parseFloat(strWeight);

                    if (weight > 120) throw new NotDeclaredLimitsException();
                    break;

                }catch (NotDeclaredLimitsException exception){
                    Console.println("Person with such weight is too big.");
                    if (fileMode) throw new IncorrectScriptException();
                } catch (NoSuchElementException exception) {
                    Console.printerror("Cannot identify the weight");
                    if (fileMode) throw new IncorrectScriptException();
                } catch (NumberFormatException exception) {
                    Console.printerror("Weight has to be a number");
                    if (fileMode) throw new IncorrectScriptException();
                } catch (NullPointerException | IllegalStateException exception) {
                    Console.printerror("Mistake occurred");
                    System.exit(0);
                }
            }
        return weight;
    }

    /**
     * asks about nationality of person
     *
     * @return new nationality
     */
    public Country askNationality () throws IncorrectScriptException {
        Country nationality;
        String strNationality;
        while (true) {
            try {
                Console.println("List of nationalities that you can choose:" + Country.nameList());
                Console.println("Enter nationality of human:");
                Console.print(clack);
                strNationality = userIO.readline();
                if (fileMode) Console.println(strNationality);
                nationality = Country.valueOf(strNationality.toUpperCase());
                break;

            } catch (NoSuchElementException exception) {
                Console.printerror("No nationality in list like that");
                if (fileMode) throw new IncorrectScriptException();
            } catch (IllegalArgumentException exception) {
                Console.printerror("No country like that in list");
                if (fileMode) throw new IncorrectScriptException();
            } catch (IllegalStateException exception) {
                Console.printerror("Mistake occurred");
                System.exit(0);
            }

        }
        return nationality;
    }

    /**
     * manages questions for update command
     *
     * @return answer
     */
    public boolean askQuestion(String question) throws IncorrectScriptException {
        String finalQuestion = question + " (yes/no):";
        String answer;
        while (true) {
            try {
                Console.println(finalQuestion);
                Console.print(clack);
                answer = userIO.readline();
                if (fileMode) Console.println(answer);
                if (!answer.equals("yes") && !answer.equals("no")) throw new NotDeclaredLimitsException();
                break;
            } catch (NoSuchElementException exception) {
                Console.printerror("Cannot understand the answer");
                if (fileMode) throw new IncorrectScriptException();
            } catch (NotDeclaredLimitsException exception) {
                Console.printerror("Answer has to 'yes' or 'no'");
                if (fileMode) throw new IncorrectScriptException();
            } catch (IllegalStateException exception) {
                Console.printerror("Mistake occurred");
                System.exit(0);
            }
        }
        return (answer.equals("+")) ? true : false;
    }

        @Override
        public String toString () {
            return "Organisation Asker is helper class for asking things";
        }
}