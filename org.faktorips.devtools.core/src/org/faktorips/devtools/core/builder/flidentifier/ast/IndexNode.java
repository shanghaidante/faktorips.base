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

package org.faktorips.devtools.core.builder.flidentifier.ast;

import org.eclipse.jface.text.Region;
import org.faktorips.datatype.Datatype;
import org.faktorips.devtools.core.model.type.IType;

/**
 * The index node is a special node that follows an {@link AssociationNode}. It represents an
 * identifier part that was suffixed with an index access. The resulting {@link Datatype} will
 * always be a subclass of {@link IType}.
 * 
 * @author dirmeier
 */
public class IndexNode extends IdentifierNode {

    private final int index;

    IndexNode(int index, IType targetType, Region region) {
        super(targetType, false, region);
        this.index = index;
    }

    public int getIndex() {
        return index;
    }

}
