package beam.aws.resources;

import beam.aws.AwsCloud;
import beam.core.BeamException;
import beam.core.BeamResource;
import beam.core.diff.ResourceDiffProperty;
import com.amazonaws.services.ec2.AmazonEC2Client;
import com.amazonaws.services.ec2.model.CreateVpcRequest;
import com.amazonaws.services.ec2.model.DeleteVpcRequest;
import com.amazonaws.services.ec2.model.DescribeVpcAttributeRequest;
import com.amazonaws.services.ec2.model.DescribeVpcsRequest;
import com.amazonaws.services.ec2.model.ModifyVpcAttributeRequest;
import com.amazonaws.services.ec2.model.Vpc;
import com.amazonaws.services.ec2.model.VpcAttributeName;
import com.psddev.dari.util.ObjectUtils;

import java.util.Set;

public class VpcResource extends TaggableResource<Vpc> {

    private String vpcId;
    private String cidrBlock;
    private Boolean enableDnsHostnames;
    private Boolean enableDnsSupport;

    public String getId() {
        return getVpcId();
    }

    public String getVpcId() {
        return vpcId;
    }

    public void setVpcId(String vpcId) {
        this.vpcId = vpcId;
    }

    @ResourceDiffProperty
    public String getCidrBlock() {
        return cidrBlock;
    }

    public void setCidrBlock(String cidrBlock) {
        this.cidrBlock = cidrBlock;
    }

    @ResourceDiffProperty(updatable = true)
    public Boolean getEnableDnsHostnames() {
        return enableDnsHostnames;
    }

    public void setEnableDnsHostnames(Boolean enableDnsHostnames) {
        this.enableDnsHostnames = enableDnsHostnames;
    }

    @ResourceDiffProperty(updatable = true)
    public Boolean getEnableDnsSupport() {
        return enableDnsSupport;
    }

    public void setEnableDnsSupport(Boolean enableDnsSupport) {
        this.enableDnsSupport = enableDnsSupport;
    }

    @Override
    public void refresh(AwsCloud cloud) {
        AmazonEC2Client client = createClient(AmazonEC2Client.class, cloud.getProvider());

        if (ObjectUtils.isBlank(getVpcId())) {
            throw new BeamException("vpc-id is missing, unable to load vpc.");
        }

        DescribeVpcsRequest request = new DescribeVpcsRequest();
        request.withVpcIds(getVpcId());

        for (Vpc vpc : client.describeVpcs(request).getVpcs()) {
            doInit(cloud, vpc);
            break;
        }
    }

    @Override
    protected void doInit(AwsCloud cloud, Vpc vpc) {
        AmazonEC2Client ec2Client = createClient(AmazonEC2Client.class, cloud.getProvider());
        String vpcId = vpc.getVpcId();

        setCidrBlock(vpc.getCidrBlock());
        setVpcId(vpcId);

        // VPC attributes.
        DescribeVpcAttributeRequest dvaRequest = new DescribeVpcAttributeRequest();

        dvaRequest.setVpcId(vpcId);
        dvaRequest.setAttribute(VpcAttributeName.EnableDnsHostnames);
        setEnableDnsHostnames(ec2Client.
                describeVpcAttribute(dvaRequest).
                isEnableDnsHostnames());

        dvaRequest.setAttribute(VpcAttributeName.EnableDnsSupport);
        setEnableDnsSupport(ec2Client.
                describeVpcAttribute(dvaRequest).
                isEnableDnsSupport());
    }

    @Override
    protected void doCreate(AwsCloud cloud) {
        AmazonEC2Client client = createClient(AmazonEC2Client.class, cloud.getProvider());
        CreateVpcRequest cvRequest = new CreateVpcRequest();

        cvRequest.setCidrBlock(getCidrBlock());
        setVpcId(client.createVpc(cvRequest).getVpc().getVpcId());

        modifyAttributes(client);
    }

    @Override
    protected void doUpdate(AwsCloud cloud, AwsResource current, Set<String> changedProperties) {
        AmazonEC2Client client = createClient(AmazonEC2Client.class, cloud.getProvider());

        modifyAttributes(client);
    }

    private void modifyAttributes(AmazonEC2Client client) {
        String vpcId = getVpcId();
        Boolean dnsHostnames = getEnableDnsHostnames();

        if (dnsHostnames != null) {
            ModifyVpcAttributeRequest mvaRequest = new ModifyVpcAttributeRequest();

            mvaRequest.setVpcId(vpcId);
            mvaRequest.setEnableDnsHostnames(dnsHostnames);
            client.modifyVpcAttribute(mvaRequest);
        }

        Boolean dnsSupport = getEnableDnsSupport();

        if (dnsSupport != null) {
            ModifyVpcAttributeRequest mvaRequest = new ModifyVpcAttributeRequest();

            mvaRequest.setVpcId(vpcId);
            mvaRequest.setEnableDnsSupport(dnsSupport);
            client.modifyVpcAttribute(mvaRequest);
        }
    }

    @Override
    public void delete(AwsCloud cloud) {
        AmazonEC2Client client = createClient(AmazonEC2Client.class, cloud.getProvider());
        DeleteVpcRequest dvRequest = new DeleteVpcRequest();

        dvRequest.setVpcId(getVpcId());
        client.deleteVpc(dvRequest);
    }

    @Override
    public String toDisplayString() {
        StringBuilder sb = new StringBuilder();
        String vpcId = getVpcId();

        if (vpcId != null) {
            sb.append(vpcId);

        } else {
            sb.append("VPC");
        }

        String cidrBlock = getCidrBlock();

        if (cidrBlock != null) {
            sb.append(' ');
            sb.append(cidrBlock);
        }

        return sb.toString();
    }
}