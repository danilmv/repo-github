package J2.lesson1;

import java.util.Formatter;

public class Team {

    private int MAX_PARTICIPANTS = 4;
    private String name;
    private Object[] participants = new Object[MAX_PARTICIPANTS];
    private int numParticipants = 0;
    private StringBuilder strBuilder = new StringBuilder();
    private Formatter formatter = new Formatter(strBuilder);

    public Team(String name, Object... participants) {
        this.name = name;
        for (int i = 0; i < participants.length && addParticipant(participants[i]); i++) ;
    }

    public boolean addParticipant(Object participant) {
        if (numParticipants < MAX_PARTICIPANTS) {
            this.participants[numParticipants++] = participant;
            return true;
        } else
            return false;
    }

    public String showResults() {
        return strBuilder.toString();
    }

    public void jump(Wall wall) {
        for (Object participant : participants) {
            if (participant instanceof Jumping) {
                if (((Jumping) participant).isActive()) {
                    if (wall.doIt((Jumping) participant))
                        formatter.format("%s перепрыгнул через стену высотой %.2fм\n", participant, wall.getHeight());
                    else
                        formatter.format("%s не смог перепрыгнуть через стену высотой %.2fм\n", participant, wall.getHeight());
                } else
                    formatter.format("%s выбыл\n", participant);
            }
        }
    }

    public void run(Track track) {
        for (Object participant : participants) {
            if (participant instanceof Running) {
                if (((Running) participant).isActive()) {
                    if (track.doIt((Running) participant))
                        formatter.format("%s пробежал трек длиной %.2fм\n", participant, track.getLength());
                    else
                        formatter.format("%s не смог пробежать трек длиной %.2fм\n", participant, track.getLength());
                } else
                    formatter.format("%s выбыл\n", participant);
            }
        }
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Участники команды \"" + name + "\":\n");
        for (int i = 0; i < numParticipants; i++)
            sb.append(participants[i].toString() + "\n");

        return sb.toString();
    }

}
