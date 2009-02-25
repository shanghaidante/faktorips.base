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

package org.faktorips.devtools.core.internal.model.enumtype;

import javax.xml.parsers.ParserConfigurationException;

import org.eclipse.core.runtime.CoreException;
import org.faktorips.devtools.core.model.IIpsModel;
import org.faktorips.devtools.core.model.enumcontent.IEnumContent;
import org.w3c.dom.Element;

public class EnumValueTest extends AbstractIpsEnumPluginTest {

    @Override
    public void setUp() throws Exception {
        super.setUp();
    }

    public void testNewEnumAttributeValue() throws CoreException {
        assertNotNull(genderEnumValueMale.newEnumAttributeValue());
    }

    public void testGetEnumAttributeValues() {
        assertEquals(2, genderEnumValueMale.getEnumAttributeValues().size());
    }

    public void testXml() throws ParserConfigurationException, CoreException {
        Element xmlElement = genderEnumContent.toXml(createXmlDocument(IEnumContent.XML_TAG));
        assertEquals(1 + 2, xmlElement.getChildNodes().getLength());

        IEnumContent loadedEnumContent = newEnumContent(ipsProject, "LoadedEnumValues");
        loadedEnumContent.initFromXml(xmlElement);
        assertEquals(2, loadedEnumContent.getEnumValues().size());
    }

    public void testValidateThis() throws CoreException {
        assertTrue(genderEnumValueFemale.isValid());

        IIpsModel ipsModel = getIpsModel();

        // Test not enough enum attribute values
        ipsModel.clearValidationCache();
        genderEnumValueFemale.getEnumAttributeValue(0).delete();
        assertEquals(1, genderEnumValueFemale.validate(ipsProject).getNoOfMessages());
    }

    public void testGetImage() {
        assertNull(genderEnumValueMale.getImage());
    }
    
    public void testGetEnumValueContainer() {
        assertEquals(genderEnumContent, genderEnumValueMale.getEnumValueContainer());
    }

}
