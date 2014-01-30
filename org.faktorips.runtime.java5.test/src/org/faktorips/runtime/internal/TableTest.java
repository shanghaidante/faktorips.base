/*******************************************************************************
 * Copyright (c) Faktor Zehn AG. <http://www.faktorzehn.org>
 * 
 * This source code is available under the terms of the AGPL Affero General Public License version 3.
 * 
 * Please see LICENSE.txt for full license terms, including the additional permissions and the
 * possibility of alternative license terms.
 *******************************************************************************/

package org.faktorips.runtime.internal;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.io.InputStream;

import org.faktorips.runtime.XmlAbstractTestCase;
import org.faktorips.values.Decimal;
import org.junit.Before;
import org.junit.Test;

/**
 * 
 * @author Peter Erzberger
 */
public class TableTest extends XmlAbstractTestCase {

    private TestTable table;

    @Before
    public void setUp() throws Exception {
        table = new TestTable();
    }

    @Test
    public void testInitFromXmlViaSax() throws Exception {
        String className = getClass().getName();
        int index = className.lastIndexOf('.');
        if (index > -1) {
            className = className.substring(index + 1);
        }
        String resourceName = className + ".xml";
        InputStream is = getClass().getResourceAsStream(resourceName);
        if (is == null) {
            throw new RuntimeException("Can't find resource " + resourceName);
        }

        table.initFromXml(is, null);

        TestTableRow row = table.rows.get(0);
        assertNull(row.getCompany());
        assertEquals(Integer.valueOf("0"), row.getGender());
        assertEquals(Decimal.valueOf("0.1"), row.getRate());

        row = table.rows.get(1);
        assertEquals("KQV", row.getCompany());
        assertEquals(Integer.valueOf("1"), row.getGender());
        assertEquals(Decimal.valueOf("0.15"), row.getRate());

        row = table.rows.get(5);
        assertEquals("BBV", row.getCompany());
        assertEquals(Integer.valueOf("1"), row.getGender());
        assertEquals(Decimal.valueOf("0.35"), row.getRate());
    }

}
