/*******************************************************************************
 * Copyright (c) Faktor Zehn AG. <http://www.faktorzehn.org>
 * 
 * This source code is available under the terms of the AGPL Affero General Public License version
 * 3.
 * 
 * Please see LICENSE.txt for full license terms, including the additional permissions and
 * restrictions as well as the possibility of alternative license terms.
 *******************************************************************************/

package org.faktorips.codegen.dthelpers;

import org.faktorips.codegen.DatatypeHelper;
import org.faktorips.codegen.JavaCodeFragment;
import org.faktorips.datatype.Datatype;
import org.faktorips.datatype.ValueDatatype;
import org.faktorips.runtime.internal.StringUtils;
import org.faktorips.util.ArgumentCheck;
import org.faktorips.valueset.OrderedValueSet;

/**
 * Abstract base class for datatype helpers.
 * 
 * @author Jan Ortmann
 */
public abstract class AbstractDatatypeHelper implements DatatypeHelper {

    private Datatype datatype;

    /**
     * Constructs a new helper.
     */
    public AbstractDatatypeHelper() {
        // Provides default constructor
    }

    /**
     * Constructs a new helper for the given datatype.
     */
    public AbstractDatatypeHelper(Datatype datatype) {
        ArgumentCheck.notNull(datatype);
        this.datatype = datatype;
    }

    public Datatype getDatatype() {
        return datatype;
    }

    public void setDatatype(Datatype datatype) {
        this.datatype = datatype;
    }

    /**
     * This method is supposed to be overridden by subclasses.
     * <p>
     * It is used within the <code>newInstanceFromExpression(String)</code> method. It returns a
     * <code>JavaCodeFragment</code> with sourcecode that creates an instance of the datatype's Java
     * class with the given expression.
     * <p>
     * If the expression is <code>null</code> the fragment's sourcecode is either the String "null"
     * or the sourcecode to get an instance of the appropriate null object. Preconditions:
     * Expression may not be null or empty. When evaluated the expression must return a String.
     */
    protected abstract JavaCodeFragment valueOfExpression(String expression);

    public JavaCodeFragment newInstanceFromExpression(String expression) {
        return newInstanceFromExpression(expression, true);
    }

    public JavaCodeFragment newInstanceFromExpression(String expression, boolean checkForNull) {
        if (expression == null || expression.length() == 0) {
            return nullExpression();
        }
        if (expression.startsWith("(")) { //$NON-NLS-1$
            expression = '(' + expression + ')';
        }
        if (!checkForNull) {
            return valueOfExpression(expression);
        }
        return generateNewInstanceWithStringUtils(expression);
    }

    private JavaCodeFragment generateNewInstanceWithStringUtils(String expression) {
        JavaCodeFragment fragment = new JavaCodeFragment();
        fragment.appendClassName(StringUtils.class).append(".isEmpty(") //$NON-NLS-1$
                .append(expression).append(") ? "); //$NON-NLS-1$
        fragment.append(nullExpression());
        fragment.append(" : "); //$NON-NLS-1$
        fragment.append(valueOfExpression(expression));
        return fragment;
    }

    public String getJavaClassName() {
        return datatype.getJavaClassName();
    }

    public String getRangeJavaClassName(boolean useTypesafeCollections) {
        return null;
    }

    public JavaCodeFragment newRangeInstance(JavaCodeFragment lowerBoundExp,
            JavaCodeFragment upperBoundExp,
            JavaCodeFragment stepExp,
            JavaCodeFragment containsNullExp,
            boolean useTypesafeCollections) {

        return null;
    }

    /**
     * {@inheritDoc}
     * 
     * Code sample
     * 
     * <pre>
     * new DefaultEnumValueSet(new GeneratedGender[] { GeneratedGender.getGeneratedGender(new Integer(1)),
     *         GeneratedGender.getGeneratedGender(new Integer(2)), GeneratedGender.getGeneratedGender(null) }, true,
     *         GeneratedGender.getGeneratedGender(null));
     * </pre>
     * 
     * Java 5 code sample
     * 
     * <pre>
     *  (EnumValueSet)new DefaultEnumValueSet&lt;GeneratedGender&gt;(
     *      true, 
     *      GeneratedGender.getGeneratedGender(null),
     *      GeneratedGender.getGeneratedGender(new Integer(1)), 
     *      GeneratedGender.getGeneratedGender(new Integer(2)));
     * </pre>
     */
    public JavaCodeFragment newEnumValueSetInstance(String[] values,
            boolean containsNull,
            boolean useTypesafeCollections) {

        JavaCodeFragment frag = new JavaCodeFragment();
        frag.append("new "); //$NON-NLS-1$
        frag.appendClassName(OrderedValueSet.class);
        frag.append("<"); //$NON-NLS-1$
        frag.appendClassName(getJavaClassName());
        frag.append(">("); //$NON-NLS-1$
        frag.append(containsNull);
        frag.append(", "); //$NON-NLS-1$
        frag.append(newInstance(null));
        for (String value : values) {
            frag.append(", "); //$NON-NLS-1$
            frag.append(newInstance(value));
        }
        frag.appendln(")"); //$NON-NLS-1$
        return frag;
    }

    public JavaCodeFragment newEnumValueSetInstance(JavaCodeFragment valueCollection,
            JavaCodeFragment containsNullExpression,
            boolean useTypesafeCollections) {

        JavaCodeFragment frag = new JavaCodeFragment();
        frag.append("new "); //$NON-NLS-1$
        frag.appendClassName(OrderedValueSet.class);
        frag.append("<"); //$NON-NLS-1$
        frag.appendClassName(getJavaClassName());
        frag.append(">"); //$NON-NLS-1$
        frag.append("("); //$NON-NLS-1$
        frag.append(valueCollection);
        frag.append(", "); //$NON-NLS-1$
        frag.append(containsNullExpression);
        frag.append(", "); //$NON-NLS-1$
        frag.append(nullExpression());
        frag.appendln(")"); //$NON-NLS-1$
        return frag;
    }

    public JavaCodeFragment referenceOrSafeCopyIfNeccessary(String expression) {
        if (datatype.isValueDatatype() && ((ValueDatatype)datatype).isMutable()) {
            return newSafeCopy(expression);
        }
        return new JavaCodeFragment(expression);
    }

    /**
     * Helpers for immutable datatypes must override this method to create a copy of the value given
     * in the expression.
     * 
     * @param expression The expression of which you want to get the new safe copy code fragment
     *            from
     */
    protected JavaCodeFragment newSafeCopy(String expression) {
        throw new RuntimeException("The DatatypeHelper for datatype " + datatype //$NON-NLS-1$
                + " does not override the method newSafeCopy!"); //$NON-NLS-1$
    }

    public JavaCodeFragment getToStringExpression(String fieldName) {
        JavaCodeFragment fragment = new JavaCodeFragment();
        fragment.append(fieldName);
        fragment.append("==null?null:"); //$NON-NLS-1$
        fragment.append(fieldName);
        fragment.append(".toString()"); //$NON-NLS-1$
        return fragment;
    }

    /**
     * Returns {@code "null"}.
     * 
     * {@inheritDoc}
     */
    public JavaCodeFragment nullExpression() {
        return new JavaCodeFragment("null"); //$NON-NLS-1$
    }

}
