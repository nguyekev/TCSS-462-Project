#!/bin/bash
# apply grayscale and put in bucket
# JSON object to pass to Lambda Function
bucketName=tcss.bucket.462.f23.image.bl
imageName=Bird.png
img1Name=ImgGray.png
img2Name=ImgGrayMirror.png
img3Name=ImgGrayMirrorRotate.png

# JSON object to pass to Lambda Function
payload='{"bucketname":"tcss.bucket.462.f23.image.bl","filename":"Bird.png", "imagePath":"/images/Bird.png"}'

echo "Invoking Lambda function using AWS CLI to upload image to S3 (ARM)"
time output=`aws lambda invoke --invocation-type RequestResponse --function-name Image_uploadarm --region us-east-2 --payload "$payload" /dev/stdout | head -n 1 | head -c -2 ; echo`

echo ""
echo "JSON RESULT:"
echo "$output" | jq
echo ""

jsonGray={"\"bucketname\"":"\"$bucketName\"","\"filename\"":"\"$imageName\"","\"outfilename\"":"\"$img1Name\""}

echo "Invoking Lambda function using AWS CLI for Grayscale transformation (ARM)"
time output=`aws lambda invoke --invocation-type RequestResponse --function-name image_grayscale_arm64 --region us-east-2 --payload $jsonGray /dev/stdout | head -n 1 | head -c -2 ; echo`
echo ""
echo "JSON RESULT:"
echo $output | jq
echo ""

# apply mirror on top of grayscale and put in bucket
# JSON object to pass to Lambda Function
jsonMirror={"\"bucketname\"":"\"$bucketName\"","\"filename\"":"\"$img1Name\"","\"outfilename\"":"\"$img2Name\""}

echo "Invoking Lambda function using AWS CLI for Mirror transformation (ARM)"
time output2=`aws lambda invoke --invocation-type RequestResponse --function-name Mirrorimage_arm64 --region us-east-2 --payload $jsonMirror /dev/stdout | head -n 1 | head -c -2 ; echo`
echo ""
echo "JSON RESULT:"
echo $output2 | jq
echo ""

# apply rotation on top of mirror on top of grayscale and put in bucket
# JSON object to pass to Lambda Function
jsonRotate={"\"bucketname\"":"\"$bucketName\"","\"filename\"":"\"$img2Name\"","\"outfilename\"":"\"$img3Name\""}

echo "Invoking Lambda function using AWS CLI for Rotation transformation (ARM)"
time output3=`aws lambda invoke --invocation-type RequestResponse --function-name ImageRotation_arm64 --region us-east-2 --payload $jsonRotate /dev/stdout | head -n 1 | head -c -2 ; echo`
echo ""
echo "JSON RESULT:"
echo $output3 | jq
echo ""



# working with rds sql

#!/bin/bash

# JSON object to pass to Lambda Function
jsonurl1={"\"url1\"":"\"https://s3.us-east-2.amazonaws.com/tcss.bucket.462.f23.image.bl/$img1Name\"","\"url2\"":"\"https://s3.us-east-2.amazonaws.com/tcss.bucket.462.f23.image.bl/$img2Name\"","\"url3\"":"\"https://s3.us-east-2.amazonaws.com/tcss.bucket.462.f23.image.bl/$img3Name\""}
# jsonurl2={"\"url\"":"\"https://s3.us-east-2.amazonaws.com/tcss.bucket.462.f23.image.bl/$img2Name\""}
# jsonurl3={"\"url\"":"\"https://s3.us-east-2.amazonaws.com/tcss.bucket.462.f23.image.bl/$img3Name\""}

#echo "Invoking Lambda function using API Gateway"
#time output=`curl -s -H "Content-Type: application/json" -X POST -d $json {API-GATEWAY-REST-URL}`
#echo ""

#echo ""
#echo "JSON RESULT:"
#echo $output | jq
#echo ""

echo "Invoking Lambda function using AWS CLI to load images url to MySQL (ARM)"
#time output=`aws lambda invoke --invocation-type RequestResponse --function-name {LAMBDA-FUNCTION-NAME} --region us-east-2 --payload $json /dev/stdout | head -n 1 | head -c -2 ; echo`
time outputurl1=`aws lambda invoke --invocation-type RequestResponse --function-name ImageSql --region us-east-2 --payload $jsonurl1 /dev/stdout | head -n 1 | head -c -2 ; echo`

echo ""
echo "JSON RESULT:"
echo $outputurl1 | jq
echo ""