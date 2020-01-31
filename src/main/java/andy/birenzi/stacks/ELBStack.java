
package andy.birenzi.stacks;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import software.amazon.awscdk.core.Construct;
import andy.birenzi.props.ELBProps;
import software.amazon.awscdk.core.Stack;
import software.amazon.awscdk.services.elasticloadbalancingv2.AddApplicationTargetGroupsProps;
import software.amazon.awscdk.services.elasticloadbalancingv2.ApplicationLoadBalancer;
import software.amazon.awscdk.services.elasticloadbalancingv2.ApplicationProtocol;
import software.amazon.awscdk.services.elasticloadbalancingv2.ApplicationTargetGroup;
import software.amazon.awscdk.services.elasticloadbalancingv2.ApplicationTargetGroupProps;
import software.amazon.awscdk.services.elasticloadbalancingv2.BaseApplicationListenerProps;
import software.amazon.awscdk.services.elasticloadbalancingv2.IApplicationLoadBalancerTarget;
import software.amazon.awscdk.services.elasticloadbalancingv2.IApplicationTargetGroup;
import software.amazon.awscdk.services.elasticloadbalancingv2.ListenerCertificate;
import software.amazon.awscdk.services.elasticloadbalancingv2.TargetType;

public class ELBStack extends Stack {
    private ApplicationLoadBalancer loadBalancer;

    public ELBStack(final Construct scope, final String id) {
        this(scope, id, null);
    }

    public ELBStack(final Construct scope, final String id, final ELBProps props) {
        super(scope, id, props);
        final String importCert = "arn:aws:acm:us-east-1:528430954406:certificate/f57f88eb-9bf2-43e3-b6e8-33dd5eae68da";
        loadBalancer = ApplicationLoadBalancer.Builder.create(this, "webELB").vpc(props.getVpc()).internetFacing(true)
                .loadBalancerName("webELB").build();
        // Create a Listener
        BaseApplicationListenerProps webListener = BaseApplicationListenerProps.builder().port(443).open(true)
                .certificates(Arrays.asList(ListenerCertificate.fromArn(importCert)))

                .protocol(ApplicationProtocol.HTTPS).build();

        // Add AutoScaling group as an ELB target
        List<IApplicationLoadBalancerTarget> targets = new ArrayList<IApplicationLoadBalancerTarget>();
        targets.add(props.getAutoScalingGroup());

        // Create a target group
        ApplicationTargetGroup webTargetGroup = new ApplicationTargetGroup(this, "WebTargetGroup",
                ApplicationTargetGroupProps.builder().vpc(props.getVpc()).targetType(TargetType.INSTANCE)
                        .targets(targets).port(443).protocol(ApplicationProtocol.HTTPS).build());

        // Add targets to Target group
        List<IApplicationTargetGroup> targetGroups = new ArrayList<IApplicationTargetGroup>();
        targetGroups.add(webTargetGroup);

        // Add Listener and Target group to ELB
        loadBalancer.addListener("WebListener", webListener).addTargetGroups(id,
                AddApplicationTargetGroupsProps.builder().targetGroups(targetGroups).build());
    }

    public ApplicationLoadBalancer getLoadBalancer() {
        return this.loadBalancer;
    }

}