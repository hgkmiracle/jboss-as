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

package org.jboss.as.datasources;

import org.jboss.as.connector.mdr.MdrServices;
import org.jboss.as.model.AbstractSubsystemAdd;
import org.jboss.as.model.UpdateContext;
import org.jboss.as.model.UpdateResultHandler;
import org.jboss.as.txn.TxnServices;
import org.jboss.jca.common.api.metadata.ds.DataSources;
import org.jboss.jca.core.spi.mdr.MetadataRepository;
import org.jboss.msc.service.BatchBuilder;
import org.jboss.msc.service.BatchServiceBuilder;
import org.jboss.msc.service.ServiceController.Mode;

/**
 * @author <a href="mailto:stefano.maestri@redhat.comdhat.com">Stefano
 *         Maestri</a>
 * @author <a href="mailto:david.lloyd@redhat.com">David M. Lloyd</a>
 */
public final class DataSourcesAdd extends AbstractSubsystemAdd<DataSourcesSubsystemElement> {

    private static final long serialVersionUID = -874698675049495644L;

    private DataSources datasources;

    public DataSources getDatasources() {
        return datasources;
    }

    public void setDatasources(DataSources datasources) {
        this.datasources = datasources;
    }

    protected DataSourcesAdd() {
        super(Namespace.CURRENT.getUriString());
    }

    @Override
    protected <P> void applyUpdate(final UpdateContext updateContext, final UpdateResultHandler<? super Void, P> resultHandler,
            final P param) {
        final BatchBuilder builder = updateContext.getBatchBuilder();

        final DataSourcesService dsService = new DataSourcesService(datasources);
        final BatchServiceBuilder<DataSources> serviceBuilder = builder.addService(DataSourcesServices.DATASOURCES_SERVICE,
                dsService);
        serviceBuilder.addDependency(MdrServices.IRONJACAMAR_MDR, MetadataRepository.class, dsService.getMdrInjector());
        serviceBuilder.addDependency(TxnServices.JBOSS_TXN_TRANSACTION_MANAGER,
                com.arjuna.ats.jbossatx.jta.TransactionManagerService.class, dsService.getTxmInjector());
        // TODO: depends on jdbc services

        serviceBuilder.setInitialMode(Mode.ACTIVE);
    }

    protected DataSourcesSubsystemElement createSubsystemElement() {
        DataSourcesSubsystemElement element = new DataSourcesSubsystemElement();
        element.setDatasources(datasources);
        return element;
    }

}
