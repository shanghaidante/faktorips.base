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

package org.faktorips.devtools.stdbuilder;

import java.util.ArrayList;

import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.faktorips.devtools.core.AbstractIpsPluginTest;
import org.faktorips.devtools.core.IpsPlugin;
import org.faktorips.devtools.core.model.ipsproject.IIpsArtefactBuilderSetInfo;
import org.faktorips.devtools.core.model.ipsproject.IIpsBuilderSetPropertyDef;
import org.faktorips.devtools.core.model.ipsproject.IIpsLoggingFrameworkConnector;
import org.faktorips.devtools.core.model.ipsproject.IIpsObjectPath;
import org.faktorips.devtools.core.model.ipsproject.IIpsProject;
import org.faktorips.devtools.core.model.ipsproject.IIpsSrcFolderEntry;

public class StdBuilderSetTest extends AbstractIpsPluginTest {

    /*
     * #bug 1460
     */
    public void testBasePackageNamesWithUpperCaseLetters() throws CoreException {
        IIpsProject ipsProject = newIpsProject();
        IIpsObjectPath path = ipsProject.getIpsObjectPath();
        IIpsSrcFolderEntry entry = path.getSourceFolderEntries()[0];
        entry.setSpecificBasePackageNameForDerivedJavaClasses("org.faktorips.sample.Model");
        entry.setSpecificBasePackageNameForMergableJavaClasses("org.faktorips.sample.Model");
        ipsProject.setIpsObjectPath(path);
        ipsProject.getProject().build(IncrementalProjectBuilder.FULL_BUILD, null);
        newPolicyCmptType(ipsProject, "Policy");
        ipsProject.getProject().build(IncrementalProjectBuilder.INCREMENTAL_BUILD, null);
    }

    public void testStdBuilderSetPropertyDefinitions() throws CoreException {
        IIpsProject ipsProject = newIpsProject();
        IIpsArtefactBuilderSetInfo builderSetInfo = IpsPlugin.getDefault().getIpsModel().getIpsArtefactBuilderSetInfo(
                "org.faktorips.devtools.stdbuilder.ipsstdbuilderset");
        assertNotNull(builderSetInfo);
        IIpsBuilderSetPropertyDef[] propertyDefs = builderSetInfo.getPropertyDefinitions();
        assertEquals(11, propertyDefs.length);

        ArrayList<String> propertyDefNames = new ArrayList<String>();
        for (IIpsBuilderSetPropertyDef propertyDef : propertyDefs) {
            propertyDefNames.add(propertyDef.getName());
        }

        assertTrue(propertyDefNames.contains("generateChangeListener"));
        assertTrue(propertyDefNames.contains("useJavaEnumTypes"));
        assertTrue(propertyDefNames.contains("generatorLocale"));
        assertTrue(propertyDefNames.contains("useTypesafeCollections"));
        assertTrue(propertyDefNames.contains("generateDeltaSupport"));
        assertTrue(propertyDefNames.contains("generateCopySupport"));
        assertTrue(propertyDefNames.contains("generateVisitorSupport"));
        assertTrue(propertyDefNames.contains("loggingFrameworkConnector"));
        assertTrue(propertyDefNames.contains("generateJaxbSupport"));
        assertTrue(propertyDefNames.contains("persistenceProvider"));
        assertTrue(propertyDefNames.contains("formulaCompiling"));

        IIpsBuilderSetPropertyDef loggingConnectorPropertyDef = builderSetInfo
                .getPropertyDefinition("loggingFrameworkConnector");
        IIpsLoggingFrameworkConnector connector = (IIpsLoggingFrameworkConnector)loggingConnectorPropertyDef
                .parseValue(loggingConnectorPropertyDef.getDefaultValue(ipsProject));
        assertNull(connector);
    }

}
