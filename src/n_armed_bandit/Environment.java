package n_armed_bandit;

import base.Action;

import java.util.ArrayList;
import java.util.Random;

public class Environment {
    public static final int n = 15;
    public static final int plays = 15;

    private ArrayList<ArrayList<Action>> field = new ArrayList<>();
    private int currentPlay;

    public Environment() {
        currentPlay = 0;
        for (int i = 0; i < plays; i++) {
            ArrayList<Action> moment = new ArrayList<>();
            Random r = new Random();
            for (int o = 0; o < n; o++) {
                moment.add(new Action(0.1 + r.nextDouble() * (20.0 - 0.1), o, i));
            }
            field.add(moment);
        }
    }

    public ArrayList<ArrayList<Action>> getField() {
        return field;
    }


    public void reset() {
        currentPlay = 0;
    }

    public ArrayList<Action> getActions() {
        return (ArrayList<Action>) field.get(currentPlay++).clone();
    }

    @Override
    public String toString() {
        String ret = "";
        for (ArrayList<Action> p : field) {
            for (Action a : p) {
                ret += "  " + String.format("%.2f", a.getReward());
            }
            ret += "\n";
        }
        return ret;
    }
}
