package tests;


import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

@Suite
@SelectClasses({
        UserDeleteTests.class,
        UserEditTest.class,
        UserGetTest.class,
        UserRegisterTest.class,
        UserAuthTest.class
})
public class TestRunner {


}