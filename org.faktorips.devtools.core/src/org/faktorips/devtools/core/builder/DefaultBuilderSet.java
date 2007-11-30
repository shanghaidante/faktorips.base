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

package org.faktorips.devtools.core.builder;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.faktorips.codegen.DatatypeHelper;
import org.faktorips.devtools.core.internal.model.TableContentsEnumDatatypeAdapter;
import org.faktorips.devtools.core.model.ipsobject.IIpsSrcFile;
import org.faktorips.devtools.core.model.ipsobject.IpsObjectType;
import org.faktorips.devtools.core.model.ipsproject.IIpsArtefactBuilder;
import org.faktorips.devtools.core.model.ipsproject.IIpsArtefactBuilderSetConfig;
import org.faktorips.devtools.core.model.ipsproject.IIpsPackageFragmentRoot;
import org.faktorips.devtools.core.model.ipsproject.IIpsSrcFolderEntry;
import org.faktorips.devtools.core.model.productcmpt.IFormula;
import org.faktorips.devtools.core.model.tablecontents.ITableContents;
import org.faktorips.devtools.core.model.tablestructure.ITableAccessFunction;
import org.faktorips.devtools.core.util.QNameUtil;
import org.faktorips.fl.CompilationResult;
import org.faktorips.fl.IdentifierResolver;

/**
 * A default implementation that extends the AbstractBuilderSet and implements the IJavaPackageStructure
 * interface. The getPackage() method provides package names for the kind constants defined in this
 * DefaultBuilderSet. This implementation uses the base package name for generated java classes as
 * the root of the package structure. The base package name can be configure for an ips project
 * within the ipsproject.xml file. On top of the base package name it adds the ips package fragment
 * name of the IpsSrcFile in question. Internal packages are distinguished from packages that
 * contain published interfaces and classes. It depends on the kind constant if an internal or
 * published package name is returned.
 * 
 * @author Peter Erzberger
 */
public class DefaultBuilderSet extends AbstractBuilderSet {

    // kind constants. These constants are not supposed to be used within JavaSourceFileBuilder
    // implementations. Since the JavaSourceFileBuilder implementations might get used in other
    // artefact builder sets using these constants would introduce a dependency to this builder set.
    // however the constants are public for use in test cases
    public final static String KIND_PRODUCT_CMPT_INTERFACE = "productcmptinterface"; //$NON-NLS-1$
    public final static String KIND_PRODUCT_CMPT_IMPL = "productcmptimplementation"; //$NON-NLS-1$
    public final static String KIND_PRODUCT_CMPT_GENERATION_INTERFACE = "productCmptGenerationInterface"; //$NON-NLS-1$
    public final static String KIND_PRODUCT_CMPT_GENERATION_IMPL = "productCmptGenerationImpl"; //$NON-NLS-1$
    public final static String KIND_PRODUCT_CMPT_CONTENT = "productcmptcontent"; //$NON-NLS-1$
    public final static String KIND_POLICY_CMPT_INTERFACE = "policycmptinterface"; //$NON-NLS-1$
    public final static String KIND_POLICY_CMPT_IMPL = "policycmptimpl"; //$NON-NLS-1$
    public final static String KIND_TABLE_IMPL = "tableimpl"; //$NON-NLS-1$
    public final static String KIND_TABLE_CONTENT = "tablecontent"; //$NON-NLS-1$
    public final static String KIND_TABLE_ROW = "tablerow"; //$NON-NLS-1$
    public final static String KIND_TEST_CASE_TYPE_CLASS = "testcasetypeclass"; //$NON-NLS-1$
    public final static String KIND_TEST_CASE_XML = "testcasexml"; //$NON-NLS-1$
    public final static String KIND_FORMULA_TEST_CASE = "formulatestcase"; //$NON-NLS-1$
    
    public final static String KIND_TABLE_TOCENTRY = "tabletocentry"; //$NON-NLS-1$
    public final static String KIND_PRODUCT_CMPT_TOCENTRY = "productcmpttocentry"; //$NON-NLS-1$
    
    private final static String INTERNAL_PACKAGE = "internal"; //$NON-NLS-1$
    
    /**
     * Returns the addition of the name of the ips package fragment that contains the provided
     * IpsSrcFile and the base package name. This method is used within the getPackage() method
     * implementation.
     */
    protected String getPackageName(IIpsSrcFile ipsSrcFile) throws CoreException {
        StringBuffer buf = new StringBuffer();
        String basePackeName = ipsSrcFile.getBasePackageNameForGeneratedJavaClass();
        if (!StringUtils.isEmpty(basePackeName)) {
            buf.append(basePackeName);
        }
        String packageFragName = ipsSrcFile.getIpsPackageFragment().getName();
        if (!StringUtils.isEmpty(packageFragName)) {
            buf.append('.').append(packageFragName);
        }
        return buf.toString().toLowerCase();
    }

    /**
     * Returns ips package fragment + ".internal." + base package name. This method is used within
     * the getPackage() method implementation.
     */
    protected String getInternalPackageName(IIpsSrcFile ipsSrcFile) throws CoreException {
        StringBuffer buf = new StringBuffer();
        String basePackeName = ipsSrcFile.getBasePackageNameForGeneratedJavaClass();
        if (!StringUtils.isEmpty(basePackeName)) {
            buf.append(basePackeName).append('.');
        }
        buf.append(INTERNAL_PACKAGE);
        String packageFragName = ipsSrcFile.getIpsPackageFragment().getName();
        if (!StringUtils.isEmpty(packageFragName)) {
            buf.append('.').append(packageFragName);
        }

        return buf.toString().toLowerCase();
    }
    
    /**
     * {@inheritDoc}
     */
    public String getTocFilePackageName(IIpsPackageFragmentRoot root) throws CoreException {
        StringBuffer buf = new StringBuffer();
        IIpsSrcFolderEntry entry = (IIpsSrcFolderEntry)root.getIpsObjectPathEntry();
        String basePackeName = entry.getBasePackageNameForDerivedJavaClasses();
        if (!StringUtils.isEmpty(basePackeName)) {
            buf.append(basePackeName).append('.');
        }
        buf.append(INTERNAL_PACKAGE);
        return buf.toString().toLowerCase();
    }
    
    /**
	 * {@inheritDoc}
	 */
	public IFile getRuntimeRepositoryTocFile(IIpsPackageFragmentRoot root) throws CoreException {
	    if (root==null) {
	        return null;   
        }
        if (!root.isBasedOnSourceFolder()) {
            return null;
        }
        IIpsSrcFolderEntry entry = (IIpsSrcFolderEntry)root.getIpsObjectPathEntry();
        String basePack = entry.getBasePackageNameForDerivedJavaClasses();
        String basePackInternal = QNameUtil.concat(basePack, INTERNAL_PACKAGE);
        IPath path = QNameUtil.toPath(basePackInternal);
        path = path.append(entry.getBasePackageRelativeTocPath());
		IFolder tocFileLocation = getTocFileLocation(root);
        if (tocFileLocation == null){
            return null;
        }
		return tocFileLocation.getFile(path);
	}

    private IFolder getTocFileLocation(IIpsPackageFragmentRoot root) throws CoreException{
        IIpsSrcFolderEntry entry = (IIpsSrcFolderEntry)root.getIpsObjectPathEntry();
        return entry.getOutputFolderForDerivedJavaFiles();
    }
    
    /**
     * {@inheritDoc}
     */
    public String getRuntimeRepositoryTocResourceName(IIpsPackageFragmentRoot root) throws CoreException {
        IFile tocFile = getRuntimeRepositoryTocFile(root);
        if (tocFile==null) {
            return null;
        }
        IFolder tocFileLocation = getTocFileLocation(root);
        return tocFile.getFullPath().removeFirstSegments(tocFileLocation.getFullPath().segmentCount()).toString();
    }

	/**
     * {@inheritDoc}
	 */
    public String getPackage(String kind, IIpsSrcFile ipsSrcFile) throws CoreException {
        // TODO v2 - das koenner wir effizienter implementieren
        if (IpsObjectType.TABLE_STRUCTURE.equals(ipsSrcFile.getIpsObjectType())) {
            if(KIND_TABLE_IMPL.equals(kind) || KIND_TABLE_ROW.equals(kind)){
                return getInternalPackageName(ipsSrcFile);
            }
        }

        if (IpsObjectType.POLICY_CMPT_TYPE.equals(ipsSrcFile.getIpsObjectType())) {
            if (KIND_POLICY_CMPT_INTERFACE.equals(kind)) {
                return getPackageName(ipsSrcFile);
            }

            if (KIND_POLICY_CMPT_IMPL.equals(kind)) {
                return getInternalPackageName(ipsSrcFile);
            }

        }

        if (IpsObjectType.PRODUCT_CMPT_TYPE_V2.equals(ipsSrcFile.getIpsObjectType())) {
            if (KIND_PRODUCT_CMPT_INTERFACE.equals(kind)) {
                return getPackageName(ipsSrcFile);
            }

            if (KIND_PRODUCT_CMPT_IMPL.equals(kind)) {
                return getInternalPackageName(ipsSrcFile);
            }
            if (KIND_PRODUCT_CMPT_GENERATION_INTERFACE.equals(kind)) {
                return getPackageName(ipsSrcFile);
            }
            if (KIND_PRODUCT_CMPT_GENERATION_IMPL.equals(kind)) {
                return getInternalPackageName(ipsSrcFile);
            }
        }

        if (IpsObjectType.PRODUCT_CMPT.equals(ipsSrcFile.getIpsObjectType())) {
            if (KIND_PRODUCT_CMPT_CONTENT.equals(kind)) {
                return getInternalPackageName(ipsSrcFile);
            }
            if (KIND_PRODUCT_CMPT_TOCENTRY.equals(kind)) {
                return getInternalPackageName(ipsSrcFile);
            }
            if (KIND_PRODUCT_CMPT_GENERATION_IMPL.equals(kind)) {
                return getInternalPackageName(ipsSrcFile);
            }
            if (KIND_FORMULA_TEST_CASE.equals(kind)){
                return getInternalPackageName(ipsSrcFile);
            }
        }

        if (IpsObjectType.TABLE_CONTENTS.equals(ipsSrcFile.getIpsObjectType())) {
            if (KIND_TABLE_CONTENT.equals(kind)) {
                return getInternalPackageName(ipsSrcFile);
            }
            if (KIND_TABLE_TOCENTRY.equals(kind)) {
                return getInternalPackageName(ipsSrcFile);
            }
        }

        if (IpsObjectType.TEST_CASE_TYPE.equals(ipsSrcFile.getIpsObjectType())) {
            if (KIND_TEST_CASE_TYPE_CLASS.equals(kind)) {
                return getInternalPackageName(ipsSrcFile);
            }
        }
        
        if (IpsObjectType.TEST_CASE.equals(ipsSrcFile.getIpsObjectType())) {
            if (KIND_TEST_CASE_XML.equals(kind)) {
                return getInternalPackageName(ipsSrcFile);
            }
        }

        return null;
    }

    /**
     * Overridden.
     */
    public boolean isSupportTableAccess() {
        return false;
    }

    /**
     * Empty implementation. Might be overriden by subclasses that support the formula language.
     */
    public CompilationResult getTableAccessCode(ITableContents tableContents, ITableAccessFunction fct, CompilationResult[] argResults) throws CoreException {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public boolean isSupportFlIdentifierResolver() {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    public IdentifierResolver createFlIdentifierResolver(IFormula formula) throws CoreException {
        return null;
    }

    /**
     * {@inheritDoc}
     * <p/>
     * Returns an empty artefact builder array. This method is supposed to be overridden by subclasses.
     */
    public IIpsArtefactBuilder[] getArtefactBuilders() {
        return new IIpsArtefactBuilder[0];
    }

    /**
     * {@inheritDoc}
     * <p/>
     * Returns <code>null</code>. This method is supposed to be overridden by subclasses.
     */
    public DatatypeHelper getDatatypeHelperForTableBasedEnum(TableContentsEnumDatatypeAdapter datatype) {
        return null;
    }

    /**
     * {@inheritDoc}
     * <p/>
     * Returns an empty string. This method is supposed to be overridden by subclasses.
     */
    public String getVersion() {
        return "";
    }

    /**
     * {@inheritDoc}
     * <p/>
     * An empty implementation. This method is supposed to be overridden by subclasses.
     */
    public void initialize(IIpsArtefactBuilderSetConfig config) throws CoreException {
    }
    
    
}
