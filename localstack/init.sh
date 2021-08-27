#!/bin/bash
set -x

awslocal sqs create-queue --queue-name order-payment-queue

awslocal sns create-topic --name order-created-topic
awslocal sns create-topic --name order-updated-topic

set +x