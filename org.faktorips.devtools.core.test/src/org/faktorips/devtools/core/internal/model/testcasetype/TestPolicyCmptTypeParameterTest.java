/***************************************************************************************************
 * Copyright (c) 2005,2006 Faktor Zehn GmbH und andere.
 * 
 * Alle Rechte vorbehalten.
 * 
 * Dieses Programm und alle mitgelieferten Sachen (Dokumentationen, Beispiele, Konfigurationen,
 * etc.) dürfen nur unter den Bedingungen der Faktor-Zehn-Community Lizenzvereinbarung – Version 0.1
 * (vor Gründung Community) genutzt werden, die Bestandteil der Auslieferung ist und auch unter
 * http://www.faktorips.org/legal/cl-v01.html eingesehen werden kann.
 * 
 * Mitwirkende: Faktor Zehn GmbH - initial API and implementation
 * 
 **************************************************************************************************/

package org.faktorips.devtools.core.internal.model.testcasetype;

import java.util.Arrays;
import java.util.GregorianCalendar;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.faktorips.devtools.core.AbstractIpsPluginTest;
import org.faktorips.devtools.core.internal.model.pctype.PolicyCmptType;
import org.faktorips.devtools.core.internal.model.productcmpttype.ProductCmptType;
import org.faktorips.devtools.core.model.IIpsProject;
import org.faktorips.devtools.core.model.IIpsSrcFile;
import org.faktorips.devtools.core.model.IpsObjectType;
import org.faktorips.devtools.core.model.pctype.IPolicyCmptType;
import org.faktorips.devtools.core.model.pctype.IPolicyCmptTypeAssociation;
import org.faktorips.devtools.core.model.pctype.AssociationType;
import org.faktorips.devtools.core.model.productcmpt.IProductCmpt;
import org.faktorips.devtools.core.model.productcmpt.IProductCmptGeneration;
import org.faktorips.devtools.core.model.productcmpt.IProductCmptLink;
import org.faktorips.devtools.core.model.productcmpttype.IProductCmptTypeAssociation;
import org.faktorips.devtools.core.model.testcasetype.ITestAttribute;
import org.faktorips.devtools.core.model.testcasetype.ITestCaseType;
import org.faktorips.devtools.core.model.testcasetype.ITestPolicyCmptTypeParameter;
import org.faktorips.devtools.core.model.testcasetype.TestParameterType;
import org.faktorips.devtools.core.util.XmlUtil;
import org.faktorips.util.message.Message;
import org.faktorips.util.message.MessageList;
import org.w3c.dom.Element;

/**
 * 
 * @author Joerg Ortmann
 */
public class TestPolicyCmptTypeParameterTest extends AbstractIpsPluginTest {

    private ITestPolicyCmptTypeParameter policyCmptTypeParameterInput;
    private IIpsProject project;
    private ITestCaseType testCaseType;
    
    protected void setUp() throws Exception {
        super.setUp();
        project = newIpsProject("TestProject");
        PolicyCmptType pct = newPolicyCmptType(project, "TestPolicy");
        testCaseType = (ITestCaseType)newIpsObject(project, IpsObjectType.TEST_CASE_TYPE, "PremiumCalculation");
        policyCmptTypeParameterInput = testCaseType.newInputTestPolicyCmptTypeParameter();
        policyCmptTypeParameterInput.setName("Parameter1");
        policyCmptTypeParameterInput.setPolicyCmptType(pct.getQualifiedName());
    }

    public void testIsRootParameter() {
        assertTrue(policyCmptTypeParameterInput.isRoot());
        ITestPolicyCmptTypeParameter targetChild = policyCmptTypeParameterInput.newTestPolicyCmptTypeParamChild();
        assertFalse(targetChild.isRoot());
    }
    
    public void testInitFromXml() {
        Element docEl = getTestDocument().getDocumentElement();
        Element paramEl = XmlUtil.getElement(docEl, "PolicyCmptTypeParameter", 0);
        policyCmptTypeParameterInput.initFromXml(paramEl);
        assertEquals(2, policyCmptTypeParameterInput.getTestPolicyCmptTypeParamChilds().length);
        assertEquals(3, policyCmptTypeParameterInput.getTestAttributes().length);
        assertTrue(policyCmptTypeParameterInput.isInputParameter());
        assertFalse(policyCmptTypeParameterInput.isExpextedResultParameter());
        assertFalse(policyCmptTypeParameterInput.isCombinedParameter());
        assertTargetTestPolicyCmptTypeParameter(policyCmptTypeParameterInput, "policyCmptType1", "base.Test1",
                "relation1", 2, 3, true, false, false);
        assertTargetTestPolicyCmptTypeParameter(policyCmptTypeParameterInput
                .getTestPolicyCmptTypeParamChild("policyCmptType2"), "policyCmptType2", "base.Test2", "relation2", 4,
                5, false, true, false);
        assertTargetTestPolicyCmptTypeParameter(policyCmptTypeParameterInput
                .getTestPolicyCmptTypeParamChild("policyCmptType3"), "policyCmptType3", "base.Test3", "relation3", 6,
                7, true, true, true);
        assertTrue(policyCmptTypeParameterInput.isRequiresProductCmpt());

        paramEl = XmlUtil.getElement(docEl, "PolicyCmptTypeParameter", 1);
        policyCmptTypeParameterInput.initFromXml(paramEl);
        assertFalse(policyCmptTypeParameterInput.isInputParameter());
        assertTrue(policyCmptTypeParameterInput.isExpextedResultParameter());
        assertFalse(policyCmptTypeParameterInput.isCombinedParameter());
        assertTargetTestPolicyCmptTypeParameter(policyCmptTypeParameterInput
                .getTestPolicyCmptTypeParamChild("policyCmptType4.1"), "policyCmptType4.1", "base.Test4.1",
                "relation4.1", 1, 1, true, false, false);

        paramEl = XmlUtil.getElement(docEl, "PolicyCmptTypeParameter", 2);
        policyCmptTypeParameterInput.initFromXml(paramEl);
        assertTrue(policyCmptTypeParameterInput.isInputParameter());
        assertTrue(policyCmptTypeParameterInput.isExpextedResultParameter());
        assertTrue(policyCmptTypeParameterInput.isCombinedParameter());
        assertTargetTestPolicyCmptTypeParameter(policyCmptTypeParameterInput
                .getTestPolicyCmptTypeParamChild("policyCmptType5.1"), "policyCmptType5.1", "base.Test5.1",
                "relation5.1", 2, 2, true, true, true);

        // wrong type type doesn't result in an exception,
        // the parameter will be parsed and stored as unknown parameter type type
        paramEl = XmlUtil.getElement(docEl, "PolicyCmptTypeParameter", 3);
        policyCmptTypeParameterInput.initFromXml(paramEl);
        assertFalse(policyCmptTypeParameterInput.isInputParameter());
        assertFalse(policyCmptTypeParameterInput.isExpextedResultParameter());
        assertFalse(policyCmptTypeParameterInput.isCombinedParameter());
    }

    public void testToXml() throws Exception {
        policyCmptTypeParameterInput.setName("Name1");
        policyCmptTypeParameterInput.setPolicyCmptType("base.Test2");
        policyCmptTypeParameterInput.setRelation("relation1");
        policyCmptTypeParameterInput.setMinInstances(7);
        policyCmptTypeParameterInput.setMaxInstances(8);
        policyCmptTypeParameterInput.setRequiresProductCmpt(true);
        policyCmptTypeParameterInput.newTestPolicyCmptTypeParamChild();
        policyCmptTypeParameterInput.setTestParameterType(TestParameterType.INPUT);
        ITestPolicyCmptTypeParameter targetChild = policyCmptTypeParameterInput.newTestPolicyCmptTypeParamChild();

        policyCmptTypeParameterInput.newTestPolicyCmptTypeParamChild();
        targetChild.setRelation("relation1");
        targetChild.setPolicyCmptType("base.Test4");
        targetChild.setName("childpolicyCmptType1");
        targetChild.setMinInstances(7);
        targetChild.setMaxInstances(8);
        policyCmptTypeParameterInput.newInputTestAttribute();

        Element el = policyCmptTypeParameterInput.toXml(newDocument());

        // overwrite with wrong data
        policyCmptTypeParameterInput.setPolicyCmptType("base.Test3");
        policyCmptTypeParameterInput.setRelation("relation2");
        policyCmptTypeParameterInput.setMinInstances(9);
        policyCmptTypeParameterInput.setMaxInstances(10);
        policyCmptTypeParameterInput.setRequiresProductCmpt(false);
        policyCmptTypeParameterInput.newInputTestAttribute();
        policyCmptTypeParameterInput.newInputTestAttribute();
        policyCmptTypeParameterInput.setTestParameterType(TestParameterType.EXPECTED_RESULT);
        policyCmptTypeParameterInput.newTestPolicyCmptTypeParamChild();

        // check the value stored before
        policyCmptTypeParameterInput.initFromXml(el);
        assertTargetTestPolicyCmptTypeParameter(policyCmptTypeParameterInput, "Name1", "base.Test2", "relation1", 7, 8,
                true, false, false);
        assertEquals(3, policyCmptTypeParameterInput.getTestPolicyCmptTypeParamChilds().length);
        assertEquals(1, policyCmptTypeParameterInput.getTestAttributes().length);
        assertTargetTestPolicyCmptTypeParameter(policyCmptTypeParameterInput, "Name1", "base.Test2", "relation1", 7, 8,
                true, false, false);
        // child type not specified therfor combinned is the default type
        assertTargetTestPolicyCmptTypeParameter(policyCmptTypeParameterInput
                .getTestPolicyCmptTypeParamChild("childpolicyCmptType1"), "childpolicyCmptType1", "base.Test4",
                "relation1", 7, 8, true, true, true);
        assertTrue(policyCmptTypeParameterInput.isRequiresProductCmpt());
    }
    
    private void assertTargetTestPolicyCmptTypeParameter(ITestPolicyCmptTypeParameter targetChild,
            String name,
            String policyCmptTypeName,
            String relationName,
            int min,
            int max,
            boolean isInput,
            boolean isExpected,
            boolean isCombined) {
        assertNotNull(targetChild);
        assertEquals(name, targetChild.getName());
        assertEquals(policyCmptTypeName, targetChild.getPolicyCmptType());
        assertEquals(relationName, targetChild.getRelation());
        assertEquals(min, targetChild.getMinInstances());
        assertEquals(max, targetChild.getMaxInstances());
        assertEquals(isInput, targetChild.isInputParameter());
        assertEquals(isExpected, targetChild.isExpextedResultParameter());
        assertEquals(isCombined, targetChild.isCombinedParameter());
    }
    
    public void testRemoveTestAttribute () throws CoreException{
        ITestAttribute testAttribute1 = policyCmptTypeParameterInput.newInputTestAttribute();
        ITestAttribute testAttribute2 = policyCmptTypeParameterInput.newInputTestAttribute();
        ITestAttribute testAttribute3 = policyCmptTypeParameterInput.newExpectedResultTestAttribute();
        assertEquals(3, policyCmptTypeParameterInput.getTestAttributes().length);
        testAttribute2.delete();
        assertEquals(2, policyCmptTypeParameterInput.getTestAttributes().length);
        testAttribute1.delete();
        testAttribute3.delete();
        assertEquals(0, policyCmptTypeParameterInput.getTestAttributes().length);
    }
    
    public void testFindRelationTest() throws Exception {
        IPolicyCmptType policyCmptTypeSuper = newPolicyCmptType(project, "policyCmptSuper");
        IPolicyCmptTypeAssociation rel1 = policyCmptTypeSuper.newPolicyCmptTypeAssociation();
        rel1.setTargetRoleSingular("relation1");
        IPolicyCmptTypeAssociation rel2 = policyCmptTypeSuper.newPolicyCmptTypeAssociation();
        rel2.setTargetRoleSingular("relation2");
        IPolicyCmptType policyCmptType = newPolicyCmptType(project, "policyCmpt");
        IPolicyCmptTypeAssociation rel3 = policyCmptType.newPolicyCmptTypeAssociation();
        rel3.setTargetRoleSingular("relation3");
        IPolicyCmptTypeAssociation rel4 = policyCmptType.newPolicyCmptTypeAssociation();
        rel4.setTargetRoleSingular("relation4");
        policyCmptType.setSupertype(policyCmptTypeSuper.getQualifiedName());
        
        ITestPolicyCmptTypeParameter child = policyCmptTypeParameterInput.newTestPolicyCmptTypeParamChild();
        policyCmptTypeParameterInput.setPolicyCmptType("policyCmpt");
        
        child.setRelation("relation1");
        assertEquals(rel1, child.findRelation());
        child.setRelation("relation2");
        assertEquals(rel2, child.findRelation());
        child.setRelation("relation3");
        assertEquals(rel3, child.findRelation());
        child.setRelation("relation4");
        assertEquals(rel4, child.findRelation());
    }
    
    public void testValidationWrongPolicyCmptTypeOfRelation() throws CoreException{
        IPolicyCmptType targetPolicyCmptTypeSuperSuper = newPolicyCmptType(project, "targetPolicyCmptSuperSuper");
        IPolicyCmptType targetPolicyCmptTypeSuper = newPolicyCmptType(project, "targetPolicyCmptSuper");
        IPolicyCmptType targetPolicyCmptType = newPolicyCmptType(project, "targetPolicyCmpt");
        targetPolicyCmptTypeSuper.setSupertype(targetPolicyCmptTypeSuperSuper.getQualifiedName());
        targetPolicyCmptType.setSupertype(targetPolicyCmptTypeSuper.getQualifiedName());
        
        IPolicyCmptType sourcePolicyCmptType = newPolicyCmptType(project, "sourcePolicyCmpt");
        IPolicyCmptTypeAssociation relation = sourcePolicyCmptType.newPolicyCmptTypeAssociation();
        relation.setTargetRoleSingular("relation");
        relation.setTarget(targetPolicyCmptTypeSuperSuper.getQualifiedName());
        
        MessageList ml = policyCmptTypeParameterInput.validate();
        assertNull(ml.getMessageByCode(ITestPolicyCmptTypeParameter.MSGCODE_WRONG_POLICY_CMPT_TYPE_OF_RELATION));

        policyCmptTypeParameterInput.setPolicyCmptType(sourcePolicyCmptType.getQualifiedName());
        ITestPolicyCmptTypeParameter child = policyCmptTypeParameterInput.newTestPolicyCmptTypeParamChild();
        // no target candidate of relation set, therefore msg couldn't be throws
        child.setRelation(relation.getName());
        ml = child.validate();
        assertNull(ml.getMessageByCode(ITestPolicyCmptTypeParameter.MSGCODE_WRONG_POLICY_CMPT_TYPE_OF_RELATION));
        
        child.setPolicyCmptType(targetPolicyCmptTypeSuperSuper.getQualifiedName());
        ml = child.validate();
        assertNull(ml.getMessageByCode(ITestPolicyCmptTypeParameter.MSGCODE_WRONG_POLICY_CMPT_TYPE_OF_RELATION));
        
        child.setPolicyCmptType(targetPolicyCmptTypeSuper.getQualifiedName());
        ml = child.validate();
        assertNull(ml.getMessageByCode(ITestPolicyCmptTypeParameter.MSGCODE_WRONG_POLICY_CMPT_TYPE_OF_RELATION));
        
        child.setPolicyCmptType(targetPolicyCmptType.getQualifiedName());
        ml = child.validate();
        assertNull(ml.getMessageByCode(ITestPolicyCmptTypeParameter.MSGCODE_WRONG_POLICY_CMPT_TYPE_OF_RELATION));
        
        
        relation.setTarget(targetPolicyCmptTypeSuper.getQualifiedName());
        
        child.setPolicyCmptType(targetPolicyCmptTypeSuperSuper.getQualifiedName());
        ml = child.validate();
        // wrong target of relation set 
        //  relation specifies super but as possible target policy cmpt type super super is set
        assertNotNull(ml.getMessageByCode(ITestPolicyCmptTypeParameter.MSGCODE_WRONG_POLICY_CMPT_TYPE_OF_RELATION));
        
        child.setPolicyCmptType(targetPolicyCmptType.getQualifiedName());
        ml = child.validate();
        assertNull(ml.getMessageByCode(ITestPolicyCmptTypeParameter.MSGCODE_WRONG_POLICY_CMPT_TYPE_OF_RELATION));
    }

    public void testValidatePolicyCmptTypeNotExists() throws Exception{
        IPolicyCmptType policyCmptType = newPolicyCmptType(project, "policyCmptSuper");
        policyCmptTypeParameterInput.setPolicyCmptType(policyCmptType.getQualifiedName());
        MessageList ml = policyCmptTypeParameterInput.validate();
        assertNull(ml.getMessageByCode(ITestPolicyCmptTypeParameter.MSGCODE_POLICY_CMPT_TYPE_NOT_EXISTS));

        policyCmptTypeParameterInput.setPolicyCmptType("x");
        ml = policyCmptTypeParameterInput.validate();
        assertNotNull(ml.getMessageByCode(ITestPolicyCmptTypeParameter.MSGCODE_POLICY_CMPT_TYPE_NOT_EXISTS));
    }
    
    public void testValidateWrongCountOfInstances() throws Exception{
        policyCmptTypeParameterInput.setMinInstances(0);
        policyCmptTypeParameterInput.setMaxInstances(1);
        MessageList ml = policyCmptTypeParameterInput.validate();
        assertNull(ml.getMessageByCode(ITestPolicyCmptTypeParameter.MSGCODE_MIN_INSTANCES_IS_GREATER_THAN_MAX));
        assertNull(ml.getMessageByCode(ITestPolicyCmptTypeParameter.MSGCODE_MAX_INSTANCES_IS_LESS_THAN_MIN));
        
        policyCmptTypeParameterInput.setMinInstances(2);
        policyCmptTypeParameterInput.setMaxInstances(1);
        ml = policyCmptTypeParameterInput.validate();
        assertNotNull(ml.getMessageByCode(ITestPolicyCmptTypeParameter.MSGCODE_MIN_INSTANCES_IS_GREATER_THAN_MAX));
        assertNotNull(ml.getMessageByCode(ITestPolicyCmptTypeParameter.MSGCODE_MAX_INSTANCES_IS_LESS_THAN_MIN));
    }

    public void testValidateTypeDoesNotMatchParentType() throws Exception{
        policyCmptTypeParameterInput.setTestParameterType(TestParameterType.INPUT);
        ITestPolicyCmptTypeParameter paramChild = policyCmptTypeParameterInput.newTestPolicyCmptTypeParamChild();
        paramChild.setTestParameterType(TestParameterType.INPUT);
        MessageList ml = policyCmptTypeParameterInput.validate();
        assertNull(ml.getMessageByCode(ITestPolicyCmptTypeParameter.MSGCODE_TYPE_DOES_NOT_MATCH_PARENT_TYPE));

        paramChild.setTestParameterType(TestParameterType.EXPECTED_RESULT);
        ml = policyCmptTypeParameterInput.validate();
        assertNotNull(ml.getMessageByCode(ITestPolicyCmptTypeParameter.MSGCODE_TYPE_DOES_NOT_MATCH_PARENT_TYPE));
        
        policyCmptTypeParameterInput.setTestParameterType(TestParameterType.EXPECTED_RESULT);
        ml = policyCmptTypeParameterInput.validate();
        assertNull(ml.getMessageByCode(ITestPolicyCmptTypeParameter.MSGCODE_TYPE_DOES_NOT_MATCH_PARENT_TYPE));

        paramChild.setTestParameterType(TestParameterType.INPUT);
        ml = policyCmptTypeParameterInput.validate();
        assertNotNull(ml.getMessageByCode(ITestPolicyCmptTypeParameter.MSGCODE_TYPE_DOES_NOT_MATCH_PARENT_TYPE));
        
        policyCmptTypeParameterInput.setTestParameterType(TestParameterType.COMBINED);
        paramChild.setTestParameterType(TestParameterType.EXPECTED_RESULT);
        ml = policyCmptTypeParameterInput.validate();
        assertNull(ml.getMessageByCode(ITestPolicyCmptTypeParameter.MSGCODE_TYPE_DOES_NOT_MATCH_PARENT_TYPE));

        paramChild.setTestParameterType(TestParameterType.INPUT);
        ml = policyCmptTypeParameterInput.validate();
        assertNull(ml.getMessageByCode(ITestPolicyCmptTypeParameter.MSGCODE_TYPE_DOES_NOT_MATCH_PARENT_TYPE));
    }
    
    public void testValidateRelationNotExists() throws Exception {
        IPolicyCmptType policyCmptType = newPolicyCmptType(project, "policyCmpt");
        IPolicyCmptTypeAssociation rel1 = policyCmptType.newPolicyCmptTypeAssociation();
        rel1.setTargetRoleSingular("relation1");
        
        policyCmptTypeParameterInput.setPolicyCmptType(policyCmptType.getQualifiedName());
        ITestPolicyCmptTypeParameter paramChild = policyCmptTypeParameterInput.newTestPolicyCmptTypeParamChild();
        paramChild.setRelation(rel1.getName());
        
        MessageList ml = policyCmptTypeParameterInput.validate();
        assertNull(ml.getMessageByCode(ITestPolicyCmptTypeParameter.MSGCODE_RELATION_NOT_EXISTS));
        
        paramChild.setRelation("x");
        ml = policyCmptTypeParameterInput.validate();
        assertNotNull(ml.getMessageByCode(ITestPolicyCmptTypeParameter.MSGCODE_RELATION_NOT_EXISTS));
    }

    public void testValidateTargetOfRelationNotExists() throws Exception {
        IPolicyCmptType policyCmptType = newPolicyCmptType(project, "policyCmpt");
        IPolicyCmptType policyCmptTypeTarget = newPolicyCmptType(project, "policyCmptTarget");
        IPolicyCmptTypeAssociation rel1 = policyCmptType.newPolicyCmptTypeAssociation();
        rel1.setAssociationType(AssociationType.COMPOSITION_MASTER_TO_DETAIL);        
        rel1.setTargetRoleSingular("relation1");
        rel1.setTarget(policyCmptTypeTarget.getQualifiedName());
        
        policyCmptTypeParameterInput.setPolicyCmptType(policyCmptType.getQualifiedName());
        ITestPolicyCmptTypeParameter paramChild = policyCmptTypeParameterInput.newTestPolicyCmptTypeParamChild();
        paramChild.setRelation(rel1.getName());
        paramChild.setPolicyCmptType(policyCmptTypeTarget.getQualifiedName());
        
        MessageList ml = policyCmptTypeParameterInput.validate();
        assertNull(ml.getMessageByCode(ITestPolicyCmptTypeParameter.MSGCODE_TARGET_OF_RELATION_NOT_EXISTS));
        
        rel1.setTarget("x");
        ml = policyCmptTypeParameterInput.validate();
        assertNotNull(ml.getMessageByCode(ITestPolicyCmptTypeParameter.MSGCODE_TARGET_OF_RELATION_NOT_EXISTS));
    }
    
    public void testValidateMustRequireProdIfRootAndAbstract() throws Exception {
        MessageList ml = policyCmptTypeParameterInput.validate();
        assertNull(ml.getMessageByCode(ITestPolicyCmptTypeParameter.MSGCODE_MUST_REQUIRE_PROD_IF_ROOT_AND_ABSTRACT));

        IPolicyCmptType policyCmptType = newPolicyCmptType(project, "policyCmpt");
        policyCmptTypeParameterInput.setPolicyCmptType(policyCmptType.getQualifiedName());
        
        policyCmptType.setAbstract(false);
        policyCmptTypeParameterInput.setRequiresProductCmpt(true);
        ml = policyCmptTypeParameterInput.validate();
        assertNull(ml.getMessageByCode(ITestPolicyCmptTypeParameter.MSGCODE_MUST_REQUIRE_PROD_IF_ROOT_AND_ABSTRACT));  
        
        policyCmptType.setAbstract(true);
        policyCmptTypeParameterInput.setRequiresProductCmpt(false);
        ml = policyCmptTypeParameterInput.validate();
        assertNotNull(ml.getMessageByCode(ITestPolicyCmptTypeParameter.MSGCODE_MUST_REQUIRE_PROD_IF_ROOT_AND_ABSTRACT));
        
        policyCmptTypeParameterInput.setRequiresProductCmpt(true);
        ml = policyCmptTypeParameterInput.validate();
        assertNull(ml.getMessageByCode(ITestPolicyCmptTypeParameter.MSGCODE_MUST_REQUIRE_PROD_IF_ROOT_AND_ABSTRACT));
    } 
    
    public void testValidateRequiresProdButPolicyCmptTypeIsNotConfByProd() throws Exception {
        MessageList ml = policyCmptTypeParameterInput.validate();
        assertNull(ml.getMessageByCode(ITestPolicyCmptTypeParameter.MSGCODE_REQUIRES_PROD_BUT_POLICY_CMPT_TYPE_IS_NOT_CONF_BY_PROD));

        IPolicyCmptType policyCmptType = newPolicyCmptType(project, "policyCmpt");
        policyCmptTypeParameterInput.setPolicyCmptType(policyCmptType.getQualifiedName());
        
        policyCmptType.setConfigurableByProductCmptType(false);
        policyCmptTypeParameterInput.setRequiresProductCmpt(true);
        ml = policyCmptTypeParameterInput.validate();
        assertNotNull(ml.getMessageByCode(ITestPolicyCmptTypeParameter.MSGCODE_REQUIRES_PROD_BUT_POLICY_CMPT_TYPE_IS_NOT_CONF_BY_PROD));  
        
        policyCmptType.setConfigurableByProductCmptType(true);
        policyCmptTypeParameterInput.setRequiresProductCmpt(false);
        ml = policyCmptTypeParameterInput.validate();
        assertNull(ml.getMessageByCode(ITestPolicyCmptTypeParameter.MSGCODE_REQUIRES_PROD_BUT_POLICY_CMPT_TYPE_IS_NOT_CONF_BY_PROD));  
        
        policyCmptType.setConfigurableByProductCmptType(false);
        policyCmptTypeParameterInput.setRequiresProductCmpt(false);
        ml = policyCmptTypeParameterInput.validate();
        assertNull(ml.getMessageByCode(ITestPolicyCmptTypeParameter.MSGCODE_REQUIRES_PROD_BUT_POLICY_CMPT_TYPE_IS_NOT_CONF_BY_PROD));  
    }     
    
    public void testValidateTargetOfAssociationNotExistsInTestCaseType() throws Exception{
        IPolicyCmptType policyCmptType = newPolicyCmptType(project, "policyCmpt");
        IPolicyCmptType policyCmptTypeTarget = newPolicyCmptType(project, "policyCmptTarget");
        IPolicyCmptTypeAssociation rel1 = policyCmptType.newPolicyCmptTypeAssociation();
        rel1.setAssociationType(AssociationType.ASSOCIATION);
        rel1.setTargetRoleSingular("relation1");
        rel1.setTarget(policyCmptTypeTarget.getQualifiedName());
        
        policyCmptTypeParameterInput.setPolicyCmptType(policyCmptType.getQualifiedName());
        ITestPolicyCmptTypeParameter paramChild = policyCmptTypeParameterInput.newTestPolicyCmptTypeParamChild();
        paramChild.setRelation(rel1.getName());
        paramChild.setPolicyCmptType(policyCmptTypeTarget.getQualifiedName());
        paramChild.setTestParameterType(TestParameterType.INPUT);
        
        MessageList ml = policyCmptTypeParameterInput.validate();
        assertNotNull(ml.getMessageByCode(ITestPolicyCmptTypeParameter.MSGCODE_TARGET_OF_ASSOCIATION_NOT_EXISTS_IN_TESTCASETYPE));
        assertEquals(ml.getFirstMessage(Message.WARNING).getCode(), ITestPolicyCmptTypeParameter.MSGCODE_TARGET_OF_ASSOCIATION_NOT_EXISTS_IN_TESTCASETYPE);
        
        ITestPolicyCmptTypeParameter targetOfAss = testCaseType.newInputTestPolicyCmptTypeParameter();
        paramChild.setTestParameterType(TestParameterType.INPUT);
        targetOfAss.setTestParameterType(TestParameterType.COMBINED);
        targetOfAss.setName("xyz");
        targetOfAss.setPolicyCmptType(policyCmptTypeTarget.getQualifiedName());
        ml = policyCmptTypeParameterInput.validate();
        assertNull(ml.getMessageByCode(ITestPolicyCmptTypeParameter.MSGCODE_TARGET_OF_ASSOCIATION_NOT_EXISTS_IN_TESTCASETYPE));
        
        paramChild.setTestParameterType(TestParameterType.EXPECTED_RESULT);
        targetOfAss.setTestParameterType(TestParameterType.COMBINED);
        ml = policyCmptTypeParameterInput.validate();
        assertNull(ml.getMessageByCode(ITestPolicyCmptTypeParameter.MSGCODE_TARGET_OF_ASSOCIATION_NOT_EXISTS_IN_TESTCASETYPE));
       
        paramChild.setTestParameterType(TestParameterType.EXPECTED_RESULT);
        targetOfAss.setTestParameterType(TestParameterType.INPUT);
        ml = policyCmptTypeParameterInput.validate();
        assertNotNull(ml.getMessageByCode(ITestPolicyCmptTypeParameter.MSGCODE_TARGET_OF_ASSOCIATION_NOT_EXISTS_IN_TESTCASETYPE));
        
        paramChild.setTestParameterType(TestParameterType.INPUT);
        targetOfAss.setTestParameterType(TestParameterType.EXPECTED_RESULT);
        ml = policyCmptTypeParameterInput.validate();
        assertNotNull(ml.getMessageByCode(ITestPolicyCmptTypeParameter.MSGCODE_TARGET_OF_ASSOCIATION_NOT_EXISTS_IN_TESTCASETYPE));
        
        paramChild.setTestParameterType(TestParameterType.COMBINED);
        targetOfAss.setTestParameterType(TestParameterType.EXPECTED_RESULT);
        ml = policyCmptTypeParameterInput.validate();
        assertNotNull(ml.getMessageByCode(ITestPolicyCmptTypeParameter.MSGCODE_TARGET_OF_ASSOCIATION_NOT_EXISTS_IN_TESTCASETYPE));
        
        paramChild.setTestParameterType(TestParameterType.COMBINED);
        targetOfAss.setTestParameterType(TestParameterType.INPUT);
        ml = policyCmptTypeParameterInput.validate();
        assertNotNull(ml.getMessageByCode(ITestPolicyCmptTypeParameter.MSGCODE_TARGET_OF_ASSOCIATION_NOT_EXISTS_IN_TESTCASETYPE));
    }
    
    private class TestContent{
        private IPolicyCmptType policy;
        private IPolicyCmptTypeAssociation coverages;
        private IPolicyCmptType coverage;
        private IProductCmpt policyProduct;
        private IProductCmpt coverageProductA;
        private IProductCmpt coverageProductB;

        private ITestPolicyCmptTypeParameter parameter;
        private ITestPolicyCmptTypeParameter childParameter;
        
        public void init(IIpsProject project) throws CoreException {
            policy = newPolicyCmptType(project, "Policy");
            policy.setConfigurableByProductCmptType(true);
            ProductCmptType productCmptTypePolicy = newProductCmptType(project, "PolicyType");
            policy.setProductCmptType(productCmptTypePolicy.getQualifiedName());
            productCmptTypePolicy.setPolicyCmptType(policy.getQualifiedName());
            
            coverage = newPolicyCmptType(project, "Coverage");
            coverage.setConfigurableByProductCmptType(true);
            ProductCmptType productCmptTypeCoverage = newProductCmptType(project, "CoverageType");
            coverage.setProductCmptType(productCmptTypeCoverage.getQualifiedName());
            productCmptTypeCoverage.setPolicyCmptType(coverage.getQualifiedName());

            coverages = policy.newPolicyCmptTypeAssociation();
            coverages.setTarget(coverage.getQualifiedName());
            coverages.setProductRelevant(true);
            coverages.setTargetRoleSingular("Coverage");
            coverages.setTargetRolePlural("Coverages");
            coverages.setTargetRoleSingularProductSide("Coverage");
            coverages.setTargetRolePluralProductSide("Coverages");
            coverages.setAssociationType(AssociationType.COMPOSITION_MASTER_TO_DETAIL);
            coverages.setProductRelevant(true);

            IProductCmptTypeAssociation association = productCmptTypePolicy.newProductCmptTypeAssociation();
            association.setTarget(productCmptTypeCoverage.getQualifiedName());
            association.setTargetRoleSingular("Coverage");
            association.setTargetRolePlural("Coverages");
            
            policyProduct = newProductCmpt(project, "PolicyA 2007-09");
            policyProduct.setProductCmptType(productCmptTypePolicy.getQualifiedName());
            coverageProductA = newProductCmpt(project, "CoverageA 2007-09");
            coverageProductA.setProductCmptType(productCmptTypeCoverage.getQualifiedName());
            coverageProductA.newGeneration(new GregorianCalendar());
            coverageProductB = newProductCmpt(project, "CoverageB 2007-09");
            coverageProductB.setProductCmptType(productCmptTypeCoverage.getQualifiedName());
            coverageProductB.newGeneration(new GregorianCalendar());
            
            parameter = testCaseType.newCombinedPolicyCmptTypeParameter();
            parameter.setPolicyCmptType(policy.getQualifiedName());
            parameter.setName("PolicyParam");
            childParameter = parameter.newTestPolicyCmptTypeParamChild();
            childParameter.setPolicyCmptType(coverage.getQualifiedName());
            childParameter.setRelation(coverages.getName());
            childParameter.setName("CoverageParam");            

        }
    }
    
    public void testGetAllowedProductCmpt() throws CoreException{
        TestContent testContent = new TestContent();
        testContent.init(project);
        
        // no relation defined
        // => root 1, child no result
        IIpsSrcFile[] allowedProductCmpt = testContent.parameter.getAllowedProductCmpt(project, testContent.policyProduct);
        assertEquals(1, allowedProductCmpt.length);
        assertEquals(testContent.policyProduct.getIpsSrcFile(), allowedProductCmpt[0]);
        allowedProductCmpt = testContent.childParameter.getAllowedProductCmpt(project, testContent.policyProduct);
        assertEquals(0, allowedProductCmpt.length);
        
        // one relation defined, but without target
        // => child no result
        IProductCmptGeneration generation = (IProductCmptGeneration)testContent.policyProduct.newGeneration(new GregorianCalendar());
        IProductCmptLink productCmptRelation = generation.newLink(testContent.coverages.getTargetRoleSingularProductSide());
        allowedProductCmpt = testContent.childParameter.getAllowedProductCmpt(project, testContent.policyProduct);
        assertEquals(0, allowedProductCmpt.length);
        
        // one relation with target
        productCmptRelation.setTarget(testContent.coverageProductA.getQualifiedName());
        allowedProductCmpt = testContent.childParameter.getAllowedProductCmpt(project, testContent.policyProduct);
        
        
        assertEquals(1, allowedProductCmpt.length);
        assertEquals(testContent.coverageProductA.getIpsSrcFile(), allowedProductCmpt[0]);
        
        // relation exists twice
        // find product cmpt only once
        generation = (IProductCmptGeneration)testContent.policyProduct.newGeneration(new GregorianCalendar());
        productCmptRelation = generation.newLink(testContent.coverages.getTargetRoleSingularProductSide());
        productCmptRelation.setTarget(testContent.coverageProductA.getQualifiedName());
        allowedProductCmpt = testContent.childParameter.getAllowedProductCmpt(project, testContent.policyProduct);
        assertEquals(1, allowedProductCmpt.length);
        assertEquals(testContent.coverageProductA.getIpsSrcFile(), allowedProductCmpt[0]);
        
        // if no parent product cmpt is given, return all product cmpt which matches the
        // relation of the parameter
        allowedProductCmpt = testContent.childParameter.getAllowedProductCmpt(project, null);
        assertEquals(2, allowedProductCmpt.length);
        asserContains(allowedProductCmpt, testContent.coverageProductA);
        asserContains(allowedProductCmpt, testContent.coverageProductB);

        // test with two generations
        //   coverageProductA specified in generation 1
        //   coverageProductB specified in generation 2
        generation = (IProductCmptGeneration)testContent.policyProduct.newGeneration(new GregorianCalendar());
        productCmptRelation = generation.newLink(testContent.coverages.getTargetRoleSingularProductSide());
        productCmptRelation.setTarget(testContent.coverageProductB.getQualifiedName());
        allowedProductCmpt = testContent.childParameter.getAllowedProductCmpt(project, testContent.policyProduct);
        assertEquals(2, allowedProductCmpt.length);
        asserContains(allowedProductCmpt, testContent.coverageProductA);
        asserContains(allowedProductCmpt, testContent.coverageProductB);
    }

    private void asserContains(IIpsSrcFile[] allowedProductCmpt, IProductCmpt productCmpt) {
        List list = Arrays.asList(allowedProductCmpt);
        assertTrue(list.contains(productCmpt.getIpsSrcFile()));
    }
}