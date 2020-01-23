package andy.birenzi.props;

import software.amazon.awscdk.core.StackProps;
import software.amazon.awscdk.services.ec2.AmazonLinuxImage;
import software.amazon.awscdk.services.ec2.SecurityGroup;
import software.amazon.awscdk.services.ec2.Vpc;
import software.amazon.awscdk.services.iam.Role;

public interface AMIProps extends StackProps {
   public Vpc getVpc();
   public AmazonLinuxImage getAMIs();
   public Role getRole();
   public SecurityGroup getSecurityGroup();
  }