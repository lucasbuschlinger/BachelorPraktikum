package de.opendiabetes.tests.plugin.interpreter;

import de.opendiabetes.tests.plugin.util.TestUtil;
import de.opendiabetes.vault.plugin.common.OpenDiabetesPlugin;
import de.opendiabetes.vault.plugin.importer.Importer;
import de.opendiabetes.vault.plugin.interpreter.Interpreter;
import org.junit.Assert;
import org.junit.Test;
import org.pf4j.DefaultPluginManager;
import org.pf4j.PluginManager;

import java.nio.file.Paths;
import java.util.Arrays;

public class NonInterpreterTest {


    /**
     * Test to see whether the plugin can be loaded.
     */
    @Test
    public void pluginLoad() {
        PluginManager manager = new DefaultPluginManager(Paths.get("export"));
        manager.loadPlugins();
        manager.startPlugins();
        Interpreter interpreter = manager.getExtensions(Interpreter.class, "PumpInterpreter").get(0);

    }

    /**
     * Test for the path setter and getter.
     */
    @Test
    public void interpretTest() {
        Interpreter interpreter = TestUtil.getInterpreter("NonInterpreter");
        interpreter.interpret(null);
    }
}
