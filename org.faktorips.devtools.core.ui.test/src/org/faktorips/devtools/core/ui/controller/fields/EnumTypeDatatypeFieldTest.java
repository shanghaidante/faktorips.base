/*******************************************************************************
 * Copyright (c) Faktor Zehn AG. <http://www.faktorzehn.org>
 * 
 * This source code is available under the terms of the AGPL Affero General Public License version 3.
 * 
 * Please see LICENSE.txt for full license terms, including the additional permissions and the
 * possibility of alternative license terms.
 *******************************************************************************/

package org.faktorips.devtools.core.ui.controller.fields;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.faktorips.abstracttest.AbstractIpsPluginTest;
import org.faktorips.datatype.Datatype;
import org.faktorips.devtools.core.EnumTypeDisplay;
import org.faktorips.devtools.core.IpsPlugin;
import org.faktorips.devtools.core.IpsPreferences;
import org.faktorips.devtools.core.model.enums.EnumTypeDatatypeAdapter;
import org.faktorips.devtools.core.model.enums.IEnumAttribute;
import org.faktorips.devtools.core.model.enums.IEnumAttributeValue;
import org.faktorips.devtools.core.model.enums.IEnumContent;
import org.faktorips.devtools.core.model.enums.IEnumType;
import org.faktorips.devtools.core.model.enums.IEnumValue;
import org.faktorips.devtools.core.model.ipsproject.IIpsProject;
import org.faktorips.devtools.core.model.value.ValueFactory;
import org.junit.Before;
import org.junit.Test;

public class EnumTypeDatatypeFieldTest extends AbstractIpsPluginTest {

    IIpsProject ipsProject;
    IEnumType enum1;

    @Override
    @Before
    public void setUp() throws Exception {
        ipsProject = newIpsProject("TestProject");

        enum1 = newEnumType(ipsProject, "enum1");
        enum1.setAbstract(false);
        enum1.setExtensible(true);
        enum1.newEnumLiteralNameAttribute();

        IEnumAttribute attr1 = enum1.newEnumAttribute();
        attr1.setDatatype(Datatype.STRING.getQualifiedName());
        attr1.setName("id");
        attr1.setUnique(true);
        attr1.setIdentifier(true);

        IEnumAttribute attr2 = enum1.newEnumAttribute();
        attr2.setDatatype(Datatype.STRING.getQualifiedName());
        attr2.setName("name");
        attr2.setUnique(true);
        attr2.setUsedAsNameInFaktorIpsUi(true);

        IEnumAttribute attr3 = enum1.newEnumAttribute();
        attr3.setDatatype(Datatype.STRING.getQualifiedName());
        attr3.setName("description");
        attr3.setUnique(false);

        IEnumValue enumValue = enum1.newEnumValue();
        List<IEnumAttributeValue> values = enumValue.getEnumAttributeValues();
        values.get(0).setValue(ValueFactory.createStringValue("A"));
        values.get(1).setValue(ValueFactory.createStringValue("a"));
        values.get(2).setValue(ValueFactory.createStringValue("aname"));
        values.get(3).setValue(ValueFactory.createStringValue("adesc"));

        IEnumValue enumValue2 = enum1.newEnumValue();
        values = enumValue2.getEnumAttributeValues();
        values.get(0).setValue(ValueFactory.createStringValue("B"));
        values.get(1).setValue(ValueFactory.createStringValue("b"));
        values.get(2).setValue(ValueFactory.createStringValue("bname"));
        values.get(3).setValue(ValueFactory.createStringValue("bdesc"));

        IEnumValue enumValue3 = enum1.newEnumValue();
        values = enumValue3.getEnumAttributeValues();
        values.get(0).setValue(ValueFactory.createStringValue("C"));
        values.get(1).setValue(ValueFactory.createStringValue("c"));
        values.get(2).setValue(ValueFactory.createStringValue("cname"));
        values.get(3).setValue(ValueFactory.createStringValue("cdesc"));

        IpsPreferences ipsPreferences = IpsPlugin.getDefault().getIpsPreferences();
        ipsPreferences.setEnumTypeDisplay(EnumTypeDisplay.NAME);
    }

    @Test
    public void testGetDatatypeValueIds() throws Exception {
        Shell shell = new Shell(Display.getDefault());
        Combo combo = new Combo(shell, SWT.None);
        EnumTypeDatatypeField field = new EnumTypeDatatypeField(combo, new EnumTypeDatatypeAdapter(enum1, null));
        field.setValue("a");
        assertEquals("a", field.getValue());
        assertEquals("aname", field.getText());

        field.setValue("b");
        assertEquals("b", field.getValue());
        assertEquals("bname", field.getText());

        field.setValue(null);
        assertNull(field.getValue());
        assertEquals(IpsPlugin.getDefault().getIpsPreferences().getNullPresentation(), field.getText());
    }

    @Test
    public void testGetDatatypeValueIdsWithEnumContent() throws Exception {
        Shell shell = new Shell(Display.getDefault());

        enum1.setExtensible(true);
        enum1.getEnumLiteralNameAttribute().delete();
        enum1.setEnumContentName(enum1.getIpsPackageFragment().getName());

        IEnumContent enumContent = newEnumContent(enum1, "enum1Content");
        IEnumValue value1 = enumContent.newEnumValue();
        List<IEnumAttributeValue> values = value1.getEnumAttributeValues();
        values.get(0).setValue(ValueFactory.createStringValue("AContent"));
        values.get(1).setValue(ValueFactory.createStringValue("ANameContent"));
        values.get(2).setValue(ValueFactory.createStringValue("ADescContent"));

        IEnumValue value2 = enumContent.newEnumValue();
        values = value2.getEnumAttributeValues();
        values.get(0).setValue(ValueFactory.createStringValue("BContent"));
        values.get(1).setValue(ValueFactory.createStringValue("BNameContent"));
        values.get(2).setValue(ValueFactory.createStringValue("BDescContent"));

        IEnumValue value3 = enumContent.newEnumValue();
        values = value3.getEnumAttributeValues();
        values.get(0).setValue(ValueFactory.createStringValue("CContent"));
        values.get(1).setValue(ValueFactory.createStringValue("CNameContent"));
        values.get(2).setValue(ValueFactory.createStringValue("CDescContent"));

        Combo combo = new Combo(shell, SWT.None);
        EnumTypeDatatypeField field = new EnumTypeDatatypeField(combo, new EnumTypeDatatypeAdapter(enum1, enumContent));
        field.setValue("AContent");
        assertEquals("AContent", field.getValue());
        assertEquals("ANameContent", field.getText());

        field.setValue("BContent");
        assertEquals("BContent", field.getValue());
        assertEquals("BNameContent", field.getText());

        field.setValue(null);
        assertNull(field.getValue());
        assertEquals(IpsPlugin.getDefault().getIpsPreferences().getNullPresentation(), field.getText());

        field.setEnableEnumContentDisplay(false);
        field.setValue(null);
        assertNull(field.getValue());
        assertEquals(IpsPlugin.getDefault().getIpsPreferences().getNullPresentation(), field.getText());
        assertNull(field.getInvalidValue());

        field.setValue("AContent");
        assertEquals("AContent", field.getValue());
        assertEquals("AContent", field.getInvalidValue());
    }

}
