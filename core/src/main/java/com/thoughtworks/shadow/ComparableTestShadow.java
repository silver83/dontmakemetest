package com.thoughtworks.shadow;

import junit.framework.Test;
import junit.framework.TestResult;

public class ComparableTestShadow implements TestShadow, Comparable {
    private final TestState state;
    private final Test test;
    private final String testClassName;
    private ShadowCabinet shadowCabinet;

    public ComparableTestShadow(Test test) {
        this(test, test.getClass().getName());
    }

    public ComparableTestShadow(Test test, String testClassName) {
        this.test = test;
        this.testClassName = testClassName;
        this.state = new TestState(this);
    }

    public void accept(ShadowVisitor visitor) {
        visitor.visitTestClassName(testClassName);
        visitor.end();
    }

    public int countTestCases() {
        return test.countTestCases();
    }

    public void run(TestResult result) {
        state.startTest(this);
        result.addListener(state);
        test.run(result);
        result.removeListener(state);
        state.endTest(this);
    }

    public int compareTo(Object o) {
        return state.compareTo(((ComparableTestShadow) o).state);
    }

    public void addListener(TestStateListener listener) {
        state.addListener(listener);
    }

    public void removeSelfFromContainer() {
        shadowCabinet.remove(this);
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final ComparableTestShadow that = (ComparableTestShadow) o;

        return test.equals(that.test);
    }

    public int hashCode() {
        return test.hashCode();
    }

    public String toString() {
        return test.toString();
    }

    void setContainer(ShadowCabinet shadowCabinet) {
        this.shadowCabinet = shadowCabinet;
    }
}
