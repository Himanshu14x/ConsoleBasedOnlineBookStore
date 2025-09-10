#!/usr/bin/env bash
set -euo pipefail

# Usage:
# ./deploy.sh <user>@<host> /remote/path /path/to/artifact.jar
# Example:
# ./deploy.sh ubuntu@1.2.3.4 /home/ubuntu/apps target/bookstore-1.0-SNAPSHOT-shaded.jar

REMOTE="$1"        # e.g. ubuntu@ec2-xx-xx-xx.compute.amazonaws.com
REMOTE_DIR="$2"    # e.g. /home/ubuntu/apps
ARTIFACT="$3"      # e.g. target/bookstore-1.0-SNAPSHOT-shaded.jar
SERVICE_NAME="bookstore.service"

echo "Deploying ${ARTIFACT} -> ${REMOTE}:${REMOTE_DIR}"

# create remote dir
ssh -o StrictHostKeyChecking=no "${REMOTE}" "mkdir -p ${REMOTE_DIR}"

# copy artifact
scp -o StrictHostKeyChecking=no "${ARTIFACT}" "${REMOTE}:${REMOTE_DIR}/bookstore.jar"

# copy systemd unit (optional) - uncomment to use the repo copy
# scp -o StrictHostKeyChecking=no deploy/bookstore.service "${REMOTE}:/tmp/bookstore.service"
# ssh "${REMOTE}" "sudo mv /tmp/bookstore.service /etc/systemd/system/bookstore.service && sudo systemctl daemon-reload"

# restart service
ssh -o StrictHostKeyChecking=no "${REMOTE}" "sudo systemctl stop bookstore.service || true; sudo cp ${REMOTE_DIR}/bookstore.jar /opt/bookstore/bookstore.jar || sudo mkdir -p /opt/bookstore && sudo cp ${REMOTE_DIR}/bookstore.jar /opt/bookstore/bookstore.jar; sudo chown -R $(whoami):$(whoami) /opt/bookstore || true; sudo systemctl start bookstore.service || (echo 'systemctl start failed, showing journal:'; sudo journalctl -u bookstore.service -n 200; exit 1)"

echo "Deployed and restarted service on ${REMOTE}"
