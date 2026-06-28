#!/bin/bash
# Load test: open N simultaneous TCP connections to the TicTacToe server.
# Each connection sends "1" (a valid cell move) and then holds open for a few seconds.
#
# Usage:
#   ./test_load.sh [NUM_CONNECTIONS]   (default: 10000)
#
# WARNING: 10,000 connections will crash Server (unbounded threads) and may
#          freeze your machine. Use ServerThreadPool to survive the load.

HOST=localhost
PORT=12345
N=${1:-10000}

echo "Sending $N simultaneous connections to $HOST:$PORT ..."

for i in $(seq 1 "$N"); do
    # Each subshell: connect, send a move, hold for 5 s, then close
    (
        exec 3<>/dev/tcp/$HOST/$PORT 2>/dev/null
        echo "1" >&3
        sleep 5
        exec 3>&-
    ) &
done

echo "$N connections launched. Waiting for them to finish..."
wait
echo "Done."
