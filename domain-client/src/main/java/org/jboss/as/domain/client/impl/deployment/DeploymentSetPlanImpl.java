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

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.jboss.as.domain.client.api.deployment.DeploymentAction;
import org.jboss.as.domain.client.api.deployment.DeploymentSetPlan;
import org.jboss.as.domain.client.api.deployment.ServerGroupDeploymentPlan;

/**
 * Describes a set of actions to take to change the deployment content available
 * to deployed in a server group or set of server groups.
 *
 * @author Brian Stansberry
 */
public class DeploymentSetPlanImpl implements DeploymentSetPlan {

    private final UUID uuid;
    private final List<DeploymentAction> deploymentActions = new ArrayList<DeploymentAction>();
    private final boolean rollback;
    private final boolean shutdown;
    private final long gracefulShutdownPeriod;
    private final List<Set<ServerGroupDeploymentPlan>> serverGroupPlans = new ArrayList<Set<ServerGroupDeploymentPlan>>();

    DeploymentSetPlanImpl() {
        this.uuid = UUID.randomUUID();
        this.rollback = false;
        this.shutdown = false;
        this.gracefulShutdownPeriod = 0;
        this.serverGroupPlans.add(new LinkedHashSet<ServerGroupDeploymentPlan>());
    }

    private DeploymentSetPlanImpl(final UUID uuid,
            final List<DeploymentAction> actions,
            final List<Set<ServerGroupDeploymentPlan>> serverGroupPlans,
            final boolean rollback,
            final boolean shutdown,
            final long gracefulTimeout) {
        this.uuid = uuid;
        this.deploymentActions.addAll(actions);
        this.rollback = rollback;
        this.shutdown = shutdown;
        this.gracefulShutdownPeriod = gracefulTimeout;
        this.serverGroupPlans.addAll(serverGroupPlans);
        LinkedHashSet<ServerGroupDeploymentPlan> last = (LinkedHashSet<ServerGroupDeploymentPlan>) serverGroupPlans.get(serverGroupPlans.size() -1);
        this.serverGroupPlans.set(serverGroupPlans.size() - 1, new LinkedHashSet<ServerGroupDeploymentPlan>(last));
    }

    @Override
    public UUID getId() {
        return uuid;
    }

    public DeploymentAction getLastAction() {
        return deploymentActions.size() == 0 ? null : deploymentActions.get(deploymentActions.size() - 1);
    }

    @Override
    public List<DeploymentAction> getDeploymentActions() {
        return new ArrayList<DeploymentAction>(deploymentActions);
    }

    @Override
    public boolean isRollback() {
        return rollback;
    }

    @Override
    public long getGracefulShutdownTimeout() {
        return gracefulShutdownPeriod;
    }

    @Override
    public boolean isGracefulShutdown() {
        return shutdown && gracefulShutdownPeriod > -1;
    }

    @Override
    public boolean isShutdown() {
        return shutdown;
    }

    @Override
    public List<Set<ServerGroupDeploymentPlan>> getServerGroupDeploymentPlans() {
        List<Set<ServerGroupDeploymentPlan>> copy = null;
        if (serverGroupPlans != null) {
            copy = new ArrayList<Set<ServerGroupDeploymentPlan>>(serverGroupPlans.size());
            for (Set<ServerGroupDeploymentPlan> set : serverGroupPlans) {
                copy.add(Collections.unmodifiableSet(new LinkedHashSet<ServerGroupDeploymentPlan>(set)));
            }
        }
        else {
            copy = Collections.emptyList();
        }
        return Collections.unmodifiableList(copy);
    }

    boolean hasServerGroupPlans() {
        return serverGroupPlans.size() > 1 || serverGroupPlans.get(0).size() > 0;
    }

    ServerGroupDeploymentPlan getLatestServerGroupDeploymentPlan() {
        LinkedHashSet<ServerGroupDeploymentPlan> lastSet = (LinkedHashSet<ServerGroupDeploymentPlan>) serverGroupPlans.get(serverGroupPlans.size() -1);
        ServerGroupDeploymentPlan last = null;
        for (ServerGroupDeploymentPlan plan : lastSet) {
            last = plan;
        }
        return last;
    }

    DeploymentSetPlanImpl addAction(final DeploymentAction action) {
        DeploymentSetPlanImpl result = new DeploymentSetPlanImpl(this.uuid, this.deploymentActions, this.serverGroupPlans, this.rollback, this.shutdown, this.gracefulShutdownPeriod);
        result.deploymentActions.add(action);
        return result;
    }

    DeploymentSetPlanImpl setRollback() {
        DeploymentSetPlanImpl result = new DeploymentSetPlanImpl(this.uuid, this.deploymentActions, this.serverGroupPlans, true, this.shutdown, this.gracefulShutdownPeriod);
        return result;
    }

    DeploymentSetPlanImpl setShutdown() {
        DeploymentSetPlanImpl result = new DeploymentSetPlanImpl(this.uuid, this.deploymentActions, this.serverGroupPlans, this.rollback, true, 0);
        return result;
    }

    DeploymentSetPlanImpl setGracefulTimeout(long timeout) {
        DeploymentSetPlanImpl result = new DeploymentSetPlanImpl(this.uuid, this.deploymentActions, this.serverGroupPlans, this.rollback, this.shutdown, timeout);
        return result;
    }

    DeploymentSetPlanImpl storeServerGroup(final ServerGroupDeploymentPlan groupPlan) {
        DeploymentSetPlanImpl result = new DeploymentSetPlanImpl(this.uuid, this.deploymentActions, this.serverGroupPlans, this.rollback, this.shutdown, this.gracefulShutdownPeriod);
        Set<ServerGroupDeploymentPlan> set = result.serverGroupPlans.get(result.serverGroupPlans.size() - 1);
        set.remove(groupPlan);
        set.add(groupPlan);
        return result;
    }

    DeploymentSetPlanImpl storeRollToServerGroup(final ServerGroupDeploymentPlan groupPlan) {
        DeploymentSetPlanImpl result = new DeploymentSetPlanImpl(this.uuid, this.deploymentActions, this.serverGroupPlans, this.rollback, true, this.gracefulShutdownPeriod);
        Set<ServerGroupDeploymentPlan> set = result.serverGroupPlans.get(result.serverGroupPlans.size() - 1);
        result.serverGroupPlans.set(result.serverGroupPlans.size() - 1, Collections.unmodifiableSet(set));
        set = new LinkedHashSet<ServerGroupDeploymentPlan>();
        set.add(groupPlan);
        result.serverGroupPlans.add(set);
        return result;
    }
}
