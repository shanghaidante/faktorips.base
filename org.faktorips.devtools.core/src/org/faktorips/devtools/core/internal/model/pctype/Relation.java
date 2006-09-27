/*******************************************************************************
 * Copyright (c) 2005,2006 Faktor Zehn GmbH und andere.
 *
 * Alle Rechte vorbehalten.
 *
 * Dieses Programm und alle mitgelieferten Sachen (Dokumentationen, Beispiele,
 * Konfigurationen, etc.) duerfen nur unter den Bedingungen der 
 * Faktor-Zehn-Community Lizenzvereinbarung - Version 0.1 (vor Gruendung Community) 
 * genutzt werden, die Bestandteil der Auslieferung ist und auch unter
 *   http://www.faktorips.org/legal/cl-v01.html
 * eingesehen werden kann.
 *
 * Mitwirkende:
 *   Faktor Zehn GmbH - initial API and implementation - http://www.faktorzehn.de
 *
 *******************************************************************************/

package org.faktorips.devtools.core.internal.model.pctype;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.graphics.Image;
import org.faktorips.devtools.core.IpsPlugin;
import org.faktorips.devtools.core.IpsStatus;
import org.faktorips.devtools.core.internal.model.IpsObjectPart;
import org.faktorips.devtools.core.internal.model.ValidationUtils;
import org.faktorips.devtools.core.model.IIpsObjectPart;
import org.faktorips.devtools.core.model.IpsObjectType;
import org.faktorips.devtools.core.model.pctype.IPolicyCmptType;
import org.faktorips.devtools.core.model.pctype.IRelation;
import org.faktorips.devtools.core.model.pctype.ITypeHierarchy;
import org.faktorips.devtools.core.model.pctype.RelationType;
import org.faktorips.util.message.Message;
import org.faktorips.util.message.MessageList;
import org.w3c.dom.Document;
import org.w3c.dom.Element;


/**
 *
 */
public class Relation extends IpsObjectPart implements IRelation {

    final static String TAG_NAME = "Relation"; //$NON-NLS-1$

    private RelationType type = RelationType.ASSOZIATION;
    private String target = ""; //$NON-NLS-1$
    private String targetRoleSingular = ""; //$NON-NLS-1$
    private String targetRolePlural = ""; //$NON-NLS-1$
    private int minCardinality = 0;
    private int maxCardinality = 1; //$NON-NLS-1$
    private boolean productRelevant = true;
    private String containerRelation = ""; //$NON-NLS-1$
    private String reverseRelation = ""; //$NON-NLS-1$
    private boolean readOnlyContainer = false;
    private String targetRoleSingularProductSide = ""; //$NON-NLS-1$
    private String targetRolePluralProductSide = ""; //$NON-NLS-1$
    private int minCardinalityProductSide = 0;
    private int maxCardinalityProductSide = 1;
    private boolean deleted = false;

    public Relation(IPolicyCmptType pcType, int id) {
        super(pcType, id);
    }

    /**
     * Constructor for testing purposes.
     */
    public Relation() {
    }

    /**
     * Overridden.
     */
    public IPolicyCmptType getPolicyCmptType() {
        return (PolicyCmptType)getIpsObject();
    }
    
    /** 
     * Overridden.
     */
    public String getName() {
        return targetRoleSingular;
    }
    
    /** 
     * Overridden.
     */
    public void delete() {
        ((PolicyCmptType)getIpsObject()).removeRelation(this);
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
     * Overridden.
     */
    public RelationType getRelationType() {
        return type;
    }
    
    /**
	 * {@inheritDoc}
	 */
	public boolean isAssoziation() {
		return type.isAssoziation();
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean isForwardComposition() {
		return type.isComposition();
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean isReverseComposition() {
		return type.isReverseComposition();
	}

	/** 
     * Overridden.
     */
    public void setRelationType(RelationType newType) {
        RelationType oldType = type;
        type = newType;
        valueChanged(oldType, newType);
    }
    
    /**
     * Overridden.
     */
    public boolean isReadOnlyContainer() {
        return readOnlyContainer;
    }
    
    /**
     * Overridden.
     */
    public void setReadOnlyContainer(boolean flag) {
        boolean oldValue = readOnlyContainer;
        this.readOnlyContainer = flag;
        valueChanged(oldValue, readOnlyContainer);
    }
    
    /** 
     * Overridden.
     */
    public String getTarget() {
        return target;
    }
    
    /**
     * Overridden.
     */
    public IPolicyCmptType findTarget() throws CoreException {
        if (StringUtils.isEmpty(target)) {
            return null;
        }
        return getIpsProject().findPolicyCmptType(target);
    }
    
    /** 
     * Overridden.
     */
    public void setTarget(String newTarget) {
        String oldTarget = target;
        target = newTarget;
        valueChanged(oldTarget, newTarget);
    }

    /** 
     * Overridden.
     */
    public String getTargetRoleSingular() {
        return targetRoleSingular;
    }

    /** 
     * Overridden.
     */
    public void setTargetRoleSingular(String newRole) {
        String oldRole = targetRoleSingular;
        targetRoleSingular = newRole;
        valueChanged(oldRole, newRole);
    }
    

    /**
     * Overridden.
     */
    public String getTargetRolePlural() {
        return targetRolePlural;
    }
    
    /**
     * Overridden.
     */
    public void setTargetRolePlural(String newRole) {
        String oldRole = targetRolePlural;
        targetRolePlural = newRole;
        valueChanged(oldRole, newRole);
    }
    
    /** 
     * Overridden.
     */
    public int getMinCardinality() {
        return minCardinality;
    }

    /** 
     * Overridden.
     */
    public void setMinCardinality(int newValue) {
        int oldValue = minCardinality;
        minCardinality = newValue;
        valueChanged(oldValue, newValue);
        
    }

    /** 
     * Overridden.
     */
    public int getMaxCardinality() {
        return maxCardinality;
    }
    
    /**
     * {@inheritDoc}
     */
    public boolean is1ToMany() {
    	return maxCardinality > 1;
    }
    
    /**
	 * {@inheritDoc}
	 */
	public boolean is1To1() {
		return maxCardinality == 1;
	}

	/**
     * Overridden.
     */ 
    public void setMaxCardinality(int newValue) {
        int oldValue = maxCardinality;
        maxCardinality = newValue;
        valueChanged(oldValue, newValue);
    }

    /** 
     * Overridden.
     */
    public boolean isProductRelevant() {
        return productRelevant;
    }

    /** 
     * Overridden.
     */
    public void setProductRelevant(boolean newValue) {
        boolean oldValue = productRelevant;
        productRelevant = newValue;
        valueChanged(oldValue, newValue);
    }
    
	/**
	 * Overridden.
	 */
	public String getTargetRoleSingularProductSide() {
		return targetRoleSingularProductSide;
	}

	/**
	 * Overridden.
	 */
	public void setTargetRoleSingularProductSide(String newRole) {
        String oldRole = targetRoleSingularProductSide;
        targetRoleSingularProductSide = newRole;
        valueChanged(oldRole, newRole);
	}

	/**
	 * Overridden.
	 */
	public void setTargetRolePluralProductSide(String newRole) {
        String oldRole = targetRolePluralProductSide;
        targetRolePluralProductSide = newRole;
        valueChanged(oldRole, newRole);
	}

    /**
	 * Overridden.
	 */
	public String getTargetRolePluralProductSide() {
		return targetRolePluralProductSide;
	}


	/**
	 * Overridden.
	 */
	public int getMinCardinalityProductSide() {
		return minCardinalityProductSide;
	}

	/**
	 * Overridden.
	 */
	public void setMinCardinalityProductSide(int newMin) {
		int oldMin = minCardinalityProductSide;
		minCardinalityProductSide = newMin;
		valueChanged(oldMin, newMin);
	}

	/**
	 * Overridden.
	 */
	public int getMaxCardinalityProductSide() {
		return maxCardinalityProductSide;
	}

	/**
	 * Overridden.
	 */
	public void setMaxCardinalityProductSide(int newMax) {
		int oldMax = maxCardinalityProductSide;
		maxCardinalityProductSide = newMax;
		valueChanged(oldMax, newMax);
	}

	/** 
     * Overridden.
     */
    public String getContainerRelation() {
        return containerRelation;
    }
    
    /**
     * Overridden.
     */
    public boolean hasContainerRelation() {
        return !StringUtils.isEmpty(containerRelation);
    }

    /**
     * {@inheritDoc}
     */
    public IRelation findContainerRelation() throws CoreException {
    	if (StringUtils.isEmpty(containerRelation)) {
    		return null;
    	}
        IPolicyCmptType type = (IPolicyCmptType)getIpsObject();
        IPolicyCmptType[] supertypes = type.getSupertypeHierarchy().getAllSupertypesInclSelf(type);
        for (int i=0; i<supertypes.length; i++) {
            IRelation[] relations = supertypes[i].getRelations();
            for (int j=0; j<relations.length; j++) {
                if (containerRelation.equals(relations[j].getTargetRoleSingular())) {
                    return relations[j];
                }
            }
        }
        return null;
    }
    
    /**
	 * {@inheritDoc}
	 */
	public boolean isContainerRelationImplementation() throws CoreException {
		return StringUtils.isNotEmpty(containerRelation);
	}
    
	/**
     * {@inheritDoc}
     */
    public boolean isContainerRelationImplementation(IRelation containerRelation) throws CoreException {
        if (containerRelation==null) {
            return false;
        }
        if (!containerRelation.isReadOnlyContainer()) {
            throw new CoreException(new IpsStatus("Relation " + containerRelation + " is not a container relation."));
        }
        if (!isContainerRelationImplementation()) {
            return false;
        }
        return containerRelation.equals(findContainerRelation());
    }

    /** 
     * {@inheritDoc}
     */
    public void setContainerRelation(String newRelation) {
        String oldValue = containerRelation;
        containerRelation = newRelation;
        valueChanged(oldValue, newRelation);
    }
    
    /**
     * {@inheritDoc}
     */
    public String getReverseRelation() {
        return reverseRelation;
    }
    
    /**
     * {@inheritDoc}
     */
    public void setReverseRelation(String newRelation) {
        String oldValue = this.reverseRelation;
        this.reverseRelation = newRelation;
        valueChanged(oldValue, newRelation);
    }
    
    /**
	 * {@inheritDoc}
	 */
	public IRelation[] findForwardCompositions() throws CoreException {
		if (!getRelationType().isReverseComposition()) {
			throw new CoreException(new IpsStatus("findForwardCompositions is only defined for reverse composition.")); //$NON-NLS-1$
		}
		IPolicyCmptType target = findTarget();
		if (target==null) {
			return new IRelation[0];
		}
		String typeName = getIpsObject().getQualifiedName();
		ITypeHierarchy hierarchy = target.getSupertypeHierarchy();
		IRelation[] relations = hierarchy.getAllRelations(target);
		List result = new ArrayList(relations.length);
		for (int i = 0; i < relations.length; i++) {
			IRelation relation = relations[i];
			if (relation.isForwardComposition()
					&& relation.getTarget().equals(typeName)
					&& relation.getReverseRelation().equals(getName())) {

				result.add(relation);
			}
		}
		return (IRelation[])result.toArray(new IRelation[result.size()]);
	}

	/**
	 * {@inheritDoc}
	 */
    public IRelation findReverseRelation() throws CoreException {
        if ((type.isComposition() || type.isReverseComposition()) && isContainerRelationImplementation()) {
        	return findReverseRelationOfImplementationRelation();
        }
    	if (StringUtils.isEmpty(reverseRelation)) {
            return null;
        }
        IPolicyCmptType target = findTarget();
        if (target==null) {
            return null;
        }
        IRelation[] relations = target.getRelations();
        for (int i=0; i<relations.length; i++) {
            if (relations[i].getName().equals(reverseRelation)) {
                return relations[i];
            }
        }
        return null;
    }
    
    private IRelation findReverseRelationOfImplementationRelation() throws CoreException {
        IRelation containerRel = findContainerRelation();
        if (containerRel==null) {
        	return null;
        }
        IRelation reverseContainerRel = containerRel.findReverseRelation();
        if (reverseContainerRel==null) {
        	return null;
        }
        IPolicyCmptType target = findTarget();
        if (target==null) {
            return null;
        }
        IRelation[] relations = target.getRelations();
        for (int i=0; i<relations.length; i++) {
            if (relations[i].getTarget().equals(getIpsObject().getQualifiedName()) 
            		&& reverseContainerRel==relations[i].findContainerRelation()) {
                return relations[i];
            }
        }
        return null;
    }
    
    /** 
     * {@inheritDoc}
     */
    public Image getImage() {
        Image baseImage;
        if (this.type==RelationType.COMPOSITION) {
            baseImage = IpsPlugin.getDefault().getImage("Composition.gif"); //$NON-NLS-1$
        } else if (this.type==RelationType.REVERSE_COMPOSITION) {
            baseImage = IpsPlugin.getDefault().getImage("ReverseComposition.gif"); //$NON-NLS-1$
        } else {
            baseImage = IpsPlugin.getDefault().getImage("Relation.gif"); //$NON-NLS-1$ 
        }
        if (isProductRelevant()) {
            return ProductRelevantIcon.createProductRelevantImage(baseImage);
        }
        return baseImage;
    }

    /** 
     * {@inheritDoc}
     */
    protected void validateThis(MessageList list) throws CoreException {
        super.validateThis(list);
        ValidationUtils.checkIpsObjectReference(target, IpsObjectType.POLICY_CMPT_TYPE, "target", this,  //$NON-NLS-1$
        		PROPERTY_TARGET, MSGCODE_TARGET_DOES_NOT_EXIST, list); //$NON-NLS-1$
        ValidationUtils.checkStringPropertyNotEmpty(targetRoleSingular, "target role", this, PROPERTY_TARGET_ROLE_SINGULAR,  //$NON-NLS-1$
        		MSGCODE_TARGET_ROLE_SINGULAR_MUST_BE_SET, list); //$NON-NLS-1$

        if (maxCardinality == 0) {
        	String text = Messages.Relation_msgMaxCardinalityMustBeAtLeast1;
        	list.add(new Message(MSGCODE_MAX_CARDINALITY_MUST_BE_AT_LEAST_1, text, Message.ERROR, this, PROPERTY_MAX_CARDINALITY)); //$NON-NLS-1$
        } else if (maxCardinality == 1 && isReadOnlyContainer() && getRelationType() != RelationType.REVERSE_COMPOSITION) {
        	String text = Messages.Relation_msgMaxCardinalityForContainerRelationTooLow;
        	list.add(new Message(MSGCODE_MAX_CARDINALITY_FOR_CONTAINERRELATION_TOO_LOW, text, Message.ERROR, this, new String[]{PROPERTY_READONLY_CONTAINER, PROPERTY_MAX_CARDINALITY})); //$NON-NLS-1$
        } else if (minCardinality > maxCardinality) {
        	String text = Messages.Relation_msgMinCardinalityGreaterThanMaxCardinality;
        	list.add(new Message(MSGCODE_MAX_IS_LESS_THAN_MIN, text, Message.ERROR, this, new String[]{PROPERTY_MIN_CARDINALITY, PROPERTY_MAX_CARDINALITY})); //$NON-NLS-1$
        }
        
        if (maxCardinality != 1 && this.type == RelationType.REVERSE_COMPOSITION) {
        	String text = Messages.Relation_msgRevereseCompositionMustHaveMaxCardinality1;
        	list.add(new Message(MSGCODE_MAX_CARDINALITY_MUST_BE_1_FOR_REVERSE_COMPOSITION, text, Message.ERROR, this, new String[] {PROPERTY_MAX_CARDINALITY, PROPERTY_RELATIONTYPE}));
        }
        
        if (this.type == RelationType.REVERSE_COMPOSITION && isProductRelevant()) {
        	String text = Messages.Relation_msgReverseCompositionCantBeMarkedAsProductRelevant;
        	list.add(new Message(MSGCODE_REVERSE_COMPOSITION_CANT_BE_MARKED_AS_PRODUCT_RELEVANT, text, Message.ERROR, this, new String[] {PROPERTY_PRODUCT_RELEVANT, PROPERTY_RELATIONTYPE}));
        }
       
        validateProductSide(list);
        
        IPolicyCmptType type = getPolicyCmptType();
        IPolicyCmptType[] supertypes = type.getSupertypeHierarchy().getAllSupertypesInclSelf(type);
        for (int i = 0; i < supertypes.length; i++) {
			IRelation[] relations = supertypes[i].getRelations();
			for (int j = 0; j < relations.length; j++) {
				if (relations[j] != this && !StringUtils.isEmpty(targetRoleSingular) && relations[j].getTargetRoleSingular().equals(targetRoleSingular)) {
		            String text = Messages.Relation_msgSameSingularRoleName;
		            list.add(new Message(MSGCODE_SAME_SINGULAR_ROLENAME, text, Message.ERROR, this, PROPERTY_TARGET_ROLE_SINGULAR));
				}
		        if (relations[j] != this && !StringUtils.isEmpty(targetRolePlural) && relations[j].getTargetRolePlural().equals(targetRolePlural))  {
		            String text = Messages.Relation_msgSamePluralRolename;
		            list.add(new Message(MSGCODE_CONTAINERRELATION_SAME_PLURAL_ROLENAME, text, Message.ERROR, this, PROPERTY_TARGET_ROLE_PLURAL)); //$NON-NLS-1$
		        }
		        
		        if (!this.isProductRelevant()) {
		        	continue;
		        }
		        
				if (relations[j] != this && !StringUtils.isEmpty(targetRoleSingularProductSide) && relations[j].getTargetRoleSingularProductSide().equals(targetRoleSingularProductSide)) {
		            String text = Messages.Relation_msgSameSingularRoleName;
		            list.add(new Message(MSGCODE_SAME_SINGULAR_ROLENAME_PRODUCTSIDE, text, Message.ERROR, this, PROPERTY_TARGET_ROLE_SINGULAR_PRODUCTSIDE));
				}
				if (relations[j] != this && !StringUtils.isEmpty(targetRolePluralProductSide) && relations[j].getTargetRolePluralProductSide().equals(targetRolePluralProductSide)) {
		            String text = Messages.Relation_msgSameSingularRoleName;
		            list.add(new Message(MSGCODE_SAME_PLURAL_ROLENAME_PRODUCTSIDE, text, Message.ERROR, this, PROPERTY_TARGET_ROLE_PLURAL_PRODUCTSIDE));
				}
			}
		}
        
        validateContainerRelation(list);
        validateReverseRelation(list);
    }
    
    private void validateProductSide(MessageList list) {
		if (!isProductRelevant()) {
			return;
		}

		if (!this.getPolicyCmptType().isConfigurableByProductCmptType()) {
			String text = Messages.Relation_msgRelationCanBeProductRelevantOnlyIfTypeIs;
			list
					.add(new Message(
							MSGCODE_RELATION_CAN_BE_PRODUCT_RELEVANT_ONLY_IF_THE_TYPE_IS,
							text, Message.ERROR, this,
							PROPERTY_PRODUCT_RELEVANT));
		}

		if (StringUtils.isEmpty(this.getTargetRoleSingularProductSide())) {
			String text = Messages.Relation_msgNoTargetRoleSingular;
			list.add(new Message(MSGCODE_NO_TARGET_ROLE_SINGULAR_PRODUCTSIDE,
					text, Message.ERROR, this,
					PROPERTY_TARGET_ROLE_SINGULAR_PRODUCTSIDE));
		}

		if (StringUtils.isEmpty(this.getTargetRolePluralProductSide())) {
			String text = Messages.Relation_msgNoTargetRolePlural;
			list.add(new Message(MSGCODE_NO_TARGET_ROLE_PLURAL_PRODUCTSIDE,
					text, Message.ERROR, this,
					PROPERTY_TARGET_ROLE_PLURAL_PRODUCTSIDE));
		} else {
			if (this.getTargetRolePluralProductSide().equals(
					this.getTargetRoleSingularProductSide())) {
				String text = Messages.Relation_msgTargetRoleSingularIlleaglySameAsTargetRolePlural;
				list
						.add(new Message(
								MSGCODE_TARGET_ROLE_PLURAL_PRODUCTSIDE_EQULAS_TARGET_ROLE_SINGULAR_PRODUCTSIDE,
								text,
								Message.ERROR,
								this,
								new String[] {
										PROPERTY_TARGET_ROLE_SINGULAR_PRODUCTSIDE,
										PROPERTY_TARGET_ROLE_PLURAL_PRODUCTSIDE }));
			}
		}
	}
    
    private void validateContainerRelation(MessageList list) throws CoreException {
        if (StringUtils.isEmpty(containerRelation)) {
            return;
        }
        IRelation relation = findContainerRelation();
        if (relation==null) {
            String text = NLS.bind(Messages.Relation_msgContainerRelNotInSupertype, containerRelation);
            list.add(new Message(MSGCODE_CONTAINERRELATION_NOT_IN_SUPERTYPE, text, Message.ERROR, this, PROPERTY_CONTAINER_RELATION)); //$NON-NLS-1$
            return;
        }
        if (!relation.isReadOnlyContainer()) {
            String text = Messages.Relation_msgNotMarkedAsContainerRel;
            list.add(new Message(MSGCODE_NOT_MARKED_AS_CONTAINERRELATION, text, Message.ERROR, this, PROPERTY_CONTAINER_RELATION)); //$NON-NLS-1$
            return;
        }
        if (relation.isProductRelevant() != isProductRelevant()) {
			String text = Messages.Relation_msgImplementationMustHaveSameProductRelevantValue;
			list
					.add(new Message(
							MSGCODE_IMPLEMENTATION_MUST_HAVE_SAME_PRODUCT_RELEVANT_VALUE,
							text, Message.ERROR, new String[] {
									PROPERTY_CONTAINER_RELATION,
									PROPERTY_PRODUCT_RELEVANT }));
		}
        IPolicyCmptType superRelationTarget = getIpsProject().findPolicyCmptType(relation.getTarget());
        if (superRelationTarget==null) {
            String text = Messages.Relation_msgNoTarget;
            list.add(new Message(MSGCODE_CONTAINERRELATION_TARGET_DOES_NOT_EXIST, text, Message.WARNING, this, PROPERTY_CONTAINER_RELATION)); //$NON-NLS-1$
            return;
        }
        IPolicyCmptType pcType = getIpsProject().findPolicyCmptType(target);
        if (pcType!=null) {
            ITypeHierarchy hierachy = pcType.getSupertypeHierarchy();
            if (!superRelationTarget.equals(pcType) && !hierachy.isSupertypeOf(superRelationTarget, pcType)) {
                String text = Messages.Relation_msgTargetNotSubclass;
                list.add(new Message(MSGCODE_TARGET_NOT_SUBCLASS, text, Message.ERROR, this, PROPERTY_CONTAINER_RELATION));     //$NON-NLS-1$
            }
        }
        IRelation reverseRel = findContainerRelationOfTypeReverseComposition();
        if(reverseRel != null && reverseRel != relation)  {
            String text = Messages.Relation_msgContainerRelNotReverseRel;
            list.add(new Message(MSGCODE_CONTAINERRELATION_NOT_REVERSERELATION, text, Message.ERROR, this, PROPERTY_CONTAINER_RELATION)); //$NON-NLS-1$
            return;
        }
    }
    
    private void validateReverseRelation(MessageList list) throws CoreException {
        if (StringUtils.isEmpty(reverseRelation)) {
            return;
        }
        IRelation reverseRelationObj = findReverseRelation();
        if (reverseRelationObj==null) {
            String text = NLS.bind(Messages.Relation_msgRelationNotInTarget, reverseRelation, target);
            list.add(new Message(MSGCODE_REVERSERELATION_NOT_IN_TARGET, text, Message.ERROR, this, PROPERTY_REVERSE_RELATION)); //$NON-NLS-1$
            return;
        }
        if (!reverseRelationObj.getReverseRelation().equals(getName())) {
            String text = Messages.Relation_msgReverseRelationNotSpecified;
            list.add(new Message(MSGCODE_REVERSERELATION_NOT_SPECIFIED, text, Message.ERROR, this, PROPERTY_REVERSE_RELATION)); //$NON-NLS-1$
        }
        if (isReadOnlyContainer() && ! reverseRelationObj.isReadOnlyContainer()) {
            String text = Messages.Relation_msgReverseRelOfContainerRelMustBeContainerRelToo;
            list.add(new Message(MSGCODE_REVERSERELATION_OF_CONTAINERRELATION_MUST_BE_CONTAINERRELATION_TOO, text, Message.ERROR, this, PROPERTY_REVERSE_RELATION)); //$NON-NLS-1$
        }
        
        if((type.isComposition() && !reverseRelationObj.getRelationType().isReverseComposition())
                || (reverseRelationObj.getRelationType().isComposition() && !type.isReverseComposition())) {
	            String text = Messages.Relation_msgReverseCompositionMissmatch;
	            list.add(new Message(MSGCODE_REVERSE_COMPOSITION_MISSMATCH, text, Message.ERROR, this, new String[]{PROPERTY_REVERSE_RELATION, PROPERTY_READONLY_CONTAINER})); //$NON-NLS-1$
	    }
	    if  ((type.isAssoziation() && !reverseRelationObj.getRelationType().isAssoziation())
	            || (reverseRelationObj.getRelationType().isAssoziation() && !type.isAssoziation())) {
	            String text = Messages.Relation_msgReverseAssociationMissmatch;
	            list.add(new Message(MSGCODE_REVERSE_ASSOCIATION_MISSMATCH, text, Message.ERROR, this, new String[]{PROPERTY_REVERSE_RELATION})); //$NON-NLS-1$
        }
    }
    
    /**
     * Overridden.
     */
    public IRelation findContainerRelationOfTypeReverseComposition() throws CoreException {
        IRelation reverseRel = findReverseRelation();
        if(reverseRel != null) {
            IRelation containerRel = reverseRel.findContainerRelation();
            if(containerRel != null) {
                IRelation reverseContainerRel = containerRel.findReverseRelation();
                if(reverseContainerRel != null && 
                        reverseContainerRel.getRelationType() == RelationType.REVERSE_COMPOSITION) {
                    return reverseContainerRel;
                }
            }
        }
        return null;
    }
    
    /**
     * {@inheritDoc}
     */
    public IRelation[] findContainerRelationCandidates() throws CoreException {
        List containerRelationCandidates = new ArrayList();
        IPolicyCmptType targetPolicyCmptType = findTarget();
        if (targetPolicyCmptType != null){
            IPolicyCmptType type = getPolicyCmptType();
            IPolicyCmptType[] supertypes = type.getSupertypeHierarchy().getAllSupertypesInclSelf(type);
            // search for relations inside each policy cmpt inside the supertype hierarchy
            for (int i = 0; i < supertypes.length; i++) {
                // check all relations of the policy cmpt type
                IRelation[] relations = supertypes[i].getRelations();
                for (int j = 0; j < relations.length; j++) {
                    if (!relations[j].isReadOnlyContainer())
                        continue;
                    
                    IPolicyCmptType targetOfContainerRelation = relations[j].findTarget();
                    if (targetOfContainerRelation == null)
                        continue;
                    
                    // check if the target of the container relation is the same policy cmpt type
                    // the relation uses as target
                    if (targetOfContainerRelation.equals(targetPolicyCmptType)) {
                        // candidate found: the target of the container relation is equal the
                        // target of this relation
                        containerRelationCandidates.add(relations[j]);
                        continue;
                    }
                    
                    // check if the target of the container relation is a subertype of the
                    // target of this relation
                    ITypeHierarchy hierarchyOfTarget = targetPolicyCmptType.getSupertypeHierarchy();
                    if (hierarchyOfTarget.isSupertypeOf(targetOfContainerRelation, targetPolicyCmptType)){
                        // candidate found: the target of the container relation is a supertype of the
                        // target of this relation
                        containerRelationCandidates.add(relations[j]);
                        continue;
                    }
                }
            }
        }
        return (IRelation[]) containerRelationCandidates.toArray(new IRelation[0]);
    }

    /**
     * Overridden.
     */
    protected Element createElement(Document doc) {
        return doc.createElement(TAG_NAME);
    }
    
    /**
     * Overridden.
     */
    protected void initPropertiesFromXml(Element element, Integer id) {
        super.initPropertiesFromXml(element, id);
        type = RelationType.getRelationType(element.getAttribute(PROPERTY_RELATIONTYPE));
        readOnlyContainer = Boolean.valueOf(element.getAttribute(PROPERTY_READONLY_CONTAINER)).booleanValue();
        target = element.getAttribute(PROPERTY_TARGET);
        targetRoleSingular = element.getAttribute(PROPERTY_TARGET_ROLE_SINGULAR);
        targetRolePlural = element.getAttribute(PROPERTY_TARGET_ROLE_PLURAL);
        try {
            minCardinality = Integer.parseInt(element.getAttribute(PROPERTY_MIN_CARDINALITY));
        } catch (NumberFormatException e) {
        	minCardinality = 0;
        }
        String max = element.getAttribute(PROPERTY_MAX_CARDINALITY);
        if (max.equals("*")) { //$NON-NLS-1$
        	maxCardinality = CARDINALITY_MANY;
        }
        else {
        	try {
        		maxCardinality = Integer.parseInt(max);
        	} catch (NumberFormatException e) {
        		maxCardinality = 0;
        	}
        }
        containerRelation = element.getAttribute(PROPERTY_CONTAINER_RELATION);
        reverseRelation = element.getAttribute(PROPERTY_REVERSE_RELATION);
        productRelevant = Boolean.valueOf(element.getAttribute(PROPERTY_PRODUCT_RELEVANT)).booleanValue();
        targetRoleSingularProductSide = element.getAttribute(PROPERTY_TARGET_ROLE_SINGULAR_PRODUCTSIDE);
        targetRolePluralProductSide = element.getAttribute(PROPERTY_TARGET_ROLE_PLURAL_PRODUCTSIDE);
        try {
            minCardinalityProductSide = Integer.parseInt(element.getAttribute(PROPERTY_MIN_CARDINALITY_PRODUCTSIDE));
        } catch (NumberFormatException e) {
        	minCardinalityProductSide = 0;
        }
        String maxPS = element.getAttribute(PROPERTY_MAX_CARDINALITY_PRODUCTSIDE);
        if (maxPS.equals("*")) { //$NON-NLS-1$
        	maxCardinalityProductSide = CARDINALITY_MANY;
        }
        else {
        	try {
        		maxCardinalityProductSide = Integer.parseInt(maxPS);
        	} catch (NumberFormatException e) {
        		maxCardinalityProductSide = 0;
        	}
        }
    }
    
    /**
     * Overridden.
     */
    protected void propertiesToXml(Element newElement) {
        super.propertiesToXml(newElement);
        newElement.setAttribute(PROPERTY_RELATIONTYPE, type.getId());
        newElement.setAttribute(PROPERTY_READONLY_CONTAINER, "" + readOnlyContainer); //$NON-NLS-1$
        newElement.setAttribute(PROPERTY_TARGET, target);
        newElement.setAttribute(PROPERTY_TARGET_ROLE_SINGULAR, targetRoleSingular);
        newElement.setAttribute(PROPERTY_TARGET_ROLE_PLURAL, targetRolePlural);
        newElement.setAttribute(PROPERTY_MIN_CARDINALITY, "" + minCardinality); //$NON-NLS-1$
        
        if (maxCardinality == CARDINALITY_MANY) {
            newElement.setAttribute(PROPERTY_MAX_CARDINALITY, "*"); //$NON-NLS-1$
        }
        else {
            newElement.setAttribute(PROPERTY_MAX_CARDINALITY, "" + maxCardinality); //$NON-NLS-1$
        }
        
        newElement.setAttribute(PROPERTY_CONTAINER_RELATION, containerRelation);
        newElement.setAttribute(PROPERTY_REVERSE_RELATION, reverseRelation);
        newElement.setAttribute(PROPERTY_PRODUCT_RELEVANT, "" + productRelevant); //$NON-NLS-1$
        newElement.setAttribute(PROPERTY_TARGET_ROLE_SINGULAR_PRODUCTSIDE, targetRoleSingularProductSide);
        newElement.setAttribute(PROPERTY_TARGET_ROLE_PLURAL_PRODUCTSIDE, targetRolePluralProductSide);
        newElement.setAttribute(PROPERTY_MIN_CARDINALITY_PRODUCTSIDE, "" + minCardinalityProductSide); //$NON-NLS-1$
        
        if (maxCardinalityProductSide == CARDINALITY_MANY) {
            newElement.setAttribute(PROPERTY_MAX_CARDINALITY_PRODUCTSIDE, "*"); //$NON-NLS-1$
        }
        else {
            newElement.setAttribute(PROPERTY_MAX_CARDINALITY_PRODUCTSIDE, "" + maxCardinalityProductSide); //$NON-NLS-1$
        }
    }
    
	public IIpsObjectPart newPart(Class partType) {
		throw new IllegalArgumentException("Unknown part type" + partType); //$NON-NLS-1$
	}

}
