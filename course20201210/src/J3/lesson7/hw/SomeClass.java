package J3.lesson7.hw;

import J3.lesson7.hw.annotation.AfterSuite;
import J3.lesson7.hw.annotation.BeforeSuite;
import J3.lesson7.hw.annotation.PRIORITIES;
import J3.lesson7.hw.annotation.Test;

public class SomeClass {
    @BeforeSuite
    public void prepare(){
        System.out.println("BeforeSuite: I'm ready");
    }
//    @BeforeSuite
//    public void prepare2(){
//        System.out.println("BeforeSuite: I'm ready again");
//    }

    @Test(priority = PRIORITIES.FIVE)
    public void someAction(){
        System.out.println("Test{5}: I did something");
    }

    @Test(priority = PRIORITIES.FIVE)
    public void anotherAction(){
        System.out.println("Test{5}: I did another thing");
    }

    @Test(priority = PRIORITIES.FIVE)
    public void somethingElse(){
        System.out.println("Test{5}: I didn't do anything");
    }

    @Test(priority = PRIORITIES.TEN)
    public void finalAction(){
        System.out.println("Test{10}: I finished");
    }

    @AfterSuite
    public void finish(){
        System.out.println("AfterSuite: I'm leaving");
    }
}
