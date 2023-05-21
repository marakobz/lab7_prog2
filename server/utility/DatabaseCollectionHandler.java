package utility;

import exceptions.DatabaseHandlingException;
import models.*;
import org.slf4j.Logger;
import util.TicketRaw;
import util.User;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.NavigableSet;
import java.util.TreeSet;


/*
TODO сделать ввод пароля невидимым, переделать ввод даты рождения человека, доперевести всё, перенести вывод выполнения запроса на клиент
 */


public class DatabaseCollectionHandler {
    Logger logger;

    //ticket table
    private final String SELECT_ALL_TICKETS = "SELECT * FROM " + DatabaseHandler.TICKET_TABLE;
    private final String SELECT_TICKET_BY_ID = SELECT_ALL_TICKETS + " WHERE " + DatabaseHandler.TICKET_TABLE_ID_COLUMN + " = ?";
    private final String SELECT_TICKET_BY_ID_AND_USER_ID = SELECT_TICKET_BY_ID + " AND " +
            DatabaseHandler.TICKET_TABLE_USER_ID_COLUMN + " = ?";
    private final String INSERT_TICKET = "INSERT INTO " +
            DatabaseHandler.TICKET_TABLE + " (" +
           // DatabaseHandler.TICKET_TABLE_ID_COLUMN + ", " +
            DatabaseHandler.TICKET_TABLE_TICKET_NAME_COLUMN + ", " +
            DatabaseHandler.TICKET_TABLE_CREATION_DATE_COLUMN + ", " +
            DatabaseHandler.TICKET_TABLE_PRICE_COLUMN + ", " +
            DatabaseHandler.TICKET_TABLE_DISCOUNT_COLUMN + ", " +
            DatabaseHandler.TICKET_TABLE_REFUND_COLUMN + ", " +
            DatabaseHandler.TICKET_TABLE_TICKET_TYPE_COLUMN + ", " +
            DatabaseHandler.TICKET_TABLE_PERSON_ID_COLUMN + ", " +
            DatabaseHandler.TICKET_TABLE_USER_ID_COLUMN +
            ") VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
    private final String DELETE_TICKET_BY_ID = "DELETE FROM " + DatabaseHandler.TICKET_TABLE +
            " WHERE " + DatabaseHandler.TICKET_TABLE_ID_COLUMN + " = ?";
    private final String UPDATE_TICKET_NAME_BY_ID = "UPDATE " + DatabaseHandler.TICKET_TABLE + " SET " +
            DatabaseHandler.TICKET_TABLE_TICKET_NAME_COLUMN + " = ?" + " WHERE " +
            DatabaseHandler.TICKET_TABLE_ID_COLUMN + " = ?";

    private final String UPDATE_TICKET_PRICE_BY_ID = "UPDATE " + DatabaseHandler.TICKET_TABLE + " SET " +
            DatabaseHandler.TICKET_TABLE_PRICE_COLUMN + " = ?" + " WHERE " +
            DatabaseHandler.TICKET_TABLE_ID_COLUMN + " = ?";

    private final String UPDATE_TICKET_DISCOUNT_PRICE_BY_ID = "UPDATE " + DatabaseHandler.TICKET_TABLE + " SET " +
            DatabaseHandler.TICKET_TABLE_DISCOUNT_COLUMN + " = ?" + " WHERE " +
            DatabaseHandler.TICKET_TABLE_ID_COLUMN + " = ?";
    private final String UPDATE_TICKET_REFUND_PRICE_BY_ID = "UPDATE " + DatabaseHandler.TICKET_TABLE + " SET " +
            DatabaseHandler.TICKET_TABLE_REFUND_COLUMN + " = ?" + " WHERE " +
            DatabaseHandler.TICKET_TABLE_ID_COLUMN + " = ?";

    private final String UPDATE_TICKET_TICKET_TYPE_BY_ID = "UPDATE " + DatabaseHandler.TICKET_TABLE + " SET " +
            DatabaseHandler.TICKET_TABLE_TICKET_TYPE_COLUMN + " = ?" + " WHERE " +
            DatabaseHandler.TICKET_TABLE_ID_COLUMN + " = ?";
    private final String UPDATE_TICKET_PERSON_BY_ID = "UPDATE " + DatabaseHandler.TICKET_TABLE + " SET " +
            DatabaseHandler.TICKET_TABLE_PERSON_ID_COLUMN + " = ?" + " WHERE " +
            DatabaseHandler.TICKET_TABLE_ID_COLUMN + " = ?";

    //  PERSON_TABLE
    private final String SELECT_ALL_PERSON = "SELECT * FROM " + DatabaseHandler.PERSON_TABLE;
    private final String SELECT_PERSON_BY_ID = SELECT_ALL_PERSON +
            " WHERE " + DatabaseHandler.PERSON_TABLE_ID_COLUMN + " = ?";
    private final String INSERT_PERSON = "INSERT INTO " +
            DatabaseHandler.PERSON_TABLE + " (" +
            DatabaseHandler.PERSON_TABLE_DATE_OF_BIRTH_COLUMN + ", " +
            DatabaseHandler.PERSON_TABLE_HEIGHT_COLUMN + ", " +
            DatabaseHandler.PERSON_TABLE_WEIGHT_COLUMN + ", " +
            DatabaseHandler.PERSON_TABLE_COUNTRY_COLUMN + ") VALUES (?, ?, ?, ?)";
    private final String UPDATE_PERSON_BY_ID = "UPDATE " + DatabaseHandler.PERSON_TABLE + " SET " +
            DatabaseHandler.PERSON_TABLE_ID_COLUMN + " = ?, " +
            DatabaseHandler.PERSON_TABLE_DATE_OF_BIRTH_COLUMN + " = ?, " +
            DatabaseHandler.PERSON_TABLE_HEIGHT_COLUMN + " = ?, " +
            DatabaseHandler.PERSON_TABLE_WEIGHT_COLUMN + " = ?, " +
            DatabaseHandler.PERSON_TABLE_COUNTRY_COLUMN + " = ?";

    private final String DELETE_PERSON_BY_ID = "DELETE FROM " + DatabaseHandler.PERSON_TABLE +
            " WHERE " + DatabaseHandler.PERSON_TABLE_ID_COLUMN + " = ?";

    // COORDINATES_TABLE
    private final String SELECT_ALL_COORDINATES = "SELECT * FROM " + DatabaseHandler.COORDINATES_TABLE;
    private final String SELECT_COORDINATES_BY_TICKET_ID = SELECT_ALL_COORDINATES +
            " WHERE " + DatabaseHandler.COORDINATES_TABLE_TICKET_ID_COLUMN + " = ?";
    private final String INSERT_COORDINATES = "INSERT INTO " +
            DatabaseHandler.COORDINATES_TABLE + " (" +
            DatabaseHandler.COORDINATES_TABLE_X_COLUMN + ", " +
            DatabaseHandler.COORDINATES_TABLE_Y_COLUMN + ", " +
            DatabaseHandler.COORDINATES_TABLE_TICKET_ID_COLUMN + ") VALUES (?, ?, ?)";
    private final String UPDATE_COORDINATES_BY_TICKET_ID = "UPDATE " + DatabaseHandler.COORDINATES_TABLE + " SET " +
            DatabaseHandler.COORDINATES_TABLE_X_COLUMN + " = ?, " +
            DatabaseHandler.COORDINATES_TABLE_Y_COLUMN + " = ?" + " WHERE " +
            DatabaseHandler.COORDINATES_TABLE_TICKET_ID_COLUMN + " = ?";


    private DatabaseHandler databaseHandler;
    private DatabaseUserManager databaseUserManager;

    public DatabaseCollectionHandler(DatabaseHandler databaseHandler, DatabaseUserManager databaseUserManager){
        this.databaseHandler = databaseHandler;
        this.databaseUserManager = databaseUserManager;
    }

    private Ticket createTicket(ResultSet resultSet) throws SQLException{
        int id = resultSet.getInt(DatabaseHandler.TICKET_TABLE_ID_COLUMN);
        String name = resultSet.getString(DatabaseHandler.TICKET_TABLE_TICKET_NAME_COLUMN);
        Coordinates coordinates = getCoordinatesByTicketId(id);
        LocalDateTime localDate = resultSet.getTimestamp(DatabaseHandler.TICKET_TABLE_CREATION_DATE_COLUMN).toLocalDateTime();
        int price = resultSet.getInt(DatabaseHandler.TICKET_TABLE_PRICE_COLUMN);
        long discount = resultSet.getLong(DatabaseHandler.TICKET_TABLE_DISCOUNT_COLUMN);
        Boolean refundable = resultSet.getBoolean(DatabaseHandler.TICKET_TABLE_REFUND_COLUMN);
        TicketType type = TicketType.valueOf(resultSet.getString(DatabaseHandler.TICKET_TABLE_TICKET_TYPE_COLUMN));
        Person person = getPersonById(resultSet.getInt(DatabaseHandler.TICKET_TABLE_PERSON_ID_COLUMN));
        User owner = databaseUserManager.getUserById(resultSet.getInt(DatabaseHandler.TICKET_TABLE_USER_ID_COLUMN));
        return new Ticket(
                id,
                name,
                coordinates,
                localDate,
                price,
                discount,
                refundable,
                type,
                person,
                owner

        );
    }
    private int getPersonIdByTicketId(int ticketId) throws SQLException {
        int personId;
        PreparedStatement preparedSelectMarineByIdStatement = null;
        try {
            preparedSelectMarineByIdStatement = databaseHandler.getPreparedStatement(SELECT_PERSON_BY_ID, false);
            preparedSelectMarineByIdStatement.setLong(1, ticketId);
            ResultSet resultSet = preparedSelectMarineByIdStatement.executeQuery();
            logger.info("Request SELECT_TICKET_BY_ID is completed.");
            if (resultSet.next()) {
                personId = resultSet.getInt(DatabaseHandler.TICKET_TABLE_PERSON_ID_COLUMN);
            } else throw new SQLException();
        } catch (SQLException exception) {
            logger.error("Mistake occurred while doing the SELECT_TICKET_BY_ID request");
            throw new SQLException(exception);
        } finally {
            databaseHandler.closePreparedStatement(preparedSelectMarineByIdStatement);
        }
        return personId;
    }
    private Coordinates getCoordinatesByTicketId(int ticketId) throws SQLException {
        Coordinates coordinates;
        PreparedStatement preparedSelectCoordinatesByTicketIdStatement = null;
        try {
            preparedSelectCoordinatesByTicketIdStatement =
                    databaseHandler.getPreparedStatement(SELECT_COORDINATES_BY_TICKET_ID, false);
            preparedSelectCoordinatesByTicketIdStatement.setInt(1, ticketId);
            ResultSet resultSet = preparedSelectCoordinatesByTicketIdStatement.executeQuery();
            Console.println("Request SELECT_COORDINATES_BY_TICKET_ID is completed.");
            if (resultSet.next()) {
                coordinates = new Coordinates(
                        resultSet.getFloat(DatabaseHandler.COORDINATES_TABLE_X_COLUMN),
                        resultSet.getFloat(DatabaseHandler.COORDINATES_TABLE_Y_COLUMN)
                );
            } else throw new SQLException();
        } catch (SQLException exception) {
            Console.printerror(exception.getErrorCode());
            Console.printerror(exception.getMessage());
            Console.printerror("Mistake occurred while doing SELECT_COORDINATES_BY_TICKET_ID request");
            throw new SQLException(exception);
        } finally {
            databaseHandler.closePreparedStatement(preparedSelectCoordinatesByTicketIdStatement);
        }
        return coordinates;
    }

    private Person getPersonById(int personId) throws SQLException {
        Person person;
        PreparedStatement preparedSelectPersonByIdStatement = null;
        try {
            preparedSelectPersonByIdStatement =
                    databaseHandler.getPreparedStatement(SELECT_PERSON_BY_ID, false);
            preparedSelectPersonByIdStatement.setInt(1, personId);
            ResultSet resultSet = preparedSelectPersonByIdStatement.executeQuery();
            Console.println("Request SELECT_PERSON_BY_ID is completed.");
            if (resultSet.next()) {
                person = new Person(
                        resultSet.getTimestamp(DatabaseHandler.PERSON_TABLE_DATE_OF_BIRTH_COLUMN).toLocalDateTime(),
                        resultSet.getFloat(DatabaseHandler.PERSON_TABLE_HEIGHT_COLUMN),
                        resultSet.getFloat(DatabaseHandler.PERSON_TABLE_WEIGHT_COLUMN),
                        Country.valueOf(resultSet.getString(DatabaseHandler.PERSON_TABLE_COUNTRY_COLUMN))
                );
            } else throw new SQLException();
        } catch (SQLException exception) {
            Console.printerror(exception.getMessage());
            Console.printerror("Mistake occurred while doing SELECT_TICKET_BY_ID request");
            throw new SQLException(exception);
        } finally {
            databaseHandler.closePreparedStatement(preparedSelectPersonByIdStatement);
        }
        return person;
    }
    public Ticket insertTicket(TicketRaw ticketRaw, User user) throws DatabaseHandlingException {
        Ticket ticket;
        PreparedStatement preparedInsertTicketStatement = null;
        PreparedStatement preparedInsertCoordinatesStatement = null;
        PreparedStatement preparedInsertPersonStatement = null;
        try {
            databaseHandler.setCommitMode();
            databaseHandler.setSavepoint();

            LocalDateTime creationTime = LocalDateTime.now();
            LocalDateTime birthday = LocalDateTime.now();

            preparedInsertTicketStatement = databaseHandler.getPreparedStatement(INSERT_TICKET, true);
            preparedInsertCoordinatesStatement = databaseHandler.getPreparedStatement(INSERT_COORDINATES, true);
            preparedInsertPersonStatement = databaseHandler.getPreparedStatement(INSERT_PERSON, true);

            preparedInsertPersonStatement.setTimestamp(1, Timestamp.valueOf(birthday));
            preparedInsertPersonStatement.setFloat(2, ticketRaw.getPerson().getHeight());
            preparedInsertPersonStatement.setFloat(3, ticketRaw.getPerson().getWeight());
            preparedInsertPersonStatement.setString(4, String.valueOf(ticketRaw.getPerson().getNationality()));
            //preparedInsertPersonStatement.setInt(4, personId);

            if (preparedInsertPersonStatement.executeUpdate() == 0) throw new SQLException();
            ResultSet generatedPersonKeys = preparedInsertPersonStatement.getGeneratedKeys();
            int personId;
            if (generatedPersonKeys.next()) {
                personId = generatedPersonKeys.getInt(4);
            } else throw new SQLException();


            Console.println("Request INSERT_PERSON is completed.");

            preparedInsertTicketStatement.setString(1, ticketRaw.getName());
            preparedInsertTicketStatement.setTimestamp(2, Timestamp.valueOf(creationTime));
            preparedInsertTicketStatement.setDouble(3, ticketRaw.getPrice());
            preparedInsertTicketStatement.setFloat(4, ticketRaw.getDiscount());
            preparedInsertTicketStatement.setBoolean(5, ticketRaw.getRefundable());
            preparedInsertTicketStatement.setString(6, ticketRaw.getType().toString());
            preparedInsertTicketStatement.setLong(7, personId);

            preparedInsertTicketStatement.setInt(8, databaseUserManager.getUserIdByUsername(user));

            if (preparedInsertTicketStatement.executeUpdate() == 0) throw new SQLException();
            ResultSet generatedMarineKeys = preparedInsertTicketStatement.getGeneratedKeys();

            int ticketId;
            if (generatedMarineKeys.next()) {
                ticketId = generatedMarineKeys.getInt(7);
            } else throw new SQLException();
            Console.println("Request INSERT_TICKET is completed.");

            preparedInsertCoordinatesStatement.setInt(1, ticketId);
            preparedInsertCoordinatesStatement.setDouble(2, ticketRaw.getCoordinates().getX());
            preparedInsertCoordinatesStatement.setFloat(3, ticketRaw.getCoordinates().getY());
            if (preparedInsertCoordinatesStatement.executeUpdate() == 0) throw new SQLException();
            Console.println("Request INSERT_COORDINATES is completed.");

            ticket = new Ticket(
                    ticketId,
                    ticketRaw.getName(),
                    ticketRaw.getCoordinates(),
                    creationTime,
                    ticketRaw.getPrice(),
                    ticketRaw.getDiscount(),
                    ticketRaw.getRefundable(),
                    ticketRaw.getType(),
                    ticketRaw.getPerson(),
                    user
            );

            databaseHandler.commit();
            return ticket;
        } catch (SQLException exception) {
            Console.println(exception.getMessage());
            Console.printerror("An error occurred while executing requests to add a new object");
            databaseHandler.rollback();
            throw new DatabaseHandlingException();
        } finally {
            databaseHandler.closePreparedStatement(preparedInsertTicketStatement);
            databaseHandler.closePreparedStatement(preparedInsertCoordinatesStatement);
            databaseHandler.closePreparedStatement(preparedInsertPersonStatement);
            databaseHandler.setNormalMode();
        }
    }


    public NavigableSet<Ticket> getCollection() throws DatabaseHandlingException{
        NavigableSet<Ticket> tickets = new TreeSet<>();
        PreparedStatement preparedStatement = null;

        try {
            preparedStatement = databaseHandler.getPreparedStatement(SELECT_ALL_TICKETS, false);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()){
                tickets.add(createTicket(resultSet));
            }
        }catch (SQLException E){
            throw new DatabaseHandlingException();
        }finally {
            databaseHandler.closePreparedStatement(preparedStatement);
        }
        return tickets;
    }
    public void updateTicketById(int ticketId, TicketRaw ticketRaw) throws DatabaseHandlingException {
        PreparedStatement preparedUpdateTicketNameByIdStatement = null;
        PreparedStatement preparedUpdateTicketCoordinatesByIdStatement = null;
        PreparedStatement preparedUpdateTicketPriceByIdStatement = null;
        PreparedStatement preparedUpdateTicketDiscountByIdStatement = null;
        PreparedStatement preparedUpdateTicketRefundableByIdStatement = null;
        PreparedStatement preparedUpdateTicketTypeByIdStatement = null;
        PreparedStatement preparedUpdateTicketPersonByIdStatement = null;



        try {
            databaseHandler.setCommitMode();
            databaseHandler.setSavepoint();

            preparedUpdateTicketNameByIdStatement = databaseHandler.getPreparedStatement(UPDATE_TICKET_NAME_BY_ID, false);
            preparedUpdateTicketCoordinatesByIdStatement = databaseHandler.getPreparedStatement(UPDATE_COORDINATES_BY_TICKET_ID, false);
            preparedUpdateTicketPriceByIdStatement = databaseHandler.getPreparedStatement(UPDATE_TICKET_PRICE_BY_ID, false);
            preparedUpdateTicketDiscountByIdStatement = databaseHandler.getPreparedStatement(UPDATE_TICKET_DISCOUNT_PRICE_BY_ID, false);
            preparedUpdateTicketRefundableByIdStatement = databaseHandler.getPreparedStatement(UPDATE_TICKET_REFUND_PRICE_BY_ID, false);
            preparedUpdateTicketTypeByIdStatement = databaseHandler.getPreparedStatement(UPDATE_TICKET_TICKET_TYPE_BY_ID, false);
            preparedUpdateTicketPersonByIdStatement = databaseHandler.getPreparedStatement(UPDATE_TICKET_PERSON_BY_ID, false);



            if (ticketRaw.getName() != null) {
                preparedUpdateTicketNameByIdStatement.setString(1, ticketRaw.getName());
                preparedUpdateTicketNameByIdStatement.setInt(2, ticketId);
                if (preparedUpdateTicketNameByIdStatement.executeUpdate() == 0) throw new SQLException();
                logger.info("Request completed: UPDATE_TICKET_NAME_BY_ID.");
            }
            if (ticketRaw.getCoordinates() != null) {
                preparedUpdateTicketCoordinatesByIdStatement.setFloat(1, ticketRaw.getCoordinates().getX());
                preparedUpdateTicketCoordinatesByIdStatement.setFloat(2, ticketRaw.getCoordinates().getY());
                preparedUpdateTicketCoordinatesByIdStatement.setInt(3, ticketId);
                if (preparedUpdateTicketCoordinatesByIdStatement.executeUpdate() == 0) throw new SQLException();
                logger.info("Request completed: UPDATE_COORDINATES_BY_TICKET_ID.");
            }
            if (ticketRaw.getPrice() != -1) {
                preparedUpdateTicketPriceByIdStatement.setDouble(1, ticketRaw.getPrice());
                preparedUpdateTicketPriceByIdStatement.setInt(2, ticketId);
                if (preparedUpdateTicketPriceByIdStatement.executeUpdate() == 0) throw new SQLException();
                logger.info("Request completed: UPDATE_TICKET_PRICE_BY_ID.");
            }

            if (ticketRaw.getDiscount() != -1) {
                preparedUpdateTicketDiscountByIdStatement.setDouble(1, ticketRaw.getDiscount());
                preparedUpdateTicketDiscountByIdStatement.setInt(2, ticketId);
                if (preparedUpdateTicketDiscountByIdStatement.executeUpdate() == 0) throw new SQLException();
                logger.info("Request completed: UPDATE_TICKET_DISCOUNT_BY_ID.");
            }

            if (ticketRaw.getRefundable() != null) {
                preparedUpdateTicketRefundableByIdStatement.setString(1, String.valueOf(ticketRaw.getRefundable()));
                preparedUpdateTicketRefundableByIdStatement.setInt(2, ticketId);
                if (preparedUpdateTicketRefundableByIdStatement.executeUpdate() == 0) throw new SQLException();
                logger.info("Request completed: UPDATE_TICKET_REFUNDABLE_BY_ID.");
            }
            if (ticketRaw.getType() != null) {
                preparedUpdateTicketTypeByIdStatement.setString(1, ticketRaw.getType().toString());
                preparedUpdateTicketTypeByIdStatement.setInt(2, ticketId);
                if (preparedUpdateTicketTypeByIdStatement.executeUpdate() == 0) throw new SQLException();
                logger.info("Request completed: UPDATE_TICKET_TYPE_BY_ID.");
            }
            if (ticketRaw.getPerson() != null) {
                preparedUpdateTicketPersonByIdStatement.setString(1, String.valueOf(ticketRaw.getPerson().getBirthday()));
                preparedUpdateTicketPersonByIdStatement.setFloat(2, ticketRaw.getPerson().getHeight());
                preparedUpdateTicketPersonByIdStatement.setFloat(3, ticketRaw.getPerson().getWeight());
                preparedUpdateTicketPersonByIdStatement.setString(4, String.valueOf(ticketRaw.getPerson().getNationality()));
                preparedUpdateTicketPersonByIdStatement.setLong(5, getPersonIdByTicketId(ticketId));
                if (preparedUpdateTicketPersonByIdStatement.executeUpdate() == 0) throw new SQLException();
                logger.info("Request completed: UPDATE_PERSON_BY_ID.");
            }

            databaseHandler.commit();
        } catch (SQLException exception) {
            logger.error("Mistake occurred while trying to update data");
            databaseHandler.rollback();
            throw new DatabaseHandlingException();
        } finally {
            databaseHandler.closePreparedStatement(preparedUpdateTicketNameByIdStatement);
            databaseHandler.closePreparedStatement(preparedUpdateTicketCoordinatesByIdStatement);
            databaseHandler.closePreparedStatement(preparedUpdateTicketPriceByIdStatement);
            databaseHandler.closePreparedStatement(preparedUpdateTicketDiscountByIdStatement);
            databaseHandler.closePreparedStatement(preparedUpdateTicketRefundableByIdStatement);
            databaseHandler.closePreparedStatement(preparedUpdateTicketTypeByIdStatement);
            databaseHandler.closePreparedStatement(preparedUpdateTicketPersonByIdStatement);
            databaseHandler.setNormalMode();
        }
    }

    public void deleteTicketById(int ticketId) throws DatabaseHandlingException {
        PreparedStatement preparedDeleteChapterByIdStatement = null;
        try {
            preparedDeleteChapterByIdStatement = databaseHandler.getPreparedStatement(DELETE_TICKET_BY_ID, false);
            preparedDeleteChapterByIdStatement.setLong(1, getPersonIdByTicketId(ticketId));
            if (preparedDeleteChapterByIdStatement.executeUpdate() == 0) Console.println(3);
            logger.info("Request completed: DELETE_TICKET_BY_ID.");
        } catch (SQLException exception) {
            logger.error("Mistake occurred while doing request: DELETE_TICKET_BY_ID!");
            throw new DatabaseHandlingException();
        } finally {
            databaseHandler.closePreparedStatement(preparedDeleteChapterByIdStatement);
        }
    }


    public boolean checkTicketUserId(int ticketId, User user) throws DatabaseHandlingException {
        PreparedStatement preparedSelectTicketByIdAndUserIdStatement = null;
        try {
            preparedSelectTicketByIdAndUserIdStatement = databaseHandler.getPreparedStatement(SELECT_TICKET_BY_ID_AND_USER_ID, false);
            preparedSelectTicketByIdAndUserIdStatement.setInt(1, ticketId);
            preparedSelectTicketByIdAndUserIdStatement.setLong(2, databaseUserManager.getUserIdByUsername(user));
            ResultSet resultSet = preparedSelectTicketByIdAndUserIdStatement.executeQuery();
            logger.info("Request completed: SELECT_TICKET_BY_ID_AND_USER_ID.");
            return resultSet.next();
        } catch (SQLException exception) {
            logger.error("Mistake occurred while doing request: SELECT_MARINE_BY_ID_AND_USER_ID!");
            throw new DatabaseHandlingException();
        } finally {
            databaseHandler.closePreparedStatement(preparedSelectTicketByIdAndUserIdStatement);
        }
    }

    /**
     * Clear the collection.
     *
     * @throws DatabaseHandlingException When there's exception inside.
     */
    public void clearCollection() throws DatabaseHandlingException {
        NavigableSet<Ticket> tickets = getCollection();
        for (Ticket ticket : tickets) {
            deleteTicketById(ticket.getId());
        }
    }

}