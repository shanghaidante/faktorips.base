/*******************************************************************************
 * Copyright (c) 2005-2010 Faktor Zehn AG und andere.
 * 
 * Alle Rechte vorbehalten.
 * 
 * Dieses Programm und alle mitgelieferten Sachen (Dokumentationen, Beispiele, Konfigurationen,
 * etc.) duerfen nur unter den Bedingungen der Faktor-Zehn-Community Lizenzvereinbarung - Version
 * 0.1 (vor Gruendung Community) genutzt werden, die Bestandteil der Auslieferung ist und auch unter
 * http://www.faktorzehn.org/fips:lizenz eingesehen werden kann.
 * 
 * Mitwirkende: Faktor Zehn AG - initial API and implementation - http://www.faktorzehn.de
 *******************************************************************************/

package org.faktorips.devtools.core.builder;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IImportContainer;
import org.eclipse.jdt.core.IImportDeclaration;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.JavaModelException;
import org.faktorips.codegen.ImportDeclaration;
import org.faktorips.devtools.core.model.ipsobject.IIpsSrcFile;
import org.faktorips.util.StringUtil;

/**
 * The intention of this class is to use it as a base class for generators that are created by JET.
 * Therefore the JET-Skeleton class needs to extend from this class. Furthermore the JET-Skeleton's
 * generate() method must exactly match the signature of this class's generate() method. Class
 * declarations within JET-template files need to be done by means of the appendXXX-methods of this
 * class. This assures that all import declarations will be collected. To be able to add the imports
 * at the right position within the code the markImportLocation() method has to be used. See the
 * method description to used it correctly. At the end of a JET-template the addImports() method
 * needs to be called to actually add the import declarations to the source content.
 * 
 * @author Peter Erzberger
 */
public abstract class JetJavaContentGenerator {

    private ImportDeclaration importDeclartion = new ImportDeclaration();
    private int importLocation = 0;
    private JavaSourceFileBuilder builder;

    /**
     * Import container of the compilation unit if it already exists and this is a re-generation,
     * null otherwise.
     */
    private IImportContainer importContainer;

    /**
     * This method is supposed to be generated by JET. Therefore the JET-Skeleton must provide the
     * signature of this method.
     * 
     * @param ipsSrcFile the IpsObject that is provided to this method by the
     *            JetJavaSoureFileBuilder during the build routine
     * 
     * @return the generated java source content
     * 
     * @throws CoreException rising checked exceptions can be wrapped into a CoreException
     */
    public abstract String generate(IIpsSrcFile ipsSrcFile) throws CoreException;

    /**
     * Creates a new JetJavaContentGenerator.
     */
    public JetJavaContentGenerator() {
        super();
    }

    /**
     * @param importContainer The importContainer to set.
     */
    public void setImportContainer(IImportContainer importContainer) {
        this.importContainer = importContainer;
    }

    /**
     * To be able to collect the imports of the generated java source content this method has to be
     * used within a JET-Template file to add a class declaration.
     * 
     * @param fullQualifiedName the qualified class name as a string
     * 
     * @return the unqualified class name
     */
    public String appendClassName(String fullQualifiedName) {
        importDeclartion.add(fullQualifiedName);
        return StringUtil.unqualifiedName(fullQualifiedName);
    }

    /**
     * To be able to collect the imports of the generated java source content this method has to be
     * used within a JET-Template file to add a class declaration.
     * 
     * @param clazz used to determine the qualified class name of this class
     * 
     * @return the unqualified class name of the provided class
     */
    public String appendClass(Class<?> clazz) {
        importDeclartion.add(clazz);
        return StringUtil.unqualifiedName(clazz.getName());
    }

    /**
     * Needs to be declared with in the JET-Template after the package and before the class
     * declaration. By means of this method the JetJavaContentGenerator is able to insert the import
     * declarations at the right point within the java source.
     * 
     * @param buffer the string buffer variable that can be accessed within JET templates
     */
    public void markImportLocation(StringBuffer buffer) {
        importLocation = buffer.length() == 0 ? 0 : buffer.length();
    }

    /**
     * Needs to be added at the last line of a JET template. It adds the collected import
     * declarations to the java source content at the marked position.
     * 
     * @param buffer the string buffer variable that can be accessed within JET templates
     */
    public void addImports(StringBuffer buffer) throws CoreException {
        StringBuffer importBuffer = new StringBuffer();
        String pack = getJavaSourceFileBuilder().getPackage();
        ImportDeclaration decl = new ImportDeclaration(importDeclartion, pack);
        if (importContainer != null && importContainer.exists()) {
            importBuffer.append(importContainer.getSource());
            decl = getNewImports(decl);
        }
        importBuffer.append(decl.toString());
        buffer.insert(importLocation, importBuffer.toString());
    }

    private ImportDeclaration getNewImports(ImportDeclaration decl) throws JavaModelException {
        if (decl.getNoOfImports() == 0) {
            return decl;
        }
        ImportDeclaration existingImports = new ImportDeclaration();
        IJavaElement[] imports = importContainer.getChildren();
        for (IJavaElement import1 : imports) {
            String imp = ((IImportDeclaration)import1).getSource(); // example for imp: import
            // java.util.Date;
            existingImports.add(imp.substring(7, imp.length() - 1));
        }
        return existingImports.getUncoveredImports(decl);
    }

    /**
     * Returns the localized text for the provided key. Calling this method is only allowed during
     * the build cycle. If it is called outside the build cycle a RuntimeException is thrown.
     * 
     * @param key the key that identifies the requested text
     */
    public String getLocalizedText(IIpsSrcFile file, String key) {
        return getJavaSourceFileBuilder().getLocalizedText(file, key);
    }

    /**
     * Returns the localized text for the provided key. Calling this method is only allowed during
     * the build cycle. If it is called outside the build cycle a RuntimeException is thrown.
     * 
     * @param key the key that identifies the requested text
     * @param replacement an indicated region within the text is replaced by the string
     *            representation of this value
     */
    public String getLocalizedText(IIpsSrcFile file, String key, Object replacement) {
        return getJavaSourceFileBuilder().getLocalizedText(file, key, replacement);
    }

    /**
     * After this content generator has been instantiated the JetJavaSourceFileBuilder added a
     * reference to itself by means of this method.
     */
    public void setJavaSourceFileBuilder(JavaSourceFileBuilder builder) {
        this.builder = builder;
    }

    /**
     * Returns the JetJavaSourceFileBuilder by which this JetJavaContentGenerator has been created.
     */
    public JavaSourceFileBuilder getJavaSourceFileBuilder() {
        return builder;
    }

    /**
     * The IJavaPackageStructure of the JetJavaSourceFileBuilder can be accessed via this method.
     */
    public IJavaPackageStructure getPackageStructure() {
        return builder.getPackageStructure();
    }

}