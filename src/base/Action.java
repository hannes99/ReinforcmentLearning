package base;

public class Action implements Comparable {

    int x, y;
    private double reward;

    public Action(double reward, int x, int y) {
        this.x = x;
        this.y = y;
        this.reward = reward;
    }

    public double getReward() {
        return reward;
    }

    @Override
    public int compareTo(Object o) {
        System.out.println("COMP");
        double otherV = ((Action) o).getReward();
        if (otherV == reward)
            return 0;
        return reward > otherV ? -1 : 1;
    }

    @Override
    public String toString() {
        return "Action[" + x + "|" + y + "](" + String.format("%.2f", reward) + ")";
    }
}
