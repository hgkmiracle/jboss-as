/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2010, Red Hat, Inc., and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package org.jboss.as.model;

import java.util.Iterator;
import java.util.List;

/**
 * An update which modifies the host's system properties.
 *
 * @author <a href="mailto:david.lloyd@redhat.com">David M. Lloyd</a>
 */
public final class HostSystemPropertyUpdate extends AbstractHostModelUpdate<Void> {
    private static final long serialVersionUID = -3412272237934071396L;

    private final AbstractPropertyUpdate propertyUpdate;

    /**
     * Construct a new instance.
     *
     * @param propertyUpdate the property update to apply
     */
    public HostSystemPropertyUpdate(final AbstractPropertyUpdate propertyUpdate) {
        this.propertyUpdate = propertyUpdate;
    }

    /** {@inheritDoc} */
    @Override
    protected void applyUpdate(final HostModel element) throws UpdateFailedException {
        propertyUpdate.applyUpdate(element.getSystemProperties());
    }

    /** {@inheritDoc} */
    @Override
    public HostSystemPropertyUpdate getCompensatingUpdate(final HostModel original) {
        return new HostSystemPropertyUpdate(propertyUpdate.getCompensatingUpdate(original.getSystemProperties()));
    }

    /** {@inheritDoc} */
    @Override
    public ServerSystemPropertyUpdate getServerModelUpdate() {
        return new ServerSystemPropertyUpdate(propertyUpdate);
    }

    @Override
    public List<String> getAffectedServers(HostModel hostModel) {
        String propertyName = propertyUpdate.getPropertyName();

        List<String> activeServers = hostModel.getActiveServerNames();
        // Remove any server that directly overrides the property
        for (Iterator<String> it = activeServers.iterator(); it.hasNext();) {
            ServerElement server = hostModel.getServer(it.next());
            if (server.getSystemProperties().getPropertyNames().contains(propertyName)) {
                it.remove();
            }
        }
        return activeServers;
    }
}
