set -e

mvn -DskipTests -Psigned -B deploy
