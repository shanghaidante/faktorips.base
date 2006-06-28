/*******************************************************************************
 * Copyright (c) 2005,2006 Faktor Zehn GmbH und andere.
 *
 * Alle Rechte vorbehalten.
 *
 * Dieses Programm und alle mitgelieferten Sachen (Dokumentationen, Beispiele,
 * Konfigurationen, etc.) duerfen nur unter den Bedingungen der 
 * Faktor-Zehn-Community Lizenzvereinbarung - Version 0.1 (vor Gruendung Community) 
 * genutzt werden, die Bestandteil der Auslieferung ist und auch unter
 *   http://www.faktorips.org/legal/cl-v01.html
 * eingesehen werden kann.
 *
 * Mitwirkende:
 *   Faktor Zehn GmbH - initial API and implementation - http://www.faktorzehn.de
 *
 *******************************************************************************/

package org.faktorips.codegen.dthelpers;

import org.faktorips.codegen.JavaCodeFragment;
import org.faktorips.datatype.Datatype;

public class ArrayOfValueDatatypeHelper extends AbstractDatatypeHelper {

    public ArrayOfValueDatatypeHelper() {
        super();
    }

    public ArrayOfValueDatatypeHelper(Datatype datatype) {
        super(datatype);
    }

    protected JavaCodeFragment valueOfExpression(String expression) {
        return nullExpression();
    }

    public JavaCodeFragment nullExpression() {
        return new JavaCodeFragment("null");
    }

    public JavaCodeFragment newInstance(String value) {
        return nullExpression();
    }

}
