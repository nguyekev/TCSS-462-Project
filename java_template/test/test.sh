#!/bin/bash

# JSON object to pass to Lambda Function
payload='{"bucketname":"tcss.bucket.462.f23.image.bl","filename":"Bird.png", "imagePath":"/images/Bird.png"}'

echo "Invoking Lambda function using AWS CLI"
output=$(aws lambda invoke --invocation-type RequestResponse --function-name Image_uploaderimage --region us-east-2 --payload "$payload" /dev/null)

echo ""
echo "JSON RESULT:"
echo "$output" | jq
echo ""
