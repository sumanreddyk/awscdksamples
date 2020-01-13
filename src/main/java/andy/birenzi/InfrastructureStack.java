package andy.birenzi;

import software.amazon.awscdk.core.Construct;
import software.amazon.awscdk.core.Stack;
import software.amazon.awscdk.core.StackProps;
import software.amazon.awscdk.services.ec2.SubnetConfiguration;
import software.amazon.awscdk.services.ec2.SubnetType;
import software.amazon.awscdk.services.ec2.Vpc;
import software.amazon.awscdk.services.ec2.VpcProps;


import java.util.*;

public class InfrastructureStack extends Stack {
    private  Vpc vpc=null;
    private final String cidr="10.0.0.0/24";
    public InfrastructureStack(final Construct scope, final String id) {
        this(scope, id, null);
    }

    public InfrastructureStack(final Construct scope, final String id, final StackProps props) {
        super(scope, id, props);
        int azNumber =2;
      
       
        SubnetConfiguration publicSubnet=SubnetConfiguration.builder().name("web").subnetType(SubnetType.PUBLIC).build();
        SubnetConfiguration privateSubnet=SubnetConfiguration.builder().name("application").subnetType(SubnetType.PRIVATE).build();
        SubnetConfiguration IsolatedSubnet=SubnetConfiguration.builder().name("rds").subnetType(SubnetType.ISOLATED).build();
    

        // Vpc birenzi = new Vpc(this, "BIrenzi");
        //Create VPC
         this.vpc = new Vpc(this, "birenzi",VpcProps.builder().cidr(cidr)
                            .enableDnsHostnames(false) .enableDnsSupport(true).maxAzs(azNumber)
                            .subnetConfiguration(Arrays.asList(publicSubnet, privateSubnet,IsolatedSubnet))
                            .natGateways(2)
                            .build()
                            );
            

    }
    public Vpc getVpc(){
        return this.vpc;
    }
    
}
 