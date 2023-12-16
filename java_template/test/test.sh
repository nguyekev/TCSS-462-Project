#!/bin/bash

# JSON object to pass to Lambda Function
payload='{"bucketname":"imag3","filename":"Bird.png", "imagePath":"/images/Bird.png"}'

echo "Invoking Lambda function using AWS CLI"
output=$(aws lambda invoke --invocation-type RequestResponse --function-name uploadImage --region us-east-2 --payload "$payload" /dev/null)

echo ""
echo "JSON RESULT:"
echo "$output" | jq
echo ""
