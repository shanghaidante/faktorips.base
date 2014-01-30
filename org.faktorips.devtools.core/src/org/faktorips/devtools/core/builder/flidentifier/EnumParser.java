/*******************************************************************************
 * Copyright (c) Faktor Zehn AG. <http://www.faktorzehn.org>
 * 
 * This source code is available under the terms of the AGPL Affero General Public License version 3.
 * 
 * Please see LICENSE.txt for full license terms, including the additional permissions and the
 * possibility of alternative license terms.
 *******************************************************************************/

package org.faktorips.devtools.core.builder.flidentifier;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.ObjectUtils;
import org.eclipse.osgi.util.NLS;
import org.faktorips.datatype.EnumDatatype;
import org.faktorips.devtools.core.builder.flidentifier.ast.EnumClassNode.EnumClass;
import org.faktorips.devtools.core.builder.flidentifier.ast.IdentifierNode;
import org.faktorips.devtools.core.model.ipsobject.IIpsObject;
import org.faktorips.devtools.core.model.ipsproject.IIpsProject;
import org.faktorips.devtools.core.model.productcmpt.IExpression;
import org.faktorips.fl.ExprCompiler;
import org.faktorips.util.message.Message;

/**
 * The parser for enum value identifiers.
 * <p>
 * An enum identifier always consists of two parts. First the enum class name and second the id of
 * the enum value. This parser is able to handle both parts, it depends on the context type which
 * part it tries to parse.
 * 
 * @author dirmeier
 */
public class EnumParser extends AbstractIdentifierNodeParser {

    private final Map<String, EnumDatatype> enumDatatypes;

    /**
     * Creates a new {@link EnumParser} for the specified expression and project
     * 
     * @param expression The expression that holds the identifier that will be parsed by this parser
     * @param ipsProject The {@link IIpsProject} used for searching any {@link IIpsObject}.
     */
    public EnumParser(IExpression expression, IIpsProject ipsProject) {
        super(expression, ipsProject);
        enumDatatypes = createEnumMap();
    }

    private Map<String, EnumDatatype> createEnumMap() {
        EnumDatatype[] enumtypes = getExpression().getEnumDatatypesAllowedInFormula();
        Map<String, EnumDatatype> newEnumDatatypes = new HashMap<String, EnumDatatype>(enumtypes.length);
        for (EnumDatatype enumtype : enumtypes) {
            newEnumDatatypes.put(enumtype.getName(), enumtype);
        }
        return newEnumDatatypes;
    }

    @Override
    protected IdentifierNode parse() {
        if (isContextTypeFormulaType()) {
            return parseEnumClass();
        }
        if (getContextType() instanceof EnumClass) {
            return parseEnumDatatype();
        }
        return null;
    }

    private IdentifierNode parseEnumClass() {
        EnumDatatype enumType = enumDatatypes.get(getIdentifierPart());
        if (enumType != null) {
            return nodeFactory().createEnumClassNode(new EnumClass(enumType));
        }
        return null;
    }

    private IdentifierNode parseEnumDatatype() {
        EnumDatatype enumType = ((EnumClass)getContextType()).getEnumDatatype();
        String[] valueIds = enumType.getAllValueIds(true);
        for (String enumValueName : valueIds) {
            if (ObjectUtils.equals(enumValueName, getIdentifierPart())) {
                return nodeFactory().createEnumValueNode(enumValueName, enumType);
            }
        }
        return nodeFactory()
                .createInvalidIdentifier(
                        Message.newError(
                                ExprCompiler.UNDEFINED_IDENTIFIER,
                                NLS.bind(Messages.EnumParser_msgErrorInvalidEnumValue, getIdentifierPart(),
                                        enumType.getName())));
    }

}
