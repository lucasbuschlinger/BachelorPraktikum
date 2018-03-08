package de.opendiabetes.vault.plugin.crawlerimporter;

import de.opendiabetes.vault.container.VaultEntry;
import de.opendiabetes.vault.plugin.common.AbstractPlugin;

import java.util.List;

public abstract class AbstractCrawlerImporter extends AbstractPlugin implements CrawlerImporter {

    /**
     * {@inheritDoc}
     */
    @Override
    public List<VaultEntry> importData() {
        throw new UnsupportedOperationException("The importData() method of a CrawlerImporter cannot be used."
                + " Use importData(username, password) instead.");
    }

}
