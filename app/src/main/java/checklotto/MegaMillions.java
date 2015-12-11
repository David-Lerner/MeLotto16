package checklotto;


import java.util.ArrayList;
import java.util.HashSet;
import java.util.Scanner;


/**
 * Class to handle checking the MEGA MILLIONS lottery.
 *
 * @author David Lerner
 */
public class MegaMillions extends Lottery
{
    public static final String MESSAGE = "Mega Millions draws take place every Tuesday and Friday after the draw entry closes at 7:45 p.m.";
    private static final String dataURL = "http://www.calottery.com/sitecore/content/Miscellaneous/download-numbers/?GameName=mega-millions&Order=No";
    private static final String refererURL = "http://www.calottery.com/play/draw-games/mega-millions/winning-numbers";
    private ArrayList<HashSet<Integer>> numbers;
    private ArrayList<Integer> megaMillion;

    public MegaMillions() throws Exception
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
        megaMillion = new ArrayList<>();
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
            HashSet<Integer> set = new HashSet<>();
            for (int i = 0; i < 5; i++)
                set.add(s.nextInt());
            numbers.add(0, set);
            megaMillion.add(0, s.nextInt());
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
     * @param a[5] n5
     * @param a[6] megaMillion number
     * @return messages that explain the results
     */
    @Override
    public String[] CheckIfWon (int a[])
    {
        String[] message = new String[3];
        if (a.length != 7)
            throw new IllegalArgumentException();
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
            int matches = 0;
            for (int i = 1; i <= 5; i++)
            {
                if (numbers.get(index).contains(a[i]))
                    matches++;
            }
            message[1] = "You matched " + matches + " out of 5 numbers correctly";
            if (megaMillion.get(index) == a[6])
                message[1] += "\nYou matched the MEGA number!";
            else
                message[1] += "\nYou did not match the MEGA number";
            message[0] = getPrizeMoney(index, matches, (megaMillion.get(index) == a[6]));
            
            /*System.out.println("http://www.calottery.com/play/draw-games/megaMillion/winning-numbers/?DrawDate=" 
                    + drawMonth.get(drawNumber - 1) + "%20" + drawDay.get(drawNumber - 1) + ",%20" 
                    + drawYear.get(drawNumber - 1) + "&DrawNumber=" + drawNumber);*/
        }

        message[2] = "For more information, check out the California lottery website at " + refererURL + "\n" + lastUpdate;
        return message;
    }

    private String getPrizeMoney(int index, int matches, boolean megaMillionMatches)
    {
        try
        {
            String temp = ReadFromWeb.readWithReferer(refererURL + "/?DrawDate="
                    + drawMonth.get(index) + "%20" + drawDay.get(index) + ",%20"
                    + drawYear.get(index) + "&DrawNumber=" + (index + firstdraw), refererURL);

            int position = temp.indexOf("5 + MEGA");
            if (position == -1)
                throw new Exception();
            String[] a = new String[12];
            for (int i = 0; i < 9; i++)
            {
                position = temp.indexOf("$", position);
                a[i] = temp.substring(position, temp.indexOf("<", position));
                position++;
            }
            a[10] = a[8];
            a[8] = a[7];
            a[7] = "$0";
            a[9] = "$0";
            a[11] = "$0";

            int numb = megaMillionMatches ? 0 : 1;
            return a[10 - (matches * 2) + numb];
        }
        catch (Exception e)
        {
            return "Reading web failed.";
        }
    }
}
