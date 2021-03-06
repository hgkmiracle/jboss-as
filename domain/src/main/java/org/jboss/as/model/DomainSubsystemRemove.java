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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Remove a subsystem from a domain profile.
 *
 * @author <a href="mailto:david.lloyd@redhat.com">David M. Lloyd</a>
 */
public final class DomainSubsystemRemove extends AbstractDomainModelUpdate<Void> {

    private static final long serialVersionUID = -9076890219875153928L;

    private final String profileName;
    private final SubsystemRemove subsystemRemove;

    /**
     * Construct a new instance.
     *
     * @param profileName the name of the profile that the change applies to
     * @param subsystemRemove the subsystem remove
     */
    public DomainSubsystemRemove(final String profileName, final SubsystemRemove subsystemRemove) {
        this.profileName = profileName;
        this.subsystemRemove = subsystemRemove;
    }

    @Override
    protected void applyUpdate(final DomainModel element) throws UpdateFailedException {
        final ProfileElement profileElement = element.getProfile(profileName);
        if (profileElement == null) {
            throw new UpdateFailedException("Profile '" + profileName + "' is not configured on this domain");
        }
        subsystemRemove.applyUpdate(profileElement);
    }

    public String getProfileName() {
        return profileName;
    }

    public SubsystemRemove getSubsystemRemove() {
        return subsystemRemove;
    }

    public String getNamespaceUri() {
        return subsystemRemove.getNamespaceUri();
    }

    @Override
    public DomainSubsystemAdd getCompensatingUpdate(final DomainModel original) {
        return new DomainSubsystemAdd(profileName, subsystemRemove.getCompensatingUpdate(original.getProfile(profileName)));
    }

    @Override
    public ServerSubsystemRemove getServerModelUpdate() {
        return new ServerSubsystemRemove(subsystemRemove);
    }

    @Override
    public List<String> getAffectedServers(DomainModel domainModel, HostModel hostModel) throws UpdateFailedException {
        if (getServerModelUpdate() == null) {
            return Collections.emptyList();
        }
        else {
            List<String> result = new ArrayList<String>();
            for (String server : hostModel.getActiveServerNames()) {
                String serverGroupName =  hostModel.getServer(server).getServerGroup();

                if (profileName.equals(domainModel.getServerGroup(serverGroupName).getProfileName())) {
                    result.add(server);
                }
            }
            return result;
        }
    }
}
