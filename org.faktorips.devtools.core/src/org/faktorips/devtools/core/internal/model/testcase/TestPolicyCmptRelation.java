/*******************************************************************************
 * Copyright (c) 2005,2006 Faktor Zehn GmbH und andere.
 *
 * Alle Rechte vorbehalten.
 *
 * Dieses Programm und alle mitgelieferten Sachen (Dokumentationen, Beispiele,
 * Konfigurationen, etc.) dürfen nur unter den Bedingungen der 
 * Faktor-Zehn-Community Lizenzvereinbarung – Version 0.1 (vor Gründung Community) 
 * genutzt werden, die Bestandteil der Auslieferung ist und auch unter
 *   http://www.faktorips.org/legal/cl-v01.html
 * eingesehen werden kann.
 *
 * Mitwirkende:
 *   Faktor Zehn GmbH - initial API and implementation 
 *
 *******************************************************************************/

package org.faktorips.devtools.core.internal.model.testcase;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.graphics.Image;
import org.faktorips.devtools.core.IpsPlugin;
import org.faktorips.devtools.core.internal.model.IpsObjectPart;
import org.faktorips.devtools.core.model.IIpsElement;
import org.faktorips.devtools.core.model.IIpsObject;
import org.faktorips.devtools.core.model.IIpsObjectPart;
import org.faktorips.devtools.core.model.pctype.IRelation;
import org.faktorips.devtools.core.model.testcase.ITestCase;
import org.faktorips.devtools.core.model.testcase.ITestPolicyCmpt;
import org.faktorips.devtools.core.model.testcase.ITestPolicyCmptRelation;
import org.faktorips.devtools.core.model.testcasetype.ITestPolicyCmptTypeParameter;
import org.faktorips.util.message.Message;
import org.faktorips.util.message.MessageList;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Test policy component relation. Defines a relation for a policy component
 * class within a test case defination.
 * 
 * @author Joerg Ortmann
 */
public class TestPolicyCmptRelation extends IpsObjectPart implements
		ITestPolicyCmptRelation {

	/** Tags */
	static final String TAG_NAME = "Relation"; //$NON-NLS-1$

	private String testPolicyCmptTypeParameter = ""; //$NON-NLS-1$

	private String target = ""; //$NON-NLS-1$

	private ITestPolicyCmpt targetChild;

	private boolean deleted = false;

	public TestPolicyCmptRelation(IIpsObject parent, int id) {
		super(parent, id);
	}

	public TestPolicyCmptRelation(IIpsObjectPart parent, int id) {
		super(parent, id);
	}

	/**
	 * {@inheritDoc}
	 */
	public String getTestPolicyCmptTypeParameter() {
		return testPolicyCmptTypeParameter;
	}

	/**
	 * {@inheritDoc}
	 */
	public void setTestPolicyCmptTypeParameter(String newPolicyCmptType) {
		String oldPolicyCmptType = this.testPolicyCmptTypeParameter;
		this.testPolicyCmptTypeParameter = newPolicyCmptType;
		valueChanged(oldPolicyCmptType, newPolicyCmptType);
	}

	/**
	 * {@inheritDoc}
	 */
	public ITestPolicyCmptTypeParameter findTestPolicyCmptTypeParameter() throws CoreException {
        if (StringUtils.isEmpty(testPolicyCmptTypeParameter)) {
            return null;
		}
		return getTestCase().findTestPolicyCmptTypeParameter(this);
	}

	/**
	 * {@inheritDoc}
	 */
	public String getTarget() {
		return target;
	}

	/**
	 * {@inheritDoc}
	 */
	public void setTarget(String target) {
		this.target = target;
	}

	/**
	 * {@inheritDoc}
	 */
	public ITestPolicyCmpt findTarget() throws CoreException {
		if (targetChild != null) {
			return targetChild;
		}
		if (StringUtils.isEmpty(target)) {
			return null;
		}
		
		// the target is an association, search for the target in the test case
		return getTestCase().findTestPolicyCmpt(target);
	}

	/**
	 * {@inheritDoc}
	 */
	protected Element createElement(Document doc) {
		return doc.createElement(TAG_NAME);
	}

	/**
	 * {@inheritDoc}
	 */
	protected void initPropertiesFromXml(Element element, Integer id) {
		super.initPropertiesFromXml(element, id);
		testPolicyCmptTypeParameter = element.getAttribute(PROPERTY_POLICYCMPTTYPE);
		target = element.getAttribute(PROPERTY_TARGET);
	}

	/**
	 * {@inheritDoc}
	 */
	protected void propertiesToXml(Element element) {
		super.propertiesToXml(element);
		element.setAttribute(PROPERTY_POLICYCMPTTYPE, testPolicyCmptTypeParameter);
		element.setAttribute(PROPERTY_TARGET, target);
	}

	/**
	 * {@inheritDoc}
	 */
	public void delete() {
		((TestPolicyCmpt) getParent()).removeTestPcTypeRelation(this);
		deleted = true;
        objectHasChanged();
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean isDeleted() {
		return deleted;
	}

	/**
	 * {@inheritDoc}
	 */
	public Image getImage() {
        if (isAccoziation()){
            // return the linked product cmpt image if the target relates a product cmpt,
            // or return the linked policy cmpt if target not found or no product cmpt is related
            try {
                ITestPolicyCmpt cmpt = findTarget();
                if (cmpt != null && StringUtils.isNotEmpty(cmpt.getProductCmpt())){
                    return IpsPlugin.getDefault().getImage("LinkProductCmpt.gif"); //$NON-NLS-1$
                }
            } catch (CoreException e) {
                // ignored exception, return default image
            }
            return IpsPlugin.getDefault().getImage("LinkPolicyCmptType.gif"); //$NON-NLS-1$
        } else {
            try {
                ITestPolicyCmptTypeParameter param = findTestPolicyCmptTypeParameter();
                if (param != null){
                    IRelation relation = param.findRelation();
                    if (relation != null){
                        return relation.getImage();
                    }
                }
            } catch (CoreException e) {
                // ignore exception, return default image
            }
            return IpsPlugin.getDefault().getImage("Relation.gif"); //$NON-NLS-1$
        }
	}

	/**
	 * This object has no parts.
	 */
	public IIpsObjectPart newPart(Class partType) {
		throw new IllegalArgumentException("Unknown part type" + partType); //$NON-NLS-1$
	}

	/**
	 * {@inheritDoc}
	 */
	public ITestPolicyCmpt newTargetTestPolicyCmptChild() {
		ITestPolicyCmpt param = newTargetTestPolicyCmptChildInternal(getNextPartId());
        return param;
	}

	/**
	 * Creates a new test policy component as target for this
	 * relation without updating the src file.
	 */
	private ITestPolicyCmpt newTargetTestPolicyCmptChildInternal(int id) {
		TestPolicyCmpt testPc = new TestPolicyCmpt(this, id);
		targetChild = testPc;
		return testPc;
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean isComposition() {
		return targetChild != null;
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean isAccoziation() {
		return targetChild == null;
	}

	/**
	 * {@inheritDoc}
	 */
	public IIpsElement[] getChildren() {
		IIpsElement[] childrenArray = null;
		if (targetChild != null) {
			childrenArray = new IIpsElement[1];
			childrenArray[0] = targetChild;
		} else {
			childrenArray = new IIpsElement[0];
		}
		return childrenArray;
	}

	/**
	 * {@inheritDoc}
	 */
	protected void reinitPartCollections() {
		targetChild = null;
	}

	/**
	 * {@inheritDoc}
	 */
	protected void reAddPart(IIpsObjectPart part) {
		if (part instanceof TestPolicyCmpt) {
			targetChild = (TestPolicyCmpt) part;
			return;
		}
		throw new RuntimeException("Unknown part type: " + part.getClass()); //$NON-NLS-1$
	}

	/**
	 * {@inheritDoc}
	 */
	protected IIpsObjectPart newPart(Element xmlTag, int id) {
		String xmlTagName = xmlTag.getNodeName();
		if (xmlTagName.equals(TestPolicyCmpt.TAG_NAME)) {
			return newTargetTestPolicyCmptChildInternal(id);
		}
		throw new RuntimeException(
				"Could not create part for tag name: " + xmlTagName); //$NON-NLS-1$
	}

	/**
	 * {@inheritDoc}
	 */
	public ITestCase getTestCase() {
		return ((ITestPolicyCmpt) getParent()).getTestCase();
	}

	/**
	 * {@inheritDoc}
	 */
	public void validateGroup(MessageList messageList) throws CoreException {
		
        // check all messages only once, thus if the same test relation is used more than one
        //  only one message are added to the list of validation errors
        
		// validate if the test policy component type parameter exists
        ITestPolicyCmptTypeParameter testCaseTypeParam = findTestPolicyCmptTypeParameter();
        if (messageList.getMessageByCode(MSGCODE_TEST_CASE_TYPE_PARAM_NOT_FOUND) == null){
    		if (testCaseTypeParam == null){
    			String text = Messages.TestPolicyCmptRelation_ValidationError_TestCaseTypeParamNotFound;
    			Message msg = new Message(MSGCODE_TEST_CASE_TYPE_PARAM_NOT_FOUND, text, Message.ERROR, this, PROPERTY_POLICYCMPTTYPE);
    			messageList.add(msg);	
    		}
        }
		// abort the rest of the validation if the test case type parameter not found
		if (testCaseTypeParam == null){
			return;
		}
		
		// validate if the model relation exists
        if (messageList.getMessageByCode(MSGCODE_MODEL_RELATION_NOT_FOUND) == null){
            IRelation modelRelation = testCaseTypeParam.findRelation();
            if (modelRelation == null){
    			String text = NLS.bind(Messages.TestPolicyCmptRelation_ValidationError_ModelRelationNotFound, testCaseTypeParam.getRelation());
    			Message msg = new Message(MSGCODE_MODEL_RELATION_NOT_FOUND, text, Message.ERROR, this, ITestPolicyCmptTypeParameter.PROPERTY_POLICYCMPTTYPE);
    			messageList.add(msg);		
    		}
        }
		
        // validate the min and max occurence defined in the test policy component type
        // parameter
        ITestPolicyCmpt parentTestPolicyCmpt = (ITestPolicyCmpt)getParent();
        ITestPolicyCmptRelation[] parentRelations = parentTestPolicyCmpt.getTestPolicyCmptRelations();

        int count = 0;
        for (int i = 0; i < parentRelations.length; i++) {
            ITestPolicyCmptRelation currRelation = parentRelations[i];
            if (currRelation.getTestPolicyCmptTypeParameter().equals(getTestPolicyCmptTypeParameter())) {
                count++;
            }
        }

        if (messageList.getMessageByCode(MSGCODE_MIN_INSTANCES_NOT_REACHED) == null) {
            if (count < testCaseTypeParam.getMinInstances()) {
                String text = NLS.bind(Messages.TestPolicyCmptRelation_ValidationError_MinimumNotReached,
                        "" + testCaseTypeParam.getMinInstances(), testPolicyCmptTypeParameter); //$NON-NLS-1$
                Message msg = new Message(MSGCODE_MIN_INSTANCES_NOT_REACHED, text, Message.ERROR, this,
                        ITestPolicyCmptTypeParameter.PROPERTY_MIN_INSTANCES);
                messageList.add(msg);
            }
        }

        if (messageList.getMessageByCode(MSGCODE_MAX_INSTANCES_REACHED) == null) {
            if (count > testCaseTypeParam.getMaxInstances()) {
                String text = NLS.bind(Messages.TestPolicyCmptRelation_ValidationError_MaximumReached,
                        "" + testCaseTypeParam.getMaxInstances(), testPolicyCmptTypeParameter); //$NON-NLS-1$
                Message msg = new Message(MSGCODE_MAX_INSTANCES_REACHED, text, Message.ERROR, this,
                        ITestPolicyCmptTypeParameter.PROPERTY_MAX_INSTANCES);
                messageList.add(msg);
            }
        }
	}
	
	/**
	 * {@inheritDoc}
	 */
	public void validateSingle(MessageList messageList) throws CoreException {
		// validate if the test case type param exists
		ITestPolicyCmptTypeParameter param = null;
		try {
			param = getTestCase().findTestPolicyCmptTypeParameter(this);
		} catch (CoreException e) {
			//	ignore exception, the param will be used to indicate errors
		}
        
		// check if the corresponding test parameter exists
		if (param == null){
			String text = NLS.bind(Messages.TestPolicyCmptRelation_ValidationError_TestCaseTypeNotFound, getTestPolicyCmptTypeParameter());
			Message msg = new Message(MSGCODE_TEST_CASE_TYPE_PARAM_NOT_FOUND, text, Message.ERROR, this, PROPERTY_POLICYCMPTTYPE);
			messageList.add(msg);	
        }
        
		// for assoziations check if the target is in the test case
		if (isAccoziation()){
			if (getTestCase().findTestPolicyCmpt(getTarget()) == null){
				String text = NLS.bind(Messages.TestPolicyCmptRelation_ValidationError_AssoziationNotFound, getTarget());
				Message msg = new Message(MSGCODE_ASSOZIATION_TARGET_NOT_IN_TEST_CASE, text, Message.ERROR, this, PROPERTY_POLICYCMPTTYPE);
				messageList.add(msg);	
			}
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	protected void validateThis(MessageList list) throws CoreException {
        super.validateThis(list);
        validateGroup(list);
		validateSingle(list);
	}
}