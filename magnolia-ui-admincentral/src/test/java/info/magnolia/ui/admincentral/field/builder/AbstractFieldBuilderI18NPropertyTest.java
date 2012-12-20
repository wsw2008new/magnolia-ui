/**
 * This file Copyright (c) 2012 Magnolia International
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
package info.magnolia.ui.admincentral.field.builder;

import java.util.Locale;

import javax.jcr.Node;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.assertEquals;

import com.vaadin.ui.Field;

import info.magnolia.cms.i18n.LocaleDefinition;
import info.magnolia.context.MgnlContext;
import info.magnolia.ui.model.field.definition.ConfiguredFieldDefinition;
import info.magnolia.ui.model.field.definition.FieldDefinition;
import info.magnolia.ui.vaadin.integration.jcr.JcrNodeAdapter;

/**
 * Main testcase for {@link AbstractFieldBuilder} i18n property.
 */
public class AbstractFieldBuilderI18NPropertyTest extends AbstractBuilderTest<ConfiguredFieldDefinition> {
    private AbstractFieldBuilder<FieldDefinition> abstractDialogField;


    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();
        i18nContentSupport.addLocale(LocaleDefinition.make("de", null, true));
        i18nContentSupport.addLocale(LocaleDefinition.make("it", null, false));
        i18nContentSupport.addLocale(LocaleDefinition.make("fr", null, true));
        i18nContentSupport.addLocale(LocaleDefinition.make("fr", "CH", true));
    }

    @Test
    public void i18nPropertyNotDefined_CurrentIsDefault() throws Exception{
        // GIVEN
        abstractDialogField = new AbstractFieldBuilderTest.TestFormField(definition, baseItem);
        abstractDialogField.setI18nContentSupport(i18nContentSupport);
        Field field = abstractDialogField.getField();
        field.setValue("new Value");
        // WHEN
        Node res = ((JcrNodeAdapter)baseItem).getNode();

        // THEN
        assertEquals(true, res.hasProperty(propertyName));
    }

    @Test
    public void i18nPropertyNotDefined_CurrentIsNotDefault() throws Exception{
        // GIVEN
        MgnlContext.getInstance().setLocale(Locale.FRENCH);
        abstractDialogField = new AbstractFieldBuilderTest.TestFormField(definition, baseItem);
        abstractDialogField.setI18nContentSupport(i18nContentSupport);
        Field field = abstractDialogField.getField();
        field.setValue("new Value");
        // WHEN
        Node res = ((JcrNodeAdapter)baseItem).getNode();

        // THEN
        // Should not be sufixed by _fr --> setI18n(false)
        assertEquals(true, res.hasProperty(propertyName));
    }

    @Test
    public void i18nPropertyDefined_CurrentIsDefault() throws Exception{
        // GIVEN
        this.definition.setI18n(true);
        abstractDialogField = new AbstractFieldBuilderTest.TestFormField(definition, baseItem);
        abstractDialogField.setI18nContentSupport(i18nContentSupport);
        Field field = abstractDialogField.getField();
        field.setValue("new Value");
        // WHEN
        Node res = ((JcrNodeAdapter)baseItem).getNode();

        // THEN
        assertEquals(true, res.hasProperty(propertyName));
    }

    @Test
    public void i18nPropertyDefined_CurrentIsNotDefault() throws Exception{
        // GIVEN
        this.definition.setI18n(true);
        MgnlContext.getInstance().setLocale(Locale.FRENCH);
        abstractDialogField = new AbstractFieldBuilderTest.TestFormField(definition, baseItem);
        abstractDialogField.setI18nContentSupport(i18nContentSupport);
        Field field = abstractDialogField.getField();
        field.setValue("new Value");
        // WHEN
        Node res = ((JcrNodeAdapter)baseItem).getNode();

        // THEN
        // Should not be sufixed by _fr --> setI18n(false)
        assertEquals(true, res.hasProperty(propertyName+"_fr"));
    }


    @Test
    public void i18nPropertyDefined_CurrentIsNotDefaultAndDefaultAlreadyExist()  throws Exception {
        // GIVEN
        this.definition.setI18n(true);
        abstractDialogField = new AbstractFieldBuilderTest.TestFormField(definition, baseItem);
        abstractDialogField.setI18nContentSupport(i18nContentSupport);
        Field field = abstractDialogField.getField();
        field.setValue("new Value");
        Node res = ((JcrNodeAdapter)baseItem).getNode();
        assertEquals(true, res.hasProperty(propertyName));

        MgnlContext.getInstance().setLocale(Locale.FRENCH);
        abstractDialogField = new AbstractFieldBuilderTest.TestFormField(definition, baseItem);
        abstractDialogField.setI18nContentSupport(i18nContentSupport);
        field = abstractDialogField.getField();
        field.setValue("new Value FR");
        // WHEN
        res = ((JcrNodeAdapter)baseItem).getNode();

        // THEN
        assertEquals(true, res.hasProperty(propertyName+"_fr"));
        assertEquals("new Value FR", res.getProperty(propertyName+"_fr").getString());
        assertEquals("new Value", res.getProperty(propertyName).getString());
    }

    @Override
    protected void createConfiguredFieldDefinition() {
        ConfiguredFieldDefinition configureFieldDefinition = new ConfiguredFieldDefinition();
        configureFieldDefinition.setName(propertyName);
        this.definition = configureFieldDefinition;
    }
}
