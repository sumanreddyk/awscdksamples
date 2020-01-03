package andy.birenzi;
import software.amazon.awscdk.core.StackProps;
import software.amazon.awscdk.services.ec2.Vpc;

interface SharedVpcPros extends StackProps {
    
   public Vpc getVpc();
       
  }