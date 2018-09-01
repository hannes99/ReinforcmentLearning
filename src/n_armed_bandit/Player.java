package n_armed_bandit;

import base.Action;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.Set;

public class Player {
    private final double e = 1.0;
    private HashMap<Action, Double> estemetedReward;
    private HashMap<Action, ArrayList<Double>> prevRewards;
    private Environment env;
    private int counter = 0;

    public Player() {
        env = new Environment();
        //System.out.println(env);
        estemetedReward = new HashMap<>();
        prevRewards = new HashMap<>();
    }

    public void printMaps(Set<Action> taken, Double avg) {
        String ret = "";
        String csv = "";
        if (counter == 0) {
            csv += "======\n";
            ret += "<script src=\"https://ajax.googleapis.com/ajax/libs/jquery/3.3.1/jquery.min.js\"></script>\n";
            ret += "<script>function g() {\n" +
                    "    for (let i = 0;i<100;i++) {\t\t\n" +
                    "\t\t$('#'+i).delay(i*1000).fadeIn(0).delay(990).fadeOut(0)\n" +
                    "    }\n" +
                    "}</script>";
        }
        csv += avg + "";
        ret += "<div calss=\"step\" id=" + counter + " style=\"display: none\"><h1>ESTIMATES</h1><br><h2>" + String.format("%.2f", avg) + "</h2>\n<table border=\"1\">";
        for (ArrayList<Action> play : env.getField()) {
            ret += "<tr>\n";
            for (Action a : play) {
                String bc = !taken.contains(a) ? "rgb(255, 255, 255)" : "rgb(0,255,0)";
                String v = estemetedReward.getOrDefault(a, -1.0) == -1 ? "?" : String.format("%.2f", estemetedReward.get(a));
                ret += "<td style=\"background-color: " + bc + "\">" + v + "</td>\n";
            }
            ret += "</tr>\n";
        }
        ret += "\n</table></div>\n";
        try (FileWriter fw = new FileWriter("log.html", true);
             BufferedWriter bw = new BufferedWriter(fw);
             PrintWriter out = new PrintWriter(bw)) {
            out.println(ret);
        } catch (IOException e) {
            //exception handling left as an exercise for the reader
        }
        try (FileWriter fw = new FileWriter("log.csv", true);
             BufferedWriter bw = new BufferedWriter(fw);
             PrintWriter out = new PrintWriter(bw)) {
            out.println(csv);
        } catch (IOException e) {
            //exception handling left as an exercise for the reader
        }
        //System.out.println(ret);
        counter++;
    }

    public void startPlay() {
        //for (int i = 0;i<10000;i++){
        while (true) {
            Random r = new Random();
            HashMap<Action, Double> actionsTaken = new HashMap<>();
            ArrayList<Action> availableInPlay;
            ArrayList<Action> available;
            for (int o = 0; o < Environment.plays; o++) {
                availableInPlay = env.getActions();
                available = (ArrayList<Action>) availableInPlay.clone();
                available.retainAll(estemetedReward.keySet());
                availableInPlay.removeAll(available);
                if (availableInPlay.size() > 0 && (available.size() < 2 || e * 10 >= r.nextInt(100))) {
                    Action selection = availableInPlay.get(Math.toIntExact(System.currentTimeMillis() % (availableInPlay.size())));
                    actionsTaken.put(selection, selection.getReward());
                } else {
                    Action selection = available.get(0);
                    for (Action a : available) {
                        selection = estemetedReward.get(a) > estemetedReward.get(selection) ? a : selection;
                    }
                    actionsTaken.put(selection, selection.getReward());
                }
            }
            double totalReward = 0;
            for (Action actionTaken : actionsTaken.keySet()) {
                totalReward += actionsTaken.get(actionTaken);
            }
            double rewardPerAction = totalReward / Environment.plays;
            for (Action actionTaken : actionsTaken.keySet()) {
                prevRewards.computeIfAbsent(actionTaken, k -> new ArrayList<>());
                prevRewards.get(actionTaken).add(rewardPerAction);
                estemetedReward.put(actionTaken, prevRewards.get(actionTaken).stream().mapToDouble(a -> a).sum() / prevRewards.get(actionTaken).size());
            }
            env.reset();
            printMaps(actionsTaken.keySet(), rewardPerAction);
            System.out.println("Reward per action: " + rewardPerAction);
        }
    }
}
