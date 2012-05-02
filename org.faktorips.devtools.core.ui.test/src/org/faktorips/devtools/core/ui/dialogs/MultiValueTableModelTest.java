/*******************************************************************************
 * Copyright (c) 2005-2012 Faktor Zehn AG und andere.
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

package org.faktorips.devtools.core.ui.dialogs;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;

import java.util.ArrayList;
import java.util.List;

import org.faktorips.abstracttest.AbstractIpsPluginTest;
import org.faktorips.devtools.core.internal.model.productcmpt.AttributeValue;
import org.faktorips.devtools.core.internal.model.productcmpt.MultiValueHolder;
import org.faktorips.devtools.core.internal.model.productcmpt.SingleValueHolder;
import org.faktorips.devtools.core.model.ipsproject.IIpsProject;
import org.faktorips.devtools.core.model.productcmpt.IAttributeValue;
import org.faktorips.devtools.core.model.productcmpt.IProductCmpt;
import org.faktorips.devtools.core.ui.dialogs.MultiValueTableModel.SingleValueViewItem;
import org.junit.Before;
import org.junit.Test;

public class MultiValueTableModelTest extends AbstractIpsPluginTest {

    private IAttributeValue attributeValue;

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();

        IIpsProject ipsProject = newIpsProject();
        IProductCmpt component = newProductCmpt(ipsProject, "qName");
        attributeValue = new AttributeValue(component, "");
        List<SingleValueHolder> list = new ArrayList<SingleValueHolder>();
        list.add(new SingleValueHolder(attributeValue, "A"));
        list.add(new SingleValueHolder(attributeValue, "B"));
        list.add(new SingleValueHolder(attributeValue, "C"));

        MultiValueHolder multiValueHolder = new MultiValueHolder(attributeValue, list);
        attributeValue.setValueHolder(multiValueHolder);
    }

    @Test
    public void applyValueList() {
        MultiValueTableModel model = new MultiValueTableModel(attributeValue);
        model.addElement();
        assertEquals(4, getAttributeValueList().size());
        assertEquals(4, model.getElements().size());
        model.addElement();
        assertEquals(5, getAttributeValueList().size());
        assertEquals(5, model.getElements().size());

        model.applyValueList();
        assertEquals(5, getAttributeValueList().size());
        assertEquals(5, model.getElements().size());
        assertSame(getAttributeValueList().get(4), model.getElements().get(4).getSingleValueHolder());
    }

    protected List<SingleValueHolder> getAttributeValueList() {
        return ((MultiValueHolder)attributeValue.getValueHolder()).getValue();
    }

    @Test
    public void swapElements() {
        MultiValueTableModel model = new MultiValueTableModel(attributeValue);
        model.swapElements(1, 2);
        assertEquals(3, model.getElements().size());
        assertEquals("C", model.getElements().get(1).getSingleValueHolder().getValue());
        assertEquals("B", model.getElements().get(2).getSingleValueHolder().getValue());
        model.swapElements(1, 2);
        assertEquals(3, model.getElements().size());
        assertEquals("B", model.getElements().get(1).getSingleValueHolder().getValue());
        assertEquals("C", model.getElements().get(2).getSingleValueHolder().getValue());
    }

    @Test
    public void addElement() {
        MultiValueTableModel model = new MultiValueTableModel(attributeValue);
        model.addElement();
        assertEquals(4, model.getElements().size());
        assertEquals(null, model.getElements().get(3).getSingleValueHolder().getValue());
        model.addElement();
        assertEquals(5, model.getElements().size());
        assertEquals(null, model.getElements().get(4).getSingleValueHolder().getValue());
    }

    @Test
    public void removeElement() {
        MultiValueTableModel model = new MultiValueTableModel(attributeValue);
        model.removeElement(1);
        assertEquals(2, model.getElements().size());
        assertEquals("C", model.getElements().get(1).getSingleValueHolder().getValue());
        model.removeElement(0);
        assertEquals(1, model.getElements().size());
        assertEquals("C", model.getElements().get(0).getSingleValueHolder().getValue());
    }

    @Test
    public void treatViewItemsWithEqualValueAsNotEqual() {
        SingleValueHolder holder = new SingleValueHolder(attributeValue, "value");
        SingleValueViewItem item1 = new SingleValueViewItem(holder);
        SingleValueViewItem item2 = new SingleValueViewItem(holder);
        assertFalse(item1.equals(item2));
    }

    @Test
    public void treatViewItemsAsNotEqual() {
        SingleValueHolder holder = new SingleValueHolder(attributeValue, "value");
        SingleValueViewItem item1 = new SingleValueViewItem(holder);
        SingleValueViewItem item2 = new SingleValueViewItem(holder);
        List<SingleValueViewItem> items = new ArrayList<MultiValueTableModel.SingleValueViewItem>();
        items.add(item1);
        items.add(item2);

        assertEquals(0, items.indexOf(item1));
        assertEquals(1, items.indexOf(item2));
    }
}