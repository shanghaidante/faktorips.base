/*******************************************************************************
 * Copyright (c) Faktor Zehn AG. <http://www.faktorzehn.org>
 * 
 * This source code is available under the terms of the AGPL Affero General Public License version 3.
 * 
 * Please see LICENSE.txt for full license terms, including the additional permissions and the
 * possibility of alternative license terms.
 *******************************************************************************/

package org.faktorips.devtools.core.model.productcmpt;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;

import org.faktorips.abstracttest.AbstractIpsPluginTest;
import org.faktorips.devtools.core.model.ipsproject.IIpsProject;
import org.faktorips.devtools.core.model.pctype.IPolicyCmptType;
import org.faktorips.devtools.core.model.productcmpttype.IProductCmptType;
import org.faktorips.devtools.core.model.productcmpttype.IProductCmptTypeAttribute;
import org.junit.Before;
import org.junit.Test;

/**
 * 
 * @author Jan Ortmann
 */
public class PropertyValueComparatorTest extends AbstractIpsPluginTest {

    private IIpsProject ipsProject;
    private IPolicyCmptType policySupertype;
    private IPolicyCmptType policyType;
    private IProductCmptType productType;
    private IProductCmptType productSupertype;

    private IProductCmptGeneration generation;
    private PropertyValueComparator comparator;

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();
        ipsProject = super.newIpsProject();
        policySupertype = newPolicyAndProductCmptType(ipsProject, "SuperPolicy", "SuperProduct");
        policyType = newPolicyAndProductCmptType(ipsProject, "Policy", "Product");
        productType = policyType.findProductCmptType(ipsProject);
        productSupertype = policySupertype.findProductCmptType(ipsProject);

        policyType.setSupertype(policySupertype.getQualifiedName());
        productType.setSupertype(productSupertype.getQualifiedName());

        IProductCmpt product = newProductCmpt(policyType.findProductCmptType(ipsProject), "Product");
        generation = product.getProductCmptGeneration(0);
    }

    @Test
    public void testConstructor_QName() {
        comparator = new PropertyValueComparator(productType.getQualifiedName(), ipsProject);
        assertEquals(productType, comparator.getProductCmptType());
    }

    @Test
    public void testAttributeValueOrder() {
        IProductCmptTypeAttribute a1 = productType.newProductCmptTypeAttribute("a1");
        IProductCmptTypeAttribute a2 = productType.newProductCmptTypeAttribute("a2");
        IProductCmptTypeAttribute a3 = productSupertype.newProductCmptTypeAttribute("a3");

        IAttributeValue value2 = generation.newAttributeValue(a2, "value2");
        IAttributeValue value3 = generation.newAttributeValue(a3, "value3");
        IAttributeValue value1 = generation.newAttributeValue(a1, "value1");

        comparator = new PropertyValueComparator(productType, ipsProject);
        IPropertyValue[] values = generation.getAttributeValues();
        Arrays.sort(values, comparator);

        assertEquals(value3, values[0]); // values for supertype attributes first
        assertEquals(value1, values[1]);
        assertEquals(value2, values[2]);
    }

}
