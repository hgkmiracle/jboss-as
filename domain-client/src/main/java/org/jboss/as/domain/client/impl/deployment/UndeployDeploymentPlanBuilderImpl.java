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

package org.jboss.as.domain.client.impl.deployment;

import org.jboss.as.domain.client.api.deployment.DeploymentAction;
import org.jboss.as.domain.client.api.deployment.DeploymentPlanBuilder;
import org.jboss.as.domain.client.api.deployment.ServerGroupDeploymentPlanBuilder;
import org.jboss.as.domain.client.api.deployment.UndeployDeploymentPlanBuilder;


/**
 * Variant of a {@link DeploymentPlanBuilderImpl} that exposes
 * directives that are only applicable following an <code>undeploy</code> directive.
 *
 * @author Brian Stansberry
 */
class UndeployDeploymentPlanBuilderImpl extends DeploymentPlanBuilderImpl implements UndeployDeploymentPlanBuilder {

    private final DeploymentAction undeployModification;

    UndeployDeploymentPlanBuilderImpl(DeploymentPlanBuilderImpl existing, DeploymentSetPlanImpl setPlan, boolean replace) {
        super(existing, setPlan, replace);
        DeploymentAction modification = setPlan.getLastAction();
        if (modification.getType() != DeploymentAction.Type.UNDEPLOY) {
            throw new IllegalStateException("Invalid action type " + modification.getType());
        }
        this.undeployModification = modification;
    }

    @Override
    public ServerGroupDeploymentPlanBuilder toServerGroup(String serverGroupName) {
        return super.toServerGroup(serverGroupName);
    }

    @Override
    public DeploymentPlanBuilder andRemoveUndeployed() {
        DeploymentActionImpl removeMod = DeploymentActionImpl.getRemoveAction(undeployModification.getDeploymentUnitUniqueName());
        return getNewBuilder(removeMod);
    }
}
