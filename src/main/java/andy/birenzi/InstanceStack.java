package andy.birenzi;

import java.util.ArrayList;
import java.util.List;


import software.amazon.awscdk.core.Construct;
import software.amazon.awscdk.core.Stack;
import software.amazon.awscdk.services.autoscaling.AutoScalingGroup;
import software.amazon.awscdk.services.ec2.InstanceClass;
import software.amazon.awscdk.services.ec2.InstanceSize;
import software.amazon.awscdk.services.ec2.InstanceType;
import software.amazon.awscdk.services.ec2.Peer;
import software.amazon.awscdk.services.ec2.Port;
import software.amazon.awscdk.services.ec2.SecurityGroupProps;
import software.amazon.awscdk.services.ec2.SubnetSelection;
import software.amazon.awscdk.services.ec2.Vpc;
import software.amazon.awscdk.services.elasticloadbalancingv2.AddApplicationTargetGroupsProps;
import software.amazon.awscdk.services.elasticloadbalancingv2.ApplicationLoadBalancer;
import software.amazon.awscdk.services.elasticloadbalancingv2.ApplicationProtocol;
import software.amazon.awscdk.services.elasticloadbalancingv2.ApplicationTargetGroup;
import software.amazon.awscdk.services.elasticloadbalancingv2.ApplicationTargetGroupProps;
import software.amazon.awscdk.services.elasticloadbalancingv2.BaseApplicationListenerProps;
import software.amazon.awscdk.services.elasticloadbalancingv2.IApplicationLoadBalancerTarget;
import software.amazon.awscdk.services.elasticloadbalancingv2.IApplicationTargetGroup;
import software.amazon.awscdk.services.elasticloadbalancingv2.TargetType;
import software.amazon.awscdk.services.ec2.SecurityGroup;


public class InstanceStack  extends Stack  {
      
    public InstanceStack(final Construct scope, final String id) {
        this(scope, id, null);
    }

    public InstanceStack(final Construct scope, final String id, final SharedPros props) {
        super(scope, id, props);
        final String webServerKeyName = "WebServerKeys";
        final MyUserData myUserData = new MyUserData();
        // final String applicationServerKeyName = "ApplicationServersKey";
       
        // //create security groups

        final SecurityGroup webSG = createSecurityGroup("webSG", props.getVpc(), "WebServer layer SG", true);
        // // allowing SSH
        webSG.addIngressRule(Peer.anyIpv4(), Port.tcp(22), "Allow SSH access from the world ");
        webSG.addIngressRule(Peer.anyIpv4(), Port.tcp(80), "Allow HTTP on port 80 access from the world ");
        webSG.addIngressRule(Peer.anyIpv4(), Port.tcp(443), "Allow HTTPs on port 443 access from the world ");

        final SecurityGroup applicationSG = createSecurityGroup("ApplicationSG", props.getVpc(), "Application layer SG",
                true);
        // // allowing SSH
        applicationSG.addIngressRule(webSG, Port.tcp(22), "Allow SSH access from the world ");
        applicationSG.addIngressRule(webSG, Port.tcp(8082), "Allow HTTP on port 8082 access from the world ");
        applicationSG.addIngressRule(webSG, Port.tcp(443), "Allow HTTPs on port 8082 access from the world ");
       

                                  
       

         AutoScalingGroup webASG= AutoScalingGroup.Builder.create(this, "WebASG").vpc(props.getVpc())
        .instanceType( InstanceType.of(InstanceClass.BURSTABLE2, InstanceSize.MICRO)).machineImage(props.getAMIs())
        .minCapacity(2).maxCapacity(3).vpcSubnets(SubnetSelection.builder().subnets(props.getVpc().getPublicSubnets()).build())
        .keyName(webServerKeyName)
        .userData(myUserData.getUserData())
        .build();
        webASG.addSecurityGroup(webSG);
       
        final ApplicationLoadBalancer webELB= ApplicationLoadBalancer.Builder.create(this, "webELB")
                                            .vpc(props.getVpc())
                                            .internetFacing(true)
                                            .loadBalancerName("webELB")
                                            //.vpcSubnets(props.getVpc().getPublicSubnets())
                                            .build();

        //Create a Listener
        BaseApplicationListenerProps webListener= BaseApplicationListenerProps
                                                  .builder().port(80).open(true).build();

        //Add AutoScaling group as an ELB target
        List<IApplicationLoadBalancerTarget> targets=new ArrayList<IApplicationLoadBalancerTarget>();
        targets.add(webASG);

        //Create a target group
        ApplicationTargetGroup webTargetGroup= new ApplicationTargetGroup(this, "id", ApplicationTargetGroupProps.builder()
                                    .vpc(props.getVpc()).targetType(TargetType.INSTANCE)
                                    .targets(targets)
                                    .port(80).protocol(ApplicationProtocol.HTTP)
                                    .build());

        //Add targets to Target group
        List<IApplicationTargetGroup> targetGroups=new ArrayList<IApplicationTargetGroup>();
        targetGroups.add(webTargetGroup);
        
        // Add Listener and Target group to ELB
        webELB.addListener("WebListener",webListener).addTargetGroups(id, AddApplicationTargetGroupsProps.builder().targetGroups(targetGroups).build());
              
       

    }

    // create SGs
    private SecurityGroup createSecurityGroup(final String id, final Vpc vpc, final String description,
            final boolean allowAllOutbound) {

        return new SecurityGroup(this, id, SecurityGroupProps.builder().vpc(vpc).description(description)
                .allowAllOutbound(allowAllOutbound).securityGroupName(this.getStackName()+"-"+id).build());
    }
     /** Unused samples codes:
      * 
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

    
}
 