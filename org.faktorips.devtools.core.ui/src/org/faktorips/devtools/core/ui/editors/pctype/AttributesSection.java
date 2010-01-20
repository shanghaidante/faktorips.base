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

package org.faktorips.devtools.core.ui.editors.pctype;

import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.actions.ActionFactory.IWorkbenchAction;
import org.faktorips.devtools.core.model.ipsobject.IIpsObject;
import org.faktorips.devtools.core.model.ipsobject.IIpsObjectPart;
import org.faktorips.devtools.core.model.pctype.IPolicyCmptType;
import org.faktorips.devtools.core.model.pctype.IPolicyCmptTypeAttribute;
import org.faktorips.devtools.core.model.pctype.IValidationRule;
import org.faktorips.devtools.core.ui.UIToolkit;
import org.faktorips.devtools.core.ui.actions.RenameAction;
import org.faktorips.devtools.core.ui.editors.EditDialog;
import org.faktorips.devtools.core.ui.editors.IDeleteListener;
import org.faktorips.devtools.core.ui.editors.IpsObjectEditorPage;
import org.faktorips.devtools.core.ui.editors.IpsPartsComposite;
import org.faktorips.devtools.core.ui.editors.SimpleIpsPartsSection;
import org.faktorips.util.ArgumentCheck;

/**
 * A section to display and edit a type's attributes.
 */
public class AttributesSection extends SimpleIpsPartsSection {

    private IpsObjectEditorPage page;

    public AttributesSection(IpsObjectEditorPage page, IPolicyCmptType pcType, Composite parent, UIToolkit toolkit) {
        super(pcType, parent, Messages.AttributesSection_title, toolkit);
        ArgumentCheck.notNull(page);
        this.page = page;
        ((AttributesComposite)getPartsComposite()).createContextMenu();
    }

    @Override
    protected IpsPartsComposite createIpsPartsComposite(Composite parent, UIToolkit toolkit) {
        return new AttributesComposite(getIpsObject(), parent, toolkit);
    }

    /**
     * A composite that shows a policy component's attributes in a viewer and allows to edit
     * attributes in a dialog, create new attributes and delete attributes.
     */
    public class AttributesComposite extends IpsPartsComposite {

        private Button overrideButton;

        public AttributesComposite(IIpsObject pdObject, Composite parent, UIToolkit toolkit) {
            super(pdObject, parent, toolkit);
            addDeleteListener();
        }

        private void addDeleteListener() {
            super.addDeleteListener(new IDeleteListener() {
                public boolean aboutToDelete(IIpsObjectPart part) {
                    IValidationRule rule = findValidationRule(part);
                    if (rule == null) {
                        // Nothing to do if no special rule is defined.
                        return true;
                    }
                    String msg = Messages.AttributesSection_deleteMessage;
                    boolean delete = MessageDialog
                            .openQuestion(getShell(), Messages.AttributesSection_deleteTitle, msg);
                    if (delete && rule != null) {
                        rule.delete();
                    } else if (!delete && rule != null) {
                        rule.setCheckValueAgainstValueSetRule(false);
                    }
                    return true;
                }

                private IValidationRule findValidationRule(IIpsObjectPart part) {
                    String name = part.getName();
                    IValidationRule[] rules = getPcType().getRules();
                    for (int i = 0; i < rules.length; i++) {
                        if (!rules[i].isCheckValueAgainstValueSetRule()) {
                            continue;
                        }
                        String[] attributes = rules[i].getValidatedAttributes();
                        if (attributes.length == 1 && attributes[0].equals(name)) {
                            return rules[i];
                        }
                    }
                    return null;
                }

                public void deleted(IIpsObjectPart part) {
                    // Nothing to do.
                }
            });
        }

        private void createContextMenu() {
            IEditorSite editorSite = (IEditorSite)page.getEditor().getSite();
            final IWorkbenchAction renameAction = ActionFactory.RENAME.create(editorSite.getWorkbenchWindow());
            IActionBars actionBars = editorSite.getActionBars();
            actionBars.setGlobalActionHandler(ActionFactory.RENAME.getId(), new RenameAction(editorSite.getShell(),
                    getPartsComposite()));

            MenuManager manager = new MenuManager();
            manager.setRemoveAllWhenShown(true);
            manager.addMenuListener(new IMenuListener() {
                public void menuAboutToShow(IMenuManager manager) {
                    manager.add(new Separator());
                    MenuManager refactorSubmenu = new MenuManager(Messages.AttributesSection_submenuRefactor);
                    refactorSubmenu.add(renameAction);
                    manager.add(refactorSubmenu);
                }
            });
            Menu contextMenu = manager.createContextMenu(getViewer().getControl());
            getViewer().getControl().setMenu(contextMenu);
        }

        @Override
        public void setDataChangeable(boolean flag) {
            super.setDataChangeable(flag);
            overrideButton.setEnabled(flag);
        }

        public IPolicyCmptType getPcType() {
            return (IPolicyCmptType)getIpsObject();
        }

        @Override
        protected boolean createButtons(Composite buttons, UIToolkit toolkit) {
            super.createButtons(buttons, toolkit);
            createButtonSpace(buttons, toolkit);
            overrideButton = toolkit.createButton(buttons, Messages.AttributesSection_OverrideButton);
            overrideButton.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL
                    | GridData.VERTICAL_ALIGN_BEGINNING));
            overrideButton.addSelectionListener(new SelectionListener() {
                public void widgetSelected(SelectionEvent e) {
                    overrideClicked();
                }

                public void widgetDefaultSelected(SelectionEvent e) {
                }
            });
            return true;
        }

        private void overrideClicked() {
            OverrideAttributeDialog dialog = new OverrideAttributeDialog(getPcType(), getShell());
            if (dialog.open() == Window.OK) {
                getPcType().overrideAttributes(dialog.getSelectedAttributes());
                refresh();
            }
        }

        @Override
        protected IStructuredContentProvider createContentProvider() {
            return new AttributeContentProvider();
        }

        @Override
        protected IIpsObjectPart newIpsPart() {
            return getPcType().newPolicyCmptTypeAttribute();
        }

        @Override
        protected EditDialog createEditDialog(IIpsObjectPart part, Shell shell) {
            return new AttributeEditDialog((IPolicyCmptTypeAttribute)part, shell);
        }

        @Override
        protected int[] moveParts(int[] indexes, boolean up) {
            return getPcType().moveAttributes(indexes, up);
        }

        private class AttributeContentProvider implements IStructuredContentProvider {

            public Object[] getElements(Object inputElement) {
                return getPcType().getPolicyCmptTypeAttributes();
            }

            public void dispose() {
                // Nothing to do.
            }

            public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
                // Nothing to do.
            }

        }

    }

}
