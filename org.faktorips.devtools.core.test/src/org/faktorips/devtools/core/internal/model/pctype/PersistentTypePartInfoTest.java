/*******************************************************************************
 * Copyright (c) Faktor Zehn AG. <http://www.faktorzehn.org>
 * 
 * This source code is available under the terms of the AGPL Affero General Public License version
 * 3.
 * 
 * Please see LICENSE.txt for full license terms, including the additional permissions and
 * restrictions as well as the possibility of alternative license terms.
 *******************************************************************************/
package org.faktorips.devtools.core.internal.model.pctype;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.eclipse.core.runtime.CoreException;
import org.faktorips.devtools.core.model.ipsobject.IpsObjectType;
import org.faktorips.devtools.core.model.pctype.IPersistentAttributeInfo;
import org.faktorips.devtools.core.model.pctype.IPersistentTypeInfo.PersistentType;
import org.faktorips.devtools.core.model.pctype.IPolicyCmptTypeAttribute;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

@RunWith(MockitoJUnitRunner.class)
public class PersistentTypePartInfoTest extends PersistenceIpsTest {

    private IPolicyCmptTypeAttribute pcTypePart;

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();

        policyCmptType.getPersistenceTypeInfo().setPersistentType(PersistentType.ENTITY);
        pcTypePart = policyCmptType.newPolicyCmptTypeAttribute();
        pcTypePart.getPersistenceAttributeInfo().setTransient(false);
        pcTypePart.setName("attr1");
    }

    @Test
    public void testInitFromXml() {
        NodeList nodeList = getTestDocument().getElementsByTagName(IPersistentAttributeInfo.XML_TAG);
        assertEquals(1, nodeList.getLength());

        Element element = (Element)nodeList.item(0);
        IPersistentAttributeInfo persistenceAttributeInfo = pcTypePart.getPersistenceAttributeInfo();
        persistenceAttributeInfo.initFromXml(element);

        assertFalse(persistenceAttributeInfo.isTransient());
        assertEquals("premiumIndex", persistenceAttributeInfo.getIndexName());
    }

    @Test
    public void testToXml() throws CoreException {
        IPersistentAttributeInfo persistenceAttributeInfo = pcTypePart.getPersistenceAttributeInfo();
        persistenceAttributeInfo.setTransient(true);
        persistenceAttributeInfo.setIndexName("XYZ");
        Element element = policyCmptType.toXml(newDocument());

        PolicyCmptType copyOfPcType = (PolicyCmptType)newIpsObject(ipsProject, IpsObjectType.POLICY_CMPT_TYPE, "Copy");
        copyOfPcType.initFromXml(element);

        assertEquals(1, copyOfPcType.getPolicyCmptTypeAttributes().size());
        IPersistentAttributeInfo persistenceAttributeInfoCopy = copyOfPcType.getPolicyCmptTypeAttributes().get(0)
                .getPersistenceAttributeInfo();

        assertTrue(persistenceAttributeInfoCopy.isTransient());
        assertEquals("XYZ", persistenceAttributeInfo.getIndexName());
    }

}
