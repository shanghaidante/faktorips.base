/*******************************************************************************
 * Copyright (c) 2005-2009 Faktor Zehn AG und andere.
 * 
 * Alle Rechte vorbehalten.
 * 
 * Dieses Programm und alle mitgelieferten Sachen (Dokumentationen, Beispiele, Konfigurationen,
 * etc.) duerfen nur unter den Bedingungen der Faktor-Zehn-Community Lizenzvereinbarung - Version
 * 0.1 (vor Gruendung Community) genutzt werden, die Bestandteil der Auslieferung ist und auch unter
 * http://www.faktorzehn.org/f10-org:lizenzen:community eingesehen werden kann.
 * 
 * Mitwirkende: Faktor Zehn AG - initial API and implementation - http://www.faktorzehn.de
 *******************************************************************************/

package org.faktorips.runtime.pds;

import javax.xml.parsers.ParserConfigurationException;

import org.faktorips.runtime.ClassloaderRuntimeRepository;
import org.faktorips.runtime.internal.toc.ITocEntryObject;
import org.faktorips.runtime.internal.toc.ReadonlyTableOfContents;
import org.w3c.dom.Element;

public class InstantProductDataProvider extends ClassloaderRuntimeRepository implements IProductDataProvider {

    public InstantProductDataProvider(ClassLoader cl, String basePackage) throws ParserConfigurationException {
        super(cl, basePackage);
        // TODO Auto-generated constructor stub
    }

    public long getModificationStamp() {
        // TODO Auto-generated method stub
        return 0;
    }

    public ReadonlyTableOfContents loadToc() {
        return (ReadonlyTableOfContents)loadTableOfContents();
    }

    public Element getProductCmptData(String ipsObjectId) {
        ITocEntryObject tocEntry = toc.getProductCmptTocEntry(ipsObjectId);
        // TODO return getDocumentElement(tocEntry);
        return null;
    }

}
