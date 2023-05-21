package utility;

import java.io.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import exceptions.DatabaseHandlingException;
import models.*;

import javax.xml.crypto.Data;

/*
TODO do loggers here and make it look normal, translate everything in english, check everything on trash
 */

/**
 * Operates the collection.
 */

public class CollectionManager {

    private NavigableSet<Ticket> collection;
    private DatabaseCollectionHandler databaseCollectionHandler;

    public CollectionManager(DatabaseCollectionHandler databaseCollectionHandler) throws IOException {
        this.databaseCollectionHandler = databaseCollectionHandler;
        loadCollection();
    }

    public NavigableSet<Ticket> getCollection(){
        return collection;
    }
    public void clearCollection() {
        collection.clear();
    }

    /**
     * @return Size of the collection.
     */
    public int collectionSize(){
        return collection.size();
    }

    /**
     * @return The first element of the collection or null if collection is empty.
     */
   public Ticket getFirst(){
        return collection.stream().findFirst().orElse(null);
    }


    /**
     * @return A ticket by his ID or null if ticket isn't found.
     */
    public Ticket getById(int id) {
        for (Ticket element : collection) {
            if (element.getId() == id) return element;
        }
        return null;
    }

    /**
     * @param ticket A ticket which value will be found.
     * @return A marine by his value or null if marine isn't found.
     */
    public Ticket getByValue(Ticket ticket){
        for (Ticket tickets : collection){
            if (tickets.equals(ticket)) return tickets;
        }
        return null;
    }

    /**
     * Removes greater.
     */
    public void removeGreater(Ticket ticketToCompare) {
        collection.removeIf(ticket -> ticket.compareTo(ticketToCompare) > 0);
    }

    /**
     * Adds a new ticket to collection.
     * @param element A ticket to add.
     */
    public void addToCollection(Ticket element) {
        collection.add(element);
    }

    /**
     * Removes a new ticket to collection.
     * @param element A marine to remove.
     */
    public void removeFromCollection(Ticket element) {
        collection.remove(element);
    }

    /**
     * Exits the program
     */
    public void exit() throws IOException {
        Console.println("Work of program is ended");
        System.exit(0);

    }

    /**
     * Loads the collection from file.
     */
    public void loadCollection() throws IOException {
        try{
            collection = databaseCollectionHandler.getCollection();
            Console.println("Collection is loaded");


        }catch (DatabaseHandlingException exception) {
            collection = new TreeSet<>();
            Console.printerror("Коллекция не может быть загружена!");
            //logger.error("Коллекция не может быть загружена!");
        }
    }

    /**
     * Counting group by its creation date
     */
    public void groupCountingByCrDate(){
        HashMap<LocalDateTime, TreeSet<Ticket>> groupMap = new HashMap<>();
        for (Ticket i : collection){
            if (groupMap.get((i).getCreationDate()) == null){
                TreeSet<Ticket> x = new TreeSet<>();
                x.add(i);
                groupMap.put((i).getCreationDate(), x);
            } else groupMap.get((i).getCreationDate()).add(i);
        }
        for (Map.Entry<LocalDateTime, TreeSet<Ticket>> entry : groupMap.entrySet()){
            Console.println("Elements created in " + entry.getKey() + " :\n");
            entry.getValue().forEach(CollectionManager::display);
        }
    }

    /**
     * Display the info about created ticket
     */
    static void display(Ticket ticket) {
        Console.println("ID of ticket:" + ticket.getId());
        Console.println("Name of ticket:" + ticket.getName());
        Console.println("Creation date of ticket:" + ticket.getCreationDate());
        Console.println("Coordinates:" + ticket.getCoordinates());
        Console.println("Price:" + ticket.getPrice());
        Console.println("Discount:" + ticket.getDiscount());
        Console.println("Refund:" + ticket.getRefundable());
        Console.println("Type of ticket:" + ticket.getType());
        Console.println("Human's info:" + ticket.getPerson());
    }

}
