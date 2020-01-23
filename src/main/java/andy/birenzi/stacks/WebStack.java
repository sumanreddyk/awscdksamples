package andy.birenzi.stacks;

import andy.birenzi.model.MyUserData;
import andy.birenzi.props.Ec2Props;
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

import software.amazon.awscdk.services.ec2.SecurityGroup;

public class WebStack extends Stack {
    private AutoScalingGroup webASG;

    public WebStack(final Construct scope, final String id) {
        this(scope, id, null);
    }

    public WebStack(final Construct scope, final String id, final Ec2Props props) {
        super(scope, id, props);
        final String webServerKeyName = "WebServerKeys";
       
       final MyUserData myUserData = new MyUserData();
       
       SecurityGroup webSG = createSecurityGroup("webSG", props.getVpc(), "WebServer layer SG", true);
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
  
         this.webASG= AutoScalingGroup.Builder.create(this, "WebASG").vpc(props.getVpc())
        .instanceType( InstanceType.of(InstanceClass.BURSTABLE2, InstanceSize.MICRO)).machineImage(props.getAMIs())
        .minCapacity(1).maxCapacity(3).vpcSubnets(SubnetSelection.builder().subnets(props.getVpc().getPublicSubnets()).build())
        .keyName(webServerKeyName)
        .role(props.getRole())
        .userData(myUserData.getUserData())
        .build();
        webASG.addSecurityGroup(webSG);
 
    }

    // create SGs
    private SecurityGroup createSecurityGroup(final String id, final Vpc vpc, final String description,
            final boolean allowAllOutbound) {

        return new SecurityGroup(this, id, SecurityGroupProps.builder().vpc(vpc).description(description)
                .allowAllOutbound(allowAllOutbound).securityGroupName(this.getStackName()+"-"+id).build());
    }
    public AutoScalingGroup getAutoScalingGroup(){
        return this.webASG;
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
 