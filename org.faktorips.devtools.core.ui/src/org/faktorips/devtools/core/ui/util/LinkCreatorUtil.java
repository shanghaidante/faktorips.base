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

package org.faktorips.devtools.core.ui.util;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.window.Window;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.dialogs.SelectionDialog;
import org.faktorips.devtools.core.IpsPlugin;
import org.faktorips.devtools.core.model.ipsobject.IIpsSrcFile;
import org.faktorips.devtools.core.model.ipsproject.IIpsProject;
import org.faktorips.devtools.core.model.productcmpt.IProductCmpt;
import org.faktorips.devtools.core.model.productcmpt.IProductCmptGeneration;
import org.faktorips.devtools.core.model.productcmpt.IProductCmptLink;
import org.faktorips.devtools.core.model.productcmpt.treestructure.IProductCmptReference;
import org.faktorips.devtools.core.model.productcmpt.treestructure.IProductCmptTypeRelationReference;
import org.faktorips.devtools.core.model.productcmpttype.IProductCmptType;
import org.faktorips.devtools.core.model.type.IAssociation;
import org.faktorips.devtools.core.ui.Messages;
import org.faktorips.devtools.core.ui.views.productstructureexplorer.AssociationSelectionDialog;

/**
 * This class provides several ways to create a links in a product component.
 * 
 * @author dirmeier
 */
public class LinkCreatorUtil {

    private boolean autoSave;

    public LinkCreatorUtil(boolean autoSave) {
        this.autoSave = autoSave;
    }

    public boolean canCreateLinks(Object target, List<IProductCmpt> draggedCmpts) throws CoreException {
        if (target instanceof IProductCmptReference) {
            // product cmpt reference in product structure view
            IProductCmptReference reference = (IProductCmptReference)target;
            return processProductCmptReference(draggedCmpts, reference, false);
        } else if (target instanceof IProductCmptTypeRelationReference) {
            IProductCmptTypeRelationReference reference = (IProductCmptTypeRelationReference)target;
            return processAssociationReference(draggedCmpts, reference, false);
        } else if (target instanceof IProductCmptLink) {
            IProductCmptLink link = ((IProductCmptLink)target);
            return processProductCmptLink(draggedCmpts, link, false);
        } else {
            return false;
        }
    }

    public boolean createLinks(List<IProductCmpt> droppedCmpts, Object target) {
        boolean haveToSave = autoSave;
        try {
            boolean result;
            IIpsSrcFile ipsSrcFile;
            if (target instanceof IProductCmptReference) {
                IProductCmptReference cmptReference = (IProductCmptReference)target;
                ipsSrcFile = cmptReference.getWrappedIpsObject().getIpsSrcFile();
                haveToSave &= !ipsSrcFile.isDirty();
                result = processProductCmptReference(droppedCmpts, cmptReference, true);
            } else if (target instanceof IProductCmptTypeRelationReference) {
                IProductCmptTypeRelationReference relationReference = (IProductCmptTypeRelationReference)target;
                ipsSrcFile = relationReference.getParent().getWrappedIpsObject().getIpsSrcFile();
                haveToSave &= !ipsSrcFile.isDirty();
                result = processAssociationReference(droppedCmpts, relationReference, true);
            } else if (target instanceof IProductCmptLink) {
                IProductCmptLink cmptLink = (IProductCmptLink)target;
                ipsSrcFile = cmptLink.getIpsObject().getIpsSrcFile();
                haveToSave &= !ipsSrcFile.isDirty();
                result = processProductCmptLink(droppedCmpts, cmptLink, true);
            } else {
                return false;
            }
            if (result && haveToSave) {
                ipsSrcFile.save(false, null);
            }
            return result;
        } catch (CoreException e) {
            IpsPlugin.log(e);
            return false;
        }
    }

    protected boolean processProductCmptReference(List<IProductCmpt> draggedCmpts,
            IProductCmptReference reference,
            boolean createLinks) throws CoreException {

        IIpsProject ipsProject = reference.getProductCmpt().getIpsProject();
        IProductCmptGeneration generation = (IProductCmptGeneration)reference.getProductCmpt()
                .findGenerationEffectiveOn(reference.getStructure().getValidAt());
        IProductCmptType cmptType = reference.getProductCmpt().findProductCmptType(ipsProject);
        if (generation == null || cmptType == null) {
            return false;
        }
        List<IAssociation> associations = cmptType.findAllNotDerivedAssociations();
        // should only return true if all dragged cmpts are valid
        boolean result = false;
        for (IProductCmpt draggedCmpt : draggedCmpts) {
            List<IAssociation> possibleAssos = new ArrayList<IAssociation>();
            for (IAssociation aAssoziation : associations) {
                if (generation.canCreateValidLink(draggedCmpt, aAssoziation, generation.getIpsProject())) {
                    possibleAssos.add(aAssoziation);
                }
            }
            if (possibleAssos.size() > 0) {
                result = true;
            } else if (!createLinks) {
                return false;
            }
            if (createLinks) {
                if (possibleAssos.size() == 1) {
                    IAssociation association = possibleAssos.get(0);
                    createLink(draggedCmpt.getQualifiedName(), generation, association);
                } else if (possibleAssos.size() > 1) {
                    Object[] selectedAssociations = selectAssociation(draggedCmpt.getQualifiedName(), possibleAssos);
                    if (selectedAssociations != null) {
                        for (Object object : selectedAssociations) {
                            if (object instanceof IAssociation) {
                                IAssociation association = (IAssociation)object;
                                createLink(draggedCmpt.getQualifiedName(), generation, association);
                            }
                        }
                    }
                }
            }
        }
        return result;
    }

    /**
     * Set to protected to override in test class
     * 
     * @param droppedCmptName
     * @param possibleAssos
     * @return
     */
    protected Object[] selectAssociation(String droppedCmptName, List<IAssociation> possibleAssos) {
        Shell shell = Display.getDefault().getActiveShell();
        if (shell == null) {
            shell = new Shell(Display.getDefault());
        }
        SelectionDialog dialog = new AssociationSelectionDialog(shell, possibleAssos, NLS.bind(
                Messages.LinkDropListener_selectAssociation, droppedCmptName));
        dialog.setBlockOnOpen(true);
        dialog.setHelpAvailable(false);
        if (dialog.open() == Window.OK) {
            if (dialog.getResult().length > 0) {
                return dialog.getResult();
            }
        }
        return null;
    }

    protected boolean processAssociationReference(List<IProductCmpt> draggedCmpts,
            IProductCmptTypeRelationReference reference,
            boolean createLink) throws CoreException {
        IAssociation association;
        IProductCmptGeneration generation;
        IProductCmpt parentCmpt = ((IProductCmptReference)reference.getParent()).getProductCmpt();
        generation = (IProductCmptGeneration)parentCmpt
                .findGenerationEffectiveOn(reference.getStructure().getValidAt());
        association = reference.getRelation();
        // should only return true if all dragged cmpts are valid
        boolean result = false;
        for (IProductCmpt draggedCmpt : draggedCmpts) {
            if (generation != null
                    && generation.canCreateValidLink(draggedCmpt, association, generation.getIpsProject())) {
                result = true;
                if (createLink) {
                    createLink(draggedCmpt.getQualifiedName(), generation, association);
                }
            } else {
                return false;
            }
        }
        return result;
    }

    protected boolean processProductCmptLink(List<IProductCmpt> draggedCmpts, IProductCmptLink link, boolean createLink)
            throws CoreException {
        IAssociation association;
        IProductCmptGeneration generation = link.getProductCmptGeneration();
        association = link.findAssociation(generation.getIpsProject());
        // should only return true if all dragged cmpts are valid
        boolean result = false;
        for (IProductCmpt draggedCmpt : draggedCmpts) {
            if (generation != null
                    && generation.canCreateValidLink(draggedCmpt, association, generation.getIpsProject())) {
                result = true;
                if (createLink) {
                    createLink(draggedCmpt.getQualifiedName(), generation, association);
                }
            } else {
                return false;
            }
        }
        return result;
    }

    private IProductCmptLink createLink(String droppedCmptQName,
            IProductCmptGeneration generation,
            IAssociation association) {
        if (generation != null && association != null && generation.getIpsSrcFile().isMutable()) {
            IProductCmptLink newLink = null;

            // TODO handle location

            // if (insertBefore != null) {
            // newLink = generation.newLink(association.getName(), insertBefore);
            // } else {
            newLink = generation.newLink(association.getName());
            // }
            newLink.setTarget(droppedCmptQName);
            newLink.setMaxCardinality(1);
            newLink.setMinCardinality(0);
            return newLink;
        } else {
            return null;
        }
    }

    /**
     * @param autoSave The autoSave to set.
     */
    public void setAutoSave(boolean autoSave) {
        this.autoSave = autoSave;
    }

    /**
     * @return Returns the autoSave.
     */
    public boolean isAutoSave() {
        return autoSave;
    }

}
