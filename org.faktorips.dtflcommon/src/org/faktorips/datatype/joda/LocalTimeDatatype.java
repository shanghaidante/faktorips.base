package org.faktorips.datatype.joda;

import org.faktorips.datatype.Datatype;
import org.faktorips.datatype.ValueClassNameDatatype;
import org.faktorips.datatype.ValueDatatype;

/**
 * {@link Datatype} for {@code org.joda.time.LocalTime}.
 */
public class LocalTimeDatatype extends ValueClassNameDatatype {

    public static final String ORG_JODA_TIME_LOCAL_TIME = "org.joda.time.LocalTime"; //$NON-NLS-1$
    public static final ValueDatatype DATATYPE = new LocalTimeDatatype();

    public LocalTimeDatatype() {
        super(ORG_JODA_TIME_LOCAL_TIME);
    }

    @Override
    public Object getValue(String value) {
        return value;
    }

    public boolean supportsCompare() {
        return true;
    }

}