package be.appify.framework.functional;

public abstract class RunAtLeastOnceRepeatableProcedure implements RepeatableProcedure {

    public RunAtLeastOnceRepeatableProcedure() {
        run();
    }

    @Override
    public final void times(int numberOfRuns) {
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
