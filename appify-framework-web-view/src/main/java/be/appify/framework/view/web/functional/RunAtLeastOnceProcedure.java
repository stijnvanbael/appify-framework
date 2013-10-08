package be.appify.framework.view.web.functional;

import be.appify.framework.functional.RepeatableProcedure;
import be.appify.framework.view.web.annotation.Parameter;

import javax.validation.constraints.Min;

public abstract class RunAtLeastOnceProcedure implements RepeatableProcedure {

    public RunAtLeastOnceProcedure() {
        run();
    }

    @Override
    public final void times(@Parameter(defaultValue = "1") @Min(1) int numberOfRuns) {
        if(numberOfRuns < 1) {
            throw new IllegalArgumentException("Cannot run less than once");
        }
        int i = 0;
        // we ran once already, so subtract one
        while(i < numberOfRuns - 1) {
            i++;
            run();
        }
    }
}
