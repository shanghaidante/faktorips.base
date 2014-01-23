/*******************************************************************************
 * Copyright (c) Faktor Zehn AG. <http://www.faktorzehn.org>
 * 
 * This source code is available under the terms of the AGPL Affero General Public License version 3
 * and if and when this source code belongs to the faktorips-runtime or faktorips-valuetype
 * component under the terms of the LGPL Lesser General Public License version 3.
 * 
 * Please see LICENSE.txt for full license terms, including the additional permissions and the
 * possibility of alternative license terms.
 *******************************************************************************/

package org.faktorips.devtools.core.internal.model.valueset;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.osgi.util.NLS;
import org.faktorips.datatype.NumericDatatype;
import org.faktorips.datatype.ValueDatatype;
import org.faktorips.devtools.core.exception.CoreRuntimeException;
import org.faktorips.devtools.core.internal.model.ipsobject.DescriptionHelper;
import org.faktorips.devtools.core.model.ipsproject.IIpsProject;
import org.faktorips.devtools.core.model.valueset.IRangeValueSet;
import org.faktorips.devtools.core.model.valueset.IValueSet;
import org.faktorips.devtools.core.model.valueset.IValueSetOwner;
import org.faktorips.devtools.core.model.valueset.ValueSetType;
import org.faktorips.runtime.internal.ValueToXmlHelper;
import org.faktorips.util.message.Message;
import org.faktorips.util.message.MessageList;
import org.faktorips.util.message.ObjectProperty;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * A value set that describes a range with a lower and an upper bound, e.g. 100-200. Lower and upper
 * bound are part of the range. If lower bound or upper bound contain an empty string, the range is
 * unbounded. The range has an optional step attribute to define that only the values where
 * <code>((value-lower) mod step)== 0</code> holds true. E.g. 100-200 with step 10 defines the
 * values 100, 110, 120, ... 200.
 * 
 * @author Jan Ortmann
 */
public class RangeValueSet extends ValueSet implements IRangeValueSet {

    public static final String XML_TAG_RANGE = "Range"; //$NON-NLS-1$

    private String lowerBound;
    private String upperBound;
    private String step;

    /**
     * Flag that indicates whether this range contains <code>null</code> or not.
     */
    private boolean containsNull;

    /**
     * Creates an unbounded range with no step.
     */
    public RangeValueSet(IValueSetOwner parent, String partId) {
        this(parent, partId, null, null, null);
    }

    /**
     * Creates a range with the given bounds and and step.
     */
    public RangeValueSet(IValueSetOwner parent, String partId, String lower, String upper, String step) {
        super(ValueSetType.RANGE, parent, partId);
        lowerBound = lower;
        upperBound = upper;
        this.step = step;
    }

    /**
     * Sets the lower bound. An empty string means that the range is unbouned.
     * 
     * @throws NullPointerException if lowerBound is <code>null</code>.
     */
    @Override
    public void setLowerBound(String lowerBound) {
        String oldBound = this.lowerBound;
        this.lowerBound = lowerBound;
        valueChanged(oldBound, lowerBound);
    }

    /**
     * Sets the step. An empty string means that no step exists and all possible values in the range
     * are valid.
     * 
     * @throws NullPointerException if step is <code>null</code>.
     */
    @Override
    public void setStep(String step) {
        String oldStep = this.step;
        this.step = step;
        valueChanged(oldStep, step);
    }

    /**
     * Sets the upper bound. An empty string means that the range is unbounded.
     * 
     * @throws NullPointerException if upperBound is <code>null</code>.
     */
    @Override
    public void setUpperBound(String upperBound) {
        String oldBound = this.upperBound;
        this.upperBound = upperBound;
        valueChanged(oldBound, upperBound);
    }

    /**
     * Returns the lower bound of the range
     */
    @Override
    public String getLowerBound() {
        return lowerBound;
    }

    /**
     * Returns the upper bound of the range
     */
    @Override
    public String getUpperBound() {
        return upperBound;
    }

    /**
     * Returns the step of the range
     */
    @Override
    public String getStep() {
        return step;
    }

    @Override
    public boolean containsValue(String value, IIpsProject ipsProject) throws CoreException {
        ValueDatatype datatype = findValueDatatype(ipsProject);
        if (!isValid(ipsProject)) {
            return false;
        }
        return checkValueInRange(value, datatype);
    }

    /**
     * The value is in the range in one of the following cases:
     * <ul>
     * <li>The value is null (according to definition of the datatype) and the range contains the
     * null value</li>
     * <li>The range is abstract and hence all values are allowed (except null if
     * {@link #isContainingNull()} is false)</li>
     * <li>The value lies between the upper and the lower value</li>
     * </ul>
     */
    private boolean checkValueInRange(String value, ValueDatatype datatype) {
        try {
            if (datatype.isNull(value)) {
                return isContainingNull();
            }
            if (isAbstract()) {
                return true;
            }
            if ((!datatype.isNull(getLowerBound()) && datatype.compare(getLowerBound(), value) > 0)
                    || (!datatype.isNull(getUpperBound()) && datatype.compare(getUpperBound(), value) < 0)) {
                return false;
            }
            return isValueMatchStep(value, datatype);
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    /**
     * if the lower bound is set, the value to check is not the real value but the value reduced by
     * the lower bound! In a range from 1-5, Step 2 the values 1, 3 and 5 are valid, not 2 and 4.
     */
    private boolean isValueMatchStep(String value, ValueDatatype datatype) {
        String diff = value;
        NumericDatatype numDatatype = (NumericDatatype)datatype;
        if (!datatype.isNull(getStep())) {
            diff = numDatatype.subtract(value, getLowerBound());
            return numDatatype.divisibleWithoutRemainder(diff, getStep());
        } else {
            return true;
        }
    }

    @Override
    public boolean containsValueSet(IValueSet subset) {
        ValueDatatype datatype = getValueDatatype();
        ValueDatatype subDatatype = ((ValueSet)subset).getValueDatatype();
        if (!datatype.equals(subDatatype)) {
            return false;
        }
        if (!checkValidRanges(subset)) {
            return false;
        }

        return checkIsRangeSubset((IRangeValueSet)subset, (NumericDatatype)datatype);
    }

    private boolean checkValidRanges(IValueSet subset) {
        try {
            if (!isValid(getIpsProject())) {
                return false;
            }
            if (!(subset instanceof RangeValueSet) || !subset.isValid(getIpsProject())) {
                return false;
            }
        } catch (CoreException e) {
            throw new CoreRuntimeException(e);
        }
        return true;
    }

    /**
     * Another range is a subset of this range if the following conditions match:
     * <ul>
     * <li>An abstract valueset is considered containing all values and thus all non-abstract
     * rangeValueSets.</li>
     * </li>If this range is not abstract, an other abstract range cannot be a subset of this
     * range</li>
     * <li>The other range is no subset if it contains null but this range does not</li>
     * <li>If both ranges are not abstract, the other range is a subset if every value that is
     * allowed in the other range is also allowed in this range, according to lower bound, upper
     * bound and step</li>
     * </ul>
     */
    private boolean checkIsRangeSubset(IRangeValueSet subRange, NumericDatatype datatype) {
        if (isAbstract()) {
            return true;
        }
        if (subRange.isAbstract()) {
            return false;
        }

        if (!isContainingNull() && subRange.isContainingNull()) {
            return false;
        }

        String lower = getLowerBound();
        String subLower = subRange.getLowerBound();
        if (isMatchLowerBound(datatype, lower, subLower)) {
            return false;
        }

        String upper = getUpperBound();
        String subUpper = subRange.getUpperBound();
        if (isMatchUpperBound(upper, subUpper, datatype)) {
            return false;
        }

        return isSubrangeMatchStep(subRange, datatype);
    }

    private boolean isMatchLowerBound(NumericDatatype datatype, String lower, String subLower) {
        return !datatype.isNull(lower) && (datatype.isNull(subLower) || datatype.compare(lower, subLower) > 0);
    }

    private boolean isMatchUpperBound(String upper, String subUpper, NumericDatatype datatype) {
        return !datatype.isNull(upper) && (datatype.isNull(subUpper) || datatype.compare(upper, subUpper) < 0);
    }

    private boolean isSubrangeMatchStep(IRangeValueSet other, NumericDatatype datatype) {
        String subStep = other.getStep();

        if (datatype.isNull(step) && !datatype.isNull(subStep)) {
            return true;
        }
        if (datatype.areValuesEqual(step, subStep)) {
            return true;
        }
        if (!datatype.divisibleWithoutRemainder(subStep, step)) {
            return false;
        }

        String lower = getLowerBound();
        String subLower = other.getLowerBound();
        String upper = getUpperBound();
        String subUpper = other.getUpperBound();
        String diffLower = datatype.subtract(subLower, lower);
        if (!datatype.divisibleWithoutRemainder(diffLower, step)) {
            return false;
        }

        String diffUpper = datatype.subtract(upper, subUpper);
        if (!datatype.divisibleWithoutRemainder(diffUpper, step)) {
            return false;
        }

        return true;
    }

    @Override
    public void validateThis(MessageList list, IIpsProject ipsProject) throws CoreException {
        ObjectProperty parentObjectProperty = new ObjectProperty(getValueSetOwner(), IValueSetOwner.PROPERTY_VALUE_SET);
        ObjectProperty lowerBoundProperty = new ObjectProperty(this, PROPERTY_LOWERBOUND);
        ObjectProperty upperBoundProperty = new ObjectProperty(this, PROPERTY_UPPERBOUND);
        ObjectProperty stepProperty = new ObjectProperty(this, PROPERTY_STEP);
        ValueDatatype datatype = getValueDatatype();
        if (datatype == null) {
            String text = Messages.Range_msgUnknownDatatype;
            list.add(new Message(MSGCODE_UNKNOWN_DATATYPE, text, Message.ERROR, parentObjectProperty,
                    lowerBoundProperty, upperBoundProperty, stepProperty));
            return;
        }
        datatype = checkDatatypePrimitiv(list, datatype);

        validateParsable(datatype, getLowerBound(), list, this, PROPERTY_LOWERBOUND);
        validateParsable(datatype, getUpperBound(), list, this, PROPERTY_UPPERBOUND);
        boolean stepParsable = validateParsable(datatype, getStep(), list, this, PROPERTY_STEP);

        if (list.getSeverity() == Message.ERROR) {
            return;
        }

        NumericDatatype numDatatype = getAndValidateNumericDatatype(datatype, list);

        String lowerValue = getLowerBound();
        String upperValue = getUpperBound();
        if (!datatype.isNull(lowerValue) && !datatype.isNull(upperValue)) {
            // range is not unbounded on one side
            if (datatype.compare(lowerValue, upperValue) > 0) {
                String text = Messages.Range_msgLowerboundGreaterUpperbound;
                list.add(new Message(MSGCODE_LBOUND_GREATER_UBOUND, text, Message.ERROR, parentObjectProperty,
                        lowerBoundProperty, upperBoundProperty));
                return;
            }
        }

        if (numDatatype != null && stepParsable && isNotEmpty(upperValue, lowerValue, getStep())) {
            String range = numDatatype.subtract(upperValue, lowerValue);
            if (!numDatatype.divisibleWithoutRemainder(range, getStep())) {
                String msg = NLS.bind(Messages.RangeValueSet_msgStepRangeMismatch, new String[] { lowerValue,
                        upperValue, getStep() });
                list.add(new Message(MSGCODE_STEP_RANGE_MISMATCH, msg, Message.ERROR, parentObjectProperty,
                        lowerBoundProperty, upperBoundProperty, stepProperty));
            }
        }
    }

    private ValueDatatype checkDatatypePrimitiv(MessageList list, ValueDatatype datatype) {
        if (datatype.isPrimitive()) {
            if (isContainingNull()) {
                String text = Messages.RangeValueSet_msgNullNotSupported;
                list.add(new Message(MSGCODE_NULL_NOT_SUPPORTED, text, Message.ERROR, this, PROPERTY_CONTAINS_NULL));
            }
            // even if the basic datatype is a primitve, null is allowed for upper, lower bound and
            // step.
            return datatype.getWrapperType();
        }
        return datatype;
    }

    private boolean isNotEmpty(String upperValue, String lowerValue, String step) {
        return !StringUtils.isEmpty(upperValue) && !StringUtils.isEmpty(lowerValue) && !StringUtils.isEmpty(step);
    }

    private NumericDatatype getAndValidateNumericDatatype(ValueDatatype datatype, MessageList list) {
        if (datatype instanceof NumericDatatype) {
            return (NumericDatatype)datatype;
        }

        String text = Messages.RangeValueSet_msgDatatypeNotNumeric;
        list.add(new Message(MSGCODE_NOT_NUMERIC_DATATYPE, text, Message.ERROR, this));

        return null;
    }

    private boolean validateParsable(ValueDatatype datatype,
            String value,
            MessageList list,
            Object invalidObject,
            String property) {
        if (!datatype.isParsable(value)) {
            String msg = NLS.bind(Messages.Range_msgPropertyValueNotParsable, value, datatype.getName());
            addMsg(list, MSGCODE_VALUE_NOT_PARSABLE, msg, invalidObject, property);
            return false;
        }
        return true;
    }

    @Override
    public ValueSetType getValueSetType() {
        return ValueSetType.RANGE;
    }

    @Override
    public String toString() {
        if (isAbstract()) {
            return super.toString() + "(abstract)"; //$NON-NLS-1$
        }
        return super.toString() + ":" + toShortString(); //$NON-NLS-1$
    }

    @Override
    public String toShortString() {
        StringBuffer sb = new StringBuffer();
        sb.append(RANGE_VALUESET_START);
        sb.append((lowerBound == null ? "unlimited" : lowerBound)); //$NON-NLS-1$
        sb.append(RANGE_VALUESET_POINTS);
        sb.append((upperBound == null ? "unlimited" : upperBound)); //$NON-NLS-1$
        if (step != null) {
            sb.append(RANGE_STEP_SEPERATOR);
            sb.append(step);
        }
        sb.append(RANGE_VALUESET_END);
        return sb.toString();
    }

    @Override
    protected void initPropertiesFromXml(Element element, String id) {
        super.initPropertiesFromXml(element, id);
        Element el = DescriptionHelper.getFirstNoneDescriptionElement(element);
        if (el.hasAttribute(PROPERTY_LOWERBOUND)) {
            // old format prior to 1.0.0.rc2
            lowerBound = el.getAttribute(PROPERTY_LOWERBOUND);
            if ("".equals(lowerBound)) { //$NON-NLS-1$
                lowerBound = null;
            }
            upperBound = el.getAttribute(PROPERTY_UPPERBOUND);
            if ("".equals(upperBound)) { //$NON-NLS-1$
                upperBound = null;
            }
            step = el.getAttribute(PROPERTY_STEP);
            if ("".equals(step)) { //$NON-NLS-1$
                step = null;
            }
        } else {
            // new format sind 1.0.0.rc2
            lowerBound = ValueToXmlHelper.getValueFromElement(el, StringUtils.capitalize(PROPERTY_LOWERBOUND));
            upperBound = ValueToXmlHelper.getValueFromElement(el, StringUtils.capitalize(PROPERTY_UPPERBOUND));
            step = ValueToXmlHelper.getValueFromElement(el, StringUtils.capitalize(PROPERTY_STEP));
        }

        containsNull = Boolean.valueOf(el.getAttribute(PROPERTY_CONTAINS_NULL)).booleanValue();
    }

    @Override
    protected void propertiesToXml(Element element) {
        super.propertiesToXml(element);
        Document doc = element.getOwnerDocument();
        Element tagElement = doc.createElement(XML_TAG_RANGE);
        tagElement.setAttribute(PROPERTY_CONTAINS_NULL, Boolean.toString(containsNull));
        ValueToXmlHelper.addValueToElement(lowerBound, tagElement, StringUtils.capitalize(PROPERTY_LOWERBOUND));
        ValueToXmlHelper.addValueToElement(upperBound, tagElement, StringUtils.capitalize(PROPERTY_UPPERBOUND));
        ValueToXmlHelper.addValueToElement(step, tagElement, StringUtils.capitalize(PROPERTY_STEP));
        element.appendChild(tagElement);
    }

    @Override
    public IValueSet copy(IValueSetOwner parent, String id) {
        RangeValueSet retValue = new RangeValueSet(parent, id);

        retValue.lowerBound = lowerBound;
        retValue.upperBound = upperBound;
        retValue.step = step;
        retValue.containsNull = containsNull;

        return retValue;
    }

    @Override
    public void copyPropertiesFrom(IValueSet source) {
        RangeValueSet set = (RangeValueSet)source;
        lowerBound = set.lowerBound;
        upperBound = set.upperBound;
        step = set.step;
        containsNull = set.containsNull;
        objectHasChanged();
    }

    /**
     * @deprecated Use {@link #isContainingNull()} instead
     */
    @Deprecated
    @Override
    public boolean getContainsNull() {
        return isContainingNull();
    }

    @Override
    public boolean isContainingNull() {
        return containsNull;
    }

    @Override
    public void setContainsNull(boolean containsNull) {
        boolean old = this.containsNull;
        this.containsNull = containsNull;
        valueChanged(old, containsNull);
    }

}
