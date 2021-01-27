package J2.lesson1;

import java.util.Random;

public class Main {
    public static void main(String[] args) {

//        1. Создайте три класса Человек, Кот, Робот, которые не наслудуются от одного класса.
//        Эти классы должны уметь бегать и прыгать (методы просто выводят информацию о действии в консоль).
//
//        2. Создайте два клсса: беговая дорожка и стена, при прохождении через которые участники должны выполнять
//        соответсвующие действия (бежать или прыгать), результат выполнения печатаем в консоль (успешно пробежал,
//        не смог пробежать и т.д.). У препятствий есть длина (для дорожки) или высота (для стены), а участников
//        ограничения на бег и прыжки.
//
//        3. Создайте два массива: с участнриками и препятсвиями, и заставьте всех участников пройти этот
//        набор препятствий. Если участник не смог пройти одно из препятствий, то дальше по списку он препятствий
//        не идет.


        Random rand = new Random();

        Object[] participants = {
                new Human(),
                new Cat(),
                new Robot()
        };
        Object[] obstacles = {
                new Track(rand.nextFloat() * 3000),
                new Wall(rand.nextFloat() * 2),
        };


//        Wall wall;
//        Track track;
//        for (Object participant : participants) {
//            for (Object obstacle : obstacles) {
//                if (participant instanceof Jumping)
//                    if (obstacle instanceof Wall) {
//                        wall = (Wall) obstacle;
//                        if (wall.doIt((Jumping) participant))
//                            System.out.printf("%s перепрыгнул через стену высотой %.2fм\n", participant, wall.getHeight());
//                        else {
//                            System.out.printf("%s не смог перепрыгнуть через стену высотой %.2fм\n", participant, wall.getHeight());
//                            break;
//                        }
//                    }
//                if (participant instanceof Running) {
//                    if (obstacle instanceof Track) {
//                        track = (Track) obstacle;
//                        if (track.doIt((Running) participant))
//                            System.out.printf("%s пробежал трек длиной %.2fм\n", participant, track.getLength());
//                        else {
//                            System.out.printf("%s не смог пробежать трек длиной %.2fм\n", participant, track.getLength());
//                            break;
//                        }
//                    }
//                }
//            }
//        }

//        2. Добавить класс Team, который будет содержать название команды, массив из четырех участников
//        (в конструкторе можно сразу указыватьвсех участников ), метод для вывода информации о членах команды,
//        прошедших дистанцию, метод вывода информации обо всех членах команды.
//        3. Добавить класс Course (полоса препятствий), в котором будут находиться массив препятствий и метод,
//        который будет просить команду пройти всю полосу;
//
//        В итоге должно быть что-то вроде:
//
//        public static void main(String[] args) {
//            Course c = new Course(...); // Создаем полосу препятствий
//            Team team = new Team(...); // Создаем команду
//            c.doIt(team); // Просим команду пройти полосу
//            team.showResults(); // Показываем результаты
//        }

        Team team = new Team("Подопытные", participants);
        System.out.println(team);
        Course course = new Course(obstacles);
        course.doIt(team);
        System.out.println(team.showResults());
    }
}
