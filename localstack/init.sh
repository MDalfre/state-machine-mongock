#!/bin/bash
set -x

awslocal sqs create-queue --queue-name order-payment-dlq
awslocal sqs create-queue --queue-name order-payment-queue \
  --attributes '{"RedrivePolicy":"{\"maxReceiveCount\":\"2\", \"deadLetterTargetArn\":\"arn:aws:sqs:us-east-1:000000000000:order-payment-dlq\"}"}'

awslocal sns create-topic --name order-created-topic
awslocal sns create-topic --name order-updated-topic

set +x