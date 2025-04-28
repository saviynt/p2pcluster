#!/bin/sh

# Export JAVA_TOOL_OPTIONS
export JAVA_TOOL_OPTIONS=${JAVA_TOOL_OPTIONS:-""}
# Optional: print for debugging
echo -e "Using JAVA_TOOL_OPTIONS=$JAVA_TOOL_OPTIONS"
# Optional: print for debugging
echo -e "JAVA_OPTS: ${JAVA_OPTS}"
java ${JAVA_OPTS} -jar ./app.jar