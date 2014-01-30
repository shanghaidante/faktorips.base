/*******************************************************************************
 * Copyright (c) Faktor Zehn AG. <http://www.faktorzehn.org>
 * 
 * This source code is available under the terms of the AGPL Affero General Public License version 3.
 * 
 * Please see LICENSE.txt for full license terms, including the additional permissions and the
 * possibility of alternative license terms.
 *******************************************************************************/

package org.faktorips.fl.functions;

import org.faktorips.codegen.JavaCodeFragment;
import org.faktorips.datatype.Datatype;
import org.faktorips.fl.CompilationResult;
import org.faktorips.fl.CompilationResultImpl;
import org.faktorips.values.Money;

/**
 * 
 * @author Jan Ortmann
 */
public class MoneyNullFct extends AbstractFlFunction {

    public MoneyNullFct() {
        super("MONEYNULL", "", Datatype.MONEY, new Datatype[0]);
    }

    /**
     * {@inheritDoc}
     */
    public CompilationResult<JavaCodeFragment> compile(CompilationResult<JavaCodeFragment>[] argResults) {
        JavaCodeFragment code = new JavaCodeFragment();
        code.appendClassName(Money.class);
        code.append(".NULL");
        return new CompilationResultImpl(code, Datatype.MONEY);
    }

}
