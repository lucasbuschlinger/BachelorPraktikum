package de.opendiabetes.tests.plugin.local;

import de.opendiabetes.tests.plugin.management.ImportAndInterpretDataflowHelper;
import de.opendiabetes.vault.data.VaultDAO;
import de.opendiabetes.vault.plugin.common.OpenDiabetesPlugin;
import de.opendiabetes.vault.plugin.importer.fileimporter.FileImporter;
import de.opendiabetes.vault.plugin.interpreter.Interpreter;
import de.opendiabetes.vault.plugin.management.OpenDiabetesPluginManager;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;

public class OverallTest {

    /*
    * run: sudo systemctl start mysql to start sql server
    * run: mysqladmin -u root -p shutdown to stop sql server
    * do not forget to apply stash "WIP on feature: /us23: /us23:" to change the neccessary changes in VaultDao to connect to db
     */
    @Test
    public void importAndInterpretTest() throws Exception {
        VaultDAO.initializeDb();
        VaultDAO db = VaultDAO.getInstance();

        ImportAndInterpretDataflowHelper manager = new ImportAndInterpretDataflowHelper();
        OpenDiabetesPluginManager pluginManager = OpenDiabetesPluginManager.getInstance();
        FileImporter medImporter = pluginManager.getPluginFromString(FileImporter.class, "MedtronicImporter");

        OpenDiabetesPlugin.StatusListener listener = new OpenDiabetesPlugin.StatusListener() {
            @Override
            public void onStatusCallback(int progress, String status) {
                System.out.println(progress+status);
            }
        };

        medImporter.registerStatusCallback(listener);

        Interpreter dateInterpreter = pluginManager.getPluginFromString(Interpreter.class, "DateInterpreter");
        dateInterpreter.registerStatusCallback(listener);
        dateInterpreter.init(db);
        System.out.println("date interpreter load configuration successful");

        Interpreter pumpInterpreter = pluginManager.getPluginFromString(Interpreter.class, "PumpInterpreter");
        pumpInterpreter.registerStatusCallback(listener);
        pumpInterpreter.init(db);
        System.out.println("pump interpreter load configuration successful");

        Interpreter[] interpreters = new Interpreter[]{dateInterpreter, pumpInterpreter};
        Assert.fail("please provide data to import!"); //Eg."~/testdata/CareLink-Export-1486459734778.csv"
        manager.importAndInterpretWithoutDb(medImporter, "data/to/import", Arrays.asList(interpreters)).forEach(x->System.out.println(x));
    }
}
