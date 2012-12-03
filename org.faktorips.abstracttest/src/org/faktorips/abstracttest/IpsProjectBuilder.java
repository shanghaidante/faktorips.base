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

package org.faktorips.abstracttest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.faktorips.datatype.Datatype;
import org.faktorips.devtools.core.IpsPlugin;
import org.faktorips.devtools.core.internal.model.productcmpt.NoVersionIdProductCmptNamingStrategyFactory;
import org.faktorips.devtools.core.model.ipsproject.IIpsProject;
import org.faktorips.devtools.core.model.ipsproject.IIpsProjectProperties;
import org.faktorips.devtools.core.model.productcmpt.IProductCmptNamingStrategy;

/**
 * Allows easy creation of {@linkplain IIpsProject IPS projects} with different configurations.
 */
public class IpsProjectBuilder {

    private final List<String> predefinedDatatypes = new ArrayList<String>();

    private final List<Locale> supportedLocales = new ArrayList<Locale>();

    private String name;

    /**
     * Following methods can be overridden by subclasses of {@link AbstractIpsPluginTest}:
     * <ul>
     * <li>
     * {@link AbstractIpsPluginTest#setTestArtefactBuilderSet(IIpsProjectProperties, IIpsProject)}
     * <li>{@link AbstractIpsPluginTest#addIpsCapabilities(IProject)}
     * <li>{@link AbstractIpsPluginTest#addJavaCapabilities(IProject)}
     * </ul>
     * Therefore, we need to call these methods on the {@link AbstractIpsPluginTest} instance.
     */
    private final AbstractIpsPluginTest ipsPluginTest;

    public IpsProjectBuilder(AbstractIpsPluginTest ipsPluginTest) {
        this.ipsPluginTest = ipsPluginTest;

        // Default name
        this.name = UUID.randomUUID().toString();
    }

    public IpsProjectBuilder name(String name) {
        this.name = name;
        return this;
    }

    public IpsProjectBuilder predefinedDatatypes(Datatype... datatypes) {
        for (Datatype datatype : datatypes) {
            predefinedDatatypes.add(datatype.getName());
        }
        return this;
    }

    public IpsProjectBuilder predefinedDatatypes(List<Datatype> datatypes) {
        for (Datatype datatype : datatypes) {
            predefinedDatatypes.add(datatype.getName());
        }
        return this;
    }

    public IpsProjectBuilder supportedLocales(Locale... locales) {
        supportedLocales.addAll(Arrays.asList(locales));
        return this;
    }

    public IpsProjectBuilder supportedLocales(List<Locale> locales) {
        supportedLocales.addAll(locales);
        return this;
    }

    public IIpsProject build() throws CoreException {
        return newIpsProject();
    }

    private IIpsProject newIpsProject() throws CoreException {
        IWorkspaceRunnable runnable = new IWorkspaceRunnable() {
            @Override
            public void run(IProgressMonitor monitor) throws CoreException {
                IProject project = new PlatformProjectBuilder().name(name).build();
                ipsPluginTest.addJavaCapabilities(project);
                ipsPluginTest.addIpsCapabilities(project);
            }
        };
        IWorkspace workspace = ResourcesPlugin.getWorkspace();
        workspace.run(runnable, workspace.getRoot(), IWorkspace.AVOID_UPDATE, null);

        IIpsProject ipsProject = IpsPlugin.getDefault().getIpsModel().getIpsProject(name);
        IIpsProjectProperties properties = ipsProject.getProperties();

        setProductCmptNamingStrategy(ipsProject, properties);
        addSupportedLanguages(properties);
        addPredefinedDatatypes(properties);

        ipsProject.setProperties(properties);

        return ipsProject;
    }

    private void setProductCmptNamingStrategy(IIpsProject ipsProject, IIpsProjectProperties properties) {
        IProductCmptNamingStrategy productCmptNamingStrategy = new NoVersionIdProductCmptNamingStrategyFactory()
                .newProductCmptNamingStrategy(ipsProject);
        properties.setProductCmptNamingStrategy(productCmptNamingStrategy);
    }

    private void addSupportedLanguages(IIpsProjectProperties properties) {
        // Add supported languages
        if (supportedLocales.size() > 0) {
            for (Locale locale : supportedLocales) {
                properties.addSupportedLanguage(locale);
            }
            properties.setDefaultLanguage(supportedLocales.toArray(new Locale[supportedLocales.size()])[0]);
        }
    }

    private void addPredefinedDatatypes(IIpsProjectProperties properties) {
        // Add predefined datatypes
        if (predefinedDatatypes.size() > 0) {
            String[] projectDatatypes = properties.getPredefinedDatatypesUsed();
            String[] newProjectDatatypes = new String[projectDatatypes.length + predefinedDatatypes.size()];
            for (int i = 0, j = projectDatatypes.length; i < predefinedDatatypes.size(); i++, j++) {
                newProjectDatatypes[j] = predefinedDatatypes.get(i);
            }
            properties.setPredefinedDatatypesUsed(newProjectDatatypes);
        }
    }

}