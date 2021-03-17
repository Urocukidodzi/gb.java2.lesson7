package Server;

import Server.Interface.AuthService;

import java.util.ArrayList;
import java.util.List;

public class BaseAuthService implements AuthService {

    private class Entry {
        private String login;
        private String pass;
        private String nick;

        public Entry(String login, String pass, String nick) {
            this.login = login;
            this.pass = pass;
            this.nick = nick;
        }
    }

    private List<Entry> entries;

    public BaseAuthService() {
        entries = new ArrayList<>();
        entries.add(new Entry("A", "A", "nickA"));
        entries.add(new Entry("B", "B", "nickB"));
        entries.add(new Entry("C", "C", "nickC"));
    }

    @Override
    public void start() {
        System.out.println("Auth service start");
    }

    @Override
    public String getNickByLoginPass(String login, String pass) {
        for (Entry c : entries) {
            if (c.login.equals(login) && c.pass.equals(pass)) {
                return c.nick;
            }
        }
        return null;
    }

    @Override
    public void stop() {
        System.out.println("Auth service stop");
    }

}
