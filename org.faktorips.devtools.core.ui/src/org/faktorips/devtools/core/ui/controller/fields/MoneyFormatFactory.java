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

package org.faktorips.devtools.core.ui.controller.fields;

import java.util.Currency;

import org.faktorips.datatype.ValueDatatype;
import org.faktorips.devtools.core.ui.IInputFormatFactory;

/**
 * Factory that creates a MoneyFormat for a Datatype
 * 
 */
public class MoneyFormatFactory implements IInputFormatFactory<String> {

    @Override
    public IInputFormat<String> newInputFormat(ValueDatatype datatype) {
        Currency currency = Currency.getInstance(datatype.getDefaultValue());
        return MoneyFormat.newInstance(currency);
    }
}
