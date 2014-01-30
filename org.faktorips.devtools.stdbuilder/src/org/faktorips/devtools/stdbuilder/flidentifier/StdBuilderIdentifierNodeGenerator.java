/*******************************************************************************
 * Copyright (c) Faktor Zehn AG. <http://www.faktorzehn.org>
 * 
 * This source code is available under the terms of the AGPL Affero General Public License version 3.
 * 
 * Please see LICENSE.txt for full license terms, including the additional permissions and the
 * possibility of alternative license terms.
 *******************************************************************************/

package org.faktorips.devtools.stdbuilder.flidentifier;

import org.faktorips.codegen.JavaCodeFragment;
import org.faktorips.datatype.Datatype;
import org.faktorips.datatype.ListOfTypeDatatype;
import org.faktorips.devtools.core.builder.flidentifier.IdentifierNodeGenerator;
import org.faktorips.devtools.core.builder.flidentifier.IdentifierNodeGeneratorFactory;
import org.faktorips.devtools.core.builder.flidentifier.ast.IdentifierNode;
import org.faktorips.devtools.core.model.ipsobject.IIpsObjectPartContainer;
import org.faktorips.devtools.core.model.ipsproject.IIpsProject;
import org.faktorips.devtools.core.model.type.IType;
import org.faktorips.devtools.stdbuilder.StandardBuilderSet;
import org.faktorips.devtools.stdbuilder.xpand.model.AbstractGeneratorModelNode;
import org.faktorips.fl.CompilationResult;

/**
 * Base class for all java-generating {@link IdentifierNodeGenerator IdentifierNodeGenerators} (in
 * the FIPS standard builder).
 * 
 * @author widmaier
 */
public abstract class StdBuilderIdentifierNodeGenerator extends IdentifierNodeGenerator<JavaCodeFragment> {

    private final StandardBuilderSet builderSet;

    public StdBuilderIdentifierNodeGenerator(IdentifierNodeGeneratorFactory<JavaCodeFragment> factory,
            StandardBuilderSet builderSet) {
        super(factory);
        this.builderSet = builderSet;
    }

    protected <X extends AbstractGeneratorModelNode> X getModelNode(IIpsObjectPartContainer container, Class<X> type) {
        return getBuilderSet().getModelNode(container, type);
    }

    protected StandardBuilderSet getBuilderSet() {
        return builderSet;
    }

    /**
     * Returns the IPS project this generator's {@link StandardBuilderSet} is responsible for.
     */
    protected IIpsProject getIpsProject() {
        return getBuilderSet().getIpsProject();
    }

    /**
     * Returns whether the given {@link CompilationResult compilation result's} code would return a
     * list of objects in contrast to a single object. E.g. a list of policy objects or attribute
     * values instead of a single policy object or attribute value.
     * 
     * @param contextCompilationResult the compilation result whose return type should be tested
     * @return <code>true</code> if the compilation result's datatype is a list datatype,
     *         <code>false</code> otherwise.
     */
    protected boolean isListDatatypeContext(CompilationResult<JavaCodeFragment> contextCompilationResult) {
        return contextCompilationResult.getDatatype() instanceof ListOfTypeDatatype;
    }

    protected IType getContextBasicDatatype(CompilationResult<JavaCodeFragment> contextCompilationResult) {
        if (isListDatatypeContext(contextCompilationResult)) {
            ListOfTypeDatatype listDatatype = (ListOfTypeDatatype)contextCompilationResult.getDatatype();
            return (IType)listDatatype.getBasicDatatype();
        } else {
            return (IType)contextCompilationResult.getDatatype();
        }
    }

    /**
     * Returns the java class name for the given datatype. For policy- and product component types
     * the names of the classes generated by the standard builder are returned.
     */
    protected String getJavaClassName(Datatype datatype) {
        return getBuilderSet().getJavaClassName(datatype, true);
    }

    protected Datatype getBasicDatatype(IdentifierNode node) {
        if (node.isListOfTypeDatatype()) {
            ListOfTypeDatatype listDatatype = (ListOfTypeDatatype)node.getDatatype();
            return listDatatype.getBasicDatatype();
        } else {
            return node.getDatatype();
        }
    }

}
