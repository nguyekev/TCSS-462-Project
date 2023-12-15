#!/bin/bash
# apply grayscale and put in bucket
# JSON object to pass to Lambda Function
bucketName=tcss.bucket.462.f23.image.bl
# imageName=5xgkhmr1.png
imageName=Bird.png
# JSON object to pass to Lambda Function
payload='{"bucketname":"tcss.bucket.462.f23.image.bl","filename":"Bird.png", "imagePath":"/images/Bird.png"}'

echo "Invoking Lambda function using AWS CLI"
output=$(aws lambda invoke --invocation-type RequestResponse --function-name Image_uploaderimage --region us-east-2 --payload "$payload" /dev/null)

echo ""
echo "JSON RESULT:"
echo "$output" | jq
echo ""

jsonGray={"\"bucketname\"":"\"$bucketName\"","\"filename\"":"\"$imageName\"","\"outfilename\"":"\"ImgGray.png\""}

echo "Invoking Lambda function using AWS CLI for Grayscale transformation"
time output=`aws lambda invoke --invocation-type RequestResponse --function-name image_grayscale --region us-east-2 --payload $jsonGray /dev/stdout | head -n 1 | head -c -2 ; echo`
echo ""
echo "JSON RESULT:"
echo $output | jq
echo ""

# apply mirror on top of grayscale and put in bucket
# JSON object to pass to Lambda Function
jsonMirror={"\"bucketname\"":"\"$bucketName\"","\"filename\"":"\"ImgGray.png\"","\"outfilename\"":"\"ImgGrayMirror.png\""}

echo "Invoking Lambda function using AWS CLI for Mirror transformation"
time output2=`aws lambda invoke --invocation-type RequestResponse --function-name image_mirrorimage --region us-east-2 --payload $jsonMirror /dev/stdout | head -n 1 | head -c -2 ; echo`
echo ""
echo "JSON RESULT:"
echo $output2 | jq
echo ""

# apply rotation on top of mirror on top of grayscale and put in bucket
# JSON object to pass to Lambda Function
jsonRotate={"\"bucketname\"":"\"$bucketName\"","\"filename\"":"\"ImgGrayMirror.png\"","\"outfilename\"":"\"ImgGrayMirrorRotate.png\""}

echo "Invoking Lambda function using AWS CLI for Rotation transformation"
time output3=`aws lambda invoke --invocation-type RequestResponse --function-name image_rotateimage --region us-east-2 --payload $jsonRotate /dev/stdout | head -n 1 | head -c -2 ; echo`
echo ""
echo "JSON RESULT:"
echo $output3 | jq
echo ""