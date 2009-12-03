/*******************************************************************************
 * Copyright (c) 2005-2009 Faktor Zehn AG und andere.
 * 
 * Alle Rechte vorbehalten.
 * 
 * Dieses Programm und alle mitgelieferten Sachen (Dokumentationen, Beispiele, Konfigurationen,
 * etc.) duerfen nur unter den Bedingungen der Faktor-Zehn-Community Lizenzvereinbarung - Version
 * 0.1 (vor Gruendung Community) genutzt werden, die Bestandteil der Auslieferung ist und auch unter
 * http://www.faktorzehn.org/f10-org:lizenzen:community eingesehen werden kann.
 * 
 * Mitwirkende: Faktor Zehn AG - initial API and implementation - http://www.faktorzehn.de
 *******************************************************************************/

package org.faktorips.devtools.core.ui.workbenchadapters;

import org.faktorips.devtools.core.internal.model.IpsModel;
import org.faktorips.devtools.core.internal.model.bf.BFElement;
import org.faktorips.devtools.core.internal.model.bf.BusinessFunction;
import org.faktorips.devtools.core.internal.model.bf.ControlFlow;
import org.faktorips.devtools.core.internal.model.businessfct.BusinessFunctionImpl;
import org.faktorips.devtools.core.internal.model.enums.EnumAttribute;
import org.faktorips.devtools.core.internal.model.enums.EnumAttributeReference;
import org.faktorips.devtools.core.internal.model.enums.EnumAttributeValue;
import org.faktorips.devtools.core.internal.model.enums.EnumContent;
import org.faktorips.devtools.core.internal.model.enums.EnumType;
import org.faktorips.devtools.core.internal.model.enums.EnumValue;
import org.faktorips.devtools.core.internal.model.ipsobject.AbstractIpsSrcFile;
import org.faktorips.devtools.core.internal.model.ipsobject.IpsObject;
import org.faktorips.devtools.core.internal.model.ipsobject.IpsObjectGeneration;
import org.faktorips.devtools.core.internal.model.ipsproject.AbstractIpsPackageFragment;
import org.faktorips.devtools.core.internal.model.ipsproject.ArchiveIpsPackageFragmentRoot;
import org.faktorips.devtools.core.internal.model.ipsproject.IpsPackageFragmentRoot;
import org.faktorips.devtools.core.internal.model.ipsproject.IpsProject;
import org.faktorips.devtools.core.internal.model.pctype.PolicyCmptType;
import org.faktorips.devtools.core.internal.model.pctype.PolicyCmptTypeAssociation;
import org.faktorips.devtools.core.internal.model.pctype.PolicyCmptTypeAttribute;
import org.faktorips.devtools.core.internal.model.pctype.ValidationRule;
import org.faktorips.devtools.core.internal.model.productcmpt.AttributeValue;
import org.faktorips.devtools.core.internal.model.productcmpt.ConfigElement;
import org.faktorips.devtools.core.internal.model.productcmpt.Formula;
import org.faktorips.devtools.core.internal.model.productcmpt.FormulaTestCase;
import org.faktorips.devtools.core.internal.model.productcmpt.FormulaTestInputValue;
import org.faktorips.devtools.core.internal.model.productcmpt.ProductCmpt;
import org.faktorips.devtools.core.internal.model.productcmpt.ProductCmptLink;
import org.faktorips.devtools.core.internal.model.productcmpt.TableContentUsage;
import org.faktorips.devtools.core.internal.model.productcmpttype.ProductCmptType;
import org.faktorips.devtools.core.internal.model.productcmpttype.ProductCmptTypeAssociation;
import org.faktorips.devtools.core.internal.model.productcmpttype.ProductCmptTypeAttribute;
import org.faktorips.devtools.core.internal.model.productcmpttype.TableStructureUsage;
import org.faktorips.devtools.core.internal.model.tablecontents.Row;
import org.faktorips.devtools.core.internal.model.tablecontents.TableContents;
import org.faktorips.devtools.core.internal.model.tablestructure.Column;
import org.faktorips.devtools.core.internal.model.tablestructure.ColumnRange;
import org.faktorips.devtools.core.internal.model.tablestructure.Key;
import org.faktorips.devtools.core.internal.model.tablestructure.TableAccessFunction;
import org.faktorips.devtools.core.internal.model.tablestructure.TableStructure;
import org.faktorips.devtools.core.internal.model.testcase.TestAttributeValue;
import org.faktorips.devtools.core.internal.model.testcase.TestCase;
import org.faktorips.devtools.core.internal.model.testcase.TestObject;
import org.faktorips.devtools.core.internal.model.testcase.TestPolicyCmpt;
import org.faktorips.devtools.core.internal.model.testcase.TestPolicyCmptLink;
import org.faktorips.devtools.core.internal.model.testcase.TestRule;
import org.faktorips.devtools.core.internal.model.testcase.TestValue;
import org.faktorips.devtools.core.internal.model.testcasetype.TestAttribute;
import org.faktorips.devtools.core.internal.model.testcasetype.TestCaseType;
import org.faktorips.devtools.core.internal.model.testcasetype.TestParameter;
import org.faktorips.devtools.core.internal.model.testcasetype.TestPolicyCmptTypeParameter;
import org.faktorips.devtools.core.internal.model.testcasetype.TestRuleParameter;
import org.faktorips.devtools.core.internal.model.type.Method;
import org.faktorips.devtools.core.internal.model.type.Parameter;
import org.faktorips.devtools.core.internal.model.valueset.ValueSet;

public class DefaultIpsWorkbenchAdapterFactory extends AbstractIpsWorkbenchAdapterFactory {

    private DefaultWorkbenchAdapter defaultWorkbenchAdapter;

    public DefaultIpsWorkbenchAdapterFactory() {
        super();
        defaultWorkbenchAdapter = new DefaultWorkbenchAdapter();
        init();
    }

    private void init() {
        initContainers();
        initSrcFiles();
        initIpsObjects();
        initIpsObjectParts();
    }

    private void initContainers() {
        register(IpsModel.class, defaultWorkbenchAdapter);
        register(IpsProject.class, defaultWorkbenchAdapter);
        register(IpsPackageFragmentRoot.class, defaultWorkbenchAdapter);
        register(ArchiveIpsPackageFragmentRoot.class, defaultWorkbenchAdapter);
        register(AbstractIpsPackageFragment.class, defaultWorkbenchAdapter);
    }

    private void initSrcFiles() {
        register(AbstractIpsSrcFile.class, new IpsSrcFileWorkbenchAdapter());
    }

    private void initIpsObjects() {
        register(IpsObject.class, defaultWorkbenchAdapter);
        register(BusinessFunction.class, defaultWorkbenchAdapter);
        register(BusinessFunctionImpl.class, defaultWorkbenchAdapter);
        register(EnumContent.class, defaultWorkbenchAdapter);
        register(EnumType.class, defaultWorkbenchAdapter);
        register(PolicyCmptType.class, defaultWorkbenchAdapter);
        register(ProductCmptType.class, defaultWorkbenchAdapter);
        register(ProductCmpt.class, new ProductCmptWorkbenchAdapter());
        register(TableStructure.class, defaultWorkbenchAdapter);
        register(TableContents.class, defaultWorkbenchAdapter);
        register(TestCaseType.class, defaultWorkbenchAdapter);
        register(TestCase.class, defaultWorkbenchAdapter);
    }

    private void initIpsObjectParts() {
        // Type
        register(Method.class, defaultWorkbenchAdapter);
        register(Parameter.class, defaultWorkbenchAdapter);

        // BF
        register(BFElement.class, defaultWorkbenchAdapter);
        register(ControlFlow.class, defaultWorkbenchAdapter);

        // Enums
        register(EnumAttribute.class, defaultWorkbenchAdapter);
        register(EnumAttributeReference.class, defaultWorkbenchAdapter);
        register(EnumAttributeValue.class, defaultWorkbenchAdapter);
        register(EnumValue.class, defaultWorkbenchAdapter);

        // generation
        register(IpsObjectGeneration.class, defaultWorkbenchAdapter);

        // PolicyCmptType
        register(PolicyCmptTypeAssociation.class, defaultWorkbenchAdapter);
        register(PolicyCmptTypeAttribute.class, defaultWorkbenchAdapter);
        register(ValidationRule.class, defaultWorkbenchAdapter);

        // ProductCmptType
        register(ProductCmptTypeAssociation.class, defaultWorkbenchAdapter);
        register(ProductCmptTypeAttribute.class, defaultWorkbenchAdapter);
        register(TableStructureUsage.class, defaultWorkbenchAdapter);

        // ProductCmpt
        register(AttributeValue.class, defaultWorkbenchAdapter);
        register(ConfigElement.class, defaultWorkbenchAdapter);
        register(Formula.class, defaultWorkbenchAdapter);
        register(FormulaTestCase.class, defaultWorkbenchAdapter);
        register(FormulaTestInputValue.class, defaultWorkbenchAdapter);
        register(ProductCmptLink.class, defaultWorkbenchAdapter);
        register(TableContentUsage.class, defaultWorkbenchAdapter);

        // TableStructure
        register(Column.class, defaultWorkbenchAdapter);
        register(ColumnRange.class, defaultWorkbenchAdapter);
        register(Key.class, defaultWorkbenchAdapter);
        register(TableAccessFunction.class, defaultWorkbenchAdapter);

        // TableContents
        register(Row.class, defaultWorkbenchAdapter);

        // TestCaseType
        register(TestAttribute.class, defaultWorkbenchAdapter);
        register(TestParameter.class, defaultWorkbenchAdapter);
        register(TestPolicyCmptTypeParameter.class, defaultWorkbenchAdapter);
        register(TestRuleParameter.class, defaultWorkbenchAdapter);

        // TestCase
        register(TestAttributeValue.class, defaultWorkbenchAdapter);
        register(TestObject.class, defaultWorkbenchAdapter);
        register(TestPolicyCmpt.class, defaultWorkbenchAdapter);
        register(TestPolicyCmptLink.class, defaultWorkbenchAdapter);
        register(TestRule.class, defaultWorkbenchAdapter);
        register(TestValue.class, defaultWorkbenchAdapter);

        // ValueSet
        register(ValueSet.class, defaultWorkbenchAdapter);
    }

}
