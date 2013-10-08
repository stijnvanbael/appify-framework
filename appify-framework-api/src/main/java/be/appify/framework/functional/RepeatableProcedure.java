package be.appify.framework.functional;

public interface RepeatableProcedure extends Procedure {
    void times(int numberOfRuns);
}
