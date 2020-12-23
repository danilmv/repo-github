package lesson4;

public class Employee {
    private String fio;
    private String position;
    private String phone;
    private int salary;
    private int age;

    static int idLast = 0;
    int id;

    public Employee(String fio, String position, String phone, int salary, int age){
        this.fio = fio;
        this.position = position;
        this.phone = phone;
        this.salary = salary;
        this.age = age;

        id = ++idLast;
    }
    public String getFio(){return fio;}
    public String getPosition(){return position;}
    public String getPhone(){return phone;}
    public int getSalary(){return salary;}
    public int getAge(){return age;}
    public String toString(){
        return "№" + id + ". ФИО: " + fio + ", должность: " + position + ", телефон: " + phone + ", з/п: " + salary + ", возраст: " + age;
    }
    public boolean raiseSalaty(int age, int raise){
        if (this.age < age)
            return false;

        salary += raise;
        return true;

    }
}
