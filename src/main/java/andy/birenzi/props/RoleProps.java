package andy.birenzi.props;

import software.amazon.awscdk.core.StackProps;
import software.amazon.awscdk.services.iam.Role;

public interface RoleProps extends StackProps {
    
   
   public Role getRole();
 
  }