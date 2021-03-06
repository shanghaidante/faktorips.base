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

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.faktorips.codegen.JavaCodeFragment;
import org.faktorips.devtools.core.model.pctype.IPolicyCmptTypeAttribute;
import org.faktorips.devtools.core.model.productcmpttype.IProductCmptTypeAttribute;
import org.faktorips.devtools.stdbuilder.xpand.GeneratorModelContext;
import org.faktorips.devtools.stdbuilder.xpand.policycmpt.model.XPolicyAttribute;
import org.faktorips.devtools.stdbuilder.xpand.productcmpt.model.XProductAssociation;
import org.faktorips.devtools.stdbuilder.xpand.productcmpt.model.XProductAttribute;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class AttributeAllowedValuesAnnGenTest {

    @Mock
    private GeneratorModelContext modelContext;

    private AttributeAllowedValuesAnnGen attributeAllowedValuesAnnGen = new AttributeAllowedValuesAnnGen();

    @Test
    public void testIsGenerateAnnotationFor() throws Exception {
        assertThat(attributeAllowedValuesAnnGen.isGenerateAnnotationFor(mock(XPolicyAttribute.class)), is(true));
        assertThat(attributeAllowedValuesAnnGen.isGenerateAnnotationFor(mock(XProductAttribute.class)), is(true));
        assertThat(attributeAllowedValuesAnnGen.isGenerateAnnotationFor(mock(XProductAssociation.class)), is(false));
    }

    @Test
    public void testCreateAnnotation_product() throws Exception {
        XProductAttribute xProductAttribute = xProductAttribute("foo");

        JavaCodeFragment codeFragment = attributeAllowedValuesAnnGen.createAnnotation(xProductAttribute);

        assertThat(codeFragment.getSourcecode(),
                is(equalTo("@IpsAllowedValues(\"foo\")" + System.getProperty("line.separator"))));
    }

    @Test
    public void testCreateAnnotation_policy() throws Exception {
        XPolicyAttribute xPolicyAttribute = xPolicyAttribute("bar");

        JavaCodeFragment codeFragment = attributeAllowedValuesAnnGen.createAnnotation(xPolicyAttribute);

        assertThat(codeFragment.getSourcecode(),
                is(equalTo("@IpsAllowedValues(\"bar\")" + System.getProperty("line.separator"))));
    }

    private XPolicyAttribute xPolicyAttribute(String name) {
        IPolicyCmptTypeAttribute policyCmptTypeAttribute = mock(IPolicyCmptTypeAttribute.class);
        when(policyCmptTypeAttribute.getName()).thenReturn(name);
        return new XPolicyAttribute(policyCmptTypeAttribute, modelContext, null);
    }

    private XProductAttribute xProductAttribute(String name) {
        IProductCmptTypeAttribute productCmptTypeAttribute = mock(IProductCmptTypeAttribute.class);
        when(productCmptTypeAttribute.getName()).thenReturn(name);
        return new XProductAttribute(productCmptTypeAttribute, modelContext, null);
    }

}
