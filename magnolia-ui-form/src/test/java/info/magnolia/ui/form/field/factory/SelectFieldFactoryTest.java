/**
 * This file Copyright (c) 2012-2015 Magnolia International
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
package info.magnolia.ui.form.field.factory;

import static org.junit.Assert.*;

import info.magnolia.ui.form.field.definition.SelectFieldDefinition;
import info.magnolia.ui.form.field.definition.SelectFieldOptionDefinition;
import info.magnolia.ui.vaadin.integration.jcr.DefaultProperty;
import info.magnolia.ui.vaadin.integration.jcr.JcrNewNodeAdapter;
import info.magnolia.ui.vaadin.integration.jcr.JcrNodeAdapter;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;

import javax.jcr.Node;

import org.junit.Test;

import com.vaadin.data.Property;
import com.vaadin.ui.AbstractSelect;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Field;

/**
 * Main testcase for {@link info.magnolia.ui.form.field.factory.SelectFieldFactory}.
 */
public class SelectFieldFactoryTest extends AbstractFieldFactoryTestCase<SelectFieldDefinition> {

    private SelectFieldFactory<SelectFieldDefinition> dialogSelect;

    @Test
    public void createField() throws Exception {
        // GIVEN
        baseItem = new JcrNewNodeAdapter(baseNode, baseNode.getPrimaryNodeType().getName());
        dialogSelect = new SelectFieldFactory<SelectFieldDefinition>(definition, baseItem);
        dialogSelect.setComponentProvider(componentProvider);

        // WHEN
        Field field = dialogSelect.createField();

        // THEN
        assertTrue(field instanceof ComboBox);
        Collection<?> items = ((ComboBox) field).getItemIds();
        assertEquals(3, items.size());
        assertEquals("1", field.getValue().toString());
    }

    @Test
    public void createFieldSelectsDefaultOptionIfConfigured() throws Exception {
        // GIVEN
        SelectFieldOptionDefinition option = definition.getOptions().get(1);
        option.setSelected(true);
        baseItem = new JcrNewNodeAdapter(baseNode, baseNode.getPrimaryNodeType().getName());
        dialogSelect = new SelectFieldFactory<SelectFieldDefinition>(definition, baseItem);
        dialogSelect.setComponentProvider(componentProvider);

        // WHEN
        Field field = dialogSelect.createField();

        // THEN
        assertEquals(option.getValue(), field.getValue().toString());
    }

    @Test
    public void createFieldSelectsFirstOptionIfNoDefaultConfigured() throws Exception {
        // GIVEN
        dialogSelect = new SelectFieldFactory<SelectFieldDefinition>(definition, baseItem);
        dialogSelect.setComponentProvider(componentProvider);

        // WHEN
        Field field = dialogSelect.createField();

        // THEN first option is selected
        assertEquals("1", field.getValue().toString());
    }

    @Test
    public void createFieldUsesNodeNamesIfOptionValuesAreNotSet() throws Exception {
        // GIVEN
        List<SelectFieldOptionDefinition> options = definition.getOptions();
        for (SelectFieldOptionDefinition option : options) {
            option.setValue(null);
            option.setName(option.getLabel().toLowerCase());
        }
        dialogSelect = new SelectFieldFactory<SelectFieldDefinition>(definition, baseItem);
        dialogSelect.setComponentProvider(componentProvider);

        // WHEN
        dialogSelect.createField();

        // THEN
        options = definition.getOptions();
        for (SelectFieldOptionDefinition option : options) {
            assertEquals(option.getName(), option.getValue());
        }
    }

    @Test
    public void createFieldDoesntSelectDefaultIfValueAlreadyExists() throws Exception {
        // GIVEN
        SelectFieldOptionDefinition option = definition.getOptions().get(1);
        option.setSelected(true);
        baseNode.setProperty(propertyName, "3");
        baseItem = new JcrNodeAdapter(baseNode);
        dialogSelect = new SelectFieldFactory<SelectFieldDefinition>(definition, baseItem);
        dialogSelect.setComponentProvider(componentProvider);

        // WHEN
        Field field = dialogSelect.createField();

        // THEN
        assertEquals("3", field.getValue().toString());
    }

    @Test
    public void createFieldWorksWithRemoteOptions() throws Exception {
        // GIVEN
        // Create a Options node.
        Node options = session.getRootNode().addNode("options");
        Node optionEn = options.addNode("en");
        optionEn.setProperty("value", "en");
        optionEn.setProperty("label", "English");
        Node optionFr = options.addNode("fr");
        optionFr.setProperty("value", "fr");
        optionFr.setProperty("label", "Francais");
        // Set remote Options in configuration
        definition.setPath(options.getPath());
        definition.setRepository(workspaceName);
        definition.setOptions(new ArrayList<SelectFieldOptionDefinition>());
        baseItem = new JcrNewNodeAdapter(baseNode, baseNode.getPrimaryNodeType().getName());
        dialogSelect = new SelectFieldFactory<SelectFieldDefinition>(definition, baseItem);
        dialogSelect.setComponentProvider(componentProvider);

        // WHEN
        Field field = dialogSelect.createField();

        // THEN
        Collection<?> items = ((ComboBox) field).getItemIds();
        assertEquals(2, items.size());
        assertEquals("en", field.getValue().toString());
    }

    @Test
    public void createFieldWithRemoteOptionsIgnoresNonMgnlNodeTypes() throws Exception {
        // GIVEN
        // Create a Options node.
        Node options = session.getRootNode().addNode("options");
        Node optionEn = options.addNode("en");
        optionEn.setProperty("value", "en");
        optionEn.setProperty("label", "English");
        Node optionFr = options.addNode("fr", "nt:hierarchyNode");
        optionFr.setProperty("value", "fr");
        optionFr.setProperty("label", "Francais");
        // Set remote Options in configuration
        definition.setPath(options.getPath());
        definition.setRepository(workspaceName);
        definition.setOptions(new ArrayList<SelectFieldOptionDefinition>());
        baseItem = new JcrNewNodeAdapter(baseNode, baseNode.getPrimaryNodeType().getName());
        dialogSelect = new SelectFieldFactory<SelectFieldDefinition>(definition, baseItem);
        dialogSelect.setComponentProvider(componentProvider);

        // WHEN
        Field field = dialogSelect.createField();

        // THEN
        Collection<?> items = ((ComboBox) field).getItemIds();
        assertEquals("Only get one option as fr option node is not of 'mgnl' type", 1, items.size());
        assertEquals("en", field.getValue().toString());
    }

    @Test
    public void createFieldWorksWithDifferentOptionValueAndLabelNames() throws Exception {
        // GIVEN
        // Create a Options node.
        Node options = session.getRootNode().addNode("options");
        Node optionEn = options.addNode("en");
        optionEn.setProperty("x", "en");
        optionEn.setProperty("z", "English");
        Node optionFr = options.addNode("fr");
        optionFr.setProperty("x", "fr");
        optionFr.setProperty("z", "Francais");
        optionFr.setProperty("selected", "true");
        // Set remote Options in configuration
        definition.setPath(options.getPath());
        definition.setRepository(workspaceName);
        definition.setOptions(new ArrayList<SelectFieldOptionDefinition>());
        // Define the name of value and label
        definition.setValueProperty("x");
        definition.setLabelProperty("z");
        baseItem = new JcrNewNodeAdapter(baseNode, baseNode.getPrimaryNodeType().getName());

        dialogSelect = new SelectFieldFactory<SelectFieldDefinition>(definition, baseItem);
        dialogSelect.setComponentProvider(componentProvider);

        // WHEN
        Field field = dialogSelect.createField();

        // THEN
        Collection<?> items = ((ComboBox) field).getItemIds();
        assertEquals(2, items.size());
        assertEquals("fr", field.getValue().toString());
    }

    @Test
    public void createFieldSortsOptionsAlphabeticallyAscendingByDefault() throws Exception {
        // GIVEN
        ArrayList<SelectFieldOptionDefinition> options = new ArrayList<SelectFieldOptionDefinition>();

        SelectFieldOptionDefinition option1 = new SelectFieldOptionDefinition();
        option1.setLabel("bb");
        option1.setValue("1");
        options.add(option1);

        SelectFieldOptionDefinition option2 = new SelectFieldOptionDefinition();
        option2.setLabel("AA");
        option2.setValue("2");
        options.add(option2);

        SelectFieldOptionDefinition option3 = new SelectFieldOptionDefinition();
        option3.setLabel("cc");
        option3.setValue("3");
        options.add(option3);

        definition.setOptions(options);

        dialogSelect = new SelectFieldFactory<SelectFieldDefinition>(definition, baseItem);
        dialogSelect.setComponentProvider(componentProvider);

        // WHEN
        AbstractSelect field = (AbstractSelect) dialogSelect.createField();

        // THEN
        String[] items = field.getItemIds().toArray(new String[] {});

        // we need to use the values here to ensure the options are sorted as expected
        assertEquals("2", items[0]);
        assertEquals("1", items[1]);
        assertEquals("3", items[2]);
        assertEquals("2", field.getValue().toString());
    }

    @Test
    public void createFieldDoesntSortOptionsIfSpecified() throws Exception {
        // GIVEN
        ArrayList<SelectFieldOptionDefinition> options = new ArrayList<SelectFieldOptionDefinition>();

        SelectFieldOptionDefinition option1 = new SelectFieldOptionDefinition();
        option1.setLabel("bb");
        option1.setValue("1");
        options.add(option1);

        SelectFieldOptionDefinition option2 = new SelectFieldOptionDefinition();
        option2.setLabel("AA");
        option2.setValue("2");
        options.add(option2);

        SelectFieldOptionDefinition option3 = new SelectFieldOptionDefinition();
        option3.setLabel("cc");
        option3.setValue("3");
        options.add(option3);

        definition.setOptions(options);
        definition.setSortOptions(false);

        dialogSelect = new SelectFieldFactory<SelectFieldDefinition>(definition, baseItem);

        dialogSelect.setComponentProvider(componentProvider);

        // WHEN
        AbstractSelect field = (AbstractSelect) dialogSelect.createField();

        // THEN
        String[] items = field.getItemIds().toArray(new String[] {});

        assertEquals("1", items[0]);
        assertEquals("2", items[1]);
        assertEquals("3", items[2]);
        assertEquals("1", field.getValue().toString());
    }

    @Test
    public void testCreateDefaultValueFromLong() throws Exception {
        // GIVEN
        dialogSelect = new SelectFieldFactory<SelectFieldDefinition>(definition, baseItem);
        dialogSelect.setComponentProvider(componentProvider);
        AbstractSelect field = (AbstractSelect) dialogSelect.createField();
        field.removeAllItems();
        field.addItem(1L); // long value

        Property<Long> dataSource = new DefaultProperty<Long>(1L);

        // WHEN
        Object defaultValue = dialogSelect.createDefaultValue(dataSource);

        // THEN
        assertEquals("1", defaultValue.toString());
    }

    @Override
    protected void createConfiguredFieldDefinition() {
        SelectFieldDefinition fieldDefinition = new SelectFieldDefinition();
        fieldDefinition = (SelectFieldDefinition) AbstractFieldFactoryTest.createConfiguredFieldDefinition(fieldDefinition, propertyName);
        fieldDefinition.setDefaultValue(null);
        SelectFieldOptionDefinition option1 = new SelectFieldOptionDefinition();
        option1.setLabel("One");
        option1.setValue("1");

        SelectFieldOptionDefinition option2 = new SelectFieldOptionDefinition();
        option2.setLabel("Two");
        option2.setValue("2");

        SelectFieldOptionDefinition option3 = new SelectFieldOptionDefinition();
        option3.setLabel("Three");
        option3.setValue("3");

        fieldDefinition.addOption(option1);
        fieldDefinition.addOption(option2);
        fieldDefinition.addOption(option3);

        this.definition = fieldDefinition;
    }


    @Test
    public void createFieldSortsOptionsByComparator() throws Exception {
        // GIVEN
        ArrayList<SelectFieldOptionDefinition> options = new ArrayList<SelectFieldOptionDefinition>();

        SelectFieldOptionDefinition option1 = new SelectFieldOptionDefinition();
        option1.setLabel("aa");
        option1.setValue("1");
        options.add(option1);

        SelectFieldOptionDefinition option2 = new SelectFieldOptionDefinition();
        option2.setLabel("bb");
        option2.setValue("2");
        options.add(option2);

        SelectFieldOptionDefinition option3 = new SelectFieldOptionDefinition();
        option3.setLabel("cc");
        option3.setValue("3");
        options.add(option3);

        SelectFieldOptionDefinition option4 = new SelectFieldOptionDefinition();
        option4.setLabel("CC");
        option4.setValue("4");
        options.add(option4);

        SelectFieldOptionDefinition option5 = new SelectFieldOptionDefinition();
        option5.setLabel("BB");
        option5.setValue("5");
        options.add(option5);

        SelectFieldOptionDefinition option6 = new SelectFieldOptionDefinition();
        option6.setLabel("AA");
        option6.setValue("6");
        options.add(option6);

        definition.setOptions(options);
        definition.setComparatorClass(TestComparator.class);

        dialogSelect = new SelectFieldFactory<SelectFieldDefinition>(definition, baseItem);
        componentProvider.setImplementation(TestComparator.class, TestComparator.class.getName());
        dialogSelect.setComponentProvider(componentProvider);



        // WHEN
        AbstractSelect field = (AbstractSelect) dialogSelect.createField();

        // THEN
        String[] items = field.getItemIds().toArray(new String[] {});

        // we need to use the values here to ensure the options are sorted as expected
        assertEquals("1", items[0]);
        assertEquals("6", items[1]);
        assertEquals("2", items[2]);
        assertEquals("5", items[3]);
        assertEquals("3", items[4]);
        assertEquals("4", items[5]);
        assertEquals("1", field.getValue().toString());
    }


    public static class TestComparator implements Comparator<SelectFieldOptionDefinition> {

        private final Collator col;

        public TestComparator() {
            col = Collator.getInstance();
            col.setStrength(Collator.PRIMARY);
        }

        @Override
            public int compare(SelectFieldOptionDefinition o1, SelectFieldOptionDefinition o2) {
                return col.compare(o1.getLabel(), o2.getLabel());
            }
        }
}
