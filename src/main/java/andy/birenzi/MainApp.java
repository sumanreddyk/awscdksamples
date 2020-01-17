package andy.birenzi;

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
        final InstanceStack ec2 = new InstanceStack(app, "InstanceStack", new SharedPros() {

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
                // TODO Auto-generated method stub
                return webRole.getRole();
            }

        });

        app.synth();
    }
}
