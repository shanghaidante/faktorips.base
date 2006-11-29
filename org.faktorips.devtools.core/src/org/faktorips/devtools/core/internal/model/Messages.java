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

import org.eclipse.osgi.util.NLS;

/**
 * 
 * @author Thorsten Guenther
 */
public class Messages extends NLS {
	private static final String BUNDLE_NAME = "org.faktorips.devtools.core.internal.model.messages"; //$NON-NLS-1$

	private Messages() {
	}

	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	public static String DefaultIpsProjectNamingConventions_error;

    public static String DefaultIpsProjectNamingConventions_msgMissingName;

    public static String DefaultIpsProjectNamingConventions_msgMissingNameForProductCmpt;

    public static String DefaultIpsProjectNamingConventions_msgNameIdDiscouraged;

    public static String DefaultIpsProjectNamingConventions_msgNameMustNotBeQualified;

    public static String DefaultIpsProjectNamingConventions_msgNameNotValid;

    public static String DefaultIpsProjectNamingConventions_msgNameNotValidForProductCmpt;

    public static String DefaultIpsProjectNamingConventions_warning;

    public static String ValidationUtils_msgObjectDoesNotExist;

	public static String ValidationUtils_msgDatatypeDoesNotExist;

	public static String ValidationUtils_msgVoidNotAllowed;

	public static String ValidationUtils_msgPropertyMissing;

	public static String IpsProjectProperties_msgUnknownDatatype;

	public static String IpsProjectProperties_msgUnknownBuilderSetId;

	public static String IpsProject_msgMissingDotIpsprojectFile;

	public static String IpsProject_msgUnparsableDotIpsprojectFile;

	public static String ValidationUtils_VALUE_VALUEDATATYPE_NOT_FOUND;

	public static String ValidationUtils_VALUEDATATYPE_INVALID;

	public static String ValidationUtils_NO_INSTANCE_OF_VALUEDATATYPE;

    public static String IpsModel_msgRuntimeIDCollision;

    public static String TimedIpsObject_msgIvalidValidToDate;

    public static String IpsObjectGeneration_msgInvalidFromDate;

    public static String IpsProject_msgNoFeatureManager;

    public static String IpsProject_msgVersionTooLow;

    public static String IpsProject_msgIncompatibleVersions;

    public static String IpsProject_msgInvalidMigrationInformation;

    public static String IpsProjectRefEntry_msgMissingReferencedProject;

    public static String IpsSrcFolderEntry_msgMissingFolder;

    public static String IpsProject_msgDuplicateTocFilePath;

    public static String IpsArchiveEntry_archiveDoesNotExist;

    public static String IpsProjectProperties_msgMissingMinFeatureId;

}
