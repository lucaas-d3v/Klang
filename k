#!/bin/bash
PROJECT_DIR="$HOME/Klang"

cd "$PROJECT_DIR" || exit 1
./kc "$@"
