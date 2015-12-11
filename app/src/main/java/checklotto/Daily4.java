package checklotto;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;


/**
 * Class to handle checking the Daily 4 lottery.
 *
 * @author David Lerner
 */
public class Daily4 extends Lottery
{
    public static final String MESSAGE = "Daily 4 draws take place every day after the draw entry closes at 6:30 p.m.";
    private static final String dataURL = "http://www.calottery.com/sitecore/content/Miscellaneous/download-numbers/?GameName=daily-4&Order=No";
    private static final String refererURL = "http://www.calottery.com/play/draw-games/daily-4/winning-numbers";
    private ArrayList<ArrayList<Integer>> numbers;
    private int style;

    public Daily4() throws Exception
    {
        super();
        String data = ReadFromWeb.read(dataURL);
        //System.out.println(data);
        parseText(data);
    }

    private void parseText(String textInput)
    {
        lastUpdate = "No Data Found";
        drawMonth = new ArrayList<>();
        drawDay = new ArrayList<>();
        drawYear = new ArrayList<>();
        numbers = new ArrayList<>();
        Scanner s = new Scanner(textInput);
        if (s.next().compareTo("Download") != 0)
            return;
        s.next();
        lastUpdate = s.nextLine().trim();
        for (int i = 0; i < 4; i++)
            s.nextLine();
        while (s.hasNextLine())
        {
            firstdraw = s.nextInt();
            s.next();
            drawMonth.add(0, s.next());
            drawDay.add(0, s.next().substring(0, 2));
            drawYear.add(0, s.next());
            ArrayList<Integer> list = new ArrayList<>();
            for (int i = 0; i < 4; i++)
                list.add(s.nextInt());
            numbers.add(0, list);
            s.nextLine();
        }
    }

    /**
     * Checks if the given numbers match the drawn numbers and prints the results
     *
     * @param a[0] draw number
     * @param a[1] n1
     * @param a[2] n2
     * @param a[3] n3
     * @param a[4] n4
     * @param a[5] style (0 = Straight, 1 = Box, 2 = Straight/Box)
     * @return messages that explain the results
     */
    @Override
    public String[] CheckIfWon (int a[])
    {
        String[] message = new String[3];
        if (a.length != 6)
            throw new IllegalArgumentException();
        style = a[5];
        int index = a[0] - firstdraw;
        if (index >= numbers.size())
        {
            message[0] = "N/A";
            message[1] = "Not drawn yet. " + MESSAGE;
        }
        else if (index < 0)
        {
            message[0] = "N/A";
            message[1] = "This drawing predates available records";
        }
        else
        {
            int matches = 1;
            int[] temp1 = new int[4];
            int[] temp2 = new int[4];
            for (int i = 0; i < 4; i++)
            {
                temp1[i] = numbers.get(index).get(i);
                temp2[i] = a[i + 1];
                if (temp1[i] != temp2[i])
                    matches = 0;
            }
            Arrays.sort(temp1);
            Arrays.sort(temp2);
            matches++;
            for (int i = 0; i < 4; i++)
            {
                if (temp1[i] != temp2[i])
                {
                    matches--;
                    break;
                }
            }

            if (style == 0)
                message[1] = "Playing Straight style, ";
            else if (style == 1)
                message[1] = "Playing Box style, ";
            else
                message[1] = "Playing Straight/Box  style, ";

            if (matches == 2)
                message[1] += "you matched all the numbers in the same order";
            else if (matches == 1)
                message[1] += "you matched all the numbers in a different order";
            else
                message[1] += "you did not match all the numbers";

            message[0] = getPrizeMoney(index, matches);
        }

        message[2] = "For more information, check out the California lottery website at " + refererURL + "\n" + lastUpdate;
        return message;
    }

    private String getPrizeMoney(int index, int matches)
    {
        try
        {
            String temp = ReadFromWeb.readWithReferer(refererURL + "/?DrawDate="
                    + drawMonth.get(index) + "%20" + drawDay.get(index) + ",%20"
                    + drawYear.get(index) + "&DrawNumber=" + (firstdraw + index), refererURL);

            int position = temp.indexOf("Straight");
            if (position == -1)
                throw new Exception();
            String[] a = new String[5];
            for (int i = 0; i < 4; i++)
            {
                position = temp.indexOf("$", position);
                a[i] = temp.substring(position, temp.indexOf("<", position));
                position++;
            }
            a[4] = "$0";

            if ((style == 0 && matches == 2) || style == 2)
                return a[style + 2 - matches];
            if (style == 1 && matches > 0)
                return a[1];
            return a[4];
        }
        catch (Exception e)
        {
            return "Reading web failed.";
        }
    }
}
