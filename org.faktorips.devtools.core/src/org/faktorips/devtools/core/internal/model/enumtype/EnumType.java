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

package org.faktorips.devtools.core.internal.model.enumtype;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import org.eclipse.core.runtime.CoreException;
import org.faktorips.devtools.core.internal.model.enums.EnumValue;
import org.faktorips.devtools.core.internal.model.enums.EnumValueContainer;
import org.faktorips.devtools.core.internal.model.ipsobject.IpsObjectPartCollection;
import org.faktorips.devtools.core.model.enums.IEnumAttributeValue;
import org.faktorips.devtools.core.model.enums.IEnumValue;
import org.faktorips.devtools.core.model.enumtype.EnumTypeValidations;
import org.faktorips.devtools.core.model.enumtype.IEnumAttribute;
import org.faktorips.devtools.core.model.enumtype.IEnumType;
import org.faktorips.devtools.core.model.ipsobject.IIpsObjectPart;
import org.faktorips.devtools.core.model.ipsobject.IIpsSrcFile;
import org.faktorips.devtools.core.model.ipsobject.IpsObjectType;
import org.faktorips.devtools.core.model.ipsproject.IIpsProject;
import org.faktorips.util.ArgumentCheck;
import org.faktorips.util.message.Message;
import org.faktorips.util.message.MessageList;
import org.w3c.dom.Element;

/**
 * Implementation of <code>IEnumType</code>, see the corresponding interface for more details.
 * 
 * @see org.faktorips.devtools.core.model.enumtype.IEnumType
 * 
 * @author Alexander Weickmann
 * 
 * @since 2.3
 */
public class EnumType extends EnumValueContainer implements IEnumType {

    /** Reference to the super enum type if any. */
    private String superEnumType;

    /** Flag indicating whether the values for this enum type are defined in the model. */
    private boolean valuesArePartOfModel;

    /** Collection containing all enum attributes for this enum type. */
    private IpsObjectPartCollection enumAttributes;

    /**
     * Flag indicating whether this enum type is abstract in means of the object oriented abstract
     * concept.
     */
    private boolean isAbstract;

    /**
     * Creates a new <code>EnumType</code>.
     * 
     * @param file The ips source file in which this enum type will be stored in.
     */
    public EnumType(IIpsSrcFile file) {
        super(file);

        this.superEnumType = "";
        this.enumAttributes = new IpsObjectPartCollection(this, EnumAttribute.class, IEnumAttribute.class,
                IEnumAttribute.XML_TAG);
        this.valuesArePartOfModel = false;
        this.isAbstract = false;
    }

    /**
     * {@inheritDoc}
     */
    public IpsObjectType getIpsObjectType() {
        return IpsObjectType.ENUM_TYPE;
    }

    /**
     * {@inheritDoc}
     */
    public boolean isAbstract() {
        return isAbstract;
    }

    /**
     * {@inheritDoc}
     */
    public void setAbstract(boolean isAbstract) {
        boolean oldIsAbstract = this.isAbstract;
        this.isAbstract = isAbstract;
        valueChanged(oldIsAbstract, isAbstract);
    }

    /**
     * {@inheritDoc}
     */
    public boolean getValuesArePartOfModel() {
        return valuesArePartOfModel;
    }

    /**
     * {@inheritDoc}
     */
    public void setValuesArePartOfModel(boolean valuesArePartOfModel) {
        boolean oldValuesArePartOfModel = this.valuesArePartOfModel;
        this.valuesArePartOfModel = valuesArePartOfModel;
        valueChanged(oldValuesArePartOfModel, valuesArePartOfModel);
    }

    /**
     * {@inheritDoc}
     */
    public String getSuperEnumType() {
        return superEnumType;
    }

    /**
     * {@inheritDoc}
     */
    public void setSuperEnumType(String superEnumTypeQualifiedName) {
        ArgumentCheck.notNull(superEnumTypeQualifiedName);

        String oldSupertype = superEnumType;
        superEnumType = superEnumTypeQualifiedName;
        valueChanged(oldSupertype, superEnumType);
    }

    /**
     * {@inheritDoc}
     */
    public List<IEnumAttribute> getEnumAttributes() {
        List<IEnumAttribute> attributesList = new ArrayList<IEnumAttribute>();
        IIpsObjectPart[] parts = enumAttributes.getParts();
        for (IIpsObjectPart currentObjectPart : parts) {
            attributesList.add((IEnumAttribute)currentObjectPart);
        }

        return attributesList;
    }

    /**
     * {@inheritDoc}
     */
    public IEnumAttribute newEnumAttribute() throws CoreException {
        IEnumAttribute newEnumAttribute = (IEnumAttribute)newPart(IEnumAttribute.class);

        // Create new enum attribute value objects on the enum values of this enum type
        for (IEnumValue currentEnumValue : getEnumValues()) {
            currentEnumValue.newEnumAttributeValue();
        }

        return newEnumAttribute;
    }

    /**
     * {@inheritDoc}
     */
    public IEnumType findEnumType() {
        return this;
    }

    /**
     * {@inheritDoc}
     */
    public int getEnumAttributesCount() {
        return enumAttributes.size();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void initFromXml(Element element, Integer id) {
        isAbstract = Boolean.parseBoolean(element.getAttribute(IEnumType.PROPERTY_ABSTRACT));
        valuesArePartOfModel = Boolean.parseBoolean(element.getAttribute(IEnumType.PROPERTY_VALUES_ARE_PART_OF_MODEL));
        superEnumType = element.getAttribute(IEnumType.PROPERTY_SUPERTYPE);

        super.initFromXml(element, id);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void propertiesToXml(Element element) {
        super.propertiesToXml(element);

        element.setAttribute(PROPERTY_SUPERTYPE, superEnumType);
        element.setAttribute(PROPERTY_ABSTRACT, String.valueOf(isAbstract));
        element.setAttribute(PROPERTY_VALUES_ARE_PART_OF_MODEL, String.valueOf(valuesArePartOfModel));
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    public int moveEnumAttribute(IEnumAttribute enumAttribute, boolean up) throws CoreException {
        ArgumentCheck.notNull(enumAttribute);

        if (up) {
            // Can't move further up any more
            if (enumAttribute == enumAttributes.getPart(0)) {
                return getIndexOfEnumAttribute(enumAttribute);
            }
        } else {
            // Can't move further down any more
            if (enumAttribute == enumAttributes.getPart(enumAttributes.size() - 1)) {
                return getIndexOfEnumAttribute(enumAttribute);
            }
        }

        List<IEnumAttribute> enumAttributesList = enumAttributes.getBackingList();
        for (int i = 0; i < enumAttributesList.size(); i++) {
            IEnumAttribute currentEnumAttribute = enumAttributesList.get(i);
            if (currentEnumAttribute == enumAttribute) {

                int[] newIndex = enumAttributes.moveParts(new int[] { i }, up);

                /*
                 * Also move the refering enum attribute values that are defined directly in this
                 * enum type.
                 */
                int modifier = (up) ? 1 : -1;
                moveEnumAttributeValues(newIndex[0] + modifier, getEnumValues(), up);

                return newIndex[0];

            }
        }

        throw new NoSuchElementException();
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    public int getIndexOfEnumAttribute(IEnumAttribute enumAttribute) {
        ArgumentCheck.notNull(enumAttribute);

        List<IEnumAttribute> enumAttributesList = enumAttributes.getBackingList();
        for (int i = 0; i < enumAttributesList.size(); i++) {
            IEnumAttribute currentEnumAttribute = enumAttributesList.get(i);
            if (currentEnumAttribute == enumAttribute) {
                return i;
            }
        }

        throw new NoSuchElementException();
    }

    /**
     * Moves the enum attribute value corresponding to the given enum attribute identified by its
     * index in each given enum value up or down in the containing list by 1.
     */
    private void moveEnumAttributeValues(int enumAttributeIndex, List<IEnumValue> enumValues, boolean up)
            throws CoreException {

        for (IEnumValue currentEnumValue : enumValues) {
            ((EnumValue)currentEnumValue).moveEnumAttributeValue(enumAttributeIndex, up);
        }
    }

    /**
     * {@inheritDoc}
     */
    public String getJavaClassName() {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     */
    public boolean hasNullObject() {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    public boolean isPrimitive() {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    public boolean isValueDatatype() {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    public boolean isVoid() {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    public int compareTo(Object o) {
        return 0;
    }

    /**
     * {@inheritDoc}
     */
    public boolean enumAttributeExists(String name) {
        ArgumentCheck.notNull(name);

        for (IEnumAttribute currentEnumAttribute : getEnumAttributes()) {
            if (currentEnumAttribute.getName().equals(name)) {
                return true;
            }
        }

        return false;
    }

    /**
     * {@inheritDoc}
     */
    public IEnumAttribute getEnumAttribute(String name) {
        ArgumentCheck.notNull(name);

        for (IEnumAttribute currentEnumAttribute : getEnumAttributes()) {
            if (currentEnumAttribute.getName().equals(name)) {
                return currentEnumAttribute;
            }
        }

        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void validateThis(MessageList list, IIpsProject ipsProject) throws CoreException {
        super.validateThis(list, ipsProject);

        Message validationMessage = null;

        // Validate super enum type
        if (!(superEnumType.equals(""))) {
            validationMessage = EnumTypeValidations.validateSuperEnumType(this, superEnumType, ipsProject);
            if (validationMessage != null) {
                list.add(validationMessage);
            }
        }

        // Validate inherited attributes
        if (!(superEnumType.equals(""))) {
            if (validationMessage == null) {
                validationMessage = EnumTypeValidations.validateInheritedAttributes(this);
                if (validationMessage != null) {
                    list.add(validationMessage);
                }
            }
        }

        // Validate identifier attribute
        validationMessage = EnumTypeValidations.validateIdentifierAttribute(this);
        if (validationMessage != null) {
            list.add(validationMessage);
        }
    }

    /**
     * {@inheritDoc}
     */
    public IEnumType findSuperEnumType() throws CoreException {
        IIpsSrcFile[] enumTypeSrcFiles = getIpsProject().findIpsSrcFiles(IpsObjectType.ENUM_TYPE);
        for (IIpsSrcFile currentIpsSrcFile : enumTypeSrcFiles) {
            IEnumType currentEnumType = (IEnumType)currentIpsSrcFile.getIpsObject();
            if (currentEnumType.getQualifiedName().equals(superEnumType)) {
                return currentEnumType;
            }
        }

        return null;
    }

    /**
     * {@inheritDoc}
     */
    public void deleteEnumAttributeWithValues(IEnumAttribute enumAttribute) throws CoreException {
        ArgumentCheck.notNull(enumAttribute);
        ArgumentCheck.isTrue(enumAttributes.getBackingList().contains(enumAttribute));

        deleteEnumAttributeValues(enumAttribute, getEnumValues());
        enumAttribute.delete();
    }

    /** Deletes all enum attribute values that refer to the given enum attribute. */
    private void deleteEnumAttributeValues(IEnumAttribute enumAttribute, List<IEnumValue> enumValues)
            throws CoreException {

        for (IEnumValue currentEnumValue : enumValues) {
            for (IEnumAttributeValue currentEnumAttributeValue : currentEnumValue.getEnumAttributeValues()) {
                if (currentEnumAttributeValue.findEnumAttribute() == enumAttribute) {
                    currentEnumAttributeValue.delete();
                    break;
                }
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    public boolean hasSuperEnumType() {
        return (!(superEnumType.equals("")));
    }

    /**
     * {@inheritDoc}
     */
    public List<IEnumType> findAllSuperEnumTypes() throws CoreException {
        List<IEnumType> enumTypes = new ArrayList<IEnumType>();

        IEnumType currentEnumType = this;
        while (currentEnumType != null) {
            IEnumType superEnumType = currentEnumType.findSuperEnumType();
            currentEnumType = superEnumType;
            if (superEnumType != null) {
                enumTypes.add(superEnumType);
            }
        }

        return enumTypes;
    }

    /**
     * {@inheritDoc}
     */
    public List<IEnumAttribute> getInheritedAttributes() {
        List<IEnumAttribute> inheritedAttributes = new ArrayList<IEnumAttribute>();

        for (IEnumAttribute currentEnumAttribute : getEnumAttributes()) {
            if (currentEnumAttribute.isInherited()) {
                inheritedAttributes.add(currentEnumAttribute);
            }
        }

        return inheritedAttributes;
    }

}
