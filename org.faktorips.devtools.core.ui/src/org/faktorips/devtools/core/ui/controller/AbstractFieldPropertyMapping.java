/*******************************************************************************
 * Copyright (c) Faktor Zehn AG. <http://www.faktorzehn.org>
 * 
 * This source code is available under the terms of the AGPL Affero General Public License version
 * 3.
 * 
 * Please see LICENSE.txt for full license terms, including the additional permissions and
 * restrictions as well as the possibility of alternative license terms.
 *******************************************************************************/
package org.faktorips.devtools.core.ui.controller;

import org.apache.commons.lang.ObjectUtils;
import org.faktorips.devtools.core.ui.controller.fields.AbstractEnumDatatypeBasedField;
import org.faktorips.devtools.core.ui.controller.fields.RadioButtonGroupField;
import org.faktorips.devtools.core.ui.controller.fields.StringValueComboField;

public abstract class AbstractFieldPropertyMapping<T> implements FieldPropertyMapping<T> {

    private EditField<T> field;
    private Object object;
    private String propertyName;

    public AbstractFieldPropertyMapping(EditField<T> edit, Object object, String propertyName) {
        this.field = edit;
        this.object = object;
        this.propertyName = propertyName;
    }

    @Override
    public EditField<T> getField() {
        return field;
    }

    @Override
    public Object getObject() {
        return object;
    }

    @Override
    public String getPropertyName() {
        return propertyName;
    }

    @Override
    public String toString() {
        return object.getClass().getName() + '.' + propertyName + '-' + field;
    }

    @Override
    public void setPropertyValue() {
        if (getField().getControl().isDisposed()) {
            return;
        }
        if (!getField().isTextContentParsable()) {
            return;
        }
        if (ObjectUtils.equals(getPropertyValue(), getField().getValue())) {
            // value hasn't changed
            return;
        }
        setPropertyValueInternal();
    }

    protected abstract void setPropertyValueInternal();

    @Override
    public void setControlValue() {
        try {
            if (getField().getControl().isDisposed()) {
                return;
            }
            T propertyValue = getPropertyValue();

            // if we have a field which maintans a list - update it.
            if (getField() instanceof AbstractEnumDatatypeBasedField) {
                ((AbstractEnumDatatypeBasedField)getField()).reInit();
            }

            if (getField().isTextContentParsable() && ObjectUtils.equals(propertyValue, getField().getValue())) {
                if (getField() instanceof StringValueComboField) {
                    /*
                     * special case: if the field is a combo field the getValue method returns null
                     * if there is no selection and if the null value is selected, therefore we must
                     * check here if the getValue is a valid selection or nothing is selected. If
                     * there is no valid selection set the new value (e.g. the null value)
                     */
                    if (((StringValueComboField)getField()).getCombo().getSelectionIndex() != -1) {
                        // the selection in the combo is valid and equal to the property value,
                        // don't set the new value
                        return;
                    }
                } else if (getField() instanceof RadioButtonGroupField) {
                    /*
                     * Unfortunately, the same special case applies to radio button groups as well.
                     */
                    if (((RadioButtonGroupField<?>)getField()).getRadioButtonGroup().getSelectedButton() != null) {
                        return;
                    }
                } else {
                    return;
                }
            }
            getField().setValue(propertyValue, false);
            // CSOFF: IllegalCatch
        } catch (Exception e) {
            throw new RuntimeException("Error setting value in control for property " + getPropertyName(), e); //$NON-NLS-1$
        }
        // CSON: IllegalCatch
    }

}