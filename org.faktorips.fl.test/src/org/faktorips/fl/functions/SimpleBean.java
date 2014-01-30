/*******************************************************************************
 * Copyright (c) Faktor Zehn AG. <http://www.faktorzehn.org>
 * 
 * This source code is available under the terms of the AGPL Affero General Public License version 3.
 * 
 * Please see LICENSE.txt for full license terms, including the additional permissions and the
 * possibility of alternative license terms.
 *******************************************************************************/

package org.faktorips.fl.functions;

import org.faktorips.values.Decimal;

/**
 * SimpleBean for testing purposes.
 * 
 * @author Jan Ortmann
 */
public class SimpleBean {

    public SimpleBean() {
        super();
    }

    public SimpleBean(Decimal value) {
        this.value = value;
    }

    private Decimal value;

    public Decimal getValue() {
        return value;
    }

    public void setValue(Decimal value) {
        this.value = value;
    }

}
