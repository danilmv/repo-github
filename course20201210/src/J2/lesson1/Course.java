package J2.lesson1;

public class Course {
    private final int MAX_OBSTACLES = 10;
    private Object[] obstacles = new Object[MAX_OBSTACLES];
    private int numObstacles = 0;

    public Course(Object... obstacles) {
        for (int i = 0; i < obstacles.length && addObstacle(obstacles[i]); i++) ;
    }

    public boolean addObstacle(Object obstacle) {
        if (numObstacles < MAX_OBSTACLES) {
            obstacles[numObstacles++] = obstacle;
            return true;
        } else
            return false;
    }

    public void doIt(Team team) {
        for (Object obstacle : obstacles) {
            if (obstacle instanceof Wall)
                team.jump((Wall) obstacle);

            if (obstacle instanceof Track)
                team.run((Track) obstacle);
        }
    }
}
