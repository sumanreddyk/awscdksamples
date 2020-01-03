package andy.birenzi;

import software.amazon.awscdk.core.App;
import software.amazon.awscdk.services.ec2.Vpc;


public class MainApp {
    public static void main(final String[] args) {
        App app = new App();

        InfrastructureStack infra= new InfrastructureStack(app, "InfrastructureStack");
        InstanceStack ec2=new InstanceStack(app, "InstanceStack",new SharedVpcPros(){
        
            @Override
            public Vpc getVpc() {
                return infra.getVpc();
            }
        });

        app.synth();
    }
}
