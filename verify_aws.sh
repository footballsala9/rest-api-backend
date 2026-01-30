#!/bin/bash

BASE_URL="http://localhost:8080/api/aws"

echo "=== S3 Verification ==="
echo "Creating bucket..."
curl -X POST "$BASE_URL/s3/buckets?bucketName=demo-bucket"
echo -e "\nUploading file..."
echo "Hello LocalStack" > hello.txt
curl -X POST -F "file=@hello.txt" "$BASE_URL/s3/files?bucketName=demo-bucket&key=hello.txt"
echo -e "\nListing files..."
curl "$BASE_URL/s3/files?bucketName=demo-bucket"
echo -e "\nDownloading file..."
curl "$BASE_URL/s3/files/hello.txt?bucketName=demo-bucket"
echo -e "\nDeleting file..."
curl -X DELETE "$BASE_URL/s3/files/hello.txt?bucketName=demo-bucket"

echo -e "\n\n=== DynamoDB Verification ==="
echo "Creating table..."
curl -X POST "$BASE_URL/dynamo/tables?tableName=DemoTable&partitionKey=id"
echo -e "\nPutting item..."
curl -X POST -H "Content-Type: application/json" -d '{"id": "1", "name": "Test Item"}' "$BASE_URL/dynamo/items?tableName=DemoTable"
echo -e "\nGetting item..."
curl "$BASE_URL/dynamo/items/1?tableName=DemoTable&keyName=id"
echo -e "\nDeleting item..."
curl -X DELETE "$BASE_URL/dynamo/items/1?tableName=DemoTable&keyName=id"

echo -e "\n\n=== Secrets Manager Verification ==="
echo "Creating secret..."
curl -X POST -H "Content-Type: text/plain" -d "super-secret-value" "$BASE_URL/secrets?name=DemoSecret"
echo -e "\nGetting secret..."
curl "$BASE_URL/secrets/DemoSecret"
echo -e "\nDeleting secret..."
curl -X DELETE "$BASE_URL/secrets/DemoSecret"

echo -e "\n\nVerification script completed."
rm hello.txt
