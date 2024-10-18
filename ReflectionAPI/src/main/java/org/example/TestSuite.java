package org.example;

import org.example.annotation.*;
import org.example.exception.ConflictingAnnotationsException;
import org.example.exception.TestException;

public class TestSuite {

    @BeforeSuite
    public static void beforeSuite() {
        System.out.println("Before suite");
    }

    @Test(priority = 1)
    public static void test1() {
        System.out.println("Test 1");
    }

    @Test(priority = 2)
    public static void test2() {
        System.out.println("Test 2");
    }

    @Test(priority = 2)
    @Disabled()
    public static void testTwo() {
        System.out.println("Test two");
    }

    @Test(priority = 3)
    public static void test3() {
        System.out.println("Test 3");
    }

    @Test
    @ThrowsException(exception = TestException.class)
    public static void exception1() {
        throw new TestException();
    }

    @Test
    @ThrowsException(exception = ConflictingAnnotationsException.class)
    public static void exception2() {
        throw new TestException();
    }

    @Before
    public static void before() {
        System.out.println("Before");
    }

    @After
    public static void after() {
        System.out.println("After");
    }

    @AfterSuite
    public static void afterSuite() {
        System.out.println("After suite");
    }

}