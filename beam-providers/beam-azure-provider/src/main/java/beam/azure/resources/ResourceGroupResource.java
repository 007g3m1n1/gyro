package beam.azure.resources;

import beam.azure.AzureResource;
import beam.core.diff.ResourceDiffProperty;
import beam.core.diff.ResourceName;
import beam.lang.Resource;
import com.microsoft.azure.management.Azure;
import com.microsoft.azure.management.resources.ResourceGroup;
import com.microsoft.azure.management.resources.fluentcore.arm.Region;
import com.psddev.dari.util.ObjectUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Creates a resource group.
 *
 * Example
 * -------
 *
 * .. code-block:: beam
 *
 *     azure::resource-group resource-group-example
 *         resource-group-name: "resource-group-example"
 *
 *         tags: {
 *             Name: "resource-group-example"
 *         }
 *     end
 */
@ResourceName("resource-group")
public class ResourceGroupResource extends AzureResource {

    private String resourceGroupName;
    private String resourceGroupId;

    private Map<String, String> tags;

    /**
     * The name of the resource group. (Required)
     */
    public String getResourceGroupName() {
        return resourceGroupName;
    }

    public void setResourceGroupName(String resourceGroupName) {
        this.resourceGroupName = resourceGroupName;
    }

    public String getResourceGroupId() {
        return resourceGroupId;
    }

    public void setResourceGroupId(String resourceGroupId) {
        this.resourceGroupId = resourceGroupId;
    }

    @ResourceDiffProperty(updatable = true, nullable = true)
    public Map<String, String> getTags() {
        if (tags == null) {
            tags = new HashMap<>();
        }

        return tags;
    }

    public void setTags(Map<String, String> tags) {
        this.tags = tags;
    }

    @Override
    public boolean refresh() {
        Azure client = createClient();

        if (!client.resourceGroups().contain(getResourceGroupName())) {
            return false;
        }

        ResourceGroup resourceGroup = client.resourceGroups().getByName(getResourceGroupName());
        setResourceGroupId(resourceGroup.id());
        setTags(resourceGroup.tags());

        return true;
    }

    @Override
    public void create() {
        Azure client = createClient();

        ResourceGroup resourceGroup = client.resourceGroups()
            .define(getResourceGroupName())
            .withRegion(Region.fromName(getRegion()))
            .create();

        setResourceGroupId(resourceGroup.id());

        resourceGroup.update().withTags(getTags()).apply();
    }

    @Override
    public void update(Resource current, Set<String> changedProperties) {
        Azure client = createClient();

        ResourceGroup resourceGroup = client.resourceGroups().getByName(getResourceGroupName());

        resourceGroup.update().withTags(getTags()).apply();
    }

    @Override
    public void delete() {
        Azure client = createClient();

        client.resourceGroups().deleteByName(getResourceGroupName());
    }

    @Override
    public String toDisplayString() {
        StringBuilder sb = new StringBuilder();

        sb.append("resource group");

        if (!ObjectUtils.isBlank(getResourceGroupName())) {
            sb.append(" - ").append(getResourceGroupName());
        }

        return sb.toString();
    }
}