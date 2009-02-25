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

package org.faktorips.devtools.core.builder;

import org.faktorips.codegen.JavaCodeFragmentBuilder;

/**
 * A TypeSection assembles the code fragments for a type, e.g. the main class or an inner class,
 * during the generation process. Therefore it provides subsections for typical code sections like
 * for constants, attributes, methods, constructors and java doc for the type.
 * 
 * @author Peter Erzberger
 */
public final class TypeSection {

    private boolean isClass = true;
    private boolean isEnum = false;
    private int classModifier;
    private String[] extendedInterfaces;
    private String superClass;
    private String unqualifiedName;
    private JavaCodeFragmentBuilder constantBuilder;
    private JavaCodeFragmentBuilder javaDocForTypeBuilder;
    private JavaCodeFragmentBuilder memberVariableBuilder;
    private JavaCodeFragmentBuilder constructorBuilder;
    private JavaCodeFragmentBuilder methodBuilder;
    private JavaCodeFragmentBuilder enumDefinitionBuilder;

    public TypeSection() {
        enumDefinitionBuilder = new JavaCodeFragmentBuilder();
        constantBuilder = new JavaCodeFragmentBuilder();
        memberVariableBuilder = new JavaCodeFragmentBuilder();
        constructorBuilder = new JavaCodeFragmentBuilder();
        methodBuilder = new JavaCodeFragmentBuilder();
        javaDocForTypeBuilder = new JavaCodeFragmentBuilder();
    }

    /**
     * Returns the class modifier for the type represented by this TypeSection.
     * 
     * @see <code>{@link java.reflect.Modifier}</code>
     */
    public int getClassModifier() {
        return classModifier;
    }

    /**
     * Sets the class modifier for the type represented by this TypeSection.
     * 
     * @see <code>{@link java.reflect.Modifier}</code>
     */
    public void setClassModifier(int classModifier) {
        this.classModifier = classModifier;
    }

    /**
     * Returns the qualified names of the interfaces that the generated class or interface extends.
     */
    public String[] getExtendedInterfaces() {
        return extendedInterfaces;
    }

    /**
     * Sets the qualified names of the interfaces that the generated class / interface implements /
     * extends.
     */
    public void setExtendedInterfaces(String[] extendedInterfaces) {
        this.extendedInterfaces = extendedInterfaces;
    }

    /**
     * Returns if the type that is to generate is a class or an interface.
     */
    public boolean isClass() {
        return isClass;
    }

    /**
     * Sets if the type that is to generate is a class or an interface.
     */
    public void setClass(boolean isClass) {
        this.isClass = isClass;
        if (this.isClass) {
            this.isEnum = false;
        }
    }

    /**
     * Returns if the type that is to generate is an enum or an interface.
     */
    public boolean isEnum() {
        return isEnum;
    }

    /**
     * Sets if the type that is to generate is an enum or an interface.
     */
    public void setEnum(boolean isEnum) {
        this.isEnum = isEnum;
        if (isEnum) {
            this.isClass = false;
        }
    }

    /**
     * Returns the qualified name of the super class that the generated class extends.
     * <code>null</code> indicates no superclass will be extended by the generated class.
     */
    public String getSuperClass() {
        return superClass;
    }

    /**
     * Sets the qualified name of the super class that the generated class extends.
     * <code>null</code> indicates no superclass will be extended by the generated class.
     */
    public void setSuperClass(String superClass) {
        this.superClass = superClass;
    }

    /**
     * Returns the unqualified name of the generated class or interface.
     */
    public String getUnqualifiedName() {
        return unqualifiedName;
    }

    /**
     * Sets the unqualified name of the generated class or interface.
     */
    public void setUnqualifiedName(String unqualifiedName) {
        this.unqualifiedName = unqualifiedName;
    }

    /**
     * Returns the {@link <code>JavaCodeFragmentBuilder</code>} that assembles the code for the
     * constant definitions.
     */
    public JavaCodeFragmentBuilder getConstantBuilder() {
        return constantBuilder;
    }

    /**
     * Returns the {@link <code>JavaCodeFragmentBuilder</code>} that assembles the code for the
     * initialisation of the enum.
     */
    public JavaCodeFragmentBuilder getEnumDefinitionBuilder() {
        return enumDefinitionBuilder;
    }

    /**
     * Returns the {@link <code>JavaCodeFragmentBuilder</code>} that assembles the code for the
     * constructor definitions.
     */
    public JavaCodeFragmentBuilder getConstructorBuilder() {
        return constructorBuilder;
    }

    /**
     * Returns the {@link <code>JavaCodeFragmentBuilder</code>} that assembles the code for the
     * member variable definitions.
     */
    public JavaCodeFragmentBuilder getMemberVarBuilder() {
        return memberVariableBuilder;
    }

    /**
     * Returns the {@link <code>JavaCodeFragmentBuilder</code>} that assembles the code for the
     * method definitions.
     */
    public JavaCodeFragmentBuilder getMethodBuilder() {
        return methodBuilder;
    }

    /**
     * Returns the {@link <code>JavaCodeFragmentBuilder</code>} that assembles the code for the java
     * doc of the type of this TypeSection.
     */
    public JavaCodeFragmentBuilder getJavaDocForTypeBuilder() {
        return javaDocForTypeBuilder;
    }
    
}
