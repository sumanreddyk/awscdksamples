package com.myorg;

import software.amazon.awscdk.core.Construct;
import software.amazon.awscdk.core.Stack;
import software.amazon.awscdk.core.StackProps;
import software.amazon.awscdk.services.ec2.AmazonLinuxEdition;
import software.amazon.awscdk.services.ec2.AmazonLinuxGeneration;
import software.amazon.awscdk.services.ec2.AmazonLinuxImage;
import software.amazon.awscdk.services.ec2.AmazonLinuxStorage;
import software.amazon.awscdk.services.ec2.AmazonLinuxVirt;
import software.amazon.awscdk.services.ec2.Instance;
import software.amazon.awscdk.services.ec2.InstanceClass;
import software.amazon.awscdk.services.ec2.InstanceProps;
import software.amazon.awscdk.services.ec2.InstanceSize;
import software.amazon.awscdk.services.ec2.InstanceType;
import software.amazon.awscdk.services.ec2.Peer;
import software.amazon.awscdk.services.ec2.Port;
import software.amazon.awscdk.services.ec2.SecurityGroupProps;
import software.amazon.awscdk.services.ec2.SubnetConfiguration;
import software.amazon.awscdk.services.ec2.SubnetType;
import software.amazon.awscdk.services.ec2.Vpc;
import software.amazon.awscdk.services.ec2.VpcProps;
import software.amazon.awscdk.services.ec2.SecurityGroup;


import java.util.*;

public class SandboxStack extends Stack {
    public SandboxStack(final Construct scope, final String id) {
        this(scope, id, null);
    }

    public SandboxStack(final Construct scope, final String id, final StackProps props) {
        super(scope, id, props);
        
        SubnetConfiguration pu=SubnetConfiguration.builder().name("web").subnetType(SubnetType.PUBLIC).build();
        SubnetConfiguration pr=SubnetConfiguration.builder().name("application").subnetType(SubnetType.PRIVATE).build();
        SubnetConfiguration is=SubnetConfiguration.builder().name("rds").subnetType(SubnetType.ISOLATED).build();
    

        // Vpc birenzi = new Vpc(this, "BIrenzi");
        //Create VPC
        Vpc birenzi = new Vpc(this,
        // //VPC ID
          "birenzi",
        //  //VpC properties
        VpcProps.builder()
        // //cidr
        .cidr("10.0.0.0/24")
         .enableDnsHostnames(false)
         .enableDnsSupport(false)
        // //AZs
         .maxAzs(2)
        // //define subnets to build for each AZ
         .subnetConfiguration(Arrays.asList(pu, pr,is))
        // //define number of NatGateways 
         .natGateways(2)
        // //type of NAT
        // // .natGatewayProvider(NatProvider.instance())
         .build()
         );

    //     //create security group
        SecurityGroup web = new SecurityGroup(this,"weSecurityGroup"
        ,SecurityGroupProps
        .builder()
        .vpc(birenzi)
        .description("webSecurityGroup")
        .allowAllOutbound(true)
        .build()
        );
    //     // allowing SSH
        web.addIngressRule(Peer.anyIpv4(), Port.tcp(22),"Allow SSH access from the world ");
        
    //     //Create Image
      AmazonLinuxImage webImage = 
      AmazonLinuxImage.Builder.create()
      .generation(AmazonLinuxGeneration.AMAZON_LINUX)
      .edition(AmazonLinuxEdition.STANDARD)
      .virtualization(AmazonLinuxVirt.HVM)
      .storage(AmazonLinuxStorage.GENERAL_PURPOSE)
      .build();
    //    //create UserData
    //   UserData
    
    //   //Select Subnets
    // SubnetSelection sub = SubnetSelection.builder()
    //  .
    //  .build() ;

      Instance webServer = new Instance(this, "webServer",
       InstanceProps.builder()
       .instanceType(InstanceType.of(
           InstanceClass.BURSTABLE2, InstanceSize.MICRO))
       .machineImage(webImage)
       .vpc(birenzi)
       .instanceName("WebServer")
       .keyName("BirenziWebServer")
       .securityGroup(web)
       .sourceDestCheck(false)
    //    .vpcSubnets( )
    //    .userData(userDat)
    // .VpcSubnet
       .build());

       
        
    }
}
 