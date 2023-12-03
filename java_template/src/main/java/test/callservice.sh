#!/bin/bash
# JSON object to pass to Lambda Function
json={"\"bucketname\"":\"test.bucket.462562.f23.image.bl\"","\"filename\"":\""Dog.png"\", "\"filepath\"":\""C:\\Users\\zebol\\Downloads\\Dog.png"\""}
# echo "Invoking Lambda function using API Gateway"
# time output=`curl -s -H "Content-Type: application/json" -X POST -d $json https://a1hrk9dnuf.execute-api.us-east-2.amazonaws.com/createCSV`
# echo “”
# echo ""
# echo "JSON RESULT:"
# echo $output | jq
# echo ""
echo "Invoking Lambda function using AWS CLI"
time output=`aws lambda invoke --invocation-type RequestResponse --function-name
test_addImage --region us-east-2 --payload $json /dev/stdout |
head -n 1 | head -c -2 ; echo`
echo ""
echo "JSON RESULT:"
echo $output | jq
echo ""