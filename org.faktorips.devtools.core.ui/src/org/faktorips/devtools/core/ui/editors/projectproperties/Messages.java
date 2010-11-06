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

package org.faktorips.devtools.core.ui.editors.projectproperties;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {

    private static final String BUNDLE_NAME = "org.faktorips.devtools.core.ui.editors.projectproperties.messages"; //$NON-NLS-1$

    static {
        NLS.initializeMessages(BUNDLE_NAME, Messages.class);
    }

    private Messages() {
        // Messages bundles shall not be initialized.
    }

    public static String IpsObjectEditor_fileHasChangesOnDiskMessage;
    public static String IpsObjectEditor_fileHasChangesOnDiskNoButton;
    public static String IpsObjectEditor_fileHasChangesOnDiskTitle;
    public static String IpsObjectEditor_fileHasChangesOnDiskYesButton;

    public static String IpsPartEditDialog_tabItemLabel;
    public static String IpsPartEditDialog_tabItemDescription;

    public static String IpsPartsComposite_buttonNew;
    public static String IpsPartsComposite_buttonEdit;
    public static String IpsPartsComposite_buttonShow;
    public static String IpsPartsComposite_buttonDelete;
    public static String IpsPartsComposite_buttonUp;
    public static String IpsPartsComposite_buttonDown;

    public static String TimedIpsObjectEditor_actualWorkingDate;

    public static String UnparsableFilePage_fileContentIsNotParsable;

    public static String UnreachableFilePage_msgUnreachableFile;

    public static String DescriptionPage_description;

    public static String DescriptionSection_description;

    public static String LabelPage_label;

    public static String LabelSection_label;

    public static String LabelEditComposite_tableColumnHeaderLanguage;
    public static String LabelEditComposite_tableColumnHeaderLabel;
    public static String LabelEditComposite_tableColumnHeaderPluralLabel;

    public static String OverviewPropertiesPage_description;
    public static String DatatypePropertiesPage_description;
    public static String ModelPropertiesPage_description;

    public static String BuildPathPropertiesPage_description;
    public static String BuildPathPropertiesPage_source;
    public static String BuildPathPropertiesPage_archive;
    public static String BuildPathPropertiesPage_projects;
    public static String BuildPathPropertiesPage_path_order;
    public static String ProductDefinitionPropertiesPage_description;
}