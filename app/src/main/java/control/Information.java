package control;

import java.util.ArrayList;
import java.util.HashMap;

import checklotto.Lottery;

/**
 * Created by David on 12/8/2015.
 */
public class Information {
    private static Information instance = null;
    private Lottery lotto;
    public Ticket currentTicket;
    private HashMap<String, Integer> lotteryTypes;
    public String photoFolderId;
    public boolean updated;
    public ArrayList<Ticket> tickets;

    protected Information() {
        // Exists only to defeat instantiation.
        lotteryTypes = new HashMap<>();
        lotteryTypes.put("POWERBALL", 0);
        lotteryTypes.put("MEGAMillions", 1);
        lotteryTypes.put("SuperLottoPlus", 2);
        lotteryTypes.put("Fantasy5", 3);
        lotteryTypes.put("Daily4", 4);
        lotteryTypes.put("Daily3", 5);
        photoFolderId = null;
        updated = false;
    }

    public static Information getInstance() {
        if(instance == null) {
            instance = new Information();
        }
        return instance;
    }

    public Lottery getLotto() {
        return lotto;
    }

    public void setLotto(Lottery lotto) {
        this.lotto = lotto;
    }

    public int getLotteryIndex(String key) {
        return lotteryTypes.get(key);
    }
}