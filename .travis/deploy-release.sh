set -e

mvn org.codehaus.mojo:versions-maven-plugin:2.7:set -DnewVersion=${TRAVIS_TAG}
mvn -DskipTests -Psigned -B deploy
