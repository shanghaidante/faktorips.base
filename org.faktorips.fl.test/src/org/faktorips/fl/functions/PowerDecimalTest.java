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

package org.faktorips.fl.functions;

import org.faktorips.datatype.Datatype;
import org.faktorips.values.Decimal;
import org.junit.Test;

public class PowerDecimalTest extends FunctionAbstractTest {

    @Test
    public void testCompile() throws Exception {
        registerFunction(new PowerDecimal("POWER", ""));
        execAndTestSuccessfull("POWER(2.0; 3.0)", Decimal.valueOf("8.0"), Datatype.DECIMAL);
        execAndTestSuccessfull("POWER(4.0; 2.0)", Decimal.valueOf("16.0"), Datatype.DECIMAL);
    }
}