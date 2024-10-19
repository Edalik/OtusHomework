package ru.otus.homework;

import ru.otus.homework.annotation.*;
import ru.otus.homework.exception.ConflictingAnnotationsException;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class TestRunner {

    public static void run(Class<?> testSuiteClass) throws Exception {
        Method beforeSuite = null;
        Method afterSuite = null;
        List<Method> tests = new ArrayList<>();
        List<Method> before = new ArrayList<>();
        List<Method> after = new ArrayList<>();

        for (Method method : testSuiteClass.getMethods()) {
            boolean testPresent = method.isAnnotationPresent(Test.class);
            boolean beforeSuitePresent = method.isAnnotationPresent(BeforeSuite.class);
            boolean afterSuitePresent = method.isAnnotationPresent(AfterSuite.class);
            boolean beforePresent = method.isAnnotationPresent(Before.class);
            boolean afterPresent = method.isAnnotationPresent(After.class);

            if (testPresent && (beforeSuitePresent || afterSuitePresent || beforePresent || afterPresent)) {
                throw new ConflictingAnnotationsException("Conflicting annotations on method: " + method.getName());
            }

            if (testPresent) {
                int priority = method.getAnnotation(Test.class).priority();
                if (priority > 10 || priority < 1) {
                    throw new IllegalArgumentException(method + " has illegal priority value");
                }
                tests.add(method);
            } else if (beforeSuitePresent) {
                if (beforeSuite != null) {
                    throw new ConflictingAnnotationsException("Multiple @BeforeSuite annotations applied in class: "
                            + testSuiteClass.getName());
                }
                beforeSuite = method;
            } else if (afterSuitePresent) {
                if (afterSuite != null) {
                    throw new ConflictingAnnotationsException("Multiple @AfterSuite annotations applied in class: "
                            + testSuiteClass.getName());
                }
                afterSuite = method;
            } else if (beforePresent) {
                before.add(method);
            } else if (afterPresent) {
                after.add(method);
            }
        }

        if (testSuiteClass.isAnnotationPresent(Disabled.class)) {
            printDisabled(testSuiteClass);
            return;
        }

        int testCount = tests.size();
        int testSuccess = 0;
        int testFailed = 0;
        int testDisabled = 0;

        if (beforeSuite != null) {
            if (beforeSuite.isAnnotationPresent(Disabled.class)) {
                printDisabled(beforeSuite);
            } else {
                beforeSuite.invoke(null);
            }
        }

        tests.sort(Comparator.comparingInt(method -> ((Method) method).getAnnotation(Test.class).priority()).reversed());
        for (Method test : tests) {
            if (test.isAnnotationPresent(Disabled.class)) {
                printDisabled(test);
                testDisabled++;
                continue;
            }

            for (Method b : before) {
                b.invoke(null);
            }

            if (test.isAnnotationPresent(ThrowsException.class)) {
                String exception = test.getAnnotation(ThrowsException.class).exception().getTypeName();
                boolean isMatchingExceptions = false;

                try {
                    test.invoke(null);
                } catch (Exception e) {
                    isMatchingExceptions = e.getCause().getClass().getTypeName().equals(exception);
                }

                if (isMatchingExceptions) {
                    testSuccess++;
                } else {
                    testFailed++;
                }
            } else {
                try {
                    test.invoke(null);
                    testSuccess++;
                } catch (Exception e) {
                    testFailed++;
                }
            }

            for (Method a : after) {
                a.invoke(null);
            }
        }

        if (afterSuite != null) {
            if (afterSuite.isAnnotationPresent(Disabled.class)) {
                printDisabled(afterSuite);
            } else {
                afterSuite.invoke(null);
            }
        }

        System.out.println("-----------------------------------------------------------------------------------------");
        System.out.println("Total test count: " + testCount);
        System.out.println("Tests succeeded: " + testSuccess);
        System.out.println("Tests failed: " + testFailed);
        System.out.println("Tests disabled: " + testDisabled);
    }

    private static <T extends AnnotatedElement> void printDisabled(T testSuiteClass) {
        String reason = testSuiteClass.getAnnotation(Disabled.class).reason();
        String reasonMessage = testSuiteClass + " is disabled. Reason: " + reason;
        System.out.println(reasonMessage);
    }

}