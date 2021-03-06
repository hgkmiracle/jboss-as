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

package org.jboss.as.domain.client.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jboss.as.domain.client.api.DomainUpdateApplier;
import org.jboss.as.domain.client.api.ServerIdentity;
import org.jboss.as.model.UpdateFailedException;

/**
 * Encapsulates the results provided by the domain controller in response
 * to a request to apply an update to the domain model and to the relevant
 * hosts. Used to provide input parameters to the callback methods in a
 * {@link DomainUpdateApplier}.
 *
 * @author Brian Stansberry
 */
public class DomainUpdateApplierResponse {

    private final UpdateFailedException domainFailure;
    private final Map<String, UpdateFailedException> hostFailures = new HashMap<String, UpdateFailedException>();
    private final List<ServerIdentity> servers = new ArrayList<ServerIdentity>();
    private final boolean cancelled;
    private final boolean rolledBack;

    public DomainUpdateApplierResponse(boolean cancelled) {
        this.cancelled = cancelled;
        this.rolledBack = !cancelled;
        this.domainFailure = null;
    }

    public DomainUpdateApplierResponse(final UpdateFailedException domainFailure) {
        this.domainFailure = domainFailure;
        this.cancelled = false;
        this.rolledBack = false;
    }

    public DomainUpdateApplierResponse(final Map<String, UpdateFailedException> hostFailures) {
        this.domainFailure = null;
        if (hostFailures != null) {
            this.hostFailures.putAll(hostFailures);
        }
        this.cancelled = false;
        this.rolledBack = false;
    }

    public DomainUpdateApplierResponse(final List<ServerIdentity> servers) {
        this.domainFailure = null;
        if (servers != null) {
            this.servers.addAll(servers);
        }
        this.cancelled = false;
        this.rolledBack = false;
    }

    public boolean isCancelled() {
        return cancelled;
    }

    public boolean isRolledBack() {
        return rolledBack;
    }

    public UpdateFailedException getDomainFailure() {
        return domainFailure;
    }

    public Map<String, UpdateFailedException> getHostFailures() {
        return Collections.unmodifiableMap(hostFailures);
    }

    public List<ServerIdentity> getServers() {
        return Collections.unmodifiableList(servers);
    }


}
