/*******************************************************************************
 * Copyright (c) Faktor Zehn AG. <http://www.faktorzehn.org>
 * 
 * This source code is available under the terms of the AGPL Affero General Public License version
 * 3.
 * 
 * Please see LICENSE.txt for full license terms, including the additional permissions and
 * restrictions as well as the possibility of alternative license terms.
 *******************************************************************************/
package org.faktorips.devtools.core.ui.editors.tablecontents;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Shell;
import org.faktorips.devtools.core.model.tablecontents.ITableContents;
import org.faktorips.devtools.core.model.tablestructure.IColumn;
import org.faktorips.devtools.core.model.tablestructure.ITableStructure;
import org.faktorips.devtools.core.ui.IpsUIPlugin;
import org.faktorips.devtools.core.ui.editors.IpsObjectEditorPage;
import org.faktorips.devtools.core.ui.editors.enumcontent.Messages;
import org.faktorips.devtools.core.ui.wizards.fixcontent.FixContentWizard;
import org.faktorips.devtools.core.ui.wizards.tablecontents.FixTableContentStrategy;
import org.faktorips.util.ArgumentCheck;

public class OpenFixTableContentWizardAction extends Action {

    /** The name of the image for the action. */
    private static final String IMAGE_NAME = "BrokenTable.gif"; //$NON-NLS-1$

    /** The <tt>IEnumContent</tt> to fix. */
    private ITableContents tableContents;

    /** The parent shell. */
    private Shell parentShell;

    /** The editor page that requested the operation or <tt>null</tt>. */
    private IpsObjectEditorPage editorPage;

    /**
     * Creates a new <tt>OpenFixEnumContentWizardAction</tt>.
     * 
     * @param editorPage The <tt>IpsObjectEditorPage</tt> that requested the operation or
     *            <tt>null</tt> (the page will be refreshed after the operation was performed if
     *            given).
     * @param parentShell The parent shell.
     * 
     * @throws NullPointerException If <tt>enumContent</tt> or <tt>parentShell</tt> is <tt>null</tt>
     *             .
     */
    public OpenFixTableContentWizardAction(IpsObjectEditorPage editorPage, ITableContents tableContents,
            Shell parentShell) {
        super();
        ArgumentCheck.notNull(new Object[] { tableContents, parentShell });

        this.tableContents = tableContents;
        this.parentShell = parentShell;
        this.editorPage = editorPage;

        setImageDescriptor(IpsUIPlugin.getImageHandling().createImageDescriptor(IMAGE_NAME));
        setText(Messages.EnumContentPage_labelOpenFixEnumTypeDialog);
        setToolTipText(Messages.EnumContentPage_tooltipOpenFixEnumTypeDialog);
    }

    @Override
    public void run() {
        FixContentWizard<ITableStructure, IColumn> wizard = new FixContentWizard<ITableStructure, IColumn>(
                tableContents, new FixTableContentStrategy(tableContents));
        WizardDialog dialog = new WizardDialog(parentShell, wizard);
        dialog.open();
        if (editorPage != null) {
            editorPage.refresh();
        }
    }

}
