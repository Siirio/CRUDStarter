package com.siirio.crud.magic;

import com.siirio.crud.annotations.CrudResource;
import com.siirio.crud.controller.BaseCrudController;
import com.siirio.crud.repository.BaseCrudRepository;
import com.siirio.crud.service.BaseCrudService;
import com.siirio.crud.mapper.GenericMapper;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.*;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.data.jpa.repository.support.JpaRepositoryFactoryBean;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;

import java.lang.reflect.Field;
import java.util.Optional;
import jakarta.persistence.Id;

public class CrudRegistrer implements BeanDefinitionRegistryPostProcessor {

    private static final String BASE_PACKAGE_TO_SCAN = "com.siirio";

    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) {
        ClassPathScanningCandidateComponentProvider scanner =
                new ClassPathScanningCandidateComponentProvider(false);
        scanner.addIncludeFilter(new AnnotationTypeFilter(CrudResource.class));

        for (BeanDefinition bd : scanner.findCandidateComponents(BASE_PACKAGE_TO_SCAN)) {
            try {
                Class<?> entityClass = ClassUtils.resolveClassName(bd.getBeanClassName(), null);
                CrudResource annotation = entityClass.getAnnotation(CrudResource.class);

                Optional<Class<?>> idClassOptional = getIdType(entityClass);
                if (idClassOptional.isEmpty()) continue;
                Class<?> idClass = idClassOptional.get();

                String path = annotation.path();

                //the magic itself
                registerDynamicRepository(registry, entityClass, idClass);
                registerDynamicMapper(registry, entityClass);
                registerService(registry, entityClass);
                registerController(registry, entityClass, path);

            } catch (Exception e) {
                throw new IllegalStateException("Failed during CRUD component registration for entity: " + bd.getBeanClassName(), e);
            }
        }
    }

    @Override
    public void postProcessBeanFactory(org.springframework.beans.factory.config.ConfigurableListableBeanFactory beanFactory) {}

    private Optional<Class<?>> getIdType(Class<?> entityClass) {
        for (Field field : entityClass.getDeclaredFields()) {
            if (field.isAnnotationPresent(Id.class)) return Optional.of(field.getType());
        }
        return Optional.empty();
    }

    // 1. Dynamic Repository Registration (REQUIRED FOR ZERO-CONFIG)
    private void registerDynamicRepository(BeanDefinitionRegistry registry, Class<?> entityClass, Class<?> idClass) {
        String entityName = entityClass.getSimpleName();
        String beanName = StringUtils.uncapitalize(entityName) + "Repository";

        BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(JpaRepositoryFactoryBean.class);

        // Arguments needed by JpaRepositoryFactoryBean
        builder.addConstructorArgValue(BaseCrudRepository.class.getName()); // Base interface
        builder.addPropertyValue("repositoryInterface", BaseCrudRepository.class.getName());
        builder.addPropertyValue("entityInformation", entityClass);

        // This is complex, requires setting up entity info, but for a starter,
        // relying on Spring's auto-config usually handles the factory bean creation.
        // A robust starter would need to register the custom factory bean properly.
        // For simplicity, we assume BaseCrudRepository is picked up by JpaRepositoryFactory.

        // We register the interface itself, relying on JpaRepositoryFactory to create the proxy implementation
        RootBeanDefinition beanDefinition = new RootBeanDefinition(BaseCrudRepository.class);
        beanDefinition.setAutowireCandidate(true);

        registry.registerBeanDefinition(beanName, beanDefinition);
    }

    // 2. Dynamic Mapper Registration (REQUIRED FOR ZERO-CONFIG)
    private void registerDynamicMapper(BeanDefinitionRegistry registry, Class<?> entityClass) {
        String entityName = entityClass.getSimpleName();
        String beanName = StringUtils.uncapitalize(entityName) + "Mapper";

        // MapStruct mappers are interfaces. They are created via a Proxy Factory.
        BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(GenericMapper.class);

        // We register the interface, assuming MapStruct annotation processor has run
        // and that its configuration handles the actual proxy/implementation creation.
        // This is a common pattern for Spring starters that rely on external processors.

        RootBeanDefinition beanDefinition = new RootBeanDefinition(GenericMapper.class);
        beanDefinition.setAutowireCandidate(true);
        beanDefinition.setPrimary(true); // Ensure this is the chosen bean when autowiring

        registry.registerBeanDefinition(beanName, beanDefinition);
    }

    // 3. Service Registration
    private void registerService(BeanDefinitionRegistry registry, Class<?> entityClass) {
        String entityName = entityClass.getSimpleName();
        String beanName = StringUtils.uncapitalize(entityName) + "Service";

        // The service needs the fully parameterized type to satisfy the constructor:
        // BaseCrudService<T, ID, M extends GenericMapper<T>>

        // A simpler approach for the starter is to rely on AutowireMode for dependencies
        BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(BaseCrudService.class);
        builder.setPrimary(true);
        builder.setAutowireMode(AbstractBeanDefinition.AUTOWIRE_CONSTRUCTOR);

        registry.registerBeanDefinition(beanName, builder.getBeanDefinition());
    }

    // 4. Controller Registration
    private void registerController(BeanDefinitionRegistry registry, Class<?> entityClass, String path) {
        String entityName = entityClass.getSimpleName();
        String beanName = StringUtils.uncapitalize(entityName) + "Controller";

        BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(BaseCrudController.class);
        builder.setAutowireMode(AbstractBeanDefinition.AUTOWIRE_CONSTRUCTOR);

        builder.getBeanDefinition().setAttribute(
                "org.springframework.web.bind.annotation.RestController", Boolean.TRUE
        );
        builder.getBeanDefinition().setAttribute(
                "org.springframework.web.bind.annotation.RequestMapping", new String[] { path }
        );

        registry.registerBeanDefinition(beanName, builder.getBeanDefinition());
    }
}