/*******************************************************************************
 * Copyright (c) 2005-2012 Faktor Zehn AG und andere.
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

package org.faktorips.devtools.stdbuilder.xpand.productcmpt.model;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import org.eclipse.core.runtime.CoreException;
import org.faktorips.devtools.core.builder.naming.BuilderAspect;
import org.faktorips.devtools.core.exception.CoreRuntimeException;
import org.faktorips.devtools.core.model.pctype.IPolicyCmptType;
import org.faktorips.devtools.core.model.pctype.IPolicyCmptTypeAttribute;
import org.faktorips.devtools.core.model.productcmpttype.IProductCmptType;
import org.faktorips.devtools.core.model.productcmpttype.IProductCmptTypeAssociation;
import org.faktorips.devtools.core.model.productcmpttype.IProductCmptTypeAttribute;
import org.faktorips.devtools.stdbuilder.xpand.model.GeneratorModelContext;
import org.faktorips.devtools.stdbuilder.xpand.model.ModelService;
import org.faktorips.devtools.stdbuilder.xpand.model.XClass;
import org.faktorips.devtools.stdbuilder.xpand.model.XDerivedUnionAssociation;
import org.faktorips.devtools.stdbuilder.xpand.policycmpt.model.XPolicyAttribute;
import org.faktorips.devtools.stdbuilder.xpand.policycmpt.model.XPolicyCmptClass;

public abstract class XProductClass extends XClass {

    private Set<XProductAttribute> attributes;

    private Set<XPolicyAttribute> configuredAttributes;

    private Set<XProductAssociation> associations;

    private Set<XDerivedUnionAssociation> subsettedDerivedUnions;

    public XProductClass(IProductCmptType ipsObjectPartContainer, GeneratorModelContext modelContext,
            ModelService modelService) {
        super(ipsObjectPartContainer, modelContext, modelService);
    }

    @Override
    public IProductCmptType getIpsObjectPartContainer() {
        return (IProductCmptType)super.getIpsObjectPartContainer();
    }

    @Override
    public IProductCmptType getType() {
        return getIpsObjectPartContainer();
    }

    @Override
    protected void clearCaches() {
        super.clearCaches();
        attributes = null;
        configuredAttributes = null;
        associations = null;
        subsettedDerivedUnions = null;
    }

    public abstract boolean isChangeOverTime();

    @Override
    public Set<XProductAttribute> getAttributes() {
        checkForUpdate();
        if (attributes == null) {
            synchronized (this) {
                if (attributes == null) {
                    attributes = initNodesForParts(getProductAttributes(isChangeOverTime()), XProductAttribute.class);
                }
            }
        }
        return new CopyOnWriteArraySet<XProductAttribute>(attributes);
    }

    public Set<XPolicyAttribute> getConfiguredAttributes() {
        checkForUpdate();
        if (configuredAttributes == null) {
            synchronized (this) {
                if (configuredAttributes == null) {
                    configuredAttributes = initNodesForParts(getConfiguredAttributes(isChangeOverTime()),
                            XPolicyAttribute.class);
                }
            }
        }
        return new CopyOnWriteArraySet<XPolicyAttribute>(configuredAttributes);
    }

    @Override
    public Set<XProductAssociation> getAssociations() {
        checkForUpdate();
        if (associations == null) {
            synchronized (this) {
                if (associations == null) {
                    associations = initNodesForParts(getProductAssociations(isChangeOverTime()),
                            XProductAssociation.class);
                }
            }
        }
        return new CopyOnWriteArraySet<XProductAssociation>(associations);
    }

    @Override
    public Set<XDerivedUnionAssociation> getSubsettedDerivedUnions() {
        checkForUpdate();
        if (subsettedDerivedUnions == null) {
            synchronized (this) {
                if (subsettedDerivedUnions == null) {
                    subsettedDerivedUnions = initNodesForParts(getProductDerivedUnionAssociations(isChangeOverTime()),
                            XDerivedUnionAssociation.class);
                }
            }
        }
        return new CopyOnWriteArraySet<XDerivedUnionAssociation>(subsettedDerivedUnions);
    }

    /**
     * Getting the list of associations defined in this type. With the parameter
     * changableAssociations you could specify whether you want the associations that are changeable
     * over time or not changeable (sometimes called static) associations.
     * <p>
     * This method does not return any derived union association.
     * <p>
     * This method needs to be final because it may be called in constructor
     * 
     * @see #getProductDerivedUnionAssociations(boolean)
     * 
     * @param changableAssociations true if you want only associations changeable over time, false
     *            to get only not changeable over time associations
     * @return The list of associations without derived unions
     */
    protected final Set<IProductCmptTypeAssociation> getProductAssociations(boolean changableAssociations) {
        return getProductAssociations(false, changableAssociations);
    }

    /**
     * This method returns the derived union associations defined in this type. With the parameter
     * changableAssociations you could specify whether you want the associations that are changeable
     * over time or not changeable (sometimes called static) associations.
     * <p>
     * If you want to have not derived union associations, @see #getProductAssociations(boolean)
     * <p>
     * This method needs to be final because it may be called in constructor
     * 
     * @param changableAssociations true if you want only associations changeable over time, false
     *            to get only not changeable over time associations
     * @return The list of derived union associations
     */
    protected final Set<IProductCmptTypeAssociation> getProductDerivedUnionAssociations(boolean changableAssociations) {
        Set<IProductCmptTypeAssociation> notDerivedUnionAssociations = getProductAssociations(changableAssociations);
        Set<IProductCmptTypeAssociation> resultingAssociations = findSubsettedDerivedUnions(
                notDerivedUnionAssociations, IProductCmptTypeAssociation.class);
        return resultingAssociations;
    }

    private Set<IProductCmptTypeAssociation> getProductAssociations(boolean derivedUnion, boolean changableAssociations) {
        Set<IProductCmptTypeAssociation> resultingAssociations = new LinkedHashSet<IProductCmptTypeAssociation>();
        List<IProductCmptTypeAssociation> allAssociations = getType().getProductCmptTypeAssociations();
        for (IProductCmptTypeAssociation assoc : allAssociations) {
            // TODO FIPS-989
            if (assoc.isDerivedUnion() == derivedUnion && changableAssociations) {
                resultingAssociations.add(assoc);
            }
        }
        return resultingAssociations;
    }

    /**
     * Returns the list of attributes. With the parameter you could specify whether you want the
     * attributes that change over time or attributes not changing over time.
     * <p>
     * This method needs to be final because it may be called in constructor
     * 
     * @param changableAttributes True to get attributes that change over time, false to get all
     *            other attributes
     * @return the list of attributes defined in this type
     */
    protected final Set<IProductCmptTypeAttribute> getProductAttributes(boolean changableAttributes) {
        Set<IProductCmptTypeAttribute> resultingAttributes = new LinkedHashSet<IProductCmptTypeAttribute>();
        List<IProductCmptTypeAttribute> allAttributes = getType().getProductCmptTypeAttributes();
        for (IProductCmptTypeAttribute attr : allAttributes) {
            if (changableAttributes == attr.isChangingOverTime()) {
                resultingAttributes.add(attr);
            }
        }
        return resultingAttributes;
    }

    /**
     * Returns the list of configured policy attributes. With the parameter you could specify
     * whether you want the attributes that change over time or attributes not changing over time.
     * <p>
     * This method needs to be final because it may be called in constructor
     * 
     * @param changableAttributes True to get attributes that change over time, false to get all
     *            other attributes
     * @return the list of policy attributes configured by this product component.
     */
    protected final Set<IPolicyCmptTypeAttribute> getConfiguredAttributes(boolean changableAttributes) {
        Set<IPolicyCmptTypeAttribute> resultingAttributes = new LinkedHashSet<IPolicyCmptTypeAttribute>();
        if (isConfigurationForPolicyCmptType()) {
            try {
                IPolicyCmptType policyType = getType().findPolicyCmptType(getIpsProject());
                List<IPolicyCmptTypeAttribute> allAttributes = policyType.getPolicyCmptTypeAttributes();
                for (IPolicyCmptTypeAttribute attr : allAttributes) {
                    if (attr.isChangingOverTime() == changableAttributes && attr.isProductRelevant()) {
                        resultingAttributes.add(attr);
                    }
                }
                return resultingAttributes;
            } catch (CoreException e) {
                throw new CoreRuntimeException(e);
            }
        } else {
            return resultingAttributes;
        }
    }

    public boolean isConfigurationForPolicyCmptType() {
        return getType().isConfigurationForPolicyCmptType();
    }

    public String getPolicyInterfaceName() {
        return getPolicyName(BuilderAspect.INTERFACE);
    }

    public String getPolicyImplClassName() {
        return getPolicyName(BuilderAspect.IMPLEMENTATION);
    }

    protected String getPolicyName(BuilderAspect aspect) {
        XPolicyCmptClass xPolicyCmptClass = getPolicyCmptClass();
        return xPolicyCmptClass.getSimpleName(aspect);
    }

    public XPolicyCmptClass getPolicyCmptClass() {
        IPolicyCmptType policyCmptType;
        try {
            policyCmptType = getType().findPolicyCmptType(getIpsProject());
        } catch (CoreException e) {
            throw new CoreRuntimeException(e);
        }
        if (policyCmptType == null) {
            throw new NullPointerException("No policy found for " + getName());
        }
        XPolicyCmptClass xPolicyCmptClass = getModelNode(policyCmptType, XPolicyCmptClass.class);
        return xPolicyCmptClass;
    }

    public String getMethodNameCreatePolicyCmpt() {
        return "create" + getPolicyImplClassName();
    }

    @Override
    public abstract Set<? extends XProductClass> getClassHierarchy();

}