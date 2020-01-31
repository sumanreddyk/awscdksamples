package andy.birenzi;

import andy.birenzi.model.AMIs;
import andy.birenzi.model.Ec2InstanceRole;
import andy.birenzi.props.Ec2Props;
import andy.birenzi.stacks.InfrastructureStack;
import andy.birenzi.stacks.WebStack;
import software.amazon.awscdk.core.App;
import software.amazon.awscdk.services.ec2.AmazonLinuxImage;
import software.amazon.awscdk.services.ec2.Vpc;
import software.amazon.awscdk.services.iam.Role;

public class MainApp {
    public static void main(final String[] args) {
        final App app = new App();
        final AMIs appImage = new AMIs();

        final InfrastructureStack infra = new InfrastructureStack(app, "InfrastructureStack");
        final Ec2InstanceRole webRole = new Ec2InstanceRole(app, "WebRole");

        final WebStack ec2 = new WebStack(app, "WebInstanceStack", new Ec2Props() {

            @Override
            public Vpc getVpc() {
                return infra.getVpc();
            }

            @Override
            public AmazonLinuxImage getAMIs() {
                return appImage.getApplicationImage();
            }

            @Override
            public Role getRole() {
                return webRole.getRole();
            }

        });
      
    
       

        app.synth();
    }
}
