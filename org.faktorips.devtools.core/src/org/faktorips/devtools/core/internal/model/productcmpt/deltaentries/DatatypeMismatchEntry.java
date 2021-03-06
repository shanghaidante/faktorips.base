/*******************************************************************************
 * Copyright (c) Faktor Zehn AG. <http://www.faktorzehn.org>
 * 
 * This source code is available under the terms of the AGPL Affero General Public License version
 * 3.
 * 
 * Please see LICENSE.txt for full license terms, including the additional permissions and
 * restrictions as well as the possibility of alternative license terms.
 *******************************************************************************/
package org.faktorips.devtools.core.internal.model.productcmpt.deltaentries;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.annotation.Nonnull;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.base.Optional;
import com.google.common.collect.Lists;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.osgi.util.NLS;
import org.faktorips.datatype.ValueDatatype;
import org.faktorips.devtools.core.exception.CoreRuntimeException;
import org.faktorips.devtools.core.internal.model.value.StringValue;
import org.faktorips.devtools.core.internal.model.valueset.EnumValueSet;
import org.faktorips.devtools.core.internal.model.valueset.RangeValueSet;
import org.faktorips.devtools.core.model.IValidationMsgCodesForInvalidValues;
import org.faktorips.devtools.core.model.ipsproject.IIpsProject;
import org.faktorips.devtools.core.model.productcmpt.DeltaType;
import org.faktorips.devtools.core.model.productcmpt.IAttributeValue;
import org.faktorips.devtools.core.model.productcmpt.IConfiguredDefault;
import org.faktorips.devtools.core.model.productcmpt.IConfiguredValueSet;
import org.faktorips.devtools.core.model.productcmpt.IPropertyValue;
import org.faktorips.devtools.core.model.productcmpt.IPropertyValueContainer;
import org.faktorips.devtools.core.model.type.IProductCmptProperty;
import org.faktorips.devtools.core.model.value.IValue;
import org.faktorips.devtools.core.model.valueset.IValueSet;
import org.faktorips.util.functional.Consumer;

public class DatatypeMismatchEntry extends AbstractDeltaEntryForProperty {

    private final List<String> oldValues;
    private final ValueConverter converter;
    private final Consumer<List<String>> valueConsumer;

    /**
     * Sets the input for conversion, which will be done with the {@code fix()} method. Instead of
     * this constructor the {@link #forEachMismatch(List)} factory method should be used to create
     * all DatatypeMismatchEntries for a {@link IPropertyValueContainer}'s properties.
     * 
     * @param propertyValue The product properties.
     * @param oldValues A list of not converted {@link String} values.
     * @param converter The converter with whom the old Values should be modified.
     * @param valueConsumer Adds a transformed list of {@link IValue} to the propertyValue.
     */
    /* private */ DatatypeMismatchEntry(IPropertyValue propertyValue, List<String> oldValues, ValueConverter converter,
            Consumer<List<String>> valueConsumer) {
        super(propertyValue);
        this.oldValues = oldValues;
        this.converter = converter;
        this.valueConsumer = valueConsumer;
    }

    @Override
    public DeltaType getDeltaType() {
        return DeltaType.DATATYPE_MISMATCH;
    }

    @Override
    public String getDescription() {
        return NLS.bind(Messages.DatatypeMismatchEntry_datatypeMissmatchDescription, Joiner.on(", ").join(oldValues), //$NON-NLS-1$
                Joiner.on(", ").join(convertedValues())); //$NON-NLS-1$
    }

    @Override
    public void fix() {
        List<String> converted = convertedValues();
        valueConsumer.accept(converted);
    }

    private List<String> convertedValues() {
        List<String> converted = Lists.transform(oldValues, new Function<String, String>() {

            @Override
            public String apply(String input) {
                return converter.convert(input, getPropertyValue().getIpsProject());
            }
        });
        return converted;
    }

    /**
     * Creates a {@link DatatypeMismatchEntry} for each {@link IPropertyValue} in the given list
     * that has a datatype not matching the corresponding {@link IProductCmptProperty}'s datatype.
     */
    public static List<DatatypeMismatchEntry> forEachMismatch(List<? extends IPropertyValue> values) {
        List<DatatypeMismatchEntry> result = new ArrayList<DatatypeMismatchEntry>();
        for (IPropertyValue propertyValue : values) {
            for (DatatypeMismatchEntry entry : createPossibleMismatch(propertyValue).asSet()) {
                result.add(entry);
            }
        }
        return result;
    }

    private static Optional<DatatypeMismatchEntry> createPossibleMismatch(final IPropertyValue propertyValue) {
        if (isConversionNeeded(propertyValue)) {
            ValueDatatype datatype = findDatatype(propertyValue);
            final ValueConverter converter = ValueConverter.getByTargetType(datatype);
            if (converter != null) {
                Optional<DatatypeMismatch<IPropertyValue>> mismatch = createMismatch(propertyValue);
                return mismatch.transform(new Function<DatatypeMismatch<?>, DatatypeMismatchEntry>() {

                    @Override
                    public DatatypeMismatchEntry apply(@Nonnull DatatypeMismatch<?> mismatch) {
                        return new DatatypeMismatchEntry(propertyValue, mismatch.getValues(), converter,
                                mismatch.getValueConsumer());
                    }
                });
            }
        }
        return Optional.absent();
    }

    private static boolean isConversionNeeded(IPropertyValue attributeValue) {
        try {
            return attributeValue.validate(attributeValue.getIpsProject()).getMessageByCode(
                    IValidationMsgCodesForInvalidValues.MSGCODE_VALUE_IS_NOT_INSTANCE_OF_VALUEDATATYPE) != null;
        } catch (CoreException e) {
            // conversion doesn't make sense in exception case
            return false;
        }
    }

    private static ValueDatatype findDatatype(IPropertyValue attributeValue) {
        try {
            IIpsProject ipsProject = attributeValue.getIpsProject();
            IProductCmptProperty property = attributeValue.findProperty(ipsProject);
            String datatype = property.getPropertyDatatype();
            return ipsProject.findValueDatatype(datatype);
        } catch (CoreException e) {
            throw new CoreRuntimeException(e);
        }
    }

    @SuppressWarnings("unchecked")
    private static <P extends IPropertyValue> Optional<DatatypeMismatch<P>> createMismatch(P propertyValue) {
        if (propertyValue instanceof IAttributeValue) {
            return Optional.of((DatatypeMismatch<P>)new AttributeValueDatatypeMismatch((IAttributeValue)propertyValue));
        } else if (propertyValue instanceof IConfiguredDefault) {
            return Optional
                    .of((DatatypeMismatch<P>)new ConfiguredDefaultDatatypeMismatch((IConfiguredDefault)propertyValue));
        } else if (propertyValue instanceof IConfiguredValueSet) {
            IConfiguredValueSet configuredValueSet = (IConfiguredValueSet)propertyValue;
            IValueSet valueSet = configuredValueSet.getValueSet();
            if (valueSet.isEnum()) {
                return Optional.of((DatatypeMismatch<P>)new EnumValueSetDatatypeMismatch(configuredValueSet));
            } else if (valueSet.isRange()) {
                return Optional.of((DatatypeMismatch<P>)new RangeValueSetDatatypeMismatch(configuredValueSet));
            }
        }
        return Optional.absent();
    }

    private abstract static class DatatypeMismatch<P extends IPropertyValue> {

        private final P propertyValue;

        public DatatypeMismatch(P propertyValue) {
            this.propertyValue = propertyValue;
        }

        public P getPropertyValue() {
            return propertyValue;
        }

        public abstract List<String> getValues();

        public abstract Consumer<List<String>> getValueConsumer();
    }

    private static class AttributeValueDatatypeMismatch extends DatatypeMismatch<IAttributeValue> {

        public AttributeValueDatatypeMismatch(IAttributeValue attributeValue) {
            super(attributeValue);
        }

        @Override
        public List<String> getValues() {
            List<IValue<?>> valueList = getPropertyValue().getValueHolder().getValueList();
            return Lists.transform(valueList, new Function<IValue<?>, String>() {
                @Override
                public String apply(@Nonnull IValue<?> input) {
                    // no usecase for converting international strings
                    return input.getContentAsString();
                }
            });
        }

        @Override
        public Consumer<List<String>> getValueConsumer() {
            return new Consumer<List<String>>() {
                @Override
                public void accept(List<String> t) {
                    List<IValue<?>> newValueList = Lists.transform(t, new Function<String, IValue<?>>() {
                        @Override
                        public IValue<?> apply(String input) {
                            return new StringValue(input);
                        }
                    });
                    getPropertyValue().getValueHolder().setValueList(newValueList);
                }
            };
        }

    }

    private static class ConfiguredDefaultDatatypeMismatch extends DatatypeMismatch<IConfiguredDefault> {

        public ConfiguredDefaultDatatypeMismatch(IConfiguredDefault propertyValue) {
            super(propertyValue);
        }

        @Override
        public List<String> getValues() {
            return Collections.singletonList(getPropertyValue().getValue());
        }

        @Override
        public Consumer<List<String>> getValueConsumer() {
            return new Consumer<List<String>>() {
                @Override
                public void accept(List<String> t) {
                    getPropertyValue().setValue(t.get(0));
                }
            };
        }

    }

    private abstract static class ConfiguredValueSetDatatypeMismatch<S extends IValueSet>
            extends DatatypeMismatch<IConfiguredValueSet> {

        private final S valueSet;

        @SuppressWarnings("unchecked")
        public ConfiguredValueSetDatatypeMismatch(IConfiguredValueSet propertyValue) {
            super(propertyValue);
            valueSet = (S)propertyValue.getValueSet();
        }

        public S getValueSet() {
            return valueSet;
        }

    }

    private static class EnumValueSetDatatypeMismatch extends ConfiguredValueSetDatatypeMismatch<EnumValueSet> {

        public EnumValueSetDatatypeMismatch(IConfiguredValueSet propertyValue) {
            super(propertyValue);
        }

        @Override
        public List<String> getValues() {
            return Arrays.asList(getValueSet().getValues());
        }

        @Override
        public Consumer<List<String>> getValueConsumer() {
            return new Consumer<List<String>>() {
                @Override
                public void accept(List<String> values) {
                    int i = 0;
                    for (String value : values) {
                        getValueSet().setValue(i++, value);
                    }
                }
            };
        }

    }

    private static class RangeValueSetDatatypeMismatch extends ConfiguredValueSetDatatypeMismatch<RangeValueSet> {

        public RangeValueSetDatatypeMismatch(IConfiguredValueSet propertyValue) {
            super(propertyValue);
        }

        @Override
        public List<String> getValues() {
            String lowerBound = getValueSet().getLowerBound();
            String upperBound = getValueSet().getUpperBound();
            String step = getValueSet().getStep();
            return Arrays.asList(lowerBound, upperBound, step);
        }

        @Override
        public Consumer<List<String>> getValueConsumer() {
            return new Consumer<List<String>>() {
                @Override
                public void accept(List<String> t) {
                    getValueSet().setUpperBound(t.get(1));
                    getValueSet().setLowerBound(t.get(0));
                    getValueSet().setStep(t.get(2));
                }
            };
        }

    }
}
