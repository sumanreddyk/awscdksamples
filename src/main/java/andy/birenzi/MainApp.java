package andy.birenzi;

import software.amazon.awscdk.core.App;
import software.amazon.awscdk.services.ec2.AmazonLinuxImage;
import software.amazon.awscdk.services.ec2.Vpc;


public class MainApp {
    public static void main(final String[] args) {
        final App app = new App();
        final AMIs appImage=new AMIs();

        final InfrastructureStack infra= new InfrastructureStack(app, "InfrastructureStack");
        
        final InstanceStack ec2=new InstanceStack(app, "InstanceStack",new SharedPros(){
        
            @Override
            public Vpc getVpc() {
                return infra.getVpc();
            }
            @Override
            public AmazonLinuxImage getAMIs() {
                return appImage.getApplicationImage();
            }
        });

        app.synth();
    }
}
