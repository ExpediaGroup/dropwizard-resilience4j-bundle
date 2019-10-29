set -e

# Remove the 'v' from the tag if it has one (e.g. v1.0.0)
if [[ $TRAVIS_TAG == v* ]]; then
    ARTIFACT_VERSION=$( echo $TRAVIS_TAG | cut -c2- )
else
    ARTIFACT_VERSION=$TRAVIS_TAG
fi

if [[ ! $ARTIFACT_VERSION =~ ^[0-9]+\.[0-9]+ ]]; then
    echo "Unexpected version value: ${ARTIFACT_VERSION}" >&2
    exit 1
fi

mvn org.codehaus.mojo:versions-maven-plugin:2.7:set -DnewVersion=${ARTIFACT_VERSION}
mvn -DskipTests -Psigned -B clean deploy
