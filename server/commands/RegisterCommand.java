package commands;

import exceptions.DatabaseHandlingException;
import exceptions.UserAlreadyExistException;
import exceptions.WrongAmountOfElementsException;
import util.ResponseCode;
import util.ServerResponse;
import util.User;
import utility.Console;
import utility.DatabaseUserManager;

public class RegisterCommand extends AbstractCommand{
    private DatabaseUserManager databaseUserManager;

    public RegisterCommand(DatabaseUserManager databaseUserManager) {
        super("register", "", "внутренняя команда");
        this.databaseUserManager = databaseUserManager;
    }

    /**
     * Executes the command.
     *
     * @return Command exit status.
     */
    @Override
    public ServerResponse execute(String stringArgument, Object objectArgument, User user) {
        try {
            if (!stringArgument.isEmpty() || objectArgument != null) throw new WrongAmountOfElementsException();
            if (databaseUserManager.insertUser(user)) Console.println("Пользователь " +
                    user.getUsername() + " зарегистрирован.");
            else throw new UserAlreadyExistException();
        } catch (WrongAmountOfElementsException exception) {
            Console.printerror("Использование: эммм...эээ.это внутренняя команда...");
        } catch (ClassCastException exception) {
            Console.printerror("Переданный клиентом объект неверен!");
        } catch (DatabaseHandlingException exception) {
            Console.printerror(exception.getMessage());
            Console.printerror("Произошла ошибка при обращении к базе данных!");
        } catch (UserAlreadyExistException exception) {
            Console.printerror("Пользователь " + user.getUsername() + " уже существует!");
        }
        return new ServerResponse("", ResponseCode.SUCCESS);
    }
}
