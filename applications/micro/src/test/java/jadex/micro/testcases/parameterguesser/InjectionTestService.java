package jadex.micro.testcases.parameterguesser;

import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;
import jadex.bridge.component.IExecutionFeature;
import jadex.bridge.component.IPojoComponentFeature;
import jadex.bridge.service.annotation.ServiceComponent;

/**
 * Created by kalinowski on 19.09.16.
 */
public class InjectionTestService implements IInjectionTestService {


    @ServiceComponent
    private IInternalAccess access;

    @ServiceComponent
    private IExternalAccess extAcc;

    @ServiceComponent
    private IExecutionFeature exeFeat;

    @ServiceComponent
    private IPojoComponentFeature pojoFeat;

    @ServiceComponent
    private ProviderAgent pojo;

    @Override
    public Object[] getInjectionClasses() {
        return new Object[]{
                IInternalAccess.class,
                IExternalAccess.class,
                IExecutionFeature.class,
                IPojoComponentFeature.class,
                "Pojo object"
        };
    }

    @Override
    public Object[] getInjections() {
        return new Object[]{
                access,
                extAcc,
                exeFeat,
                pojoFeat,
                pojo
        };
    }

}
