/*******************************************************************************
 * Copyright (c) Faktor Zehn AG. <http://www.faktorzehn.org>
 * 
 * This source code is available under the terms of the AGPL Affero General Public License version
 * 3.
 * 
 * Please see LICENSE.txt for full license terms, including the additional permissions and
 * restrictions as well as the possibility of alternative license terms.
 *******************************************************************************/
package org.faktorips.devtools.core.internal.model.productcmpt;

import java.beans.PropertyChangeEvent;
import java.util.Locale;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.runtime.CoreException;
import org.faktorips.devtools.core.internal.model.productcmpt.template.TemplateValueFinder;
import org.faktorips.devtools.core.internal.model.productcmpt.template.TemplateValueSettings;
import org.faktorips.devtools.core.model.ipsproject.IIpsProject;
import org.faktorips.devtools.core.model.pctype.IPolicyCmptType;
import org.faktorips.devtools.core.model.pctype.IValidationRule;
import org.faktorips.devtools.core.model.productcmpt.IPropertyValueContainer;
import org.faktorips.devtools.core.model.productcmpt.IValidationRuleConfig;
import org.faktorips.devtools.core.model.productcmpt.PropertyValueType;
import org.faktorips.devtools.core.model.productcmpt.template.TemplateValueStatus;
import org.faktorips.devtools.core.model.type.IProductCmptProperty;
import org.faktorips.util.ArgumentCheck;
import org.faktorips.util.message.MessageList;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class ValidationRuleConfig extends AbstractSimplePropertyValue implements IValidationRuleConfig {

    public static final String TAG_NAME = "ValidationRuleConfig"; //$NON-NLS-1$

    public static final String TAG_NAME_ACTIVE = "active"; //$NON-NLS-1$
    public static final String TAG_NAME_RULE_NAME = "ruleName"; //$NON-NLS-1$

    private boolean isActive = false;

    private String validationRuleName;

    private final TemplateValueSettings templateValueSettings;

    public ValidationRuleConfig(IPropertyValueContainer parent, String id, String ruleName) {
        super(parent, id);
        this.validationRuleName = ruleName;
        this.templateValueSettings = new TemplateValueSettings(this);
    }

    @Override
    public boolean isActive() {
        if (getTemplateValueStatus() == TemplateValueStatus.INHERITED) {
            return findTemplateActiveState();
        }

        if (getTemplateValueStatus() == TemplateValueStatus.UNDEFINED) {
            return false;
        }

        return isActive;
    }

    private boolean findTemplateActiveState() {
        IValidationRuleConfig template = findTemplateProperty(getIpsProject());
        if (template == null) {
            // Template should exist but does not. Use the "last known" value as a more or less
            // helpful fallback while some validation hopefully addresses the missing template...
            return isActive;
        }
        return template.isActive();
    }

    @Override
    protected Element createElement(Document doc) {
        return doc.createElement(TAG_NAME);
    }

    @Override
    protected void initPropertiesFromXml(Element element, String id) {
        super.initPropertiesFromXml(element, id);
        validationRuleName = element.getAttribute(TAG_NAME_RULE_NAME);
        isActive = Boolean.valueOf(element.getAttribute(TAG_NAME_ACTIVE));
        templateValueSettings.initPropertiesFromXml(element);
    }

    @Override
    protected void propertiesToXml(Element element) {
        super.propertiesToXml(element);
        element.setAttribute(TAG_NAME_RULE_NAME, validationRuleName);
        element.setAttribute(TAG_NAME_ACTIVE, Boolean.toString(isActive()));
        templateValueSettings.propertiesToXml(element);
    }

    @Override
    public IValidationRule findValidationRule(IIpsProject ipsProject) throws CoreException {
        IPropertyValueContainer propertyValueContainer = (IPropertyValueContainer)getParent();
        IPolicyCmptType pcType = propertyValueContainer.findPolicyCmptType(ipsProject);
        if (pcType != null) {
            IValidationRule rule = pcType.findValidationRule(validationRuleName, ipsProject);
            return rule;
        }
        return null;
    }

    /**
     * @return the name of this {@link IValidationRuleConfig}. That name is also the name of the
     *         configured {@link IValidationRule}.
     */
    @Override
    public String getName() {
        return validationRuleName;
    }

    @Override
    public void setValidationRuleName(String validationRuleName) {
        String oldValue = this.validationRuleName;
        this.validationRuleName = validationRuleName;
        valueChanged(oldValue, validationRuleName);
    }

    @Override
    public String getValidationRuleName() {
        return validationRuleName;
    }

    @Override
    public void setActive(boolean active) {
        boolean oldValue = isActive;
        isActive = active;
        valueChanged(oldValue, active);
    }

    @Override
    public String getCaption(Locale locale) throws CoreException {
        ArgumentCheck.notNull(locale);

        String caption = null;
        IValidationRule rule = findValidationRule(getIpsProject());
        if (rule != null) {
            caption = rule.getLabelValue(locale);
        }
        return caption;
    }

    @Override
    public String getLastResortCaption() {
        return StringUtils.capitalize(validationRuleName);
    }

    @Override
    public String getPropertyName() {
        return getName();
    }

    @Override
    public IProductCmptProperty findProperty(IIpsProject ipsProject) throws CoreException {
        return findValidationRule(ipsProject);
    }

    @Override
    public PropertyValueType getPropertyValueType() {
        return PropertyValueType.VALIDATION_RULE_CONFIG;
    }

    @Override
    public Boolean getPropertyValue() {
        return isActive();
    }

    @Override
    public void setTemplateValueStatus(TemplateValueStatus newStatus) {
        if (newStatus == TemplateValueStatus.DEFINED) {
            // Copy current active state from template (if present)
            this.isActive = isActive();
        }
        TemplateValueStatus oldStatus = templateValueSettings.getStatus();
        templateValueSettings.setStatus(newStatus);
        objectHasChanged(new PropertyChangeEvent(this, PROPERTY_TEMPLATE_VALUE_STATUS, oldStatus, newStatus));

    }

    @Override
    public TemplateValueStatus getTemplateValueStatus() {
        return templateValueSettings.getStatus();
    }

    @Override
    public IValidationRuleConfig findTemplateProperty(IIpsProject ipsProject) {
        return TemplateValueFinder.findTemplateValue(this, IValidationRuleConfig.class);
    }

    @Override
    protected void validateThis(MessageList list, IIpsProject ipsProject) throws CoreException {
        super.validateThis(list, ipsProject);
        list.add(templateValueSettings.validate(this, ipsProject));
    }

}
