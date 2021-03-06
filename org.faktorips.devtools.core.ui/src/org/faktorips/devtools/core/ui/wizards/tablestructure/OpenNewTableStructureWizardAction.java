/*******************************************************************************
 * Copyright (c) Faktor Zehn AG. <http://www.faktorzehn.org>
 * 
 * This source code is available under the terms of the AGPL Affero General Public License version
 * 3.
 * 
 * Please see LICENSE.txt for full license terms, including the additional permissions and
 * restrictions as well as the possibility of alternative license terms.
 *******************************************************************************/

package org.faktorips.devtools.core.ui.wizards.tablestructure;

import org.eclipse.ui.INewWizard;
import org.faktorips.devtools.core.ui.wizards.OpenNewWizardAction;

/**
 *
 */
public class OpenNewTableStructureWizardAction extends OpenNewWizardAction {

    /**
     * Overridden method.
     * 
     * @see org.faktorips.devtools.core.ui.wizards.OpenNewWizardAction#createWizard()
     */
    @Override
    public INewWizard createWizard() {
        return new NewTableStructureWizard();
    }

    /**
     * Overridden method.
     * 
     * @see org.eclipse.ui.IWorkbenchWindowActionDelegate#dispose()
     */
    @Override
    public void dispose() {
        // nothing to do
    }

}
