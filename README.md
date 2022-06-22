# CDK Java demo project

## How to run this

```bash
# Set your AWS account data and credentials (or use --profile <profile name> later in the cdk calls):
export AWS_ACCOUNT_ID="123456789012"
export AWS_ACCESS_KEY_ID="xxxxxxxxx"
export AWS_SECRET_ACCESS_KEY="yyyyy"

mvn package                                             # compile and run tests
cdk bootstrap aws://${AWS_ACCOUNT_ID}/eu-central-1      # necessary only once
cdk synth                                               # check if the code is understood by the CDK
cdk deploy                                              # deploy the resources to your AWS account
```

Have a look at the final output of the deployment. There should be a line like
```
JavaCdkDemo01.JavaCdkDemo01ECSServiceLoadBalancerDNS123ABC = JavaC-XYZ-123456.eu-central-1.elb.amazonaws.com
```
Open a browser and go to http://JavaC-XYZ-123456.eu-central-1.elb.amazonaws.com (the string on the right hand side of the `=` sign).

## Useful commands

 * `cdk ls` list all stacks in the app
 * `cdk synth` emit the synthesized CloudFormation template
 * `cdk deploy` deploy this stack to your default AWS account/region
 * `cdk diff` compare deployed stack with current state
 * `cdk docs` open CDK documentation
 * `cdk destroy` destroy the stack when finished

## Deployment

GitHub Actions takes care of deploying the Docker image and CloudFormation stacks. Before we can use the workflow, we need to install the baseline stack once. The baseline stack ensures GitHub can deploy infrastructure to AWS.

Run the following command. Make sure you have AWS credentials in place for the desired AWS account.

```sh
# Replace the GitHub org and repo name to match your setup
WEB_IDENTITY_GITHUB_SUB="repo:HenrikFricke/java-cdk-demo-01:ref:refs/heads/main" \
  npx cdk deploy JavaCdk01Baseline --require-approval never
```

The environment variable `WEB_IDENTITY_GITHUB_SUB` ensures only GitHub Actions triggered in the GitHub org and repo can fetch AWS credentials. Try to be as specific as possible.

After deployment, go to the AWS management console, go to IAM, and search for the role `GitHubActionsRole`. Copy the full ARN. Go back to GitHub and add the following actions secrets to the repo:

```
AWS_ROLE_TO_ASSUME=$ROLE_ARN_FROM_THE_CONSOLE
WEB_IDENTITY_GITHUB_SUB=$THE_VALUE_WE_USED_EARLIER
```

That's it. Now we can use GitHub to deploy the project.

## Credits

* Java stack props and builder pattern: https://github.com/stephane-devops/pingMeCdkTransitGatewayWGraph
