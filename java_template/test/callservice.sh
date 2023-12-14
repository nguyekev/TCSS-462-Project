#!/bin/bash
# apply grayscale and put in bucket
# JSON object to pass to Lambda Function
bucketName=tcss.bucket.462.f23.image.bl
jsonGray={"\"bucketname\"":"\"$bucketName\"","\"filename\"":"\"Dog.png\"","\"outfilename\"":"\"DogGray.png\""}

echo "Invoking Lambda function using AWS CLI for Grayscale transformation"
time output=`aws lambda invoke --invocation-type RequestResponse --function-name image_grayscale --region us-east-2 --payload $jsonGray /dev/stdout | head -n 1 | head -c -2 ; echo`
echo ""
echo "JSON RESULT:"
echo $output | jq
echo ""

# apply mirror on top of grayscale and put in bucket
# JSON object to pass to Lambda Function
jsonMirror={"\"bucketname\"":"\"$bucketName\"","\"filename\"":"\"DogGray.png\"","\"outfilename\"":"\"DogGrayMirror.png\""}

echo "Invoking Lambda function using AWS CLI for Mirror transformation"
time output2=`aws lambda invoke --invocation-type RequestResponse --function-name image_mirrorimage --region us-east-2 --payload $jsonMirror /dev/stdout | head -n 1 | head -c -2 ; echo`
echo ""
echo "JSON RESULT:"
echo $output2 | jq
echo ""

# apply rotation on top of mirror on top of grayscale and put in bucket
# JSON object to pass to Lambda Function
jsonRotate={"\"bucketname\"":"\"$bucketName\"","\"filename\"":"\"DogGrayMirror.png\"","\"outfilename\"":"\"DogGrayMirrorRotate.png\""}

echo "Invoking Lambda function using AWS CLI for Rotation transformation"
time output3=`aws lambda invoke --invocation-type RequestResponse --function-name image_rotateimage --region us-east-2 --payload $jsonRotate /dev/stdout | head -n 1 | head -c -2 ; echo`
echo ""
echo "JSON RESULT:"
echo $output3 | jq
echo ""