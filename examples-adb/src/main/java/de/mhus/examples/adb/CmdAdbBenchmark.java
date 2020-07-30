package de.mhus.examples.adb;

import org.apache.karaf.shell.api.action.Argument;
import org.apache.karaf.shell.api.action.Command;
import org.apache.karaf.shell.api.action.lifecycle.Service;

import de.mhus.db.osgi.api.adb.AdbOsgiUtil;
import de.mhus.db.osgi.api.adb.AdbService;
import de.mhus.lib.adb.DbManager;
import de.mhus.lib.core.MStopWatch;
import de.mhus.lib.errors.MException;
import de.mhus.osgi.api.karaf.AbstractCmd;

@Command(scope = "itest", name = "adbbenchmark", description = "Adb benchmark itest tool")
@Service
public class CmdAdbBenchmark extends AbstractCmd {

    @Argument(
            index = 0,
            name = "amount",
            required = true,
            description =
                    "Amount of entities to create"
            ,
            multiValued = false)
    int amount;
    
    @Argument(
            index = 1,
            name = "readLoops",
            required = true,
            description =
                    "Amount of foll read loops over all entries"
            ,
            multiValued = false)
    int readLoops;
    
    @Override
    public Object execute2() throws Exception {
        AdbService service = AdbOsgiUtil.getCommonAdbService();
        DbManager adb = service.getManager();

        // Cleanup
        {
            for (Member entry : adb.getAll(Member.class)) {
//                System.out.print(".");
                entry.delete();
            }
        }
       
        // save / load
        {
            MStopWatch watch = new MStopWatch("ADB SaveLoad").start();
            for (int i = 0; i < amount; i++) {
//                System.out.print(".");
                Member entry = adb.inject(new Member());
                entry.setName("Trooper " + i);
                entry.save();
                
                Member loaded = adb.getObject(Member.class, entry.getId());
                if (!entry.getName().equals(loaded.getName()))
                    throw new MException("ADB Not the same name");
            }
            watch.stop();
            System.out.println();
            System.out.println(watch);
        }

        // Read
        {
            MStopWatch watch = new MStopWatch("ADB Read").start();
            for (int i = 0; i < readLoops; i++) {
//                System.out.print(".");
                for (Member entry : adb.getAll(Member.class))
                    entry.getName();
            }
            watch.stop();
            System.out.println();
            System.out.println(watch);
        }
        
        // Delete
        {
            int cnt = 0;
            MStopWatch watch = new MStopWatch("ADB Delete").start();
            for (Member entry : adb.getAll(Member.class)) {
//                System.out.print(".");
                entry.delete();
                cnt++;
            }
            watch.stop();
            System.out.println();
            System.out.println(cnt + " " + watch);
        }

        return null;
    }

}
