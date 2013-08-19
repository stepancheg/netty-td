#!/bin/sh -ex

cd $(dirname $0)

mkdir -p lib
cd lib

download() {
    url="$1"
    file="$2"
    curl -s -o $file $url
}

download_from_maven() {
    groupSlashed="$1"
    artifact="$2"
    version="$3"
    for suffix in '' '-sources'; do
        download \
            http://repo2.maven.org/maven2/$groupSlashed/$artifact/$version/$artifact-$version$suffix.jar \
            $artifact-$version$suffix.jar
    done
}

download_from_maven junit junit 4.11
download_from_maven io/netty netty-all 4.0.7.Final
download_from_maven org/hamcrest hamcrest-all 1.3
download_from_maven com/intellij annotations 12.0


#download http://repo.springsource.org/libs-milestone-local/org/springframework/spring/4.0.0.M2/spring-framework-4.0.0.M2-dist.zip spring-framework-4.0.0.M2-dist.zip
#jar xf spring-framework-4.0.0.M2-dist.zip
#mv spring-framework-4.0.0.M2-dist.zip/libs/spring-beans-* ./
#mv spring-framework-4.0.0.M2-dist.zip/libs/spring-context-* ./
#mv spring-framework-4.0.0.M2-dist.zip/libs/spring-context-support-* ./
#mv spring-framework-4.0.0.M2-dist.zip/libs/spring-core-* ./
#mv spring-framework-4.0.0.M2-dist.zip/libs/spring-test-* ./

# vim: set ts=4 sw=4 et:
