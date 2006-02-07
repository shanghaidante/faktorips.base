package org.faktorips.devtools.core.model.productcmpttype;

import org.eclipse.core.runtime.CoreException;
import org.faktorips.devtools.core.model.IIpsObject;
import org.faktorips.devtools.core.model.pctype.IAttribute;
import org.faktorips.devtools.core.model.pctype.IPolicyCmptType;

/**
 * A product component type. Currently the product component type represents a filtered view
 * on the policy component type that gives access to product relevant information.
 * 
 * @author Jan Ortmann
 */
public interface IProductCmptType extends IIpsObject {

	/**
	 * Returns <code>true</code> if the type is abstract, <code>false</code> otherwise.
	 */
	public boolean isAbstract();
	
	/**
	 * Returns the qualified name of the policy component type this is a 
	 * product component type for.
	 */
	public String getPolicyCmptyType();
	
	/**
	 * Returns the policy component type this is a product component type for
	 * or <code>null</code> if the policy component type can't be found.
	 * 
	 * @throws CoreException if an erros occurs while searching for the type.
	 */
	public IPolicyCmptType findPolicyCmptyType() throws CoreException;
	
    /**
     * Returns the type's supertype if the type is based on a supertype and the supertype can be found
     * on the project's ips object path. Returns <code>null</code> if either this type is not based on
     * a supertype or the supertype can't be found on the project's ips object path. 
     *
     * @throws CoreException if an error occurs while searching for the supertype.
	 */
	public IProductCmptType findSupertype() throws CoreException;
	
	/**
	 * Returns the relations defined for this type or an empty array is no relation
	 * is defined. Note that computed or derived attributes are not returned as these 
	 * correspond to methods on the product side.
	 */
	public IAttribute[] getAttributes();

	/**
	 * Returns the relations defined for this type or an empty array is no relation
	 * is defined.
	 */
	public IProductCmptTypeRelation[] getRelations();
	
    /**
     * Returns the first relation with the indicated name or <code>null</code> if
     * no such relation exists.
     * <p>
     * Note that a relation's name is equal to it's target role singular, so you
     * can also use the target role singular as parameter.
     * 
     * @throws NullPointerException if name is <code>null</code>.
     */
	public IProductCmptTypeRelation getRelation(String relationName);
	
}
