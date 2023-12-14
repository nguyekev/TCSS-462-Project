#!/bin/bash
# JSON object to pass to Lambda Function

echo "Invoking Lambda function using AWS CLI"
time output=$(aws lambda invoke --invocation-type RequestResponse --function-name uploadImage --region us-east-2 --payload '{"bucketname":"imag3","filename":"5xgkhmr1.png","imagePath":"java_template/src/main/java/images/5xgkhmr1.png"}' /dev/stdout)
echo ""
echo "JSON RESULT:"
echo "$output" | jq
echo ""
