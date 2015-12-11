package checklotto;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;


/**
 *
 * Abstract class implemented by the lotteries.
 *
 * @author David Lerner
 */
public abstract class Lottery
{
    static String MESSAGE;
    protected String lastUpdate;
    protected ArrayList<String> drawMonth;
    protected ArrayList<String> drawDay;
    protected ArrayList<String> drawYear;
    protected int firstdraw;
    protected HashMap<String, Integer> months;

    public Lottery()
    {
        months = new HashMap<>();
        months.put("Jan", 1);
        months.put("Feb", 2);
        months.put("Mar", 3);
        months.put("Apr", 4);
        months.put("May", 5);
        months.put("Jun", 6);
        months.put("Jul", 7);
        months.put("Aug", 8);
        months.put("Sep", 9);
        months.put("Oct", 10);
        months.put("Nov", 11);
        months.put("Dec", 12);
    }

    /**
     *
     * @param a an array of integers that list the draw number and lotto numbers
     * @return message that explains the results
     */
    public abstract String[] CheckIfWon (int a[]);

    /**
     *
     * @param drawNumber the draw number of the ticket
     * @return whether the ticket already had its draw date
     */
    public boolean wasDrawn(int drawNumber)
    {
        return drawNumber <= drawDay.size() - 1 + firstdraw;
    }

    /**
     *
     * @param m the month as an integer from 1 to 12
     * @return the String value of the month (Jan, Feb, etc.)
     */
    public String getMonthByNumber (int m) {
        Set<String> keys =  months.keySet();
        for (String s : keys)
        {
            if (months.get(s) == m)
                return s;
        }
        return null;
    }

    /**
     *
     * @param day
     * @param month
     * @param year
     * @return draw number of the corresponding date
     */
    public int getDrawNumberByDate(String day, String month, String year)
    {
        if (!months.containsKey(month))
            return -1;
        int monthInt = months.get(month);
        int dayInt;
        int yearInt;
        try
        {
            dayInt = Integer.parseInt(day);
            yearInt = Integer.parseInt(year);
        }
        catch (Exception e)
        {
            return -1;
        }
        if (dayInt < 1 || dayInt > 31 || drawDay.isEmpty())
            return -1;
        for (int i = drawDay.size() - 1; i >= 0; i--)
        {
            int cmp = compareDates(dayInt, monthInt, yearInt, i);
            if (cmp == 0)
                return i + firstdraw;
            else if (cmp > 0 && i == drawDay.size() - 1)
                return 1 + i + firstdraw;
            else if (cmp > 0)
                return -1;
        }
        return -1;
    }

    private int compareDates(int day, int month, int year, int i)
    {
        int Dyear = Integer.parseInt(drawYear.get(i));
        int Dmonth = months.get(drawMonth.get(i));
        int Dday = Integer.parseInt(drawDay.get(i));
        if (year > Dyear)
            return 1;
        else if (year < Dyear)
            return -1;
        else if (month > Dmonth)
            return 1;
        else if (month < Dmonth)
            return -1;
        else if (day > Dday)
            return 1;
        else if (day < Dday)
            return -1;
        return 0;
    }
}
