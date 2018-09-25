#!/bin/bash

build_type="release"

if [ "$1" ]; then
    build_type="$1"
fi

case "$build_type" in
    debug)
        ;;
    release)
        ;;
    performance)
        ;;
    *)
        echo "Invalid build type \"$build_type\" specified"
        exit -1
esac

echo "Build type: $build_type"

./gradlew installDist distZip :dxa-chunkbench:jar :dxa-helloworld:jar :dxa-migration:jar :dxa-terminal:client:installDist :dxa-terminal:client:distZip :dxa-terminal:server:jar -PbuildVariant="$build_type"
