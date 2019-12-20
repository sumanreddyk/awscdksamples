package com.myorg;

import software.amazon.awscdk.core.Construct;
import software.amazon.awscdk.core.Stack;
import software.amazon.awscdk.core.StackProps;
import software.amazon.awscdk.services.ec2.SubnetConfiguration;
import software.amazon.awscdk.services.ec2.SubnetType;
import software.amazon.awscdk.services.ec2.Vpc;
import software.amazon.awscdk.services.ec2.VpcProps;


import java.util.*;

public class SandboxStack extends Stack {
    private  Vpc vpc=null;
    public SandboxStack(final Construct scope, final String id) {
        this(scope, id, null);
    }

    public SandboxStack(final Construct scope, final String id, final StackProps props) {
        super(scope, id, props);
        int azNumber =2;
      
       
        SubnetConfiguration pu=SubnetConfiguration.builder().name("web").subnetType(SubnetType.PUBLIC).build();
        SubnetConfiguration pr=SubnetConfiguration.builder().name("application").subnetType(SubnetType.PRIVATE).build();
        SubnetConfiguration is=SubnetConfiguration.builder().name("rds").subnetType(SubnetType.ISOLATED).build();
    

        // Vpc birenzi = new Vpc(this, "BIrenzi");
        //Create VPC
         this.vpc = new Vpc(this, "birenzi",VpcProps.builder().cidr("10.0.0.0/24")
                            .enableDnsHostnames(false) .enableDnsSupport(false).maxAzs(azNumber)
                            .subnetConfiguration(Arrays.asList(pu, pr,is))
                            .natGateways(2)
                            .build()
                            );

    }
    public Vpc getVpc(){
        return this.vpc;
    }
    
}
 