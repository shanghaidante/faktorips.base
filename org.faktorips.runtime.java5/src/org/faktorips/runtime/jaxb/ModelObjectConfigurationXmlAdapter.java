/*******************************************************************************
 * Copyright (c) Faktor Zehn AG. <http://www.faktorzehn.org>
 * 
 * This source code is available under the terms of the AGPL Affero General Public License version
 * 3.
 * 
 * Please see LICENSE.txt for full license terms, including the additional permissions and
 * restrictions as well as the possibility of alternative license terms.
 *******************************************************************************/

package org.faktorips.runtime.jaxb;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import org.faktorips.runtime.IProductComponent;
import org.faktorips.runtime.IRuntimeRepository;
import org.faktorips.runtime.internal.ModelObjectConfiguration;

/**
 * Custom JAXB marshaling/unmarshaling for {@link ModelObjectConfiguration} instances.
 * <p>
 * When marshaling/unmarshaling a configurable policy component (and thus a
 * {@link ModelObjectConfiguration}), the respective product component is preserved in XML by the
 * means of the product component ID.
 */
public class ModelObjectConfigurationXmlAdapter extends XmlAdapter<String, ModelObjectConfiguration> {

    private IRuntimeRepository repository;

    public ModelObjectConfigurationXmlAdapter() {
        // nothing to do
    }

    public ModelObjectConfigurationXmlAdapter(IRuntimeRepository repository) {
        this.repository = repository;
    }

    /**
     * Returns the ID for the specified product component.
     */
    @Override
    public String marshal(ModelObjectConfiguration config) throws Exception {
        if (config == null) {
            return null;
        }
        return config.getProductComponent().getId();
    }

    /**
     * Returns the product component for the specified ID.
     */
    @Override
    public ModelObjectConfiguration unmarshal(String id) throws Exception {
        IProductComponent productComponent = getProductComponentFor(id);
        return new ModelObjectConfiguration(productComponent);
    }

    private IProductComponent getProductComponentFor(String id) {
        IProductComponent productComponent;
        if (id == null) {
            return null;
        } else {
            productComponent = repository.getProductComponent(id);
        }
        return productComponent;
    }

}
