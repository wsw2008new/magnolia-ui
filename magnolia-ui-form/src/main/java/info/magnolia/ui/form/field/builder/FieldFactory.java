/**
 * This file Copyright (c) 2013 Magnolia International
 * Ltd.  (http://www.magnolia-cms.com). All rights reserved.
 *
 *
 * This file is dual-licensed under both the Magnolia
 * Network Agreement and the GNU General Public License.
 * You may elect to use one or the other of these licenses.
 *
 * This file is distributed in the hope that it will be
 * useful, but AS-IS and WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE, TITLE, or NONINFRINGEMENT.
 * Redistribution, except as permitted by whichever of the GPL
 * or MNA you select, is prohibited.
 *
 * 1. For the GPL license (GPL), you can redistribute and/or
 * modify this file under the terms of the GNU General
 * Public License, Version 3, as published by the Free Software
 * Foundation.  You should have received a copy of the GNU
 * General Public License, Version 3 along with this program;
 * if not, write to the Free Software Foundation, Inc., 51
 * Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * 2. For the Magnolia Network Agreement (MNA), this file
 * and the accompanying materials are made available under the
 * terms of the MNA which accompanies this distribution, and
 * is available at http://www.magnolia-cms.com/mna.html
 *
 * Any modifications to this file must keep this entire header
 * intact.
 *
 */
package info.magnolia.ui.form.field.builder;

import info.magnolia.objectfactory.ComponentProvider;
import info.magnolia.ui.form.field.definition.FieldDefinition;
import info.magnolia.ui.form.field.validation.ValidatorFieldFactory;
import info.magnolia.ui.api.builder.DefinitionToImplementationMapping;
import info.magnolia.ui.api.builder.MappingFactoryBase;

import java.io.Serializable;

import javax.inject.Inject;

import com.vaadin.data.Item;

/**
 * Factory for creating DialogField instances using an internal set of mappings connecting a {@link info.magnolia.ui.form.field.definition.FieldDefinition} class with a {@link FieldBuilder} class.
 *
 * @see FieldDefinition
 * @see FieldBuilder
 */
public class FieldFactory extends MappingFactoryBase<FieldDefinition, FieldBuilder> implements Serializable {

    private ValidatorFieldFactory validatorFieldFactory;

    @Inject
    public FieldFactory(ComponentProvider componentProvider, DialogFieldRegistry dialogFieldRegistry, ValidatorFieldFactory validatorFieldFactory) {
        super(componentProvider);
        this.validatorFieldFactory = validatorFieldFactory;
        for (DefinitionToImplementationMapping<FieldDefinition, FieldBuilder> definitionToImplementationMapping : dialogFieldRegistry.getDefinitionToImplementationMappings()) {
            addMapping(definitionToImplementationMapping.getDefinition(), definitionToImplementationMapping.getImplementation());
        }
    }

    public FieldBuilder create(FieldDefinition definition, Item item, Object... parameters) {
        FieldBuilder fieldBuilder = super.create(definition, item, parameters);
        fieldBuilder.setValidatorFieldFactory(validatorFieldFactory);
        return fieldBuilder;
    }
}