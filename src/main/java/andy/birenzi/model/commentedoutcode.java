

   /** Unused samples codes:
      * 
  //  https.addTargetGroups("WebTG",AddApplicationTargetGroupsProps.builder().targetGroups(targetGroups).priority(2).pathPattern("/").build());
           
        //  redirect.setCondition("Path", Arrays.asList("/"));
        
        //  redirect.
        
            // // Add Listener and Target group to ELB
            // loadBalancer.addListener("WebListenerHttp",webListenerHTTP).addRedirectResponse("Redirect", 
            // AddRedirectResponseProps.builder().host("duo.bowdoin.xyz").protocol("HTTPS").port("443").path("/duodevicemanagement").statusCode("HTTP_301").build());
            // loadBalancer.addListener("WebListenerHttps",webListenerHTTPs)
            // .addTargetGroups("ELBTargetGroup", AddApplicationTargetGroupsProps.builder().targetGroups(targetGroups).build())
            // ;

       createLinuxEc2Instance("WebServer", "WebServer", InstanceType.of(InstanceClass.BURSTABLE2, InstanceSize.MICRO),
                SubnetSelection.builder().subnetType(SubnetType.PUBLIC).onePerAz(true).build(), props.getVpc(), azNumber,
                webImage, webSG, webServerKeyName);

        
        createLinuxEc2Instance("ApplicationServer", "ApplicationServer",
                InstanceType.of(InstanceClass.BURSTABLE2, InstanceSize.MICRO),
                SubnetSelection.builder().subnetType(SubnetType.PRIVATE).onePerAz(true).build(), props.getVpc(),
                azNumber, webImage, applicationSG, applicationServerKeyName

        );

        AutoScalingGroup privateASG= AutoScalingGroup.Builder.create(this, "privateASG").vpc(props.getVpc())
        .instanceType( InstanceType.of(InstanceClass.BURSTABLE2, InstanceSize.MICRO)).machineImage(props.getAMIs())
        .minCapacity(2).maxCapacity(3).vpcSubnets(SubnetSelection.builder().subnets(props.getVpc().getPublicSubnets()).build())
        .keyName(applicationServerKeyName)
        .build();
        privateASG.getConnections().addSecurityGroup(applicationSG);

    // create servers, no longer being used because am using AutoScalingGroup
    private void createLinuxEc2Instance(final String id, final String instanceName, final InstanceType instanceType,
            final SubnetSelection subnet, final Vpc vpc, final int azNumber, final AmazonLinuxImage image,
            final SecurityGroup securityGroup, final String keyName) {
        for( int i=0; i< azNumber; i++){
            new Instance(this, id+i,
            InstanceProps.builder()
            .instanceType(instanceType)
            .machineImage(image)
            .vpc(vpc)
            .instanceName(instanceName+i)
            .securityGroup(securityGroup)
            .sourceDestCheck(false)
            
            .vpcSubnets(
               subnet)
            .availabilityZone(vpc.getAvailabilityZones().get(i))
            .keyName(keyName)
            .build());
            }
        } 
    
      
       
       
        **/