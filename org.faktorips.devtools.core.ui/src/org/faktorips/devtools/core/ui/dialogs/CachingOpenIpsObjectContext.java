/*******************************************************************************
 * Copyright (c) Faktor Zehn AG. <http://www.faktorzehn.org>
 * 
 * This source code is available under the terms of the AGPL Affero General Public License version 3.
 * 
 * Please see LICENSE.txt for full license terms, including the additional permissions and the
 * possibility of alternative license terms.
 *******************************************************************************/

package org.faktorips.devtools.core.ui.dialogs;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.faktorips.devtools.core.model.ipsobject.IIpsSrcFile;

/**
 * This implementation of {@link ISelectIpsObjectContext} supports a lazy loading mechanism getting
 * the list of source files only on first access and caching the result. This should be the
 * preferred way for {@link ISelectIpsObjectContext} because loading the files before opening the
 * dialog would not gives enough response to the user.
 * 
 * @author dirmeier
 */
public abstract class CachingOpenIpsObjectContext implements ISelectIpsObjectContext {

    // volatile for double checking ideom
    public volatile List<IIpsSrcFile> srcFiles;

    @Override
    public final List<IIpsSrcFile> getIpsSrcFiles(IProgressMonitor progressMonitor) throws CoreException {
        if (srcFiles == null) {
            synchronized (this) {
                if (srcFiles == null) {
                    srcFiles = new ArrayList<IIpsSrcFile>();
                    srcFiles = loadIpsSrcFiles(progressMonitor);
                }
            }
        }
        return srcFiles;
    }

    /**
     * This method is only called once for this context when loading the list of {@link IIpsSrcFile}
     * s the first time. This method may be performance intensive.
     * 
     * @param progressMonitor The {@link IProgressMonitor} showing the state of loading the source
     *            files
     * @return the list of {@link IIpsSrcFile} to show in the select dialog
     */
    public abstract List<IIpsSrcFile> loadIpsSrcFiles(IProgressMonitor progressMonitor) throws CoreException;

}
