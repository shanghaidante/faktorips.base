/*******************************************************************************
 * Copyright (c) Faktor Zehn AG. <http://www.faktorzehn.org>
 * 
 * This source code is available under the terms of the AGPL Affero General Public License version 3.
 * 
 * Please see LICENSE.txt for full license terms, including the additional permissions and the
 * possibility of alternative license terms.
 *******************************************************************************/

package org.faktorips.devtools.stdbuilder.enumtype;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Locale;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.faktorips.devtools.core.builder.AbstractArtefactBuilder;
import org.faktorips.devtools.core.exception.CoreRuntimeException;
import org.faktorips.devtools.core.model.enums.IEnumType;
import org.faktorips.devtools.core.model.ipsobject.IIpsObject;
import org.faktorips.devtools.core.model.ipsobject.IIpsSrcFile;
import org.faktorips.devtools.core.model.ipsobject.IpsObjectType;
import org.faktorips.devtools.core.model.ipsproject.ISupportedLanguage;
import org.faktorips.devtools.stdbuilder.StandardBuilderSet;
import org.faktorips.util.LocalizedStringsSet;

/**
 * This builder generates property files for every language and every enum type that contains
 * multilingual attributes. The property file contains one property for every internationalized enum
 * attribute value. The key of the property will be <name of attribute>_<id of value>.
 * 
 * @author dirmeier
 */
public class EnumPropertyBuilder extends AbstractArtefactBuilder {

    private IEnumType enumType;

    public EnumPropertyBuilder(StandardBuilderSet builderSet) {
        super(builderSet, new LocalizedStringsSet(EnumPropertyBuilder.class));
    }

    @Override
    public StandardBuilderSet getBuilderSet() {
        return (StandardBuilderSet)super.getBuilderSet();
    }

    @Override
    public String getName() {
        return "EnumPropertyBuilder";
    }

    @Override
    public void build(IIpsSrcFile ipsSrcFile) throws CoreException {
        IIpsObject ipsObject = ipsSrcFile.getIpsObject();
        if (ipsObject instanceof IEnumType) {
            IEnumType foundEnumType = (IEnumType)ipsObject;
            this.enumType = foundEnumType;
            if (foundEnumType.containsValues()) {
                generatePropertyFilesFor();
            }
        }
    }

    void generatePropertyFilesFor() {
        Set<ISupportedLanguage> supportedLanguages = getIpsProject().getReadOnlyProperties().getSupportedLanguages();
        for (ISupportedLanguage supportedLanguage : supportedLanguages) {
            EnumPropertyGenerator enumPropertyGenerator = new EnumPropertyGenerator(enumType,
                    supportedLanguage.getLocale());
            generatePropertyFile(enumPropertyGenerator);
        }
    }

    void generatePropertyFile(EnumPropertyGenerator enumPropertyGenerator) {
        loadFromFile(enumPropertyGenerator);
        boolean generatedSomething = enumPropertyGenerator.generatePropertyFile();
        if (generatedSomething) {
            writeToFile(enumPropertyGenerator);
        }
    }

    private void loadFromFile(EnumPropertyGenerator enumPropertyGenerator) {
        try {
            IFile file = getPropertyFile(enumPropertyGenerator.getLocale());
            if (file.exists()) {
                enumPropertyGenerator.readFromStream(file.getContents());
            }
        } catch (CoreException e) {
            throw new CoreRuntimeException(e);
        }
    }

    private void writeToFile(EnumPropertyGenerator enumPropertyGenerator) {
        try {
            IFile file = getPropertyFile(enumPropertyGenerator.getLocale());
            createFolderIfNotThere((IFolder)file.getParent());
            createFileIfNotTher(file);
            InputStream inputStream = enumPropertyGenerator.getStream(getLocalizedStringSet().getString(
                    "MULTILINGUAL_PROPERTY_COMMENT", getBuilderSet().getLanguageUsedInGeneratedSourceCode(),
                    enumType.getName(), enumPropertyGenerator.getLocale().getDisplayLanguage()));
            writeToFile(file, inputStream, true, true);
        } catch (CoreException e) {
            throw new CoreRuntimeException(e);
        }
    }

    private void createFileIfNotTher(IFile file) throws CoreException {
        if (!file.exists()) {
            file.create(new ByteArrayInputStream("".getBytes()), true, null);
            file.setDerived(buildsDerivedArtefacts() && getBuilderSet().isMarkNoneMergableResourcesAsDerived());
        }
    }

    /**
     * This method returns the property file in which we want to write the messages for the given
     * locale. The property file is located in derived @see {@link #buildsDerivedArtefacts()} but
     * uses the base package of mergeable sources to get the same qualified name as the generated
     * enum java class.
     */
    IFile getPropertyFile(Locale locale) throws CoreException {
        IFolder artefactDestination = getArtefactDestination(enumType.getIpsSrcFile());
        IPath relativeJavaFile = getBuilderSet().getEnumTypeBuilder().getRelativeJavaFile(enumType.getIpsSrcFile());
        IPath relativePropertyFile = relativeJavaFile.removeFileExtension();
        IPath folder = relativePropertyFile.removeLastSegments(1);
        String filename = relativePropertyFile.lastSegment() + "_" + locale.getLanguage();
        return artefactDestination.getFile(folder.append(filename).addFileExtension("properties"));
    }

    @Override
    public boolean isBuilderFor(IIpsSrcFile ipsSrcFile) throws CoreException {
        IpsObjectType ipsObjectType = ipsSrcFile.getIpsObjectType();
        return IpsObjectType.ENUM_TYPE.equals(ipsObjectType);
    }

    @Override
    public boolean isBuildingInternalArtifacts() {
        return false;
    }

    @Override
    public boolean buildsDerivedArtefacts() {
        return true;
    }

    @Override
    public void delete(IIpsSrcFile ipsSrcFile) throws CoreException {
        Set<ISupportedLanguage> supportedLanguages = getIpsProject().getReadOnlyProperties().getSupportedLanguages();
        for (ISupportedLanguage supportedLanguage : supportedLanguages) {
            IFile propertyFile = getPropertyFile(supportedLanguage.getLocale());
            if (propertyFile.exists()) {
                propertyFile.delete(true, null);
            }
        }
    }

}
