package andy.birenzi;

import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.fasterxml.jackson.databind.node.ArrayNode;

import software.amazon.awscdk.core.Construct;
import software.amazon.awscdk.core.ConstructNode;
import software.amazon.awscdk.core.IConstruct;
import software.amazon.awscdk.core.IDependable;
import software.amazon.awscdk.core.Stack;
import software.amazon.awscdk.services.autoscaling.AutoScalingGroup;
import software.amazon.awscdk.services.ec2.AmazonLinuxEdition;
import software.amazon.awscdk.services.ec2.AmazonLinuxGeneration;
import software.amazon.awscdk.services.ec2.AmazonLinuxImage;
import software.amazon.awscdk.services.ec2.AmazonLinuxStorage;
import software.amazon.awscdk.services.ec2.AmazonLinuxVirt;
import software.amazon.awscdk.services.ec2.IConnectable;
import software.amazon.awscdk.services.ec2.Instance;
import software.amazon.awscdk.services.ec2.InstanceClass;
import software.amazon.awscdk.services.ec2.InstanceProps;
import software.amazon.awscdk.services.ec2.InstanceSize;
import software.amazon.awscdk.services.ec2.InstanceType;
import software.amazon.awscdk.services.ec2.Peer;
import software.amazon.awscdk.services.ec2.Port;
import software.amazon.awscdk.services.ec2.Protocol;
import software.amazon.awscdk.services.ec2.SecurityGroupProps;
import software.amazon.awscdk.services.ec2.SelectedSubnets;
import software.amazon.awscdk.services.ec2.SubnetSelection;
import software.amazon.awscdk.services.ec2.SubnetType;
import software.amazon.awscdk.services.ec2.Vpc;
import software.amazon.awscdk.services.elasticloadbalancingv2.AddApplicationTargetGroupsProps;
import software.amazon.awscdk.services.elasticloadbalancingv2.AddApplicationTargetsProps;
import software.amazon.awscdk.services.elasticloadbalancingv2.ApplicationListener;
import software.amazon.awscdk.services.elasticloadbalancingv2.ApplicationListenerProps;
import software.amazon.awscdk.services.elasticloadbalancingv2.ApplicationLoadBalancer;
import software.amazon.awscdk.services.elasticloadbalancingv2.ApplicationProtocol;
import software.amazon.awscdk.services.elasticloadbalancingv2.ApplicationTargetGroup;
import software.amazon.awscdk.services.elasticloadbalancingv2.ApplicationTargetGroupProps;
import software.amazon.awscdk.services.elasticloadbalancingv2.BaseApplicationListenerProps;
import software.amazon.awscdk.services.elasticloadbalancingv2.IApplicationListener;
import software.amazon.awscdk.services.elasticloadbalancingv2.IApplicationLoadBalancerTarget;
import software.amazon.awscdk.services.elasticloadbalancingv2.IApplicationTargetGroup;
import software.amazon.awscdk.services.elasticloadbalancingv2.LoadBalancerTargetProps;
import software.amazon.awscdk.services.elasticloadbalancingv2.TargetGroupAttributes;
import software.amazon.awscdk.services.elasticloadbalancingv2.TargetType;
import software.amazon.awscdk.services.ec2.SecurityGroup;


public class InstanceStack  extends Stack  {
      
    public InstanceStack(final Construct scope, final String id) {
        this(scope, id, null);
    }

    public InstanceStack(final Construct scope, final String id, final SharedVpcPros props) {
        super(scope, id, props);
        final int azNumber = 2;
        final String webServerKeyName = "WebServerKeys";
        final String applicationServerKeyName = "ApplicationServersKey";
       

        // //Create Image
        final AmazonLinuxImage webImage = AmazonLinuxImage.Builder.create()
                .generation(AmazonLinuxGeneration.AMAZON_LINUX).edition(AmazonLinuxEdition.STANDARD)
                .virtualization(AmazonLinuxVirt.HVM).storage(AmazonLinuxStorage.GENERAL_PURPOSE).build();

        // //create security group

        final SecurityGroup webSG = createSecurityGroup("cdk", props.getVpc(), "WebServer layer SG", true);
        // // allowing SSH
        webSG.addIngressRule(Peer.anyIpv4(), Port.tcp(22), "Allow SSH access from the world ");
        webSG.addIngressRule(Peer.anyIpv4(), Port.tcp(80), "Allow HTTP on port 80 access from the world ");
        webSG.addIngressRule(Peer.anyIpv4(), Port.tcp(443), "Allow HTTP on port 80 access from the world ");

        final SecurityGroup applicationSG = createSecurityGroup("ApplicationSG", props.getVpc(), "Application layer SG",
                true);
        // // allowing SSH
        applicationSG.addIngressRule(webSG, Port.tcp(22), "Allow SSH access from the world ");
        applicationSG.addIngressRule(webSG, Port.tcp(8082), "Allow HTTP on port 8082 access from the world ");
        applicationSG.addIngressRule(webSG, Port.tcp(443), "Allow HTTPs on port 8082 access from the world ");
       

                                  
        // create webservers
        // createLinuxEc2Instance("WebServer", "WebServer", InstanceType.of(InstanceClass.BURSTABLE2, InstanceSize.MICRO),
        //         SubnetSelection.builder().subnetType(SubnetType.PUBLIC).onePerAz(true).build(), props.getVpc(), azNumber,
        //         webImage, webSG, webServerKeyName);

        // create API servers
        // createLinuxEc2Instance("ApplicationServer", "ApplicationServer",
        //         InstanceType.of(InstanceClass.BURSTABLE2, InstanceSize.MICRO),
        //         SubnetSelection.builder().subnetType(SubnetType.PRIVATE).onePerAz(true).build(), props.getVpc(),
        //         azNumber, webImage, applicationSG, applicationServerKeyName

        // );

        AutoScalingGroup webASG= AutoScalingGroup.Builder.create(this, "WebASG").vpc(props.getVpc())
        .instanceType( InstanceType.of(InstanceClass.BURSTABLE2, InstanceSize.MICRO)).machineImage(webImage)
        .minCapacity(2).maxCapacity(3).vpcSubnets(SubnetSelection.builder().subnets(props.getVpc().getPublicSubnets()).build())
        .keyName(webServerKeyName)
        .build();
        webASG.getConnections().addSecurityGroup(webSG);


        AutoScalingGroup privateASG= AutoScalingGroup.Builder.create(this, "privateASG").vpc(props.getVpc())
        .instanceType( InstanceType.of(InstanceClass.BURSTABLE2, InstanceSize.MICRO)).machineImage(webImage)
        .minCapacity(2).maxCapacity(3).vpcSubnets(SubnetSelection.builder().subnets(props.getVpc().getPublicSubnets()).build())
        .keyName(applicationServerKeyName)
        .build();
        privateASG.getConnections().addSecurityGroup(applicationSG);
        // privateASG.scaleOnCpuUtilization("cpuUtilization",CpuUti)
       
      
       
        
        // webASG.attachToApplicationTargetGroup();
        // test.addTargets("", new AddApplicationTargetsProps.Builder().targets().build());
        
       
        final ApplicationLoadBalancer webELB= ApplicationLoadBalancer.Builder.create(this, "webELB")
                                            .vpc(props.getVpc())
                                            .internetFacing(true)
                                            .loadBalancerName("webELB")
                                            // .vpcSubnets(props.getVpc().getPublicSubnets())
                                            .build();

        
        BaseApplicationListenerProps webListener= BaseApplicationListenerProps.builder().port(80).open(true).build();
        AddApplicationTargetGroupsProps.builder().targetGroups(new ArrayList<IApplicationTargetGroup>());
        

        List<IApplicationLoadBalancerTarget> targets=new ArrayList<IApplicationLoadBalancerTarget>();
        targets.add(webASG);
        ApplicationTargetGroup webTargetGroup= new ApplicationTargetGroup(this, "id", ApplicationTargetGroupProps.builder()
                                    .vpc(props.getVpc()).targetType(TargetType.INSTANCE)
                                    .targets(targets)
                                    .port(80).protocol(ApplicationProtocol.HTTPS).build());
        
        List<IApplicationTargetGroup> targetGroups=new ArrayList<IApplicationTargetGroup>();
        targetGroups.add(webTargetGroup);
        
        // test.addTarget(target);
        webELB.addListener("WebListener",webListener).addTargetGroups(id, AddApplicationTargetGroupsProps.builder().targetGroups(targetGroups).build());
       
         
        // ApplicationTargetGroup applicationFleet=  new ApplicationTargetGroup(this, "AppFleet", ApplicationTargetGroupProps.builder().targets(Itargets).build());
        // BaseApplicationListenerProps applicationListener= BaseApplicationListenerProps.builder().port(80).open(false).build();
        // final ApplicationLoadBalancer applicationELB= ApplicationLoadBalancer.Builder.create(this, "applicationELB")
        //                                                 .vpc(props.getVpc())
        //                                                 .internetFacing(false)
        //                                                 .loadBalancerName("ApplicationELB")
        //                                                 .build();    
        
        // applicationELB.addListener("ApplicationListner", applicationListener);
              
       

    }

    // create SGs
    private SecurityGroup createSecurityGroup(final String id, final Vpc vpc, final String description,
            final boolean allowAllOutbound) {

        return new SecurityGroup(this, id, SecurityGroupProps.builder().vpc(vpc).description(description)
                .allowAllOutbound(allowAllOutbound).build());
    }

    // create servers
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
   


    
}
 