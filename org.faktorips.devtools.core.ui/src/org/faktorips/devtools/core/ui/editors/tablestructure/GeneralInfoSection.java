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

package org.faktorips.devtools.core.ui.editors.tablestructure;

import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.widgets.Section;
import org.faktorips.devtools.core.internal.model.tablestructure.TableStructureType;
import org.faktorips.devtools.core.model.ipsobject.IExtensionPropertyDefinition;
import org.faktorips.devtools.core.model.tablestructure.ITableStructure;
import org.faktorips.devtools.core.ui.ExtensionPropertyControlFactory;
import org.faktorips.devtools.core.ui.UIToolkit;
import org.faktorips.devtools.core.ui.forms.IpsSection;

/**
 * Shows a table structure's general properties and allows to edit them.
 * 
 * @author Thorsten Waertel
 */
public class GeneralInfoSection extends IpsSection {

    private final ExtensionPropertyControlFactory extFactory;

    private final ITableStructure tableStructure;

    public GeneralInfoSection(ITableStructure tableStructure, Composite parent, UIToolkit toolkit) {
        super(parent, Section.TITLE_BAR, GridData.FILL_HORIZONTAL, toolkit);

        this.tableStructure = tableStructure;
        extFactory = new ExtensionPropertyControlFactory(tableStructure.getClass());

        initControls();
        setText(Messages.GeneralInfoSection_labelGeneralInfoSection);
    }

    @Override
    protected void initClientComposite(Composite client, UIToolkit toolkit) {
        client.setLayout(new GridLayout(1, false));
        Composite composite = toolkit.createLabelEditColumnComposite(client);

        toolkit.createFormLabel(composite, Messages.GeneralInfoSection_labelTableType);
        Combo combo = toolkit.createCombo(composite);

        getBindingContext().bindContent(combo, tableStructure, ITableStructure.PROPERTY_TYPE,
                new TableStructureType[] { TableStructureType.SINGLE_CONTENT, TableStructureType.MULTIPLE_CONTENTS });

        extFactory.createControls(composite, toolkit, tableStructure, IExtensionPropertyDefinition.POSITION_TOP);
        extFactory.bind(getBindingContext());
    }

}
