#!/bin/bash
cd /home/kavia/workspace/code-generation/task-tracker-282217-282192/todo_backend
./gradlew checkstyleMain
LINT_EXIT_CODE=$?
if [ $LINT_EXIT_CODE -ne 0 ]; then
   exit 1
fi

