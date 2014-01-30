/*******************************************************************************
 * Copyright (c) Faktor Zehn AG. <http://www.faktorzehn.org>
 * 
 * This source code is available under the terms of the AGPL Affero General Public License version 3.
 * 
 * Please see LICENSE.txt for full license terms, including the additional permissions and the
 * possibility of alternative license terms.
 *******************************************************************************/

package org.faktorips.runtime.internal.toc;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.imageio.spi.ServiceRegistry;

import org.faktorips.runtime.IRuntimeObject;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * A table contents for the runtime repository.
 * <p>
 * The table of contents contains a list of toc entries that contain the information needed to
 * identify and load the objects stored in the repository.
 * </p>
 * <p>
 * <em>The table of contents can be extended to read toc entries for new object types by implementing
 * and registering a {@link ITocEntryFactory}.</em>
 * </p>
 * 
 * @author Jan Ortmann
 */
public abstract class AbstractReadonlyTableOfContents implements IReadonlyTableOfContents {

    public final static String TOC_XML_ELEMENT = "FaktorIps-TableOfContents";
    public static final String PRODUCT_DATA_VERSION_XML_ELEMENT = "productDataVersion";

    private String productDataVersion;

    private Map<String, ITocEntryFactory<?>> tocEntryFactoriesByXmlTag;
    private final ClassLoader classLoader;

    private Map<String, ITocEntryFactory<?>> getTocEntryFactoriesByXmlTag() {
        if (tocEntryFactoriesByXmlTag == null) {
            synchronized (TocEntryObject.class) {
                if (tocEntryFactoriesByXmlTag == null) {
                    tocEntryFactoriesByXmlTag = new HashMap<String, ITocEntryFactory<?>>();
                    for (ITocEntryFactory<?> tocEntryFactory : AbstractTocEntryFactory.getBaseTocEntryFactories()) {
                        tocEntryFactoriesByXmlTag.put(tocEntryFactory.getXmlTag(), tocEntryFactory);
                    }
                    @SuppressWarnings("rawtypes")
                    Iterator<ITocEntryFactory> tocEntryFactories = ServiceRegistry.lookupProviders(
                            ITocEntryFactory.class, classLoader);
                    for (; tocEntryFactories.hasNext();) {
                        ITocEntryFactory<?> tocEntryFactory = tocEntryFactories.next();
                        tocEntryFactoriesByXmlTag.put(tocEntryFactory.getXmlTag(), tocEntryFactory);
                    }
                }

            }
        }
        return tocEntryFactoriesByXmlTag;
    }

    /**
     * Creats a new toc that uses the given {@link ClassLoader} to find {@link ITocEntryFactory}
     * implementations via {@link ServiceRegistry}.
     * 
     * @param classLoader the {@link ClassLoader} used to find {@link ITocEntryFactory}
     *            implementations
     */
    public AbstractReadonlyTableOfContents(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    /**
     * Initializes the table of contents with data stored in the xml element.
     */
    public void initFromXml(Element tocElement) {
        productDataVersion = tocElement.getAttribute(PRODUCT_DATA_VERSION_XML_ELEMENT);
        if (productDataVersion == null) {
            productDataVersion = "0";
        }
        NodeList nl = tocElement.getChildNodes();
        for (int i = 0; i < nl.getLength(); i++) {
            if (nl.item(i) instanceof Element) {
                Element entryElement = (Element)nl.item(i);
                internalAddEntry(getTocEntryFactoriesByXmlTag().get(entryElement.getNodeName()).createFromXml(
                        entryElement));
            }
        }
    }

    /**
     * Adds the entry to the table of contents.
     */
    protected abstract void internalAddEntry(TocEntryObject entry);

    /**
     * Returns the toc entry representing a product component for the given id or null if no entry
     * exists for the given id.
     */
    public abstract ProductCmptTocEntry getProductCmptTocEntry(String id);

    /**
     * Returns the toc entry representing a product component for the given product component kind
     * id and versionId or null if no such entry exists.
     */
    public abstract ProductCmptTocEntry getProductCmptTocEntry(String kindId, String versionId);

    /**
     * Returns all toc's entries representing product components.
     */
    public abstract List<ProductCmptTocEntry> getProductCmptTocEntries();

    /**
     * Returns all toc's entries representing product components that belong to the indicated
     * product component kind.
     */
    public abstract List<ProductCmptTocEntry> getProductCmptTocEntries(String kindId);

    /**
     * Returns all toc's entries representing tables.
     */
    public abstract List<TableContentTocEntry> getTableTocEntries();

    /**
     * Returns all toc's entries representing test cases.
     */
    public abstract List<TestCaseTocEntry> getTestCaseTocEntries();

    /**
     * Returns a toc entry representing a test case for the given qualified name.
     */
    public abstract TestCaseTocEntry getTestCaseTocEntryByQName(String qName);

    /**
     * Returns a toc entry representing a table for the table's class object.
     */
    public abstract TableContentTocEntry getTableTocEntryByClassname(String implementationClass);

    /**
     * Returns a toc entry representing a table for this table's qualified table name.
     */
    public abstract TableContentTocEntry getTableTocEntryByQualifiedTableName(String qualifiedTableName);

    /**
     * Returns all toc's entries representing model types.
     */
    public abstract Set<ModelTypeTocEntry> getModelTypeTocEntries();

    /**
     * Returns the toc entry representing enum contents for the specified implementation class.
     */
    public abstract EnumContentTocEntry getEnumContentTocEntry(String className);

    /**
     * Returns all toc entries that link to an enumeration xml adapter.
     */
    public abstract Set<EnumXmlAdapterTocEntry> getEnumXmlAdapterTocEntries();

    public String getProductDataVersion() {
        return productDataVersion;
    }

    @Override
    public String toString() {
        StringBuffer buf = new StringBuffer("TOC");
        buf.append(System.getProperty("line.separator"));
        List<ProductCmptTocEntry> entries = getProductCmptTocEntries();
        for (ProductCmptTocEntry entry : entries) {
            buf.append(entry.toString());
            buf.append(System.getProperty("line.separator"));
        }

        List<TableContentTocEntry> tableEntries = getTableTocEntries();
        for (TableContentTocEntry entry : tableEntries) {
            buf.append(entry.toString());
            buf.append(System.getProperty("line.separator"));
        }

        List<TestCaseTocEntry> testEntries = getTestCaseTocEntries();
        for (TestCaseTocEntry entry : testEntries) {
            buf.append(entry.toString());
            buf.append(System.getProperty("line.separator"));
        }

        return buf.toString();
    }

    public abstract <T extends IRuntimeObject> List<CustomTocEntryObject<T>> getTypedTocEntries(Class<T> type);

}
