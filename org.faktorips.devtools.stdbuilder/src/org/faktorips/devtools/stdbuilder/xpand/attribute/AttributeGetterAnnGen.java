/*******************************************************************************
 * Copyright (c) Faktor Zehn AG. <http://www.faktorzehn.org>
 * 
 * This source code is available under the terms of the AGPL Affero General Public License version
 * 3.
 * 
 * Please see LICENSE.txt for full license terms, including the additional permissions and
 * restrictions as well as the possibility of alternative license terms.
 *******************************************************************************/
package org.faktorips.devtools.stdbuilder.xpand.attribute;

import org.faktorips.codegen.JavaCodeFragment;
import org.faktorips.codegen.JavaCodeFragmentBuilder;
import org.faktorips.devtools.core.model.pctype.IPolicyCmptTypeAttribute;
import org.faktorips.devtools.core.model.type.IAttribute;
import org.faktorips.devtools.stdbuilder.IAnnotationGenerator;
import org.faktorips.devtools.stdbuilder.xpand.model.AbstractGeneratorModelNode;
import org.faktorips.devtools.stdbuilder.xpand.model.XAttribute;
import org.faktorips.devtools.stdbuilder.xpand.policycmpt.model.XPolicyAttribute;
import org.faktorips.runtime.model.annotation.IpsAttribute;
import org.faktorips.runtime.model.annotation.IpsConfiguredAttribute;
import org.faktorips.runtime.modeltype.IModelTypeAttribute.AttributeType;
import org.faktorips.runtime.modeltype.IModelTypeAttribute.ValueSetType;

/**
 * Generates the {@link IpsAttribute} annotation on attribute getter methods.
 */
public class AttributeGetterAnnGen implements IAnnotationGenerator {

    @Override
    public JavaCodeFragment createAnnotation(AbstractGeneratorModelNode modelNode) {
        XAttribute xAttribute = (XAttribute)modelNode;
        IAttribute attribute = xAttribute.getAttribute();
        JavaCodeFragmentBuilder annotationCode = new JavaCodeFragmentBuilder();
        annotationCode.append("@");
        annotationCode.appendClassName(IpsAttribute.class);
        annotationCode.append("(name = \"");
        annotationCode.append(attribute.getName());
        annotationCode.append("\", type = ");
        annotationCode.appendClassName(AttributeType.class);
        annotationCode.append(".");
        annotationCode.append(getAttributeType(attribute).name());
        annotationCode.append(", valueSetType = ");
        annotationCode.appendClassName(ValueSetType.class);
        annotationCode.append(".");
        annotationCode.append(getValueSetType(attribute).name());
        annotationCode.append(")");
        if (xAttribute instanceof XPolicyAttribute && ((XPolicyAttribute)xAttribute).isProductRelevant()) {
            annotationCode.appendln();
            annotationCode.append("@");
            annotationCode.appendClassName(IpsConfiguredAttribute.class);
            annotationCode.append("(changingOverTime = ");
            annotationCode.append(Boolean.toString(xAttribute.isChangingOverTime()));
            annotationCode.append(")");
        }
        return annotationCode.getFragment();
    }

    private AttributeType getAttributeType(IAttribute attribute) {
        if (attribute instanceof IPolicyCmptTypeAttribute) {
            return AttributeType.forName(((IPolicyCmptTypeAttribute)attribute).getAttributeType().getId());
        } else {
            return AttributeType.CONSTANT;
        }
    }

    private ValueSetType getValueSetType(IAttribute attribute) {
        ValueSetType valueSetType = null;
        switch (attribute.getValueSet().getValueSetType()) {
            case ENUM:
                valueSetType = ValueSetType.Enum;
                break;
            case RANGE:
                valueSetType = ValueSetType.Range;
                break;
            default:
                valueSetType = ValueSetType.AllValues;
        }
        return valueSetType;
    }

    @Override
    public boolean isGenerateAnnotationFor(AbstractGeneratorModelNode modelNode) {
        return modelNode instanceof XAttribute;
    }
}
