package commands;

import exceptions.DatabaseHandlingException;
import exceptions.UserIsNotFoundException;
import exceptions.WrongAmountOfElementsException;
import util.ResponseCode;
import util.ServerResponse;
import util.User;
import utility.Console;
import utility.DatabaseUserManager;

public class LoginCommand extends AbstractCommand{
    private DatabaseUserManager databaseUserManager;

    public LoginCommand(DatabaseUserManager databaseUserManager) {
        super("login", "", "внутренняя команда");
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
            if (databaseUserManager.checkUserByUsernameAndPassword(user)) Console.println("Пользователь " +
                    user.getUsername() + " авторизован.");
            else throw new UserIsNotFoundException();
        } catch (WrongAmountOfElementsException exception) {
            Console.printerror("Использование: эммм...эээ.это внутренняя команда...");
        } catch (ClassCastException exception) {
            Console.printerror("Переданный клиентом объект неверен!");
        } catch (DatabaseHandlingException exception) {
            Console.printerror("Произошла ошибка при обращении к базе данных!");
        } catch (UserIsNotFoundException exception) {
            Console.printerror("Неправильные имя пользователя или пароль!");
        }
        return new ServerResponse("", ResponseCode.SUCCESS);
    }
}
