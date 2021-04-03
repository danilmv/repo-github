package J3.lesson7.hw;

import J3.lesson7.hw.annotation.AfterSuite;
import J3.lesson7.hw.annotation.BeforeSuite;
import J3.lesson7.hw.annotation.Priorities;
import J3.lesson7.hw.annotation.Test;

public class SomeClass {
    @BeforeSuite
//    @Test()
    public void prepare(){
        System.out.println("BeforeSuite: I'm ready");
    }
//    @BeforeSuite
//    public void prepare2(){
//        System.out.println("BeforeSuite: I'm ready again");
//    }

    @Test()
    public void someAction(){
        System.out.println("Test{5}: I did something");
    }

    @Test()
    public void anotherAction(){
        System.out.println("Test{5}: I did another thing");
    }

    @Test()
    public void somethingElse(){
        System.out.println("Test{5}: I didn't do anything");
    }

    @Test(priority = Priorities.TEN)
    public void finalAction(){
        System.out.println("Test{10}: I finished");
    }

    @AfterSuite
    public void finish(){
        System.out.println("AfterSuite: I'm leaving");
    }
}
