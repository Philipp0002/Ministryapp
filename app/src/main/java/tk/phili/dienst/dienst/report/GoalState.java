package tk.phili.dienst.dienst.report;

import lombok.Data;

@Data
public class GoalState {

    private boolean hasGoal;
    private String localizedGoalText;
    private float progressPercent;

}
