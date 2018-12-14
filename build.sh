#!/bin/bash

set -e

usage() {
  echo "Usage: $0 app [type] [-d destination]"
  exit 1
}

DEST=/home/$USER
BUILD_TYPE="release"

while getopts d: option
do
  case "${option}"
  in
    d) DEST=${OPTARG};;
  esac
done

shift $((OPTIND-1))

APP=$1

if [ "$2" ]; then
    BUILD_TYPE="$2"
fi

if [ -z "${DEST}" ] || [ -z "${APP}" ] || [ -z "${BUILD_TYPE}" ]; then
    usage
fi

mkdir -p "${DEST}/dxram/dxapp"

./gradlew ${APP}:jar -PbuildVariant="${BUILD_TYPE}"
./gradlew dxa-terminal:server:jar -PbuildVariant="${BUILD_TYPE}"
./gradlew dxa-terminal:client:installDist -PoutputDir="${DEST}/dxram"

cp ./${APP}/build/libs/${APP}.jar "${DEST}/dxram/dxapp"
cp ./dxa-terminal/server/build/libs/dxa-terminal.jar "${DEST}/dxram/dxapp"
