/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2010 TH4 SYSTEMS GmbH (http://th4-systems.com)
 *
 * OpenSCADA is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License version 3
 * only, as published by the Free Software Foundation.
 *
 * OpenSCADA is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License version 3 for more details
 * (a copy is included in the LICENSE file that accompanied this code).
 *
 * You should have received a copy of the GNU Lesser General Public License
 * version 3 along with OpenSCADA. If not, see
 * <http://opensource.org/licenses/lgpl-3.0.html> for a copy of the LGPLv3 License.
 */

package cn.com.sgcc.gdt.opc.core.dcom.common.impl;

import cn.com.sgcc.gdt.opc.core.dcom.common.EventHandler;
import org.jinterop.dcom.common.JIException;
import org.jinterop.dcom.core.IJIComObject;
import org.jinterop.dcom.core.JIFrameworkHelper;

public class EventHandlerImpl implements EventHandler {
    private String identifier = null;

    private IJIComObject object = null;

    @Override
    public String getIdentifier() {
        return this.identifier;
    }

    @Override
    public synchronized IJIComObject getObject() {
        return this.object;
    }

    public synchronized void setInfo(final IJIComObject object, final String identifier) {
        this.object = object;
        this.identifier = identifier;
    }

    @Override
    public synchronized void detach() throws JIException {
        if (this.object != null && this.identifier != null) {
            try {
                JIFrameworkHelper.detachEventHandler(this.object, this.identifier);
            } finally {
                this.object = null;
                this.identifier = null;
            }
        }
    }

}
