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

package org.faktorips.devtools.core.ui.views.productdefinitionexplorer;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.faktorips.devtools.core.model.IIpsObject;
import org.faktorips.devtools.core.model.IIpsProject;

/**
 * ViewerFilter for <code>ProductExplorer<code> viewpart. It is used to
 * filter out all projects that are not productdefinition projects.
 * 
 * @author Stefan Widmaier
 */
public class ProductExplorerFilter extends ViewerFilter {
	
	public ProductExplorerFilter(){}
	
	/**
	 * Returns <code>true</code> for all <code>IIpsElement</code>s except 
	 * <code>IIpsProject</code>s that do not have the isProductdefinitionProject
	 *  flag set to true. 
     *  <p>
     *  Returns false if the element is an <code>IFile</code> with name ".ipsproject".
     *  <p>
     *  True is returned for all other types.
	 */
	public boolean select(Viewer viewer, Object parentElement, Object element) {
		if(element instanceof IIpsProject){
			return ((IIpsProject)element).isProductDefinitionProject();
		}
        if(element instanceof IFile){
            // filter out rest of hidden files (e.g. ".ipsproject")
            if(((IFile)element).getName().indexOf(".")==0){ //$NON-NLS-1$
                return false;
            }
        }
        if (element instanceof IIpsObject){
            return ((IIpsObject)element).getIpsObjectType().isProductDefinitionType();
        }
		return true;
	}
}
