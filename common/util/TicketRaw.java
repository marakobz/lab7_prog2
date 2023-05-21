package util;

import models.Coordinates;
import models.Person;
import models.Ticket;
import models.TicketType;

import java.io.Serializable;
import java.util.Objects;

/**
 * Class for get Ticket's value.
 */
public class TicketRaw  implements Serializable {
    private String name;
    private Coordinates coordinates;
    private int price;
    private long discount;
    private Boolean refundable;
    private TicketType type;
    private Person person;

    public TicketRaw(
                  String name,
                  Coordinates coordinates,
                  int price,
                  long discount,
                  Boolean refundable,
                  TicketType type,
                  Person person) {
        this.name = name;
        this.coordinates = coordinates;
        this.price = price;
        this.discount = discount;
        this.refundable = refundable;
        this.type = type;
        this.person = person;
    }



    public String getName() {
        return name;
    }

    public Coordinates getCoordinates() {
        return coordinates;
    }


    public int getPrice() {
        return price;
    }

    public long getDiscount() {
        return discount;
    }

    public Boolean getRefundable() {
        return refundable;
    }

    public TicketType getType() {
        return type;
    }

    public Person getPerson() {
        return person;
    }


    @Override
    public String toString() {
        String info = "";
        info += "\n Name: " + name;
        info += "\n Place: " + coordinates;
        info += "\n Price: " + price;
        info += "\n Discount: " + discount;
        info += "\n Refund: " + refundable;
        info += "\n Ticket type: " + type;
        info += "\n Person: " + person;
        return info;
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName(), getCoordinates(), getPrice(), getDiscount(), getRefundable(), getType(), getPerson());
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj instanceof Ticket) {
            Ticket ticketObj = (Ticket) obj;
            return (
                    name.equals(ticketObj.getName()) &&
                            coordinates.equals(ticketObj.getCoordinates()) &&
                            (price == ticketObj.getPrice()) &&
                            refundable.equals(ticketObj.getRefundable()) &&
                            type.equals(ticketObj.getType()) &&
                            person.equals(ticketObj.getPerson()));
        }
        return false;
    }

}
