package edu.sdu.sensumbosted;

import edu.sdu.sensumbosted.data.DataService;
import edu.sdu.sensumbosted.data.SqlTable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

public class GdprActions {

    private static DataService data;
    private static final Logger log = LoggerFactory.getLogger(GdprActions.class);

    public static void main(String[] args) throws IOException {
        if (args.length < 2) exitWithHelp();
        UUID id;
        try {
            id = UUID.fromString(args[1]);
        } catch (IllegalArgumentException e) { exitWithHelp(); return; }

        data  = new DataService();
        switch (args[0]) {
            case "delete": delete(id); break;
            case "dump": {
                if (args.length < 3) exitWithHelp();
                dump(id, new File(args[2]));
                break;
            }
        }
    }

    private static void delete(UUID id) {
        String type = null;
        if(data.delete(id, SqlTable.MANAGER)) type = "manager";
        else if(data.delete(id, SqlTable.PRACTITIONER)) type = "practitioner";
        else if(data.delete(id, SqlTable.PATIENT)) type = "patient";

        if (type == null) {
            log.error("No user found for that ID");
            System.exit(2);
        }

        log.info("Deleted user of type {}", type);
    }

    private static void dump(UUID id, File file) throws IOException {
        List<Object> row = data.getRawUserRow(id);
        if (row == null) {
            log.error("User was not found");
            return;
        }
        
        String result = row.stream()
                .map(Object::toString)
                .reduce("", (s, s2) -> s + s2 + "\n");
        try (FileOutputStream stream = new FileOutputStream(file)) {
            stream.write(result.getBytes());
        }
        log.info("Info has been written to {}", file);
    }

    private static void exitWithHelp() {
        String info = "Usage: delete <uuid> OR dump <uuid> <file>";
        log.info(info);
        System.exit(1);
    }

}
