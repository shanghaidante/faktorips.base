/*******************************************************************************
 * Copyright (c) 2005,2006 Faktor Zehn GmbH und andere.
 *
 * Alle Rechte vorbehalten.
 *
 * Dieses Programm und alle mitgelieferten Sachen (Dokumentationen, Beispiele,
 * Konfigurationen, etc.) dürfen nur unter den Bedingungen der 
 * Faktor-Zehn-Community Lizenzvereinbarung – Version 0.1 (vor Gründung Community) 
 * genutzt werden, die Bestandteil der Auslieferung ist und auch unter
 *   http://www.faktorips.org/legal/cl-v01.html
 * eingesehen werden kann.
 *
 * Mitwirkende:
 *   Faktor Zehn GmbH - initial API and implementation 
 *
 *******************************************************************************/

package org.faktorips.devtools.core.ui.preferencepages;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.ui.dialogs.ElementTreeSelectionDialog;
import org.eclipse.ui.dialogs.ISelectionStatusValidator;
import org.eclipse.ui.model.WorkbenchContentProvider;
import org.eclipse.ui.model.WorkbenchLabelProvider;
import org.faktorips.devtools.core.IpsPlugin;
import org.faktorips.devtools.core.IpsStatus;
import org.faktorips.devtools.core.model.ipsproject.IIpsArchiveEntry;
import org.faktorips.devtools.core.model.ipsproject.IIpsObjectPath;
import org.faktorips.devtools.core.ui.UIToolkit;

/**
 * Composite for modifying IPS archive references  
 * @author Roman Grutza
 */
public class ArchiveComposite extends Composite {

    private UIToolkit toolkit;
    private TableViewer tableViewer;
    private Button addIpsArchivesButton;
    private Button removeIpsArchivesButton;
    private Button addExternalIpsArchivesButton;
    private Table table;
    private IIpsObjectPath ipsObjectPath;
    private boolean dataChanged = false;

    /**
     * Referenced IPS archives for the current IPS projects have been modified
     * @return true if current project's archives have been modified, false otherwise
     */
    public final boolean isDataChanged() {
        return dataChanged;
    }

    public ArchiveComposite(Composite parent) {
        super(parent, SWT.NONE);
        
        this.toolkit = new UIToolkit(null);

        this.setLayout(new GridLayout(1, true));

        Composite tableWithButtons = toolkit.createGridComposite(this, 2, false, true);
        tableWithButtons.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        IpsArchiveAdapter archiveAdapter = new IpsArchiveAdapter();
        
        Label tableViewerLabel = new Label(tableWithButtons, SWT.NONE);
        tableViewerLabel.setText(Messages.ArchiveComposite_viewer_label);
        GridData gd = new GridData(GridData.VERTICAL_ALIGN_BEGINNING);
        gd.horizontalSpan = 2;
        tableViewerLabel.setLayoutData(gd);

        tableViewer = createViewer(tableWithButtons, archiveAdapter);
        tableViewer.getControl().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        Composite buttons = toolkit.createComposite(tableWithButtons);
        buttons.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_BEGINNING));
        GridLayout buttonLayout = new GridLayout(1, true);
        buttonLayout.horizontalSpacing = 10;
        buttonLayout.marginWidth = 10;
        buttonLayout.marginHeight = 0;
        buttons.setLayout(buttonLayout);
        createButtons(buttons, archiveAdapter);
    }
        
    private void createButtons(Composite buttons, IpsArchiveAdapter archiveAdapter) {
        addIpsArchivesButton = toolkit.createButton(buttons, Messages.ArchiveComposite_button_add_archive);
        addIpsArchivesButton.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_BEGINNING));
        addIpsArchivesButton.addSelectionListener(archiveAdapter);

        addExternalIpsArchivesButton = toolkit.createButton(buttons, Messages.ArchiveComposite_button_add_external_archive);
        addExternalIpsArchivesButton.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_BEGINNING));
        addExternalIpsArchivesButton.addSelectionListener(archiveAdapter);

        removeIpsArchivesButton = toolkit.createButton(buttons, Messages.ArchiveComposite_button_remove_archive);
        removeIpsArchivesButton.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_BEGINNING));
        removeIpsArchivesButton.addSelectionListener(archiveAdapter);
        removeIpsArchivesButton.setEnabled(false);
        
    }

    private TableViewer createViewer(Composite parent, IpsArchiveAdapter archiveAdapter) {
        table = new Table(parent, SWT.BORDER | SWT.MULTI);
        tableViewer = new TableViewer(table);
        tableViewer.addSelectionChangedListener(archiveAdapter);
        tableViewer.setContentProvider(new ArrayContentProvider());
        tableViewer.setLabelProvider(new TableLabelProvider());
        
        tableViewer.setInput(ipsObjectPath);

        return tableViewer;
    }

    /**
     * Initializes the composite for an existing IPS Project
     * @param ipsProject IPS project to initialize
     * @throws CoreException 
     */    
    public void init(final IIpsObjectPath ipsObjectPath) {
        this.ipsObjectPath = ipsObjectPath;

        IIpsArchiveEntry[] archiveEntries = ipsObjectPath.getArchiveEntries();
        tableViewer.setInput(archiveEntries);

        if (Display.getCurrent() != null) {
            tableViewer.refresh();
        } else {
            Display.getDefault().asyncExec(new Runnable() {
                public void run() {
                    tableViewer.refresh();
                }
            });
        }
    }    
    
    private void addIpsArchives() {
        
        IIpsArchiveEntry[] archiveEntries = ipsObjectPath.getArchiveEntries();
        List alreadyRefArchives = new ArrayList();
        for  (int i = 0; i < archiveEntries.length; i++) {
            alreadyRefArchives.add(archiveEntries[i].getArchiveFile());
        }
        
        ElementTreeSelectionDialog dialog = new ElementTreeSelectionDialog(null, new WorkbenchLabelProvider(), new WorkbenchContentProvider());
        dialog.setInput(ResourcesPlugin.getWorkspace().getRoot());
        dialog.setMessage(Messages.ArchiveComposite_dialog_message_add);
        dialog.setTitle(Messages.ArchiveComposite_dialog_title_add);
        dialog.addFilter(new IpsarViewerFilter(alreadyRefArchives, true));

        ISelectionStatusValidator validator = new ISelectionStatusValidator() {
            public IStatus validate(Object[] selection) {
                for (int i = 0; i < selection.length; i++) {
                    if (selection[i] == null || ! (selection[i] instanceof IFile))
                        return new IpsStatus(IpsStatus.WARNING, Messages.ArchiveComposite_dialog_warning_select_archive);
                }
                return new IpsStatus(IpsStatus.OK, " "); //$NON-NLS-1$
            }
        };
        // prevent user to click OK when a folder is selected, only IPS archives are commitable
        dialog.setValidator(validator);

        try {    
            if (dialog.open() == Window.OK) {
                Object[] selectedArchives = dialog.getResult();
                if (selectedArchives.length < 1)
                    return;

                for (int i = 0; i < selectedArchives.length; i++) {
                    IFile archiveFile = (IFile) selectedArchives[i];
                    tableViewer.add(ipsObjectPath.newArchiveEntry(archiveFile));
                }
            }
        } catch (CoreException e) {
            IpsPlugin.logAndShowErrorDialog(e);
            return;
        }
        dataChanged = true;
    }

    private void addExternalIpsArchives() {
        // TODO: impl
        // needs model change, since now it's not possible to reference archives outside the current projects` path?
    }

    private void removeIpsArchives() {
        IStructuredSelection selection = (IStructuredSelection)tableViewer.getSelection();
        if (selection.size() > 0) {
            dataChanged  = true;
            for (Iterator it = selection.iterator(); it.hasNext(); ) {
                IIpsArchiveEntry archiveEntry = (IIpsArchiveEntry) it.next();
                ipsObjectPath.removeArchiveEntry(archiveEntry.getIpsArchive());
                tableViewer.remove(archiveEntry);
            }
        }        
    }
    
    
    private static class TableLabelProvider extends LabelProvider {

        public Image getImage(Object element) {           
            return IpsPlugin.getDefault().getImage("IpsAr.gif"); //$NON-NLS-1$
        }

        public String getText(Object element) {
            if (element instanceof IIpsArchiveEntry) {
                IIpsArchiveEntry ipsarEntry = (IIpsArchiveEntry) element;
                StringBuffer result = new StringBuffer();
                result.append(ipsarEntry.getArchiveFile().getName());
                result.append(" - "); //$NON-NLS-1$
                result.append(ipsarEntry.getArchiveFile().getProject().getName());
                
                int numSegments = ipsarEntry.getArchiveFile().getProjectRelativePath().segmentCount();
                if ( numSegments > 1) { // append directory tree in which the IPS archive resides 
                    for (int i = 0; i < numSegments - 1; i++) {
                        result.append(IPath.SEPARATOR);
                        result.append(ipsarEntry.getArchiveFile().getProjectRelativePath().segment(i));                        
                    }
                }
                return result.toString();
            }
            return Messages.ArchiveComposite_labelProvider_invalid_element;
        }
    }

    private class IpsArchiveAdapter implements SelectionListener, ISelectionChangedListener {

        public void selectionChanged(SelectionChangedEvent event) {
            if (event.getSelection().isEmpty()) {
                removeIpsArchivesButton.setEnabled(false);
            } else {
                removeIpsArchivesButton.setEnabled(true);
            }
        }

        public void widgetSelected(SelectionEvent e) {
            if (e.getSource() == addIpsArchivesButton) {
                addIpsArchives();
            }
            if (e.getSource() == addExternalIpsArchivesButton) {
                addExternalIpsArchives();
            }
            if (e.getSource() == removeIpsArchivesButton) {
                removeIpsArchives();
            }
        }

        public void widgetDefaultSelected(SelectionEvent e) { /* nothing to do */ }
    }

}

