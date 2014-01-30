/*******************************************************************************
 * Copyright (c) Faktor Zehn AG. <http://www.faktorzehn.org>
 * 
 * This source code is available under the terms of the AGPL Affero General Public License version 3.
 * 
 * Please see LICENSE.txt for full license terms, including the additional permissions and the
 * possibility of alternative license terms.
 *******************************************************************************/

package org.faktorips.codegen.dthelpers;

import static org.junit.Assert.assertEquals;

import org.faktorips.codegen.JavaCodeFragment;
import org.junit.Before;
import org.junit.Test;

public class StringHelperTest {

    private StringHelper helper;

    @Before
    public void setUp() {
        helper = new StringHelper();
    }

    @Test
    public void testDoNotEscapeForwardSlash() {
        JavaCodeFragment fragment = helper.newInstance("/");
        assertEquals("\"/\"", fragment.getSourcecode());
    }

}
