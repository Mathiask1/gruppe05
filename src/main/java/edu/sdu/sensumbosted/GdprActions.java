package edu.sdu.sensumbosted;

import edu.sdu.sensumbosted.data.DataService;
import edu.sdu.sensumbosted.entity.Manager;
import edu.sdu.sensumbosted.entity.Patient;
import edu.sdu.sensumbosted.entity.Practitioner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
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
        if(data.delete(id, Manager.SQL_TABLE)) type = "manager";
        else if(data.delete(id, Practitioner.SQL_TABLE)) type = "practitioner";
        else if(data.delete(id, Patient.SQL_TABLE)) type = "patient";

        if (type == null) {
            log.error("No user found for that ID");
            System.exit(2);
        }

        log.info("Deleted user of type {}", type);
    }

    private static void dump(UUID id, File file) throws IOException {

    }

    private static void exitWithHelp() {
        String info = "Usage: delete <uuid> OR dump <uuid> <file>";
        log.info(info);
        System.exit(1);
    }

}
