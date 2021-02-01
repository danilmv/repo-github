package J2.lesson3;

import java.util.*;

public class PhoneBook {
    private TreeMap<String, HashSet<String>> numbers = new TreeMap<>();

    public void add(String name, String phone) {
        HashSet<String> numbers = this.numbers.get(name);
        if (numbers == null)
            this.numbers.put(name, new HashSet<>(Arrays.asList(phone)));
        else
            numbers.add(phone);
    }

    public HashSet<String> get(String name) {
        return numbers.get(name);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("\nPhone Book:\n");
        for (Map.Entry<String, HashSet<String>> entry : numbers.entrySet()) {
            sb.append(entry.getKey() + ": ");
            for (String phone : entry.getValue()) {
                sb.append(phone + " ");
            }
            sb.append("\n");
        }

        return sb.toString();
    }
}
