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
package info.magnolia.ui.form.field.upload.basic;

import info.magnolia.cms.beans.runtime.FileProperties;
import info.magnolia.ui.form.field.upload.FileItemWrapper;
import info.magnolia.ui.form.field.upload.UploadReceiver;
import info.magnolia.ui.vaadin.integration.jcr.DefaultPropertyUtil;
import info.magnolia.ui.vaadin.integration.jcr.JcrItemNodeAdapter;
import info.magnolia.ui.vaadin.integration.jcr.JcrNewNodeAdapter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Date;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.jackrabbit.JcrConstants;
import org.apache.jackrabbit.value.BinaryImpl;
import org.apache.jackrabbit.value.ValueFactoryImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.data.Item;
import com.vaadin.data.Property;

/**
 * Base Implementation of {@link FileItemWrapper}.
 * This class perform the bridge between an {@link com.vaadin.data.Item} and a {@link UploadReceiver}. <br>
 * During initialization, the Item passed on the constructor populate the local variables. <br>
 * These local variables are used by the Field to display File informations (like FileName, FileSize...) <br>
 * When an Upload is performed (Uploaded File is handled by the UploadReceiver), <br>
 * - the local variables are populated based on the UploadReceiver values <br>
 * - the Item is also populated based on these values. <br>
 */
public class BasicFileItemWrapper implements FileItemWrapper {
    private static final Logger log = LoggerFactory.getLogger(BasicFileItemWrapper.class);

    // File Properties
    private File uploadedFile;
    private final File tmpDirectory;
    protected long fileSize;
    private String mimeType;
    private String extension;
    private String fileName;

    protected JcrItemNodeAdapter item;

    public BasicFileItemWrapper(File tmpDirectory) {
        this.tmpDirectory = tmpDirectory;
    }

    public BasicFileItemWrapper(JcrItemNodeAdapter jcrItem, File tmpDirectory) {
        this.tmpDirectory = tmpDirectory;
        populateFromItem(jcrItem);
    }

    /**
     * Populate the local variable with the values of the {@link Item}.
     */
    @Override
    public void populateFromItem(Item jcrItem) {
        if (jcrItem instanceof JcrItemNodeAdapter) {
            item = (JcrItemNodeAdapter) jcrItem;
        } else {
            log.warn("Item {} is not a JcrItemAdapter. Wrapper will not be initialized", jcrItem);
            return;
        }
        if (item instanceof JcrNewNodeAdapter) {
            log.debug("BaseFileWrapper will be empty as the related Item is a new Item");
            initJcrItemProperty(item);
        } else {
            log.debug("BaseFileWrapper will be initialized with on the current Item values");
            populateWrapperFromItem();
        }
    }

    /**
     * Populate the wrapper variable based on the current Item.
     */
    protected void populateWrapperFromItem() {
        fileName = item.getItemProperty(FileProperties.PROPERTY_FILENAME) != null ? (String) item.getItemProperty(FileProperties.PROPERTY_FILENAME).getValue() : "";
        Property<?> data = item.getItemProperty(JcrConstants.JCR_DATA);
        if (data != null) {
            fileSize = Long.parseLong(item.getItemProperty(FileProperties.PROPERTY_SIZE).getValue().toString());
            mimeType = String.valueOf(item.getItemProperty(FileProperties.PROPERTY_CONTENTTYPE).getValue());
            if (item.getItemProperty(FileProperties.PROPERTY_EXTENSION) != null) {
                extension = String.valueOf(item.getItemProperty(FileProperties.PROPERTY_EXTENSION).getValue());
            }
            try {
                uploadedFile = File.createTempFile(StringUtils.rightPad(fileName, 5, "x"), null, tmpDirectory);
                FileOutputStream fileOuputStream = new FileOutputStream(uploadedFile);
                if (data.getValue() instanceof BinaryImpl) {
                    IOUtils.copy(((BinaryImpl) data.getValue()).getStream(), fileOuputStream);
                } else {
                    fileOuputStream.write((byte[]) data.getValue());
                }
                fileOuputStream.close();
                uploadedFile.deleteOnExit();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * Populate the local variables with the values of the {UploadReceiver receiver}.
     * Update the related {@link Item} with this new values.
     */
    @Override
    public void populateFromReceiver(UploadReceiver receiver) {
        populateWrapperFromReceiver(receiver);
        populateItem();
    }

    protected void populateWrapperFromReceiver(UploadReceiver receiver) {
        uploadedFile = receiver.getFile();
        fileName = receiver.getFileName();
        extension = receiver.getExtension();
        fileSize = receiver.getFileSize();
        mimeType = receiver.getMimeType();
    }

    /**
     * Clear the local variables.
     * Clear the Item.
     */
    @Override
    public void clearProperties() {
        uploadedFile = null;
        fileName = null;
        extension = null;
        fileSize = -1;
        mimeType = null;

        item.getParent().removeChild(item);
    }


    @Override
    public void reloadPrevious() {
        if (!isEmpty()) {
            populateWrapperFromItem();
        }
    }

    @Override
    public boolean isEmpty() {
        return uploadedFile == null;
    }

    /**
     * Update the {@link Item} based on the local values.
     */
    @SuppressWarnings("unchecked")
    protected void populateItem() {
        // Attach the Item to the parent in order to be stored.
        item.getParent().addChild(item);
        // Populate Data
        Property<Object> data = item.getItemProperty(JcrConstants.JCR_DATA);

        if (uploadedFile != null) {
            try {
                data.setValue(ValueFactoryImpl.getInstance().createBinary(new FileInputStream(uploadedFile)));
            } catch (Exception re) {
                log.error("Could not get Binary. Upload will not be performed", re);
                item.getParent().removeChild(item);
                return;
            }
        }
        item.getItemProperty(FileProperties.PROPERTY_FILENAME).setValue(StringUtils.substringBeforeLast(fileName, "."));
        item.getItemProperty(FileProperties.PROPERTY_CONTENTTYPE).setValue(mimeType);
        item.getItemProperty(FileProperties.PROPERTY_LASTMODIFIED).setValue(new Date());
        item.getItemProperty(FileProperties.PROPERTY_SIZE).setValue(fileSize);
        item.getItemProperty(FileProperties.PROPERTY_EXTENSION).setValue(extension);
    }

    /**
     * Initialize a Item Node Adapter with the mandatory File property.
     */
    protected void initJcrItemProperty(JcrItemNodeAdapter jcrItem) {
        jcrItem.addItemProperty(JcrConstants.JCR_DATA, DefaultPropertyUtil.newDefaultProperty(JcrConstants.JCR_DATA, "Binary", null));
        jcrItem.addItemProperty(FileProperties.PROPERTY_FILENAME, DefaultPropertyUtil.newDefaultProperty(FileProperties.PROPERTY_FILENAME, "String", null));
        jcrItem.addItemProperty(FileProperties.PROPERTY_CONTENTTYPE, DefaultPropertyUtil.newDefaultProperty(FileProperties.PROPERTY_CONTENTTYPE, "String", null));
        jcrItem.addItemProperty(FileProperties.PROPERTY_LASTMODIFIED, DefaultPropertyUtil.newDefaultProperty(FileProperties.PROPERTY_LASTMODIFIED, "Date", null));
        jcrItem.addItemProperty(FileProperties.PROPERTY_SIZE, DefaultPropertyUtil.newDefaultProperty(FileProperties.PROPERTY_SIZE, "Long", null));
        jcrItem.addItemProperty(FileProperties.PROPERTY_EXTENSION, DefaultPropertyUtil.newDefaultProperty(FileProperties.PROPERTY_EXTENSION, "String", null));
    }


    @Override
    public long getFileSize() {
        return this.fileSize;
    }

    @Override
    public String getMimeType() {
        return this.mimeType;
    }

    @Override
    public String getExtension() {
        return this.extension;
    }

    @Override
    public String getFileName() {
        return this.fileName;
    }

    @Override
    public File getFile() {
        return this.uploadedFile;
    }
}
