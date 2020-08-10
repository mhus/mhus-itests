package de.mhus.lib.itest.cases;

import java.io.File;

import de.mhus.lib.core.MProperties;
import de.mhus.lib.core.util.MMaven;
import de.mhus.lib.core.util.MMaven.Pom;
import de.mhus.lib.errors.NotFoundException;

public class TestUtil {

    public static MProperties loadProperties() throws NotFoundException {
        MProperties prop = new MProperties(System.getenv());
        
        if (!prop.containsKey("project.version")) {
            System.out.println("Load env from file");
            File f = new File("../target/classes/app.properties");
            if (!f.exists())
                throw new NotFoundException("app.properties not found: " + f);
            prop.putAll(MProperties.load(f));
        }
        try {
            Pom pom = MMaven.loadPom("pom.xml");
            prop.setString("project.version", pom.getArtifact().getVersion());
        } catch (Exception e) {
            e.printStackTrace();
        }
        // overload with local config
        {
            File file = new File("../../itest.properties");
            if (file.exists()) {
                MProperties local = MProperties.load(file);
                prop.putReadProperties(local);
            } else
                System.out.println("Local config not found " + file.getAbsolutePath());
        }
        
        System.out.println(prop);
        return prop;
    }

}
