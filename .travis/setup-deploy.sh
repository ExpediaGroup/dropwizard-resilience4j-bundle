#!/bin/bash

# Pull request is a number or false
if [ "${TRAVIS_PULL_REQUEST}" != "false" ]; then
    echo "Skipping env deployment setup for pull requests"
    exit 0
fi

# For builds triggered by a tag, TRAVIS_BRANCH is the same as the name of the tag
if [[ "${TRAVIS_BRANCH}" != "master" && "${TRAVIS_BRANCH}" != "${TRAVIS_TAG}" ]]; then
    echo "Skipping env deployment setup for non-releases"
    exit 0
fi

echo "Verifying environment variables"

SIGNING_VARS='MAVEN_MASTER SONATYPE_USERNAME SONATYPE_PASSWORD GPG_EXECUTABLE GPG_KEYNAME GPG_PASSPHRASE'
for var in ${SIGNING_VARS[@]}
do
    if [ -z ${!var} ] ; then
        echo "Variable $var is not set cannot setup gpg signatures"
        exit 1
    fi
done

## setup maven decryption since the env vars are probably encrypted with Maven
cat > ${HOME}/.m2/settings-security.xml << EOM
<settingsSecurity>
    <master>${MAVEN_MASTER}</master>
</settingsSecurity>
EOM
echo "Maven security settings setup"

echo "Setting up env for deployment"
openssl aes-256-cbc -K $encrypted_95be9191f256_key -iv $encrypted_95be9191f256_iv -in .travis/codesigning.asc.enc -out .travis/codesigning.asc -d
if [ $? -ne 0 ] ; then
	echo "Unable to process gpg keys cannot sign"
	exit 1
fi

gpg --fast-import .travis/codesigning.asc
if [ $? -ne 0 ] ; then
	echo "Unable to process gpg keys cannot sign"
	exit 1
fi

#echo $GPG_OWNERTRUST | base64 --decode | $GPG_EXECUTABLE --import-ownertrust

echo "Configuring maven settings to sign jars and publish to sonatype"
cp ./.travis/settings.xml ${HOME}/.m2/settings.xml
echo "Maven settings setup completed"

echo "Environment setup for signing deployments"
