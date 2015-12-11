package control;

import java.util.ArrayList;
import java.util.Scanner;

/**
 * Created by David on 12/9/2015.
 */
public class Storager {
    public static String TicketToText (Ticket t)
    {
        StringBuilder sb = new StringBuilder();
        sb.append(t.ticketType);
        sb.append(" ");
        sb.append(t.drawNumber);
        sb.append(" ");
        sb.append(t.drawDay);
        sb.append(" ");
        sb.append(t.drawMonth);
        sb.append(" ");
        sb.append(t.drawYear);
        sb.append(" ");
        sb.append(t.playType);
        sb.append(" ");
        sb.append(t.draws);
        sb.append(" ");
        for (int i : t.specialNumber) {
            sb.append(i);
            sb.append(" ");
        }
        for (boolean b : t.isDrawn) {
            sb.append(b);
            sb.append(" ");
        }
        for (ArrayList<Integer> a : t.numbers)
        {
            for (Integer i : a)
            {
                sb.append(i);
                sb.append(" ");
            }
        }
        for (String s : t.amountWon) {
            sb.append(s);
            sb.append(" ");
        }
        for (String s : t.imageLink) {
            sb.append(s);
            sb.append(" ");
        }
        sb.append(System.getProperty("line.separator"));
        return sb.toString();
    }

    public static Ticket TextToTicket (String s)
    {
        Scanner in = new Scanner(s);
        Ticket t = new Ticket(in.next());
        t.drawNumber = in.nextInt();
        t.drawDay = in.next();
        t.drawMonth = in.next();
        t.drawYear = in.next();
        t.playType = in.nextInt();
        t.draws = in.nextInt();
        t.specialNumber = new int[t.draws];
        for (int i = 0; i < t.draws; i++) {
            t.specialNumber[i] = in.nextInt();
        }
        t.isDrawn = new boolean[t.draws];
        for (int i = 0; i < t.draws; i++) {
            t.isDrawn[i] = in.nextBoolean();
        }
        ArrayList<Integer> a = new ArrayList<>();
        while (in.hasNextInt())
            a.add(in.nextInt());
        int n = a.size() / t.draws;
        t.numbers = new ArrayList[t.draws];
        for (int i = 0; i < t.draws; i++) {
            t.numbers[i] = new ArrayList<>();
            for (int j = 0; j < n; j++) {
                t.numbers[i].add(a.remove(0));
            }
        }
        t.amountWon = new String[t.draws];
        for (int i = 0; i < t.draws; i++) {
            t.amountWon[i] = in.next();
        }
        t.imageLink = new ArrayList<>();
        while (in.hasNext())
        {
            t.imageLink.add(in.next());
        }
        t.valid = true;
        return t;
    }
}
