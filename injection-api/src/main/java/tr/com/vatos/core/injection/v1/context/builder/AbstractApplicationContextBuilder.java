package tr.com.vatos.core.injection.v1.context.builder;

import tr.com.vatos.core.builder.impl.OuterChildBuilder;
import tr.com.vatos.core.common.utils.NullCheckUtils;
import tr.com.vatos.core.injection.v1.annotations.Configuration;
import tr.com.vatos.core.injection.v1.application.builder.ApplicationBuilder;
import tr.com.vatos.core.injection.v1.context.ApplicationContext;
import tr.com.vatos.core.injection.v1.context.ContextType;
import tr.com.vatos.core.reflections.classes.ClassManager;

import java.util.Collection;
import java.util.HashSet;

public abstract class AbstractApplicationContextBuilder<T extends AbstractApplicationContextBuilder> extends OuterChildBuilder<ApplicationBuilder,ApplicationBuilder.ApplicationBuildingContext> {

    protected final ContextType contextType;
    protected final ClassManager classManager;
    protected final Collection<Class<?>> configurations;

    protected AbstractApplicationContextBuilder(ApplicationBuilder parent, ApplicationBuilder.ApplicationBuildingContext context,ContextType contextType) {
        super(parent, context);
        this.contextType = contextType;
        this.classManager = ClassManager.instance();
        this.configurations = new HashSet<Class<?>>();
    }

    protected abstract ApplicationContext buildApplicationContext();

    public T addConfiguration(Class<?> cls)
    {
        addConfigurationClass(cls);
        return (T) this;
    }

    private void addConfigurationClass(Class<?> cls)
    {
        if(classManager.classHasAnnotation(cls, Configuration.class))
        {
            this.configurations.add(cls);
            Configuration configuration = classManager.getAnnotationFromClassWithoutNullCheck(cls, Configuration.class);
            if(NullCheckUtils.isNotArrayEmpty(configuration.imports()))
            {
                for(Class<?> imported : configuration.imports())
                {
                    addConfiguration(imported);
                }
            }
        }
        else
        {
            //TODO throw exception!
            System.out.println("not added " + cls.getSimpleName());
        }
    }



    @Override
    protected void beforeEnd() {
        getContext().put(this.contextType,buildApplicationContext());
    }
}
