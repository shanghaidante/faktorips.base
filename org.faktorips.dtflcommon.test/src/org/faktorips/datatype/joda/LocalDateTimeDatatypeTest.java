/*******************************************************************************
 * Copyright (c) Faktor Zehn AG. <http://www.faktorzehn.org>
 * 
 * This source code is available under the terms of the AGPL Affero General Public License version 3.
 * 
 * Please see LICENSE.txt for full license terms, including the additional permissions and the
 * possibility of alternative license terms.
 *******************************************************************************/

package org.faktorips.datatype.joda;

import junit.framework.TestCase;

import org.junit.Test;

public class LocalDateTimeDatatypeTest extends TestCase {

    private LocalDateTimeDatatype datatype;

    @Test
    public void testIsParsable() {
        datatype = new LocalDateTimeDatatype();
        assertTrue(datatype.isParsable(null));
        assertTrue(datatype.isParsable(""));
        assertTrue(datatype.isParsable("2013-11-13 10:44:00"));
        assertFalse(datatype.isParsable("2013-11-13 24:61:61"));
    }
}
