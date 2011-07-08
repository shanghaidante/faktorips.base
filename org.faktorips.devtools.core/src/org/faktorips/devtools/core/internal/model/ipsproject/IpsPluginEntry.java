/*******************************************************************************
 * Copyright (c) 2005-2011 Faktor Zehn AG und andere.
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

package org.faktorips.devtools.core.internal.model.ipsproject;

import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.faktorips.devtools.core.model.ipsproject.IIpsObjectPathEntry;
import org.faktorips.util.message.MessageList;

/**
 * Entry to reference an osgi bundel / PlugIn directly in the ips object path.
 * 
 * !!! EXPERIMENTAL !!!
 * 
 * THE CODE IS COMPLETELY OUTCOMMENTED TO AVOID DEPENDENIES TO PDE
 * 
 * @author Jan Ortmann
 */
public class IpsPluginEntry extends IpsContainerEntry {

    // private IPluginModelBase pluginModel = null;

    public IpsPluginEntry(IpsObjectPath path) {
        super(path);
        // pluginModel = PluginRegistry.findModel(path.getIpsProject().getProject());
    }

    @Override
    public String getType() {
        return TYPE_PLUGIN;
    }

    @Override
    public List<IIpsObjectPathEntry> resolveEntries() throws CoreException {
        return null;
        // IpsObjectPath ipsObjectPath = (IpsObjectPath)getIpsObjectPath();
        // List<IIpsObjectPathEntry> resolvedEntries = new ArrayList<IIpsObjectPathEntry>();
        // if (pluginModel == null) {
        // return resolvedEntries;
        // }
        // BundleDescription bundleDesc = pluginModel.getBundleDescription();
        // BundleSpecification[] requires = bundleDesc.getRequiredBundles();
        // for (int i = 0; i < requires.length; i++) {
        // BaseDescription baseDesc = requires[i].getSupplier();
        // BundleDescription requiredBundleDesc = baseDesc.getSupplier();
        // IPluginModelBase requiredBundleModel = PluginRegistry.findModel(requiredBundleDesc);
        //
        // IResource resource = requiredBundleModel.getUnderlyingResource();
        // if (resource != null) {
        // IIpsProject ipsProject =
        // IpsPlugin.getDefault().getIpsModel().getIpsProject(resource.getProject());
        // if (ipsProject.exists()) {
        // resolvedEntries.add(new IpsProjectRefEntry(ipsObjectPath, ipsProject));
        // }
        // continue;
        // }
        // Bundle requiredBundle = Platform.getBundle(requiredBundleDesc.getSymbolicName());
        // if (requiredBundle != null) {
        //                URL entryURL = requiredBundle.getEntry("ipsobjects/ipsobjects.properties"); //$NON-NLS-1$
        // if (entryURL == null) {
        // continue;
        // }
        // String location = requiredBundle.getLocation();
        //                if (location.startsWith("reference:file:/")) { //$NON-NLS-1$
        //                    location = location.substring("reference:file:/".length()); //$NON-NLS-1$
        // }
        // IpsArchiveEntry archiveEntry = new IpsArchiveEntry(ipsObjectPath);
        // archiveEntry.setArchivePath(getIpsProject(), Path.fromOSString(location));
        // resolvedEntries.add(archiveEntry);
        //
        // }
        // }
        // return resolvedEntries;
    }

    @Override
    public MessageList validate() throws CoreException {
        return null;
        // MessageList list = new MessageList();
        // if (pluginModel == null) {
        //            Message msg = Message.newError("NOT_A_PLUGIN", "The project " + this.getIpsProject().getName() //$NON-NLS-1$ //$NON-NLS-2$
        //                    + " is not a PlugIn-Project!"); //$NON-NLS-1$
        // list.add(msg);
        // }
        // return list;
    }
}