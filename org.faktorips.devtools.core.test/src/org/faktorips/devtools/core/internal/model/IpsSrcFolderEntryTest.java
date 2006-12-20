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

package org.faktorips.devtools.core.internal.model;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.faktorips.devtools.core.AbstractIpsPluginTest;
import org.faktorips.devtools.core.model.IIpsObjectPath;
import org.faktorips.devtools.core.model.IIpsProject;
import org.faktorips.devtools.core.model.IIpsProjectProperties;
import org.faktorips.devtools.core.model.IIpsSrcFolderEntry;
import org.faktorips.util.message.MessageList;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * 
 * @author Jan Ortmann
 */
public class IpsSrcFolderEntryTest extends AbstractIpsPluginTest {

    private IIpsProject ipsProject;
    private IpsObjectPath path;
    
    protected void setUp() throws Exception {
        super.setUp();
        ipsProject = this.newIpsProject("TestProject");
        path = new IpsObjectPath();
    }
    
    public void testGetOutputFolderForGeneratedJavaFiles() {
        IFolder src = ipsProject.getProject().getFolder("src");
        IFolder out1 = ipsProject.getProject().getFolder("out1");
        IFolder out2 = ipsProject.getProject().getFolder("out2");
        path.setOutputFolderForGeneratedJavaFiles(out1);
        IIpsSrcFolderEntry entry = path.newSourceFolderEntry(src);
        entry.setSpecificOutputFolderForMergableJavaFiles(out2);

        path.setOutputDefinedPerSrcFolder(false);
        assertEquals(out1, entry.getOutputFolderForMergableJavaFiles());

        path.setOutputDefinedPerSrcFolder(true);
        assertEquals(out2, entry.getOutputFolderForMergableJavaFiles());
    }

    public void testGetBasePackageNameForGeneratedJavaClasses() {
        IFolder src = ipsProject.getProject().getFolder("src");
        path.setBasePackageNameForMergableJavaClasses("pack1");
        IIpsSrcFolderEntry entry = path.newSourceFolderEntry(src);
        entry.setSpecificBasePackageNameForMergableJavaClasses("pack2");

        path.setOutputDefinedPerSrcFolder(false);
        assertEquals("pack1", entry.getBasePackageNameForMergableJavaClasses());

        path.setOutputDefinedPerSrcFolder(true);
        assertEquals("pack2", entry.getBasePackageNameForMergableJavaClasses());
    }
    
    public void testGetOutputFolderForExtensionJavaFiles() {
        IFolder src = ipsProject.getProject().getFolder("src");
        IFolder out1 = ipsProject.getProject().getFolder("out1");
        IFolder out2 = ipsProject.getProject().getFolder("out2");
        path.setOutputFolderForDerivedSources(out1);
        IIpsSrcFolderEntry entry = path.newSourceFolderEntry(src);
        entry.setSpecificOutputFolderForDerivedJavaFiles(out2);

        path.setOutputDefinedPerSrcFolder(false);
        assertEquals(out1, entry.getOutputFolderForDerivedJavaFiles());

        path.setOutputDefinedPerSrcFolder(true);
        assertEquals(out2, entry.getOutputFolderForDerivedJavaFiles());
    }

    public void testGetBasePackageNameForExtensionJavaClasses() {
        IFolder src = ipsProject.getProject().getFolder("src");
        path.setBasePackageNameForDerivedJavaClasses("pack1");
        IIpsSrcFolderEntry entry = path.newSourceFolderEntry(src);
        entry.setSpecificBasePackageNameForDerivedJavaClasses("pack2");

        path.setOutputDefinedPerSrcFolder(false);
        assertEquals("pack1", entry.getBasePackageNameForDerivedJavaClasses());

        path.setOutputDefinedPerSrcFolder(true);
        assertEquals("pack2", entry.getBasePackageNameForDerivedJavaClasses());
    }
    
    public void testInitFromXml() {
        IProject project = ipsProject.getProject();
        IpsSrcFolderEntry entry = new IpsSrcFolderEntry(path);
        Document doc = getTestDocument();
        NodeList nl = doc.getDocumentElement().getElementsByTagName("Entry");
        
        entry.initFromXml((Element)nl.item(0), ipsProject.getProject());
        assertEquals(project.getFolder("ipssrc"), entry.getSourceFolder());
        assertEquals(project.getFolder("generated"), entry.getSpecificOutputFolderForMergableJavaFiles());
        assertEquals("org.sample.generated", entry.getSpecificBasePackageNameForMergableJavaClasses());
        assertEquals(project.getFolder("extensions"), entry.getSpecificOutputFolderForDerivedJavaFiles());
        assertEquals("org.sample.extensions", entry.getSpecificBasePackageNameForDerivedJavaClasses());
        assertEquals("motor.repository-toc.xml", entry.getBasePackageRelativeTocPath());

        entry.initFromXml((Element)nl.item(1), ipsProject.getProject());
        assertNull(entry.getSpecificOutputFolderForMergableJavaFiles());
        assertEquals("", entry.getSpecificBasePackageNameForMergableJavaClasses());
        assertEquals("", entry.getSpecificBasePackageNameForDerivedJavaClasses());
    }

    public void testToXml() {
        IProject project = ipsProject.getProject();
        IpsSrcFolderEntry entry = new IpsSrcFolderEntry(path, project.getFolder("ipssrc").getFolder("modelclasses"));
        entry.setSpecificOutputFolderForMergableJavaFiles(project.getFolder("javasrc").getFolder("modelclasses"));
        entry.setSpecificBasePackageNameForMergableJavaClasses("org.faktorips.sample.model");
        entry.setBasePackageRelativeTocPath("toc.xml");
        Element element = entry.toXml(newDocument());
        entry = new IpsSrcFolderEntry(path);
        entry.initFromXml(element, project);
        assertEquals("toc.xml", entry.getBasePackageRelativeTocPath());
        assertEquals(project.getFolder("ipssrc").getFolder("modelclasses"), entry.getSourceFolder());
        assertEquals(project.getFolder("javasrc").getFolder("modelclasses"), entry.getSpecificOutputFolderForMergableJavaFiles());
        assertEquals("org.faktorips.sample.model", entry.getSpecificBasePackageNameForMergableJavaClasses());
        
        // null, default values for new entries
        entry = new IpsSrcFolderEntry(path, project.getFolder("ipssrc").getFolder("modelclasses"));
        element = entry.toXml(newDocument());
        entry = new IpsSrcFolderEntry(path);
        entry.initFromXml(element, project);
        assertNull(entry.getSpecificOutputFolderForMergableJavaFiles());
        assertNull(entry.getSpecificOutputFolderForDerivedJavaFiles());
        assertEquals("", entry.getSpecificBasePackageNameForMergableJavaClasses());
        assertEquals("", entry.getSpecificBasePackageNameForDerivedJavaClasses());
    }
    
    public void testValidate() throws CoreException{
        MessageList ml = ipsProject.validate();
        assertEquals(0, ml.getNoOfMessages());

        IIpsProjectProperties props = ipsProject.getProperties();
        IIpsObjectPath path = props.getIpsObjectPath();
        IIpsSrcFolderEntry[] srcEntries = path.getSourceFolderEntries();
        assertEquals(1, srcEntries.length);

        // validate missing outputFolderGenerated
        IFolder folder1 = ipsProject.getProject().getFolder("none");
        srcEntries[0].setSpecificOutputFolderForMergableJavaFiles(folder1);
        ipsProject.setProperties(props);
        ml = ipsProject.validate();
        assertEquals(1, ml.getNoOfMessages());
        assertNotNull(ml.getMessageByCode(IIpsSrcFolderEntry.MSGCODE_MISSING_FOLDER));

        // validate missing outputFolderExtension
        srcEntries[0].setSpecificOutputFolderForDerivedJavaFiles(folder1);
        ipsProject.setProperties(props);
        ml = ipsProject.validate();
        assertEquals(2, ml.getNoOfMessages());

        // validate missing source folder
        path.newSourceFolderEntry(folder1);
        ipsProject.setProperties(props);
        ml = ipsProject.validate();
        assertEquals(5, ml.getNoOfMessages());
    }
}
