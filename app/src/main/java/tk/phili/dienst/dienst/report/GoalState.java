package tk.phili.dienst.dienst.report;

public class GoalState {

    private boolean hasGoal;
    private String localizedGoalText;
    private float progressPercent;

    public boolean hasGoal() {
        return hasGoal;
    }

    public void setHasGoal(boolean hasGoal) {
        this.hasGoal = hasGoal;
    }

    public String getLocalizedGoalText() {
        return localizedGoalText;
    }

    public void setLocalizedGoalText(String localizedGoalText) {
        this.localizedGoalText = localizedGoalText;
    }

    public float getProgressPercent() {
        return progressPercent;
    }

    public void setProgressPercent(float progressPercent) {
        this.progressPercent = progressPercent;
    }
}
