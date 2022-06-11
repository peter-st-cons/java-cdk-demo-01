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
