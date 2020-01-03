package andy.birenzi;

import software.amazon.awscdk.core.Construct;
import software.amazon.awscdk.core.Stack;
import software.amazon.awscdk.services.autoscaling.AutoScalingGroup;
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
import software.amazon.awscdk.services.ec2.SubnetSelection;
import software.amazon.awscdk.services.ec2.SubnetType;
import software.amazon.awscdk.services.ec2.Vpc;
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

        final SecurityGroup webSG = createSecurityGroup("WebServerSG", props.getVpc(), "WebServer layer SG", true);
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
        // create auto-scaling groups

        // create webservers
        createLinuxEc2Instance("WebServer", "WebServer", InstanceType.of(InstanceClass.BURSTABLE2, InstanceSize.MICRO),
                SubnetSelection.builder().subnetType(SubnetType.PUBLIC).onePerAz(true).build(), props.getVpc(), azNumber,
                webImage, webSG, webServerKeyName);

        // create API servers
        createLinuxEc2Instance("ApplicationServer", "ApplicationServer",
                InstanceType.of(InstanceClass.BURSTABLE2, InstanceSize.MICRO),
                SubnetSelection.builder().subnetType(SubnetType.PRIVATE).onePerAz(true).build(), props.getVpc(),
                azNumber, webImage, applicationSG, applicationServerKeyName

        );

        AutoScalingGroup webASG= AutoScalingGroup.Builder.create(this, "WebASG").vpc(props.getVpc())
        .instanceType( InstanceType.of(InstanceClass.BURSTABLE2, InstanceSize.MICRO)).machineImage(webImage)
        .minCapacity(2).maxCapacity(3)
        .build();
        // Create RDS cluster

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
 