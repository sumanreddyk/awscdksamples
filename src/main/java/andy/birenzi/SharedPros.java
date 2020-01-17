package andy.birenzi;

import software.amazon.awscdk.core.StackProps;
import software.amazon.awscdk.services.ec2.AmazonLinuxImage;
import software.amazon.awscdk.services.ec2.Vpc;
import software.amazon.awscdk.services.iam.Role;

interface SharedPros extends StackProps {
    
   public Vpc getVpc();
   public AmazonLinuxImage getAMIs();
   public Role getRole();
  
  }