package beam.plugins.enterprise;

import beam.lang.plugins.Provider;

public class EnterprisePlugin extends Provider {

    @Override
    public String name() {
        return "enterprise";
    }

    @Override
    public void init() {
        getScope().getResourceTypes().put("aws::credentials", EnterpriseAwsCredentials.class);
    }

}
