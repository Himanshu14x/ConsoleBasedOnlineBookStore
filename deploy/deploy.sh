#!/bin/bash
REMOTE="$1"
REMOTE_PATH="${2:-/home/ubuntu/apps}"
SSH_PORT="${3:-22}"
if [ -z "$REMOTE" ]; then echo "Usage: $0 <remote_user>@<remote_host> [remote_path] [ssh_port]"; exit 1; fi
JAR="target/bookstore-1.0-SNAPSHOT-shaded.jar"
if [ ! -f "$JAR" ]; then echo "Jar not found at $JAR. Build the project first: mvn -B clean package"; exit 2; fi
echo "Copying $JAR to $REMOTE:$REMOTE_PATH"
scp -P $SSH_PORT "$JAR" "$REMOTE:$REMOTE_PATH"
echo "Restarting service on remote host (requires sudo privileges)"
ssh -p $SSH_PORT "$REMOTE" "sudo systemctl restart bookstore.service"
echo "Deploy complete."
