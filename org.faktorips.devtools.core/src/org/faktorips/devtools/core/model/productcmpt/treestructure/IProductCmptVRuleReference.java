/*******************************************************************************
 * Copyright (c) 2005-2010 Faktor Zehn AG und andere.
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

package org.faktorips.devtools.core.model.productcmpt.treestructure;

import org.faktorips.devtools.core.model.productcmpt.IValidationRuleConfig;

/**
 * A Reference to an {@link IValidationRuleConfig} to be used in an
 * {@link IProductCmptTreeStructure}.
 * 
 * @author Stefan Widmaier, FaktorZehn AG
 */
public interface IProductCmptVRuleReference extends IProductCmptStructureReference {
    /**
     * @return the referenced {@link IValidationRuleConfig}.
     */
    public IValidationRuleConfig getValidationRuleConfig();
}