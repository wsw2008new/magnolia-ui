/**
 * This file Copyright (c) 2010-2012 Magnolia International
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
package info.magnolia.ui.vaadin.integration.widget.client;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.RepeatingCommand;
import com.google.gwt.user.client.Window;

import org.vaadin.openesignforms.ckeditor.widgetset.client.ui.VCKEditorTextField;

import com.vaadin.terminal.gwt.client.ApplicationConnection;
import com.vaadin.terminal.gwt.client.UIDL;

/**
 * Magnolia rich text field adds an ability to custom plugins to communicate 
 * with the server. This was not possible with the add-on.
 */
public class VMagnoliaRichTextField extends VCKEditorTextField {
    
    public static final String VAR_EXTERNAL_LINK = "externalLink";

    public VMagnoliaRichTextField() {
        super();
    }
    
    public void updateFromUIDL(UIDL uidl, ApplicationConnection client) {
        super.updateFromUIDL(uidl, client);
        Scheduler.get().scheduleFixedDelay(new RepeatingCommand() {             
            @Override
            public boolean execute() {
                return !register(VMagnoliaRichTextField.this);
            }
        }, 50);
        
        if(uidl.hasAttribute(VAR_EXTERNAL_LINK)) {
            Window.alert(uidl.getStringAttribute(VAR_EXTERNAL_LINK));
        }
    }
    
    public void onEvent() {
        clientToServer.updateVariable(paintableId, VAR_EXTERNAL_LINK, "", true);
    }
    
    public native boolean register(VMagnoliaRichTextField listener)
    /*-{
        if($wnd.CKEDITOR) {
            for(var key in $wnd.CKEDITOR.instances) {
                var editor = $wnd.CKEDITOR.instances[key];
                editor.on('demoevent', function( ev ) {
                    ev.listenerData.@info.magnolia.ui.vaadin.integration.widget.client.VMagnoliaRichTextField::onEvent()();
                }, null, listener);
            }
            return true;
        } else {
            return false;
        }
     }-*/;
}
