package org.camunda.bpm.extension.osgi.eventing;

import org.apache.felix.dm.DependencyActivatorBase;
import org.apache.felix.dm.DependencyManager;
import org.camunda.bpm.extension.osgi.eventing.impl.OSGiEventDistributor;
import org.osgi.framework.BundleContext;
import org.osgi.service.event.EventAdmin;

/**
 * @author Ronny Bräunlich
 */
public class Activator extends DependencyActivatorBase {
		@Override
		public void init(BundleContext context, DependencyManager manager) throws Exception {
				manager.add(createComponent()
								.setImplementation(OSGiEventDistributor.class)
								.add(createServiceDependency()
																.setService(EventAdmin.class)
																.setRequired(true)
								));
		}
}