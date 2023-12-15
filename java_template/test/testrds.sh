#!/bin/bash

# JSON object to pass to Lambda Function
json={"\"url\"":"\"https://s3.us-east-2.amazonaws.com/tcss.bucket.462.f23.image.bl/ImgGrayMirrorRotate.png\""}

#echo "Invoking Lambda function using API Gateway"
#time output=`curl -s -H "Content-Type: application/json" -X POST -d $json {API-GATEWAY-REST-URL}`
#echo ""

#echo ""
#echo "JSON RESULT:"
#echo $output | jq
#echo ""

echo "Invoking Lambda function using AWS CLI"
#time output=`aws lambda invoke --invocation-type RequestResponse --function-name {LAMBDA-FUNCTION-NAME} --region us-east-2 --payload $json /dev/stdout | head -n 1 | head -c -2 ; echo`
time output=`aws lambda invoke --invocation-type RequestResponse --function-name ImageSql --region us-east-2 --payload $json /dev/stdout | head -n 1 | head -c -2 ; echo`

echo ""
echo "JSON RESULT:"
echo $output | jq
echo ""
