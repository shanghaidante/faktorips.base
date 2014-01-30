/*******************************************************************************
 * Copyright (c) Faktor Zehn AG. <http://www.faktorzehn.org>
 * 
 * This source code is available under the terms of the AGPL Affero General Public License version 3.
 * 
 * Please see LICENSE.txt for full license terms, including the additional permissions and the
 * possibility of alternative license terms.
 *******************************************************************************/

package org.faktorips.devtools.core.ui.editors.productcmpt;

import java.util.List;

import org.eclipse.swt.widgets.Composite;
import org.faktorips.devtools.core.model.productcmpt.ITableContentUsage;
import org.faktorips.devtools.core.model.productcmpttype.ITableStructureUsage;
import org.faktorips.devtools.core.ui.UIToolkit;
import org.faktorips.devtools.core.ui.binding.BindingContext;
import org.faktorips.devtools.core.ui.controller.EditField;
import org.faktorips.devtools.core.ui.controller.fields.TextButtonField;
import org.faktorips.devtools.core.ui.controls.TableContentsUsageRefControl;
import org.faktorips.devtools.core.ui.forms.IpsSection;

/**
 * Provides controls that allow the user to edit an {@link ITableContentUsage}.
 * 
 * @since 3.6
 * 
 * @author Alexander Weickmann
 * 
 * @see ITableContentUsage
 */
public class TableContentUsageEditComposite extends
        EditPropertyValueComposite<ITableStructureUsage, ITableContentUsage> {

    public TableContentUsageEditComposite(ITableStructureUsage property, ITableContentUsage propertyValue,
            IpsSection parentSection, Composite parent, BindingContext bindingContext, UIToolkit toolkit) {

        super(property, propertyValue, parentSection, parent, bindingContext, toolkit);
        initControls();
    }

    @Override
    protected void createEditFields(List<EditField<?>> editFields) {
        createTableContentEditField(editFields);
    }

    private void createTableContentEditField(List<EditField<?>> editFields) {
        TableContentsUsageRefControl tcuControl = new TableContentsUsageRefControl(getPropertyValue().getIpsProject(),
                this, getToolkit(), getPropertyValue());

        TextButtonField editField = new TextButtonField(tcuControl);
        editFields.add(editField);
        getBindingContext().bindContent(editField, getPropertyValue(), ITableContentUsage.PROPERTY_TABLE_CONTENT);
    }

}
