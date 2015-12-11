package control;

import java.util.ArrayList;

/**
 * Created by David on 12/8/2015.
 */
public class Ticket
{
    public String ticketType;
    public int drawNumber;
    public String drawDay;
    public String drawMonth;
    public String drawYear;
    public int playType;
    public int draws;
    public int[] specialNumber;
    public boolean[] isDrawn;
    public ArrayList<Integer>[] numbers;
    public String[] amountWon;
    public ArrayList<String> imageLink;

    public boolean valid;

    public Ticket (String ticketType)
    {
        this.ticketType = ticketType;
        playType = 0;
        imageLink = new ArrayList<>();
        valid = false;
    }
}
