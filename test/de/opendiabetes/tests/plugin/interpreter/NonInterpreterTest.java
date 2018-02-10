package de.opendiabetes.tests.plugin.interpreter;

import de.opendiabetes.tests.plugin.util.TestUtil;
import de.opendiabetes.vault.data.VaultDao;
import de.opendiabetes.vault.plugin.common.OpenDiabetesPlugin;
import de.opendiabetes.vault.plugin.importer.Importer;
import de.opendiabetes.vault.plugin.interpreter.Interpreter;
import de.opendiabetes.vault.plugin.management.ImportAndInterpretDataflowWrapper;
import org.junit.Test;
import org.pf4j.DefaultPluginManager;
import org.pf4j.PluginManager;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

public class NonInterpreterTest {


    /**
     * Test to see whether the plugin can be loaded.
     */
    @Test
    public void pluginLoad() {
        PluginManager manager = new DefaultPluginManager(Paths.get("export"));
        manager.loadPlugins();
        manager.startPlugins();
        Interpreter interpreter = manager.getExtensions(Interpreter.class, "NonInterpreter").get(0);

    }

    /**
     * Test for the path setter and getter.
     */
    @Test
    public void interpretTest() {
        Interpreter interpreter = TestUtil.getInterpreter("NonInterpreter");
        interpreter.interpret(null);
    }

    /*
    * run: sudo systemctl start mysql to start sql server
    * run: mysqladmin -u root -p shutdown to stop sql server
    * do not forget to apply stash "WIP on feature: /us23: /us23:" to change the neccessary changes in VaultDao to connect to db
     */
    @Test
    public void importAndInterpretTest() throws IOException, SQLException {
        VaultDao.initializeDb();
        VaultDao db = VaultDao.getInstance();

        ImportAndInterpretDataflowWrapper manager = new ImportAndInterpretDataflowWrapper();
        Importer medImporter = TestUtil.getImporter("MedtronicImporter");
        medImporter.setImportFilePath("/home/magnus/testdata/CareLink-Export-1486459734778.csv");

        OpenDiabetesPlugin.StatusListener listener = new OpenDiabetesPlugin.StatusListener() {
            @Override
            public void onStatusCallback(int progress, String status) {
                System.out.println(progress+status);
            }
        };

        medImporter.registerStatusCallback(listener);

        Interpreter dateInterpreter = TestUtil.getInterpreter("DateInterpreter");
        dateInterpreter.registerStatusCallback(listener);

        Properties dateInterpreterProperties = new Properties();
        dateInterpreterProperties.load(new FileInputStream("/home/magnus/OpenDiabetes/properties/dateInterpreter.properties"));
        if (!dateInterpreter.loadConfiguration(dateInterpreterProperties)) return;
        dateInterpreter.init(db);

        System.out.println("date interpreter load configuration successful");

        Interpreter pumpInterpreter = TestUtil.getInterpreter("PumpInterpreter");
        Properties pumpInterpreterProperties = new Properties();


        pumpInterpreterProperties.load(new FileInputStream("/home/magnus/OpenDiabetes/properties/pumpInterpreter.properties"));
        if (!pumpInterpreter.loadConfiguration(pumpInterpreterProperties)) return;
        pumpInterpreter.init(db);
        System.out.println("pump interpreter load configuration successful");
        Interpreter[] interpreters = new Interpreter[]{dateInterpreter, pumpInterpreter};
        manager.importAndInterpretWithoutDb(medImporter, Arrays.asList(interpreters)).forEach(x->System.out.println(x));
    }
}
