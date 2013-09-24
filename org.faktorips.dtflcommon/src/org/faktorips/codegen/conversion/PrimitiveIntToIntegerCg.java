/*******************************************************************************
 * Copyright (c) 2005-2012 Faktor Zehn AG und andere.
 * 
 * Alle Rechte vorbehalten.
 * 
 * Dieses Programm und alle mitgelieferten Sachen (Dokumentationen, Beispiele, Konfigurationen,
 * etc.) duerfen nur unter den Bedingungen der Faktor-Zehn-Community Lizenzvereinbarung - Version
 * 0.1 (vor Gruendung Community) genutzt werden, die Bestandteil der Auslieferung ist und auch unter
 * http://www.faktorzehn.org/fips:lizenz eingesehen werden kann.
 * 
 * Mitwirkende: Faktor Zehn AG - initial API and implementation - http://www.faktorzehn.de
 *******************************************************************************/

package org.faktorips.codegen.conversion;

import org.faktorips.codegen.JavaCodeFragment;
import org.faktorips.codegen.dthelpers.PrimitiveIntegerHelper;
import org.faktorips.datatype.Datatype;
import org.faktorips.datatype.PrimitiveIntegerDatatype;

public class PrimitiveIntToIntegerCg extends AbstractSingleConversionCg {

    public PrimitiveIntToIntegerCg() {
        super(Datatype.PRIMITIVE_INT, Datatype.INTEGER);
    }

    public JavaCodeFragment getConversionCode(JavaCodeFragment fromValue) {
        return new PrimitiveIntegerHelper((PrimitiveIntegerDatatype)Datatype.PRIMITIVE_INT).toWrapper(fromValue);
    }

}
