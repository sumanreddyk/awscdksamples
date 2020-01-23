package andy.birenzi.model;

import java.util.Arrays;

import software.amazon.awscdk.core.Construct;
import software.amazon.awscdk.core.Environment;
import software.amazon.awscdk.core.Stack;
import software.amazon.awscdk.core.StackProps;
import software.amazon.awscdk.services.iam.Effect;
import software.amazon.awscdk.services.iam.PolicyStatement;
import software.amazon.awscdk.services.iam.Role;
import software.amazon.awscdk.services.iam.RoleProps;
import software.amazon.awscdk.services.iam.ServicePrincipal;

public class Ec2InstanceRole extends Stack{
    private Role instanceRole;
    public Ec2InstanceRole(final Construct scope, final String id) {
        this(scope, id, null);
    }

    public Ec2InstanceRole(final Construct scope, final String id, final StackProps props) {
        super(scope, id, props);
        Environment env= Environment.builder().account("528430954406").region("us-east-1").build();
        instanceRole= new Role(this, "WebAppInstanceRole", RoleProps
        .builder().assumedBy(ServicePrincipal.Builder.create("ec2.amazonaws.com").build())
        .build());

        PolicyStatement getParameters= PolicyStatement.Builder.create()
                                            .effect(Effect.ALLOW)
                                            .actions(Arrays.asList("ssm:GetParameters"))
                                            .resources(Arrays.asList("arn:aws:ssm:"+env.getRegion()+":"+env.getAccount()+":parameter/duo_*"))
                                           
                                            .build();
                                            PolicyStatement getParameter= PolicyStatement.Builder.create()
                                            .effect(Effect.ALLOW)
                                            .actions(Arrays.asList("ssm:GetParameter"))
                                            .resources(Arrays.asList("arn:aws:ssm:"+env.getRegion()+":"+env.getAccount()+":parameter/duo_*"))
                                           
                                            .build();
    PolicyStatement decrypt= PolicyStatement.Builder.create()
    .effect(Effect.ALLOW)
    .actions(Arrays.asList("kms:Decrypt"))
    .resources(Arrays.asList("arn:aws:kms:"+env.getRegion()+":"+env.getAccount()+":key/ssm"))
    .build();

        instanceRole.addToPolicy(getParameters);
        instanceRole.addToPolicy(decrypt);
        instanceRole.addToPolicy(getParameter);
    }
    public Role getRole(){
        return this.instanceRole;
    }
   
}