package data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Log implements Serializable {
    private static final long serialVersionUID = 1L;

    private final List<LogEntry> entries = new ArrayList<>();

    public void addEntry(String userId, String action) {
        entries.add(new LogEntry(userId, action, LocalDateTime.now()));
    }

    public List<LogEntry> getEntries() {
        return Collections.unmodifiableList(entries);
    }

    public void printAll() {
        if (entries.isEmpty()) {
            System.out.println("Log бос.");
            return;
        }
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        System.out.println("========== SYSTEM LOG ==========");
        for (LogEntry e : entries) {
            System.out.printf("[%s] %-15s | %s%n",
                    e.getTimestamp().format(fmt),
                    e.getUserId(),
                    e.getAction());
        }
        System.out.println("================================");
    }

    public static class LogEntry implements Serializable {
        private static final long serialVersionUID = 1L;

        private final String        userId;
        private final String        action;
        private final LocalDateTime timestamp;

        public LogEntry(String userId, String action, LocalDateTime timestamp) {
            this.userId    = userId;
            this.action    = action;
            this.timestamp = timestamp;
        }

        public String        getUserId()    { return userId; }
        public String        getAction()    { return action; }
        public LocalDateTime getTimestamp() { return timestamp; }

        @Override
        public String toString() {
            return "[" + timestamp + "] " + userId + " → " + action;
        }
    }
}
