package andy.birenzi.props;

import software.amazon.awscdk.core.StackProps;
import software.amazon.awscdk.services.autoscaling.AutoScalingGroup;
import software.amazon.awscdk.services.ec2.Vpc;

public interface ELBProps extends StackProps {
    
     public AutoScalingGroup getAutoScalingGroup();
     public Vpc getVpc();
  
  }