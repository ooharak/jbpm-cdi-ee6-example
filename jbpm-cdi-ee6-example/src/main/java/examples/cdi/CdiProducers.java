package examples.cdi;

import java.util.logging.LogManager;
import java.util.logging.Logger;

import javax.enterprise.context.Dependent;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Disposes;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;

import org.drools.core.io.impl.ClassPathResource;
import org.jbpm.process.audit.AuditLoggerFactory;
import org.jbpm.process.audit.JPAProcessInstanceDbLog;
import org.jbpm.runtime.manager.impl.RuntimeEnvironmentBuilder;
import org.jbpm.runtime.manager.impl.cdi.InjectableRegisterableItemsFactory;
import org.jbpm.services.task.admin.listener.TaskCleanUpProcessEventListener;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.manager.RuntimeEngine;
import org.kie.api.runtime.manager.RuntimeManager;
import org.kie.internal.runtime.manager.RuntimeEnvironment;
import org.kie.internal.runtime.manager.RuntimeManagerFactory;
import org.kie.internal.runtime.manager.context.EmptyContext;
import org.kie.internal.task.api.UserGroupCallback;

public class CdiProducers {
	
	@Inject
	RuntimeManagerFactory managerFactory;
	

	@Inject
	private BeanManager beanManager;

	@Inject
	private UserGroupCallback callback;
	
	@Produces
	@Dependent
	@PersistenceUnit(unitName="org.jbpm.domain")
	public EntityManagerFactory emf;
	
	@Produces
	@RequestScoped
	public EntityManager produceEntityManager(EntityManagerFactory emf) {
		return emf.createEntityManager();
	}
	
	public void disposeEntityManager(@Disposes EntityManager em) {
		em.close();
	}
	
	@Produces
	public Logger produceLogger(InjectionPoint injectionPoint) {
		return LogManager.getLogManager().getLogger(
				injectionPoint.getMember().getDeclaringClass().getPackage().getName());
	}
	
	@Produces
	@Singleton
	public RuntimeEngine produceRuntimeEngine() {
		RuntimeEnvironment env = RuntimeEnvironmentBuilder.getDefault()
				.persistence(true)
				.entityManagerFactory(emf)
				.registerableItemsFactory(
						InjectableRegisterableItemsFactory.getFactory(beanManager, AuditLoggerFactory.newJPAInstance(emf)))
						.userGroupCallback(callback)
						.addAsset(new ClassPathResource("Example1.bpmn"), ResourceType.BPMN2)
						.get();
		RuntimeManager manager = managerFactory.newSingletonRuntimeManager(env);
		RuntimeEngine runtime = manager.getRuntimeEngine(EmptyContext.get());
		JPAProcessInstanceDbLog.setEnvironment(env.getEnvironment());
		runtime.getKieSession().addEventListener(new TaskCleanUpProcessEventListener(runtime.getTaskService()));
		return runtime;
	}
}
